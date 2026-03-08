package ar.edu.unsam.phm.domain

class Usuario (
    val nombre: String,
    val apellido: String,
    var descripcion: String,
    var email: String,
    var celular: String,
    var ciudad: String,
    var tipoUsuario: TipoUsuario
) {
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



