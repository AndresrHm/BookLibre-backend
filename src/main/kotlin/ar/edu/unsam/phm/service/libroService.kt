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
import ar.edu.unsam.phm.domain.UsuarioPrestadorFiltro
import ar.edu.unsam.phm.domain.paginate
import ar.edu.unsam.phm.dto.DireccionOrden
import ar.edu.unsam.phm.dto.FiltroDisponibilidadPerfil
import ar.edu.unsam.phm.dto.FiltrosDeLibrosDTO
import ar.edu.unsam.phm.dto.LibroUsuarioPerfilDTO
import ar.edu.unsam.phm.dto.OrdenLibroPerfil
import ar.edu.unsam.phm.mapper.LibroMapper
import ar.edu.unsam.phm.repository.LibroRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class LibroService(
    var repositorioLibro: LibroRepository,
    var serviceReservas: ReservaService,
    var libroMapper: LibroMapper
) {
    private fun getLibroByIdOrThrow(idLibro: Int): Libro =
        repositorioLibro.getById(idLibro)
            ?: throw NotFoundException("No existe un libro con el id $idLibro")

    private fun validarPropietario(libro: Libro, idUsuario: Int, idLibro: Int) {
        if (libro.idPropietario != idUsuario) {
            throw BusinessException("El libro con id $idLibro no pertenece al usuario $idUsuario")
        }
    }

    fun crearLibro(libro: Libro): Libro {
        val nuevoLibro = libro
        nuevoLibro.init()

        repositorioLibro.create(nuevoLibro)
        return nuevoLibro
    }

    fun obtenerLibroParaEdicion(idLibro: Int, idUsuario: Int): Libro {
        val libroParaEdicion = getLibroByIdOrThrow(idLibro)
        validarPropietario(libroParaEdicion, idUsuario, idLibro)

        return libroParaEdicion
    }

    fun editarLibro(libroParaEdicion: Libro, idLibro: Int, idUsuario: Int): Libro {
        val libroEditado = libroParaEdicion

        libroEditado.init()

        val libroOriginal = getLibroByIdOrThrow(idLibro)
        validarPropietario(libroOriginal, idUsuario, idLibro)

        repositorioLibro.update(libroEditado)

        return libroEditado
    }

    fun obtenerLibroDetalle(idLibro: Int): Libro {
        return getLibroByIdOrThrow(idLibro)
    }

    /*
    fun getLibrosDisponiblesPorFecha(desde: LocalDate, hasta: LocalDate): List<Libro> {
        val librosReservados= serviceReservas.getLibrosReservadosEntreFechas(desde, hasta)
        return repositorioLibro.allInstances().filter { it !in librosReservados }
    }
    fun getLibrosPorGeneroExacto(genero: Genero): List<Libro> {
        return repositorioLibro.allInstances().filter { it.genero == genero }
    }
    fun getLibrosPorRangoDePaginas(min: Int, max: Int): List<Libro> {
        return repositorioLibro.allInstances().filter { it.cantidadPaginas in min..max }
    }*/
    private val filtrosAplicables: List<FiltroDeLibro> = listOf(
        BusquedaPorNombreFiltro(),
        EntreFechasFiltro(serviceReservas),
        GeneroFiltro(),
        RangoDePaginasFiltro(),
        ISBN13Filtro(),
        UsuarioPrestadorFiltro(serviceReservas)
    )

    fun getLibrosPorFiltros(
        filtros: FiltrosDeLibrosDTO,
        page: Int = 0,
        size: Int = 6
    ): PageResponse<Libro> {
        require(page >= 0) { "page no puede ser negativo" }
        require(size > 0) { "size debe ser mayor a 0" }

        val librosFiltrados = filtrosAplicables.fold(repositorioLibro.getElementos()) { resultado, filtro ->
            if (filtro.cumpleCondicion(filtros)) filtro.filtrar(resultado, filtros) else resultado
        }

        val pageResult = paginate(page, size, librosFiltrados)
        val totalPages = if (pageResult.totalElements == 0) 0 else (pageResult.totalElements + size - 1) / size

        return PageResponse(
            content = pageResult.content,
            page = page,
            size = size,
            totalElements = pageResult.totalElements,
            totalPages = totalPages
        )
    }
    fun bringKarma(libro: Libro, usuarioId: Int): Int {
        val usuarioLector = serviceReservas.usuarioRepository.getById(usuarioId)
            ?: throw NotFoundException("No existe un usuario con el id $usuarioId")

        return libro.calcularKarmaHomePage(usuarioLector)
    }
//        filtrosAplicables.fold(repositorioLibro.getElementos()) { resultado, filtro ->
//            if (filtro.cumpleCondicion(filtros)) filtro.filtrar(resultado, filtros) else resultado
//        }
//    fun getLibrosPorFiltros(filtros: FiltrosDeLibrosDTO): List<Libro> =
//        filtrosAplicables.fold(repositorioLibro.getElementos()) { resultado, filtro ->
//            if (filtro.cumpleCondicion(filtros)) filtro.filtrar(resultado, filtros) else resultado
//        }


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

        val idsLibrosPrestados = serviceReservas
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
        validarPropietario(libro, idUsuario, idLibro)

        repositorioLibro.delete(libro)
    }
}
