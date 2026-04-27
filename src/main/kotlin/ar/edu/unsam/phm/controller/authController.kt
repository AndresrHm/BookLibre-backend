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

    /**
     * Endpoint para autenticar un usuario y generar tokens de acceso.
     *
     * Flujo:
     * 1. Valida las credenciales del usuario (username + password)
     * 2. Genera nuevos tokens (access token + refresh token)
     * 3. Establece ambos tokens en cookies HTTPOnly
     * 4. Retorna los tokens y datos del usuario al cliente
     *
     * @param loginRequest Credenciales del usuario (username y password)
     * @param response Objeto de respuesta HTTP para establecer las cookies
     * @return ResponseEntity con tokens y datos del usuario autenticado
     */
    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequestDTO,
        response: HttpServletResponse
    ): ResponseEntity<LoginResponseDTO> {
        // PASO 1: Validar credenciales y obtener usuario autenticado
        val usuario = authService.findUsuario(
            loginRequest.username,
            loginRequest.password
        )

        // PASO 2: Generar tokens JWT (access + refresh) para el usuario autenticado
        val tokens = authService.generateTokens(usuario)

        // PASO 3: Crear cookie para access token
        // - httpOnly(false): Permite acceso desde JavaScript (para testing/debugging)
        // - secure(false): Sin HTTPS aún (cambiar a true en producción)
        // - maxAge: 10 segundos (muy corto para testing)
        val accessCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, tokens.accessToken)
            .httpOnly(false)
            .secure(false)
            .path("/")
            .maxAge(ACCESS_TOKEN_MAX_AGE_SECONDS)
            .sameSite("Lax")
            .build()

        // PASO 4: Crear cookie para refresh token
        // - httpOnly(true): Protección contra XSS
        // - secure(false): Sin HTTPS aún
        // - maxAge: 7 días
        val refreshCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, tokens.refreshToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(REFRESH_TOKEN_MAX_AGE_SECONDS)
            .sameSite("Lax")
            .build()

        // PASO 5: Agregar cookies a la respuesta HTTP
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString())
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())

        // PASO 6: Construir respuesta con tokens y datos del usuario autenticado
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

    /**
     * Endpoint para registrar un nuevo usuario en el sistema.
     *
     * @param registerRequest Datos del usuario a registrar
     * @return ResponseEntity con status 201 Created si el registro es exitoso
     */
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
        // Revocar el refresh token en base de datos para invalidarlo
        // Esto asegura que no pueda ser utilizado nuevamente
        try {
            refreshToken?.let { authService.revokeRefreshToken(it) }
        } catch (_: Exception) {
            // Si falla la revocación, continuamos igual (el logout será completado)
        }

        // Limpiar cookies de sesión estableciendo maxAge(0) para que expiren inmediatamente
        val accessCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, "")
            .httpOnly(false)
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

    /**
     * Endpoint para refrescar el access token utilizando el refresh token almacenado en la cookie.
     *
     * Flujo:
     * 1. Obtiene el refresh token de la cookie (si no existe, retorna 401 Unauthorized)
     * 2. Valida y procesa el refresh token a través del AuthService
     * 3. Genera nuevos tokens (access + refresh) en caso de ser válido
     * 4. Establece las nuevas cookies en la respuesta HTTP
     * 5. Retorna los nuevos tokens al cliente
     *
     * @param refreshToken Token obtenido de la cookie para refrescar el acceso
     * @param response Objeto de respuesta HTTP para establecer las nuevas cookies
     * @return ResponseEntity con los nuevos tokens (access + refresh) y su tiempo de expiración
     */
    @PostMapping("/refresh")
    fun refresh(
        @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) refreshToken: String?,
        response: HttpServletResponse
    ): ResponseEntity<TokenResponseDTO> {
        // PASO 1: Validar que el refresh token esté presente en la cookie
        // Si no existe, retorna 401 Unauthorized para intentar login nuevamente
        val tokenDesdeCookie = refreshToken
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        // PASO 2: Validar y procesar el refresh token a través del servicio
        // El servicio verificará que el token sea válido, no esté expirado y exista en DB
        val tokens = authService.refreshAccessToken(tokenDesdeCookie)

        // PASO 3: Crear cookie para el nuevo access token
        // - httpOnly(false): Permite que JavaScript acceda al token (para testing/debugging)
        // - secure(false): Sin HTTPS aún (cambiar a true en producción con HTTPS)
        // - maxAge: 10 segundos (muy corto para testing, ajustar en producción)
        val accessCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, tokens.accessToken)
            .httpOnly(false)
            .secure(false)
            .path("/")
            .maxAge(ACCESS_TOKEN_MAX_AGE_SECONDS)
            .sameSite("Lax")
            .build()

        // PASO 4: Crear cookie para el nuevo refresh token
        // - httpOnly(true): Protección contra XSS, no accesible desde JavaScript
        // - secure(false): Sin HTTPS aún (cambiar a true en producción con HTTPS)
        // - maxAge: 7 días (tiempo de vida del refresh token)
        val refreshCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, tokens.refreshToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(REFRESH_TOKEN_MAX_AGE_SECONDS)
            .sameSite("Lax")
            .build()

        // PASO 5: Agregar ambas cookies a la respuesta HTTP
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString())
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())

        // PASO 6: Retornar los nuevos tokens y metadatos en el body de la respuesta
        return ResponseEntity.ok(tokens)
    }
}


