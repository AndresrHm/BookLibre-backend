package ar.edu.unsam.phm.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "tipoUsuario"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Publicador::class, name = "publicador"),
    JsonSubTypes.Type(value = Lector::class, name = "lector"),
    JsonSubTypes.Type(value = LectorPublicador::class, name = "lectorPublicador")
)
interface TipoUsuario {
    fun esUsuario(usuario: Usuario)
}

@JsonTypeName("publicador")
object Publicador : TipoUsuario {
    override fun esUsuario(usuario: Usuario) {
    }
}

@JsonTypeName("lector")
object Lector : TipoUsuario {
    override fun esUsuario(usuario: Usuario) {
    }
}

@JsonTypeName("lectorPublicador")
object LectorPublicador : TipoUsuario {
    override fun esUsuario(usuario: Usuario) {
    }
}

///   aun no tienen ninguna accion en el enunciado