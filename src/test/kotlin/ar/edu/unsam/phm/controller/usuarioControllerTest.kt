package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Rol
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.crearUsuario
import ar.edu.unsam.phm.repository.CalificacionRepository
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.RolRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest(@Autowired val mockMvc: MockMvc) {
    @Autowired
    private lateinit var libroRepository: LibroRepository

    @Autowired
    private lateinit var reservaRepository: ReservaRepository

    @Autowired
    private lateinit var calificacionRepository: CalificacionRepository

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var rolRepository: RolRepository

    private lateinit var rolLector: Rol
    private lateinit var rolPublicador: Rol

    private lateinit var usuario1: Usuario
    private lateinit var usuario2: Usuario

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

        usuario1 = crearUsuario(nombre = "Juan", apellido = "Perez", roles = mutableSetOf(rolLector, rolPublicador))
        usuario2 = crearUsuario(nombre = "Maria", apellido = "Gonzalez", roles = mutableSetOf(rolLector, rolPublicador))

        usuario1.biblioKarma = 50
        usuario2.biblioKarma = 150

        usuarioRepository.save(usuario1)
        usuarioRepository.save(usuario2)
    }

    @Test
    fun `Se obtiene la informacion del header del usuario autenticado`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/HEADER")
                .principal(UsernamePasswordAuthenticationToken(usuario1.id.toString(), null))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.usuario.id").value(usuario1.id.toString()))
            .andExpect(jsonPath("$.usuario.biblioKarma").value(50))
    }

    @Test
    fun `Se obtiene el perfil del usuario autenticado`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/PERFIL")
                .principal(UsernamePasswordAuthenticationToken(usuario2.id.toString(), null))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.usuario.id").value(usuario2.id.toString()))
            .andExpect(jsonPath("$.usuario.nombre").value("Maria"))
    }

    @Test
    fun `Se obtiene el tipo de usuario del usuario autenticado`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/TIPOUSUARIO")
                .principal(UsernamePasswordAuthenticationToken(usuario1.id.toString(), null))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.tipo").value("TIPOUSUARIO"))
    }

    @Test
    fun `Si el usuario autenticado no existe devuelve 404`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/HEADER")
                .principal(UsernamePasswordAuthenticationToken("-1", null))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `Se obtiene el DTO de edicion del usuario autenticado`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/UPDATE")
                .principal(UsernamePasswordAuthenticationToken(usuario1.id.toString(), null))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.usuario.nombre").value("Juan"))
            .andExpect(jsonPath("$.usuario.apellido").value("Perez"))
    }

    @Test
    fun `Se obtiene el karma del usuario autenticado`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/usuario/KARMA")
                .principal(UsernamePasswordAuthenticationToken(usuario1.id.toString(), null))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.usuario.biblioKarma").value(50))
    }

    @Test
    fun `Se actualiza el usuario autenticado correctamente`() {
        val body = """
            {
                "nombre": "Juan Actualizado",
                "apellido": "Perez",
                "descripcion": "Nueva descripcion",
                "ciudad": "Buenos Aires",
                "tipoUsuario": [
                    {"id": ${rolLector.id}, "name": "${rolLector.name}"},
                    {"id": ${rolPublicador.id}, "name": "${rolPublicador.name}"}
                ],
                "urlImagen": "http://imagen.com/foto.jpg"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/usuario")
                .principal(UsernamePasswordAuthenticationToken(usuario1.id.toString(), null))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isOk)
    }
}
