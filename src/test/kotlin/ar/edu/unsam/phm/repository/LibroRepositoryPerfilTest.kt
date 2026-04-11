package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.TipoLibro
import ar.edu.unsam.phm.dto.DireccionOrden
import ar.edu.unsam.phm.dto.FiltroDisponibilidadPerfil
import ar.edu.unsam.phm.dto.FiltrosDeLibros
import ar.edu.unsam.phm.dto.OrdenHomePage
import ar.edu.unsam.phm.dto.OrdenLibroPerfil
import ar.edu.unsam.phm.dto.OrigenLibro
import ar.edu.unsam.phm.service.LibroService
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class LibroRepositoryPerfilTest : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("Busqueda de libros por propietario con filtros y orden") {
        it("filtra solo disponibles") {
            val repositorioLibro = mockk<LibroRepository>()
            val repositorioReservas = mockk<ReservaRepository>(relaxed = true)
            val repositorioUsuario = mockk<UsuarioRepository>(relaxed = true)
            val servicio = LibroService(repositorioLibro, repositorioReservas, repositorioUsuario)

            val disponible = crearLibro(id = 1, titulo = "Disponible", autor = "Autor A", idPropietario = 10)
            val prestado = crearLibro(id = 2, titulo = "Prestado", autor = "Autor B", idPropietario = 10)

            every { repositorioLibro.findByActivoTrueAndIdPropietario(10) } returns listOf(disponible, prestado)
            every { repositorioLibro.buscarLibrosPrestados() } returns listOf(prestado)

            val resultado = servicio.getLibrosPorFiltros(
                filtrosDeHome = filtrosVacios(),
                page = 0,
                size = 10,
                ordenHomePage = OrdenHomePage.NOMBRE_ASC,
                userId = 10,
                flagDiff = OrigenLibro.PERFIL,
                disponibilidadLibrosPerfil = FiltroDisponibilidadPerfil.DISPONIBLES,
                ordenLibroPerfil = OrdenLibroPerfil.FECHA_AGREGADO,
                direccionOrdenPerfil = DireccionOrden.ASCENDENTE,
            )

            resultado.content.map { it.id } shouldContainExactly listOf(disponible.id)
            resultado.totalElements shouldBe 1
        }

        it("ordena por titulo") {
            val repositorioLibro = mockk<LibroRepository>()
            val repositorioReservas = mockk<ReservaRepository>(relaxed = true)
            val repositorioUsuario = mockk<UsuarioRepository>(relaxed = true)
            val servicio = LibroService(repositorioLibro, repositorioReservas, repositorioUsuario)

            val libroAutorZ = crearLibro(id = 1, titulo = "Mismo", autor = "Zulu", idPropietario = 5)
            val libroAutorA = crearLibro(id = 2, titulo = "Mismo", autor = "Alfa", idPropietario = 5)
            val libroTituloB = crearLibro(id = 3, titulo = "Zoo", autor = "Beta", idPropietario = 5)

            every { repositorioLibro.findByActivoTrueAndIdPropietario(5) } returns listOf(libroAutorZ, libroTituloB, libroAutorA)
            every { repositorioLibro.buscarLibrosPrestados() } returns emptyList()

            val resultado = servicio.getLibrosPorFiltros(
                filtrosDeHome = filtrosVacios(),
                page = 0,
                size = 10,
                ordenHomePage = OrdenHomePage.NOMBRE_ASC,
                userId = 5,
                flagDiff = OrigenLibro.PERFIL,
                disponibilidadLibrosPerfil = FiltroDisponibilidadPerfil.TODOS,
                ordenLibroPerfil = OrdenLibroPerfil.TITULO,
                direccionOrdenPerfil = DireccionOrden.ASCENDENTE,
            )

            resultado.content.map { it.id } shouldContainExactly listOf(libroAutorA.id, libroAutorZ.id, libroTituloB.id)
        }

        it("ordena por disponibilidad en descendente dejando prestados primero") {
            val repositorioLibro = mockk<LibroRepository>()
            val repositorioReservas = mockk<ReservaRepository>(relaxed = true)
            val repositorioUsuario = mockk<UsuarioRepository>(relaxed = true)
            val servicio = LibroService(repositorioLibro, repositorioReservas, repositorioUsuario)

            val disponible = crearLibro(id = 1, titulo = "Disponible", autor = "Autor A", idPropietario = 8)
            val prestado = crearLibro(id = 2, titulo = "Prestado", autor = "Autor B", idPropietario = 8)

            every { repositorioLibro.findByActivoTrueAndIdPropietario(8) } returns listOf(disponible, prestado)
            every { repositorioLibro.buscarLibrosPrestados() } returns listOf(prestado)

            val resultado = servicio.getLibrosPorFiltros(
                filtrosDeHome = filtrosVacios(),
                page = 0,
                size = 10,
                ordenHomePage = OrdenHomePage.NOMBRE_ASC,
                userId = 8,
                flagDiff = OrigenLibro.PERFIL,
                disponibilidadLibrosPerfil = FiltroDisponibilidadPerfil.TODOS,
                ordenLibroPerfil = OrdenLibroPerfil.DISPONIBLE_PRESTADO,
                direccionOrdenPerfil = DireccionOrden.DESCENDENTE,
            )

            resultado.content.map { it.id } shouldContainExactly listOf(prestado.id, disponible.id)
        }

        it("ordena por fecha agregada") {
            val repositorioLibro = mockk<LibroRepository>()
            val repositorioReservas = mockk<ReservaRepository>(relaxed = true)
            val repositorioUsuario = mockk<UsuarioRepository>(relaxed = true)
            val servicio = LibroService(repositorioLibro, repositorioReservas, repositorioUsuario)

            val libroViejo = crearLibro(id = 1, titulo = "Viejo", autor = "Autor A", idPropietario = 3, fechaAgregado = LocalDate.of(2020, 1, 1))
            val libroMedio = crearLibro(id = 2, titulo = "Medio", autor = "Autor B", idPropietario = 3, fechaAgregado = LocalDate.of(2021, 1, 1))
            val libroNuevo = crearLibro(id = 3, titulo = "Nuevo", autor = "Autor C", idPropietario = 3, fechaAgregado = LocalDate.of(2022, 1, 1))

            every { repositorioLibro.findByActivoTrueAndIdPropietario(3) } returns listOf(libroNuevo, libroViejo, libroMedio)
            every { repositorioLibro.buscarLibrosPrestados() } returns emptyList()

            val resultado = servicio.getLibrosPorFiltros(
                filtrosDeHome = filtrosVacios(),
                page = 0,
                size = 10,
                ordenHomePage = OrdenHomePage.NOMBRE_ASC,
                userId = 3,
                flagDiff = OrigenLibro.PERFIL,
                disponibilidadLibrosPerfil = FiltroDisponibilidadPerfil.TODOS,
                ordenLibroPerfil = OrdenLibroPerfil.FECHA_AGREGADO,
                direccionOrdenPerfil = DireccionOrden.ASCENDENTE,
            )

            resultado.content.map { it.id } shouldContainExactly listOf(libroViejo.id, libroMedio.id, libroNuevo.id)
        }
    }
})

private fun filtrosVacios() = FiltrosDeLibros(
    generos = null,
    paginasMin = null,
    paginasMax = null,
    fechaDesde = null,
    fechaHasta = null,
    isbn13 = null,
    usuarioPrestador = null,
    search = null,
    usuarioLector = null,
)

private fun crearLibro(
    id: Long,
    titulo: String,
    autor: String,
    idPropietario: Long,
    fechaAgregado: LocalDate = LocalDate.of(2024, 1, 1),
): Libro {
    val libro = Libro(
        titulo = titulo,
        tipoLibro = TipoLibro.COMUN,
        descripcion = "Descripcion",
        portada = "Portada",
        genero = Genero.CIENCIA_FICCION,
        autor = autor,
        cantidadPaginas = 200,
        isbn13 = 1234567890123,
        idioma = Idioma.ESPANOL,
        editorial = "Editorial",
        fechaPublicacion = LocalDate.of(2020, 1, 1),
        estadoLibro = EstadoLibro.BUENO,
        idPropietario = idPropietario,
    )
    libro.id = id
    libro.fechaAgregado = fechaAgregado
    return libro
}
