package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Rol
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.crearLibro
import ar.edu.unsam.phm.dto.GetLibroDTO
import ar.edu.unsam.phm.dto.LibroEdicionDTO
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
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class LibroControllerTest(@Autowired val mockMvc: MockMvc) {
    @Autowired
    private lateinit var libroRepository: LibroRepository

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var reservaRepository: ReservaRepository

    @Autowired
    private lateinit var calificacionRepository: CalificacionRepository

    @Autowired
    private lateinit var rolRepository: RolRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private var usuarioPerfilId: Long = 0
    private var usuarioNoPropietarioId: Long = 0
    private var libro1Id: String = ""

    @BeforeEach
    fun init() {
        calificacionRepository.deleteAll()
        reservaRepository.deleteAll()
        libroRepository.deleteAll()
        usuarioRepository.deleteAll()
        rolRepository.deleteAll()

        val rolLector = rolRepository.save(Rol().apply { name = TipoUsuario.LECTOR.roleName })
        val rolPublicador = rolRepository.save(Rol().apply { name = TipoUsuario.PUBLICADOR.roleName })

        val usuarioGuardado = usuarioRepository.save(
            Usuario(
                nombre = "Usuario",
                apellido = "Perfil",
                descripcion = "Desc",
                email = "perfil@test.com",
                celular = "11223344",
                ciudad = "CABA",
                roles = mutableSetOf(rolLector, rolPublicador),
                password = "123",
                urlImagen = "img.jpg"
            )
        )
        usuarioPerfilId = usuarioGuardado.id!!

        val otroUsuario = usuarioRepository.save(
            Usuario(
                nombre = "Otro",
                apellido = "Usuario",
                descripcion = "Desc",
                email = "otro@test.com",
                celular = "11999999",
                ciudad = "CABA",
                roles = mutableSetOf(rolLector, rolPublicador),
                password = "123",
                urlImagen = "img2.jpg"
            )
        )
        usuarioNoPropietarioId = otroUsuario.id!!

        libro1Id = libroRepository.save(crearLibro(idPropietario = usuarioPerfilId)).id
        libroRepository.save(crearLibro(titulo = "Segundo libro", idPropietario = usuarioPerfilId))
        libroRepository.save(crearLibro(titulo = "Tercer libro", genero = Genero.ROMANCE, idPropietario = usuarioPerfilId))
    }

    @Test
    fun `Se puede crear un libro`() {
        val libroValido = crearLibroDTO(idPropietario = usuarioPerfilId)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/libros")
                .principal(authPrincipal(usuarioPerfilId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroValido))
        ).andExpect(status().isCreated)
    }

    @Test
    fun `No se puede crear un libro que no pase las validaciones`() {
        val libroInvalido = crearLibroDTO(titulo = "", idPropietario = usuarioPerfilId)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/libros")
                .principal(authPrincipal(usuarioPerfilId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroInvalido))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `Obtener detalle devuelve el libro y campos de ranking`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/libros/{libroId}/{tipoLibroDTO}", libro1Id, GetLibroDTO.DETALLE)
                .principal(authPrincipal(usuarioPerfilId))
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.tipo").value("DETALLE"))
            .andExpect(jsonPath("$.libro.titulo").value("Título de libro"))
            .andExpect(jsonPath("$.libro.rating").exists())
    }

    @Test
    fun `Obtener detalle de libro inexistente devuelve 404`() {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/libros/{libroId}/{tipoLibroDTO}", "-1", GetLibroDTO.DETALLE)
                .principal(authPrincipal(usuarioPerfilId))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `Obtener libro para edicion`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/libros/{libroId}/{tipoLibroDTO}", libro1Id, GetLibroDTO.EDICION)
                .principal(authPrincipal(usuarioPerfilId))
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.libro.titulo").value("Título de libro"))
    }

    @Test
    fun `Se puede editar un libro`() {
        val libroEditadoDTO = crearLibroDTO(id = libro1Id, titulo = "Editado", idPropietario = usuarioPerfilId)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/libros")
                .principal(authPrincipal(usuarioPerfilId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroEditadoDTO))
        ).andExpect(status().isOk)
    }

    @Test
    fun `No se puede editar si no es el propietario`() {
        val libroDTO = crearLibroDTO(id = libro1Id, idPropietario = usuarioPerfilId)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/libros")
                .principal(authPrincipal(usuarioNoPropietarioId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(libroDTO))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `Traer libros del perfil con filtros y orden`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/libros")
                .principal(authPrincipal(usuarioPerfilId))
                .param("flagDiff", "PERFIL")
                .param("userId", usuarioPerfilId.toString())
                .param("disponibilidadLibrosPerfil", "DISPONIBLES")
                .param("ordenPerfilPage", "DISPONIBLE_PRESTADO")
                .param("direccionPerfilPage", "ASCENDENTE")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].tipo").value("PERFIL"))
    }

    @Test
    fun `Traer libros para home con filtros de busqueda y karma`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/libros")
                .principal(authPrincipal(usuarioNoPropietarioId))
                .param("flagDiff", "HOME")
                .param("search", "Segundo")
                .param("usuarioPrestador", "Usuario")
                .param("ordenHomePage", "NOMBRE_ASC")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].tipo").value("HOME"))
            .andExpect(jsonPath("$.content[0].libro.biblioKarma").exists())
    }

    @Test
    fun `Traer libros filtrando por ID de prestador y lector`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/libros")
                .principal(authPrincipal(usuarioPerfilId))
                .param("flagDiff", "HOME")
                .param("usuarioPrestadorId", usuarioPerfilId.toString())
                .param("usuarioLectorId", usuarioNoPropietarioId.toString())
        ).andExpect(status().isOk)
    }

    @Test
    fun `Obtener nombres de prestadores disponibles`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/libros/prestadoresDisponibles")
                .principal(authPrincipal(usuarioNoPropietarioId))
                .param("search", "Usu")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0]").value("Usuario"))
    }

    @Test
    fun `Borrado logico de libro exitoso`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/libros/$libro1Id")
                .principal(authPrincipal(usuarioPerfilId))
        ).andExpect(status().isOk)

        assertTrue(!libroRepository.findById(libro1Id).get().activo)
    }

    @Test
    fun `Lector no puede pedir libro para edicion`() {
        val authLector = UsernamePasswordAuthenticationToken(
            usuarioNoPropietarioId.toString(),
            null,
            listOf(SimpleGrantedAuthority(TipoUsuario.LECTOR.roleName))
        )
        mockMvc.perform(
            MockMvcRequestBuilders.get("/libros/{id}/EDICION", libro1Id)
                .principal(authLector)
        ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("No tienes permisos para editar libros"))
    }

    @Test
    fun `Error al solicitar tipo de DTO invalido`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/libros/{id}/INVALIDO", libro1Id)
                .principal(authPrincipal(usuarioPerfilId))
        ).andExpect(status().isBadRequest)
    }
}

private fun authPrincipal(usuarioId: Long): UsernamePasswordAuthenticationToken {
    return UsernamePasswordAuthenticationToken(
        usuarioId.toString(),
        null,
        listOf(
            SimpleGrantedAuthority(TipoUsuario.LECTOR.roleName),
            SimpleGrantedAuthority(TipoUsuario.PUBLICADOR.roleName)
        )
    )
}

fun crearLibroDTO(
    id: String = "1",
    titulo: String = "Título de libro",
    idPropietario: Long = 0
) = LibroEdicionDTO(
    id = id,
    imagenUrl = "url",
    titulo = titulo,
    tipoLibro = "comun",
    genero = Genero.CIENCIA_FICCION,
    autor = "Autor",
    idioma = Idioma.ESPANOL,
    estado = EstadoLibro.BUENO,
    editorial = "Ed",
    fechaPublicacion = LocalDate.now(),
    cantidadPaginas = 100,
    isbn13 = 1111111111111L,
    descripcion = "Desc",
    idPropietario = idPropietario
)