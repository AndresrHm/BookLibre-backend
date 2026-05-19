package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.LibroClickLog
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.LibroClickLogRepository
import org.springframework.stereotype.Service

@Service
class LibroClickLogService(
    private val repository: LibroClickLogRepository,
    private val libroRepository: LibroRepository,
) {
    fun logClick(libroId: String, libroTitulo: String, usuarioId: Long, libroPropietarioId: Long? = null) {
        val log = LibroClickLog(
            libroId = libroId,
            libroTitulo = libroTitulo,
            usuarioId = usuarioId,
            libroPropietarioId = libroPropietarioId,
        )
        repository.save(log)
    }

    fun obtenerClicksPorLibro(libroId: String): List<LibroClickLog> {
        return repository.findByLibroIdOrderByClickTimestampDesc(libroId)
    }

    fun obtenerLogsResumenClickLibroPorUsuario(usuarioId: Long): List<LibroClickLog> {
        // Si el id del libro fuera estable y nunca cambiara, este método podría simplificarse a algo como:
        //
        // fun obtenerLogsResumenClickLibroPorUsuario(usuarioId: Long): List<LibroClickLog> {
        //     val librosPropios = libroRepository.findByIdPropietario(usuarioId)
        //     val idsLibrosPropios = librosPropios.map { it.id }.toSet()
        //     if (idsLibrosPropios.isEmpty()) return emptyList()
        //
        //     return repository.findByLibroIdIn(idsLibrosPropios)
        //         .distinctBy { log -> log.id ?: "${log.libroId}-${log.libroTitulo}-${log.usuarioId}-${log.clickTimestamp}" }
        // }

        val librosPropios = libroRepository.findByIdPropietario(usuarioId)
        val idsLibrosPropios = librosPropios.map { it.id }.toSet()
        val titulosLibrosPropios = librosPropios.map { it.titulo }.toSet()
        val logsPorNuevoPropietario: List<LibroClickLog> = repository.findByLibroPropietarioId(usuarioId)
        val logsPorId: List<LibroClickLog> = if (idsLibrosPropios.isEmpty()) {
            emptyList()
        } else {
            repository.findByLibroIdIn(idsLibrosPropios)
        }
        val logsPorTitulo: List<LibroClickLog> = if (titulosLibrosPropios.isEmpty()) {
            emptyList()
        } else {
            repository.findByLibroTituloIn(titulosLibrosPropios)
        }
        //Aqui se logro mostrar el resumen de esta forma debido a que los libros
        //cuando se ejecuta el bootstrap se eliminan y crean los libros por lo cual el id nunca es el mismo
        //mas adelante cuando tengamos una persistencia total de ids podemos simplificar la busqueda de los mismos


        val logs: List<LibroClickLog> = (logsPorNuevoPropietario + logsPorId + logsPorTitulo)
            .distinctBy { log -> log.id ?: "${log.libroId}-${log.libroTitulo}-${log.usuarioId}-${log.clickTimestamp}" }
        return logs
    }
}