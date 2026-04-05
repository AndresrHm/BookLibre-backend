package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.requireBusiness
import java.time.LocalDate

data class ReservaDTO(
    val reservaId: Long?,
    val libroId: Long?,
    val titulo: String,
    val autor: String,
    val imagenUrl: String,
    val calificacionLibro: Double,
    val usuario: String,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val estado: EstadoReserva,
    val puedeCalificar: Boolean,
    val biblioKarma: Int,
) {
}

data class CreacionReservaDTO(
    val libroId: Long?,
    val usuarioPrestadorId: Long?,
    val usuarioLectorId: Long?,
    val fechaRecogida: LocalDate,
    val fechaDevolucion: LocalDate,
) {
    init {
        requireBusiness(fechaDevolucion.isAfter(fechaRecogida), ErrorStrings.FECHA_DEVOLUCION_POSTERIOR_RECOGIDA)
        requireBusiness(usuarioLectorId != usuarioPrestadorId, ErrorStrings.USUARIO_PRESTADOR_Y_LECTOR_DISTINTOS)
        // TODO: Realizar estas dos validaciones en el service.
//        requireBusiness(usuarioPrestador.id == libro.idPropietario, "El libro no pertenece al usuario prestador")
//        requireBusiness(usuarioLector.id != libro.idPropietario, "Un usuario no puede reservar su propio libro")
    }
}

enum class EstadoReserva {
    ACTIVO,
    PROXIMO_A_VENCER,
    DEVUELTO,
}