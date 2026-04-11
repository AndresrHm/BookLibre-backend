package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.errors.ErrorStrings
import java.time.LocalDate
import ar.edu.unsam.phm.utils.diasEntre
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

const val MAX_CARACTERES_DESCRIPCION = 500
const val MAX_PAGINAS_LIBRO = 1500

@Entity
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
    val idPropietario: Long
) {
    @Id
    @GeneratedValue
    var id: Long? = 0
    var fechaAgregado = LocalDate.now()
    var activo = true

    fun init() {
        verificarTitulo()
        verificarDescripcion()
        verificarAutor()
        verificarEditorial()
        verificarPaginas()
        verificarCodigo()
    }

    fun darKarma(reserva: Reserva, cantidadReservas: Int): Int = tipoLibro.darKarma(reserva, cantidadReservas)

    fun calcularKarmaHomePage(
        usuarioLector: Usuario,
        cantidadReservas: Int,
        fechaIni: LocalDate,
        fechaFin: LocalDate
    ): Int =
        diasEntre(fechaIni, fechaFin) * 5 + tipoLibro.calcularKarma(this, cantidadReservas, usuarioLector)

    // Validaciones de creación/edición
    private fun verificarTitulo() {
        if (titulo.isBlank()) {
            throw BusinessException(ErrorStrings.LIBRO_DEBE_TENER_TITULO)
        }
    }

    private fun verificarDescripcion() {
        if (descripcion.isBlank()) {
            throw BusinessException(ErrorStrings.LIBRO_DEBE_TENER_DESCRIPCION)
        }
        if (descripcion.length > MAX_CARACTERES_DESCRIPCION) {
            throw BusinessException(ErrorStrings.descripcionNoPuedeSuperar(MAX_CARACTERES_DESCRIPCION))
        }
    }

    private fun verificarAutor() {
        if (autor.isBlank()) {
            throw BusinessException(ErrorStrings.LIBRO_DEBE_TENER_AUTOR)
        }
    }

    private fun verificarEditorial() {
        if (editorial.isBlank()) {
            throw BusinessException(ErrorStrings.LIBRO_DEBE_TENER_EDITORIAL)
        }
    }

    private fun verificarPaginas() {
        if (cantidadPaginas <= 0) {
            throw BusinessException(ErrorStrings.LIBRO_CANTIDAD_PAGINAS_POSITIVA)
        }
        if (cantidadPaginas >= MAX_PAGINAS_LIBRO) {
            throw BusinessException(ErrorStrings.libroNoPuedeSuperarPaginas(MAX_PAGINAS_LIBRO))
        }
    }

    private fun verificarCodigo() {
        if (isbn13 <= 0) {
            throw BusinessException(ErrorStrings.ISBN13_DEBE_SER_POSITIVO)
        }

        if (isbn13.toString().length != 13) {
            throw BusinessException(ErrorStrings.ISBN13_DEBE_TENER_13_DIGITOS)
        }
    }

    fun getFechaAsString(): String = fechaAgregado.formatear()

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
