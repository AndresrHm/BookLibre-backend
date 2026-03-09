package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.dto.LibroPrestadoDTO
import ar.edu.unsam.phm.dto.PageResponse
import ar.edu.unsam.phm.mapper.toLibroPrestadoDTO
import ar.edu.unsam.phm.repository.ReservaRepository
import org.springframework.stereotype.Service

@Service
class ReservaService(
    var reservaRepository: ReservaRepository
) {
    fun getReservasRealizadas(
        userID: Int, page: Int, size: Int
    ): PageResponse<LibroPrestadoDTO> {

        val reservas = reservaRepository.findByLectorID(userID, page, size)

        val reservasDTO = reservas.content.map { it.toLibroPrestadoDTO(userID) }

        val totalPages = ((reservas.totalElements + size - 1) / size)

        return PageResponse(
            content = reservasDTO,
            page = page,
            size = size,
            totalElements = reservas.totalElements,
            totalPages = totalPages,
        )
    }

    fun getReservasRecibidas(
        userID: Int, page: Int, size: Int
    ): PageResponse<LibroPrestadoDTO> {

        val reservas = reservaRepository.findByPrestadorID(userID, page, size)

        val reservasDTO = reservas.content.map { it.toLibroPrestadoDTO(userID) }

        val totalPages = ((reservas.totalElements + size - 1) / size)

        return PageResponse(
            content = reservasDTO,
            page = page,
            size = size,
            totalElements = reservas.totalElements,
            totalPages = totalPages,
        )
    }
}