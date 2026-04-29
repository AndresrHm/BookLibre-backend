package ar.edu.unsam.phm.service

import FechasReserva
import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import java.util.*

class ReservaServiceSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    val reservaRepository = mockk<ReservaRepository>(relaxed = true)
    val usuarioRepository = mockk<UsuarioRepository>(relaxed = true)
    val libroRepository = mockk<LibroRepository>(relaxed = true)

    val service = ReservaService(reservaRepository, usuarioRepository, libroRepository)

    val idLibro = 1L
    val idPrestador = 2L
    val idLector = 3L

    val libro = crearLibro(idPropietario = idPrestador).apply { id = idLibro }
    val lector = crearUsuario().apply {
        id = idLector
        roles = mutableSetOf(crearRol(nameCreacion = "LECTOR"))
    }
    val prestador = crearUsuario().apply {
        id = idPrestador
        roles = mutableSetOf(
            crearRol(nameCreacion = "LECTOR"),
            crearRol(nameCreacion = "PRESTADOR")
        )
    }

    describe("ReservaService: crearReserva") {

        it("lanza excepción si el libro no existe") {
            every { libroRepository.findById(any()) } returns Optional.empty()
            shouldThrow<BusinessException> {
                service.crearReserva(99L, lector, prestador, LocalDate.now(), LocalDate.now().plusDays(1))
            }
        }

        it("lanza excepción si el libro no pertenece al prestador") {
            val prestadorFalso = crearUsuario(id = 999L)
            every { libroRepository.findById(idLibro) } returns Optional.of(libro)

            val exception = shouldThrow<BusinessException> {
                service.crearReserva(idLibro, lector, prestadorFalso, LocalDate.now(), LocalDate.now().plusDays(1))
            }
            exception.message shouldBe "El libro no pertenece al usuario prestador"
        }

        it("lanza excepción si el lector intenta reservar su propio libro") {
            every { libroRepository.findById(idLibro) } returns Optional.of(libro)
            every { libroRepository.existsById(idLibro) } returns true
            every { reservaRepository.findFechasByLibroId(idLibro) } returns emptyList()

            val exception = shouldThrow<BusinessException> {
                service.crearReserva(idLibro, prestador, prestador, LocalDate.now(), LocalDate.now().plusDays(1))
            }
            exception.message shouldBe "Un usuario no puede reservar un libro que le pertenece"
        }

        it("lanza excepción si hay solapamiento") {
            every { libroRepository.findById(idLibro) } returns Optional.of(libro)
            every { libroRepository.existsById(idLibro) } returns true

            val reservaPrevia = mockk<FechasReserva>()
            every { reservaPrevia.fechaRecogida } returns LocalDate.now().plusDays(10)
            every { reservaPrevia.fechaDevolucion } returns LocalDate.now().plusDays(15)
            every { reservaRepository.findFechasByLibroId(idLibro) } returns listOf(reservaPrevia)

            shouldThrow<BusinessException> {
                service.crearReserva(idLibro, lector, prestador, LocalDate.now().plusDays(12), LocalDate.now().plusDays(14))
            }
        }

        it("camino feliz: crea la reserva correctamente") {
            every { libroRepository.findById(idLibro) } returns Optional.of(libro)
            every { libroRepository.existsById(idLibro) } returns true
            every { reservaRepository.findFechasByLibroId(idLibro) } returns emptyList()
            every { reservaRepository.countByLibroId(idLibro) } returns 5

            every { usuarioRepository.save(any<Usuario>()) } returns lector
            every { reservaRepository.save(any<Reserva>()) } returns mockk()

            val resultado = service.crearReserva(idLibro, lector, prestador, LocalDate.now(), LocalDate.now().plusDays(1))

            resultado.libro.id shouldBe idLibro
            verify(exactly = 1) { reservaRepository.save(any()) }
        }
    }

    describe("ReservaService: Otros métodos") {
        it("obtenerFechasDeReservasDeLibro lanza NotFound si el libro no existe") {
            every { libroRepository.existsById(any()) } returns false
            shouldThrow<NotFoundException> {
                service.obtenerFechasDeReservasDeLibro(99L)
            }
        }
    }
})