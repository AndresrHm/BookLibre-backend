package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.dto.PageResponse
import ar.edu.unsam.phm.repository.LibroRepository
import org.springframework.stereotype.Service

@Service
class CalificacionService(
    var libroRepository: LibroRepository
) {
    fun obtenerCalificaciones(
        libroID: Int,
        page: Int,
        size: Int,
    ): PageResponse<Calificacion> {

        val libro = libroRepository.getById(libroID) ?: throw NotFoundException("No existe libro con el id $libroID")

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