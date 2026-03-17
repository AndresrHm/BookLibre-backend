package ar.edu.unsam.phm.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.AnnotationSpec
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
class CalificacionControllerTest(@Autowired val mockMvc: MockMvc) {
    // ----- Obtener calificaciones de un libro
    @Test
    fun `Se pueden obtenre las calificaciones de un libro`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/libros/{libroId}/calificaciones", 15)
                    .param("page", "0")
            )
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].titulo").value("1984"))
    }

    // ----- Calificar un libro
}