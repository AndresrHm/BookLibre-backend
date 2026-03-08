package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.RepositorioDatos
import java.time.LocalDate

class Libro(
    val titulo: String,
    val tipoLibro: TipoLibro,
    val descripcion: String,
    val genero: Genero,
    val autor: String,
    val cantidadPaginas: Int,
    val isbn13: Int,
    val idioma: Idioma,
    val editorial: String,
    val fechaPublicacion: LocalDate,
    val estadoLibro: EstadoLibro
) :RepositorioDatos {
    override var id: Int = 0
    val listaDeResenias = mutableListOf<Resenias>()
    var cantidadReservas = 0

    fun darKarma(reserva: reserva): Int = tipoLibro.darKarma(reserva)

    fun reservar() {
        cantidadReservas += 1
    }

    override fun searchCondition(texto: String): Boolean {
        TODO("Aun sin implementar")
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

data class Resenias(val idUsuario: Int, val puntuacion: Double, val comentario: String)
