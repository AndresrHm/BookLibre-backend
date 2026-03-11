package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.Identificable
import ar.edu.unsam.phm.BusinessException
import java.time.LocalDate

val MAX_CARACTERES_DESCRIPCION = 500

class Libro(
    var titulo: String,
    val tipoLibro: TipoLibro,
    var descripcion: String,
    var portada: String,
    var genero: Genero,
    var autor: String,
    var cantidadPaginas: Int,
    var isbn13: Long,
    var idioma: Idioma,
    var editorial: String,
    var fechaPublicacion: LocalDate,
    var estadoLibro: EstadoLibro,
    val idPropietario: Int
) : Identificable {
    override var id: Int = 0
    private val calificaciones = mutableListOf<Calificacion>()
    private var cantidadReservas = 0

    fun init() {
        verficarTitulo()
        verficarDescripcion()
        verficarAutor()
        verficarEditorial()
        verficarPaginas()
        verficarCodigo()
    }

    fun darKarma(reserva: Reserva): Int = tipoLibro.darKarma(reserva)

    fun reservar() {
        cantidadReservas += 1
    }

    fun agregarCalificacion(calificacion: Calificacion) {
        calificaciones.add(calificacion)
    }

    fun obtenerCalificaciones(): List<Calificacion> {
        return this.calificaciones.toList()
    }

    //Para página de detalle
    fun rating(): Double {
        return 1.0; //TODO()
    }

    fun ultimasResenias(): List<Calificacion> {
        return calificaciones // Falta decidir el criterio de muestra de reseñas (recientes, más altas, etc.)
    }

    fun cantidadResenias(): Int = calificaciones.size

    private fun verficarTitulo() {
        if (titulo.isBlank()) {
            throw BusinessException("El libro debe tener un título")
        }
    }

    private fun verficarDescripcion() {
        if (descripcion.isBlank()) {
            throw BusinessException("El libro debe tener una descripción")
        }
        if (descripcion.length > MAX_CARACTERES_DESCRIPCION) {
            throw BusinessException("La descripción no puede superar los $MAX_CARACTERES_DESCRIPCION caracteres")
        }
    }

    private fun verficarAutor() {
        if (autor.isBlank()) {
            throw BusinessException("El libro debe tener un autor")
        }
    }

    private fun verficarEditorial() {
        if (editorial.isBlank()) {
            throw BusinessException("El libro debe ser de una editorial")
        }
    }

    private fun verficarPaginas() {
        if (cantidadPaginas <= 0) {
            throw BusinessException("La cantidad de páginas del libro debe ser positiva")
        }
    }

    private fun verficarCodigo() {
        if (isbn13 <= 0) {
            throw BusinessException("El código ISBN-13 debe ser positivo")
        }

        if (isbn13.toString().length != 13) {
            throw BusinessException("El código ISBN-13 debe contener 13 dígitos")
        }
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
