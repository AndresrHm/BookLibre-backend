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
import ar.edu.unsam.phm.dto.FiltrosDeLibrosDTO
import ar.edu.unsam.phm.repository.LibroRepository
import org.springframework.stereotype.Service

@Service
class LibroService(
    var repositorioLibro: LibroRepository,
    serviceReservas: ReservaService
) {
    fun crearLibro(libro: Libro): Libro {
        val nuevoLibro = libro
        nuevoLibro.init()

        repositorioLibro.create(nuevoLibro)
        return nuevoLibro
    }

    fun obtenerLibroParaEdicion(idLibro: Int, idUsuario: Int): Libro {
        val libroParaEdicion = repositorioLibro.getById(idLibro)

        if (libroParaEdicion == null) {
            throw NotFoundException("No existe un libro con el id $idLibro")
        }
        if (libroParaEdicion.idPropietario != idUsuario) {
            throw BusinessException("El libro con id $idLibro no pertenece al usuario $idUsuario")
        }

        return libroParaEdicion
    }

    fun editarLibro(libroParaEdicion: Libro, idLibro: Int, idUsuario: Int): Libro {
        val libroEditado = libroParaEdicion

        libroEditado.init()

        val libroOriginal = repositorioLibro.getById(libroEditado.id)
        if (libroOriginal?.idPropietario != idUsuario) {
            throw BusinessException("El libro con id $idLibro no pertenece al usuario $idUsuario")
        }

        repositorioLibro.update(libroEditado)

        return libroEditado
    }

    fun obtenerLibroDetalle(idLibro: Int): Libro {
        val libroDetalle = repositorioLibro.getById(idLibro)

        if (libroDetalle == null) {
            throw NotFoundException("No existe un libro con el id $idLibro")
        }

        return libroDetalle
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
//        filtrosAplicables.fold(repositorioLibro.getElementos()) { resultado, filtro ->
//            if (filtro.cumpleCondicion(filtros)) filtro.filtrar(resultado, filtros) else resultado
//        }
}
