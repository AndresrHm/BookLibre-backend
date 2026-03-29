package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.repository.Identificable
import java.time.LocalDate

class Usuario(
    override var id: Int = -1,
    var nombre: String,
    var apellido: String,
    var descripcion: String,
    var email: String,
    var celular: String,
    var ciudad: String,
    var tipoUsuario: TipoUsuario = TipoUsuario.LECTOR,
    var urlImagen: String,
) : Identificable {
    var biblioKarma: Int = 0
    val listaDeLibrosParaPrestar = mutableListOf<Libro>()
    var cantidadDeLibrosPedidos : Int = 0  //// ver mas adealnte
    val fechaDeCreacion = LocalDate.now()
    lateinit var passwordSesion: String

    fun agregarLibroParaPrestar(libro: Libro) {
        listaDeLibrosParaPrestar.add(libro)
    }

    fun sumarKarma(karma: Int) {
        if (karma <= 0)
            throw BusinessException("Solo se puede sumar karma positivo")

        biblioKarma += karma
    }
    fun agregarLibroLeido() {
        cantidadDeLibrosPedidos += 1
    }

    fun crearReserva(
        libro: Libro,
        usuarioPrestador: Usuario,
        fechaRecogida: LocalDate,
        fechaDevolucion: LocalDate,
        fechasDeReservas: List<Pair<LocalDate, LocalDate>>
    ): Reserva {
        libro.reservar(fechaRecogida, fechaDevolucion, fechasDeReservas)

        val reserva = Reserva(
            libro = libro,
            usuarioPrestador = usuarioPrestador,
            usuarioLector = this,
            fechaRecogida = fechaRecogida,
            fechaDevolucion = fechaDevolucion,
        )
        agregarLibroLeido()
        sumarKarma(reserva.darKarma())
        return reserva
    }

    fun getFechaCreacionAsString(): String = fechaDeCreacion.formatear()

    fun esLector(): Boolean = this.tipoUsuario == TipoUsuario.LECTOR

    fun esPublicador(): Boolean = this.tipoUsuario == TipoUsuario.PUBLICADOR

    fun esLectorPublicador(): Boolean = this.tipoUsuario == TipoUsuario.LECTORPUBLICADOR
}

enum class TipoUsuario {
    LECTOR, PUBLICADOR, LECTORPUBLICADOR
}



