package ar.edu.unsam.phm.controller

import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class LibroControllerTest(@Autowired val mockMvc: MockMvc) {

    @Test
    fun `Se pueden obtener libros de home con filtros por query params`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/libros/homePage/user/filter")
                    .param("page", "0")
                    .param("size", "6")
                    .param("userId", "1")
                    .param("ordenHomePage", "NOMBRE_ASC")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(6))
            .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$.content").isArray)
    }

    @Test
    fun `Se obtiene un libro por busqueda por nombre`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/libros/homePage/user/filter")
                    .param("page", "0")
                    .param("size", "6")
                    .param("userId", "2")
                    .param("ordenHomePage", "NOMBRE_ASC")
                    .param("search", "Dune")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content[0].titulo").value("Dune"))
    }

    @Test
    fun `Se pueden enviar multiples generos como query params`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/libros/homePage/user/filter")
                    .param("page", "0")
                    .param("size", "10")
                    .param("userId", "1")
                    .param("ordenHomePage", "NOMBRE_ASC")
                    // Spring bindea listas en @ModelAttribute recibiendo el mismo param repetido.
                    .param("generos", "DRAMA")
                    .param("generos", "CIENCIA_FICCION")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content[*].genero", hasItem("DRAMA")))
            .andExpect(jsonPath("$.content[*].genero", hasItem("CIENCIA_FICCION")))
            .andExpect(jsonPath("$.content[0].titulo", notNullValue()))
    }
    @Test
    fun `Si el userId no existe, se retorna error 404`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/libros/homePage/user/filter")
                    .param("page", "0")
                    .param("size", "6")
                    .param("userId", "69420")
                    .param("ordenHomePage", "NOMBRE_ASC")
            )
            .andExpect(status().isNotFound)
    }

     @Test
     fun `Si el page es negativo, se retorna error 400`() {
         mockMvc
             .perform(
                 MockMvcRequestBuilders.get("/libros/homePage/user/filter")
                     .param("page", "-1")
                     .param("size", "6")
                     .param("userId", "1")
                     .param("ordenHomePage", "NOMBRE_ASC")
             )
             .andExpect(status().isBadRequest)
    }

    @Test
    fun `Si se pasan fechas en el filtro, se verifica que la fecha de final sea posterior a la fecha inicial`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/libros/homePage/user/filter")
                    .param("page", "0")
                    .param("size", "6")
                    .param("userId", "1")
                    .param("ordenHomePage", "NOMBRE_ASC")
                    .param("fechaDesde", "2027-05-01")
                    .param("fechaHasta", "2026-04-01")
            )
            .andExpect(status().isBadRequest)
    }

}