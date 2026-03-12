package ar.edu.unsam.phm.bootstrap

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Lector
import ar.edu.unsam.phm.domain.Publicador
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.comun
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
class BootstrapData(
    val reservaRepository: ReservaRepository,
    val usuarioRepository: UsuarioRepository,
    val libroRepository: LibroRepository
) {

    @Bean
    fun initData() = CommandLineRunner {

        val usuario1 = Usuario(
            nombre = "Juan",
            apellido = "Perez",
            descripcion = "Lector de ciencia ficción",
            email = "juan@mail.com",
            celular = "111111",
            ciudad = "Buenos Aires",
            tipoUsuario = mutableSetOf<TipoUsuario>(Lector, Publicador),
            urlImagen = "https://ui-avatars.com/api/?name=Juan+Perez",
            id = 1
        )

        val usuario2 = Usuario(
            nombre = "Ana",
            apellido = "Gomez",
            descripcion = "Fan de novelas",
            email = "ana@mail.com",
            celular = "222222",
            ciudad = "Buenos Aires",
            tipoUsuario = mutableSetOf<TipoUsuario>(Publicador),
            urlImagen = "https://ui-avatars.com/api/?name=Ana+Gomez",
            id = 2
        )

        usuarioRepository.create(usuario1)
        usuarioRepository.create(usuario2)

        usuario1.biblioKarma = 2000
        usuario2.biblioKarma = 4000

        val libros = (1..17).map {
            Libro(
                titulo = "Libro $it",
                tipoLibro = comun,
                descripcion = "Descripción libro $it",
                genero = Genero.CIENCIA_FICCION,
                autor = "Autor $it",
                cantidadPaginas = 200 + it,
                isbn13 = 100000000000,
                idioma = Idioma.ESPANOL,
                editorial = "Editorial $it",
                fechaPublicacion = LocalDate.now().minusYears(2),
                estadoLibro = EstadoLibro.EXCELENTE,
                portada = "https://www.giantfreakinrobot.com/wp-content/uploads/2022/08/rick-astley.jpg",
                idPropietario = 1
            )
        }

        val librosHomePageTest = listOf(
            Libro(
                titulo = "El Aleph",
                tipoLibro = comun,
                descripcion = "Cuentos",
                genero = Genero.DRAMA,
                autor = "Jorge Luis Borges",
                cantidadPaginas = 180,
                isbn13 = 9780307474728L,
                idioma = Idioma.ESPANOL,
                editorial = "Losada",
                fechaPublicacion = LocalDate.of(1949, 6, 1),
                estadoLibro = EstadoLibro.BUENO,
                portada = "Portada de libro",
                idPropietario = 1
            ),
            Libro(
                titulo = "Dune",
                tipoLibro = comun,
                descripcion = "Sci-fi clasica",
                genero = Genero.CIENCIA_FICCION,
                autor = "Frank Herbert",
                cantidadPaginas = 412,
                isbn13 = 9780441172719L,
                idioma = Idioma.INGLES,
                editorial = "Chilton Books",
                fechaPublicacion = LocalDate.of(1965, 8, 1),
                estadoLibro = EstadoLibro.MUY_BUENO,
                portada = "Portada de libro",
                idPropietario = 1
            ),
            Libro(
                titulo = "Orgullo y Prejuicio",
                tipoLibro = comun,
                descripcion = "Novela romantica",
                genero = Genero.ROMANCE,
                autor = "Jane Austen",
                cantidadPaginas = 279,
                isbn13 = 9780141439518L,
                idioma = Idioma.INGLES,
                editorial = "T. Egerton",
                fechaPublicacion = LocalDate.of(1813, 1, 28),
                estadoLibro = EstadoLibro.REGULAR,
                portada = "Portada de libro",
                idPropietario = 1
            ),
            Libro(
                titulo = "Habitos Atomicos",
                tipoLibro = comun,
                descripcion = "Mejora personal",
                genero = Genero.AUTOAYUDA,
                autor = "James Clear",
                cantidadPaginas = 320,
                isbn13 = 9780735211292L,
                idioma = Idioma.ESPANOL,
                editorial = "Penguin",
                fechaPublicacion = LocalDate.of(2018, 10, 16),
                estadoLibro = EstadoLibro.EXCELENTE,
                portada = "Portada de libro",
                idPropietario = 1
            ),
            Libro(
                titulo = "Rayuela",
                tipoLibro = comun,
                descripcion = "Literatura latinoamericana",
                genero = Genero.LITERATURA_CLASICA,
                autor = "Julio Cortazar",
                cantidadPaginas = 736,
                isbn13 = 9788437604578L,
                idioma = Idioma.ESPANOL,
                editorial = "Sudamericana",
                fechaPublicacion = LocalDate.of(1963, 6, 28),
                estadoLibro = EstadoLibro.BUENO,
                portada = "Portada de libro",
                idPropietario = 1
            ),
            Libro(
                titulo = "Dune Messiah",
                tipoLibro = comun,
                descripcion = "Segunda parte de Dune",
                genero = Genero.CIENCIA_FICCION,
                autor = "Frank Herbert",
                cantidadPaginas = 688,
                isbn13 = 9780441172726L,
                idioma = Idioma.INGLES,
                editorial = "Chilton Books",
                fechaPublicacion = LocalDate.of(1969, 6, 1),
                estadoLibro = EstadoLibro.MUY_BUENO,
                portada = "Portada de libro",
                idPropietario = 1
            )
        )

        libros.forEach { libroRepository.create(it) }
        librosHomePageTest.forEach { libroRepository.create(it) }

        libros.forEachIndexed { index, libro ->

            // como está hecho, el usuario 2 es lector de los 17 libros del usuario 1, puede calificar cualquiera.
            val reserva = Reserva(
                libro = libro,
                usuarioPrestador = usuario1,
                usuarioLector = usuario2,
                fechaRecogida = LocalDate.now().minusMonths((index + 1).toLong()),
                fechaDevolucion = LocalDate.now().minusDays((10 - index).toLong())
            )

            reservaRepository.create(reserva)
        }

        libroRepository.create(libros[0])
    }


}
