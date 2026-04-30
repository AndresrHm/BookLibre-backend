package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.errors.BusinessException
import ar.edu.unsam.phm.domain.Rol
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.RolRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import ar.edu.unsam.phm.security.TokenUtils
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class AuthServiceTest {

    @MockK lateinit var usuarioRepository: UsuarioRepository
    @MockK lateinit var rolRepository: RolRepository
    @MockK lateinit var refreshTokenService: RefreshTokenService
    @MockK lateinit var tokenUtils: TokenUtils

    @InjectMockKs
    lateinit var authService: AuthService

    private lateinit var usuarioMock: Usuario
    private val passwordPlana = "password123"

    @BeforeEach
    fun setUp() {
        usuarioMock = spyk(Usuario(
            nombre = "Enzo", apellido = "Perez", descripcion = "D",
            email = "enzo@river.com", celular = "1", ciudad = "C",
            password = passwordPlana
        ).apply { id = 1L })
    }

    // --- TESTS DE findUsuario (Coverage de ramas de búsqueda) ---

    @Test
    fun `findUsuario encuentra por email correctamente`() {
        every { usuarioRepository.findByEmail("enzo@river.com") } returns usuarioMock
        every { usuarioMock.validarCredenciales(passwordPlana) } returns Unit

        val resultado = authService.findUsuario(" enzo@river.com ", passwordPlana)

        assertEquals(usuarioMock, resultado)
        verify { usuarioMock.validarCredenciales(passwordPlana) }
    }

    @Test
    fun `findUsuario encuentra por nombre si el email falla`() {
        every { usuarioRepository.findByEmail("Enzo") } returns null
        every { usuarioRepository.findFirstByNombre("Enzo") } returns usuarioMock
        every { usuarioMock.validarCredenciales(passwordPlana) } returns Unit

        val resultado = authService.findUsuario("Enzo", passwordPlana)

        assertEquals(usuarioMock, resultado)
    }

    @Test
    fun `findUsuario lanza IllegalArgumentException si no encuentra nada`() {
        every { usuarioRepository.findByEmail(any()) } returns null
        every { usuarioRepository.findFirstByNombre(any()) } returns null

        val ex = assertThrows<IllegalArgumentException> {
            authService.findUsuario("fantasma", "123")
        }
        assertEquals(ErrorStrings.USUARIO_O_CONTRASENIA_INCORRECTOS, ex.message)
    }

    // --- TESTS DE saveUsuario (Coverage de Roles y Encriptación) ---

    @Test
    fun `saveUsuario guarda correctamente encriptando y persistiendo roles`() {
        val rol = Rol().apply { name = TipoUsuario.LECTOR.roleName }
        usuarioMock.roles = mutableSetOf(rol)

        every { rolRepository.findByName(rol.name) } returns rol
        every { usuarioRepository.save(any()) } returns usuarioMock
        every { usuarioMock.encriptarPassword() } returns Unit

        authService.saveUsuario(usuarioMock)

        verify { usuarioMock.encriptarPassword() }
        verify { usuarioRepository.save(usuarioMock) }
    }

    @Test
    fun `saveUsuario lanza BusinessException si no tiene roles validos`() {
        usuarioMock.roles = mutableSetOf() // Sin roles

        assertThrows<BusinessException> {
            authService.saveUsuario(usuarioMock)
        }
    }

    // --- TESTS DE TOKENS ---

    @Test
    fun `generateTokens genera access y refresh token correctamente`() {
        every { tokenUtils.createToken(any(), any()) } returns "access-token"
        every { tokenUtils.accessTokenMinutes } returns 30
        every { refreshTokenService.generateAndSaveRefreshToken(any()) } returns "refresh-token"

        val response = authService.generateTokens(usuarioMock)

        assertEquals("access-token", response.accessToken)
        assertEquals("refresh-token", response.refreshToken)
        assertEquals(1800000L, response.expiresIn) // 30 min en ms
    }

    @Test
    fun `refreshAccessToken genera nuevos tokens si el refresh es valido`() {
        val validTokenMock = mockk<ar.edu.unsam.phm.domain.RefreshToken>()
        every { validTokenMock.username } returns "enzo@river.com"

        every { refreshTokenService.validateRefreshToken("old-refresh") } returns validTokenMock
        every { usuarioRepository.findFirstByNombre("enzo@river.com") } returns null
        every { usuarioRepository.findByEmail("enzo@river.com") } returns usuarioMock

        every { tokenUtils.createToken(any(), any()) } returns "new-access"
        every { tokenUtils.accessTokenMinutes } returns 10
        every { refreshTokenService.generateAndSaveRefreshToken(any()) } returns "new-refresh"

        val response = authService.refreshAccessToken("old-refresh")

        assertEquals("new-access", response.accessToken)
        verify { refreshTokenService.validateRefreshToken("old-refresh") }
    }

    @Test
    fun `refreshAccessToken lanza excepcion si el token es invalido`() {
        every { refreshTokenService.validateRefreshToken(any()) } returns null

        assertThrows<BusinessException> {
            authService.refreshAccessToken("token-trucho")
        }
    }

    @Test
    fun `refreshAccessToken lanza excepcion si el usuario no existe`() {
        val validTokenMock = mockk<ar.edu.unsam.phm.domain.RefreshToken>()
        every { validTokenMock.username } returns "desaparecido@river.com"
        every { refreshTokenService.validateRefreshToken(any()) } returns validTokenMock
        every { usuarioRepository.findFirstByNombre(any()) } returns null
        every { usuarioRepository.findByEmail(any()) } returns null

        assertThrows<BusinessException> {
            authService.refreshAccessToken("valido-pero-sin-user")
        }
    }

    @Test
    fun `revokeRefreshToken llama al service correspondiente`() {
        every { refreshTokenService.revokeToken(any()) } returns Unit

        authService.revokeRefreshToken("token-a-morir")

        verify { refreshTokenService.revokeToken("token-a-morir") }
    }
}