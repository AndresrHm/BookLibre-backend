package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.dto.EstadoReserva
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class ReservaSpec : DescribeSpec({
    describe("Test de Reserva") {

        it("reserva se crea correctamente con datos válidos") {
            shouldNotThrow<BusinessException> {
                crearReserva()
            }
        }

        it("reserva no se crea si fechaRecogida es posterior a fechaDevolucion") {
            shouldThrow<BusinessException> {
                crearReserva(
                    fechaRecogida = LocalDate.now().plusDays(3),
                    fechaDevolucion = LocalDate.now()
                )
            }
        }

        it("reserva calcula correctamente su estado en base a las fechas (estado devuelto)") {
            val reserva = crearReserva(
                fechaRecogida = LocalDate.now().minusDays(3),
                fechaDevolucion = LocalDate.now().minusDays(2),
            )

            reserva.estado().shouldBe(EstadoReserva.DEVUELTO)
        }

        it("reserva calcula correctamente su estado en base a las fechas (estado próximo a vencer)") {
            val reserva = crearReserva(
                fechaRecogida = LocalDate.now(),
                fechaDevolucion = LocalDate.now().plusDays(1),
            )

            reserva.estado().shouldBe(EstadoReserva.PROXIMO_A_VENCER)
        }

        it("reserva calcula correctamente su estado en base a las fechas (estado activo)") {
            val reserva = crearReserva(
                fechaRecogida = LocalDate.now().minusDays(3),
                fechaDevolucion = LocalDate.now().plusDays(5),
            )

            reserva.estado().shouldBe(EstadoReserva.ACTIVO)
        }
    }
})


fun crearReserva(
    libro: Libro = ar.edu.unsam.phm.domain.libro,
    usuarioPrestador: Usuario = ar.edu.unsam.phm.domain.usuarioPrestador,
    usuarioLector: Usuario = ar.edu.unsam.phm.domain.usuarioLector,
    fechaRecogida: LocalDate = LocalDate.now().plusDays(7),
    fechaDevolucion: LocalDate = LocalDate.now().plusDays(14),
): Reserva {
    return Reserva(
        libro = libro,
        usuarioLector = usuarioLector,
        usuarioPrestador = usuarioPrestador,
        fechaRecogida = fechaRecogida,
        fechaDevolucion = fechaDevolucion,
    )
}

val libro = Libro(
    titulo = "Dune",
    tipoLibro = comun,
    descripcion = "Sci-fi clásica",
    portada = "https://covers.openlibrary.org/b/id/15092781-L.jpg",
    genero = Genero.CIENCIA_FICCION,
    autor = "Frank Herbert",
    cantidadPaginas = 412,
    isbn13 = 9780441172719L,
    idioma = Idioma.INGLES,
    editorial = "Chilton Books",
    fechaPublicacion = LocalDate.of(1965, 8, 1),
    estadoLibro = EstadoLibro.MUY_BUENO,
    idPropietario = 1
)

val usuarioPrestador = Usuario(
    id = 1,
    nombre = "Juan",
    apellido = "Perez",
    descripcion = "Lector de ciencia ficción",
    email = "juan@mail.com",
    celular = "111111",
    ciudad = "Buenos Aires",
    tipoUsuario = lectorPublicador,
    urlImagen = "https://ui-avatars.com/api/?name=Juan+Perez"
).apply { biblioKarma = 2000 }

val usuarioLector = Usuario(
    id = 2,
    nombre = "Ana",
    apellido = "Gomez",
    descripcion = "Fan de novelas",
    email = "ana@mail.com",
    celular = "222222",
    ciudad = "Buenos Aires",
    tipoUsuario = lectorPublicador,
    urlImagen = "https://ui-avatars.com/api/?name=Ana+Gomez"
).apply { biblioKarma = 4000 }
