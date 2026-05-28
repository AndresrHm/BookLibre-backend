package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.LibroReservaCount
import ar.edu.unsam.phm.domain.Reserva
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ReservaRepository : CrudRepository<Reserva, Long>, JpaSpecificationExecutor<Reserva> {
    fun countByLibroId(libroId: String): Int

    @Query(
        """
        SELECT DISTINCT r.libroId
        FROM Reserva r
        WHERE r.fechaRecogida <= :fechaFin
          AND r.fechaDevolucion >= :fechaInicio
    """
    )
    fun getLibrosReservadosEntreFechas(fechaInicio: LocalDate, fechaFin: LocalDate): List<String>

    fun countByUsuarioPrestadorId(usuarioId: Long): Int

    @Query("""
    SELECT r.libroId as libroId, COUNT(r) as total
    FROM Reserva r
    WHERE r.libroId IN :libroIds
    GROUP BY r.libroId
""")
    fun countReservasByLibroIds(libroIds: List<String>): List<LibroReservaCount>
}