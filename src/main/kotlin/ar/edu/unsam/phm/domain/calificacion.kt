package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.requireBusiness
import ar.edu.unsam.phm.repository.Identificable
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDate

@Entity
data class Calificacion(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cantidad_id")
    val reserva: Reserva,
    val puntuacion: Double,
    val comentario: String,
    val fecha: LocalDate,
): Identificable {
    override var id: Int = -1
}
