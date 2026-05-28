package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.errors.BusinessException
import ar.edu.unsam.phm.errors.NotFoundException
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.*
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import ar.edu.unsam.phm.utils.sanitizarSearch
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class LibroService(
    var repositorioLibro: LibroRepository,
    var repositorioUsuario: UsuarioRepository,
) {
    fun crearLibro(nuevoLibro: Libro): Libro {
        val prestador = repositorioUsuario.findByIdOrNull(nuevoLibro.idPropietario)
            ?: throw BusinessException(ErrorStrings.usuarioNoExistePorId(nuevoLibro.idPropietario))

        if (!prestador.esPublicador())
            throw BusinessException(ErrorStrings.SOLO_PUBLICADOR_PUEDE_CREAR_LIBROS)

        repositorioLibro.save(nuevoLibro)
        return nuevoLibro
    }

    fun editarLibro(libroEditado: Libro, idUsuarioActual: Long): Libro {
        val prestador = repositorioUsuario.findByIdOrNull(libroEditado.idPropietario)
            ?: throw BusinessException(ErrorStrings.usuarioNoExistePorId(libroEditado.idPropietario))

        if (!prestador.esPublicador())
            throw BusinessException(ErrorStrings.SOLO_PUBLICADOR_PUEDE_CREAR_LIBROS)

        val libroOriginal = getLibroByIdOrThrow(libroEditado.id)
        validarPropietario(libroOriginal, idUsuarioActual)

        libroOriginal.titulo = libroEditado.titulo
        libroOriginal.genero = libroEditado.genero
        libroOriginal.autor = libroEditado.autor
        libroOriginal.idioma = libroEditado.idioma
        libroOriginal.estadoLibro = libroEditado.estadoLibro
        libroOriginal.editorial = libroEditado.editorial
        libroOriginal.fechaPublicacion = libroEditado.fechaPublicacion
        libroOriginal.cantidadPaginas = libroEditado.cantidadPaginas
        libroOriginal.isbn13 = libroEditado.isbn13
        libroOriginal.descripcion = libroEditado.descripcion
        libroOriginal.portada = libroEditado.portada

        repositorioLibro.save(libroOriginal)
        return libroOriginal
    }

    fun getLibrosPorFiltros(
        filtrosDeHome: FiltrosDeLibros,
        page: Int = 0,
        size: Int = 6,
        ordenHomePage: OrdenHomePage,
        userId: Long,
        flagDiff: OrigenLibro,
        disponibilidadLibrosPerfil: FiltroDisponibilidadPerfil,
        ordenLibroPerfil: OrdenLibroPerfil,
        direccionOrdenPerfil: DireccionOrden,
    ): PageResponse<Libro> {
        validarFlagDiff(flagDiff)

        return when (flagDiff) {
            OrigenLibro.HOME -> librosFiltradosHomePage(
                filtros = filtrosDeHome,
                page = page,
                size = size,
                ordenHomePage = ordenHomePage,
                userId = userId,
            )

            OrigenLibro.PERFIL -> librosFiltradosPerfilDesdeDb(
                propietarioId = userId,
                page = page,
                size = size,
                disponibilidad = disponibilidadLibrosPerfil,
                orden = ordenLibroPerfil,
                direccion = direccionOrdenPerfil,
            )
        }
    }


    fun librosFiltradosHomePage(
        filtros: FiltrosDeLibros,
        page: Int = 0,
        size: Int = 6,
        ordenHomePage: OrdenHomePage,
        userId: Long,
    ): PageResponse<Libro> {
        repositorioUsuario.findByIdOrNull(userId)
            ?: throw NotFoundException(ErrorStrings.usuarioNoExistePorId(userId))

        // Validar y obtener el rango de fechas que se debe usar para la consulta.
        validarPaginacion(page, size)
        validarRangosFiltros(filtros)

        val (fechaDesde, fechaHasta) = rangoFechasParaConsulta(filtros, OrigenLibro.HOME)
        val searchSanitizado = sanitizarSearch(filtros.search)

        val sort = sortHomePage(ordenHomePage)
        val pageable = PageRequest.of(page, size, sort)

        val resultado: Page<Libro> = repositorioLibro.buscarConFiltros(
            search = searchSanitizado,
            isbn13 = filtros.isbn13,
            paginasMin = filtros.paginasMin,
            paginasMax = filtros.paginasMax,
            generos = filtros.generos,
            soloIdPropietario = filtros.usuarioPrestador?.id,
            excluirIdPropietario = if (filtros.usuarioPrestador == null) userId else null,
            pageable = pageable,
            fechaDesde = fechaDesde,
            fechaHasta = fechaHasta,
        )

        return PageResponse(
            content = resultado.content,
            page = page,
            size = size,
            totalElements = resultado.totalElements.toInt(),
            totalPages = resultado.totalPages,
        )
    }

    fun bringKarma(
        libro: Libro,
        cantidadReservas: Int,
        usuarioId: Long,
        fechaIni: LocalDate?,
        fechaFin: LocalDate?,
    ): Int {
        val usuarioLector = repositorioUsuario.findByIdOrNull(usuarioId)
            ?: throw NotFoundException(ErrorStrings.usuarioNoExistePorId(usuarioId))

        val fechaDesde = fechaIni ?: LocalDate.now()
        val fechaHasta = fechaFin ?: LocalDate.now()

        return libro.calcularKarmaHomePage(usuarioLector, cantidadReservas, fechaDesde, fechaHasta)
    }

    fun disponibilidadLibro(idLibro: String): String {
        val libro = getLibroByIdOrThrow(idLibro)
        return if (estaReservadoHoy(libro.id)) "PRESTADO" else "DISPONIBLE"
    }

    fun obtenerPrestadoresDisponibles(search: String?, userId: Long): List<String> {
        repositorioUsuario.findByIdOrNull(userId)
            ?: throw NotFoundException(ErrorStrings.usuarioNoExistePorId(userId))

        val termino = search?.trim().orEmpty()

        return repositorioLibro.findByActivoTrue()
            .filter { it.idPropietario != userId && !estaReservadoHoy(it.id) }
            .mapNotNull { repositorioUsuario.findByIdOrNull(it.idPropietario)?.nombre }
            .distinct()
            .filter { (termino.isBlank() || it.contains(termino, ignoreCase = true)) }
            .sortedBy { it.lowercase() }
    }

    fun borrarLibro(idLibro: String, idUsuario: Long) {
        val libro = getLibroByIdOrThrow(idLibro)
        validarPropietario(libro, idUsuario)

        if (estaReservadoHoy(libro.id)) {
            throw BusinessException(ErrorStrings.LIBRO_PRESTADO_NO_SE_PUEDE_BORRAR)
        }

        libro.activo = false
        repositorioLibro.save(libro)
    }

    fun obtenerLibro(idLibro: String): Libro = getLibroByIdOrThrow(idLibro)

    private fun estaReservadoHoy(libroId: String): Boolean {
        val hoy = LocalDate.now()
        val libro = repositorioLibro.findByIdOrNull(libroId) ?: return false
        return libro.fechasReserva.any { it.fechaInicio <= hoy && it.fechaFin >= hoy }
    }

    private fun sortHomePage(ordenHomePage: OrdenHomePage): Sort = when (ordenHomePage) {
        OrdenHomePage.NOMBRE_ASC -> Sort.by("titulo").ascending()
        OrdenHomePage.NOMBRE_DESC -> Sort.by("titulo").descending()
        OrdenHomePage.AUTOR_ASC -> Sort.by("autor").ascending()
        OrdenHomePage.AUTOR_DESC -> Sort.by("autor").descending()
        OrdenHomePage.USER_ASC -> Sort.unsorted()
        OrdenHomePage.USER_DESC -> Sort.unsorted()
    }

    private fun sortPerfil(orden: OrdenLibroPerfil, direccion: DireccionOrden): Sort {
        val direction = if (direccion == DireccionOrden.ASCENDENTE) Sort.Direction.ASC else Sort.Direction.DESC

        return when (orden) {
            OrdenLibroPerfil.TITULO -> Sort.by(direction, "titulo")
                .and(Sort.by(direction, "autor"))
                .and(Sort.by(Sort.Direction.ASC, "id"))

            OrdenLibroPerfil.DISPONIBLE_PRESTADO -> Sort.by(direction, "titulo")
                .and(Sort.by(direction, "autor"))
                .and(Sort.by(Sort.Direction.ASC, "id"))

            OrdenLibroPerfil.FECHA_AGREGADO -> Sort.by(direction, "fechaAgregado")
                .and(Sort.by(Sort.Direction.ASC, "id"))
        }
    }


    private fun librosFiltradosPerfilDesdeDb(
        propietarioId: Long,
        page: Int,
        size: Int,
        disponibilidad: FiltroDisponibilidadPerfil,
        orden: OrdenLibroPerfil,
        direccion: DireccionOrden,
    ): PageResponse<Libro> {
        validarPaginacion(page, size)

        val resultado = repositorioLibro.buscarConFiltros(
            search = null,
            isbn13 = null,
            paginasMin = null,
            paginasMax = null,
            generos = null,
            soloIdPropietario = propietarioId,
            excluirIdPropietario = null,
            pageable = PageRequest.of(page, size, sortPerfil(orden, direccion)),
            disponibilidad = disponibilidad,
            fechaDesde = null,
            fechaHasta = null,
        )

        return PageResponse(
            content = resultado.content,
            page = page,
            size = size,
            totalElements = resultado.totalElements.toInt(),
            totalPages = resultado.totalPages,
        )
    }

    private fun getLibroByIdOrThrow(idLibro: String): Libro =
        repositorioLibro.findByIdOrNull(idLibro)
            ?: throw NotFoundException(ErrorStrings.libroNoEncontradoPorId(idLibro))

    private fun validarPropietario(libro: Libro, idUsuario: Long) {
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

    private fun rangoFechasParaConsulta(filtros: FiltrosDeLibros, origen: OrigenLibro): Pair<LocalDate?, LocalDate?> {
        return if (origen == OrigenLibro.HOME) {
            if (filtros.fechaDesde == null && filtros.fechaHasta == null) {
                val hoy = LocalDate.now()
                Pair(hoy, hoy)
            } else Pair(filtros.fechaDesde, filtros.fechaHasta)
        } else {
            Pair(filtros.fechaDesde, filtros.fechaHasta)
        }
    }

    private fun validarFlagDiff(flagDiff: OrigenLibro) {
        if (flagDiff != OrigenLibro.HOME && flagDiff != OrigenLibro.PERFIL) {
            throw BusinessException(ErrorStrings.FLAG_DIFF_INVALIDO)
        }
    }
}
