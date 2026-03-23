package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.crearLibro
import ar.edu.unsam.phm.domain.crearUsuario
import ar.edu.unsam.phm.dto.CreacionReservaDTO
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.containsInAnyOrder
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
import kotlin.test.assertEquals

val HOY = LocalDate.now()

//Lo moví a una clase aparte porque usa datos mockeados
@SpringBootTest
@AutoConfigureMockMvc
class CreacionReservaControllerTest(@Autowired val mockMvc: MockMvc) {
    @Autowired
    private lateinit var reservaRepository: ReservaRepository

    @Autowired
    private lateinit var libroRepository: LibroRepository

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    val usuarioLector = crearUsuario(nombre = "Lector")
    val usuarioPrestador = crearUsuario(nombre = "Prestador")
    val libro = crearLibro(idPropietario = 2, titulo = "Título")

    @BeforeEach
    fun init() {
        libroRepository.clearInit()
        usuarioRepository.clearInit()
        reservaRepository.clearInit()
    }

    // Tests de creación de reservas
    @Test
    fun `Se puede reservar un libro`() {
        usuarioRepository.create(usuarioLector)
        usuarioRepository.create(usuarioPrestador)
        libroRepository.create(libro)

        val reserva = crearCreacionReservaDTO(usuarioLectorId = 1, usuarioPrestadorId = 2, libroId = 1)

        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.libroId").value("1"))
            .andExpect(jsonPath("$.usuarioPrestadorId").value("2"))
            .andExpect(jsonPath("$.usuarioLectorId").value("1"))
            .andExpect(jsonPath("$.fechaRecogida").value(HOY.toString()))
            .andExpect(jsonPath("$.fechaDevolucion").value(HOY.plusDays(1).toString()))
        assertEquals(reservaRepository.getElementos().size, 1)
    }

    @Test
    fun `La reserva falla si el libro no existe`() {
        usuarioRepository.create(usuarioLector)
        usuarioRepository.create(usuarioPrestador)

        val reserva = crearCreacionReservaDTO(usuarioLectorId = 1, usuarioPrestadorId = 2, libroId = 1)

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "No existe un libro con el id 1")
    }

    @Test
    fun `La reserva falla si el usuario prestador no existe`() {
        usuarioRepository.create(usuarioLector)
        libroRepository.create(libro)

        val reserva = crearCreacionReservaDTO(usuarioLectorId = 1, usuarioPrestadorId = 2, libroId = 1)

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "No se encontró usuario con id 2")
    }

    @Test
    fun `La reserva falla si el usuario lector no existe`() {
        usuarioRepository.create(usuarioPrestador)
        libroRepository.create(libro)

        val reserva = crearCreacionReservaDTO(usuarioLectorId = 1, usuarioPrestadorId = 2, libroId = 1)

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "No se encontró usuario con id 2")
    }

    @Test
    fun `La reserva falla si se usan datos no permitidos`() {
        usuarioRepository.create(usuarioLector)
        usuarioRepository.create(usuarioPrestador)
        libroRepository.create(libro)

        val reserva = crearCreacionReservaDTO(usuarioLectorId = 1, usuarioPrestadorId = 2, libroId = 1, fechaRecogida = HOY.minusDays(1))

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(errorMessage, "No se puede realizar una reserva para una fecha anterior a la actual")
    }
}

private fun crearCreacionReservaDTO(
    libroId: Int = 1,
    usuarioPrestadorId: Int = 1,
    usuarioLectorId: Int = 2,
    fechaRecogida: LocalDate = HOY,
    fechaDevolucion: LocalDate = HOY.plusDays(1)
): CreacionReservaDTO {
    return CreacionReservaDTO(
        libroId = libroId,
        usuarioPrestadorId = usuarioPrestadorId,
        usuarioLectorId = usuarioLectorId,
        fechaRecogida = fechaRecogida,
        fechaDevolucion = fechaDevolucion
    )
}
