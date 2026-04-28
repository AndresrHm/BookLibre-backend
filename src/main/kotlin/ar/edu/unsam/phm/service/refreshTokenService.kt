package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.domain.RefreshToken
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.RepoRefreshToken
import ar.edu.unsam.phm.security.TokenUtils
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RepoRefreshToken,
    private val tokenUtils: TokenUtils
) {
    /**
     * Genera y guarda un nuevo refresh token para un usuario
     * @param username El nombre/email del usuario
     * @return El refresh token en texto plano (el cliente lo almacena)
     */
    fun generateAndSaveRefreshToken(username: String): String {
        // Generar token aleatorio
        val rawToken = tokenUtils.generateRefreshToken()

        // Hashear el token antes de guardarlo en BD
        val tokenHash = tokenUtils.hashToken(rawToken)

        // Revocar token anterior si existe (para evitar múltiples tokens activos)
        revokeByUsername(username)

        // Crear entidad y guardar
        val refreshToken = RefreshToken().apply {
            this.tokenHash = tokenHash
            this.username = username
            this.expirationDate = LocalDateTime.now().plusDays(tokenUtils.getRefreshTokenExpirationDays().toLong())
            this.revoked = false
        }

        refreshTokenRepository.save(refreshToken)

        // Retornar el token en texto plano (para que el cliente lo guarde)
        return rawToken
    }

    /**
     * Valida un refresh token
     * @param token El refresh token en texto plano
     * @return La entidad RefreshToken si es válido, null si no existe o está revocado
     */
    fun validateRefreshToken(token: String): RefreshToken? {
        // Hashear el token recibido
        val tokenHash = tokenUtils.hashToken(token)

        // Buscar en BD
        val refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
            .orElse(null) ?: return null

        // Validar que no esté revocado y no esté expirado
        if (!refreshToken.isValid()) {
            return null
        }

        return refreshToken
    }

    /**
     * Revoca todos los refresh tokens de un usuario (útil para logout)
     * @param username El nombre/email del usuario
     */
    fun revokeByUsername(username: String) {
        val tokens = refreshTokenRepository.findAllByUsername(username)
        tokens.forEach { it.revoked = true }
        refreshTokenRepository.saveAll(tokens)
    }

    /**
     * Revoca un token específico
     * @param token El refresh token en texto plano
     */
    fun revokeToken(token: String) {
        val tokenHash = tokenUtils.hashToken(token)
        val refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
            .orElse(null)

        if (refreshToken != null) {
            refreshToken.revoked = true
            refreshTokenRepository.save(refreshToken)
        }
    }
}

