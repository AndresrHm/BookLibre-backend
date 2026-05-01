package ar.edu.unsam.phm.security


import ar.edu.unsam.phm.domain.TipoUsuario
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler
import org.springframework.security.web.csrf.CsrfTokenRequestHandler
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler
import org.springframework.util.StringUtils
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.function.Supplier

@Configuration
@EnableWebSecurity
class BooklibreSecurityConfig(
    var jwtAuthorizationFilter: JWTAuthorizationFilter
) {
    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .cors(Customizer.withDefaults())
            .csrf {
                it.ignoringRequestMatchers("/auth/login", "/auth/register", "/auth/refresh", "/auth/logout")
                it.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                it.csrfTokenRequestHandler(SpaCsrfTokenRequestHandler())
            }
            .authorizeHttpRequests {
                // Queremos que cualquier persona se pueda loguear, registrar y poder mostrar errores
                // Importante: no definirlo solo para el method POST
                // porque CORS requiere que también puedas mandar el OPTIONS para hacer el pre-flight
                it.requestMatchers("/auth/login").permitAll()
                it.requestMatchers("/auth/register").permitAll()
                it.requestMatchers("/auth/refresh").permitAll()
                it.requestMatchers("/auth/logout").permitAll()
                it.requestMatchers("/error").permitAll()
                it.requestMatchers(HttpMethod.OPTIONS).permitAll()

                // Permisos de libros
                it.requestMatchers(HttpMethod.GET, "/libros")
                    .hasAuthority(TipoUsuario.LECTOR.roleName) // Ejemplo, solo lectores pueden ver el home.
                it.requestMatchers(HttpMethod.GET, "/libros/prestadoresDisponibles")
                    .hasAuthority(TipoUsuario.LECTOR.roleName)
                it.requestMatchers(HttpMethod.GET, "/libros/*/*").hasAuthority(TipoUsuario.PUBLICADOR.roleName) // Por la forma en que está hecho, del mismo endpoint se saca libro para editar o para ver. Se deja ver el endpoint y se valida a nivel controller.
                it.requestMatchers(HttpMethod.GET, "/libros/*/*").hasAuthority(TipoUsuario.LECTOR.roleName)
                it.requestMatchers(HttpMethod.PUT, "/libros").hasAuthority(TipoUsuario.PUBLICADOR.roleName)
                it.requestMatchers(HttpMethod.POST, "/libros").hasAuthority(TipoUsuario.PUBLICADOR.roleName)
                it.requestMatchers(HttpMethod.DELETE, "/libros").hasAuthority(TipoUsuario.PUBLICADOR.roleName)

                // Permisos de calificaciones
                it.requestMatchers(HttpMethod.GET, "/calificaciones/*").permitAll()
                it.requestMatchers(HttpMethod.POST, "/calificaciones").hasAuthority(TipoUsuario.LECTOR.roleName)

                // Permisos de reservas
                it.requestMatchers(HttpMethod.GET, "/reservas").authenticated()
                it.requestMatchers(HttpMethod.POST, "/reservas").hasAuthority(TipoUsuario.LECTOR.roleName)

                // Permisos de configuración de usuarioRepository
                it.requestMatchers(HttpMethod.GET, "/usuario/*/*").permitAll()
                it.requestMatchers(HttpMethod.PUT, "/usuario").permitAll()

                    // Default: que se autentique para poder ver información
                    .anyRequest().authenticated()
            }
            //.httpBasic(Customizer.withDefaults()) Lo comento porque da error 500, supuestamente choca con el JWTAuthorizationFilter
            .sessionManagement { configurer ->
                configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            // agregado para JWT
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
            // fin agregado
            .exceptionHandling {
                it.authenticationEntryPoint(
                    AuthenticationEntryPoint { _, response, authException ->
                        response.status = HttpStatus.UNAUTHORIZED.value()

                        // con este header en el front
                        response.setHeader(
                            "WWW-Authenticate",
                            "Bearer error=\"invalid_token\", error_description=\"${authException.message}\""
                        )

                        response.contentType = "application/json"
                        response.writer.write(
                            """{"error":"invalid_token","message":"${authException.message}"}"""
                        )
                    }
                )
            }
            .build()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager {
        return configuration.authenticationManager
    }

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:5173")
                    .allowedHeaders("*")
                    .allowedMethods("POST", "GET", "PUT", "DELETE")
                    .allowCredentials(true)
                    .exposedHeaders("WWW-Authenticate")
            }
        }
    }
}

class SpaCsrfTokenRequestHandler : CsrfTokenRequestHandler {
    private val plain: CsrfTokenRequestHandler = CsrfTokenRequestAttributeHandler()
    private val xor: CsrfTokenRequestHandler = XorCsrfTokenRequestAttributeHandler()

    val logger: Logger = LoggerFactory.getLogger(SpaCsrfTokenRequestHandler::class.java)

    override fun handle(request: HttpServletRequest, response: HttpServletResponse, csrfToken: Supplier<CsrfToken>) {
        /*
         * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection of
         * the CsrfToken when it is rendered in the response body.
         */
        xor.handle(request, response, csrfToken)
        /*
         * Render the token value to a cookie by causing the deferred token to be loaded.
         */
        csrfToken.get()
    }

    override fun resolveCsrfTokenValue(request: HttpServletRequest, csrfToken: CsrfToken): String? {
        logger.info("header name ${csrfToken.headerName}")
        val headerValue = request.getHeader(csrfToken.headerName)
        /*
         * If the request contains a request header, use CsrfTokenRequestAttributeHandler
         * to resolve the CsrfToken. This applies when a single-page application includes
         * the header value automatically, which was obtained via a cookie containing the
         * raw CsrfToken.
         */
        logger.info("header value $headerValue")
        return if (StringUtils.hasText(headerValue)) {
            logger.info("plain $plain")
            plain
        } else {
            /*
             * In all other cases (e.g. if the request contains a request parameter), use
             * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
             * when a server-side rendered form includes the _csrf request parameter as a
             * hidden input.
             */
            logger.info("xor $xor")
            xor
        }.resolveCsrfTokenValue(request, csrfToken)
    }
}