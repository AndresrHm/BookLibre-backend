package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.BusinessException
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

        it("calificación no se crea si puntuación no está entre 0.5 y 5.0") {
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
    puntuacion: Double = 4.5,
    comentario: String = "Excelente libro, lo volvería a leer!",
    fecha: LocalDate = LocalDate.now(),
): Calificacion {
    return Calificacion(
        reserva = crearReserva_(),
        puntuacion = puntuacion,
        comentario = comentario,
        fecha = fecha,
    )
}

fun crearReserva_(
    libro: Libro = ar.edu.unsam.phm.domain.libro_,
    usuarioPrestador: Usuario = ar.edu.unsam.phm.domain.usuarioPrestador_,
    usuarioLector: Usuario = ar.edu.unsam.phm.domain.usuarioLector_,
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

val libro_ = Libro(
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

val usuarioPrestador_ = Usuario(
    id = 1,
    nombre = "Juan",
    apellido = "Perez",
    descripcion = "Lector de ciencia ficción",
    email = "juan@mail.com",
    celular = "111111",
    ciudad = "Buenos Aires",
    tipoUsuario = TipoUsuario.LECTOR,
    urlImagen = "https://ui-avatars.com/api/?name=Juan+Perez"
).apply { biblioKarma = 2000 }

val usuarioLector_ = Usuario(
    id = 2,
    nombre = "Ana",
    apellido = "Gomez",
    descripcion = "Fan de novelas",
    email = "ana@mail.com",
    celular = "222222",
    ciudad = "Buenos Aires",
    tipoUsuario = TipoUsuario.LECTOR,
    urlImagen = "https://ui-avatars.com/api/?name=Ana+Gomez"
).apply { biblioKarma = 4000 }
