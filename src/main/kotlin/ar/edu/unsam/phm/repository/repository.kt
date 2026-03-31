package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.errors.ErrorStrings

interface Identificable {
    var id: Long?
}

abstract class Repositorio<T : Identificable> {
    private val elementos = mutableListOf<T>()
    private var idIncremental = 1

    open fun create(elemento: T) {
        elemento.id = idIncremental.toLong()
        idIncremental += 1
        elementos.add(elemento)
    }

    fun delete(elemento: T) = elementos.removeIf { it.id == elemento.id }

    fun update(elemento: T) {
        if (!elementosID().contains(elemento.id)) throw ElementoNoEncontrado(ErrorStrings.ELEMENTO_NO_ENCONTRADO_ID)
        delete(elemento)
        elementos.add(elemento)
    }

    fun getById(id: Int): T? = elementos.firstOrNull { it.id == id.toLong() }

//    abstract fun search(string: String): List<T> Por el momento no se especifica un search()

    fun getElementos(): List<T> = elementos.toList()

    fun elementosID() = elementos.map { it.id }

    fun clearInit() {
        elementos.clear()
        idIncremental = 1
    }
}

class ElementoNoEncontrado(msg: String) : RuntimeException(msg)