package ar.edu.unsam.phm.domain

import BaseEntity
import java.time.LocalDate
import ar.edu.unsam.phm.utils.diasEntre
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.springframework.data.mongodb.core.mapping.Document

data class FechaReserva(
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
)

@Document(collection = "libros")
class Libro(
    var titulo: String,
    @Enumerated(EnumType.STRING)
    val tipoLibro: TipoLibro,
    var descripcion: String,
    var portada: String,
    @Enumerated(EnumType.STRING)
    var genero: Genero,
    var autor: String,
    var cantidadPaginas: Int,
    var isbn13: Long,
    @Enumerated(EnumType.STRING)
    var idioma: Idioma,
    var editorial: String,
    var fechaPublicacion: LocalDate,
    @Enumerated(EnumType.STRING)
    var estadoLibro: EstadoLibro,
    val idPropietario: Long,
) : BaseEntity() {
    @Id
    @GeneratedValue
    lateinit var id: String
    var fechaAgregado: LocalDate? = LocalDate.now()
    var activo = true
    var calificacion: Double = 0.0
    var ultimasCalificaciones: List<Calificacion> = listOf()
    var fechasReserva: MutableList<FechaReserva> = mutableListOf()

    fun darKarma(reserva: Reserva, cantidadReservas: Int): Int = tipoLibro.darKarma(this, reserva.usuarioLector, cantidadReservas)

    fun calcularKarmaHomePage(
        usuarioLector: Usuario,
        cantidadReservas: Int,
        fechaIni: LocalDate,
        fechaFin: LocalDate
    ): Int =
        diasEntre(fechaIni, fechaFin) * 5 + tipoLibro.calcularKarma(this, cantidadReservas, usuarioLector)

    fun actualizarCalificacion(nuevaCalificacion: Double) {
        calificacion = nuevaCalificacion
    }

    fun actualizarUltimasCalificaciones(calificaciones: List<Calificacion>) {
        ultimasCalificaciones = calificaciones
    }

    fun agregarFechaReserva(fechaInicio: LocalDate, fechaFin: LocalDate) {

        fechasReserva.add(0, FechaReserva(fechaInicio, fechaFin))
    }

    fun calificacionPromedio() = calificacion


    fun getFechaAsString(): String = fechaAgregado?.formatear() ?: ""

    fun tipoComoTexto(): String =
        if (tipoLibro == TipoLibro.CON_DEDICATORIA) "conDedicatoria"
        else if (tipoLibro == TipoLibro.COLECCIONABLE) "coleccionable"
        else "comun"
}

enum class Genero {
    DRAMA, CIENCIA_FICCION, ROMANCE, AUTOAYUDA, DISENO, LITERATURA_CLASICA
}

enum class Idioma {
    ESPANOL, INGLES, FRANCES, PORTUGUES
}

enum class EstadoLibro {
    EXCELENTE, MUY_BUENO, BUENO, MALO, REGULAR
}

interface LibroReservaCount {
    fun getLibroId(): String
    fun getTotal(): Long
}
