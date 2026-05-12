package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.errors.BusinessException
import ar.edu.unsam.phm.errors.NotFoundException
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.domain.paginate
import ar.edu.unsam.phm.dto.*
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.predicate.LibroSpecifications.LibroSpecifications.activo
import ar.edu.unsam.phm.predicate.LibroSpecifications.LibroSpecifications.dePropietario
import ar.edu.unsam.phm.predicate.LibroSpecifications.LibroSpecifications.filtrosHome
import ar.edu.unsam.phm.predicate.LibroSpecifications.disponibleHoy
import ar.edu.unsam.phm.predicate.LibroSpecifications.reservadoHoy
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import ar.edu.unsam.phm.utils.sanitizarSearch
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class LibroService(
    var repositorioLibro: LibroRepository,
    var repositorioReservas: ReservaRepository,
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

        val libroOriginal = getLibroByIdOrThrow(libroEditado.id!!)
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

            OrigenLibro.PERFIL -> {
                val librosOrdenados = filtrarYOrdenarLibrosPerfil(
                    propietarioId = userId,
                    disponibilidad = disponibilidadLibrosPerfil,
                    orden = ordenLibroPerfil,
                    direccion = direccionOrdenPerfil,
                )
                paginar(librosOrdenados, page, size)
            }
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

        val filtrosNormalizados = normalizarFechasPorDefecto(filtros)
        val searchSanitizado = sanitizarSearch(filtrosNormalizados.search)

        validarPaginacion(page, size)
        validarRangosFiltros(filtrosNormalizados)

        val sort = sortHomePage(ordenHomePage)
        val pageable = PageRequest.of(page, size, sort)

        /*val spec = filtrosHome(
            search = searchSanitizado,
            isbn13 = filtrosNormalizados.isbn13,
            paginasMin = filtrosNormalizados.paginasMin,
            paginasMax = filtrosNormalizados.paginasMax,
            generos = filtrosNormalizados.generos,
            usuarioPrestadorId = filtrosNormalizados.usuarioPrestador?.id,
            userId = userId,
            fechaDesde = filtrosNormalizados.fechaDesde,
            fechaHasta = filtrosNormalizados.fechaHasta,
        )

        val resultado: Page<Libro> = repositorioLibro.findAll(spec, pageable)*/

        val idsReservados: Set<String> = if (filtrosNormalizados.fechaDesde != null && filtrosNormalizados.fechaHasta != null) {
            repositorioReservas.getLibrosReservadosEntreFechas(
                filtrosNormalizados.fechaDesde,
                filtrosNormalizados.fechaHasta
            ).toSet()
        } else emptySet()

        val resultado: Page<Libro> = repositorioLibro.buscarConFiltros(
            search = searchSanitizado,
            isbn13 = filtrosNormalizados.isbn13,
            paginasMin = filtrosNormalizados.paginasMin,
            paginasMax = filtrosNormalizados.paginasMax,
            generos = filtrosNormalizados.generos,
            soloIdPropietario = filtrosNormalizados.usuarioPrestador?.id,
            excluirIdPropietario = if (filtrosNormalizados.usuarioPrestador == null) userId else null,
            excluirIds = idsReservados,
            pageable = pageable,
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
        return if (estaReservadoHoy(libro.id!!)) "PRESTADO" else "DISPONIBLE"
    }

    fun obtenerPrestadoresDisponibles(search: String?, userId: Long): List<String> {
        repositorioUsuario.findByIdOrNull(userId)
            ?: throw NotFoundException(ErrorStrings.usuarioNoExistePorId(userId))

        val termino = search?.trim().orEmpty()

        return repositorioLibro.findByActivoTrue()
            .filter { it.idPropietario != userId && !estaReservadoHoy(it.id!!) }
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
        return libroId in repositorioReservas.getLibrosReservadosEntreFechas(hoy, hoy).toSet()
    }

    private fun sortHomePage(ordenHomePage: OrdenHomePage): Sort = when (ordenHomePage) {
        OrdenHomePage.NOMBRE_ASC -> Sort.by("titulo").ascending()
        OrdenHomePage.NOMBRE_DESC -> Sort.by("titulo").descending()
        OrdenHomePage.AUTOR_ASC -> Sort.by("autor").ascending()
        OrdenHomePage.AUTOR_DESC -> Sort.by("autor").descending()
        OrdenHomePage.USER_ASC -> Sort.unsorted()
        OrdenHomePage.USER_DESC -> Sort.unsorted()
    }

    private fun filtrarYOrdenarLibrosPerfil(
        propietarioId: Long,
        disponibilidad: FiltroDisponibilidadPerfil,
        orden: OrdenLibroPerfil,
        direccion: DireccionOrden,
    ): List<Libro> {
        /*val baseSpec = Specification.where(activo()).and(dePropietario(propietarioId))

        val specFinal = when (disponibilidad) {
            FiltroDisponibilidadPerfil.TODOS -> baseSpec
            FiltroDisponibilidadPerfil.DISPONIBLES -> baseSpec.and(disponibleHoy())
            FiltroDisponibilidadPerfil.PRESTADOS -> baseSpec.and(reservadoHoy())
        }

        val librosFiltrados = repositorioLibro.findAll(specFinal)

        val librosPrestadosIds = repositorioLibro.findAll(Specification.where(activo()).and(reservadoHoy()))
            .mapNotNull { it.id }.toSet()*/


        val librosActivos = repositorioLibro.findByActivoTrueAndIdPropietario(propietarioId)
        val idsReservadosHoy = repositorioReservas
            .getLibrosReservadosEntreFechas(LocalDate.now(), LocalDate.now())
            .toSet()

        val librosFiltrados = when (disponibilidad) {
            FiltroDisponibilidadPerfil.TODOS -> librosActivos
            FiltroDisponibilidadPerfil.DISPONIBLES -> librosActivos.filter { it.id !in idsReservadosHoy }
            FiltroDisponibilidadPerfil.PRESTADOS -> librosActivos.filter { it.id in idsReservadosHoy }
        }

        val comparadorBase = when (orden) {
            OrdenLibroPerfil.TITULO -> compareBy<Libro>({ it.titulo.lowercase() }, { it.autor.lowercase() })
            OrdenLibroPerfil.DISPONIBLE_PRESTADO -> compareBy<Libro>(
                { it.id in idsReservadosHoy },
                { it.titulo.lowercase() },
                { it.autor.lowercase() }
            )
            OrdenLibroPerfil.FECHA_AGREGADO -> compareBy { it.fechaAgregado }
        }

        val comparadorFinal = when (direccion) {
            DireccionOrden.ASCENDENTE -> comparadorBase.thenBy { it.id }
            DireccionOrden.DESCENDENTE -> comparadorBase.reversed().thenBy { it.id }
        }

        return librosFiltrados.sortedWith(comparadorFinal)
    }

    private fun paginar(libros: List<Libro>, page: Int, size: Int): PageResponse<Libro> {
        validarPaginacion(page, size)

        val pageResult = paginate(page, size, libros)
        val totalPages = if (pageResult.totalElements == 0) 0 else (pageResult.totalElements + size - 1) / size

        return PageResponse(
            content = pageResult.content,
            page = page,
            size = size,
            totalElements = pageResult.totalElements,
            totalPages = totalPages,
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
