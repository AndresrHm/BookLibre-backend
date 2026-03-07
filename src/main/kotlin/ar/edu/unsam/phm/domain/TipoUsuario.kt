package ar.edu.unsam.phm.domain

interface TipoUsuario {
    fun esUsuario(usuario: Usuario)
}

object Publicador : TipoUsuario {
    override fun esUsuario(usuario: Usuario) {
    }
}

object Lector : TipoUsuario {
    override fun esUsuario(usuario: Usuario) {
    }
}

object LectorPublicador : TipoUsuario {
    override fun esUsuario(usuario: Usuario) {
    }
}

///   aun no tienen ninguna accion en el enunciado