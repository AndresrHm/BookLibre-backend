package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.crearLibro
import ar.edu.unsam.phm.dto.LibroEdicionDTO
import ar.edu.unsam.phm.repository.LibroRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import kotlin.test.assertFalse
import org.junit.jupiter.api.Assertions.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
class LibroControllerTest(@Autowired val mockMvc: MockMvc) {
    @Autowired
    private lateinit var libroRepository: LibroRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    val libro1 = crearLibro()

    @BeforeEach
    fun init() {
        libroRepository.clearInit()
        libroRepository.create(libro1)
    }

    // ----- Crear un libro -----
    @Test
    fun `Se puede crear un libro`() {
        val libroValido = crearLibroDTO()
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/libros")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(libroValido))
            )
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.titulo").value("Título de libro"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.tipoLibro").value("comun"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.descripcion").value("Descripción de libro"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.imagenUrl").value("Portada de libro"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.genero").value("CIENCIA_FICCION"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.autor").value("Autor de libro"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cantidadPaginas").value("10"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.isbn13").value("1111111111111"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.idioma").value("ESPANOL"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.editorial").value("Editorial de libro"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.fechaPublicacion").value("2026-03-23"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.estado").value("BUENO"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.idPropietario").value("0"))
    }

    @Test
    fun `No se puede crear un libro que no pase las validaciones`() {
        val libroInvalido = crearLibroDTO(titulo = "")
        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/libros")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(libroInvalido))
            )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "El libro debe tener un título")
        assertEquals(libroRepository.getElementos().size, 1) // El libro no fue agregado al repositorio
    }

    // ----- Obtener detalle de libro -----
    @Test
    fun `Cuando se provee un id, devuelve el libro asociado a ese id (detalle)`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/libros/{libroId}/detalle", 1)
        ).andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.titulo").value("Título de libro"))
    }

    @Test
    fun `Cuando se provee un id de un libro que no existe, devuelve un error (detalle)`() {
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.get("/libros/{libroId}/detalle", -1)
        ).andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "No existe un libro con el id -1")
    }

    // ----- Obtener libro para edición -----
    @Test
    fun `Cuando se provee un id, devuelve el libro asociado a ese id (edición)`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/libros/{libroId}/edicion", 1)
        ).andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.titulo").value("Título de libro"))
    }

    @Test
    fun `Cuando se provee un id de un libro que no existe, devuelve un error (edición)`() {
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.get("/libros/{libroId}/edicion", -1)
        ).andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "No existe un libro con el id -1")
    }

    // ----- Editar un libro -----
    @Test
    fun `Se puede editar un libro`() {
        val libroEditadoDTO = crearLibroDTO(titulo = "Nuevo título editado")
        mockMvc.perform(
            MockMvcRequestBuilders.put("/libros")
                .param("idUsuario", "0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroEditadoDTO))
        ).andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.titulo").value("Nuevo título editado"))
    }

    @Test
    fun `No se puede editar un libro si el usuario no es el propietario`() {
        val libroDTO = crearLibroDTO(idPropietario = -1)

        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.put("/libros")
                .param("idUsuario", "-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroDTO))
        )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals("El libro con id 1 no pertenece al usuario -1", errorMessage)
    }

    @Test
    fun `No se puede editar un libro que no se encuentra en la base de datos`() {
        val libroDTO = crearLibroDTO(id = -1)

        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.put("/libros")
                .param("idUsuario", "0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroDTO))
        )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals("No existe un libro con el id -1", errorMessage)
    }

    @Test
    fun `No se puede editar un libro con datos no permitidos`() {
        val libroDTO = crearLibroDTO(titulo = "")

        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.put("/libros")
                .param("idUsuario", "0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroDTO))
        )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals("El libro debe tener un título", errorMessage)
    }

    // ----- Eliminar un libro -----
    @Test
    fun `Se puede eliminar un libro de la base de datos`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/libros/1")
                .param("idUsuario", "0")
        )
            .andExpect(status().isNoContent)

        assertEquals(libroRepository.getById(1), null) // El libro fue eliminado satisfactoriamente
    }

    @Test
    fun `No se puede eliminar un libro que no está en la base de datos`() {
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.delete("/libros/0")
                .param("idUsuario", "0")
        )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "No existe un libro con el id 0")
    }

    @Test
    fun `No se puede eliminar un libro que no pertenece al usuario que lo solicita`() {
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.delete("/libros/1")
                .param("idUsuario", "-1")
        )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "El libro con id 1 no pertenece al usuario -1")
    }
}

fun crearLibroDTO(
    id: Int = 1,
    titulo: String = "Título de libro",
    tipoLibro: String = "comun",
    descripcion: String = "Descripción de libro",
    portada: String = "Portada de libro",
    genero: Genero = Genero.CIENCIA_FICCION,
    autor: String = "Autor de libro",
    cantidadPaginas: Int = 10,
    isbn13: Long = 1111111111111,
    idioma: Idioma = Idioma.ESPANOL,
    editorial: String = "Editorial de libro",
    fechaPublicacion: LocalDate = LocalDate.of(2026, 3, 23),
    estadoLibro: EstadoLibro = EstadoLibro.BUENO,
    idPropietario: Int = 0
): LibroEdicionDTO {
    return LibroEdicionDTO(
        id = id,
        imagenUrl = portada,
        titulo = titulo,
        tipoLibro = tipoLibro,
        genero = genero,
        autor = autor,
        idioma = idioma,
        estado = estadoLibro,
        editorial = editorial,
        fechaPublicacion = fechaPublicacion,
        cantidadPaginas = cantidadPaginas,
        isbn13 = isbn13,
        descripcion = descripcion,
        idPropietario = idPropietario
    )
}