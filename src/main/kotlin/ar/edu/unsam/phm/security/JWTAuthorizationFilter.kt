package ar.edu.unsam.phm.security

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.service.UsuarioService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import io.jsonwebtoken.JwtException

@Component
class JWTAuthorizationFilter(
    var tokenUtils: TokenUtils,
    var usuarioService: UsuarioService
) : OncePerRequestFilter(
) {
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
                val usernamePAT = tokenUtils.getAuthentication(token)
                usuarioService.validarExistenciaUsuario(usernamePAT.name.toLong()) // revisar
                SecurityContextHolder.getContext().authentication = usernamePAT
            }
            filterChain.doFilter(request, response)
        } catch (e: BusinessException) {
            // Captura la excepción de token expirado y devuelve el status code adecuado (401-Unauthorized)
            logger.warn(e.message)
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.setHeader("WWW-Authenticate", "Bearer error=\"invalid_token\", error_description=\"The access token expired\"")
            response.contentType = "application/json"
            response.writer.write("{\"error\":\"Token expired\",\"message\":\"${e.message}\"}")

            // Importante: NO llamar a filterChain.doFilter() después de manejar el error
            return
        } catch (e: JwtException) {
            // Captura cualquier excepción de JWT (token inválido, expirado, etc.)
            logger.warn("JWT Exception: ${e.message}")
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.setHeader("WWW-Authenticate", "Bearer error=\"invalid_token\", error_description=\"Invalid or expired token\"")
            response.contentType = "application/json"
            response.writer.write("{\"error\":\"Invalid token\",\"message\":\"${e.message}\"}")
            return
        } catch (e: Exception) {
            // Captura cualquier otra excepción inesperada
            logger.error("Unexpected error in JWT filter: ${e.message}", e)
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            response.writer.write("{\"error\":\"Authentication error\",\"message\":\"An error occurred during authentication\"}")
            return
        }
    }
}