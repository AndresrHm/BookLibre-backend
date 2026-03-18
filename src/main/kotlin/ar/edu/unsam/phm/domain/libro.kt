package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.Identificable
import ar.edu.unsam.phm.BusinessException
import java.time.LocalDate

const val MAX_CARACTERES_DESCRIPCION = 500

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
    override var id: Int = -1
    val fechaAgregado = LocalDate.now()
    var cantidadReservas = 0
    var cantidadResenias = 0
    var rating: Double = 0.0

    fun init() {
        verficarTitulo()
        verficarDescripcion()
        verficarAutor()
        verficarEditorial()
        verficarPaginas()
        verficarCodigo()
    }

    fun darKarma(reserva: Reserva): Int = tipoLibro.darKarma(reserva)

    fun reservar(
        fechaRecogida: LocalDate,
        fechaDevolucion: LocalDate,
        fechasDeReservas: List<Pair<LocalDate, LocalDate>>
    ) {
        validarReserva(fechaRecogida, fechaDevolucion, fechasDeReservas)
        cantidadReservas += 1
    }

    fun validarReserva(
        fechaRecogida: LocalDate,
        fechaDevolucion: LocalDate,
        fechasDeReserva: List<Pair<LocalDate, LocalDate>>
    ) {
        val haySolapamiento = (fechasDeReserva.any {
            // first: Inicio de reserva existente
            // second: Fin de reserva existente
            fechaRecogida <= it.second && fechaDevolucion >= it.first
        })

        if (haySolapamiento) throw BusinessException("Existe una reserva en la fecha estipulada")
    }

    // Para página de detalle
    fun rating(): Double {
        return rating
    }

    fun actualizarRating(nuevoRating: Double) {
        if (nuevoRating < 0.0 || nuevoRating > 5.0)
            throw BusinessException("La calificación del libro debe ser un número entre 0 y 5")
        cantidadResenias += 1
        rating = nuevoRating
    }

    // Validaciones de creación/edición
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

    fun getFechaAsString(): String = fechaAgregado.formatear()
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
