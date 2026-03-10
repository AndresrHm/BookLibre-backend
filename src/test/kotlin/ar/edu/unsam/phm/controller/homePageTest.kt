package ar.edu.unsam.phm.controller

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import java.time.LocalDate
import io.kotest.matchers.shouldBe
import ar.edu.unsam.phm.domain.Comun
import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.service.ReservaService
import ar.edu.unsam.phm.service.LibroService
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.Lector
import ar.edu.unsam.phm.domain.Publicador
import ar.edu.unsam.phm.dto.FiltrosDeLibrosDTO

class EspecTest : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe ("Test de Busqueda de libros Basico"){
        val repositoryReservas = ReservaRepository()
        val repositoryLibros = LibroRepository()
        val serviceReservas = ReservaService(repositoryReservas)
        val serviceLibros = LibroService(repositoryLibros, serviceReservas)

        val usuario1 = Usuario(
            nombre = "Juan",
            apellido = "Perez",
            descripcion = "Lector apasionado de ciencia ficcion",
            email = "juan.perez@example.com",
            celular = "+54 11 1234-5678",
            ciudad = "Buenos Aires",
            tipoUsuario = Lector
        )

        val usuario2 = Usuario(
            nombre = "Maria",
            apellido = "Gomez",
            descripcion = "Coleccionista de libros clasicos",
            email = "maria.gomez@example.com",
            celular = "+54 11 8765-4321",
            ciudad = "Cordoba",
            tipoUsuario = Publicador
        )

        val libro1 = Libro("El Aleph", Comun, "Cuentos", Genero.DRAMA, "Jorge Luis Borges", 180, 9780307474728.toInt(), Idioma.ESPANOL, "Losada", LocalDate.of(1949, 6, 1), EstadoLibro.BUENO)
        val libro2 = Libro("Dune", Comun, "Sci-fi clasica", Genero.CIENCIA_FICCION, "Frank Herbert", 412, 9780441172719.toInt(), Idioma.INGLES, "Chilton Books", LocalDate.of(1965, 8, 1), EstadoLibro.MUY_BUENO)
        val libro3 = Libro("Orgullo y Prejuicio", Comun, "Novela romantica", Genero.ROMANCE, "Jane Austen", 279, 9780141439518.toInt(), Idioma.INGLES, "T. Egerton", LocalDate.of(1813, 1, 28), EstadoLibro.REGULAR)
        val libro4 = Libro("Habitos Atomicos", Comun, "Mejora personal", Genero.AUTOAYUDA, "James Clear", 320, 9780735211292.toInt(), Idioma.ESPANOL, "Penguin", LocalDate.of(2018, 10, 16), EstadoLibro.EXCELENTE)
        val libro5 = Libro("Rayuela", Comun, "Literatura latinoamericana", Genero.LITERATURA_CLASICA, "Julio Cortazar", 736, 9788437604578.toInt(), Idioma.ESPANOL, "Sudamericana", LocalDate.of(1963, 6, 28), EstadoLibro.BUENO)

        it( "se crea un libro"){
            repositoryLibros.create(libro1)
            repositoryLibros.getById(libro1.id) shouldBe libro1
        }

        it( "se devuelve libros solo con filtro de fechas"){
            repositoryLibros.create(libro1)
            repositoryLibros.create(libro2)
            repositoryLibros.create(libro3)
            repositoryLibros.create(libro4)
            repositoryLibros.create(libro5)
            val reserva1 = Reserva(
                libro = libro1,
                usuarioPrestador = usuario2,
                usuarioLector = usuario1,
                fechaRecogida = LocalDate.of(2026, 4, 1),
                fechaDevolucion = LocalDate.of(2026, 4, 15)
            )

            val reserva2 = Reserva(
                libro = libro3,
                usuarioPrestador = usuario2,
                usuarioLector = usuario1,
                fechaRecogida = LocalDate.of(2026, 4, 10),
                fechaDevolucion = LocalDate.of(2026, 4, 20)
            )

            repositoryReservas.create(reserva1)
            repositoryReservas.create(reserva2)

            val filtroDeLibros = FiltrosDeLibrosDTO(
                fechaDesde = LocalDate.of(2026, 4, 5),
                fechaHasta = LocalDate.of(2026, 4, 12),
                genero = null,
                paginasMin = null,
                paginasMax = null,
                isbn13 = null,
                usuarioPrestador = null
            )

            val librosFiltrados = serviceLibros.getLibrosPorFiltros(filtroDeLibros)
            librosFiltrados shouldBe listOf(libro2, libro4, libro5)


        }
        it( "devuelve libros con filtro de genero y fechas"){
            repositoryLibros.create(libro1)
            repositoryLibros.create(libro2)
            repositoryLibros.create(libro3)
            repositoryLibros.create(libro4)
            repositoryLibros.create(libro5)

            val reserva1 = Reserva(
                libro = libro1,
                usuarioPrestador = usuario2,
                usuarioLector = usuario1,
                fechaRecogida = LocalDate.of(2026, 4, 1),
                fechaDevolucion = LocalDate.of(2026, 4, 15)
            )

            val reserva2 = Reserva(
                libro = libro3,
                usuarioPrestador = usuario2,
                usuarioLector = usuario1,
                fechaRecogida = LocalDate.of(2026, 4, 10),
                fechaDevolucion = LocalDate.of(2026, 4, 20)
            )

            repositoryReservas.create(reserva1)
            repositoryReservas.create(reserva2)

            val filtroDeLibros = FiltrosDeLibrosDTO(
                fechaDesde = LocalDate.of(2026, 4, 5),
                fechaHasta = LocalDate.of(2026, 4, 12),
                genero = Genero.CIENCIA_FICCION,
                paginasMin = null,
                paginasMax = null,
                isbn13 = null,
                usuarioPrestador = null
            )

            val librosFiltrados = serviceLibros.getLibrosPorFiltros(filtroDeLibros)
            librosFiltrados shouldBe listOf(libro2)
        }



    }
})