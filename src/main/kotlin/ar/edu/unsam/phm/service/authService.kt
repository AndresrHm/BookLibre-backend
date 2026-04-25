package ar.edu.unsam.phm.service


import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.RolRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val usuarioRepository: UsuarioRepository,
    private val rolRepository: RolRepository,
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
        val rolesPersistidos = usuario.roles
            .mapNotNull { it.name.takeIf { name -> name.isNotBlank() }?.let(rolRepository::findByName) }
            .toMutableSet()

        if (rolesPersistidos.isEmpty()) {
            throw BusinessException(ErrorStrings.USUARIO_DEBE_TENER_TIPO_USUARIO)
        }

        usuario.roles = rolesPersistidos
        usuario.encriptarPassword()
        usuarioRepository.save(usuario)
    }
}