package ar.edu.unsam.phm.domain

import kotlin.math.ceil

interface TipoLibro {
    fun darKarma(reserva: Reserva): Int
}

object comun : TipoLibro {
    override fun darKarma(reserva: Reserva): Int {
        val coefPorLector = if (reserva.usuarioLector.biblioKarma > 1000) 5 else 2

        return reserva.libro.cantidadPaginas * coefPorLector
    }
}

object conDedicatoria : TipoLibro {
    override fun darKarma(reserva: Reserva): Int {
        return reserva.libro.cantidadReservas * 10 + 200
    }
}

object coleccionable : TipoLibro {
    override fun darKarma(reserva: Reserva): Int {
        return ceil(reserva.usuarioLector.biblioKarma / 5.0).toInt() + reserva.libro.cantidadPaginas
    }
}