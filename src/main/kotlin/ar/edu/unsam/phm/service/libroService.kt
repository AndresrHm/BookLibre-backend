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
        require(page >= 0) { "page no puede ser negativo" }
        require(size > 0) { "size debe ser mayor a 0" }

        val librosFiltrados = filtrosAplicables
            .fold(repositorioLibro.getElementos()) { resultado, filtro ->
                if (filtro.cumpleCondicion(filtros)) filtro.filtrar(resultado, filtros) else resultado
            }
            .filter { it.idPropietario != userId }

        val librosPorFiltroOrdenado = when (ordenHomePage) {
            OrdenHomePage.NOMBRE_ASC -> librosFiltrados.sortedBy { it.titulo.lowercase() }
            OrdenHomePage.NOMBRE_DESC -> librosFiltrados.sortedByDescending { it.titulo.lowercase() }
            OrdenHomePage.AUTOR_ASC -> librosFiltrados.sortedBy { it.autor.lowercase() }
            OrdenHomePage.AUTOR_DESC -> librosFiltrados.sortedByDescending { it.autor.lowercase() }
            OrdenHomePage.USER_ASC -> librosFiltrados.sortedBy { repositorioUsuario.getById(it.idPropietario)?.nombre?.lowercase() ?: "" }
            OrdenHomePage.USER_DESC -> librosFiltrados.sortedByDescending { repositorioUsuario.getById(it.idPropietario)?.nombre?.lowercase() ?: "" }
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

    fun bringKarma(libro: Libro, usuarioId: Int, fechaIni : LocalDate?, fechaFin: LocalDate?): Int {
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
        require(page >= 0) { "page debe ser mayor o igual a 0" }
        require(size > 0) { "size debe ser mayor a 0" }

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
            content = resultadoPagina.content.map { libroMapper.libroToLibroPerfilUsuarioDTO(it) },
            page = page,
            size = size,
            totalElements = resultadoPagina.totalElements,
            totalPages = totalPaginas,
        )
    }

    fun borrarLibro(idLibro: Int, idUsuario: Int) {
        val libro = getLibroByIdOrThrow(idLibro)
        validarPropietario(libro, idUsuario)

        repositorioLibro.delete(libro)
    }

    private fun getLibroByIdOrThrow(idLibro: Int): Libro =
        repositorioLibro.getById(idLibro)
            ?: throw NotFoundException("No existe un libro con el id $idLibro")

    private fun validarPropietario(libro: Libro, idUsuario: Int) {
        if (libro.idPropietario != idUsuario) {
            throw BusinessException("El libro con ${libro.id} no pertenece al usuario $idUsuario")
        }
    }
}
