package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.LibroClickLog
import org.springframework.data.mongodb.repository.MongoRepository

interface LibroClickLogRepository : MongoRepository<LibroClickLog, String> {
	fun findByLibroIdOrderByClickTimestampDesc(libroId: String): List<LibroClickLog>

	fun findByLibroIdIn(libroIds: Collection<String>): List<LibroClickLog>

	fun findByLibroTituloIn(libroTitulos: Collection<String>): List<LibroClickLog>

	fun findByLibroPropietarioId(libroPropietarioId: Long): List<LibroClickLog>

	fun findByUsuarioIdOrderByClickTimestampDesc(usuarioId: Long): List<LibroClickLog>

	fun countByUsuarioId(usuarioId: Long): Long
}
