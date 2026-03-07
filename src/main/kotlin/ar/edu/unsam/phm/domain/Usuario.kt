package ar.edu.unsam.phm.domain
//En primer lugar tenemos a los usuarios. De los mismos se sabe que tienen
//Nombre completo (nombre y apellido)
//Una breve descripción de un renglón (por ejemplo “Lector intrépido”)
//Su email
//Su nro de celular
//La ciudad donde reside
//El tipo de usuario (o rol dentro de la aplicación que puede ser: lector, publicador o lector / publicador) Por el momento no tiene comportamiento asociado.

class Usuario(
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



