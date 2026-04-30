package ar.edu.unsam.phm.security

import ar.edu.unsam.phm.errors.BusinessException
import ar.edu.unsam.phm.service.UsuarioService
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class JWTAuthorizationFilterTest {

    private lateinit var tokenUtils: TokenUtils
    private lateinit var usuarioService: UsuarioService
    private lateinit var filter: JWTAuthorizationFilter
    private lateinit var filterChain: FilterChain

    @BeforeEach
    fun setup() {
        tokenUtils = mock(TokenUtils::class.java)
        usuarioService = mock(UsuarioService::class.java)
        filterChain = mock(FilterChain::class.java)

        filter = JWTAuthorizationFilter(tokenUtils, usuarioService)
    }

    @Test
    fun `should skip public endpoints`() {
        val request = MockHttpServletRequest()
        request.servletPath = "/auth/login"

        val response = MockHttpServletResponse()

        filter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun `should continue when no token`() {
        val request = MockHttpServletRequest()
        request.servletPath = "/private"

        val response = MockHttpServletResponse()

        filter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun `should authenticate when token is valid`() {
        val request = MockHttpServletRequest().apply {
            servletPath = "/private"
        }
        request.setCookies(Cookie("access_token", "valid"))

        val response = MockHttpServletResponse()

        val auth = UsernamePasswordAuthenticationToken("1", null)

        `when`(tokenUtils.getAuthentication("valid")).thenReturn(auth)

        filter.doFilter(request, response, filterChain)

        verify(usuarioService).validarExistenciaUsuario(1)
        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun `should return 401 on BusinessException`() {
        val request = MockHttpServletRequest().apply {
            servletPath = "/private"
        }
        request.setCookies(Cookie("access_token", "valid"))

        val response = MockHttpServletResponse()

        `when`(tokenUtils.getAuthentication("expired"))
            .thenThrow(BusinessException("TOKEN_EXPIRADO"))

        filter.doFilter(request, response, filterChain)

        assertEquals(401, response.status)
    }

    @Test
    fun `should return 401 on JwtException`() {
        val request = MockHttpServletRequest().apply {
            servletPath = "/private"
        }
        request.setCookies(Cookie("access_token", "valid"))

        val response = MockHttpServletResponse()

        `when`(tokenUtils.getAuthentication("bad"))
            .thenThrow(JwtException("invalid"))

        filter.doFilter(request, response, filterChain)

        assertEquals(401, response.status)
    }

    @Test
    fun `should return 401 on unexpected exception`() {
        val request = MockHttpServletRequest().apply {
            servletPath = "/private"
            setCookies(Cookie("access_token", "boom"))
        }

        val response = MockHttpServletResponse()

        `when`(tokenUtils.getAuthentication("boom"))
            .thenThrow(RuntimeException("unexpected"))

        filter.doFilter(request, response, filterChain)

        assertEquals(401, response.status)
    }
}