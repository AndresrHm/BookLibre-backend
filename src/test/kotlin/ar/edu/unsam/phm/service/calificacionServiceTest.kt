package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.dto.CrearCalificacionDTO
import ar.edu.unsam.phm.repository.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class CalificacionServiceTest {

    @Autowired lateinit var calificacionService: CalificacionService
    @Autowired lateinit var usuarioRepository: UsuarioRepository
    @Autowired lateinit var libroRepository: LibroRepository
    @Autowired lateinit var reservaRepository: ReservaRepository
    @Autowired lateinit var calificacionRepository: CalificacionRepository
    @Autowired lateinit var rolRepository: RolRepository

    private lateinit var lector: Usuario
    private lateinit var prestador: Usuario
    private lateinit var libro: Libro
    private lateinit var reservaFinalizada: Reserva

    @BeforeEach
    fun setup() {
        calificacionRepository.deleteAll()
        reservaRepository.deleteAll()
        libroRepository.deleteAll()
        usuarioRepository.deleteAll()
        rolRepository.deleteAll()

        val rolLector = rolRepository.save(Rol().apply { name = TipoUsuario.LECTOR.roleName })

        lector = usuarioRepository.save(Usuario(
            nombre = "Juan", apellido = "Lector", email = "j@t.com",
            roles = mutableSetOf(rolLector), password = "123",
            descripcion = "D", celular = "1", ciudad = "C", urlImagen = "u"
        ))

        prestador = usuarioRepository.save(Usuario(
            nombre = "Dueño", apellido = "D", email = "d@t.com",
            roles = mutableSetOf(rolLector), password = "123",
            descripcion = "D", celular = "1", ciudad = "C", urlImagen = "u"
        ))

        libro = libroRepository.save(crearLibro(idPropietario = prestador.id!!))

        reservaFinalizada = reservaRepository.save(Reserva(
            libro = libro,
            usuarioPrestador = prestador,
            usuarioLector = lector,
            fechaRecogida = LocalDate.now().minusDays(10),
            fechaDevolucion = LocalDate.now().minusDays(1)
        ))
    }

    @Test
    fun `Si ya existe una calificacion se actualiza correctamente`() {
        val dto1 = CrearCalificacionDTO(reservaFinalizada.id!!, "Primera", 4.0)
        calificacionService.calificarLibro(lector.id!!, dto1)

        val dto2 = CrearCalificacionDTO(reservaFinalizada.id!!, "Segunda", 2.0)
        calificacionService.calificarLibro(lector.id!!, dto2)

        val calif = calificacionRepository.findByLibroIdAndUsuarioId(libro.id!!, lector.id!!)

        assertNotNull(calif)
        assertEquals("Segunda", calif?.comentario)
        assertEquals(2.0, calif?.puntuacion)
        assertEquals(1, calificacionRepository.count())
    }

    @Test
    fun `No se puede calificar si la reserva no esta en estado DEVUELTO`() {
        val reservaActiva = reservaRepository.save(Reserva(
            libro = libro,
            usuarioPrestador = prestador,
            usuarioLector = lector,
            fechaRecogida = LocalDate.now(),
            fechaDevolucion = LocalDate.now().plusDays(5)
        ))

        val exception = org.junit.jupiter.api.assertThrows<BusinessException> {
            calificacionService.calificarLibro(lector.id!!, CrearCalificacionDTO(reservaActiva.id!!, "Falla", 4.0))
        }
    }

    @Test
    fun `El promedio del libro se actualiza segun las calificaciones`() {
        val dto = CrearCalificacionDTO(reservaFinalizada.id!!, "Muy bueno", 5.0)
        calificacionService.calificarLibro(lector.id!!, dto)

        val libroActualizado = libroRepository.findById(libro.id!!).get()
        assertEquals(5.0, libroActualizado.calificacion)
    }
}