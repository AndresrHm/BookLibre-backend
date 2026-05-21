package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.controller.crearReserva
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
class TipoLibroSpec : DescribeSpec({

    val libro = crearLibro(cantidadPaginas = 100).apply { id = "libro-123" }
    val usuarioPrestador = crearUsuario().apply{
        roles = mutableSetOf(crearRol(2, TipoUsuario.PUBLICADOR.roleName))
    }
    val usuarioAltaKarma = crearUsuario().apply{
        biblioKarma = 1001
        roles = mutableSetOf(crearRol())
    }
    val usuarioBajaKarma = crearUsuario().apply{
        biblioKarma = 999
        roles = mutableSetOf(crearRol())
    }

    describe("TipoLibro: COMUN") {
        it("debe usar coeficiente 5 si biblioKarma es mayor a 1000 (Branch: True)") {
            TipoLibro.COMUN.calcularKarma(libro, 1, usuarioAltaKarma) shouldBe 500 // 100 * 5
        }

        it("debe usar coeficiente 2 si biblioKarma es 1000 o menor (Branch: False)") {
            TipoLibro.COMUN.calcularKarma(libro, 1, usuarioBajaKarma) shouldBe 200 // 100 * 2
        }
    }

    describe("TipoLibro: CON_DEDICATORIA") {
        it("debe calcular karma fijo + proporcional a reservas") {
            // cantidadReservas * 10 + 200 -> 5 * 10 + 200 = 250
            TipoLibro.CON_DEDICATORIA.calcularKarma(libro, 5, usuarioBajaKarma) shouldBe 250
        }
    }

    describe("TipoLibro: COLECCIONABLE") {
        it("debe calcular karma usando ceil y paginas") {
            // ceil(biblioKarma / 5.0) + paginas
            // usuario 1001 / 5.0 = 200.2 -> ceil 201 + 100 = 301
            TipoLibro.COLECCIONABLE.calcularKarma(libro, 1, usuarioAltaKarma) shouldBe 301
        }
    }

    describe("TipoLibro: delegación darKarma") {
        it("debe delegar el cálculo correctamente desde una reserva") {
            // Prueba la llamada que entra por darKarma (usa la lógica de COMUN)
            TipoLibro.COMUN.darKarma(libro, usuarioBajaKarma, 1) shouldBe 200
        }
    }
})