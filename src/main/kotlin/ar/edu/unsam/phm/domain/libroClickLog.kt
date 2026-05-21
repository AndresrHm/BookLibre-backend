package ar.edu.unsam.phm.domain

import jakarta.persistence.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.ZonedDateTime

@Document(collection = "libro_click_logs")
data class LibroClickLog(
    @Id
    val id: String? = null,

    val libroId: String,
    val libroTitulo: String,
    val usuarioId: Long,
    val libroPropietarioId: Long? = null,
    val clickTimestamp: String = ZonedDateTime.now().toString()
)