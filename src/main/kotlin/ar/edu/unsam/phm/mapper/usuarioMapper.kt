package ar.edu.unsam.phm.mapper


import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.UsarioPerfilDTO
import ar.edu.unsam.phm.dto.UsuarioHeaderDTO

object UsuarioMapper {
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
        fechaCreacion = usuario.getFechaCreacionAsString()
    )

    fun toUsuarioHeaderDTO(usuario: Usuario): UsuarioHeaderDTO = UsuarioHeaderDTO(
        id = usuario.id,
        urlImagen = usuario.urlImagen,
        biblioKarma = usuario.biblioKarma
    )
}
