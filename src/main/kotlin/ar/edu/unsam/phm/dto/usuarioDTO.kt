package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario

data class UsuarioHeaderDTO(
    val id: Int,
    val urlImagen: String,
    val karma: Int
)

data class UsuarioEdicionDTO(
    val nombre: String,
    val apellido: String,
    val descripcion: String,
    val ciudad: String,
    val tipoUsuario: TipoUsuario,
    val urlImagen: String
)

data class UsarioPerfilDTO(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val descripcion: String,
    val ciudad: String,
    val tipoUsuario: MutableSet<TipoUsuario> = mutableSetOf<TipoUsuario>(),
    val urlImagen: String,
    val libros: List<Libro>)