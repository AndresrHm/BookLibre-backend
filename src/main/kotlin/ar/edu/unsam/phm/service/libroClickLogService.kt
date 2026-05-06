package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.LibroClickLog
import ar.edu.unsam.phm.repository.LibroClickLogRepository
import org.springframework.stereotype.Service

@Service
class LibroClickLogService(
    private val repository: LibroClickLogRepository
) {
    fun logClick(libroId: Long, libroTitulo: String, usuarioNombre: String) {
        val log = LibroClickLog(
            libroId = libroId,
            libroTitulo = libroTitulo,
            usuarioNombre = usuarioNombre,
        )
        repository.save(log)
    }
}