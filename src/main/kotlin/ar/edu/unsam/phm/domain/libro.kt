package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.Identificable
import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.dto.LibroUsuarioPerfilDTO
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    private val DATE_PATTERN = "dd/MM/yyyy"
    private val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)

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

    // Para página de detalle
    fun rating(): Double {
        return rating
    }

    fun actualizarRating(nuevoRating: Double) {
        cantidadResenias += 1
        rating = nuevoRating
    }

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
    fun getFechaAsString(): String = formatter.format(fechaAgregado)
    fun toLibroPerfilUsuarioDTO(): LibroUsuarioPerfilDTO {
        return LibroUsuarioPerfilDTO(
            id = this.id,
            titulo = this.titulo,
            portada = this.portada,
            genero = this.genero,
            autor = this.autor,
            fechaAgregado = this.getFechaAsString()
        )
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
