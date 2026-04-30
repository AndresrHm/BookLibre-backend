package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.domain.RefreshToken
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.RefreshTokenRepository
import ar.edu.unsam.phm.security.TokenUtils
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val tokenUtils: TokenUtils
) : RefreshTokenPort {
    override fun generateAndSaveRefreshToken(username: String): String {
        val rawToken = tokenUtils.generateRefreshToken()

        val tokenHash = tokenUtils.hashToken(rawToken)

        revokeByUsername(username)

        val refreshToken = RefreshToken().apply {
            this.tokenHash = tokenHash
            this.username = username
            this.expirationDate = LocalDateTime.now().plusDays(tokenUtils.getRefreshTokenExpirationDays().toLong())
            this.revoked = false
        }

        refreshTokenRepository.save(refreshToken)

        return rawToken
    }

    override fun validateRefreshToken(token: String): RefreshToken? {
        val tokenHash = tokenUtils.hashToken(token)

        val refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
            .orElse(null) ?: return null

        if (!refreshToken.isValid()) {
            return null
        }

        return refreshToken
    }

    // TODO: Revisar, existen 2 revoke token
    override fun revokeByUsername(username: String) {
        val tokens = refreshTokenRepository.findAllByUsername(username)
        tokens.forEach { it.revoked = true }
        refreshTokenRepository.saveAll(tokens)
    }

    override fun revokeToken(token: String) {
        val tokenHash = tokenUtils.hashToken(token)
        val refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
            .orElse(null)

        if (refreshToken != null) {
            refreshToken.revoked = true
            refreshTokenRepository.save(refreshToken)
        }
    }
}

