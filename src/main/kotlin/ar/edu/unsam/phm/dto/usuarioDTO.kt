package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.TipoUsuario
import java.time.LocalDateTime

data class UsuarioHeaderDTO(
    val id: Int,
    val urlImagen: String,
    val biblioKarma: Int
)

data class UsuarioKarmaDTO(
    val id: Int,
    val biblioKarma: Int
)

data class UsuarioEdicionDTO(
    val id: Int,
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
    val tipoUsuario: TipoUsuario,
    val urlImagen: String,
    val biblioKarma: Int,
    val celular: String,
    val email: String,
    val fechaCreacion: String,
    val cantidadDePrestamos: Int,
    val cantidadDeLibrosLeidos: Int
)

