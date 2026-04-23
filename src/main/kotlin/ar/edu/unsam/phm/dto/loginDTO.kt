package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Rol

data class LoginRequestDTO(
    val username: String,
    val password: String
)

data class RegisterRequestDTO(
    val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val email: String,
    val celular: String,
    val tipoUsuario: MutableSet<Rol>,
    val ciudad: String,
    val password: String
)

