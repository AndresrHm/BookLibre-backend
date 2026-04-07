package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.errors.ErrorStrings
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UsuarioRepository : CrudRepository<Usuario, Long> {
    fun findByNombre(nombre: String): MutableList<Usuario>
//    fun inicioSesion(usuarioLogin : String, passwordLogin : String): Int {
//            val usuario = getElementos().find {
//                        it.email == usuarioLogin  && it.passwordSesion == passwordLogin
//                    }
//                    return usuario?.id ?: throw IllegalArgumentException(ErrorStrings.USUARIO_O_CONTRASENIA_INCORRECTOS)
//        }
//
//    fun getTipoUsuario(id: Int) = getById(id)?.tipoUsuario
}
