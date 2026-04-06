package ar.edu.unsam.phm.domain

import kotlin.math.ceil

interface TipoLibro {
    fun darKarma(reserva: Reserva, cantidadReservas: Int): Int
    fun posibleKarmaALector(libro : Libro, cantidadReservas: Int ,user: Usuario) : Int
}

object comun : TipoLibro {
    override fun darKarma(reserva: Reserva, cantidadReservas: Int): Int {
        val coefPorLector = if (reserva.usuarioLector.biblioKarma > 1000) 5 else 2

        return reserva.libro.cantidadPaginas * coefPorLector
    }

    override fun posibleKarmaALector(libro: Libro, cantidadReservas: Int, user: Usuario): Int {
        val coefPorLector = if (user.biblioKarma > 1000) 5 else 2

        return libro.cantidadPaginas * coefPorLector
    }
}

object conDedicatoria : TipoLibro {
    override fun darKarma(reserva: Reserva, cantidadReservas: Int): Int {
        return cantidadReservas * 10 + 200
    }
    override fun posibleKarmaALector(libro: Libro, cantidadReservas: Int, user: Usuario): Int {
        return cantidadReservas * 10 + 200
    }
}

object coleccionable : TipoLibro {
    override fun darKarma(reserva: Reserva, cantidadReservas: Int): Int {
        return ceil(reserva.usuarioLector.biblioKarma / 5.0).toInt() + reserva.libro.cantidadPaginas
    }
    override fun posibleKarmaALector(libro: Libro, cantidadReservas: Int, user: Usuario): Int {
        return ceil(user.biblioKarma / 5.0).toInt() + libro.cantidadPaginas
    }
}