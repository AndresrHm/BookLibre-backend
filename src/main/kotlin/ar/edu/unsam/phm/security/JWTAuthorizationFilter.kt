package ar.edu.unsam.phm.security

import ar.edu.unsam.phm.errors.BusinessException
import ar.edu.unsam.phm.service.UsuarioService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import io.jsonwebtoken.JwtException

@Component
class JWTAuthorizationFilter : OncePerRequestFilter(
) {

    // ver si podemos mejorar lo del autowired
    @Autowired
    lateinit var tokenUtils: TokenUtils

    @Autowired
    lateinit var usuarioService: UsuarioService

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val publicAuthEndpoints = setOf("/auth/login", "/auth/register", "/auth/refresh", "/auth/logout")

        if (request.servletPath in publicAuthEndpoints) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val token = request.cookies?.find { it.name == "access_token" }?.value

            if (token != null) {
                val authentication = tokenUtils.getAuthentication(token)
                usuarioService.validarExistenciaUsuario(authentication.name.toLong())

                SecurityContextHolder.getContext().authentication = authentication
            }

            filterChain.doFilter(request, response)

        } catch (e: JwtException) {
            logger.warn("JWT error: ${e.message}")
            unauthorized(response, "Invalid or expired token")
            return

        } catch (e: BusinessException) {
            logger.warn("Business error: ${e.message}")
            unauthorized(response, e.message ?: "Token expired")
            return

        } catch (e: Exception) {
            logger.error("Unexpected error in JWT filter", e)
            unauthorized(response, "Authentication error")
            return
        }
    }
}

private fun unauthorized(
    response: HttpServletResponse,
    description: String
) {
    response.status = HttpServletResponse.SC_UNAUTHORIZED

    response.setHeader(
        "WWW-Authenticate",
        "Bearer error=\"invalid_token\", error_description=\"$description\""
    )

    response.contentType = "application/json"
    response.writer.write(
        """{"error":"invalid_token","message":"$description"}"""
    )
}