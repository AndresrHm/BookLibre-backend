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

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val usuarioMapper: UsuarioMapper
) {

    companion object {
        private const val ACCESS_TOKEN_COOKIE = "access_token"
        private const val REFRESH_TOKEN_COOKIE = "refreshToken"
        private const val ACCESS_TOKEN_MAX_AGE_SECONDS = 10L // 10 segundos para testing
        private const val REFRESH_TOKEN_MAX_AGE_SECONDS = 60L * 60 * 24 * 7
    }

    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequestDTO,
        response: HttpServletResponse
    ): ResponseEntity<LoginResponseDTO> {
        // Autenticar usuario
        val usuario = authService.findUsuario(
            loginRequest.username,
            loginRequest.password
        )

        // Generar tokens (access + refresh)
        val tokens = authService.generateTokens(usuario)

        // Guardar access token en cookie
        val accessCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, tokens.accessToken)
            .httpOnly(false) // lo dejo en false para que lo podamos ver
            .secure(false) // todavía no tenemos https
            .path("/")
            .maxAge(ACCESS_TOKEN_MAX_AGE_SECONDS)
            .sameSite("Lax")
            .build()

        val refreshCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, tokens.refreshToken)
            .httpOnly(true)
            .secure(false) // todavía no tenemos https
            .path("/")
            .maxAge(REFRESH_TOKEN_MAX_AGE_SECONDS)
            .sameSite("Lax")
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString())
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())

        // Retornar respuesta con tokens y datos del usuario
        val loginResponse = LoginResponseDTO(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            expiresIn = tokens.expiresIn,
            userId = usuario.id,
            email = usuario.email,
            nombre = usuario.nombre
        )

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
        // Revocar el refresh token
        try {
            refreshToken?.let { authService.revokeRefreshToken(it) }
        } catch (e: Exception) {
            // Si falla la revocación, continuamos igual
        }

        // Limpiar cookies de sesión
        val accessCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, "")
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .build()

        val refreshCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString())
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())
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

        val accessCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, tokens.accessToken)
            .httpOnly(false)
            .secure(false)
            .path("/")
            .maxAge(ACCESS_TOKEN_MAX_AGE_SECONDS)
            .sameSite("Lax")
            .build()

        val refreshCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, tokens.refreshToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(REFRESH_TOKEN_MAX_AGE_SECONDS)
            .sameSite("Lax")
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString())
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())

        return ResponseEntity.ok(tokens)
    }
}


