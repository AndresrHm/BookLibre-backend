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
        libros = usuario.listaDeLibrosParaPrestar.map { libroParaPrestar ->
            val libroPerfil = libroParaPrestar.toLibroPerfilUsuarioDTO()
            libroPerfil
        }.toMutableList()
    )

    fun toUsuarioHeaderDTO(usuario: Usuario): UsuarioHeaderDTO = UsuarioHeaderDTO(
        id = usuario.id,
        urlImagen = usuario.urlImagen,
        karma = usuario.biblioKarma
    )
}
