package ar.edu.unsam.phm.controller

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
class ReservaControllerTest(@Autowired val mockMvc: MockMvc) {
    // ----- Reservas Realizadas -----
    @Test
    fun `Un usuario puede obtener sus reservas realizadas correctamente (toMe)`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reservas/{id}/realizadas", 1)
                    .param("page", "0")
                    .param("size", "6")
            )
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].titulo").value("1984"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].titulo").value("Animal Farm"))
    }

    @Test
    fun `Si el usuario es inexistente, se rechaza la request`() {

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reservas/{id}/realizadas", 99)
                    .param("page", "0")
                    .param("size", "6")
            )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "El usuario logueado no existe")
    }

    @Test
    fun `El filtrado funciona correctamente`() {

        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reservas/{id}/realizadas", 1)
                    .param("page", "0")
                    .param("size", "6")
                    .param("string", "cien")
            )
            .andExpect(status().isOk)

            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(6))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.totalPages").value(1))

            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].titulo").value("Cien años de soledad"))
            .andExpect(jsonPath("$.content[0].autor").value("Gabriel García Márquez"))
            .andExpect(
                jsonPath("$.content[0].imagenUrl")
                    .value("https://covers.openlibrary.org/b/id/14831384-L.jpg")
            )

    }

    // ----- Reservas Recibidas -----
    @Test
    fun `Un usuario puede obtener sus reservas recibidas correctamente (byMe)`() {

        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reservas/{id}/recibidas", 1)
                    .param("page", "0")
                    .param("size", "6")
            )
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].titulo").value("Dune"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].titulo").value("Foundation"))
    }

    @Test
    fun `_Si el usuario es inexistente, se rechaza la request`() {

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reservas/{id}/recibidas", 99)
                    .param("page", "0")
                    .param("size", "6")
            )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "El usuario logueado no existe")
    }

    @Test
    fun `_El filtrado funciona correctamente`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/reservas/{id}/recibidas", 1)
                    .param("page", "0")
                    .param("size", "6")
                    .param("string", "the l")
            )
            .andExpect(status().isOk)

            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(6))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(1))

            .andExpect(
                jsonPath("$.content[*].titulo")
                    .value(
                        containsInAnyOrder(
                            "The Left Hand of Darkness",
                            "The Lord of the Rings"
                        )
                    )
            )

            .andExpect(
                jsonPath("$.content[*].autor")
                    .value(
                        containsInAnyOrder(
                            "Ursula K. Le Guin",
                            "J.R.R. Tolkien"
                        )
                    )
            )

            .andExpect(
                jsonPath("$.content[*].imagenUrl")
                    .value(
                        containsInAnyOrder(
                            "https://covers.openlibrary.org/b/id/14920821-L.jpg",
                            "https://covers.openlibrary.org/b/id/15131000-L.jpg"
                        )
                    )
            )
    }
}