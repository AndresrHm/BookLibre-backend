package ar.edu.unsam.phm.service


import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class LoginRegistroService(
    val usuarioRepository: UsuarioRepository,
) {
    fun buscarUsuarioContraseniaUsuario(usuarioLogin : String, passwordLogin : String)=usuarioRepository.inicioSesion(usuarioLogin, passwordLogin)

    fun crearUsuario(usuario : Usuario) {usuarioRepository.create(usuario)}

}