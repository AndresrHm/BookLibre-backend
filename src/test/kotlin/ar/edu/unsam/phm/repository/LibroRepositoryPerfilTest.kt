package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.TipoLibro
import ar.edu.unsam.phm.domain.comun
import ar.edu.unsam.phm.dto.DireccionOrden
import ar.edu.unsam.phm.dto.FiltroDisponibilidadPerfil
import ar.edu.unsam.phm.dto.OrdenLibroPerfil
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import java.time.LocalDate

class LibroRepositoryPerfilTest : DescribeSpec({
    describe("Busqueda de libros por propietario con filtros y orden") {
        it("filtra solo disponibles") {
            val reservaRepository = ReservaRepository()
            val libroRepository = LibroRepository(reservaRepository)

            val disponible = crearLibro(titulo = "Disponible", autor = "Autor A", idPropietario = 10)
            val prestado = crearLibro(titulo = "Prestado", autor = "Autor B", idPropietario = 10)

            libroRepository.create(disponible)
            libroRepository.create(prestado)

            val resultado = libroRepository.buscarPorIdPropietario(
                propietarioId = 10,
                page = 0,
                size = 10,
                orden = OrdenLibroPerfil.FECHA_AGREGADO,
                direccion = DireccionOrden.ASCENDENTE,
                filtro = FiltroDisponibilidadPerfil.DISPONIBLES,
                librosPrestadosIds = setOf(prestado.id)
            )

            resultado.content.map { it.id } shouldContainExactly listOf(disponible.id)
        }

        it("ordena por titulo y luego por autor") {
            val reservaRepository = ReservaRepository()
            val libroRepository = LibroRepository(reservaRepository)

            val libroAutorZ = crearLibro(titulo = "Mismo", autor = "Zulu", idPropietario = 5)
            val libroAutorA = crearLibro(titulo = "Mismo", autor = "Alfa", idPropietario = 5)
            val libroTituloB = crearLibro(titulo = "Zoo", autor = "Beta", idPropietario = 5)

            libroRepository.create(libroAutorZ)
            libroRepository.create(libroAutorA)
            libroRepository.create(libroTituloB)

            val resultado = libroRepository.buscarPorIdPropietario(
                propietarioId = 5,
                page = 0,
                size = 10,
                orden = OrdenLibroPerfil.TITULO,
                direccion = DireccionOrden.ASCENDENTE,
                filtro = FiltroDisponibilidadPerfil.TODOS,
                librosPrestadosIds = emptySet()
            )

            resultado.content.map { it.id } shouldContainExactly listOf(libroAutorA.id, libroAutorZ.id, libroTituloB.id)
        }

        it("ordena por disponibilidad en descendente dejando prestados primero") {
            val reservaRepository = ReservaRepository()
            val libroRepository = LibroRepository(reservaRepository)

            val disponible = crearLibro(titulo = "Disponible", autor = "Autor A", idPropietario = 8)
            val prestado = crearLibro(titulo = "Prestado", autor = "Autor B", idPropietario = 8)

            libroRepository.create(disponible)
            libroRepository.create(prestado)

            val resultado = libroRepository.buscarPorIdPropietario(
                propietarioId = 8,
                page = 0,
                size = 10,
                orden = OrdenLibroPerfil.DISPONIBLE_PRESTADO,
                direccion = DireccionOrden.DESCENDENTE,
                filtro = FiltroDisponibilidadPerfil.TODOS,
                librosPrestadosIds = setOf(prestado.id)
            )

            resultado.content.map { it.id } shouldContainExactly listOf(prestado.id, disponible.id)
        }
    }
})

private fun crearLibro(
    titulo: String,
    tipoLibro: TipoLibro = comun,
    descripcion: String = "Descripcion",
    portada: String = "Portada",
    genero: Genero = Genero.CIENCIA_FICCION,
    autor: String,
    cantidadPaginas: Int = 200,
    isbn13: Long = 1234567890123,
    idioma: Idioma = Idioma.ESPANOL,
    editorial: String = "Editorial",
    fechaPublicacion: LocalDate = LocalDate.of(2020, 1, 1),
    estadoLibro: EstadoLibro = EstadoLibro.BUENO,
    idPropietario: Int
): Libro {
    return Libro(
        titulo = titulo,
        tipoLibro = tipoLibro,
        descripcion = descripcion,
        portada = portada,
        genero = genero,
        autor = autor,
        cantidadPaginas = cantidadPaginas,
        isbn13 = isbn13,
        idioma = idioma,
        editorial = editorial,
        fechaPublicacion = fechaPublicacion,
        estadoLibro = estadoLibro,
        idPropietario = idPropietario
    )
}

