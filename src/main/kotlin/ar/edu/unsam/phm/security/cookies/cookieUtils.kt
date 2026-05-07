package ar.edu.unsam.phm.security.cookies

import java.time.Duration
import org.springframework.http.ResponseCookie

object CookieUtils {

    fun buildCookie(
        name: String,
        value: String,
        maxAge: Duration
    ): ResponseCookie =
        ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .maxAge(maxAge)
            .sameSite("Strict")
            .build()
}