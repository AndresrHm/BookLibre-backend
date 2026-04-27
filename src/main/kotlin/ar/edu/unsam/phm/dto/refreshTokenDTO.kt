package ar.edu.unsam.phm.dto

data class TokenResponseDTO(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long // en milisegundos
)

data class LoginResponseDTO(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val userId: Long?,
    val email: String,
    val nombre: String
)
