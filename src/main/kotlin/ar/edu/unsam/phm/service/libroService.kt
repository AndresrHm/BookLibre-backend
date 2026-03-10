package ar.edu.unsam.phm.service
import ar.edu.unsam.phm.domain.BusquedaPorNombreFiltro
import ar.edu.unsam.phm.domain.EntreFechasFiltro
import ar.edu.unsam.phm.domain.FiltroDeLibro
import ar.edu.unsam.phm.domain.GeneroFiltro
import ar.edu.unsam.phm.domain.ISBN13Filtro
import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.RangoDePaginasFiltro
import ar.edu.unsam.phm.domain.UsuarioPrestadorFiltro
import ar.edu.unsam.phm.dto.FiltrosDeLibrosDTO
import ar.edu.unsam.phm.dto.LibroEdicionDTO
import ar.edu.unsam.phm.dto.toLibro
import ar.edu.unsam.phm.repository.LibroRepository
import org.springframework.stereotype.Service

@Service
class LibroService(
    var repositorioLibro: LibroRepository,
    var serviceReservas: ReservaService
) {
    fun crearLibro(libro: LibroEdicionDTO): Libro {
        val nuevoLibro = libro.toLibro()
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

    fun editarLibro(libroParaEdicion: LibroEdicionDTO, idLibro: Int, idUsuario: Int): Libro {
        val libroEditado = libroParaEdicion.toLibro()

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
        BusquedaPorNombreFiltro(repositorioLibro),
        EntreFechasFiltro(serviceReservas),
        GeneroFiltro(repositorioLibro),
        RangoDePaginasFiltro(repositorioLibro),
        ISBN13Filtro(repositorioLibro),
        UsuarioPrestadorFiltro(repositorioLibro, serviceReservas)
    )
    fun getLibrosPorFiltros(filtros: FiltrosDeLibrosDTO): List<Libro> =
        filtrosAplicables.fold(repositorioLibro.getElementos()) { resultado, filtro ->
            if (filtro.cumpleCondicion(filtros)) filtro.filtrar(resultado, filtros) else resultado
        }
}
