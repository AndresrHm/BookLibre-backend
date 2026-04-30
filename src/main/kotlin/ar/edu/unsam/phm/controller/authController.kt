package ar.edu.unsam.phm.controller

import ActionResponse
import ar.edu.unsam.phm.dto.LoginRequestDTO
import ar.edu.unsam.phm.dto.LoginResponseDTO
import ar.edu.unsam.phm.dto.RegisterRequestDTO
import ar.edu.unsam.phm.dto.TokenResponseDTO
import ar.edu.unsam.phm.mapper.UsuarioMapper
import ar.edu.unsam.phm.service.AuthService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val usuarioMapper: UsuarioMapper
) {
    companion object {
        private const val ACCESS_TOKEN_COOKIE = "access_token"
        private const val REFRESH_TOKEN_COOKIE = "refreshToken"
    }

    private val accessTokenMaxAge = Duration.ofSeconds(60)
    private val refreshTokenMaxAge = Duration.ofDays(1)

    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequestDTO,
        response: HttpServletResponse
    ): ResponseEntity<LoginResponseDTO> {
        val usuario = authService.findUsuario(
            loginRequest.username,
            loginRequest.password
        )

        val tokens = authService.generateTokens(usuario)
        val accessCookie = buildCookie(ACCESS_TOKEN_COOKIE, tokens.accessToken, accessTokenMaxAge)
        val refreshCookie = buildCookie(REFRESH_TOKEN_COOKIE, tokens.refreshToken, refreshTokenMaxAge)

        val loginResponse = LoginResponseDTO(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            expiresIn = tokens.expiresIn,
            userId = usuario.id,
            email = usuario.email,
            nombre = usuario.nombre
        )

        response.addCookies(accessCookie, refreshCookie)
        return ResponseEntity.ok(loginResponse)
    }

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody registerRequest: RegisterRequestDTO
    ): ResponseEntity<ActionResponse> {
        authService.saveUsuario(usuarioMapper.fromRegisterRequestDTO(registerRequest))

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ActionResponse("Usuario registrado con éxito"))
    }

    @PostMapping("/logout")
    fun logout(
        @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) refreshToken: String?,
        response: HttpServletResponse
    ): ResponseEntity<ActionResponse> {
        try {
            refreshToken?.let { authService.revokeRefreshToken(it) }
        } catch (_: Exception) {
        }

        val accessCookie = buildCookie(ACCESS_TOKEN_COOKIE, "", Duration.ofSeconds(0))
        val refreshCookie = buildCookie(REFRESH_TOKEN_COOKIE, "", Duration.ofSeconds(0))

        response.addCookies(accessCookie, refreshCookie)
        return ResponseEntity.ok(ActionResponse("Logout realizado con éxito"))
    }

    @PostMapping("/refresh")
    fun refresh(
        @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) refreshToken: String?,
        response: HttpServletResponse
    ): ResponseEntity<TokenResponseDTO> {

        val tokenDesdeCookie = refreshToken
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val tokens = authService.refreshAccessToken(tokenDesdeCookie)

        val accessCookie = buildCookie(ACCESS_TOKEN_COOKIE, tokens.accessToken, accessTokenMaxAge)
        val refreshCookie = buildCookie(REFRESH_TOKEN_COOKIE, tokens.refreshToken, refreshTokenMaxAge)

        response.addCookies(accessCookie, refreshCookie)
        return ResponseEntity.ok(tokens)
    }

    private fun buildCookie(name: String, value: String, maxAge: Duration): ResponseCookie =
        ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(maxAge)
            .sameSite("Strict")
            .build()

    // Para agregar las cookies al header sin ensuciar tanto
    private fun HttpServletResponse.addCookies(vararg cookies: ResponseCookie) {
        cookies.forEach {
            this.addHeader(HttpHeaders.SET_COOKIE, it.toString())
        }
    }
}