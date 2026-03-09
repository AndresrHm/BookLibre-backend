package ar.edu.unsam.phm.service
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.Libro
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

    fun obtenerLibroParaEdicion(id: Int): Libro {
        val libroParaEdicion = repositorioLibro.getById(id)

        if (libroParaEdicion == null) {
            throw NotFoundException("No existe un libro con el id $id")
        }
        //Debería verificar que el usuario que pide el libro para editar sea su dueño

        return libroParaEdicion
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
}

