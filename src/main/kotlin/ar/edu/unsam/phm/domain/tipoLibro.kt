package ar.edu.unsam.phm.domain

import kotlin.math.ceil

interface TipoLibro {
    fun darKarma(reserva: reserva): Int
}

object Comun : TipoLibro {
    override fun darKarma(reserva: reserva): Int {
        if (reserva.usuarioLector.biblioKarma > 1000) {
            return reserva.libro.cantidadPaginas * 5
        } else {
            return reserva.libro.cantidadPaginas * 2
        }
    }
}

object ConDedicatoria : TipoLibro {
    override fun darKarma(reserva: reserva): Int {
        return reserva.libro.cantidadReservas * 10 + 200
    }
}

object Coleccionable : TipoLibro {
    override fun darKarma(reserva: reserva): Int {
        return ceil(reserva.usuarioLector.biblioKarma / 5.0).toInt() + reserva.libro.cantidadPaginas
    }
}