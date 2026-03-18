package ar.edu.unsam.phm.controller

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import java.time.LocalDate
import io.kotest.matchers.shouldBe
import ar.edu.unsam.phm.domain.comun
import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.TipoLibro
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.service.ReservaService
import ar.edu.unsam.phm.service.LibroService
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.lector
import ar.edu.unsam.phm.dto.FiltrosDeLibrosDTO
import ar.edu.unsam.phm.mapper.LibroMapper
import ar.edu.unsam.phm.repository.UsuarioRepository
/*
class EspecTest : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe ("Test de Busqueda de libros Basico"){
        val repositoryReservas = ReservaRepository()
        val repositoryLibros = LibroRepository(repositoryReservas)
        val repositoryUsuario = UsuarioRepository()
        val serviceReservas = ReservaService(repositoryReservas, repositoryUsuario, repositoryLibros)
        val serviceLibros = LibroService(repositoryLibros, serviceReservas, LibroMapper(repositoryLibros))

        val usuario1 = Usuario(
            nombre = "Juan",
            apellido = "Perez",
            descripcion = "Lector apasionado de ciencia ficcion",
            email = "juan.perez@example.com",
            celular = "+54 11 1234-5678",
            ciudad = "Buenos Aires",
            tipoUsuario = lector,
            urlImagen = "https://www.grupocos.org"
        )

        val usuario2 = Usuario(
            nombre = "Maria",
            apellido = "Gomez",
            descripcion = "Coleccionista de libros clasicos",
            email = "maria.gomez@example.com",
            celular = "+54 11 8765-4321",
            ciudad = "Cordoba",
            tipoUsuario = lector,
            urlImagen = "https://www.grupocos.org"
        )

        val libro1 = crearLibro(
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
            estadoLibro = EstadoLibro.BUENO
        )

        val libro2 = crearLibro(
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
            estadoLibro = EstadoLibro.MUY_BUENO
        )


        val libro3 = crearLibro(
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
            estadoLibro = EstadoLibro.REGULAR
        )

        val libro4 = crearLibro(
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
            estadoLibro = EstadoLibro.EXCELENTE
        )

        val libro5 = crearLibro(
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
            estadoLibro = EstadoLibro.BUENO
        )

        val libro6 = crearLibro(
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
            estadoLibro = EstadoLibro.MUY_BUENO
        )
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
                usuarioPrestador = null,
                search = null
            )

            val librosFiltrados = serviceLibros.getLibrosPorFiltros(filtroDeLibros)
            librosFiltrados.content shouldBe listOf(libro2, libro4, libro5)


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
                usuarioPrestador = null,
                search = null
            )

            val librosFiltrados = serviceLibros.getLibrosPorFiltros(filtroDeLibros)
            librosFiltrados.content shouldBe listOf(libro2)
        }
        it ("devuelve libros por nombre") {
            repositoryLibros.create(libro1)
            repositoryLibros.create(libro2)
            repositoryLibros.create(libro3)
            repositoryLibros.create(libro4)
            repositoryLibros.create(libro5)
            repositoryLibros.create(libro6)

            val filtroDeLibros = FiltrosDeLibrosDTO(
                fechaDesde = null,
                fechaHasta = null,
                genero = null,
                paginasMin = null,
                paginasMax = null,
                isbn13 = null,
                usuarioPrestador = null,
                search = "Dune"
            )

            val librosFiltrados = serviceLibros.getLibrosPorFiltros(filtroDeLibros)
            librosFiltrados.content shouldBe listOf(libro2, libro6)
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
*/
