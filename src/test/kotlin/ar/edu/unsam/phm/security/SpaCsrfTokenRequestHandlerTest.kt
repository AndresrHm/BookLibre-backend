package ar.edu.unsam.phm.security

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.web.csrf.DefaultCsrfToken
import java.util.function.Supplier

class SpaCsrfTokenRequestHandlerTest {

    private val handler = SpaCsrfTokenRequestHandler()

    @Test
    fun `should use plain handler when header is present`() {
        val request = MockHttpServletRequest()
        request.addHeader("X-CSRF-TOKEN", "token123")

        val csrfToken: CsrfToken =
            DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "token123")

        val result = handler.resolveCsrfTokenValue(request, csrfToken)

        assertEquals("token123", result)
    }

    @Test
    fun `should return null when no csrf token provided`() {
        val request = MockHttpServletRequest()

        val csrfToken: CsrfToken =
            DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "token123")

        val result = handler.resolveCsrfTokenValue(request, csrfToken)

        assertNull(result)
    }

    @Test
    fun `handle should not crash`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        val supplier = Supplier<CsrfToken> {
            DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "token123")
        }

        handler.handle(request, response, supplier)
    }
}