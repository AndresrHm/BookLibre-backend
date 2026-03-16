package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.PageResult
import ar.edu.unsam.phm.domain.paginate
import ar.edu.unsam.phm.dto.DireccionOrden
import ar.edu.unsam.phm.dto.FiltroDisponibilidadPerfil
import ar.edu.unsam.phm.dto.OrdenLibroPerfil
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class LibroRepository(
    private val reservaRepository: ReservaRepository
) : Repositorio<Libro>() {
    fun actualizarRating(libroID: Int, rating: Double) {
        val libro = getById(libroID)
        if (libro != null) libro.actualizarRating(rating) else throw BusinessException("No existe el libro con id ${libroID}.")
    }

    fun buscarPorIdPropietario(
        propietarioId: Int,
        page: Int,
        size: Int,
        orden: OrdenLibroPerfil,
        direccion: DireccionOrden,
        filtro: FiltroDisponibilidadPerfil,
        librosPrestadosIds: Set<Int> = emptySet()
    ): PageResult<Libro> {
        val librosDelUsuario = getElementos()
            .filter { it.idPropietario == propietarioId }

        val librosFiltrados = when (filtro) {
            FiltroDisponibilidadPerfil.TODOS -> librosDelUsuario
            FiltroDisponibilidadPerfil.DISPONIBLES -> librosDelUsuario.filter { it.id !in librosPrestadosIds }
            FiltroDisponibilidadPerfil.PRESTADOS -> librosDelUsuario.filter { it.id in librosPrestadosIds }
        }

        val comparadorBase = when (orden) {
            OrdenLibroPerfil.TITULO -> compareBy<Libro>({ it.titulo.lowercase() }, { it.autor.lowercase() })
            // false (disponible) queda antes que true (prestado) en orden ascendente
            OrdenLibroPerfil.DISPONIBLE_PRESTADO -> compareBy<Libro>({ it.id in librosPrestadosIds }, { it.titulo.lowercase() }, { it.autor.lowercase() })
            OrdenLibroPerfil.FECHA_AGREGADO -> compareBy<Libro> { it.fechaAgregado }
        }

        val comparadorFinal = when (direccion) {
            DireccionOrden.ASCENDENTE -> comparadorBase.thenBy { it.id }
            DireccionOrden.DESCENDENTE -> comparadorBase.reversed().thenBy { it.id }
        }

        val ordenados = librosFiltrados.sortedWith(comparadorFinal)
        return paginate(page, size, ordenados)
    }
    fun verDisponibilidadDelLibro(libroID: Int): String {
        val hoy = LocalDate.now()
        val prestado = reservaRepository.getElementos().any { reserva ->
            reserva.libro.id == libroID &&
            !hoy.isBefore(reserva.fechaRecogida) &&
            !hoy.isAfter(reserva.fechaDevolucion)
        }
        return if (prestado) "PRESTADO" else "DISPONIBLE"
    }
}

