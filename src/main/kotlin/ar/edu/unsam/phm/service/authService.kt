package ar.edu.unsam.phm.service


import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val usuarioRepository: UsuarioRepository,
) {
    fun findUsuario(usuarioLogin: String, passwordLogin: String): Usuario {
        val login = usuarioLogin.trim()

        val usuario = usuarioRepository.findByEmail(login)
            ?: usuarioRepository.findFirstByNombre(login)
            ?: throw IllegalArgumentException(ErrorStrings.USUARIO_O_CONTRASENIA_INCORRECTOS)

        usuario.validarCredenciales(passwordLogin)
        return usuario
    }

    fun saveUsuario(usuario: Usuario) {
        usuario.encriptarPassword()
        usuarioRepository.save(usuario)
    }
}