package ar.edu.unsam.phm.security

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.service.UsuarioService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.net.http.HttpResponse

@Component
class JWTAuthorizationFilter : OncePerRequestFilter(
) {

    @Autowired
    lateinit var tokenUtils: TokenUtils

    @Autowired
    lateinit var usuarioService: UsuarioService

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = request.cookies?.find { it.name == "access_token" }?.value

            if (token != null) {
                val usernamePAT = tokenUtils.getAuthentication(token)
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
        }
    }
}