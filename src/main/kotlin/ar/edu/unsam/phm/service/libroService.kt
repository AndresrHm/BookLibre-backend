package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.BusquedaPorNombreFiltro
import ar.edu.unsam.phm.domain.EntreFechasFiltro
import ar.edu.unsam.phm.domain.FiltroDeLibro
import ar.edu.unsam.phm.domain.GeneroFiltro
import ar.edu.unsam.phm.domain.ISBN13Filtro
import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.domain.RangoDePaginasFiltro
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.UsuarioPrestadorFiltro
import ar.edu.unsam.phm.domain.paginate
import ar.edu.unsam.phm.dto.DireccionOrden
import ar.edu.unsam.phm.dto.FiltroDisponibilidadPerfil
import ar.edu.unsam.phm.dto.FiltrosDeLibrosDTO
import ar.edu.unsam.phm.dto.LibroUsuarioPerfilDTO
import ar.edu.unsam.phm.dto.OrdenHomePage
import ar.edu.unsam.phm.dto.OrdenLibroPerfil
import ar.edu.unsam.phm.mapper.LibroMapper
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class LibroService(
    var repositorioLibro: LibroRepository,
    var repositorioReservas: ReservaRepository,
    var repositorioUsuario: UsuarioRepository,
    var libroMapper: LibroMapper
) {
    fun crearLibro(libro: Libro): Libro {
        val nuevoLibro = libro
        val tipoUsuario = repositorioUsuario.getTipoUsuario(nuevoLibro.idPropietario)
        if(tipoUsuario == TipoUsuario.LECTOR){throw BusinessException("Solo un Publicador puede crear libros.")}
        repositorioLibro.create(nuevoLibro)
        return nuevoLibro
    }

    fun obtenerLibroParaEdicion(idLibro: Int): Libro {
        val libroParaEdicion = getLibroByIdOrThrow(idLibro)

        return libroParaEdicion
    }

    fun editarLibro(libroParaEdicion: Libro, idUsuarioActual: Int): Libro {
        val libroEditado = libroParaEdicion

        val tipoUsuario = repositorioUsuario.getTipoUsuario(libroEditado.idPropietario)
        if(tipoUsuario == TipoUsuario.LECTOR){throw BusinessException("Solo un Publicador puede editar libros.")}

        val libroOriginal = getLibroByIdOrThrow(libroParaEdicion.id)
        validarPropietario(libroOriginal, idUsuarioActual)

        repositorioLibro.update(libroEditado)

        return libroEditado
    }

    fun obtenerLibroDetalle(idLibro: Int): Libro {
        return getLibroByIdOrThrow(idLibro)
    }

    private val filtrosAplicables: List<FiltroDeLibro> = listOf(
        BusquedaPorNombreFiltro(),
        EntreFechasFiltro(repositorioReservas),
        GeneroFiltro(),
        RangoDePaginasFiltro(),
        ISBN13Filtro(),
        UsuarioPrestadorFiltro(repositorioReservas)
    )

    fun getLibrosPorFiltros(
        filtros: FiltrosDeLibrosDTO,
        page: Int = 0,
        size: Int = 6,
        ordenHomePage: OrdenHomePage,
        userId: Int
    ): PageResponse<Libro> {
        val filtrosNormalizados = normalizarFechasPorDefecto(filtros)

        validarPaginacion(page, size)
        validarRangosFiltros(filtrosNormalizados)
        val librosFiltrados = try {
            filtrosAplicables
                .fold(repositorioLibro.filtrarLibrosActivos()) { resultado, filtro ->
                    if (filtro.cumpleCondicion(filtrosNormalizados)) filtro.filtrar(resultado, filtrosNormalizados) else resultado
                }
                .filter { it.idPropietario != userId }
        } catch (e: IllegalArgumentException) {
            throw BusinessException(e.message ?: "Parametros de filtro invalidos")
        }

        val librosPorFiltroOrdenado = when (ordenHomePage) {
            OrdenHomePage.NOMBRE_ASC -> librosFiltrados.sortedBy { it.titulo.lowercase() }
            OrdenHomePage.NOMBRE_DESC -> librosFiltrados.sortedByDescending { it.titulo.lowercase() }
            OrdenHomePage.AUTOR_ASC -> librosFiltrados.sortedBy { it.autor.lowercase() }
            OrdenHomePage.AUTOR_DESC -> librosFiltrados.sortedByDescending { it.autor.lowercase() }
            OrdenHomePage.USER_ASC -> librosFiltrados.sortedBy {
                repositorioUsuario.getById(it.idPropietario)?.nombre?.lowercase() ?: ""
            }

            OrdenHomePage.USER_DESC -> librosFiltrados.sortedByDescending {
                repositorioUsuario.getById(it.idPropietario)?.nombre?.lowercase() ?: ""
            }
        }

        val pageResult = paginate(page, size, librosPorFiltroOrdenado)
        val totalPages = if (pageResult.totalElements == 0) 0 else (pageResult.totalElements + size - 1) / size

        return PageResponse(
            content = pageResult.content,
            page = page,
            size = size,
            totalElements = pageResult.totalElements,
            totalPages = totalPages
        )
    }

    fun bringKarma(libro: Libro, usuarioId: Int, fechaIni: LocalDate?, fechaFin: LocalDate?): Int {
        val usuarioLector = repositorioUsuario.getById(usuarioId)
            ?: throw NotFoundException("No existe un usuario con el id $usuarioId")


        val fechaDesde = fechaIni ?: LocalDate.now()
        val fechaHasta = fechaFin ?: LocalDate.now()

        return libro.calcularKarmaHomePage(usuarioLector, fechaDesde, fechaHasta)
    }

    fun obtenerLibrosDeUsuario(
        usuarioId: Int,
        page: Int,
        size: Int,
        orden: OrdenLibroPerfil,
        direccion: DireccionOrden,
        filtro: FiltroDisponibilidadPerfil
    ): PageResponse<LibroUsuarioPerfilDTO> {
        validarPaginacion(page, size)

        val idsLibrosPrestados = repositorioReservas
            .getLibrosReservadosEntreFechas(LocalDate.now(), LocalDate.now())
            .map { it.id }
            .toSet()

        val resultadoPagina = repositorioLibro.buscarPorIdPropietario(
            propietarioId = usuarioId,
            page = page,
            size = size,
            orden = orden,
            direccion = direccion,
            filtro = filtro,
            librosPrestadosIds = idsLibrosPrestados
        )

        val totalPaginas =
            if (resultadoPagina.totalElements == 0) 0 else (resultadoPagina.totalElements + size - 1) / size

        return PageResponse(
            content = resultadoPagina.content.map { libro ->
                val disponibilidad = if (libro.id in idsLibrosPrestados) "PRESTADO" else "DISPONIBLE"
                libroMapper.libroToLibroPerfilUsuarioDTO(libro, disponibilidad)
            },
            page = page,
            size = size,
            totalElements = resultadoPagina.totalElements,
            totalPages = totalPaginas,
        )
    }

    fun borrarLibro(idLibro: Int, idUsuario: Int) {
        val libro = getLibroByIdOrThrow(idLibro)
        validarPropietario(libro, idUsuario)
        if (repositorioReservas.estadoLibro(libro)) {
            throw BusinessException("El Libro se encuenta prestado")
        } else {
            libro.activo = false
            repositorioLibro.update(libro)
        }
    }

    private fun getLibroByIdOrThrow(idLibro: Int): Libro =
        repositorioLibro.getById(idLibro)
            ?: throw NotFoundException("No existe un libro con el id $idLibro")

    private fun validarPropietario(libro: Libro, idUsuario: Int) {
        if (libro.idPropietario != idUsuario) {
            throw BusinessException("El libro con id ${libro.id} no pertenece al usuario $idUsuario")
        }
    }

    private fun validarPaginacion(page: Int, size: Int) {
        if (page < 0) throw BusinessException("page no puede ser negativo")
        if (size <= 0) throw BusinessException("size debe ser mayor a 0")
    }

    private fun validarRangosFiltros(filtros: FiltrosDeLibrosDTO) {
        val fechaDesde = filtros.fechaDesde
        val fechaHasta = filtros.fechaHasta
        if (entreFechas(fechaDesde, fechaHasta)) {
            throw BusinessException("fechaDesde no puede ser mayor a fechaHasta")
        }

        val paginasMin = filtros.paginasMin
        val paginasMax = filtros.paginasMax
        if (paginasMin != null && paginasMax != null && paginasMin > paginasMax) {
            throw BusinessException("paginasMin no puede ser mayor a paginasMax")
        }
    }

    private fun entreFechas(fechaDesde: LocalDate?, fechaHasta: LocalDate?): Boolean =
        fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)

    private fun normalizarFechasPorDefecto(filtros: FiltrosDeLibrosDTO): FiltrosDeLibrosDTO {
        if (filtros.fechaDesde != null || filtros.fechaHasta != null) return filtros

        val hoy = LocalDate.now()
        return filtros.copy(fechaDesde = hoy, fechaHasta = hoy)
    }
}
