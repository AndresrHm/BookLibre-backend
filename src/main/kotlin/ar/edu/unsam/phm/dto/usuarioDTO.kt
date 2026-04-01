package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.TipoUsuario

sealed class SealedGetUsuarioDTO(val tipo: String) {
    data class Header(
        val usuario: UsuarioHeaderDTO
    ) : SealedGetUsuarioDTO("HEADER")
    data class Karma(
        val usuario: UsuarioKarmaDTO
    ): SealedGetUsuarioDTO("KARMA")
    data class Update(
        val usuario: UsuarioEdicionDTO
    ): SealedGetUsuarioDTO("UPDATE")
    data class Perfil(
        val usuario: UsarioPerfilDTO
    ): SealedGetUsuarioDTO("PERFIL")
    data class TipoPerfil(
        val usuario: TipoUsuario
    ): SealedGetUsuarioDTO("TIPOUSUARIO")
}

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

enum class TipoDTOUsuario() {
    HEADER,
    KARMA,
    UPDATE,
    PERFIL,
    TIPOUSUARIO
}
