package ar.edu.unsam.phm.service


import ar.edu.unsam.phm.errors.BusinessException
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.TokenResponseDTO
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.RolRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import ar.edu.unsam.phm.security.config.TokenUtils
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.minutes

@Service
class AuthService(
    private val usuarioRepository: UsuarioRepository,
    private val rolRepository: RolRepository,
    private val refreshTokenPort: RefreshTokenPort,
    private val tokenUtils: TokenUtils,
) {
    fun findUsuario(usuarioLogin: String, passwordLogin: String): Usuario {
        val login = usuarioLogin.trim()

        val usuario = usuarioRepository.findByEmail(login)
            ?: usuarioRepository.findFirstByNombre(login)
            ?: throw IllegalArgumentException(ErrorStrings.USUARIO_O_CONTRASENIA_INCORRECTOS)

        usuario.validarCredenciales(passwordLogin)
        return usuario
    }

    fun saveUsuario(usuario: Usuario) {
        val rolesPersistidos = usuario.roles
            .mapNotNull { it.name.takeIf { name -> name.isNotBlank() }?.let(rolRepository::findByName) }
            .toMutableSet()

        if (rolesPersistidos.isEmpty()) {
            throw BusinessException(ErrorStrings.USUARIO_DEBE_TENER_TIPO_USUARIO)
        }

        usuario.roles = rolesPersistidos
        usuario.encriptarPassword()
        usuarioRepository.save(usuario)
    }

    fun generateTokens(usuario: Usuario): TokenResponseDTO {
        // Generar access token
        val accessToken = tokenUtils.createToken(usuario.id.toString(), usuario.roles)
        val accessTokenExpirationMs = tokenUtils.accessTokenMinutes.minutes.inWholeMilliseconds

        // Generar refresh token (se guarda en BD)
        val refreshToken = refreshTokenPort.generateAndSaveRefreshToken(usuario.email)

        return TokenResponseDTO(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = accessTokenExpirationMs
        )
    }

    fun refreshAccessToken(refreshToken: String): TokenResponseDTO {
        // Validar el refresh token
        val validToken = refreshTokenPort.validateRefreshToken(refreshToken)
            ?: throw BusinessException(ErrorStrings.REFRESH_TOKEN_INVALIDO_O_EXPIRADO)

        // Obtener el usuario
        val usuario = usuarioRepository.findFirstByNombre(validToken.username)
            ?: usuarioRepository.findByEmail(validToken.username)
            ?: throw BusinessException(ErrorStrings.USUARIO_NO_ENCONTRADO)

        // Generar nuevos tokens (esto también revoca el anterior y crea uno nuevo)
        return generateTokens(usuario)
    }

    fun revokeRefreshToken(refreshToken: String) {
        refreshTokenPort.revokeToken(refreshToken)
    }
}

