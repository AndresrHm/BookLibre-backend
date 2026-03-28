package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.BusinessException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

val HOY = LocalDate.now()

class LibroSpec : DescribeSpec({
    describe(name = "Tests de Libros") {
        describe(name = "Creación de libros") {
            val libroValido = crearLibro()

            it("Un libro puede ser creado con normalidad") {
                libroValido.titulo shouldBe "Título de libro"

                shouldNotThrow<BusinessException> {
                    libroValido.init()
                }
            }

            it("Un libro no puede no tener título") {
                val libroSinTitulo = crearLibro(titulo = "")

                shouldThrow<BusinessException> {
                    libroSinTitulo.init()
                }
            }

            it("Un libro no puede no tener descripción") {
                val libroSinDescripcion = crearLibro(descripcion = "")

                shouldThrow<BusinessException> {
                    libroSinDescripcion.init()
                }
            }

            it("Un libro no puede tener una descripción excesivamente larga") {
                val libroSinDescripcion =
                    crearLibro(descripcion = "A".repeat(501))

                shouldThrow<BusinessException> {
                    libroSinDescripcion.init()
                }
            }

            it("Un libro no puede no tener autor") {
                val libroSinAutor = crearLibro(autor = "")

                shouldThrow<BusinessException> {
                    libroSinAutor.init()
                }
            }

            it("Un libro no puede tener menos de 1 página") {
                val libroSinPaginas = crearLibro(cantidadPaginas = -1)

                shouldThrow<BusinessException> {
                    libroSinPaginas.init()
                }
            }

            it("Un libro no puede no tener páginas") {
                val libroSinPaginas = crearLibro(cantidadPaginas = 0)

                shouldThrow<BusinessException> {
                    libroSinPaginas.init()
                }
            }

            it("El código ISBN13 de un libro debe contener 13 dígitos") {
                val libroISBN13Sin13 = crearLibro(isbn13 = 1)

                shouldThrow<BusinessException> {
                    libroISBN13Sin13.init()
                }
            }

            it("El código ISBN13 de un libro debe ser positivo") {
                val libroISBN13Negativo = crearLibro(isbn13 = -101010101010)

                shouldThrow<BusinessException> {
                    libroISBN13Negativo.init()
                }
            }

            it("Un libro no puede no tener editorial") {
                val libroSinEditorial = crearLibro(editorial = "")

                shouldThrow<BusinessException> {
                    libroSinEditorial.init()
                }
            }
        }

        describe(name = "Validación de calificación y reserva") {
            val libro = crearLibro()

            it("Si se actualiza el rating, aumenta la cantida de reseñas") {
                libro.rating().shouldBe(0.0)
                libro.cantidadResenias.shouldBe(0)

                libro.actualizarRating(1.0)

                libro.rating().shouldBe(1.0)
                libro.cantidadResenias.shouldBe(1)
            }

            it("La calificación de un libro debe ubicarse en cierto rango") {
                val libroSinResenias = crearLibro()
                libroSinResenias.rating().shouldBe(0.0)
                libroSinResenias.cantidadResenias.shouldBe(0)

                shouldThrow<BusinessException> { libroSinResenias.actualizarRating(-1.0) }
                shouldThrow<BusinessException> { libroSinResenias.actualizarRating(5.1) }

                libroSinResenias.rating().shouldBe(0.0)
                libroSinResenias.cantidadResenias.shouldBe(0)
            }
        }
    }
})

fun crearLibro(
    titulo: String = "Título de libro",
    tipoLibro: TipoLibro = comun,
    descripcion: String = "Descripción de libro",
    portada: String = "Portada de libro",
    genero: Genero = Genero.CIENCIA_FICCION,
    autor: String = "Autor de libro",
    cantidadPaginas: Int = 10,
    isbn13: Long = 1111111111111,
    idioma: Idioma = Idioma.ESPANOL,
    editorial: String = "Editorial de libro",
    fechaPublicacion: LocalDate = LocalDate.now(),
    estadoLibro: EstadoLibro = EstadoLibro.BUENO,
    idPropietario: Int = 0
): Libro {
    return Libro(
        titulo,
        tipoLibro,
        descripcion,
        portada,
        genero,
        autor,
        cantidadPaginas,
        isbn13,
        idioma,
        editorial,
        fechaPublicacion,
        estadoLibro,
        idPropietario
    )
}