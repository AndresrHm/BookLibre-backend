package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.errors.BusinessException
import ar.edu.unsam.phm.errors.NotFoundException
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.*
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
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

        val filtrosNormalizados = normalizarFechasPorDefecto(filtros)
        val searchSanitizado = sanitizarSearch(filtrosNormalizados.search)

        validarPaginacion(page, size)
        validarRangosFiltros(filtrosNormalizados)

        val sort = sortHomePage(ordenHomePage)
        val pageable = PageRequest.of(page, size, sort)


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

    // Paginación del perfil directamente en Mongo: se filtra y ordena por criteria,
    // evitando traer todos los libros a memoria antes de armar la página.
    private fun librosFiltradosPerfilDesdeDb(
        propietarioId: Long,
        page: Int,
        size: Int,
        disponibilidad: FiltroDisponibilidadPerfil,
        orden: OrdenLibroPerfil,
        direccion: DireccionOrden,
    ): PageResponse<Libro> {
        validarPaginacion(page, size)

        val sort = sortPerfil(orden, direccion)
        val offset = page.toLong() * size

        val pageResult = when {
            orden == OrdenLibroPerfil.DISPONIBLE_PRESTADO && disponibilidad == FiltroDisponibilidadPerfil.TODOS -> {
                val primeraDisponibilidad = if (direccion == DireccionOrden.ASCENDENTE) {
                    FiltroDisponibilidadPerfil.DISPONIBLES
                } else {
                    FiltroDisponibilidadPerfil.PRESTADOS
                }
                val segundaDisponibilidad = if (primeraDisponibilidad == FiltroDisponibilidadPerfil.DISPONIBLES) {
                    FiltroDisponibilidadPerfil.PRESTADOS
                } else {
                    FiltroDisponibilidadPerfil.DISPONIBLES
                }

                val totalPrimera = repositorioLibro.contarConFiltros(
                    search = null,
                    isbn13 = null,
                    paginasMin = null,
                    paginasMax = null,
                    generos = null,
                    soloIdPropietario = propietarioId,
                    excluirIdPropietario = null,
                    excluirIds = emptySet(),
                    disponibilidad = primeraDisponibilidad,
                )
                val totalSegunda = repositorioLibro.contarConFiltros(
                    search = null,
                    isbn13 = null,
                    paginasMin = null,
                    paginasMax = null,
                    generos = null,
                    soloIdPropietario = propietarioId,
                    excluirIdPropietario = null,
                    excluirIds = emptySet(),
                    disponibilidad = segundaDisponibilidad,
                )

                val content = mutableListOf<Libro>()
                var remaining = size

                if (offset < totalPrimera) {
                    val primerBloque = repositorioLibro.buscarConFiltrosSinPaginacion(
                        search = null,
                        isbn13 = null,
                        paginasMin = null,
                        paginasMax = null,
                        generos = null,
                        soloIdPropietario = propietarioId,
                        excluirIdPropietario = null,
                        excluirIds = emptySet(),
                        sort = sort,
                        skip = offset,
                        limit = remaining,
                        disponibilidad = primeraDisponibilidad,
                    )
                    content.addAll(primerBloque)
                    remaining -= primerBloque.size

                    if (remaining > 0) {
                        val segundoBloque = repositorioLibro.buscarConFiltrosSinPaginacion(
                            search = null,
                            isbn13 = null,
                            paginasMin = null,
                            paginasMax = null,
                            generos = null,
                            soloIdPropietario = propietarioId,
                            excluirIdPropietario = null,
                            excluirIds = emptySet(),
                            sort = sort,
                            skip = 0,
                            limit = remaining,
                            disponibilidad = segundaDisponibilidad,
                        )
                        content.addAll(segundoBloque)
                    }
                } else {
                    val segundoSkip = offset - totalPrimera
                    val segundoBloque = repositorioLibro.buscarConFiltrosSinPaginacion(
                        search = null,
                        isbn13 = null,
                        paginasMin = null,
                        paginasMax = null,
                        generos = null,
                        soloIdPropietario = propietarioId,
                        excluirIdPropietario = null,
                        excluirIds = emptySet(),
                        sort = sort,
                        skip = segundoSkip,
                        limit = remaining,
                        disponibilidad = segundaDisponibilidad,
                    )
                    content.addAll(segundoBloque)
                }

                val totalElements = totalPrimera + totalSegunda
                PageResponse(
                    content = content,
                    page = page,
                    size = size,
                    totalElements = totalElements.toInt(),
                    totalPages = if (totalElements == 0L) 0 else ((totalElements + size - 1) / size).toInt(),
                )
            }

            else -> {
                val resultado = repositorioLibro.buscarConFiltros(
                    search = null,
                    isbn13 = null,
                    paginasMin = null,
                    paginasMax = null,
                    generos = null,
                    soloIdPropietario = propietarioId,
                    excluirIdPropietario = null,
                    excluirIds = emptySet(),
                    pageable = PageRequest.of(page, size, sort),
                    disponibilidad = disponibilidad,
                )

                PageResponse(
                    content = resultado.content,
                    page = page,
                    size = size,
                    totalElements = resultado.totalElements.toInt(),
                    totalPages = resultado.totalPages,
                )
            }
        }

        return pageResult
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
