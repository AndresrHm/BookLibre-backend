package ar.edu.unsam.phm.dto

data class LoginRequestDTO(
    val id: Int = 0,
    val username: String,
    val password: String
)

