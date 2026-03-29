package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.comun
import ar.edu.unsam.phm.domain.crearLibro
import ar.edu.unsam.phm.domain.crearUsuario
import ar.edu.unsam.phm.dto.EstadoReserva
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
class ReservaControllerTest(@Autowired val mockMvc: MockMvc) {
    @Autowired
    private lateinit var reservaRepository: ReservaRepository

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var libroRepository: LibroRepository

    val libro1usuario1 = crearLibro(
        "1984",
        comun,
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
        comun,
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
        comun,
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
        comun,
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

    val usuario1 = crearUsuario(
        id = 1
    )

    val usuario2 = crearUsuario(
        id = 2
    )

    val fechaRecogida = LocalDate.of(2026, 3, 20)
    val fechaDevolucion = LocalDate.of(2026, 3, 21)

    val reserva1 = crearReserva(
        libro1usuario1,
        usuarioPrestador = usuario1,
        usuarioLector = usuario2,
        fechaRecogida,
        fechaDevolucion,
    )

    val reserva2 = crearReserva(
        libro2usuario1,
        usuarioPrestador = usuario1,
        usuarioLector = usuario2,
        fechaRecogida,
        fechaDevolucion,
    )

    val reserva3 = crearReserva(
        libro1usuario2,
        usuarioPrestador = usuario2,
        usuarioLector = usuario1,
        fechaRecogida,
        fechaDevolucion,
    )

    val reserva4 = crearReserva(
        libro2usuario2,
        usuarioPrestador = usuario2,
        usuarioLector = usuario1,
        fechaRecogida,
        fechaDevolucion,
    )

    @BeforeEach
    fun init() {
        reservaRepository.clearInit()
        usuarioRepository.clearInit()
        libroRepository.clearInit()

        usuarioRepository.create(usuario1)
        usuarioRepository.create(usuario2)

        libroRepository.create(libro1usuario1)
        libroRepository.create(libro2usuario1)
        libroRepository.create(libro1usuario2)
        libroRepository.create(libro2usuario2)

        reservaRepository.create(reserva1)
        reservaRepository.create(reserva2)
        reservaRepository.create(reserva3)
        reservaRepository.create(reserva4)
    }

    // ----- Reservas Realizadas -----
    @Test
    fun `Un usuario puede obtener sus reservas realizadas correctamente (donde el usuario es lector)`() {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/reservas/{id}", usuario1.id)
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
                MockMvcRequestBuilders.get("/reservas/{id}", -1)
                    .param("type", "TO_ME")
                    .param("page", "0")
                    .param("size", "6")
            ).andExpect(status().isBadRequest).andReturn().resolvedException?.message

        assertEquals(ErrorStrings.USUARIO_LOGUEADO_NO_EXISTE, errorMessage)
    }

    @Test
    fun `El filtrado funciona correctamente`() {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/reservas/{id}", usuario1.id)
                    .param("type", "TO_ME")
                    .param("page", "0")
                    .param("size", "6")
                    .param("string", "dun")
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
            MockMvcRequestBuilders.get("/reservas/{id}", usuario1.id)
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
            MockMvcRequestBuilders.get("/reservas/{id}", -1)
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
            MockMvcRequestBuilders.get("/reservas/{id}", usuario1.id)
                .param("type", "BY_ME")
                .param("page", "0")
                .param("size", "6")
                .param("string", "cien")
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
    fechaRecogida: LocalDate,
    fechaDevolucion: LocalDate,
): Reserva {
    return Reserva(
        libro,
        usuarioPrestador,
        usuarioLector,
        fechaRecogida,
        fechaDevolucion,
    )
}