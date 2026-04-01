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
import ar.edu.unsam.phm.dto.FiltrosDeLibros
import ar.edu.unsam.phm.dto.LibroUsuarioPerfilDTO
import ar.edu.unsam.phm.dto.OrdenHomePage
import ar.edu.unsam.phm.dto.OrdenLibroPerfil
import ar.edu.unsam.phm.dto.OrigenLibro
import ar.edu.unsam.phm.errors.ErrorStrings
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
    fun crearLibro(nuevoLibro: Libro): Libro {
        val tipoUsuario = repositorioUsuario.getTipoUsuario(nuevoLibro.idPropietario)
        if (esTipoLector(tipoUsuario)) {
            throw BusinessException(ErrorStrings.SOLO_PUBLICADOR_PUEDE_CREAR_LIBROS)
        }
        repositorioLibro.create(nuevoLibro)
        return nuevoLibro
    }

    fun obtenerLibro(idLibro: Int): Libro {
        val libro = getLibroByIdOrThrow(idLibro)

        return libro
    }

    fun editarLibro(libroEditado: Libro, idUsuarioActual: Int): Libro {
        val tipoUsuario = repositorioUsuario.getTipoUsuario(libroEditado.idPropietario)
        if (esTipoLector(tipoUsuario)) {
            throw BusinessException(ErrorStrings.SOLO_PUBLICADOR_PUEDE_EDITAR_LIBROS)
        }

        val libroOriginal = getLibroByIdOrThrow(libroEditado.id)
        validarPropietario(libroOriginal, idUsuarioActual)

        repositorioLibro.update(libroEditado)

        return libroEditado
    }

    private fun esTipoLector(tipoUsuario: TipoUsuario?): Boolean = tipoUsuario == TipoUsuario.LECTOR

    private val filtrosAplicables: List<FiltroDeLibro> = listOf(
        BusquedaPorNombreFiltro(),
        EntreFechasFiltro(repositorioReservas),
        GeneroFiltro(),
        RangoDePaginasFiltro(),
        ISBN13Filtro(),
        UsuarioPrestadorFiltro(repositorioReservas)
    )

    fun getLibrosPorFiltros(
        filtrosDeHome: FiltrosDeLibros,
        page: Int = 0,
        size: Int = 6,
        ordenHomePage: OrdenHomePage,
        userId: Int,
        flagDiff: OrigenLibro, // Luego cambiar por un enum
        // de momento, si es false, se asume que se quieren los libros del perfil,
        // y si es true, se asume que se quieren los libros de la home page
        disponibilidadLibrosPerfil: FiltroDisponibilidadPerfil,
        ordenLibroPerfil: OrdenLibroPerfil,
        direccionOrdenPerfil: DireccionOrden

    ): PageResponse<Libro> {
        validarFlagDiff(flagDiff)

        return when (flagDiff) {
            OrigenLibro.HOME -> librosFiltradosHomePage(
                filtrosDeHome,
                page,
                size,
                ordenHomePage,
                userId
            )
            OrigenLibro.PERFIL -> obtenerLibrosDeUsuario(
                usuarioId = userId,
                page = page,
                size = size,
                orden = ordenLibroPerfil,
                direccion = direccionOrdenPerfil,
                filtro = disponibilidadLibrosPerfil,
            )
        }
    }
    fun librosFiltradosHomePage(
        filtros: FiltrosDeLibros,
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
                    if (filtro.cumpleCondicion(filtrosNormalizados)) filtro.filtrar(
                        resultado,
                        filtrosNormalizados
                    ) else resultado
                }
                .filter { it.idPropietario != userId }
        } catch (e: IllegalArgumentException) {
            throw BusinessException(e.message ?: ErrorStrings.PARAMETROS_FILTRO_INVALIDOS)
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

    fun obtenerLibrosDeUsuario(
        usuarioId: Int,
        page: Int,
        size: Int,
        orden: OrdenLibroPerfil,
        direccion: DireccionOrden,
        filtro: FiltroDisponibilidadPerfil
    ): PageResponse<Libro> {
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

        val totalPaginas = if (resultadoPagina.totalElements == 0) 0 else (resultadoPagina.totalElements + size - 1) / size

        return PageResponse(
            content = resultadoPagina.content,
            page = page,
            size = size,
            totalElements = resultadoPagina.totalElements,
            totalPages = totalPaginas
        )
//        return PageResponse(
//            content = resultadoPagina.content.map { libro ->
//                val disponibilidad = if (libro.id in idsLibrosPrestados) "PRESTADO" else "DISPONIBLE"
//                libroMapper.libroToLibroPerfilUsuarioDTO(libro, disponibilidad)
//            },
//            page = page,
//            size = size,
//            totalElements = resultadoPagina.totalElements,
//            totalPages = totalPaginas,
//        )
    }

    fun bringKarma(
        libro: Libro,
        cantidadReservas: Int,
        usuarioId: Int,
        fechaIni: LocalDate?,
        fechaFin: LocalDate?
    ): Int {
        val usuarioLector = repositorioUsuario.getById(usuarioId)
            ?: throw NotFoundException(ErrorStrings.usuarioNoExistePorId(usuarioId))

        val fechaDesde = fechaIni ?: LocalDate.now()
        val fechaHasta = fechaFin ?: LocalDate.now()

        return libro.calcularKarmaHomePage(usuarioLector, cantidadReservas, fechaDesde, fechaHasta)
    }
    fun disponibilidadLibro(idLibro: Int): String {
        val libro = getLibroByIdOrThrow(idLibro)
        return if (repositorioReservas.estaReservado(libro)) "PRESTADO" else "DISPONIBLE"
    }
    fun borrarLibro(idLibro: Int, idUsuario: Int) {
        val libro = getLibroByIdOrThrow(idLibro)
        validarPropietario(libro, idUsuario)
        if (repositorioReservas.estaReservado(libro)) {
            throw BusinessException(ErrorStrings.LIBRO_PRESTADO_NO_SE_PUEDE_BORRAR)
        } else {
            libro.activo = false
            repositorioLibro.update(libro)
        }
    }

    private fun getLibroByIdOrThrow(idLibro: Int): Libro =
        repositorioLibro.getById(idLibro)
            ?: throw NotFoundException(ErrorStrings.libroNoEncontradoPorId(idLibro))

    private fun validarPropietario(libro: Libro, idUsuario: Int) {
        if (libro.idPropietario != idUsuario) {
            throw BusinessException(ErrorStrings.libroNoPerteneceAlUsuario(libro.id, idUsuario))
        }
    }

    private fun validarPaginacion(page: Int, size: Int) {
        if (page < 0) throw BusinessException(ErrorStrings.PAGE_NO_PUEDE_SER_NEGATIVO)
        if (size <= 0) throw BusinessException(ErrorStrings.SIZE_DEBE_SER_MAYOR_A_CERO)
    }

    private fun validarRangosFiltros(filtros: FiltrosDeLibros) {
        val fechaDesde = filtros.fechaDesde
        val fechaHasta = filtros.fechaHasta
        if (entreFechas(fechaDesde, fechaHasta)) {
            throw BusinessException(ErrorStrings.FECHA_DESDE_MAYOR_A_FECHA_HASTA)
        }

        val paginasMin = filtros.paginasMin
        val paginasMax = filtros.paginasMax
        if (paginasMin != null && paginasMax != null && paginasMin > paginasMax) {
            throw BusinessException(ErrorStrings.PAGINAS_MIN_MAYOR_A_PAGINAS_MAX)
        }
    }

    private fun entreFechas(fechaDesde: LocalDate?, fechaHasta: LocalDate?): Boolean =
        fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)

    private fun normalizarFechasPorDefecto(filtros: FiltrosDeLibros): FiltrosDeLibros {
        if (filtros.fechaDesde != null || filtros.fechaHasta != null) return filtros

        val hoy = LocalDate.now()
        return filtros.copy(fechaDesde = hoy, fechaHasta = hoy)
    }

    private fun validarFlagDiff(flagDiff: OrigenLibro) {
        if (flagDiff != OrigenLibro.HOME && flagDiff != OrigenLibro.PERFIL) {
            throw BusinessException(ErrorStrings.FLAG_DIFF_INVALIDO)
        }
    }
}
