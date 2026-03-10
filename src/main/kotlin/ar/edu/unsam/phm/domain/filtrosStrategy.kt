package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.dto.FiltrosDeLibrosDTO
import ar.edu.unsam.phm.service.ReservaService
import ar.edu.unsam.phm.repository.LibroRepository


interface FiltroDeLibro{
    fun cumpleCondicion(queFiltrar: FiltrosDeLibrosDTO): Boolean
    fun filtrar(libros: List<Libro>, queFiltrar: FiltrosDeLibrosDTO): List<Libro>
}

class EntreFechasFiltro(
    val reservaService: ReservaService,
): FiltroDeLibro {

    override fun cumpleCondicion(queFiltrar: FiltrosDeLibrosDTO): Boolean {
        return queFiltrar.fechaDesde != null && queFiltrar.fechaHasta != null
    }

    override fun filtrar(libros: List<Libro>, queFiltrar: FiltrosDeLibrosDTO): List<Libro> {
        val desde= queFiltrar.fechaDesde!!
        val hasta= queFiltrar.fechaHasta!!
        val reservados= reservaService.getLibrosReservadosEntreFechas(desde, hasta)
        return libros.filter { it !in reservados }
    }
}

class GeneroFiltro(
    val repositorioLibro: LibroRepository
): FiltroDeLibro {
    override fun cumpleCondicion(queFiltrar: FiltrosDeLibrosDTO): Boolean {
        return queFiltrar.genero != null
    }
    override fun filtrar(libros: List<Libro>, queFiltrar: FiltrosDeLibrosDTO): List<Libro> {
        val genero= queFiltrar.genero!!
        return repositorioLibro.getElementos().filter { it.genero == genero }
    }
}

class RangoDePaginasFiltro(
    val repositorioLibro: LibroRepository
): FiltroDeLibro {
    override fun cumpleCondicion(queFiltrar: FiltrosDeLibrosDTO): Boolean {
        return queFiltrar.paginasMin != null && queFiltrar.paginasMax != null
    }
    override fun filtrar(libros: List<Libro>, queFiltrar: FiltrosDeLibrosDTO): List<Libro> {
        val min= queFiltrar.paginasMin!!
        val max= queFiltrar.paginasMax!!
        return repositorioLibro.getElementos().filter { it.cantidadPaginas in min..max }
    }
}