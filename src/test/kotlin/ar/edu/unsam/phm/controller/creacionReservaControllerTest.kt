package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Rol
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.crearLibro
import ar.edu.unsam.phm.domain.crearUsuario
import ar.edu.unsam.phm.dto.CreacionReservaDTO
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.CalificacionRepository
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.RolRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals

val HOY = LocalDate.now()

//Lo moví a una clase aparte porque usa datos mockeados
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
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
    private lateinit var rolRepository: RolRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var usuarioLector: Usuario
    lateinit var usuarioPrestador: Usuario

    private lateinit var rolLector: Rol
    private lateinit var rolPublicador: Rol

    lateinit var libro: Libro

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

        usuarioLector = crearUsuario(nombre = "Lector", roles = mutableSetOf(rolLector))
        usuarioPrestador = crearUsuario(nombre = "Prestador", roles = mutableSetOf(rolLector))

        usuarioRepository.save(usuarioLector)
        usuarioRepository.save(usuarioPrestador)
        libro = crearLibro(idPropietario = usuarioPrestador.id!!, titulo = "Título")
        libroRepository.save(libro)
    }

    // Tests de creación de reservas
    @Test
    fun `Se puede reservar un libro`() {
        val reserva = crearCreacionReservaDTO(
            usuarioPrestadorId = usuarioPrestador.id!!,
            libroId = libro.id
        )

        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .principal(UsernamePasswordAuthenticationToken(usuarioLector.id.toString(), null))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isCreated)
        assertEquals(reservaRepository.count(), 1)
    }

    @Test
    fun `La reserva falla si el libro no existe`() {
        libroRepository.deleteAll()

        val libroIdInexistente = "-999"
        val reserva = crearCreacionReservaDTO(usuarioPrestadorId = usuarioPrestador.id!!, libroId = libroIdInexistente)

        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .principal(UsernamePasswordAuthenticationToken(usuarioLector.id.toString(), null))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `La reserva falla si el usuario prestador no existe`() {
        usuarioRepository.delete(usuarioPrestador)

        val reserva = crearCreacionReservaDTO(usuarioPrestadorId = 2, libroId = "1")

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .principal(UsernamePasswordAuthenticationToken(usuarioLector.id.toString(), null))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.usuarioNoEncontradoPorId(2), errorMessage)
    }

    @Test
    fun `La reserva falla si el usuario lector no existe`() {
        usuarioRepository.delete(usuarioLector)

        val reserva = crearCreacionReservaDTO(usuarioPrestadorId = 2, libroId = "1")

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .principal(UsernamePasswordAuthenticationToken(usuarioLector.id.toString(), null))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.usuarioNoEncontradoPorId(usuarioLector.id!!), errorMessage)
    }

    @Test
    fun `La reserva falla si se usan datos no permitidos`() {
        val reserva = crearCreacionReservaDTO(
            usuarioPrestadorId = usuarioPrestador.id!!,
            libroId = libro.id,
            fechaRecogida = HOY.minusDays(1)
        )

        val errorMessage = mockMvc
            .perform(
                MockMvcRequestBuilders.post("/reservas")
                    .principal(UsernamePasswordAuthenticationToken(usuarioLector.id.toString(), null))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reserva))
            )
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.FECHA_RESERVA_ANTERIOR_A_HOY, errorMessage)
    }
}

private fun crearCreacionReservaDTO(
    libroId: String = "1",
    usuarioPrestadorId: Long = 1,
    fechaRecogida: LocalDate = HOY,
    fechaDevolucion: LocalDate = HOY.plusDays(1)
): CreacionReservaDTO {
    return CreacionReservaDTO(
        libroId = libroId,
        usuarioPrestadorId = usuarioPrestadorId,
        fechaRecogida = fechaRecogida,
        fechaDevolucion = fechaDevolucion
    )
}
