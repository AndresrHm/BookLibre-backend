package ar.edu.unsam.phm.errors

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    // Maneja excepciones de negocio (BusinessException)
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to (ex.message ?: "Error de negocio")))
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<Map<String, String>> {
        val error = mapOf("message" to (ex.message ?: "Recurso no encontrado"))
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    // Maneja otros errores (Validaciones, 400s, 500s)
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<Map<String, String>> {
        // Obliga a devolver un objeto con la clave 'message', para mostrarlo correctamente con los toast
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("message" to (ex.message ?: "Ocurrió un error inesperado")))
    }
}