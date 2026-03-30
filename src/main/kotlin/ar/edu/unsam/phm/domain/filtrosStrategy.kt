package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.dto.FiltrosDeLibros
import ar.edu.unsam.phm.repository.ReservaRepository


interface FiltroDeLibro{
    fun cumpleCondicion(queFiltrar: FiltrosDeLibros): Boolean
    fun filtrar(libros: List<Libro>, queFiltrar: FiltrosDeLibros): List<Libro>
}

class EntreFechasFiltro(
    val reservaRepo: ReservaRepository,
): FiltroDeLibro {

    override fun cumpleCondicion(queFiltrar: FiltrosDeLibros): Boolean {
        return queFiltrar.fechaDesde != null && queFiltrar.fechaHasta != null
    }

    override fun filtrar(libros: List<Libro>, queFiltrar: FiltrosDeLibros): List<Libro> {
        val desde= queFiltrar.fechaDesde!!
        val hasta= queFiltrar.fechaHasta!!
        val reservados= reservaRepo.getLibrosReservadosEntreFechas(desde, hasta)
        return libros.filter { it !in reservados }
    }
}

class GeneroFiltro : FiltroDeLibro {
    override fun cumpleCondicion(queFiltrar: FiltrosDeLibros): Boolean {
        return !queFiltrar.generos.isNullOrEmpty()
    }

    override fun filtrar(libros: List<Libro>, queFiltrar: FiltrosDeLibros): List<Libro> {
        val generosABuscar = queFiltrar.generos!!.toSet()
        return libros.filter { it.genero in generosABuscar }
    }
}

class RangoDePaginasFiltro : FiltroDeLibro {
    override fun cumpleCondicion(queFiltrar: FiltrosDeLibros): Boolean {
        return queFiltrar.paginasMin != null && queFiltrar.paginasMax != null
    }

    override fun filtrar(libros: List<Libro>, queFiltrar: FiltrosDeLibros): List<Libro> {
        val min= queFiltrar.paginasMin!!
        val max= queFiltrar.paginasMax!!
        return libros.filter { it.cantidadPaginas in min..max }
    }
}

class BusquedaPorNombreFiltro : FiltroDeLibro {
    override fun cumpleCondicion(queFiltrar: FiltrosDeLibros): Boolean {
        return !queFiltrar.search.isNullOrBlank()
    }

    override fun filtrar(libros: List<Libro>, queFiltrar: FiltrosDeLibros): List<Libro> {
        val search= queFiltrar.search!!
        return libros.filter { it.titulo.contains(search, ignoreCase = true) }
    }
}

class ISBN13Filtro : FiltroDeLibro {
    override fun cumpleCondicion(queFiltrar: FiltrosDeLibros): Boolean {
        return queFiltrar.isbn13 != null
    }

    override fun filtrar(libros: List<Libro>, queFiltrar: FiltrosDeLibros): List<Libro> {
        val isbn13= queFiltrar.isbn13!!
        return libros.filter { it.isbn13 == isbn13 }
    }
}

class UsuarioPrestadorFiltro(
    val reservaRepository: ReservaRepository
): FiltroDeLibro {
    override fun cumpleCondicion(queFiltrar: FiltrosDeLibros): Boolean {
        return queFiltrar.usuarioPrestador != null
    }
    override fun filtrar(libros: List<Libro>, queFiltrar: FiltrosDeLibros): List<Libro> {
        val usuarioPrestador= queFiltrar.usuarioPrestador!!
        val prestadosPorUsuario= reservaRepository.getLibrosPrestadosPorUsuario(usuarioPrestador.id)
        return libros.filter { it in prestadosPorUsuario }

    }
}