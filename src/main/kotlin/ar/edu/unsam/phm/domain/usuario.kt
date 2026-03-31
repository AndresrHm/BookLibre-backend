package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.Identificable
import java.time.LocalDate

class Usuario(
    override var id: Int = -1,
    var nombre: String,
    var apellido: String,
    var descripcion: String,
    var email: String,
    var celular: String,
    var ciudad: String,
    var tipoUsuario: TipoUsuario = TipoUsuario.LECTOR,
    var urlImagen: String,
) : Identificable {
    var biblioKarma: Int = 0
    val listaDeLibrosParaPrestar = mutableListOf<Libro>()
    val fechaDeCreacion = LocalDate.now()
    lateinit var passwordSesion: String

    fun agregarLibroParaPrestar(libro: Libro) {
        listaDeLibrosParaPrestar.add(libro)
    }

    fun sumarKarma(karma: Int) {
        if (karma <= 0)
            throw BusinessException(ErrorStrings.SOLO_SE_PUEDE_SUMAR_KARMA_POSITIVO)

        biblioKarma += karma
    }

    fun getFechaCreacionAsString(): String = fechaDeCreacion.formatear()

    fun esLector(): Boolean = this.tipoUsuario == TipoUsuario.LECTOR

    fun esPublicador(): Boolean = this.tipoUsuario == TipoUsuario.PUBLICADOR

    fun esLectorPublicador(): Boolean = this.tipoUsuario == TipoUsuario.LECTORPUBLICADOR
}

enum class TipoUsuario {
    LECTOR, PUBLICADOR, LECTORPUBLICADOR
}



