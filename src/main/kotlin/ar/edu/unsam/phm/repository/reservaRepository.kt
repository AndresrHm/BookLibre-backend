package ar.edu.unsam.phm.repository

import FechasReserva
import ar.edu.unsam.phm.controller.ReservaType
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.LibroReservaCount
import ar.edu.unsam.phm.domain.Reserva
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ReservaRepository : CrudRepository<Reserva, Long>, JpaSpecificationExecutor<Reserva> {
    fun findFechasByLibroId(libroId: Long): List<FechasReserva>

    fun countByLibroId(libroId: Long): Int

    @Query(
        """
        SELECT DISTINCT r.libro 
        FROM Reserva r 
        WHERE r.fechaRecogida <= :fechaFin 
          AND r.fechaDevolucion >= :fechaInicio
    """
    )
    fun getLibrosReservadosEntreFechas(fechaInicio: LocalDate, fechaFin: LocalDate): List<Libro>

    fun countByUsuarioLectorId(usuarioId: Long): Int
    fun countByUsuarioPrestadorId(usuarioId: Long): Int

    @Query("""
    SELECT r.libro.id as libroId, COUNT(r) as total
    FROM Reserva r
    WHERE r.libro.id IN :libroIds
    GROUP BY r.libro.id
""")
    fun countReservasByLibroIds(libroIds: List<Long>): List<LibroReservaCount>
}