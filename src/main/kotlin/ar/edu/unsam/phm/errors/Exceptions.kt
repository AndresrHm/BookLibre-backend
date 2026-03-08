package ar.edu.unsam.phm

import java.lang.RuntimeException//agrego archivo de errores
object ErrorMessages{
    const val ID_INEXISTENTE = "El ID no corresponde con ningun elemento del repositorio"
    const val PROC_NOT_FOUND = "No se encontraron procesos para ejecutar"
}

class BusinessException(msg : String): RuntimeException(msg)
class NotFoundException(msg : String): RuntimeException(msg)
