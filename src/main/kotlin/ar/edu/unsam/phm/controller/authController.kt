package ar.edu.unsam.phm.controller

import ActionResponse
import ar.edu.unsam.phm.dto.LoginRequestDTO
import ar.edu.unsam.phm.dto.RegisterRequestDTO
import ar.edu.unsam.phm.mapper.UsuarioMapper
import ar.edu.unsam.phm.security.TokenUtils
import ar.edu.unsam.phm.service.AuthService
import jakarta.servlet.http.HttpServletResponse
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
    ): ResponseEntity<Map<String, String>> {
        val usuario = authService.findUsuario(
            loginRequest.username,
            loginRequest.password
        )

        val token = tokenUtils.createToken(usuario.id.toString(), usuario.roles)

        val cookie = ResponseCookie.from("access_token", token)
            .httpOnly(false) // lo dejo en false para que lo podamos ver
            .secure(false) // todavía no tenemos https
            .path("/")
            .maxAge(300) // 5 min
            .sameSite("Lax")
            .build()

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(mapOf("message" to "Usuario logueado con éxito"))
    }

    @PostMapping("/register")
    fun register(
        @RequestBody registerRequest: RegisterRequestDTO
    ): ResponseEntity<ActionResponse> {
        authService.saveUsuario(usuarioMapper.registerRequestDTOtoUsuario(registerRequest))

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ActionResponse("Usuario registrado con éxito"))
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Void> {
        val cookie = ResponseCookie.from("access_token", "")
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
        return ResponseEntity.ok().build()
    }
}
