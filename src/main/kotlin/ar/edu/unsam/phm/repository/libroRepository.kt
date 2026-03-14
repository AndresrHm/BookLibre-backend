package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.PageResult
import ar.edu.unsam.phm.domain.paginate
import ar.edu.unsam.phm.dto.OrdenCalificacion
import org.springframework.stereotype.Repository

@Repository
class LibroRepository : Repositorio<Libro>() {
    fun actualizarRating(libroID: Int, rating: Double) {
        val libro = getById(libroID)
        if (libro != null) libro.actualizarRating(rating) else throw BusinessException("No existe el libro con id ${libroID}.")
    }
}