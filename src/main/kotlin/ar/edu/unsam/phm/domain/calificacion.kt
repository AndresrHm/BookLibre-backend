package ar.edu.unsam.phm.domain

import BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDate

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["usuario_id", "libro_id"])])
data class Calificacion(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    val usuario: Usuario,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id", nullable = false)
    val libro: Libro,

    var puntuacion: Double,
    var comentario: String,
    var fecha: LocalDate,
) : BaseEntity() {
    @Id
    @GeneratedValue
    var id: Long? = null
}
