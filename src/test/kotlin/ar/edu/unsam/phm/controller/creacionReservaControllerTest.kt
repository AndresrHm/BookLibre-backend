package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.crearLibro
import ar.edu.unsam.phm.domain.crearUsuario
import ar.edu.unsam.phm.dto.CreacionReservaDTO
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
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
        libroRepository.deleteAll()
        usuarioRepository.deleteAll()
        reservaRepository.deleteAll()
    }

    // Tests de creación de reservas
    @Test
    fun `Se puede reservar un libro`() {
        usuarioRepository.save(usuarioLector)
        usuarioRepository.save(usuarioPrestador)
        libroRepository.save(libro)

        val reserva = crearCreacionReservaDTO(usuarioLectorId = 1, usuarioPrestadorId = 2, libroId = 1)

        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isCreated)
        assertEquals(reservaRepository.count(), 1)
    }

    @Test
    fun `La reserva falla si el libro no existe`() {
        usuarioRepository.save(usuarioLector)
        usuarioRepository.save(usuarioPrestador)

        val reserva = crearCreacionReservaDTO(usuarioLectorId = 1, usuarioPrestadorId = 2, libroId = 1)

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.libroNoEncontradoPorId(1), errorMessage)
    }

    @Test
    fun `La reserva falla si el usuario prestador no existe`() {
        usuarioRepository.save(usuarioLector)
        libroRepository.save(libro)

        val reserva = crearCreacionReservaDTO(usuarioLectorId = 1, usuarioPrestadorId = 2, libroId = 1)

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.usuarioNoEncontradoPorId(2), errorMessage)
    }

    @Test
    fun `La reserva falla si el usuario lector no existe`() {
        usuarioRepository.save(usuarioPrestador)
        libroRepository.save(libro)

        val reserva = crearCreacionReservaDTO(usuarioLectorId = 1, usuarioPrestadorId = 2, libroId = 1)

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.usuarioNoEncontradoPorId(2), errorMessage)
    }

    @Test
    fun `La reserva falla si se usan datos no permitidos`() {
        usuarioRepository.save(usuarioLector)
        usuarioRepository.save(usuarioPrestador)
        libroRepository.save(libro)

        val reserva = crearCreacionReservaDTO(usuarioLectorId = 1, usuarioPrestadorId = 2, libroId = 1, fechaRecogida = HOY.minusDays(1))

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.FECHA_RESERVA_ANTERIOR_A_HOY, errorMessage)
    }
}

private fun crearCreacionReservaDTO(
    libroId: Long = 1,
    usuarioPrestadorId: Long = 1,
    usuarioLectorId: Long = 2,
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
