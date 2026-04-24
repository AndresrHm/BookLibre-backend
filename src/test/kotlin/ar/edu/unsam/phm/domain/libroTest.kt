package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.dto.LibroEdicionDTO
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import jakarta.validation.Validation
import java.time.LocalDate

val HOY = LocalDate.now()

class LibroSpec : DescribeSpec({
    val validator = Validation.buildDefaultValidatorFactory().validator

    describe(name = "Validaciones declarativas para edición de libros") {
        it("Un DTO de edición válido no produce errores") {
            val violations = validator.validate(crearLibroEdicionDTO())
            violations.isEmpty() shouldBe true
        }

        it("Un libro no puede no tener título") {
            val violations = validator.validate(crearLibroEdicionDTO(titulo = ""))
            violations.any { it.propertyPath.toString() == "titulo" } shouldBe true
        }

        it("Un libro no puede no tener descripción") {
            val violations = validator.validate(crearLibroEdicionDTO(descripcion = ""))
            violations.any { it.propertyPath.toString() == "descripcion" } shouldBe true
        }

        it("Un libro no puede tener una descripción excesivamente larga") {
            val violations = validator.validate(crearLibroEdicionDTO(descripcion = "A".repeat(501)))
            violations.any { it.propertyPath.toString() == "descripcion" } shouldBe true
        }

        it("Un libro no puede no tener autor") {
            val violations = validator.validate(crearLibroEdicionDTO(autor = ""))
            violations.any { it.propertyPath.toString() == "autor" } shouldBe true
        }

        it("Un libro no puede tener menos de 1 página") {
            val violations = validator.validate(crearLibroEdicionDTO(cantidadPaginas = -1))
            violations.any { it.propertyPath.toString() == "cantidadPaginas" } shouldBe true
        }

        it("Un libro no puede no tener páginas") {
            val violations = validator.validate(crearLibroEdicionDTO(cantidadPaginas = 0))
            violations.any { it.propertyPath.toString() == "cantidadPaginas" } shouldBe true
        }

        it("El código ISBN13 de un libro debe contener 13 dígitos") {
            val violations = validator.validate(crearLibroEdicionDTO(isbn13 = 1))
            violations.any { it.propertyPath.toString() == "isbn13" } shouldBe true
        }

        it("El código ISBN13 de un libro debe ser positivo") {
            val violations = validator.validate(crearLibroEdicionDTO(isbn13 = -101010101010))
            violations.any { it.propertyPath.toString() == "isbn13" } shouldBe true
        }

        it("Un libro no puede no tener editorial") {
            val violations = validator.validate(crearLibroEdicionDTO(editorial = ""))
            violations.any { it.propertyPath.toString() == "editorial" } shouldBe true
        }
    }
})

fun crearLibroEdicionDTO(
    id: Long = 1,
    titulo: String = "Título de libro",
    tipoLibro: String = "comun",
    descripcion: String = "Descripción de libro",
    imagenUrl: String = "Portada de libro",
    genero: Genero = Genero.CIENCIA_FICCION,
    autor: String = "Autor de libro",
    cantidadPaginas: Int = 10,
    isbn13: Long = 1111111111111,
    idioma: Idioma = Idioma.ESPANOL,
    editorial: String = "Editorial de libro",
    fechaPublicacion: LocalDate = LocalDate.now(),
    estado: EstadoLibro = EstadoLibro.BUENO,
    idPropietario: Long = 1
): LibroEdicionDTO =
    LibroEdicionDTO(
        id = id,
        imagenUrl = imagenUrl,
        titulo = titulo,
        tipoLibro = tipoLibro,
        genero = genero,
        autor = autor,
        idioma = idioma,
        estado = estado,
        editorial = editorial,
        fechaPublicacion = fechaPublicacion,
        cantidadPaginas = cantidadPaginas,
        isbn13 = isbn13,
        descripcion = descripcion,
        idPropietario = idPropietario
    )

fun crearLibro(
    titulo: String = "Título de libro",
    tipoLibro: TipoLibro = TipoLibro.COMUN,
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
    idPropietario: Long = 0
): Libro =
    Libro(
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
