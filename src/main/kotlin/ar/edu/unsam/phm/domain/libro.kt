package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.Identificable
import java.time.LocalDate

class Libro(
    val titulo: String,
    val tipoLibro: TipoLibro,
    val tipoLibroStrategy: TipoLibroStrategy = Comun,
    val descripcion: String,
    val genero: Genero,
    val autor: String,
    val cantidadPaginas: Int,
    val isbn13: Long,
    val idioma: Idioma,
    val editorial: String,
    val fechaPublicacion: LocalDate,
    val estadoLibro: EstadoLibro,
    val imagenURL: String,
    override var id: Int = -1,
) : Identificable {
    var cantidadReservas = 0
    lateinit var calificacion: Calificacion

    fun darKarma(reserva: Reserva): Int = tipoLibroStrategy.darKarma(reserva)

    fun reservar() {
        cantidadReservas += 1
    }
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

enum class TipoLibro {
    COMUN, DEDICATORIA, COLECCIONABLE
}

enum class Calificacion(val valor: Int) {
    UNO(1),
    DOS(2),
    TRES(3),
    CUATRO(4),
    CINCO(5)
}

data class Resenias(val idUsuario: Int, val puntuacion: Calificacion, val comentario: String)
