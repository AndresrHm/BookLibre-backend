package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.errors.BusinessException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import java.time.LocalDate

class CalificacionSpec : DescribeSpec({
    describe("Test de Calificacion") {

        it("calificación se crea correctamente con datos válidos") {
            println(crearCalificacion())
            shouldNotThrow<BusinessException> {
                crearCalificacion()
            }
        }

        it("calificación no se crea si usuarioID < 0") {
            shouldThrow<BusinessException> {
                crearCalificacion(usuarioID = -1)
            }
        }

        it("calificación no se crea si nombreUsuario está vacío") {
            shouldThrow<BusinessException> {
                crearCalificacion(nombreUsuario = "")
            }
        }

        it("calificación no se crea si libroID < 0") {
            shouldThrow<BusinessException> {
                crearCalificacion(libroID = -1)
            }
        }

        it("calificación no se crea si puntuación no está entre 0.0 y 5.0") {
            shouldThrow<BusinessException> {
                crearCalificacion(puntuacion = 10.00)
            }
        }

        it("calificación no se crea si comentario tiene más de 500 caracteres") {
            shouldThrow<BusinessException> {
                crearCalificacion(
                    comentario = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus"
                )
            }
        }
    }
})

fun crearCalificacion(
    usuarioID: Int = 1,
    nombreUsuario: String = "Juan",
    imagenUsuario: String = "/imagen/juan",
    libroID: Int = 10,
    puntuacion: Double = 4.5,
    comentario: String = "Excelente libro, lo volvería a leer!",
    fecha: LocalDate = LocalDate.now(),
): Calificacion {
    return Calificacion(
        usuarioID,
        nombreUsuario,
        imagenUsuario,
        libroID,
        puntuacion,
        comentario,
        fecha,
    )
}