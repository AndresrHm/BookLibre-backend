package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.errors.BusinessException
import ar.edu.unsam.phm.errors.NotFoundException
import ar.edu.unsam.phm.domain.*
import ar.edu.unsam.phm.dto.*
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import java.util.*

class LibroServiceSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    val libroRepository = mockk<LibroRepository>(relaxed = true)
    val reservaRepository = mockk<ReservaRepository>(relaxed = true)
    val usuarioRepository = mockk<UsuarioRepository>(relaxed = true)

    val service = LibroService(libroRepository, reservaRepository, usuarioRepository)

    val idUsuario = 1L
    val idLibro = 10L

    val publicador = crearUsuario(id = idUsuario, roles = mutableSetOf(crearRol(nameCreacion = TipoUsuario.PUBLICADOR.roleName)))
    val lector = crearUsuario(id = 2L, roles = mutableSetOf(crearRol(nameCreacion = TipoUsuario.LECTOR.roleName)))

    val libro = crearLibro(idPropietario = idUsuario).apply { id = idLibro }

    describe("LibroService: crearLibro") {

        it("lanza excepción si el usuario no tiene rol publicador") {
            every { usuarioRepository.findById(idUsuario) } returns Optional.of(lector)

            val exception = shouldThrow<BusinessException> {
                service.crearLibro(libro)
            }
            exception.message shouldBe "Solo un Publicador puede crear libros."
        }

        it("guarda el libro correctamente si el usuario es publicador") {
            every { usuarioRepository.findById(idUsuario) } returns Optional.of(publicador)
            every { libroRepository.save(any<Libro>()) } returns libro

            val resultado = service.crearLibro(libro)

            resultado shouldBe libro
            verify(exactly = 1) { libroRepository.save(any<Libro>()) }
        }
    }

    describe("LibroService: editarLibro") {
        it("edita el libro correctamente si el usuario es el dueño") {
            val libroEditado = crearLibro(idPropietario = idUsuario, titulo = "Nuevo Titulo").apply { id = idLibro }
            every { usuarioRepository.findById(idUsuario) } returns Optional.of(publicador)
            every { libroRepository.findById(idLibro) } returns Optional.of(libro)
            every { libroRepository.save(any()) } returns libroEditado

            val resultado = service.editarLibro(libroEditado, idUsuario)

            resultado.titulo shouldBe "Nuevo Titulo"
            verify(exactly = 1) { libroRepository.save(any()) }
        }

        it("lanza excepción si el usuario no es publicador al editar") {
            val libroEditado = crearLibro(idPropietario = 2L).apply { id = idLibro }
            every { usuarioRepository.findById(2L) } returns Optional.of(lector)

            shouldThrow<BusinessException> {
                service.editarLibro(libroEditado, 2L)
            }.message shouldBe "Solo un Publicador puede crear libros."
        }
    }

    describe("LibroService: getLibrosPorFiltros (Perfil)") {
        it("devuelve los libros del perfil correctamente") {
            val listaLibros = listOf(libro)
            every { libroRepository.findByActivoTrueAndIdPropietario(idUsuario) } returns listaLibros
            every { reservaRepository.getLibrosReservadosEntreFechas(any(), any()) } returns emptyList()

            val response = service.getLibrosPorFiltros(
                filtrosDeHome = FiltrosDeLibros(null, null, null, null, null, null, null, null, null),
                userId = idUsuario,
                flagDiff = OrigenLibro.PERFIL,
                disponibilidadLibrosPerfil = FiltroDisponibilidadPerfil.TODOS,
                ordenLibroPerfil = OrdenLibroPerfil.TITULO,
                direccionOrdenPerfil = DireccionOrden.ASCENDENTE,
                ordenHomePage = OrdenHomePage.NOMBRE_ASC
            )

            response.content shouldBe listaLibros
            response.totalElements shouldBe 1
        }
    }

    describe("LibroService: validaciones adicionales") {
        it("lanza excepción si fechaDesde > fechaHasta") {
            every { usuarioRepository.findById(idUsuario) } returns Optional.of(lector)
            val filtros = FiltrosDeLibros(null, null, null, LocalDate.now().plusDays(2), LocalDate.now(), null, null, null, null)

            shouldThrow<BusinessException> {
                service.librosFiltradosHomePage(filtros, userId = idUsuario, ordenHomePage = OrdenHomePage.NOMBRE_ASC)
            }.message shouldBe "fechaDesde no puede ser mayor a fechaHasta"
        }

        it("lanza exception si el origen de búsqueda no es válido") {
            shouldThrow<BusinessException> {
                service.getLibrosPorFiltros(
                    filtrosDeHome = FiltrosDeLibros(null, null, null, null, null, null, null, null, null),
                    userId = idUsuario,
                    flagDiff = mockk(),
                    disponibilidadLibrosPerfil = FiltroDisponibilidadPerfil.TODOS,
                    ordenLibroPerfil = OrdenLibroPerfil.TITULO,
                    direccionOrdenPerfil = DireccionOrden.ASCENDENTE,
                    ordenHomePage = OrdenHomePage.NOMBRE_ASC
                )
            }.message shouldBe "No se reconoce el origen de la busqueda"
        }
    }

    describe("LibroService: Otros métodos") {
        it("obtenerPrestadoresDisponibles filtra correctamente") {
            every { usuarioRepository.findById(idUsuario) } returns Optional.of(lector)
            every { libroRepository.findByActivoTrue() } returns listOf(libro)
            every { reservaRepository.getLibrosReservadosEntreFechas(any(), any()) } returns emptyList()

            val prestadores = service.obtenerPrestadoresDisponibles(null, idUsuario)
            prestadores shouldBe emptyList()
        }

        it("bringKarma lanza NotFound si el usuario no existe") {
            every { usuarioRepository.findById(any()) } returns Optional.empty()
            shouldThrow<NotFoundException> {
                service.bringKarma(libro, 5, 99L, null, null)
            }
        }
    }

    describe("LibroService: borrarLibro") {

        it("lanza excepción si el libro está prestado hoy") {
            every { libroRepository.findById(idLibro) } returns Optional.of(libro)
            every { reservaRepository.getLibrosReservadosEntreFechas(any(), any()) } returns listOf(idLibro)

            val exception = shouldThrow<BusinessException> {
                service.borrarLibro(idLibro, idUsuario)
            }
            exception.message shouldBe "El libro se encuentra prestado"
        }
    }

    describe("LibroService: getLibrosPorFiltros (HomePage)") {

        it("devuelve una respuesta paginada usando filtros específicos") {
            every { usuarioRepository.findById(idUsuario) } returns Optional.of(lector)
            every { reservaRepository.getLibrosReservadosEntreFechas(any(), any()) } returns emptyList()

            val filtros = FiltrosDeLibros(
                generos = listOf(Genero.CIENCIA_FICCION),
                paginasMin = 10,
                paginasMax = 500,
                fechaDesde = LocalDate.now(),
                fechaHasta = LocalDate.now().plusDays(5),
                isbn13 = 1111111111111L,
                usuarioPrestador = null,
                search = "Fundación",
                usuarioLector = lector
            )

            val page: Page<Libro> = PageImpl(listOf(libro), PageRequest.of(0, 6), 1)
            every { libroRepository.buscarConFiltros(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns page

            val response = service.getLibrosPorFiltros(
                filtrosDeHome = filtros,
                userId = idUsuario,
                flagDiff = OrigenLibro.HOME,
                disponibilidadLibrosPerfil = FiltroDisponibilidadPerfil.TODOS,
                ordenLibroPerfil = OrdenLibroPerfil.TITULO,
                direccionOrdenPerfil = DireccionOrden.ASCENDENTE,
                ordenHomePage = OrdenHomePage.NOMBRE_ASC
            )

            response.totalElements shouldBe 1
            response.content.first().titulo shouldBe "Título de libro"
        }

        it("lanza excepción si el rango de páginas es inválido en el constructor de filtros") {
            every { usuarioRepository.findById(idUsuario) } returns Optional.of(lector)

            val filtrosInvalidos = FiltrosDeLibros(
                generos = null, paginasMin = 500, paginasMax = 100,
                fechaDesde = null, fechaHasta = null, isbn13 = null,
                usuarioPrestador = null, search = null, usuarioLector = null
            )

            val exception = shouldThrow<BusinessException> {
                service.librosFiltradosHomePage(filtrosInvalidos, userId = idUsuario, ordenHomePage = OrdenHomePage.NOMBRE_ASC)
            }
            exception.message shouldBe "paginasMin no puede ser mayor a paginasMax"
        }
    }

    describe("LibroService: Cobertura de ordenamiento y disponibilidad") {

        it("sortHomePage: cubre ordenamiento por Autor y por Usuario (unsorted)") {
            every { usuarioRepository.findById(idUsuario) } returns Optional.of(lector)
            every { reservaRepository.getLibrosReservadosEntreFechas(any(), any()) } returns emptyList()
            val page: Page<Libro> = PageImpl(listOf(libro))
            every { libroRepository.buscarConFiltros(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns page

            service.librosFiltradosHomePage(FiltrosDeLibros(null, null, null, null, null, null, null, null, null),
                userId = idUsuario, ordenHomePage = OrdenHomePage.AUTOR_ASC)

            service.librosFiltradosHomePage(FiltrosDeLibros(null, null, null, null, null, null, null, null, null),
                userId = idUsuario, ordenHomePage = OrdenHomePage.USER_DESC)

            verify(atLeast = 2) { libroRepository.buscarConFiltros(any(), any(), any(), any(), any(), any(), any(), any(), any()) }
        }

        it("filtrarYOrdenarLibrosPerfil: cubre filtros de DISPONIBLES y PRESTADOS") {
            every { libroRepository.findByActivoTrueAndIdPropietario(idUsuario) } returns listOf(libro)
            every { reservaRepository.getLibrosReservadosEntreFechas(any(), any()) } returns emptyList()

            service.getLibrosPorFiltros(FiltrosDeLibros(null, null, null, null, null, null, null, null, null),
                userId = idUsuario, flagDiff = OrigenLibro.PERFIL, disponibilidadLibrosPerfil = FiltroDisponibilidadPerfil.DISPONIBLES,
                ordenLibroPerfil = OrdenLibroPerfil.TITULO, direccionOrdenPerfil = DireccionOrden.ASCENDENTE, ordenHomePage = OrdenHomePage.NOMBRE_ASC)

            service.getLibrosPorFiltros(FiltrosDeLibros(null, null, null, null, null, null, null, null, null),
                userId = idUsuario, flagDiff = OrigenLibro.PERFIL, disponibilidadLibrosPerfil = FiltroDisponibilidadPerfil.PRESTADOS,
                ordenLibroPerfil = OrdenLibroPerfil.TITULO, direccionOrdenPerfil = DireccionOrden.ASCENDENTE, ordenHomePage = OrdenHomePage.NOMBRE_ASC)

            verify(atLeast = 2) { libroRepository.findByActivoTrueAndIdPropietario(idUsuario) }
        }

        it("filtrarYOrdenarLibrosPerfil: cubre ordenamiento por FECHA_AGREGADO y DISPONIBLE_PRESTADO") {
            every { libroRepository.findByActivoTrueAndIdPropietario(idUsuario) } returns listOf(libro)
            every { reservaRepository.getLibrosReservadosEntreFechas(any(), any()) } returns emptyList()

            service.getLibrosPorFiltros(FiltrosDeLibros(null, null, null, null, null, null, null, null, null),
                userId = idUsuario, flagDiff = OrigenLibro.PERFIL, disponibilidadLibrosPerfil = FiltroDisponibilidadPerfil.TODOS,
                ordenLibroPerfil = OrdenLibroPerfil.FECHA_AGREGADO, direccionOrdenPerfil = DireccionOrden.DESCENDENTE, ordenHomePage = OrdenHomePage.NOMBRE_ASC)

            service.getLibrosPorFiltros(FiltrosDeLibros(null, null, null, null, null, null, null, null, null),
                userId = idUsuario, flagDiff = OrigenLibro.PERFIL, disponibilidadLibrosPerfil = FiltroDisponibilidadPerfil.TODOS,
                ordenLibroPerfil = OrdenLibroPerfil.DISPONIBLE_PRESTADO, direccionOrdenPerfil = DireccionOrden.ASCENDENTE, ordenHomePage = OrdenHomePage.NOMBRE_ASC)

            verify(atLeast = 2) { libroRepository.findByActivoTrueAndIdPropietario(idUsuario) }
        }
    }

    describe("LibroService: Casos de borde en paginación y karma") {

        it("lanza excepción si el size de página es cero o negativo") {
            every { usuarioRepository.findById(idUsuario) } returns Optional.of(lector)

            val exception = shouldThrow<BusinessException> {
                service.librosFiltradosHomePage(
                    filtros = FiltrosDeLibros(null, null, null, null, null, null, null, null, null),
                    page = 0,
                    size = 0,
                    userId = idUsuario,
                    ordenHomePage = OrdenHomePage.NOMBRE_ASC
                )
            }
            exception.message shouldBe "size debe ser mayor a 0"
        }

        it("paginar: cubre el caso de lista vacía para totalPages = 0") {
            every { libroRepository.findByActivoTrueAndIdPropietario(idUsuario) } returns emptyList()
            every { reservaRepository.getLibrosReservadosEntreFechas(any(), any()) } returns emptyList()

            val response = service.getLibrosPorFiltros(FiltrosDeLibros(null, null, null, null, null, null, null, null, null),
                userId = idUsuario, flagDiff = OrigenLibro.PERFIL, disponibilidadLibrosPerfil = FiltroDisponibilidadPerfil.TODOS,
                ordenLibroPerfil = OrdenLibroPerfil.TITULO, direccionOrdenPerfil = DireccionOrden.ASCENDENTE, ordenHomePage = OrdenHomePage.NOMBRE_ASC)

            response.totalPages shouldBe 0
        }

        it("bringKarma: calcula correctamente con fechas por defecto") {
            every { usuarioRepository.findById(idUsuario) } returns Optional.of(lector)
            service.bringKarma(libro, 10, idUsuario, null, null)

            verify(exactly = 1) { usuarioRepository.findById(idUsuario) }
        }
    }
})
