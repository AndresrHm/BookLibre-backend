package ar.edu.unsam.phm.domain

import kotlin.math.ceil

interface TipoLibroStrategy {
    fun darKarma(reserva: Reserva): Int
}

object Comun : TipoLibroStrategy {
    override fun darKarma(reserva: Reserva): Int {
        if (reserva.usuarioLector.biblioKarma > 1000) {
            return reserva.libro.cantidadPaginas * 5
        } else {
            return reserva.libro.cantidadPaginas * 2
        }
    }
}

object ConDedicatoria : TipoLibroStrategy {
    override fun darKarma(reserva: Reserva): Int {
        return reserva.libro.cantidadReservas * 10 + 200
    }
}

object Coleccionable : TipoLibroStrategy {
    override fun darKarma(reserva: Reserva): Int {
        return ceil(reserva.usuarioLector.biblioKarma / 5.0).toInt() + reserva.libro.cantidadPaginas
    }
}