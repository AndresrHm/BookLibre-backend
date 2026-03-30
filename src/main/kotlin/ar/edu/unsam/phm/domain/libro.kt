package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.Identificable
import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.errors.ErrorStrings
import java.time.LocalDate
import ar.edu.unsam.phm.utils.diasEntre

const val MAX_CARACTERES_DESCRIPCION = 500
const val MAX_PAGINAS_LIBRO = 1500

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
    var activo = true

    fun init() {
        verficarTitulo()
        verficarDescripcion()
        verficarAutor()
        verficarEditorial()
        verficarPaginas()
        verficarCodigo()
    }

    fun darKarma(reserva: Reserva, cantidadReservas: Int): Int = tipoLibro.darKarma(reserva, cantidadReservas)

    fun calcularKarmaHomePage(usuarioLector: Usuario, cantidadReservas: Int, fechaIni: LocalDate, fechaFin: LocalDate): Int =
        diasEntre(fechaIni, fechaFin) * 5 + tipoLibro.posibleKarmaALector(this, cantidadReservas, usuarioLector)

    // Validaciones de creación/edición
    private fun verficarTitulo() {
        if (titulo.isBlank()) {
            throw BusinessException(ErrorStrings.LIBRO_DEBE_TENER_TITULO)
        }
    }

    private fun verficarDescripcion() {
        if (descripcion.isBlank()) {
            throw BusinessException(ErrorStrings.LIBRO_DEBE_TENER_DESCRIPCION)
        }
        if (descripcion.length > MAX_CARACTERES_DESCRIPCION) {
            throw BusinessException(ErrorStrings.descripcionNoPuedeSuperar(MAX_CARACTERES_DESCRIPCION))
        }
    }

    private fun verficarAutor() {
        if (autor.isBlank()) {
            throw BusinessException(ErrorStrings.LIBRO_DEBE_TENER_AUTOR)
        }
    }

    private fun verficarEditorial() {
        if (editorial.isBlank()) {
            throw BusinessException(ErrorStrings.LIBRO_DEBE_TENER_EDITORIAL)
        }
    }

    private fun verficarPaginas() {
        if (cantidadPaginas <= 0) {
            throw BusinessException(ErrorStrings.LIBRO_CANTIDAD_PAGINAS_POSITIVA)
        }
        if (cantidadPaginas >= MAX_PAGINAS_LIBRO) {
            throw BusinessException(ErrorStrings.libroNoPuedeSuperarPaginas(MAX_PAGINAS_LIBRO))
        }
    }

    private fun verficarCodigo() {
        if (isbn13 <= 0) {
            throw BusinessException(ErrorStrings.ISBN13_DEBE_SER_POSITIVO)
        }

        if (isbn13.toString().length != 13) {
            throw BusinessException(ErrorStrings.ISBN13_DEBE_TENER_13_DIGITOS)
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
