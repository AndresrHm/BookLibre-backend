package ar.edu.unsam.phm.exception

import ar.edu.unsam.phm.errors.BusinessException
import ar.edu.unsam.phm.errors.GlobalExceptionHandler
import ar.edu.unsam.phm.errors.NotFoundException
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

class GlobalExceptionHandlerTest {
    @RestController
    class MockController {
        @GetMapping("/business")
        fun triggerBusiness() { throw BusinessException("Error de negocio testeado") }

        @GetMapping("/notfound")
        fun triggerNotFound() { throw NotFoundException("No lo encontre") }

        @GetMapping("/general")
        fun triggerGeneral() { throw RuntimeException("Exploto todo") }

        @GetMapping("/null-message")
        fun triggerNullMessage() { throw Exception() }
    }

    private val mockMvc = MockMvcBuilders
        .standaloneSetup(MockController())
        .setControllerAdvice(GlobalExceptionHandler())
        .build()

    @Test
    fun `BusinessException devuelve 400 y el mensaje correcto`() {
        mockMvc.perform(get("/business"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Error de negocio testeado"))
    }

    @Test
    fun `NotFoundException devuelve 404 y el mensaje correcto`() {
        mockMvc.perform(get("/notfound"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("No lo encontre"))
    }

    @Test
    fun `Exception general devuelve 400 y mensaje de error`() {
        mockMvc.perform(get("/general"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Exploto todo"))
    }

    @Test
    fun `Cuando la excepcion no tiene mensaje devuelve el texto por defecto`() {
        mockMvc.perform(get("/null-message"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("No se pudo conectar con el servidor."))
    }
}