package ar.edu.unsam.phm.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

/*@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "tipoUsuario"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = publicador::class, name = "publicador"),
    JsonSubTypes.Type(value = lector::class, name = "lector"),
    JsonSubTypes.Type(value = lectorPublicador::class, name = "lectorPublicador")
)


interface TipoUsuario {
    fun esUsuario(usuario: Usuario)
}

@JsonTypeName("publicador")
object publicador : TipoUsuario {
    override fun esUsuario(usuario: Usuario) {
    }
}

@JsonTypeName("lector")
object lector : TipoUsuario {
    override fun esUsuario(usuario: Usuario) {
    }
}

@JsonTypeName("lectorPublicador")
object lectorPublicador : TipoUsuario {
    override fun esUsuario(usuario: Usuario) {
    }
}
*/
///   aun no tienen ninguna accion en el enunciado