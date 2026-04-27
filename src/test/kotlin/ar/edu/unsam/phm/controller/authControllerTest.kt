package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Rol
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.repository.CalificacionRepository
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.RepoRefreshToken
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.RolRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest(@Autowired val mockMvc: MockMvc) {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var rolRepository: RolRepository

    @Autowired
    private lateinit var refreshTokenRepository: RepoRefreshToken

    @Autowired
    private lateinit var reservaRepository: ReservaRepository

    @Autowired
    private lateinit var libroRepository: LibroRepository

    @Autowired
    private lateinit var calificacionRepository: CalificacionRepository

    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun init() {
        // Limpiamos en orden para evitar conflictos de FK con datos de otros tests.
        calificacionRepository.deleteAll()
        reservaRepository.deleteAll()
        libroRepository.deleteAll()
        refreshTokenRepository.deleteAll()
        usuarioRepository.deleteAll()
        rolRepository.deleteAll()

        val rolLector = Rol().apply { name = TipoUsuario.LECTOR.roleName }
        rolRepository.save(rolLector)

        val usuario = Usuario(
            nombre = "usuarioLogin",
            apellido = "test",
            descripcion = "usuario para auth test",
            email = "usuario.login@test.com",
            celular = "1111111111",
            ciudad = "Buenos Aires",
            roles = mutableSetOf(rolLector),
            password = "secreto123"
        )
        usuario.encriptarPassword()
        usuarioRepository.save(usuario)
    }

    @Test
    fun `login exitoso retorna body con tokens y setea cookies`() {
        val loginBody = """
            {
              "username": "usuario.login@test.com",
              "password": "secreto123"
            }
        """.trimIndent()

        val result = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val responseJson = objectMapper.readTree(result.response.contentAsString)
        assertTrue(responseJson.get("accessToken").asText().isNotBlank())
        assertTrue(responseJson.get("refreshToken").asText().isNotBlank())
        assertTrue(responseJson.get("expiresIn").asLong() > 0)

        val setCookieHeaders = result.response.getHeaders("Set-Cookie")
        assertTrue(setCookieHeaders.any { it.startsWith("access_token=") })
        assertTrue(setCookieHeaders.any { it.startsWith("refreshToken=") })
    }

    @Test
    fun `refresh con cookie valida rota tokens y devuelve 200`() {
        val loginBody = """
            {
              "username": "usuario.login@test.com",
              "password": "secreto123"
            }
        """.trimIndent()

        val loginResult = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val loginJson = objectMapper.readTree(loginResult.response.contentAsString)
        val oldRefreshToken = loginJson.get("refreshToken").asText()

        val refreshResult = mockMvc.perform(
            post("/auth/refresh")
                .cookie(Cookie("refreshToken", oldRefreshToken))
        )
            .andExpect(status().isOk)
            .andReturn()

        val refreshJson = objectMapper.readTree(refreshResult.response.contentAsString)
        val newRefreshToken = refreshJson.get("refreshToken").asText()

        assertTrue(refreshJson.get("accessToken").asText().isNotBlank())
        assertTrue(newRefreshToken.isNotBlank())
        assertNotEquals(oldRefreshToken, newRefreshToken)

        val setCookieHeaders = refreshResult.response.getHeaders("Set-Cookie")
        assertTrue(setCookieHeaders.any { it.startsWith("access_token=") })
        assertTrue(setCookieHeaders.any { it.startsWith("refreshToken=") })
    }

    @Test
    fun `refresh sin cookie devuelve 401`() {
        mockMvc.perform(post("/auth/refresh"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `logout limpia cookies y revoca refresh token`() {
        val loginBody = """
            {
              "username": "usuario.login@test.com",
              "password": "secreto123"
            }
        """.trimIndent()

        val loginResult = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val loginJson = objectMapper.readTree(loginResult.response.contentAsString)
        val refreshToken = loginJson.get("refreshToken").asText()

        val logoutResult = mockMvc.perform(
            post("/auth/logout")
                .cookie(Cookie("refreshToken", refreshToken))
        )
            .andExpect(status().isOk)
            .andReturn()

        val setCookieHeaders = logoutResult.response.getHeaders("Set-Cookie")
        val accessCookie = setCookieHeaders.firstOrNull { it.startsWith("access_token=") }
        val refreshCookie = setCookieHeaders.firstOrNull { it.startsWith("refreshToken=") }

        assertTrue(accessCookie?.contains("Max-Age=0") == true)
        assertTrue(refreshCookie?.contains("Max-Age=0") == true)

        mockMvc.perform(
            post("/auth/refresh")
                .cookie(Cookie("refreshToken", refreshToken))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `login con password invalida devuelve 400`() {
        val loginBody = """
            {
              "username": "usuario.login@test.com",
              "password": "incorrecta"
            }
        """.trimIndent()

        val result = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody)
        )
            .andExpect(status().isBadRequest)
            .andReturn()

        assertEquals(400, result.response.status)
    }
}
