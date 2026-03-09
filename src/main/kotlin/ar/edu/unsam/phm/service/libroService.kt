package ar.edu.unsam.phm.service
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.dto.LibroEditadoDTO
import ar.edu.unsam.phm.dto.toLibro
import ar.edu.unsam.phm.repository.LibroRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class LibroService(
    var repositorioLibro: LibroRepository,
    var serviceReservas: ReservaService
) {
    fun crearLibro(libro: LibroEditadoDTO): Libro {
        val nuevoLibro = libro.toLibro()
        nuevoLibro.init()

        repositorioLibro.create(nuevoLibro)
        return nuevoLibro
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

