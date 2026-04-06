package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.TipoUsuario

data class LoginRequestDTO(
    val id: Int = 0,
    val username: String,
    val password: String
)

data class RegisterRequestDTO(
    val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val email: String,
    val celular: String,
    val tipoDeUsuario: TipoUsuario,
    val ciudad: String,
    val password: String
)

