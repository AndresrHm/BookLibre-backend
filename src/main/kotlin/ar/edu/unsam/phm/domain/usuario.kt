package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.errors.ErrorStrings
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate

@Entity
class Usuario(
    var nombre: String,
    var apellido: String,
    var descripcion: String,
    var email: String,
    var celular: String,
    var ciudad: String,
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "rol_id", referencedColumnName = "id")]
    )
    var roles: MutableSet<Rol> = mutableSetOf(),
    var urlImagen: String,
    var password: String
) {
    @Id @GeneratedValue
    var id: Long? = null
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "libros_para_prestar")
    val listaDeLibrosParaPrestar = mutableListOf<Libro>()
    var biblioKarma: Int = 0
    val fechaDeCreacion: LocalDate = LocalDate.now()

    fun agregarLibroParaPrestar(libro: Libro) {
        listaDeLibrosParaPrestar.add(libro)
    }

    fun sumarKarma(karma: Int) {
        if (karma <= 0)
            throw BusinessException(ErrorStrings.SOLO_SE_PUEDE_SUMAR_KARMA_POSITIVO)

        biblioKarma += karma
    }

    fun getFechaCreacionAsString(): String = fechaDeCreacion.formatear()

    fun esLector(): Boolean = roles.any { it.name == TipoUsuario.LECTOR.roleName }

    fun esPublicador(): Boolean = roles.any { it.name == TipoUsuario.LECTOR.roleName }

    fun esLectorPublicador(): Boolean = esLector() && esPublicador()

    fun validarCredenciales(passwordAVerificar: String) {
        if (!getDefaultEncoder().matches(passwordAVerificar, password)) {
            throw BusinessException(ErrorStrings.CREDENCIALES_INVALIDAS)
        }
    }

    private fun getDefaultEncoder(): PasswordEncoder {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()!!
    }
}

enum class TipoUsuario(val roleName: String) {
    LECTOR("LECTOR"),
    PUBLICADOR("PUBLICADOR"),
}

@Entity
@Table(name = "roles")
class Rol(
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String = ""
}
