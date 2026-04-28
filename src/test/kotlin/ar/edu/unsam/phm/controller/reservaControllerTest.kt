package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Rol
import ar.edu.unsam.phm.domain.TipoLibro
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.crearLibro
import ar.edu.unsam.phm.domain.crearUsuario
import ar.edu.unsam.phm.dto.EstadoReserva
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.CalificacionRepository
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.RolRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ReservaControllerTest(@Autowired val mockMvc: MockMvc) {
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

    val libro2usuario1 = crearLibro(
        "Cien años de soledad",
        TipoLibro.COMUN,
        "Realismo mágico",
        "https://covers.openlibrary.org/b/id/14831384-L.jpg",
        Genero.LITERATURA_CLASICA,
        "Gabriel García Márquez",
        417,
        9780307474728L,
        Idioma.ESPANOL,
        "Sudamericana",
        LocalDate.of(1967, 5, 30),
        EstadoLibro.EXCELENTE,
        1
    )

    val libro1usuario2 = crearLibro(
        "Dune",
        TipoLibro.COMUN,
        "Sci-fi clásica",
        "https://covers.openlibrary.org/b/id/15092781-L.jpg",
        Genero.CIENCIA_FICCION,
        "Frank Herbert",
        412,
        9780441172719L,
        Idioma.INGLES,
        "Chilton Books",
        LocalDate.of(1965, 8, 1),
        EstadoLibro.MUY_BUENO,
        2
    )

    val libro2usuario2 = crearLibro(
        "Foundation",
        TipoLibro.COMUN,
        "Saga Fundación",
        "https://covers.openlibrary.org/b/id/14612980-L.jpg",
        Genero.CIENCIA_FICCION,
        "Isaac Asimov",
        255,
        9780553293357L,
        Idioma.INGLES,
        "Gnome Press",
        LocalDate.of(1951, 1, 1),
        EstadoLibro.BUENO,
        2
    )

    lateinit var usuario1: Usuario
    lateinit var usuario2: Usuario

    private lateinit var rolLector: Rol
    private lateinit var rolPublicador: Rol

    val fechaRecogida = LocalDate.of(2026, 3, 20)
    val fechaDevolucion = LocalDate.of(2026, 3, 21)

    private lateinit var reserva1: Reserva
    private lateinit var reserva2: Reserva
    private lateinit var reserva3: Reserva
    private lateinit var reserva4: Reserva

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

        usuario1 = crearUsuario(id = 1, roles = mutableSetOf(rolLector, rolPublicador))
        usuario2 = crearUsuario(id = 2, roles = mutableSetOf(rolLector, rolPublicador))

        usuarioRepository.save(usuario1)
        usuarioRepository.save(usuario2)

        libroRepository.save(libro1usuario1)
        libroRepository.save(libro2usuario1)
        libroRepository.save(libro1usuario2)
        libroRepository.save(libro2usuario2)

        reserva1 = crearReserva(
            libro1usuario1,
            usuarioPrestador = usuario1,
            usuarioLector = usuario2,
            fechaRecogida,
            fechaDevolucion,
        )
        reserva2 = crearReserva(
            libro2usuario1,
            usuarioPrestador = usuario1,
            usuarioLector = usuario2,
            fechaRecogida,
            fechaDevolucion,
        )
        reserva3 = crearReserva(
            libro1usuario2,
            usuarioPrestador = usuario2,
            usuarioLector = usuario1,
            fechaRecogida,
            fechaDevolucion,
        )
        reserva4 = crearReserva(
            libro2usuario2,
            usuarioPrestador = usuario2,
            usuarioLector = usuario1,
            fechaRecogida,
            fechaDevolucion,
        )


        reservaRepository.save(reserva1)
        reservaRepository.save(reserva2)
        reservaRepository.save(reserva3)
        reservaRepository.save(reserva4)
    }

    // ----- Reservas Realizadas -----
    @Test
    fun `Un usuario puede obtener sus reservas realizadas correctamente (donde el usuario es lector)`() {

        mockMvc.perform(
            MockMvcRequestBuilders.get("/reservas")
                .principal(UsernamePasswordAuthenticationToken(usuario1.id.toString(), null))
                .param("usuarioId", usuario1.id.toString())
                .param("type", "TO_ME")
                .param("page", "0")
                .param("size", "6")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[*].titulo").value(containsInAnyOrder("Dune", "Foundation")))

            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(6))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(1))

            .andExpect(jsonPath("$.content[0].titulo").value("Dune"))
            .andExpect(jsonPath("$.content[0].autor").value("Frank Herbert"))
            .andExpect(jsonPath("$.content[0].imagenUrl").value("https://covers.openlibrary.org/b/id/15092781-L.jpg"))
            .andExpect(jsonPath("$.content[0].usuario").value(usuario1.nombre))
            .andExpect(jsonPath("$.content[0].fechaInicio").value(fechaRecogida.toString()))
            .andExpect(jsonPath("$.content[0].fechaFin").value(fechaDevolucion.toString()))
            .andExpect(jsonPath("$.content[0].estado").value(EstadoReserva.DEVUELTO.toString()))
            .andExpect(jsonPath("$.content[0].puedeCalificar").value(true))
            .andExpect(jsonPath("$.content[0].biblioKarma").value(824))

            .andExpect(jsonPath("$.content[1].titulo").value("Foundation"))
            .andExpect(jsonPath("$.content[1].autor").value("Isaac Asimov"))
            .andExpect(jsonPath("$.content[1].imagenUrl").value("https://covers.openlibrary.org/b/id/14612980-L.jpg"))
            .andExpect(jsonPath("$.content[1].usuario").value(usuario1.nombre))
            .andExpect(jsonPath("$.content[1].fechaInicio").value(fechaRecogida.toString()))
            .andExpect(jsonPath("$.content[1].fechaFin").value(fechaDevolucion.toString()))
            .andExpect(jsonPath("$.content[1].estado").value(EstadoReserva.DEVUELTO.toString()))
            .andExpect(jsonPath("$.content[1].puedeCalificar").value(true))
            .andExpect(jsonPath("$.content[1].biblioKarma").value(510))
    }

    @Test
    fun `Si el usuario es inexistente, se rechaza la request`() {

        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.get("/reservas")
                .principal(UsernamePasswordAuthenticationToken("-1", null))
                .param("usuarioId", "-1")
                .param("type", "TO_ME")
                .param("page", "0")
                .param("size", "6")
        ).andExpect(status().isBadRequest).andReturn().resolvedException?.message

        assertEquals(ErrorStrings.USUARIO_LOGUEADO_NO_EXISTE, errorMessage)
    }

    @Test
    fun `El filtrado funciona correctamente`() {

        mockMvc.perform(
            MockMvcRequestBuilders.get("/reservas")
                .principal(UsernamePasswordAuthenticationToken(usuario1.id.toString(), null))
                .param("usuarioId", usuario1.id.toString())
                .param("type", "TO_ME")
                .param("page", "0")
                .param("size", "6")
                .param("search", "dun")
        ).andExpect(status().isOk)

            .andExpect(jsonPath("$.page").value(0)).andExpect(jsonPath("$.size").value(6))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.totalPages").value(1))

            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].titulo").value("Dune"))
            .andExpect(jsonPath("$.content[0].autor").value("Frank Herbert"))
    }

    // ----- Reservas Recibidas -----
    @Test
    fun `Un usuario puede obtener sus reservas recibidas correctamente (donde el usuario es prestador)`() {

        mockMvc.perform(
            MockMvcRequestBuilders.get("/reservas")
                .principal(UsernamePasswordAuthenticationToken(usuario1.id.toString(), null))
                .param("usuarioId", usuario1.id.toString())
                .param("type", "BY_ME")
                .param("page", "0")
                .param("size", "6")
        )
            .andExpect(status().isOk)

            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(6))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.totalPages").value(1))

            .andExpect(jsonPath("$.content[0].titulo").value("1984"))
            .andExpect(jsonPath("$.content[0].autor").value("George Orwell"))
            .andExpect(jsonPath("$.content[0].imagenUrl").value("https://covers.openlibrary.org/b/id/12693610-L.jpg"))
            .andExpect(jsonPath("$.content[0].usuario").value(usuario2.nombre))
            .andExpect(jsonPath("$.content[0].fechaInicio").value(fechaRecogida.toString()))
            .andExpect(jsonPath("$.content[0].fechaFin").value(fechaDevolucion.toString()))
            .andExpect(jsonPath("$.content[0].estado").value(EstadoReserva.DEVUELTO.toString()))
            .andExpect(jsonPath("$.content[0].puedeCalificar").value(false))
            .andExpect(jsonPath("$.content[0].biblioKarma").value(656))

            .andExpect(jsonPath("$.content[1].titulo").value("Cien años de soledad"))
            .andExpect(jsonPath("$.content[1].autor").value("Gabriel García Márquez"))
            .andExpect(jsonPath("$.content[1].imagenUrl").value("https://covers.openlibrary.org/b/id/14831384-L.jpg"))
            .andExpect(jsonPath("$.content[1].usuario").value(usuario2.nombre))
            .andExpect(jsonPath("$.content[1].fechaInicio").value(fechaRecogida.toString()))
            .andExpect(jsonPath("$.content[1].fechaFin").value(fechaDevolucion.toString()))
            .andExpect(jsonPath("$.content[1].estado").value(EstadoReserva.DEVUELTO.toString()))
            .andExpect(jsonPath("$.content[1].puedeCalificar").value(false))
            .andExpect(jsonPath("$.content[1].biblioKarma").value(834))
    }

    @Test
    fun `Si el usuario es inexistente, se rechaza la request (recibidas)`() {

        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.get("/reservas")
                .principal(UsernamePasswordAuthenticationToken("-1", null))
                .param("usuarioId", "-1")
                .param("type", "BY_ME")
                .param("page", "0")
                .param("size", "6")
        )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.USUARIO_LOGUEADO_NO_EXISTE, errorMessage)
    }

    @Test
    fun `El filtrado funciona correctamente en reservas recibidas`() {

        mockMvc.perform(
            MockMvcRequestBuilders.get("/reservas")
                .principal(UsernamePasswordAuthenticationToken(usuario1.id.toString(), null))
                .param("usuarioId", usuario1.id.toString())
                .param("type", "BY_ME")
                .param("page", "0")
                .param("size", "6")
                .param("search", "cien")
        )
            .andExpect(status().isOk)

            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.content.length()").value(1))

            .andExpect(jsonPath("$.content[0].titulo").value("Cien años de soledad"))
            .andExpect(jsonPath("$.content[0].autor").value("Gabriel García Márquez"))
    }
}

fun crearReserva(
    libro: Libro,
    usuarioPrestador: Usuario,
    usuarioLector: Usuario,
    fechaRecogida: LocalDate = LocalDate.now(),
    fechaDevolucion: LocalDate = LocalDate.now().plusDays(1),
): Reserva {
    return Reserva(
        libro,
        usuarioPrestador,
        usuarioLector,
        fechaRecogida,
        fechaDevolucion,
    )
}