package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.errors.ErrorStrings
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import java.time.LocalDate

@Entity
class Usuario(
    var nombre: String,
    var apellido: String,
    var descripcion: String,
    var email: String,
    var celular: String,
    var ciudad: String,
    var tipoUsuario: TipoUsuario = TipoUsuario.LECTOR,
    var urlImagen: String,
) {
    @Id
    @GeneratedValue
    var id: Long? = null
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "libros_para_prestar")
    val listaDeLibrosParaPrestar = mutableListOf<Libro>()
    var biblioKarma: Int = 0
    val fechaDeCreacion: LocalDate = LocalDate.now()
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



