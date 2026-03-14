package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.Identificable
import ar.edu.unsam.phm.dto.UsuarioEdicionDTO
import java.time.LocalDate

class Usuario (
    var nombre: String,
    var apellido: String,
    var descripcion: String,
    var email: String,
    var celular: String,
    var ciudad: String,
    var tipoUsuario: TipoUsuario = lector,
    var urlImagen: String,
    override var id: Int = -1
): Identificable {
    var biblioKarma: Int = 0
    val listaDeLibrosParaPrestar = mutableListOf<Libro>()
    val listaDeLibrosPedidos = mutableListOf<Libro>()
    val fechaDeCreacion = LocalDate.now()

    fun agregarLibroParaPrestar(libro: Libro) {
        listaDeLibrosParaPrestar.add(libro)
    }

    fun sumaKarma(karma: Int) {
        biblioKarma += karma
    }

    fun update (usuarioNuevo: UsuarioEdicionDTO) {
        nombre = usuarioNuevo.nombre
        apellido = usuarioNuevo.apellido
        descripcion = usuarioNuevo.descripcion
        ciudad = usuarioNuevo.ciudad
        tipoUsuario = usuarioNuevo.tipoUsuario
        urlImagen = usuarioNuevo.urlImagen
    }

    fun getFechaCreacionAsString() : String = fechaDeCreacion.formatear()

}



