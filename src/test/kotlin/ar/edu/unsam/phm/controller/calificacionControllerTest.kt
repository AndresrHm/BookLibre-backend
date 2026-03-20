package ar.edu.unsam.phm.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals


@SpringBootTest
@AutoConfigureMockMvc
class CalificacionControllerTest(@Autowired val mockMvc: MockMvc) {

    // ----- Obtener calificaciones de un libro -----
    @Test
    fun `Se pueden obtener las calificaciones de un libro`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/libros/{libroId}/calificaciones", 15)
                    .param("page", "0")
            )
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].nombreUsuario").value("Juan Perez"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.content[0].imagenUsuario")
                    .value("https://ui-avatars.com/api/?name=Juan+Perez")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].puntuacion").value(4.5))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].comentario").value("Me encantó"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].fecha").exists())


    }

    @Test
    fun `Si el libro no existe, no se retorna ninguna calificación`() {
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.get("/libros/{libroId}/calificaciones", 69420)
                .param("page", "0")
        )
            .andExpect(status().isNotFound).andReturn().resolvedException?.message

        assertEquals(errorMessage, "No existe libro con el id 69420")
    }

    // ----- Calificar un libro -----
    @Test
    fun `Si el usuario devolvió el libro, y es lector, puede calificarlo`() {
        // Libro[40] (ID 41) -> It, prestado por usuario con ID 5 a usuario con ID 3.
        // Tiene fecha de devolución como hoy - 1 día.
        // Reserva ID 55.
        val requestBody = """
    {
        "reservaId": 55,
        "usuarioId": 3,
        "comentario": "Muy buen libro",
        "puntuacion": 4.5
    }
""".trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/libros/{libroId}/calificaciones", 41)
                .param("usuarioId", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        ).andExpect(status().isOk)
    }

    @Test
    fun `Si el usuario no es lector, no puede calificarlo`() {
        // Libro[40] (ID 41) -> It, prestado por usuario con ID 5 a usuario con ID 3.
        // Tiene fecha de devolución como hoy - 1 día.
        // Reserva ID 55.

        val requestBody = """
    {
        "reservaId": 55,
        "usuarioId": 69420,
        "comentario": "Muy buen libro",
        "puntuacion": 4.5
    }
""".trimIndent()

        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.post("/libros/{libroId}/calificaciones", 41)
                .param("usuarioId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "El usuario logueado no puede calificar este libro")
    }

    @Test
    fun `Si el usuario es lector pero aún no terminó la reserva, no puede calificarlo`() {
        // Libro[41] (ID 42) -> Carrie, prestado por usuario con ID 5 a usuario con ID 3.
        // Tiene fecha de devolución como hoy + 3 días.
        // Reserva ID 56.

        val requestBody = """
    {
        "reservaId": 56,
        "usuarioId": 3,
        "comentario": "Muy buen libro",
        "puntuacion": 4.5
    }
""".trimIndent()

        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.post("/libros/{libroId}/calificaciones", 42)
                .param("usuarioId", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "Podrá calificar el libro cuando termine la reserva")
    }
}