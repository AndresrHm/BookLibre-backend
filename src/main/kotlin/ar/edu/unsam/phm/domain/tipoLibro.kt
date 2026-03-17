package ar.edu.unsam.phm.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import kotlin.math.ceil

interface TipoLibro {
    fun darKarma(reserva: Reserva): Int
    fun posibleKarmaALector(libro : Libro, user: Usuario) : Int
}

object comun : TipoLibro {
    override fun darKarma(reserva: Reserva): Int {
        val coefPorLector = if (reserva.usuarioLector.biblioKarma > 1000) 5 else 2

        return reserva.libro.cantidadPaginas * coefPorLector
    }

    override fun posibleKarmaALector(libro: Libro, user: Usuario): Int {
        val coefPorLector = if (user.biblioKarma > 1000) 5 else 2

        return libro.cantidadPaginas * coefPorLector
    }
}

object conDedicatoria : TipoLibro {
    override fun darKarma(reserva: Reserva): Int {
        return reserva.libro.cantidadReservas * 10 + 200
    }
    override fun posibleKarmaALector(libro: Libro, user: Usuario): Int {
        return libro.cantidadReservas * 10 + 200
    }
}

object coleccionable : TipoLibro {
    override fun darKarma(reserva: Reserva): Int {
        return ceil(reserva.usuarioLector.biblioKarma / 5.0).toInt() + reserva.libro.cantidadPaginas
    }
    override fun posibleKarmaALector(libro: Libro, user: Usuario): Int {
        return ceil(user.biblioKarma / 5.0).toInt() + libro.cantidadPaginas
    }
}