package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.crearUsuario
import ar.edu.unsam.phm.dto.TipoDTOUsuario
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerTest(@Autowired val mockMvc: MockMvc) {
    @Autowired
    private lateinit var libroRepository: LibroRepository

    @Autowired
    private lateinit var reservaRepository: ReservaRepository

    @Autowired
    private lateinit var calificacionRepository: CalificacionRepository

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    val usuario1 =
        crearUsuario(nombre = "Juan", apellido = "Pérez", tipoUsuario = TipoUsuario.LECTOR)
    val usuario2 =
        crearUsuario(nombre = "María", apellido = "González", tipoUsuario = TipoUsuario.PUBLICADOR)
    val usuario3 =
        crearUsuario(nombre = "Carlos", apellido = "López", tipoUsuario = TipoUsuario.LECTORPUBLICADOR)

    var usuario1Id = 0L // Los seteo en el init porque el deleteAll() no reinicia el contador de ids
    var usuario2Id = 0L
    var usuario3Id = 0L

    val usuarioIdInexistente = -1L

    @BeforeEach
    fun init() {
        calificacionRepository.deleteAll()
        reservaRepository.deleteAll()
        libroRepository.deleteAll()
        usuarioRepository.deleteAll()
        usuario1.biblioKarma = 50
        usuario2.biblioKarma = 150
        usuario3.biblioKarma = 300
        usuarioRepository.save(usuario1)
        usuarioRepository.save(usuario2)
        usuarioRepository.save(usuario3)
        usuario1Id = usuario1.id!!
        usuario2Id = usuario2.id!!
        usuario3Id = usuario3.id!!
    }

    // ----- Obtener información del header -----
    @Test
    fun `Se obtiene la información del header del usuario`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/{usuarioId}/{tipoDTOUsuario}", usuario1Id, TipoDTOUsuario.HEADER)
        ).andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.usuario.id").value(usuario1Id.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.usuario.urlImagen").value("imagenUsuario"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.usuario.biblioKarma").value("50"))
    }

    @Test
    fun `No se encuentra información del header si el usuario no existe`() {
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.get(
                "/usuario/{usuarioId}/{tipoDTOUsuario}",
                usuarioIdInexistente,
                TipoDTOUsuario.HEADER
            )
        ).andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.usuarioNoEncontradoPorId(usuarioIdInexistente), errorMessage)
    }

    // ----- Obtener perfil del usuario -----
    @Test
    fun `Se obtiene el perfil completo del usuario`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/{usuarioId}/{tipoDTOUsuario}", usuario2Id, TipoDTOUsuario.PERFIL)
        ).andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.usuario.id").value(usuario2.id.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.usuario.nombre").value("María"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.usuario.apellido").value("González"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.usuario.tipoUsuario").value("PUBLICADOR"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.usuario.biblioKarma").value("150"))
    }

    @Test
    fun `No se encuentra el perfil si el usuario no existe`() {
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.get(
                "/usuario/{usuarioId}/{tipoDTOUsuario}",
                usuarioIdInexistente,
                TipoDTOUsuario.PERFIL
            )
        ).andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.usuarioNoEncontradoPorId(usuarioIdInexistente), errorMessage)
    }

    // ----- Obtener tipo de usuario -----
    @Test
    fun `Se obtiene el tipo de usuario (LECTOR)`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/{usuarioId}/{tipoDTOUsuario}", usuario1Id, TipoDTOUsuario.TIPOUSUARIO)
        ).andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.usuario").value("LECTOR"))
    }

    @Test
    fun `Se obtiene el tipo de usuario (PUBLICADOR)`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/{usuarioId}/{tipoDTOUsuario}", usuario2Id, TipoDTOUsuario.TIPOUSUARIO)
        ).andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.usuario").value("PUBLICADOR"))
    }

    @Test
    fun `Se obtiene el tipo de usuario (LECTORPUBLICADOR)`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/{usuarioId}/{tipoDTOUsuario}", usuario3Id, TipoDTOUsuario.TIPOUSUARIO)
        ).andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.usuario").value("LECTORPUBLICADOR"))
    }

    @Test
    fun `No se encuentra el tipo de usuario si el usuario no existe`() {
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.get(
                "/usuario/{usuarioId}/{tipoDTOUsuario}",
                usuarioIdInexistente,
                TipoDTOUsuario.TIPOUSUARIO
            )
        ).andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.usuarioNoEncontradoPorId(usuarioIdInexistente), errorMessage)
    }
}
