package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.Identificable

class Usuario (
    val nombre: String,
    val apellido: String,
    var descripcion: String,
    var email: String,
    var celular: String,
    var ciudad: String,
    var tipoUsuario: TipoUsuario,
    override var id: Int = -1
): Identificable {
    var biblioKarma: Int = 0
    val listaDeLibrosParaPrestar = mutableListOf<Libro>()
    val listaDeLibrosPedidos = mutableListOf<Libro>()

    fun agregarLibroParaPrestar(libro: Libro) {
        listaDeLibrosParaPrestar.add(libro)
    }

    fun sumaKarma(karma: Int) {
        biblioKarma += karma
    }
}



