package ar.edu.unsam.phm.errors

import com.mongodb.MongoTimeoutException
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to ex.message!!))
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<Map<String, String>> {
        val error = mapOf("message" to ex.message!!)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    // Por el momento no funciona, deberíamos ver dónde se tira el exception de mongo
    @ExceptionHandler(MongoTimeoutException::class)
    fun handleMongoTimeoutException(
        ex: MongoTimeoutException
    ): ResponseEntity<Map<String, String>> {

        ex.printStackTrace() // log interno

        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(
                mapOf(
                    "message" to
                            "No se pudo conectar con el servidor."
                )
            )
    }

    // Maneja otros errores (Validaciones, 400s, 500s)
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<Map<String, String>> {

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                mapOf(
                    "message" to when {
                        ex.message?.contains("Timed out while waiting for a server") == true ->
                            "No se pudo conectar con el servidor."

                        ex.message?.contains("Mongo") == true ->
                            "No se pudo conectar con el servidor."

                        else ->
                            ex.message ?: "No se pudo conectar con el servidor."
                    }
                )
            )
    }
}