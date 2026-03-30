package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.crearUsuario
import ar.edu.unsam.phm.errors.ErrorStrings
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
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    val usuario1 = crearUsuario(id = 1, nombre = "Juan", apellido = "Pérez", tipoUsuario = TipoUsuario.LECTOR)
    val usuario2 = crearUsuario(id = 2, nombre = "María", apellido = "González", tipoUsuario = TipoUsuario.PUBLICADOR)
    val usuario3 = crearUsuario(id = 3, nombre = "Carlos", apellido = "López", tipoUsuario = TipoUsuario.LECTORPUBLICADOR)

    @BeforeEach
    fun init() {
        usuarioRepository.clearInit()
        usuario1.biblioKarma = 50
        usuario2.biblioKarma = 150
        usuario3.biblioKarma = 300
        usuarioRepository.create(usuario1)
        usuarioRepository.create(usuario2)
        usuarioRepository.create(usuario3)
    }

    // ----- Obtener información del header -----
    @Test
    fun `Se obtiene la información del header del usuario`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/{usuarioId}/header", 1)
        ).andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.urlImagen").value("imagenUsuario"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.biblioKarma").value("50"))
    }

    @Test
    fun `No se encuentra información del header si el usuario no existe`() {
        val usuarioIdInexistente = -1
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/{usuarioId}/header", usuarioIdInexistente)
        ).andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.usuarioNoEncontradoPorId(usuarioIdInexistente), errorMessage)
    }

    // ----- Obtener perfil del usuario -----
    @Test
    fun `Se obtiene el perfil completo del usuario`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/{usuarioId}/perfil", 2)
        ).andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value("María"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.apellido").value("González"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.tipoUsuario").value("PUBLICADOR"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.biblioKarma").value("150"))
    }

    @Test
    fun `No se encuentra el perfil si el usuario no existe`() {
        val usuarioIdInexistente = -1
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/{usuarioId}/perfil", usuarioIdInexistente)
        ).andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.usuarioNoEncontradoPorId(usuarioIdInexistente), errorMessage)
    }

    // ----- Obtener tipo de usuario -----
    @Test
    fun `Se obtiene el tipo de usuario (LECTOR)`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/{usuarioId}/tipoUsuario", 1)
        ).andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").value("LECTOR"))
    }

    @Test
    fun `Se obtiene el tipo de usuario (PUBLICADOR)`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/{usuarioId}/tipoUsuario", 2)
        ).andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").value("PUBLICADOR"))
    }

    @Test
    fun `Se obtiene el tipo de usuario (LECTORPUBLICADOR)`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/{usuarioId}/tipoUsuario", 3)
        ).andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").value("LECTORPUBLICADOR"))
    }

    @Test
    fun `No se encuentra el tipo de usuario si el usuario no existe`() {
        val usuarioIdInexistente = -1
        val errorMessage = mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/{usuarioId}/tipoUsuario", usuarioIdInexistente)
        ).andExpect(status().isNotFound)
            .andReturn().resolvedException?.message

        assertEquals(ErrorStrings.usuarioNoEncontradoPorId(usuarioIdInexistente), errorMessage)
    }
}
