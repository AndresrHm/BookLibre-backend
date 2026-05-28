package ar.edu.unsam.phm.service

import FechasReserva
import ar.edu.unsam.phm.errors.BusinessException
import ar.edu.unsam.phm.errors.NotFoundException
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

    val idLibro = "1"
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
                service.crearReserva("99", lector, prestador, LocalDate.now(), LocalDate.now().plusDays(1))
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

            val exception = shouldThrow<BusinessException> {
                service.crearReserva(idLibro, prestador, prestador, LocalDate.now(), LocalDate.now().plusDays(1))
            }
            exception.message shouldBe "Un usuario no puede reservar un libro que le pertenece"
        }

        it("lanza excepción si hay solapamiento") {
            // Configurar el libro real con una fecha que sea envuelta por la nueva reserva
            libro.fechasReserva = mutableListOf(
                FechaReserva(
                    fechaInicio = LocalDate.now().plusDays(12),
                    fechaFin = LocalDate.now().plusDays(14)
                )
            )

            every { libroRepository.findById(idLibro) } returns Optional.of(libro)
            every { libroRepository.existsById(idLibro) } returns true

            // Enviamos un rango que envuelva por completo a la reserva existente (del día 10 al 15)
            val exception = shouldThrow<BusinessException> {
                service.crearReserva(
                    idLibro,
                    lector,
                    prestador,
                    LocalDate.now().plusDays(10),
                    LocalDate.now().plusDays(15)
                )
            }
            exception.message shouldBe "Existe una reserva en el rango de fechas estipulado"
        }

        it("camino feliz: crea la reserva correctamente") {
            every { libroRepository.findById(idLibro) } returns Optional.of(libro)
            every { libroRepository.existsById(idLibro) } returns true
            every { reservaRepository.countByLibroId(idLibro) } returns 5

            every { usuarioRepository.save(any<Usuario>()) } returns lector
            every { libroRepository.save(any<Libro>()) } returns libro
            every { reservaRepository.save(any<Reserva>()) } returnsArgument 0

            val resultado = service.crearReserva(idLibro, lector, prestador, LocalDate.now(), LocalDate.now().plusDays(1))

            resultado.libroId shouldBe idLibro
            verify(exactly = 1) { reservaRepository.save(any()) }
        }

        it("al crear una reserva limpia las fechas pasadas antes de agregar la nueva") {
            // preparar libro con una fecha pasada y una futura
            val pasada = FechaReserva(LocalDate.now().minusDays(5), LocalDate.now().minusDays(1))
            val futura = FechaReserva(LocalDate.now().plusDays(2), LocalDate.now().plusDays(3))
            libro.fechasReserva = mutableListOf(pasada, futura)

            every { libroRepository.findById(idLibro) } returns Optional.of(libro)
            every { libroRepository.existsById(idLibro) } returns true
            every { reservaRepository.countByLibroId(idLibro) } returns 0

            every { usuarioRepository.save(any<Usuario>()) } returns lector
            every { reservaRepository.save(any<Reserva>()) } returns mockk()
            every { libroRepository.save(any<Libro>()) } returns libro

            val fechaInicio = LocalDate.now()
            val fechaFin = LocalDate.now().plusDays(2)

            service.crearReserva(idLibro, lector, prestador, fechaInicio, fechaFin)

            // la reserva pasada debe haberse eliminado
            libro.fechasReserva.any { it.fechaFin.isBefore(LocalDate.now()) } shouldBe false

            // la nueva reserva debe agregarse al inicio
            libro.fechasReserva.first().fechaInicio shouldBe fechaInicio
            libro.fechasReserva.first().fechaFin shouldBe fechaFin

            // la futura original debe permanecer
            libro.fechasReserva.any { it == futura } shouldBe true
        }
    }
})