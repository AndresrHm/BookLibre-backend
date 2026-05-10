package ar.edu.unsam.phm.domain

import java.lang.Math.ceil

enum class TipoLibro {
    COMUN {
        override fun calcularKarma(libro: Libro, cantidadReservas: Int, user: Usuario): Int {
            val coefPorLector = if (user.biblioKarma > 1000) 5 else 2

            return libro.cantidadPaginas * coefPorLector
        }
    },
    CON_DEDICATORIA {
        override fun calcularKarma(libro: Libro, cantidadReservas: Int, user: Usuario): Int {
            return cantidadReservas * 10 + 200
        }
    },
    COLECCIONABLE {
        override fun calcularKarma(libro: Libro, cantidadReservas: Int, user: Usuario): Int {
            return ceil(user.biblioKarma / 5.0).toInt() + libro.cantidadPaginas
        }
    };

    fun darKarma(libro: Libro, usuarioLector: Usuario, cantidadReservas: Int): Int {
        return calcularKarma(libro, cantidadReservas, usuarioLector)
    }
    abstract fun calcularKarma(libro: Libro, cantidadReservas: Int, user: Usuario): Int

}