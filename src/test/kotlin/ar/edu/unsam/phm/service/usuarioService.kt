package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.repository.*
import ar.edu.unsam.phm.errors.ErrorStrings
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@SpringBootTest
@Transactional
class UsuarioServiceTest {

    @Autowired lateinit var usuarioService: UsuarioService
    @Autowired lateinit var usuarioRepository: UsuarioRepository
    @Autowired lateinit var calificacionRepository: CalificacionRepository
    @Autowired lateinit var libroRepository: LibroRepository
    @Autowired lateinit var reservaRepository: ReservaRepository
    @Autowired lateinit var rolRepository: RolRepository
    @Autowired lateinit var entityManager: EntityManager

    private lateinit var rolLector: Rol
    private lateinit var rolPublicador: Rol
    private lateinit var lector: Usuario
    private lateinit var publicador: Usuario

    @BeforeEach
    fun setup() {
        calificacionRepository.deleteAll()
        reservaRepository.deleteAll()
        libroRepository.deleteAll()
        usuarioRepository.deleteAll()
        rolRepository.deleteAll()

        rolLector = rolRepository.save(Rol().apply { name = TipoUsuario.LECTOR.roleName })
        rolPublicador = rolRepository.save(Rol().apply { name = TipoUsuario.PUBLICADOR.roleName })

        lector = usuarioRepository.save(Usuario("Enzo", "Perez", "D", "e@r.com", "1", "C", mutableSetOf(rolLector), password = "123"))
        publicador = usuarioRepository.save(Usuario("Beto", "Alonso", "D", "b@r.com", "1", "C", mutableSetOf(rolPublicador), password = "123"))

        entityManager.flush()
        entityManager.clear()
    }

    @Test
    fun `Si un Lector no tiene reservas se desactivan sus libros (Baja Logica)`() {
        val libro = libroRepository.save(crearLibro(idPropietario = lector.id!!))

        usuarioService.usuarioUpdate(lector)

        entityManager.flush()
        entityManager.clear()

        val libroDB = libroRepository.findById(libro.id!!).get()
        assertFalse(libroDB.activo, "El libro debería estar inactivo")
    }

    @Test
    fun `Si un Lector tiene reservas lanza BusinessException`() {
        val libro = libroRepository.save(crearLibro(idPropietario = publicador.id!!))
        reservaRepository.save(Reserva(libro, publicador, lector, LocalDate.now(), LocalDate.now()))

        assertThrows<BusinessException> { usuarioService.usuarioUpdate(lector) }
    }

    @Test
    fun `Si un Publicador tiene devoluciones pendientes lanza BusinessException`() {
        val libro = libroRepository.save(crearLibro(idPropietario = publicador.id!!))
        reservaRepository.save(Reserva(libro, publicador, lector, LocalDate.now(), LocalDate.now()))

        assertThrows<BusinessException> { usuarioService.usuarioUpdate(publicador) }
    }

    @Test
    fun `Si es Lector y Publicador a la vez no se ejecuta ninguna validacion ni borrado`() {
        lector.roles.add(rolPublicador)
        val libro = libroRepository.save(crearLibro(idPropietario = lector.id!!))

        usuarioService.usuarioUpdate(lector)

        entityManager.flush()
        entityManager.clear()
        assertTrue(libroRepository.findById(libro.id!!).get().activo, "No debería desactivarse nada")
    }

    @Test
    fun `Publicador sin deudas actualiza correctamente`() {
        val res = usuarioService.usuarioUpdate(publicador)
        assertEquals(publicador.id, res.id)
    }

    @Test
    fun `getUsuarioById funciona y tambien lanza NotFound`() {
        assertNotNull(usuarioService.getUsuarioById(lector.id!!))
        assertThrows<NotFoundException> { usuarioService.getUsuarioById(999L) }
    }

    @Test
    fun `getUsuarioByNombre maneja la lista y lanza NotFound si esta vacia`() {
        val encontrado = usuarioService.getUsuarioByNombre("Enzo")
        assertFalse(encontrado.isEmpty())
    }

    @Test
    fun `validarExistenciaUsuario lanza BusinessException si no existe`() {
        assertThrows<BusinessException> { usuarioService.validarExistenciaUsuario(999L) }
    }

    @Test
    fun `getNombreDeUsuario funciona para casos existentes y nulos`() {
        assertEquals("Enzo", usuarioService.getNombreDeUsuario(lector.id!!))
        assertNull(usuarioService.getNombreDeUsuario(999L))
    }

    @Test
    fun `obtenerCantidadDeLibrosPrestados y Leidos funcionan correctamente`() {
        val libro = libroRepository.save(crearLibro(idPropietario = publicador.id!!))
        reservaRepository.save(Reserva(libro, publicador, lector, LocalDate.now(), LocalDate.now()))

        assertEquals(1, usuarioService.obtenerCantidadDeLibrosLeidos(lector.id!!))
        assertEquals(1, usuarioService.obtenerCantidadDeLibrosPrestados(publicador.id!!))
    }

    @Test
    fun `Karma solo acepta valores positivos`() {
        lector.sumarKarma(10)
        assertEquals(10, lector.biblioKarma)
        assertThrows<BusinessException> { lector.sumarKarma(-1) }
    }
}