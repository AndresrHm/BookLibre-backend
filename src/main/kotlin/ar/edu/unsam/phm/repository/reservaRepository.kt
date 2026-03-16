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
    ): PageResult<Reserva> {

        val filtered = getElementos().filter { it.usuarioLector.id == userID }
        return paginate(page, size, filtered)
    }

    // reservas donde el user es prestador
    fun findByPrestadorID(
        userID: Int,
        page: Int,
        size: Int,
    ): PageResult<Reserva> {

        val filtered = getElementos().filter { it.usuarioPrestador.id == userID }
        return paginate(page, size, filtered)
    }

    // obtenemos última reseña (userID + libroID)
    fun obtenerUltimaReserva(usuarioID: Int, libroID: Int): Reserva? {
        return getElementos()
            .filter { it.usuarioLector.id == usuarioID && it.libro.id == libroID }
            .maxByOrNull { it.fechaDevolucion }
    }

    // cantidad de reservas de un dueño
    fun obtenerCantidadDeReservasDeAlgunDuenio(usuarioID: Int) : Int {
        val listaDeReservasFiltradas = getElementos().filter { it.usuarioPrestador.id == usuarioID }
        return listaDeReservasFiltradas.size
    }
}