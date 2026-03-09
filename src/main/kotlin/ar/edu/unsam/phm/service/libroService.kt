package ar.edu.unsam.phm.service
import ar.edu.unsam.phm.domain.EntreFechasFiltro
import ar.edu.unsam.phm.domain.FiltroDeLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.GeneroFiltro
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.RangoDePaginasFiltro
import ar.edu.unsam.phm.dto.FiltrosDeLibrosDTO
import ar.edu.unsam.phm.repository.LibroRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class LibroService(
    var repositorioLibro: LibroRepository,
    var serviceReservas: ReservaService
) {
    private val filtrosAplicables: List<FiltroDeLibro> = listOf(
        EntreFechasFiltro(serviceReservas),
        GeneroFiltro(repositorioLibro),
        RangoDePaginasFiltro(repositorioLibro)
    )

    fun getLibrosPorFiltros(filtros: FiltrosDeLibrosDTO): List<Libro> {
        var resultado = repositorioLibro.getElementos()
        for (filtro in filtrosAplicables) {
            if (filtro.cumpleCondicion(filtros)) {
                resultado = filtro.filtrar(resultado, filtros)
            }
        }
        return resultado
    }
}
