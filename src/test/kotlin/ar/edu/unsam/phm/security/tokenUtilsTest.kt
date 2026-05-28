package ar.edu.unsam.phm.security

import ar.edu.unsam.phm.errors.BusinessException
import ar.edu.unsam.phm.domain.Rol
import ar.edu.unsam.phm.security.config.TokenUtils
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class TokenUtilsTest {

    private lateinit var tokenUtils: TokenUtils

    @BeforeEach
    fun setup() {
        tokenUtils = TokenUtils()
        tokenUtils.secretKey = "my-super-secret-key-my-super-secret-key"
        tokenUtils.accessTokenMinutes = 30
        tokenUtils.refreshTokenDays = 7
    }

    @Test
    fun `should create and parse token correctly`() {
        val roles = mutableSetOf(
            Rol().apply { name = "ROLE_USER" }
        )

        val token = tokenUtils.createToken("123", roles)
        val auth = tokenUtils.getAuthentication(token)

        assertEquals("123", auth.name)
        assertTrue(auth.authorities.any { it.authority == "ROLE_USER" })
    }

    @Test
    fun `should throw when subject is blank`() {
        val token = Jwts.builder()
            .subject("")
            .issuedAt(Date())
            .claim("roles", emptyList<String>())
            .signWith(Keys.hmacShaKeyFor(tokenUtils.secretKey.toByteArray()))
            .compact()

        assertThrows(BusinessException::class.java) {
            tokenUtils.getAuthentication(token)
        }
    }

    @Test
    fun `should handle roles as map`() {
        val roles = listOf(mapOf("roleName" to "ROLE_ADMIN"))

        val token = Jwts.builder()
            .subject("1")
            .claim("roles", roles)
            .signWith(Keys.hmacShaKeyFor(tokenUtils.secretKey.toByteArray()))
            .compact()

        val auth = tokenUtils.getAuthentication(token)

        assertTrue(auth.authorities.any { it.authority == "ROLE_ADMIN" })
    }

    @Test
    fun `should generate refresh token`() {
        val token = tokenUtils.generateRefreshToken()
        assertNotNull(token)
        assertTrue(token.length > 10)
    }

    @Test
    fun `should hash token`() {
        val hash = tokenUtils.hashToken("abc")
        assertNotNull(hash)
    }

    @Test
    fun `should throw when token is expired`() {
        val now = Date()
        val token = Jwts.builder()
            .subject("123")
            .issuedAt(Date(now.time - 100000))
            .expiration(Date(now.time - 50000))
            .claim("roles", emptyList<String>())
            .signWith(Keys.hmacShaKeyFor(tokenUtils.secretKey.toByteArray()))
            .compact()

        assertThrows(BusinessException::class.java) {
            tokenUtils.getAuthentication(token)
        }
    }

    @Test
    fun `should throw when subject is null`() {
        val token = Jwts.builder()
            .issuedAt(Date())
            .claim("roles", emptyList<String>())
            .signWith(Keys.hmacShaKeyFor(tokenUtils.secretKey.toByteArray()))
            .compact()

        assertThrows(BusinessException::class.java) {
            tokenUtils.getAuthentication(token)
        }
    }

    @Test
    fun `should handle roles as plain strings list`() {
        val roles = listOf("ROLE_USER")

        val token = Jwts.builder()
            .subject("123")
            .claim("roles", roles)
            .signWith(Keys.hmacShaKeyFor(tokenUtils.secretKey.toByteArray()))
            .compact()

        val auth = tokenUtils.getAuthentication(token)

        assertTrue(auth.authorities.any { it.authority == "ROLE_USER" })
    }

    @Test
    fun `should handle roles as map with name fallback key`() {
        val roles = listOf(mapOf("name" to "ROLE_GUEST"))

        val token = Jwts.builder()
            .subject("123")
            .claim("roles", roles)
            .signWith(Keys.hmacShaKeyFor(tokenUtils.secretKey.toByteArray()))
            .compact()

        val auth = tokenUtils.getAuthentication(token)

        assertTrue(auth.authorities.any { it.authority == "ROLE_GUEST" })
    }

    @Test
    fun `should skip role when map structure lacks expected fields`() {
        val roles = listOf(mapOf("invalidKey" to "ROLE_NONE"))

        val token = Jwts.builder()
            .subject("123")
            .claim("roles", roles)
            .signWith(Keys.hmacShaKeyFor(tokenUtils.secretKey.toByteArray()))
            .compact()

        val auth = tokenUtils.getAuthentication(token)

        assertTrue(auth.authorities.isEmpty())
    }

    @Test
    fun `should return refresh token expiration days correctly`() {
        assertEquals(7, tokenUtils.getRefreshTokenExpirationDays())
    }

    @Test
    fun `should handle case when roles claim is not a list`() {
        val token = Jwts.builder()
            .subject("123")
            .claim("roles", "NOT_A_LIST")
            .signWith(Keys.hmacShaKeyFor(tokenUtils.secretKey.toByteArray()))
            .compact()

        val auth = tokenUtils.getAuthentication(token)

        assertEquals("123", auth.name)
        assertTrue(auth.authorities.isEmpty())
    }

    @Test
    fun `should handle mixed elements inside roles list`() {
        val roles = listOf(12345)

        val token = Jwts.builder()
            .subject("123")
            .claim("roles", roles)
            .signWith(Keys.hmacShaKeyFor(tokenUtils.secretKey.toByteArray()))
            .compact()

        val auth = tokenUtils.getAuthentication(token)

        assertTrue(auth.authorities.isEmpty())
    }
}