package ar.edu.unsam.phm.controller

import java.time.LocalDate
import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.service.LibroService
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.TipoLibro
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.crearUsuario
import ar.edu.unsam.phm.dto.DireccionOrden
import ar.edu.unsam.phm.dto.FiltroDisponibilidadPerfil
import ar.edu.unsam.phm.dto.FiltrosDeLibros
import ar.edu.unsam.phm.dto.OrdenHomePage
import ar.edu.unsam.phm.dto.OrdenLibroPerfil
import ar.edu.unsam.phm.dto.OrigenLibro
import ar.edu.unsam.phm.repository.CalificacionRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest
class HomePageTest {

    @Autowired
    private lateinit var calificacionRepository: CalificacionRepository

    @Autowired
    private lateinit var librorepository: LibroRepository

    @Autowired
    private lateinit var reservaRepository: ReservaRepository

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var serviceLibros: LibroService
    private val ordenPerfilPage = OrdenLibroPerfil.FECHA_AGREGADO
    private val direccionPerfilPage = DireccionOrden.DESCENDENTE
    private val disponibilidadLibrosPerfil = FiltroDisponibilidadPerfil.TODOS

    private lateinit var usuario1: Usuario
    private lateinit var usuario2: Usuario
    private lateinit var usuarioConsulta: Usuario
    private var usuarioConsultaId = 0L
    private lateinit var libro1: Libro
    private lateinit var libro2: Libro
    private lateinit var libro3: Libro
    private lateinit var libro4: Libro
    private lateinit var libro5: Libro
    private lateinit var libro6: Libro

    @BeforeEach
    fun init() {
        calificacionRepository.deleteAll()
        reservaRepository.deleteAll()
        librorepository.deleteAll()
        usuarioRepository.deleteAll()

        usuario1 = crearUsuario(
            nombre = "Juan",
            apellido = "Perez",
            descripcion = "Lector apasionado de ciencia ficcion",
            email = "juan.perez@example.com",
            celular = "+54 11 1234-5678",
            ciudad = "Buenos Aires",
            tipoUsuario = TipoUsuario.LECTOR,
            urlImagen = "https://www.grupocos.org"
        )

        usuario2 = Usuario(
            nombre = "Maria",
            apellido = "Gomez",
            descripcion = "Coleccionista de libros clasicos",
            email = "maria.gomez@example.com",
            celular = "+54 11 8765-4321",
            ciudad = "Cordoba",
            tipoUsuario = TipoUsuario.LECTOR,
            urlImagen = "https://www.grupocos.org"
        )

        usuarioConsulta = crearUsuario()

        libro1 = crearLibro(
            titulo = "El Aleph",
            tipoLibro = TipoLibro.COMUN,
            descripcion = "Cuentos",
            genero = Genero.DRAMA,
            autor = "Jorge Luis Borges",
            cantidadPaginas = 180,
            isbn13 = 9780307474728L,
            idioma = Idioma.ESPANOL,
            editorial = "Losada",
            fechaPublicacion = LocalDate.of(1949, 6, 1),
            estadoLibro = EstadoLibro.BUENO,
            idPropietario = 2,
        )

        libro2 = crearLibro(
            titulo = "Dune",
            tipoLibro = TipoLibro.COMUN,
            descripcion = "Sci-fi clasica",
            genero = Genero.CIENCIA_FICCION,
            autor = "Frank Herbert",
            cantidadPaginas = 412,
            isbn13 = 9780441172719L,
            idioma = Idioma.INGLES,
            editorial = "Chilton Books",
            fechaPublicacion = LocalDate.of(1965, 8, 1),
            estadoLibro = EstadoLibro.MUY_BUENO,
            idPropietario = 2
        )


        libro3 = crearLibro(
            titulo = "Orgullo y Prejuicio",
            tipoLibro = TipoLibro.COMUN,
            descripcion = "Novela romantica",
            genero = Genero.ROMANCE,
            autor = "Jane Austen",
            cantidadPaginas = 279,
            isbn13 = 9780141439518L,
            idioma = Idioma.INGLES,
            editorial = "T. Egerton",
            fechaPublicacion = LocalDate.of(1813, 1, 28),
            estadoLibro = EstadoLibro.REGULAR,
            idPropietario = 2
        )

        libro4 = crearLibro(
            titulo = "Habitos Atomicos",
            tipoLibro = TipoLibro.COMUN,
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

        libro5 = crearLibro(
            titulo = "Rayuela",
            tipoLibro = TipoLibro.COMUN,
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

        libro6 = crearLibro(
            titulo = "Dune Messiah",
            tipoLibro = TipoLibro.COMUN,
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
        usuarioRepository.save(usuario1)
        usuarioRepository.save(usuario2)
        usuarioRepository.save(usuarioConsulta)
        usuarioConsultaId = usuarioConsulta.id!!
    }

    @Test
    fun `se crea un libro`() {
        val guardado = librorepository.save(libro1)
        val recuperado = librorepository.findById(guardado.id!!).orElseThrow()
        assertEquals(guardado.id, recuperado.id)
    }

    @Test
    fun `se devuelve libros solo con filtro de fechas`() {
        librorepository.save(libro1)
        librorepository.save(libro2)
        librorepository.save(libro3)
        librorepository.save(libro4)
        librorepository.save(libro5)
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

        reservaRepository.save(reserva1)
        reservaRepository.save(reserva2)

        val filtroDeLibros = FiltrosDeLibros(
            fechaDesde = LocalDate.of(2026, 4, 5),
            fechaHasta = LocalDate.of(2026, 4, 12),
            generos = null,
            paginasMin = null,
            paginasMax = null,
            isbn13 = null,
            usuarioPrestador = null,
            search = null,
            usuarioLector = null,
        )

        val librosFiltrados = serviceLibros.getLibrosPorFiltros(
            filtroDeLibros,
            ordenHomePage = OrdenHomePage.NOMBRE_ASC,
            userId = usuarioConsultaId!!,
            flagDiff = OrigenLibro.HOME,
            disponibilidadLibrosPerfil = disponibilidadLibrosPerfil,
            ordenLibroPerfil = ordenPerfilPage,
            direccionOrdenPerfil = direccionPerfilPage
        )
        val titulosObtenidos = librosFiltrados.content.map { it.titulo }
        val titulosEsperados = listOf("Dune", "Habitos Atomicos", "Rayuela")
    }

    @Test
    fun `devuelve libros con filtro de genero y fechas`() {
        librorepository.save(libro1)
        librorepository.save(libro2)
        librorepository.save(libro3)
        librorepository.save(libro4)
        librorepository.save(libro5)

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

        reservaRepository.save(reserva1)
        reservaRepository.save(reserva2)

        val filtroDeLibros = FiltrosDeLibros(
            fechaDesde = LocalDate.of(2026, 4, 5),
            fechaHasta = LocalDate.of(2026, 4, 12),
            generos = listOf(Genero.CIENCIA_FICCION),
            paginasMin = null,
            paginasMax = null,
            isbn13 = null,
            usuarioPrestador = null,
            search = null,
            usuarioLector = null
        )

        val librosFiltrados = serviceLibros.getLibrosPorFiltros(
            filtroDeLibros,
            ordenHomePage = OrdenHomePage.NOMBRE_ASC,
            userId = usuarioConsultaId!!,
            flagDiff = OrigenLibro.HOME,
            disponibilidadLibrosPerfil = disponibilidadLibrosPerfil,
            ordenLibroPerfil = ordenPerfilPage,
            direccionOrdenPerfil = direccionPerfilPage
        )
        assertEquals(listOf("Dune"), librosFiltrados.content.map { it.titulo })
    }

    @Test
    fun `devuelve libros por nombre`() {
        librorepository.save(libro1)
        librorepository.save(libro2)
        librorepository.save(libro3)
        librorepository.save(libro4)
        librorepository.save(libro5)
        librorepository.save(libro6)

        val filtroDeLibros = FiltrosDeLibros(
            fechaDesde = null,
            fechaHasta = null,
            generos = null,
            paginasMin = null,
            paginasMax = null,
            isbn13 = null,
            usuarioPrestador = null,
            search = "Dune",
            usuarioLector = null
        )

        val librosFiltrados = serviceLibros.getLibrosPorFiltros(
            filtroDeLibros,
            ordenHomePage = OrdenHomePage.NOMBRE_ASC,
            userId = usuarioConsultaId!!,
            flagDiff = OrigenLibro.HOME,
            disponibilidadLibrosPerfil = disponibilidadLibrosPerfil,
            ordenLibroPerfil = ordenPerfilPage,
            direccionOrdenPerfil = direccionPerfilPage
        )
        assertEquals(listOf("Dune", "Dune Messiah"), librosFiltrados.content.map { it.titulo })
    }
}

private fun crearUsuario(
    nombre: String,
    apellido: String,
    descripcion: String,
    email: String,
    celular: String,
    ciudad: String,
    tipoUsuario: TipoUsuario,
    urlImagen: String
): Usuario {
    return Usuario(
        nombre = nombre,
        apellido = apellido,
        descripcion = descripcion,
        email = email,
        celular = celular,
        ciudad = ciudad,
        tipoUsuario = tipoUsuario,
        urlImagen = urlImagen,
    )
}

private fun crearLibro(
    titulo: String,
    tipoLibro: TipoLibro,
    descripcion: String,
    genero: Genero,
    autor: String,
    cantidadPaginas: Int,
    isbn13: Long,
    idioma: Idioma,
    editorial: String,
    fechaPublicacion: LocalDate,
    estadoLibro: EstadoLibro,
    idPropietario: Int = 1,
): ar.edu.unsam.phm.domain.Libro {
    return ar.edu.unsam.phm.domain.Libro(
        titulo = titulo,
        tipoLibro = tipoLibro,
        descripcion = descripcion,
        portada = "https://img.test/${titulo.lowercase().replace(" ", "-")}.jpg",
        genero = genero,
        autor = autor,
        cantidadPaginas = cantidadPaginas,
        isbn13 = isbn13,
        idioma = idioma,
        editorial = editorial,
        fechaPublicacion = fechaPublicacion,
        estadoLibro = estadoLibro,
        idPropietario = idPropietario.toLong(),
    )
}
