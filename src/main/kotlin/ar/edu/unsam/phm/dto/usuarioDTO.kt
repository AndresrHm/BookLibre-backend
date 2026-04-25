package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Rol
import ar.edu.unsam.phm.errors.ErrorStrings
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

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
        val usuario: Set<Rol>
    ): SealedGetUsuarioDTO("TIPOUSUARIO")
}

data class UsuarioHeaderDTO(
    val id: Long?,
    val urlImagen: String,
    val biblioKarma: Int
)

data class UsuarioKarmaDTO(
    val id: Long?,
    val biblioKarma: Int
)

data class UsuarioEdicionDTO(
    @field:NotNull(message = ErrorStrings.USUARIO_ID_REQUERIDO)
    val id: Long?,
    @field:NotBlank(message = ErrorStrings.USUARIO_DEBE_TENER_NOMBRE)
    val nombre: String,
    @field:NotBlank(message = ErrorStrings.USUARIO_DEBE_TENER_APELLIDO)
    val apellido: String,
    @field:NotBlank(message = ErrorStrings.USUARIO_DEBE_TENER_DESCRIPCION)
    val descripcion: String,
    @field:NotBlank(message = ErrorStrings.USUARIO_DEBE_TENER_CIUDAD)
    val ciudad: String,
    @field:NotNull(message = ErrorStrings.USUARIO_DEBE_TENER_TIPO_USUARIO)
    val tipoUsuario: MutableSet<Rol>,
    @field:NotBlank(message = ErrorStrings.USUARIO_DEBE_TENER_URL_IMAGEN)
    val urlImagen: String
)

data class UsarioPerfilDTO(
    val id: Long?,
    val nombre: String,
    val apellido: String,
    val descripcion: String,
    val ciudad: String,
    val tipoUsuario: MutableSet<Rol>,
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
