package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Rol
import ar.edu.unsam.phm.errors.ErrorStrings
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class LoginRequestDTO(
    val username: String,
    val password: String
)

data class RegisterRequestDTO(
    @field:NotNull(message = ErrorStrings.USUARIO_ID_REQUERIDO)
    val id: Int = 0,
    @field:NotBlank(message = ErrorStrings.USUARIO_DEBE_TENER_NOMBRE)
    val nombre: String,
    @field:NotBlank(message = ErrorStrings.USUARIO_DEBE_TENER_APELLIDO)
    val apellido: String,
    @field:NotBlank(message = ErrorStrings.USUARIO_DEBE_TENER_MAIL)
    val email: String,
    @field:NotBlank(message = ErrorStrings.USUARIO_DEBE_TENER_CELULAR)
    val celular: String,
    @field:NotNull(message = ErrorStrings.USUARIO_DEBE_TENER_TIPO_USUARIO)
    val tipoUsuario: MutableSet<Rol>,
    @field:NotBlank(message = ErrorStrings.USUARIO_DEBE_TENER_CIUDAD)
    val ciudad: String,
    @field:NotBlank(message = ErrorStrings.USUARIO_DEBE_TENER_CONTRASNIA)
    val password: String
)

