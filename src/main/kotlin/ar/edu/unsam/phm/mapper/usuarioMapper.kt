package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.RegisterRequestDTO
import ar.edu.unsam.phm.dto.TipoDTOUsuario
import ar.edu.unsam.phm.dto.UsarioPerfilDTO
import ar.edu.unsam.phm.dto.UsuarioEdicionDTO
import ar.edu.unsam.phm.dto.UsuarioHeaderDTO
import ar.edu.unsam.phm.dto.UsuarioKarmaDTO
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.stereotype.Component

@Component
class UsuarioMapper(
    private val reservaRepository: ReservaRepository,
    private val usuarioService: UsuarioService
) {
    fun usuarioEdicionDTOtoLibro(usuarioEdicionDTO: UsuarioEdicionDTO): Usuario {
        val usuario = usuarioService.getUsuarioById(usuarioEdicionDTO.id)
        val usuarioNuevo = Usuario(
            nombre = usuarioEdicionDTO.nombre,
            apellido = usuarioEdicionDTO.apellido,
            descripcion = usuarioEdicionDTO.descripcion,
            ciudad = usuarioEdicionDTO.ciudad,
            tipoUsuario = usuarioEdicionDTO.tipoUsuario,
            urlImagen = usuarioEdicionDTO.urlImagen,
            email = usuario.email,
            celular = usuario.celular
        )
        usuarioNuevo.id = usuarioEdicionDTO.id
        usuarioNuevo.biblioKarma = usuario.biblioKarma

        return usuarioNuevo
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
        cantidadDePrestamos = reservaRepository.obtenerCantidadDeReservasDeAlgunDuenio(usuario.id),
        cantidadDeLibrosLeidos = reservaRepository.obtenerCantidadDeLibrosPedidos(usuario.id)
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

    fun toUsuarioKarma(usuario: Usuario): UsuarioKarmaDTO = UsuarioKarmaDTO(
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
        celular = register.celular,

    ).apply {passwordSesion = register.password}

    fun toTipoDTOUsuario(tipo: String) = TipoDTOUsuario.valueOf(tipo.uppercase())
}
