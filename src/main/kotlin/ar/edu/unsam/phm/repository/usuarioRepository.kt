package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Usuario
import org.springframework.stereotype.Repository

@Repository
class UsuarioRepository : Repositorio<Usuario>() {

    fun inicioSesion(usuarioLogin : String, passwordLogin : String): Int {
            val usuario = getElementos().find {
                        it.email == usuarioLogin  && it.passwordSesion == passwordLogin
                    }
                    return usuario?.id ?: throw IllegalArgumentException("Usuario o contraseña incorrectos")
        }

    fun getTipoUsuario(id: Int) = getById(id)?.tipoUsuario
}
