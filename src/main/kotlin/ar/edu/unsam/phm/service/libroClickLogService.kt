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
        val librosPropios = libroRepository.findByIdPropietario(usuarioId)
        val idsLibrosPropios = librosPropios.map { it.id }.toSet()
        if (idsLibrosPropios.isEmpty()) return emptyList()

        return repository.findByLibroIdIn(idsLibrosPropios)
            .distinctBy { log ->
                log.id ?: "${log.libroId}-${log.libroTitulo}-${log.usuarioId}-${log.clickTimestamp}"
            }


    }
}
