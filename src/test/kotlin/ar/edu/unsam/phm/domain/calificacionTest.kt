package ar.edu.unsam.phm.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class CalificacionSpec : DescribeSpec({
    val libro = crearLibro()
    val usuario = crearUsuario()
    val fecha = LocalDate.now()

    describe("Calificacion: validación de data class y propiedades") {
        it("debería instanciar correctamente y permitir acceso a propiedades") {
            val calificacion = Calificacion(usuario, libro, 5.0, "Excelente", fecha)

            calificacion.usuario shouldBe usuario
            calificacion.libro shouldBe libro
            calificacion.puntuacion shouldBe 5.0
            calificacion.comentario shouldBe "Excelente"
            calificacion.fecha shouldBe fecha
        }

        it("debería permitir modificar propiedades mutables") {
            val calificacion = Calificacion(usuario, libro, 1.0, "Malo", fecha)
            calificacion.puntuacion = 4.0
            calificacion.comentario = "Mejoró"

            calificacion.puntuacion shouldBe 4.0
            calificacion.comentario shouldBe "Mejoró"
        }

        it("debería cumplir con equals y hashCode") {
            val cal1 = Calificacion(usuario, libro, 5.0, "Bien", fecha)
            val cal2 = Calificacion(usuario, libro, 5.0, "Bien", fecha)

            cal1 shouldBe cal2
            cal1.hashCode() shouldBe cal2.hashCode()
        }

        it("debería probar el método toString") {
            val calificacion = Calificacion(usuario, libro, 5.0, "Bien", fecha)
            calificacion.toString().contains("Calificacion") shouldBe true
        }

        it("debería probar el seteo del id (BaseEntity)") {
            val calificacion = Calificacion(usuario, libro, 5.0, "Bien", fecha)
            calificacion.id = 1L
            calificacion.id shouldBe 1L
        }
    }
})