package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Rol
import ar.edu.unsam.phm.domain.TipoLibro
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.crearLibro
import ar.edu.unsam.phm.domain.crearUsuario
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.CalificacionRepository
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.RolRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
class CalificacionControllerTest(@Autowired val mockMvc: MockMvc) {
    @Autowired
    private lateinit var reservaRepository: ReservaRepository

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var libroRepository: LibroRepository

    @Autowired
    private lateinit var calificacionRepository: CalificacionRepository

    @Autowired
    private lateinit var rolRepository: RolRepository

    val libro1usuario1 = crearLibro(
        "1984",
        TipoLibro.COMUN,
        "Distopía política",
        "https://covers.openlibrary.org/b/id/12693610-L.jpg",
        Genero.CIENCIA_FICCION,
        "George Orwell",
        328,
        9780451524935L,
        Idioma.INGLES,
        "Secker & Warburg",
        LocalDate.of(1949, 6, 8),
        EstadoLibro.BUENO,
        1
    )

    lateinit var usuario1: Usuario
    lateinit var usuario2: Usuario

    private lateinit var rolLector: Rol
    private lateinit var rolPublicador: Rol

    var idUsuario1 = 0L
    var idUsuario2 = 0L

    private val hoy = LocalDate.now()
    private val fechaRecogida = hoy.minusDays(10)
    private val fechaDevolucion = hoy.minusDays(1)

    val reserva1 = crearReserva(
        libro1usuario1,
        usuarioPrestador = usuario1,
        usuarioLector = usuario2,
        fechaRecogida,
        fechaDevolucion,
    )

    var idReserva1 = reserva1.id

    // Reserva que todavia no termino para validar que no se puede calificar
    val reservaNoTerminada = crearReserva(
        libro1usuario1,
        usuarioPrestador = usuario1,
        usuarioLector = usuario2,
        hoy,
        hoy.plusDays(1),
    )

    var idReservaNoTerminada = reserva1.id

    private val fechaCalificacion1 = hoy.minusDays(2)
    val calificacion1 = crearCalificacion(
        reserva1.usuarioLector,
        reserva1.libro,
        4.5,
        "Muy bueno!",
        fechaCalificacion1
    )

    @BeforeEach
    fun init() {
        calificacionRepository.deleteAll()
        reservaRepository.deleteAll()
        libroRepository.deleteAll()
        usuarioRepository.deleteAll()
        rolRepository.deleteAll()

        rolLector = Rol().apply { name = TipoUsuario.LECTOR.roleName }
        rolPublicador = Rol().apply { name = TipoUsuario.PUBLICADOR.roleName }
        rolRepository.saveAll(listOf(rolLector, rolPublicador))

        usuario1 = crearUsuario(
            roles = mutableSetOf(rolLector, rolPublicador)
        )
        usuario2 = crearUsuario(
            nombre = "Juan",
            apellido = "Perez",
            urlImagen = "https://ui-avatars.com/api/?name=Juan+Perez",
            roles = mutableSetOf(rolLector, rolPublicador)
        )

        usuarioRepository.save(usuario1)
        usuarioRepository.save(usuario2)
        idUsuario1 = usuario1.id!! // Los seteo porque no se reinician los ids al hacer deleteAll()
        idUsuario2 = usuario2.id!!

        libroRepository.save(libro1usuario1)

        reservaRepository.save(reserva1)
        reservaRepository.save(reservaNoTerminada)
        idReserva1 = reserva1.id // Los seteo porque no se reinician los ids al hacer deleteAll()
        idReservaNoTerminada = reservaNoTerminada.id

        calificacionRepository.save((calificacion1))
    }

    // ----- Obtener calificaciones de un libro -----
    @Test
    fun `Se pueden obtener las calificaciones de un libro`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/calificaciones/{libroId}", libro1usuario1.id)
                    .param("page", "0")
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].nombreUsuario").value("Juan Perez"))
            .andExpect(jsonPath("$.content[0].imagenUsuario").value("https://ui-avatars.com/api/?name=Juan+Perez"))
            .andExpect(jsonPath("$.content[0].puntuacion").value(4.5))
            .andExpect(jsonPath("$.content[0].comentario").value("Muy bueno!"))
            .andExpect(jsonPath("$.content[0].fecha").value(fechaCalificacion1.toString()))

    }

    @Test
    fun `Si el libro no existe, no se retorna ninguna calificación`() {
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.get("/calificaciones/{libroId}", -1)
                .param("page", "0")
        )
            .andExpect(status().isNotFound).andReturn().resolvedException?.message

        assertEquals(ErrorStrings.libroNoExistePorId(-1), errorMessage)
    }

    // ----- Calificar un libro -----
    @Test
    fun `Si el usuario devolvió el libro, y es lector, puede calificarlo`() {
        val requestBody = """
    {
        "reservaId": ${idReserva1},
        "usuarioId": ${idUsuario2},
        "comentario": "Muy buen libro",
        "puntuacion": 4.5
    }
""".trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/calificaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        ).andExpect(status().isCreated)
    }

    @Test
    fun `Si el usuario no es el lector, no puede calificarlo`() {
        val requestBody = """
    {
        "reservaId": ${idReserva1},
        "usuarioId": ${idUsuario1},
        "comentario": "Muy buen libro",
        "puntuacion": 4.5
    }
""".trimIndent()

        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.post("/calificaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.USUARIO_NO_PUEDE_CALIFICAR_LIBRO, errorMessage)
    }

    @Test
    fun `Si el usuario es lector pero aún no terminó la reserva, no puede calificarlo`() {
        val requestBody = """
    {
        "reservaId": ${idReservaNoTerminada},
        "usuarioId": ${idUsuario2},
        "comentario": "Muy buen libro",
        "puntuacion": 4.5
    }
""".trimIndent()

        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.post("/calificaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.PODRA_CALIFICAR_AL_FINALIZAR_RESERVA, errorMessage)
    }

    fun crearCalificacion(
        usuario: Usuario = crearUsuario(roles = mutableSetOf(rolLector, rolPublicador)),
        libro: Libro = crearLibro(),
        puntuacion: Double = 1.0,
        comentario: String = "Comentario",
        fecha: LocalDate = LocalDate.now(),
    ): Calificacion {
        return Calificacion(
            usuario,
            libro,
            puntuacion,
            comentario,
            fecha,
        )
    }
}

