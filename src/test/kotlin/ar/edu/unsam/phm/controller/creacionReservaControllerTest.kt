package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.crearLibro
import ar.edu.unsam.phm.domain.crearUsuario
import ar.edu.unsam.phm.dto.CreacionReservaDTO
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.CalificacionRepository
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
    private lateinit var calificacionRepository: CalificacionRepository

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
    lateinit var libro: Libro

    @BeforeEach
    fun init() {
        calificacionRepository.deleteAll()
        reservaRepository.deleteAll()
        libroRepository.deleteAll()
        usuarioRepository.deleteAll()

        usuarioRepository.save(usuarioLector)
        usuarioRepository.save(usuarioPrestador)
        libro = crearLibro(idPropietario = usuarioPrestador.id!!, titulo = "Título")
        libroRepository.save(libro)
    }

    // Tests de creación de reservas
    @Test
    fun `Se puede reservar un libro`() {
        val reserva = crearCreacionReservaDTO(
            usuarioLectorId = usuarioLector.id!!,
            usuarioPrestadorId = usuarioPrestador.id!!,
            libroId = libro.id!!
        )

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
        libroRepository.deleteAll()

        val reserva = crearCreacionReservaDTO(usuarioLectorId = 1, usuarioPrestadorId = 2, libroId = 1)

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.usuarioNoEncontradoPorId(1), errorMessage)
    }

    @Test
    fun `La reserva falla si el usuario prestador no existe`() {
        usuarioRepository.delete(usuarioPrestador)

        val reserva = crearCreacionReservaDTO(usuarioLectorId = 1, usuarioPrestadorId = 2, libroId = 1)

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.usuarioNoEncontradoPorId(1), errorMessage)
    }

    @Test
    fun `La reserva falla si el usuario lector no existe`() {
        usuarioRepository.delete(usuarioLector)

        val reserva = crearCreacionReservaDTO(usuarioLectorId = 1, usuarioPrestadorId = 2, libroId = 1)

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.usuarioNoEncontradoPorId(1), errorMessage)
    }

    @Test
    fun `La reserva falla si se usan datos no permitidos`() {
        val reserva = crearCreacionReservaDTO(
            usuarioLectorId = usuarioLector.id!!,
            usuarioPrestadorId = usuarioPrestador.id!!,
            libroId = libro.id!!,
            fechaRecogida = HOY.minusDays(1)
        )

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
