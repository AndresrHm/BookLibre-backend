package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.dto.CalificacionDTO
import ar.edu.unsam.phm.dto.PageResponse
import ar.edu.unsam.phm.mapper.toCalificacion
import ar.edu.unsam.phm.repository.LibroRepository
import org.springframework.stereotype.Service

@Service
class CalificacionService(
    var libroRepository: LibroRepository
) {
    fun calificarLibro(
        libroID: Int,
        calificacionDTO: CalificacionDTO
    ) {
        val calificacion = calificacionDTO.toCalificacion()

        val libro = libroRepository.getById(libroID) ?: throw NotFoundException("No existe un libro con el id $libroID.")

        libro.agregarCalificacion(calificacion)
    }

    fun obtenerCalificaciones(
        libroID: Int,
        page: Int,
        size: Int,
    ): PageResponse<Calificacion> {

        val libro = libroRepository.getById(libroID) ?: throw NotFoundException("No existe libro con el id $libroID.")

        val calificaciones = libro.obtenerCalificaciones()

        val totalPages = (calificaciones.size + size - 1) / size

        return PageResponse(
            content = calificaciones,
            page = page,
            size = size,
            totalElements = calificaciones.size,
            totalPages = totalPages
        )
    }
}