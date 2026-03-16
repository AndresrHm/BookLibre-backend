package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.PageResult
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.paginate
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Repository
open class ReservaRepository : Repositorio<Reserva>() {

    // reservas donde el user es lector
    fun findByLectorID(
        userID: Int,
        page: Int,
        size: Int,
        string: String?,
    ): PageResult<Reserva> {

        var filtered = getElementos().filter { it.usuarioLector.id == userID }

        if (string != null) {
            filtered = filtered.filter { r ->
                r.libro.titulo.lowercase().contains(string.lowercase()) ||
                r.libro.autor.lowercase().contains(string.lowercase())
            }
        }

        return paginate(page, size, filtered)
    }

    // reservas donde el user es prestador
    fun findByPrestadorID(
        userID: Int,
        page: Int,
        size: Int,
        string: String?,
    ): PageResult<Reserva> {

        var filtered = getElementos().filter { it.usuarioPrestador.id == userID }

        if (string != null) {
            filtered = filtered.filter { r ->
                r.libro.titulo.lowercase().contains(string.lowercase()) ||
                        r.libro.autor.lowercase().contains(string.lowercase())
            }
        }

        return paginate(page, size, filtered)
    }

    // obtenemos última reseña (userID + libroID)
    fun obtenerUltimaReserva(usuarioID: Int, libroID: Int): Reserva? {
        return getElementos()
            .filter { it.usuarioLector.id == usuarioID && it.libro.id == libroID }
            .maxByOrNull { it.fechaDevolucion }
    }
}