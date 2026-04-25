package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.*
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.stereotype.Component

@Component
class UsuarioMapper(
    private val usuarioService: UsuarioService
) {

    fun fromEdicionDTO(usuarioEdicionDTO: UsuarioEdicionDTO, usuarioExistente: Usuario): Usuario {
        return Usuario(
            nombre = usuarioEdicionDTO.nombre,
            apellido = usuarioEdicionDTO.apellido,
            descripcion = usuarioEdicionDTO.descripcion,
            ciudad = usuarioEdicionDTO.ciudad,
            roles = usuarioEdicionDTO.tipoUsuario,
            urlImagen = usuarioEdicionDTO.urlImagen,
            email = usuarioExistente.email,
            celular = usuarioExistente.celular,
            password = usuarioExistente.password
        ).apply {
            id = usuarioExistente.id
            biblioKarma = usuarioExistente.biblioKarma
        }
    }

    fun toPerfilDTO(usuario: Usuario): UsarioPerfilDTO = UsarioPerfilDTO(
        id = usuario.id,
        nombre = usuario.nombre,
        apellido = usuario.apellido,
        descripcion = usuario.descripcion,
        ciudad = usuario.ciudad,
        tipoUsuario = usuario.roles,
        urlImagen = usuario.urlImagen,
        email = usuario.email,
        celular = usuario.celular,
        biblioKarma = usuario.biblioKarma,
        fechaCreacion = usuario.getFechaCreacionAsString(),
        cantidadDePrestamos = usuarioService.obtenerCantidadDeLibrosPrestados(usuario.id!!),
        cantidadDeLibrosLeidos = usuarioService.obtenerCantidadDeLibrosLeidos(usuario.id!!)
    )

    fun toHeaderDTO(usuario: Usuario): UsuarioHeaderDTO = UsuarioHeaderDTO(
        id = usuario.id,
        urlImagen = usuario.urlImagen,
        biblioKarma = usuario.biblioKarma
    )

    fun toEdicionDTO(usuario: Usuario): UsuarioEdicionDTO = UsuarioEdicionDTO(
        nombre = usuario.nombre,
        apellido = usuario.apellido,
        descripcion = usuario.descripcion,
        ciudad = usuario.ciudad,
        tipoUsuario = usuario.roles,
        urlImagen = usuario.urlImagen
    )

    fun toKarmaDTO(usuario: Usuario): UsuarioKarmaDTO = UsuarioKarmaDTO(
        id = usuario.id,
        biblioKarma = usuario.biblioKarma
    )

    fun fromRegisterRequestDTO(register: RegisterRequestDTO): Usuario = Usuario(
        nombre = register.nombre,
        apellido = register.apellido,
        descripcion = "",
        ciudad = register.ciudad,
        roles = register.tipoUsuario,
        email = register.email,
        celular = register.celular,
        password = register.password
    )

    fun toTipoDTO(tipo: String): TipoDTOUsuario = TipoDTOUsuario.valueOf(tipo.uppercase())
}
