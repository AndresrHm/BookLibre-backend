package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.*
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.stereotype.Component

@Component
class UsuarioMapper(
    private val usuarioService: UsuarioService
) {

    fun usuarioEdicionDTOtoUsuario(usuarioEdicionDTO: UsuarioEdicionDTO, usuarioExistente: Usuario): Usuario {
        return Usuario(
            nombre = usuarioEdicionDTO.nombre,
            apellido = usuarioEdicionDTO.apellido,
            descripcion = usuarioEdicionDTO.descripcion,
            ciudad = usuarioEdicionDTO.ciudad,
            tipoUsuario = usuarioEdicionDTO.tipoUsuario,
            urlImagen = usuarioEdicionDTO.urlImagen,
            email = usuarioExistente.email,
            celular = usuarioExistente.celular
        ).apply {
            id = usuarioEdicionDTO.id
            biblioKarma = usuarioExistente.biblioKarma
            passwordSesion = usuarioExistente.passwordSesion
        }
    }

    fun toUsuarioPerfilDTO(usuario: Usuario): UsarioPerfilDTO = UsarioPerfilDTO(
        id = usuario.id,
        nombre = usuario.nombre,
        apellido = usuario.apellido,
        descripcion = usuario.descripcion,
        ciudad = usuario.ciudad,
        tipoUsuario = usuario.tipoUsuario,
        urlImagen = usuario.urlImagen,
        email = usuario.email,
        celular = usuario.celular,
        biblioKarma = usuario.biblioKarma,
        fechaCreacion = usuario.getFechaCreacionAsString(),
        cantidadDePrestamos = usuarioService.obtenerCantidadDeLibrosPrestados(usuario.id!!),
        cantidadDeLibrosLeidos = usuarioService.obtenerCantidadDeLibrosLeidos(usuario.id!!)
    )

    fun toUsuarioHeaderDTO(usuario: Usuario): UsuarioHeaderDTO = UsuarioHeaderDTO(
        id = usuario.id,
        urlImagen = usuario.urlImagen,
        biblioKarma = usuario.biblioKarma
    )

    fun toUsuarioEdicionDTO(usuario: Usuario): UsuarioEdicionDTO = UsuarioEdicionDTO(
        id = usuario.id,
        nombre = usuario.nombre,
        apellido = usuario.apellido,
        descripcion = usuario.descripcion,
        ciudad = usuario.ciudad,
        tipoUsuario = usuario.tipoUsuario,
        urlImagen = usuario.urlImagen
    )

    fun toUsuarioKarmaDTO(usuario: Usuario): UsuarioKarmaDTO = UsuarioKarmaDTO(
        id = usuario.id,
        biblioKarma = usuario.biblioKarma
    )

    fun registerRequestDTOtoUsuario(register: RegisterRequestDTO): Usuario = Usuario(
        nombre = register.nombre,
        apellido = register.apellido,
        descripcion = "",
        ciudad = register.ciudad,
        tipoUsuario = register.tipoDeUsuario,
        urlImagen = "",
        email = register.email,
        celular = register.celular
    ).apply {
        passwordSesion = register.password
    }

    fun toTipoDTOUsuario(tipo: String): TipoDTOUsuario = TipoDTOUsuario.valueOf(tipo.uppercase())
}
