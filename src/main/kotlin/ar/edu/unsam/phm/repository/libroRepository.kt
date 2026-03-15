package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.PageResult
import ar.edu.unsam.phm.domain.paginate
import ar.edu.unsam.phm.dto.DireccionOrden
import ar.edu.unsam.phm.dto.OrdenCalificacion
import ar.edu.unsam.phm.dto.OrdenLibroPerfil
import org.springframework.stereotype.Repository

@Repository
class LibroRepository : Repositorio<Libro>() {
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
        librosPrestadosIds: Set<Int> = emptySet()
    ): PageResult<Libro> {
        val librosDelUsuario = getElementos()
            .filter { it.idPropietario == propietarioId }

        val comparadorBase = when (orden) {
            OrdenLibroPerfil.TITULO -> compareBy<Libro> { it.titulo.lowercase() }
            // false (disponible) queda antes que true (prestado) en orden ascendente
            OrdenLibroPerfil.DISPONIBLE_PRESTADO -> compareBy<Libro> { it.id in librosPrestadosIds }
            OrdenLibroPerfil.FECHA_AGREGADO -> compareBy<Libro> { it.fechaAgregado }
        }

        val comparadorFinal = when (direccion) {
            DireccionOrden.ASCENDENTE -> comparadorBase.thenBy { it.id }
            DireccionOrden.DESCENDENTE -> comparadorBase.reversed().thenBy { it.id }
        }

        val ordenados = librosDelUsuario.sortedWith(comparadorFinal)
        return paginate(page, size, ordenados)
    }
}

