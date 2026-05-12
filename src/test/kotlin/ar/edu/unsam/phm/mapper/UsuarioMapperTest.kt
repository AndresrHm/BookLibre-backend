package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Rol
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.crearUsuario
import ar.edu.unsam.phm.dto.RegisterRequestDTO
import ar.edu.unsam.phm.dto.UsuarioEdicionDTO
import ar.edu.unsam.phm.repository.CalificacionRepository
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.RolRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UsuarioMapperTest {

    @Autowired private lateinit var usuarioMapper: UsuarioMapper
    @Autowired private lateinit var usuarioRepository: UsuarioRepository
    @Autowired private lateinit var rolRepository: RolRepository
    @Autowired private lateinit var libroRepository: LibroRepository
    @Autowired private lateinit var reservaRepository: ReservaRepository
    @Autowired private lateinit var calificacionRepository: CalificacionRepository

    private lateinit var rolLector: Rol
    private lateinit var rolPublicador: Rol
    private lateinit var usuario: Usuario

    @BeforeEach
    fun setup() {
        calificacionRepository.deleteAll()
        reservaRepository.deleteAll()
        libroRepository.deleteAll()
        usuarioRepository.deleteAll()
        rolRepository.deleteAll()

        rolLector = rolRepository.save(Rol().apply { name = TipoUsuario.LECTOR.roleName })
        rolPublicador = rolRepository.save(Rol().apply { name = TipoUsuario.PUBLICADOR.roleName })
        usuario = usuarioRepository.save(
            crearUsuario(nombre = "Juan", apellido = "Perez", roles = mutableSetOf(rolLector, rolPublicador))
        )
    }

    @Test
    fun `fromEdicionDTO mapea correctamente el DTO a un Usuario`() {
        val dto = UsuarioEdicionDTO(
            nombre = "Juan Actualizado",
            apellido = "Perez Actualizado",
            descripcion = "Nueva descripcion",
            ciudad = "Buenos Aires",
            tipoUsuario = mutableSetOf(rolLector),
            urlImagen = "https://imagen.com/foto.jpg"
        )

        val resultado = usuarioMapper.fromEdicionDTO(dto, usuario)

        assertEquals("Juan Actualizado", resultado.nombre)
        assertEquals("Perez Actualizado", resultado.apellido)
        assertEquals("Nueva descripcion", resultado.descripcion)
        assertEquals("Buenos Aires", resultado.ciudad)
        assertEquals("https://imagen.com/foto.jpg", resultado.urlImagen)
        assertEquals(usuario.email, resultado.email)
        assertEquals(usuario.id, resultado.id)
        assertEquals(usuario.biblioKarma, resultado.biblioKarma)
    }

    @Test
    fun `fromRegisterRequestDTO mapea correctamente el DTO a un Usuario`() {
        val dto = RegisterRequestDTO(
            nombre = "Maria",
            apellido = "Garcia",
            email = "maria@test.com",
            celular = "1234567890",
            tipoUsuario = mutableSetOf(rolLector),
            ciudad = "Rosario",
            password = "password123"
        )

        val resultado = usuarioMapper.fromRegisterRequestDTO(dto)

        assertEquals("Maria", resultado.nombre)
        assertEquals("Garcia", resultado.apellido)
        assertEquals("maria@test.com", resultado.email)
        assertEquals("1234567890", resultado.celular)
        assertEquals("Rosario", resultado.ciudad)
        assertEquals("password123", resultado.password)
    }

    @Test
    fun `toEdicionDTO mapea correctamente el Usuario al DTO`() {
        val dto = usuarioMapper.toEdicionDTO(usuario)

        assertEquals(usuario.nombre, dto.nombre)
        assertEquals(usuario.apellido, dto.apellido)
        assertEquals(usuario.descripcion, dto.descripcion)
        assertEquals(usuario.ciudad, dto.ciudad)
        assertEquals(usuario.urlImagen, dto.urlImagen)
    }

    @Test
    fun `toKarmaDTO mapea correctamente el Usuario al DTO`() {
        val dto = usuarioMapper.toKarmaDTO(usuario)

        assertEquals(usuario.id, dto.id)
        assertEquals(usuario.biblioKarma, dto.biblioKarma)
    }
}
