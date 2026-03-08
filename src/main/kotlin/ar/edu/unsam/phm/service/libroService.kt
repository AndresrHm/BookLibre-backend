package ar.edu.unsam.phm.service
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.repository.LibroRepository
import java.time.LocalDate

//class libroService(
//    var repositorioLibro: LibroRepository,
//    var serviceReservas: ReservaService
//) {
//    fun getLibrosDisponiblesPorFecha(desde: LocalDate, hasta: LocalDate): List<Libro> {
//        val librosReservados= serviceReservas.getLibrosReservadosEntreFechas(desde, hasta)
//        return repositorioLibro.allInstances().filter { it !in librosReservados }
//    }
//    fun getLibrosPorGeneroExacto(genero: Genero): List<Libro> {
//        return repositorioLibro.allInstances().filter { it.genero == genero }
//    }
//    fun getLibrosPorRangoDePaginas(min: Int, max: Int): List<Libro> {
//        return repositorioLibro.allInstances().filter { it.cantidadPaginas in min..max }
//    }
//}

