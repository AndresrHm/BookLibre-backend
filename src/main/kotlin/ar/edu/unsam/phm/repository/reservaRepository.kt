package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.PageResult
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.paginate
import org.springframework.stereotype.Repository
import java.time.LocalDate

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

    // obtener reservas asociadas a un libro
    fun findByLibroId(libroID: Int) : List<Reserva> {
        val listaDeReservas = getElementos().filter { it.libro.id == libroID }
        return listaDeReservas
    }

    // obtenemos última reserva (userID + libroID)
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
    fun getLibrosReservadosEntreFechas(fechaInicio: LocalDate, fechaFin: LocalDate): List<Libro> {
        require(!fechaInicio.isAfter(fechaFin)) { "fechaInicio no puede ser mayor a fechaFin" }

        return getElementos()
            .filter { reserva ->
                // Solapamiento inclusivo del rango [fechaInicio, fechaFin]
                !reserva.fechaRecogida.isAfter(fechaFin) && !reserva.fechaDevolucion.isBefore(fechaInicio)
            }
            .map { it.libro }
            .distinctBy { it.id }
    }

    fun getLibrosPrestadosPorUsuario(usuarioPrestadorId: Int): List<Libro> {
        return getElementos()
            .filter { it.usuarioPrestador.id == usuarioPrestadorId }
            .map { it.libro }
    }
}