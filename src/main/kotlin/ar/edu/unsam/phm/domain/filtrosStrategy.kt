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

class BusquedaPorNombreFiltro(
    val repositorioLibro: LibroRepository
): FiltroDeLibro {
    override fun cumpleCondicion(queFiltrar: FiltrosDeLibrosDTO): Boolean {
        return queFiltrar.search != null
    }
    override fun filtrar(libros: List<Libro>, queFiltrar: FiltrosDeLibrosDTO): List<Libro> {
        val search= queFiltrar.search!!
        return repositorioLibro.getElementos().filter { it.titulo.contains(search, ignoreCase = true) }
    }
}

class ISBN13Filtro(
    val repositorioLibro: LibroRepository
): FiltroDeLibro {
    override fun cumpleCondicion(queFiltrar: FiltrosDeLibrosDTO): Boolean {
        return queFiltrar.isbn13 != null
    }
    override fun filtrar(libros: List<Libro>, queFiltrar: FiltrosDeLibrosDTO): List<Libro> {
        val isbn13= queFiltrar.isbn13!!
        return repositorioLibro.getElementos().filter { it.isbn13 == isbn13 }
    }
}

class UsuarioPrestadorFiltro(
    val repositorioLibro: LibroRepository,
    val reservaService: ReservaService
): FiltroDeLibro {
    override fun cumpleCondicion(queFiltrar: FiltrosDeLibrosDTO): Boolean {
        return queFiltrar.usuarioPrestador != null
    }
    override fun filtrar(libros: List<Libro>, queFiltrar: FiltrosDeLibrosDTO): List<Libro> {
        val usuarioPrestador= queFiltrar.usuarioPrestador!!
        return reservaService.getLibrosPrestadosPorUsuario(usuarioPrestador)
    }
}