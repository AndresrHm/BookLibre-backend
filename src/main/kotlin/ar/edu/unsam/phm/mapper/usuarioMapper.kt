package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.RegisterRequestDTO
import ar.edu.unsam.phm.dto.UsarioPerfilDTO
import ar.edu.unsam.phm.dto.UsuarioEdicionDTO
import ar.edu.unsam.phm.dto.UsuarioHeaderDTO
import ar.edu.unsam.phm.dto.UsuarioKarmaDTO
import ar.edu.unsam.phm.repository.ReservaRepository
import org.springframework.stereotype.Component

@Component
class UsuarioMapper(
    private val reservaRepository: ReservaRepository
) {
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
        cantidadDeLibrosLeidos = usuario.getCantidadDeLibrosLeidos()
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
        id = 0,
        nombre = register.nombre,
        apellido = register.apellido,
        descripcion = "",
        ciudad = register.ciudad,
        tipoUsuario = register.tipoDeUsuario,
        urlImagen = "",
        email = register.email,
        celular = register.celular,
    )
}
