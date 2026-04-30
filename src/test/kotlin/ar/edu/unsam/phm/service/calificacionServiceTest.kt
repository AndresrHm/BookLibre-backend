package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.errors.BusinessException
import ar.edu.unsam.phm.errors.NotFoundException
import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.dto.CrearCalificacionDTO
import ar.edu.unsam.phm.dto.OrdenCalificacion
import ar.edu.unsam.phm.repository.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    @Test
    fun `calificarLibro lanza NotFoundException si el usuario no existe`() {
        assertThrows<NotFoundException> {
            calificacionService.calificarLibro(-1L, CrearCalificacionDTO(reservaFinalizada.id!!, "Comentario", 4.0))
        }
    }

    @Test
    fun `calificarLibro lanza BusinessException si el usuario no es lector`() {
        val noLector = usuarioRepository.save(Usuario(
            nombre = "NoLector", apellido = "NL", email = "nl@t.com",
            roles = mutableSetOf(), password = "123",
            descripcion = "D", celular = "1", ciudad = "C", urlImagen = "u"
        ))
        assertThrows<BusinessException> {
            calificacionService.calificarLibro(noLector.id!!, CrearCalificacionDTO(reservaFinalizada.id!!, "Comentario", 4.0))
        }
    }

    @Test
    fun `calificarLibro lanza NotFoundException si la reserva no existe`() {
        assertThrows<NotFoundException> {
            calificacionService.calificarLibro(lector.id!!, CrearCalificacionDTO(-1L, "Comentario", 4.0))
        }
    }

    @Test
    fun `calificarLibro lanza BusinessException si el usuario no es el lector de la reserva`() {
        assertThrows<BusinessException> {
            calificacionService.calificarLibro(prestador.id!!, CrearCalificacionDTO(reservaFinalizada.id!!, "Comentario", 4.0))
        }
    }

    @Test
    fun `obtenerCalificaciones devuelve resultados con orden DATE_ASC`() {
        calificacionService.calificarLibro(lector.id!!, CrearCalificacionDTO(reservaFinalizada.id!!, "Comentario", 4.0))
        val resultado = calificacionService.obtenerCalificaciones(libro.id!!, OrdenCalificacion.DATE_ASC, 0, 10)
        assertEquals(1, resultado.totalElements)
    }

    @Test
    fun `obtenerCalificaciones devuelve resultados con orden DATE_DESC`() {
        calificacionService.calificarLibro(lector.id!!, CrearCalificacionDTO(reservaFinalizada.id!!, "Comentario", 4.0))
        val resultado = calificacionService.obtenerCalificaciones(libro.id!!, OrdenCalificacion.DATE_DESC, 0, 10)
        assertEquals(1, resultado.totalElements)
    }

    @Test
    fun `obtenerCalificaciones devuelve resultados con orden RATING_ASC`() {
        calificacionService.calificarLibro(lector.id!!, CrearCalificacionDTO(reservaFinalizada.id!!, "Comentario", 4.0))
        val resultado = calificacionService.obtenerCalificaciones(libro.id!!, OrdenCalificacion.RATING_ASC, 0, 10)
        assertEquals(1, resultado.totalElements)
    }

    @Test
    fun `obtenerCalificaciones devuelve resultados con orden RATING_DESC`() {
        calificacionService.calificarLibro(lector.id!!, CrearCalificacionDTO(reservaFinalizada.id!!, "Comentario", 4.0))
        val resultado = calificacionService.obtenerCalificaciones(libro.id!!, OrdenCalificacion.RATING_DESC, 0, 10)
        assertEquals(1, resultado.totalElements)
    }

    @Test
    fun `obtenerCalificaciones lanza NotFoundException si el libro no existe`() {
        assertThrows<NotFoundException> {
            calificacionService.obtenerCalificaciones(-1L, OrdenCalificacion.DATE_ASC, 0, 10)
        }
    }

    @Test
    fun `ultimasCalificaciones devuelve las ultimas calificaciones del libro`() {
        calificacionService.calificarLibro(lector.id!!, CrearCalificacionDTO(reservaFinalizada.id!!, "Muy bueno", 5.0))
        val resultado = calificacionService.ultimasCalificaciones(libro.id!!)
        assertEquals(1, resultado.size)
    }

    @Test
    fun `ultimasCalificaciones lanza NotFoundException si el libro no existe`() {
        assertThrows<NotFoundException> {
            calificacionService.ultimasCalificaciones(-1L)
        }
    }
}