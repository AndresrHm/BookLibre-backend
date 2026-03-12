package ar.edu.unsam.phm.bootstrap

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Lector
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.comun
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
class BootstrapData(
    val reservaRepository: ReservaRepository,
    val libroRepository: LibroRepository
) {

    @Bean
    fun initData() = CommandLineRunner {

        val usuario1 = Usuario(
            "Juan",
            "Perez",
            "Lector de ciencia ficción",
            "juan@mail.com",
            "111111",
            "Buenos Aires",
            id = 1,
            tipoUsuario = Lector
        )

        val usuario2 = Usuario(
            "Ana",
            "Gomez",
            "Fan de novelas",
            "ana@mail.com",
            "222222",
            "Buenos Aires",
            id = 2,
            tipoUsuario = Lector
        )

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

        libros.forEachIndexed { index, libro ->

            val reserva = Reserva(
                libro = libro,
                usuarioPrestador = usuario1,
                usuarioLector = usuario2,
                fechaRecogida = LocalDate.now().minusDays((index + 1).toLong()),
                fechaDevolucion = LocalDate.now().plusDays((10 - index).toLong())
            )

            reservaRepository.create(reserva)
        }

        libroRepository.create(Libro(
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
            portada = "",
            idPropietario = 1
        ))
        libroRepository.create(Libro(
            titulo = "Dune",
            tipoLibro = comun,
            descripcion = "Sci-fi clasica",
            genero = Genero.CIENCIA_FICCION,
            autor = "Frank Herbert",
            cantidadPaginas = 412,
            isbn13 = 9780441013593L,
            idioma = Idioma.ESPANOL,
            editorial = "Debolsillo",
            fechaPublicacion = LocalDate.of(1965, 8, 1),
            estadoLibro = EstadoLibro.BUENO,
            portada = "",
            idPropietario = 1
        ))
        libroRepository.create(Libro(
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
            portada = "",
            idPropietario = 1
        ))
        libroRepository.create(Libro(
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
            portada = "",
            idPropietario = 1
        ))
        libroRepository.create(Libro(
            titulo = "Rayuela",
            tipoLibro = comun,
            descripcion = "Novela experimental",
            genero = Genero.DRAMA,
            autor = "Julio Cortázar",
            cantidadPaginas = 300,
            isbn13 = 9789875800000L,
            idioma = Idioma.ESPANOL,
            editorial = "Sudamericana",
            fechaPublicacion = LocalDate.of(1963, 6, 28),
            estadoLibro = EstadoLibro.BUENO,
            portada = "",
            idPropietario = 1
        ))
        libroRepository.create(Libro(
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
            portada = "",
            idPropietario = 1
        ))
        
    }
    

}