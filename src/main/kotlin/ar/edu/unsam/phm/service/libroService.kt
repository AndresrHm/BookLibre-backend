package ar.edu.unsam.phm.service
import ar.edu.unsam.phm.domain.BusquedaPorNombreFiltro
import ar.edu.unsam.phm.domain.EntreFechasFiltro
import ar.edu.unsam.phm.domain.FiltroDeLibro
import ar.edu.unsam.phm.domain.GeneroFiltro
import ar.edu.unsam.phm.domain.ISBN13Filtro
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.RangoDePaginasFiltro
import ar.edu.unsam.phm.domain.UsuarioPrestadorFiltro
import ar.edu.unsam.phm.dto.FiltrosDeLibrosDTO
import ar.edu.unsam.phm.repository.LibroRepository
import org.springframework.stereotype.Service

@Service
class LibroService(
    var repositorioLibro: LibroRepository,
    var serviceReservas: ReservaService
) {
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
