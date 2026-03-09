package ar.edu.unsam.phm.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import kotlin.math.ceil

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "tipoLibro" // Este es el campo que buscará en el JSON
)
@JsonSubTypes(
    JsonSubTypes.Type(value = comun::class, name = "comun"),
    JsonSubTypes.Type(value = conDedicatoria::class, name = "conDedicatoria"),
    JsonSubTypes.Type(value = coleccionable::class, name = "coleccionable")
)
interface TipoLibro {
    fun darKarma(reserva: Reserva): Int
}

@JsonTypeName("comun")
object comun : TipoLibro {
    override fun darKarma(reserva: Reserva): Int {
        val coefPorLector = if (reserva.usuarioLector.biblioKarma > 1000) 5 else 2

        return reserva.libro.cantidadPaginas * coefPorLector
    }
}

@JsonTypeName("conDedicatoria")
object conDedicatoria : TipoLibro {
    override fun darKarma(reserva: Reserva): Int {
        return reserva.libro.cantidadReservas * 10 + 200
    }
}

@JsonTypeName("coleccionable")
object coleccionable : TipoLibro {
    override fun darKarma(reserva: Reserva): Int {
        return ceil(reserva.usuarioLector.biblioKarma / 5.0).toInt() + reserva.libro.cantidadPaginas
    }
}