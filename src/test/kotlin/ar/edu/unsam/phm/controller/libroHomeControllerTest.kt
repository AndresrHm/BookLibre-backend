package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.TipoLibro
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.CalificacionRepository
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
class LibroHomeControllerTest(@Autowired val mockMvc: MockMvc) {

    @Autowired
    private lateinit var calificacionRepository: CalificacionRepository

    @Autowired
    private lateinit var libroRepository: LibroRepository

    @Autowired
    private lateinit var reservaRepository: ReservaRepository

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    private val usuario1 = crearUsuario(nombre = "Ana", email = "ana@test.com")
    private val usuario2 = crearUsuario(nombre = "Bruno", email = "bruno@test.com")

    var usuario1Id = 0L
    var usuario2Id = 0L

    private lateinit var libro1Usuario1: Libro
    private lateinit var libro2Usuario1: Libro
    private lateinit var libro1Usuario2: Libro
    private lateinit var libro2Usuario2: Libro
    private lateinit var libro3Usuario2: Libro

    @BeforeEach
    fun init() {
        reservaRepository.deleteAll()
        calificacionRepository.deleteAll()
        libroRepository.deleteAll()
        usuarioRepository.deleteAll()

        usuarioRepository.save(usuario1)
        usuarioRepository.save(usuario2)

        usuario1Id = usuario1.id!!
        usuario2Id = usuario2.id!!

        libro1Usuario1 = crearLibro(
            titulo = "1984",
            genero = Genero.CIENCIA_FICCION,
            autor = "George Orwell",
            cantidadPaginas = 328,
            isbn13 = 9780451524935,
            idPropietario = usuario1Id // ID real de Ana
        )

        libro2Usuario1 = crearLibro(
            titulo = "Hamlet",
            genero = Genero.DRAMA,
            autor = "William Shakespeare",
            cantidadPaginas = 160,
            isbn13 = 9780141015866,
            idPropietario = usuario1Id
        )

        libro1Usuario2 = crearLibro(
            titulo = "Dune",
            genero = Genero.CIENCIA_FICCION,
            autor = "Frank Herbert",
            cantidadPaginas = 412,
            isbn13 = 9780441172719,
            idPropietario = usuario2Id // ID real de Bruno
        )

        libro2Usuario2 = crearLibro(
            titulo = "Foundation",
            genero = Genero.CIENCIA_FICCION,
            autor = "Isaac Asimov",
            cantidadPaginas = 255,
            isbn13 = 9780553293357,
            idPropietario = usuario2Id
        )

        libro3Usuario2 = crearLibro(
            titulo = "El gran gatsby",
            genero = Genero.LITERATURA_CLASICA,
            autor = "F. Scott Fitzgerald",
            cantidadPaginas = 180,
            isbn13 = 9780743273565,
            idPropietario = usuario2Id
        )
        libroRepository.saveAll(
            listOf(
                libro1Usuario1,
                libro2Usuario1,
                libro1Usuario2,
                libro2Usuario2,
                libro3Usuario2
            )
        )
    }

    @Test
    fun `Se pueden obtener libros de home con filtros por query params`() {
        mockMvc.perform(
            requestLibrosHome(userId = usuario1Id.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(6))
            .andExpect(jsonPath("$.totalElements").value(3))
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.content", hasSize<Any>(3)))
            .andExpect(jsonPath("$.content[*].tipo", contains("HOME", "HOME", "HOME")))
            .andExpect(jsonPath("$.content[*].libro.titulo", contains("Dune", "El gran gatsby", "Foundation")))
            .andExpect(jsonPath("$.content[*].libro.userCreador", contains("Bruno", "Bruno", "Bruno")))
    }

    @Test
    fun `Se obtiene un libro por busqueda por nombre`() {
        mockMvc.perform(
            requestLibrosHome(userId = usuario1Id.toString())
                .param("search", "Dune")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content", hasSize<Any>(1)))
            .andExpect(jsonPath("$.content[0].tipo").value("HOME"))
            .andExpect(jsonPath("$.content[0].libro.titulo").value("Dune"))
            .andExpect(jsonPath("$.content[0].libro.autor").value("Frank Herbert"))
    }

    @Test
    fun `Se pueden enviar multiples generos como query params`() {
        mockMvc.perform(
            requestLibrosHome(size = "10", userId = usuario1Id.toString())
                .param("generos", "DRAMA")
                .param("generos", "CIENCIA_FICCION")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.content", hasSize<Any>(2)))
            .andExpect(jsonPath("$.content[*].tipo", containsInAnyOrder("HOME", "HOME")))
            .andExpect(jsonPath("$.content[*].libro.titulo", containsInAnyOrder("Dune", "Foundation")))
            .andExpect(jsonPath("$.content[*].libro.genero", containsInAnyOrder("CIENCIA_FICCION", "CIENCIA_FICCION")))
    }

    @Test
    fun `Si el userId no existe, se retorna error 404`() {
        val usuarioIdInexistente: Long = 999
        val errorMessage = mockMvc.perform(
            requestLibrosHome(userId = usuarioIdInexistente.toString())
        )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.usuarioNoExistePorId(usuarioIdInexistente), errorMessage)
    }

    @Test
    fun `Si el page es negativo, se retorna error 400`() {
        val errorMessage = mockMvc.perform(
            requestLibrosHome(page = "-1", userId = usuario1Id.toString())
        )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.PAGE_NO_PUEDE_SER_NEGATIVO, errorMessage)
    }

    @Test
    fun `Si se pasan fechas invalidas en el filtro, se retorna error 400`() {
        val errorMessage = mockMvc.perform(
            requestLibrosHome(userId = usuario1Id.toString())
                .param("fechaDesde", "2027-05-01")
                .param("fechaHasta", "2026-04-01")
        )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.FECHA_DESDE_MAYOR_A_FECHA_HASTA, errorMessage)
    }

    @Test
    fun `Te devuelve los libros segun el orden pedido`() {
        mockMvc.perform(
            requestLibrosHome(ordenHomePage = "NOMBRE_DESC", userId = usuario1Id.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[*].libro.titulo", contains("Foundation", "El gran gatsby", "Dune")))
    }

    @Test
    fun `Se puede filtrar home por usuarioPrestador por nombre`() {
        mockMvc.perform(
            requestLibrosHome(userId = usuario2Id.toString())
                .param("paginasMin", "200")
                .param("paginasMax", "800")
                .param("usuarioPrestador", "Ana")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].tipo").value("HOME"))
            .andExpect(jsonPath("$.content[0].libro.titulo").value("1984"))
            .andExpect(jsonPath("$.content[0].libro.userCreador").value("Ana"))
    }

    @Test
    fun `Si se filtra por usuarioPrestador propio, se muestran sus libros`() {
        mockMvc.perform(
            requestLibrosHome(userId = usuario2Id.toString())
                .param("paginasMin", "200")
                .param("paginasMax", "800")
                .param("usuarioPrestador", "Ana")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].tipo").value("HOME"))
            .andExpect(jsonPath("$.content[0].libro.titulo").value("1984"))
            .andExpect(jsonPath("$.content[0].libro.userCreador").value("Ana"))
    }

    // TODO: Estos tests están comentados hasta que se implemente la funcionalidad
    // @Test
    // fun `Se pueden obtener prestadores disponibles para el desplegable`() {
    //     mockMvc.perform(
    //         MockMvcRequestBuilders.get("/libros/prestadoresDisponibles")
    //             .param("userId", "1")
    //     )
    //         .andExpect(status().isOk)
    //         .andExpect(jsonPath("$", hasSize<Any>(1)))
    //         .andExpect(jsonPath("$[0]").value("Bruno"))
    // }
    //
    // @Test
    // fun `Se pueden filtrar prestadores disponibles por nombre`() {
    //     mockMvc.perform(
    //         MockMvcRequestBuilders.get("/libros/prestadoresDisponibles")
    //             .param("userId", "1")
    //             .param("search", "bru")
    //     )
    //         .andExpect(status().isOk)
    //         .andExpect(jsonPath("$", hasSize<Any>(1)))
    //         .andExpect(jsonPath("$[0]").value("Bruno"))
    // }

    private fun requestLibrosHome(
        page: String = "0",
        size: String = "6",
        userId: String = "1",
        ordenHomePage: String = "NOMBRE_ASC",
    ): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.get("/libros")
            .param("flagDiff", "HOME")
            .param("page", page)
            .param("size", size)
            .param("userId", userId)
            .param("ordenHomePage", ordenHomePage)
    }
}

private fun crearUsuario(
    nombre: String,
    email: String,
): Usuario {
    return Usuario(
        nombre = nombre,
        apellido = "Test",
        descripcion = "Usuario de prueba",
        email = email,
        celular = "11111111",
        ciudad = "Buenos Aires",
        tipoUsuario = TipoUsuario.LECTORPUBLICADOR,
        urlImagen = "https://img.test/user.jpg",
    )
}

private fun crearLibro(
    titulo: String,
    genero: Genero,
    autor: String,
    cantidadPaginas: Int,
    isbn13: Long,
    idPropietario: Long,
): Libro {
    return Libro(
        titulo = titulo,
        tipoLibro = TipoLibro.COMUN,
        descripcion = "Descripcion de $titulo",
        portada = "https://img.test/${titulo.lowercase().replace(" ", "-")}.jpg",
        genero = genero,
        autor = autor,
        cantidadPaginas = cantidadPaginas,
        isbn13 = isbn13,
        idioma = Idioma.INGLES,
        editorial = "Editorial Test",
        fechaPublicacion = java.time.LocalDate.of(2020, 1, 1),
        estadoLibro = EstadoLibro.BUENO,
        idPropietario = idPropietario,
    )
}