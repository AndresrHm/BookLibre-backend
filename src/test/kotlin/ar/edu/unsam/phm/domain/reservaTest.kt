package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.controller.crearReserva
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class ReservaSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    val libro = crearLibro(cantidadPaginas = 100).apply { id = "libro-123" }
    val prestador = crearUsuario(nombre = "Prestador")
    val lector = crearUsuario(nombre = "Lector")

    val inicio = LocalDate.of(2026, 4, 1)
    val fin = LocalDate.of(2026, 4, 10)

    val reserva = crearReserva(libro, prestador, lector, inicio, fin)

    describe("Test de Reserva: Karma") {
        it("calcula el karma sumando los días de la reserva y el karma del libro") {
            // Días entre 1 y 10 = 9 días
            // 9 * 5 = 45
            // Si TipoLibro.COMUN (con usuario default) devuelve 200
            // Total = 245
            reserva.darKarma(libro, 1) shouldBe 245
        }
    }

    describe("Test de Reserva: Estado") {
        it("estado: es DEVUELTO si hoy es después de la fecha de devolución") {
            val hoy = fin.plusDays(1)
            reserva.estado(hoy) shouldBe EstadoReserva.DEVUELTO
        }

        it("estado: es PROXIMO_A_VENCER si faltan 3 días o menos") {
            // Hoy es el 8, la devolución el 10. (2 días de diferencia)
            val hoy = fin.minusDays(2)
            reserva.estado(hoy) shouldBe EstadoReserva.PROXIMO_A_VENCER
        }

        it("estado: es ACTIVO si faltan más de 3 días") {
            // Hoy es el 1, la devolución el 10. (9 días de diferencia)
            val hoy = inicio
            reserva.estado(hoy) shouldBe EstadoReserva.ACTIVO
        }
    }
})