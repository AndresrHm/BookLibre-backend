package ar.edu.unsam.phm.controller

import ActionResponse
import ar.edu.unsam.phm.dto.LoginRequestDTO
import ar.edu.unsam.phm.dto.LoginResponseDTO
import ar.edu.unsam.phm.dto.RefreshTokenRequestDTO
import ar.edu.unsam.phm.dto.RegisterRequestDTO
import ar.edu.unsam.phm.dto.TokenResponseDTO
import ar.edu.unsam.phm.mapper.UsuarioMapper
import ar.edu.unsam.phm.security.TokenUtils
import ar.edu.unsam.phm.service.AuthService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val tokenUtils: TokenUtils,
    private val usuarioMapper: UsuarioMapper
) {

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
        val cookie = ResponseCookie.from("access_token", tokens.accessToken)
            .httpOnly(false) // lo dejo en false para que lo podamos ver
            .secure(false) // todavía no tenemos https
            .path("/")
            .maxAge(300) // 5 min (coincide con access token)
            .sameSite("Lax")
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())

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
        @RequestBody body: RefreshTokenRequestDTO,
        response: HttpServletResponse
    ): ResponseEntity<ActionResponse> {
        // Revocar el refresh token
        try {
            authService.revokeRefreshToken(body.refreshToken)
        } catch (e: Exception) {
            // Si falla la revocación, continuamos igual
        }

        // Limpiar cookie de access token
        val cookie = ResponseCookie.from("access_token", "")
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
        return ResponseEntity.ok(ActionResponse("Logout realizado con éxito"))
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody body: RefreshTokenRequestDTO): ResponseEntity<TokenResponseDTO> {
        val tokens = authService.refreshAccessToken(body.refreshToken)
        return ResponseEntity.ok(tokens)
    }
}


