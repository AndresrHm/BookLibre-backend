package ar.edu.unsam.phm.service


import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class LoginRegistroService(
    val usuarioRepository: UsuarioRepository,
) {
    fun buscarUsuarioContraseniaUsuario(usuarioLogin: String, passwordLogin: String) {
//        TODO: Corregir usuarioRepository.inicioSesion(usuarioLogin, passwordLogin)
        throw IllegalArgumentException(ErrorStrings.USUARIO_O_CONTRASENIA_INCORRECTOS)
    }

    fun crearUsuario(usuario: Usuario) {
        usuarioRepository.save(usuario)
    }
}