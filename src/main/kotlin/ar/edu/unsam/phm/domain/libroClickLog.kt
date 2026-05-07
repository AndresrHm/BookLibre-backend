package ar.edu.unsam.phm.domain

import jakarta.persistence.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "libro_click_logs")
data class LibroClickLog(
    @Id
    val id: String? = null,

    val libroId: Long,
    val libroTitulo: String,
    val usuarioId: Long,
    val clickTimestamp: Instant = Instant.now(),
)