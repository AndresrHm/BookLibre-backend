package ar.edu.unsam.phm.security.cookies

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie


fun HttpServletResponse.addCookies(vararg cookies: ResponseCookie) {
    cookies.forEach {
        addHeader(HttpHeaders.SET_COOKIE, it.toString())
    }
}