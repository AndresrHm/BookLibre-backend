package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.DireccionOrden
import ar.edu.unsam.phm.dto.FiltroDisponibilidadPerfil
import ar.edu.unsam.phm.dto.FiltrosDeLibros
import ar.edu.unsam.phm.dto.OrdenHomePage
import ar.edu.unsam.phm.dto.OrdenLibroPerfil
import ar.edu.unsam.phm.dto.OrigenLibro
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class LibroService(
    private val libroRepository: LibroRepository,
    private val usuarioRepository: UsuarioRepository
) {

    // Crea un libro validando que el usuario propietario sea publicador.
    @Transactional
    fun crearLibro(nuevoLibro: Libro): Libro {
        val usuario = usuarioRepository.findById(nuevoLibro.idPropietario)
            .orElseThrow { NotFoundException(ErrorStrings.usuarioNoEncontradoPorId(nuevoLibro.idPropietario)) }

        if (usuario.esLector()) {
            throw BusinessException(ErrorStrings.SOLO_PUBLICADOR_PUEDE_CREAR_LIBROS)
        }

        nuevoLibro.init()
        return libroRepository.save(nuevoLibro)
    }

    // Obtiene un libro por id o lanza error si no existe.
    @Transactional(readOnly = true)
    fun obtenerLibro(idLibro: Long): Libro {
        return libroRepository.findById(idLibro)
            .orElseThrow { NotFoundException(ErrorStrings.libroNoEncontradoPorId(idLibro)) }
    }

    // Edita un libro validando permisos del propietario y datos obligatorios.
    @Transactional
    fun editarLibro(libroEditado: Libro, idUsuarioActual: Long): Libro {
        val usuario = usuarioRepository.findById(libroEditado.idPropietario)
            .orElseThrow { NotFoundException(ErrorStrings.usuarioNoEncontradoPorId(libroEditado.idPropietario)) }

        if (usuario.esLector()) {
            throw BusinessException(ErrorStrings.SOLO_PUBLICADOR_PUEDE_EDITAR_LIBROS)
        }

        val libroOriginal = obtenerLibro(libroEditado.id!!)
        validarPropietario(libroOriginal, idUsuarioActual)

        libroEditado.init()
        return libroRepository.save(libroEditado)
    }
    // Aplica filtros y ordenamiento sobre los libros de un propietario.
    @Transactional(readOnly = true)
    fun obtenerLibrosPorPropietarioConFiltros(
        propietarioId: Long,
        filtro: FiltroDisponibilidadPerfil = FiltroDisponibilidadPerfil.TODOS,
        orden: OrdenLibroPerfil = OrdenLibroPerfil.TITULO,
        direccion: DireccionOrden = DireccionOrden.ASCENDENTE
    ): List<Libro> {
        return filtrarYOrdenarLibrosPerfil(propietarioId, filtro, orden, direccion)
    }

    // Desactiva todos los libros de un propietario.
    @Transactional
    fun desactivarLibrosPorPropietario(propietarioId: Long) {
        libroRepository.eliminarPorIDPropietario(propietarioId)
    }
    // Borra lógicamente un libro si pertenece al usuario y no está prestado.
    @Transactional
    fun borrarLibro(idLibro: Long, idUsuario: Long) {
        val libro = obtenerLibro(idLibro)
        validarPropietario(libro, idUsuario)

        val libroPrestado = libroRepository.buscarLibrosPrestados().any { it.id == idLibro }
        if (libroPrestado) {
            throw BusinessException(ErrorStrings.LIBRO_PRESTADO_NO_SE_PUEDE_BORRAR)
        }

        libro.activo = false
        libroRepository.save(libro)
    }

    // Obtiene los prestadores disponibles, filtrando por texto opcional.
    @Transactional(readOnly = true)
    fun obtenerPrestadoresDisponibles(search: String?, userId: Long): List<String> {
        usuarioRepository.findById(userId)
            .orElseThrow { NotFoundException(ErrorStrings.usuarioNoEncontradoPorId(userId)) }

        val termino = search?.trim().orEmpty()

        return libroRepository.filtrarLibrosActivos()
            .filter { it.idPropietario != userId }
            .filter { libro ->
                libroRepository.buscarLibrosPrestados().none { it.id == libro.id }
            }
            .mapNotNull { libro ->
                usuarioRepository.findById(libro.idPropietario)
                    .map { it.nombre }
                    .orElse(null)
            }
            .distinct()
            .filter { termino.isBlank() || it.contains(termino, ignoreCase = true) }
            .sortedBy { it.lowercase() }
    }

    private fun validarPropietario(libro: Libro, idUsuario: Long) {
        if (libro.idPropietario != idUsuario) {
            throw BusinessException(ErrorStrings.libroNoPerteneceAlUsuario(libro.id, idUsuario))
        }
    }

    // Indica si un libro está prestado o disponible.
    @Transactional(readOnly = true)
    fun disponibilidadLibro(idLibro: Long?): String {
        if (idLibro == null) return "DISPONIBLE"

        return if (libroRepository.buscarLibrosPrestados().any { it.id == idLibro }) {
            "PRESTADO"
        } else {
            "DISPONIBLE"
        }
    }

    // Calcula el karma potencial del libro para el usuario lector.
    @Transactional(readOnly = true)
    fun bringKarma(
        libro: Libro,
        cantidadReservas: Int,
        usuarioId: Long,
        fechaIni: LocalDate?,
        fechaFin: LocalDate?
    ): Int {
        val usuarioLector = usuarioRepository.findById(usuarioId)
            .orElseThrow { NotFoundException(ErrorStrings.usuarioNoEncontradoPorId(usuarioId)) }

        val fechaDesde = fechaIni ?: LocalDate.now()
        val fechaHasta = fechaFin ?: LocalDate.now()

        return libro.calcularKarmaHomePage(usuarioLector, cantidadReservas, fechaDesde, fechaHasta)
    }

    // Ejecuta el filtrado principal de HOME o PERFIL y pagina el resultado.
    @Transactional(readOnly = true)
    fun getLibrosPorFiltros(
        filtros: FiltrosDeLibros,
        page: Int,
        size: Int,
        ordenHomePage: OrdenHomePage,
        userId: Long,
        flagDiff: OrigenLibro,
        disponibilidad: FiltroDisponibilidadPerfil,
        orden: OrdenLibroPerfil,
        direccion: DireccionOrden
    ): PageResponse<Libro> {
        return when (flagDiff) {
           OrigenLibro.HOME -> {
               paginar(emptyList(), page, size)
           }
               //librosFiltradosHomePage(
//                filtrosDeHome,
//                page,
//                size,
//                ordenHomePage,
//                userId
//            )
            OrigenLibro.PERFIL -> {
                val librosOrdenados = filtrarYOrdenarLibrosPerfil(userId, disponibilidad, orden, direccion)
                paginar(librosOrdenados, page, size)
            }
        }
    }

    private fun filtrarYOrdenarLibrosPerfil(
        propietarioId: Long,
        disponibilidad: FiltroDisponibilidadPerfil,
        orden: OrdenLibroPerfil,
        direccion: DireccionOrden
    ): List<Libro> {
        val librosDelUsuario = libroRepository.buscarPorIdPropietario(propietarioId)

        val librosFiltrados = when (disponibilidad) {
            FiltroDisponibilidadPerfil.TODOS -> librosDelUsuario
            FiltroDisponibilidadPerfil.DISPONIBLES -> libroRepository.buscarLibrosDisponiblesPorPropietario(propietarioId)
            FiltroDisponibilidadPerfil.PRESTADOS -> libroRepository.buscarLibrosPrestadosPorPropietario(propietarioId)
        }

        val librosPrestadosIds = libroRepository.buscarLibrosPrestados().mapNotNull { it.id }.toSet()

        val comparadorBase = when (orden) {
            OrdenLibroPerfil.TITULO -> compareBy<Libro>({ it.titulo.lowercase() }, { it.autor.lowercase() })
            OrdenLibroPerfil.DISPONIBLE_PRESTADO -> compareBy<Libro>({ it.id in librosPrestadosIds }, { it.titulo.lowercase() }, { it.autor.lowercase() })
            OrdenLibroPerfil.FECHA_AGREGADO -> compareBy<Libro> { it.fechaAgregado }
        }

        val comparadorFinal = when (direccion) {
            DireccionOrden.ASCENDENTE -> comparadorBase.thenBy { it.id }
            DireccionOrden.DESCENDENTE -> comparadorBase.reversed().thenBy { it.id }
        }

        return librosFiltrados.sortedWith(comparadorFinal)
    }
    //private fun filtrarYOrdenarLibrosHome(
    //    fun librosFiltradosHomePage(
//        filtros: FiltrosDeLibros,
//        page: Int = 0,
//        size: Int = 6,
//        ordenHomePage: OrdenHomePage,
//        userId: Long
//    ): PageResponse<Libro> {
//        val filtrosNormalizados = normalizarFechasPorDefecto(filtros)
//
//        validarPaginacion(page, size)
//        validarRangosFiltros(filtrosNormalizados)
//
//        val librosFiltrados: List<Libro> = try {
//            filtrosAplicables
//                .fold(repositorioLibro.filtrarLibrosActivos()) { resultado: List<Libro>, filtro: FiltroDeLibro ->
//                    if (filtro.cumpleCondicion(filtrosNormalizados)) filtro.filtrar(
//                        resultado,
//                        filtrosNormalizados
//                    ) else resultado
//                }
//                .let { filtrados: List<Libro> ->
//                    if (filtrosNormalizados.usuarioPrestador == null) {
//                        filtrados.filter { it.idPropietario != userId }
//                    } else {
//                        filtrados
//                    }
//                }
//        } catch (e: IllegalArgumentException) {
//            throw BusinessException(e.message ?: ErrorStrings.PARAMETROS_FILTRO_INVALIDOS)
//        }
//
//        val librosPorFiltroOrdenado = when (ordenHomePage) {
//            OrdenHomePage.NOMBRE_ASC -> librosFiltrados.sortedBy { it.titulo.lowercase() }
//            OrdenHomePage.NOMBRE_DESC -> librosFiltrados.sortedByDescending { it.titulo.lowercase() }
//            OrdenHomePage.AUTOR_ASC -> librosFiltrados.sortedBy { it.autor.lowercase() }
//            OrdenHomePage.AUTOR_DESC -> librosFiltrados.sortedByDescending { it.autor.lowercase() }
//            OrdenHomePage.USER_ASC -> librosFiltrados.sortedBy {
//                repositorioUsuario.getById(it.idPropietario)?.nombre?.lowercase() ?: ""
//            }
//
//            OrdenHomePage.USER_DESC -> librosFiltrados.sortedByDescending {
//                repositorioUsuario.getById(it.idPropietario)?.nombre?.lowercase() ?: ""
//            }
//        }
//
//
//        val pageResult = paginate(page, size, librosPorFiltroOrdenado)
//        val totalPages = if (pageResult.totalElements == 0) 0 else (pageResult.totalElements + size - 1) / size
//
//        return PageResponse(
//            content = pageResult.content,
//            page = page,
//            size = size,
//            totalElements = pageResult.totalElements,
//            totalPages = totalPages
//        )
//    }

    private fun paginar(libros: List<Libro>, page: Int, size: Int): PageResponse<Libro> {
        val start = page * size
        val end = start + size
        val paginatedContent = if (start < libros.size) {
            libros.subList(start, minOf(end, libros.size))
        } else {
            emptyList()
        }

        val totalPages = (libros.size + size - 1) / size

        return PageResponse(
            content = paginatedContent,
            page = page,
            size = size,
            totalElements = libros.size,
            totalPages = totalPages
        )
    }
}

