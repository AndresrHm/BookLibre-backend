package ar.edu.unsam.phm.security

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.domain.Rol
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.errors.ErrorStrings
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.security.MessageDigest
import java.util.*
import kotlin.time.Duration.Companion.minutes

@Component
class TokenUtils {
    @Value($$"${security.secret-key}")
    lateinit var secretKey: String

    @Value($$"${security.access-token-minutes}")
    var accessTokenMinutes: Int = 30

    @Value($$"${security.refresh-token-days}")
    var refreshTokenDays: Int = 7

    val logger: Logger = LoggerFactory.getLogger(TokenUtils::class.java)

    fun createToken(usuarioId: String, roles: MutableSet<Rol>): String {
        val longExpirationTime = accessTokenMinutes.minutes.inWholeMilliseconds
        val now = Date()

        // acá construimos el token
        return Jwts.builder()
            .subject(usuarioId)
            .issuedAt(now)
            .expiration(Date(now.time + longExpirationTime))
            .claim("roles", roles)
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .compact()
    }

    fun generateRefreshToken(): String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    fun getRefreshTokenExpirationDays(): Int = refreshTokenDays

    fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest((token + secretKey).toByteArray())
        return Base64.getEncoder().encodeToString(hash)
    }

    fun getAuthentication(token: String): UsernamePasswordAuthenticationToken {
        try {
            val secret = Keys.hmacShaKeyFor(secretKey.toByteArray())
            val claims = Jwts.parser()
                .verifyWith(secret)
                .build()
                // acá se valida el vencimiento del token
                .parseSignedClaims(token)
                .payload

            // Token no tiene usuario
            if (claims.subject == null || claims.subject.isBlank()) {
                throw BusinessException(ErrorStrings.CREDENCIALES_INVALIDAS)
            }

            logger.info("Token decoded, user: " + claims.subject + " - roles: " + claims["roles"])

            val rolesClaim = claims["roles"]
            val authorities = mutableListOf<SimpleGrantedAuthority>()
            if (rolesClaim is List<*>) {
                rolesClaim.forEach { role ->
                    // Si el rol es un String, lo tomamos directo
                    if (role is String) {
                        authorities.add(SimpleGrantedAuthority(role))
                    }
                    // Si el rol es un Mapa (porque es un objeto Rol serializado),
                    // buscamos el campo que contiene el nombre del rol (ej: "name", "roleName", etc.)
                    else if (role is Map<*, *>) {
                        val roleName = role["roleName"]?.toString() ?: role["name"]?.toString()
                        if (roleName != null) {
                            authorities.add(SimpleGrantedAuthority(roleName))
                        }
                    }
                }
            }

            return UsernamePasswordAuthenticationToken(claims.subject, null, authorities)
        } catch (expiredJwtException: ExpiredJwtException) {
            throw BusinessException(ErrorStrings.TOKEN_EXPIRADO)
        }
    }
}