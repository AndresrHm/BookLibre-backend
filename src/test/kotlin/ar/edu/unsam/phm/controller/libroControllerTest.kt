package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.crearLibro
import ar.edu.unsam.phm.dto.GetLibroDTO
import ar.edu.unsam.phm.dto.LibroEdicionDTO
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@SpringBootTest
@AutoConfigureMockMvc
class LibroControllerTest(@Autowired val mockMvc: MockMvc) {
    @Autowired
    private lateinit var libroRepository: LibroRepository

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private var usuarioPerfilId: Long = 0
    private var libro1Id: Long = 0

    @BeforeEach
    fun init() {
        libroRepository.deleteAll()
        usuarioRepository.deleteAll()

        val usuarioGuardado = usuarioRepository.save(
            Usuario(
                nombre = "Usuario",
                apellido = "Perfil",
                descripcion = "Usuario para tests de perfil",
                email = "perfil@test.com",
                celular = "11223344",
                ciudad = "Buenos Aires",
                tipoUsuario = TipoUsuario.LECTORPUBLICADOR,
                urlImagen = "https://test.com/perfil.jpg"
            )
        )

        usuarioPerfilId = usuarioGuardado.id!!

        val libro1 = libroRepository.save(crearLibro(idPropietario = usuarioPerfilId))
        libroRepository.save(crearLibro(titulo = "Segundo libro", autor = "Otro autor", idPropietario = usuarioPerfilId))
        libroRepository.save(crearLibro(titulo = "Tercer libro", autor = "Tercer autor", genero = Genero.ROMANCE, idPropietario = usuarioPerfilId))

        libro1Id = libro1.id!!

        // Sanity check: los tres libros base deben quedar persistidos para los asserts del perfil.
        assertEquals(3, libroRepository.count())
    }

    // ----- Crear un libro -----
    @Test
    fun `Se puede crear un libro`() {
        val libroValido = crearLibroDTO(idPropietario = usuarioPerfilId)
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/libros")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(libroValido))
            )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.message").value("Se creó el libro Título de libro exitosamente"))
    }

    @Test
    fun `No se puede crear un libro que no pase las validaciones`() {
        val libroInvalido = crearLibroDTO(titulo = "", idPropietario = usuarioPerfilId)
        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/libros")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(libroInvalido))
            )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.LIBRO_DEBE_TENER_TITULO, errorMessage)
        assertEquals(libroRepository.count(), 3)
    }

    // ----- Obtener detalle de libro -----
    @Test
    fun `Cuando se provee un id, devuelve el libro asociado a ese id (detalle)`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/libros/{libroId}/{tipoLibroDTO}", libro1Id, GetLibroDTO.DETALLE)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.libro.titulo").value("Título de libro"))
    }

    @Test
    fun `Cuando se provee un id de un libro que no existe, devuelve un error (detalle)`() {
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.get("/libros/{libroId}/{tipoLibroDTO}", -1, GetLibroDTO.DETALLE)
        ).andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.libroNoEncontradoPorId(-1), errorMessage)
    }

    // ----- Obtener libro para edición -----
    @Test
    fun `Cuando se provee un id, devuelve el libro asociado a ese id (edición)`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/libros/{libroId}/{tipoLibroDTO}", libro1Id, GetLibroDTO.EDICION)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.libro.titulo").value("Título de libro"))
    }

    @Test
    fun `Cuando se provee un id de un libro que no existe, devuelve un error (edición)`() {
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.get("/libros/{libroId}/{tipoLibroDTO}", -1, GetLibroDTO.EDICION)
        ).andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.libroNoEncontradoPorId(-1), errorMessage)
    }

    // ----- Editar un libro -----
    @Test
    fun `Se puede editar un libro`() {
        val libroEditadoDTO = crearLibroDTO(
            id = libro1Id,
            titulo = "Nuevo título editado",
            idPropietario = usuarioPerfilId,
        )
        mockMvc.perform(
            MockMvcRequestBuilders.put("/libros")
                .param("idUsuario", usuarioPerfilId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroEditadoDTO))
        ).andExpect(status().isOk())
    }

    @Test
    fun `Se trae la lista de libros del perfil`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/libros")
                .param("flagDiff", "PERFIL")
                .param("page", "0")
                .param("size", "3")
                .param("userId", usuarioPerfilId.toString())
                .param("disponibilidadLibrosPerfil", "TODOS")
                .param("ordenPerfilPage", "FECHA_AGREGADO")
                .param("direccionPerfilPage", "DESCENDENTE")
        ).andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].tipo").value("PERFIL"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].tipo").value("PERFIL"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].tipo").value("PERFIL"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].libro.titulo").value("Título de libro"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].libro.titulo").value("Segundo libro"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[2].libro.titulo").value("Tercer libro"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.page").value(0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(3))
    }

    @Test
    fun `No se puede editar un libro si el usuario no es el propietario`() {
        val libroDTO = crearLibroDTO(id = libro1Id, idPropietario = usuarioPerfilId)

        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.put("/libros")
                .param("idUsuario", "-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroDTO))
        )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.libroNoPerteneceAlUsuario(libro1Id, -1), errorMessage)
    }

    @Test
    fun `No se puede editar un libro que no se encuentra en la base de datos`() {
        val libroDTO = crearLibroDTO(id = -1, idPropietario = usuarioPerfilId)

        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.put("/libros")
                .param("idUsuario", usuarioPerfilId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroDTO))
        )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.libroNoEncontradoPorId(-1), errorMessage)
    }

    @Test
    fun `No se puede editar un libro con datos no permitidos`() {
        val libroDTO = crearLibroDTO(id = libro1Id, titulo = "", idPropietario = usuarioPerfilId)

        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.put("/libros")
                .param("idUsuario", usuarioPerfilId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroDTO))
        )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.LIBRO_DEBE_TENER_TITULO, errorMessage)
    }

    // ----- Eliminar un libro -----
    @Test
    fun `No se puede eliminar un libro que no está en la base de datos`() {
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.delete("/libros/0")
                .param("idUsuario", usuarioPerfilId.toString())
        )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.libroNoEncontradoPorId(0), errorMessage)
    }

    @Test
    fun `No se puede eliminar un libro que no pertenece al usuario que lo solicita`() {
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.delete("/libros/$libro1Id")
                .param("idUsuario", "-1")
        )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.libroNoPerteneceAlUsuario(libro1Id, -1), errorMessage)
    }
}

fun crearLibroDTO(
    id: Long = 1,
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
    idPropietario: Long = 0
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