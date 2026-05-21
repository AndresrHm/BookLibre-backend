package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.LibroClickLog
import ar.edu.unsam.phm.dto.LibroClickLogDTO
import ar.edu.unsam.phm.dto.LibroMasClickeadoDTO
import ar.edu.unsam.phm.dto.ResumenClickLibroPorUsuarioDTO
import ar.edu.unsam.phm.service.LibroClickLogService
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException

@Component
class ClickMapper(
    private val libroClickLogService: LibroClickLogService,
) {
    fun toDTO(obj: LibroClickLog): LibroClickLogDTO {
        return LibroClickLogDTO(
            libroId = obj.libroId,
            libroTitulo = obj.libroTitulo,
            usuarioId = obj.usuarioId,
            clickTimestamp = obj.clickTimestamp,
        )
    }

    fun toDTO(obj: List<LibroClickLog>): List<LibroClickLogDTO> {
        return obj.map { toDTO(it) }
    }

    fun obtenerClicksPorLibro(libroId: String): List<LibroClickLogDTO> {
        return toDTO(libroClickLogService.obtenerClicksPorLibro(libroId))
    }

    fun obtenerResumenClickLibroPorUsuario(usuarioId: Long): ResumenClickLibroPorUsuarioDTO {
        return toResumenDTO(libroClickLogService.obtenerLogsResumenClickLibroPorUsuario(usuarioId))
    }

    private fun toResumenDTO(logs: List<LibroClickLog>): ResumenClickLibroPorUsuarioDTO {
        if (logs.isEmpty()) {
            return ResumenClickLibroPorUsuarioDTO(
                libroMasClickado = LibroMasClickeadoDTO(
                    titulo = "",
                    clicks = 0,
                ),
                totalClicks = 0,
                promedioClicksDiarios = 0.0,
            )
        }

        val clicksTotales = logs.size.toLong()
        val cantidadDiasConClicks = logs
            .mapNotNull { log -> log.toLocalDateOrNull() }
            .distinct()
            .size
            .toLong()

        val librosAgrupados = logs.groupBy { log -> log.libroId to log.libroTitulo }
        val libroMasClickeado = librosAgrupados
            .maxByOrNull { entry -> entry.value.size }
            ?.let { entry ->
                val clave = entry.key
                val clics = entry.value
                LibroMasClickeadoDTO(
                    titulo = clave.second,
                    clicks = clics.size.toLong(),
                )
            } ?: LibroMasClickeadoDTO(
            titulo = "",
            clicks = 0,
        )

        val divisor = if (cantidadDiasConClicks == 0L) 1.0 else cantidadDiasConClicks.toDouble()
        val promedioClicksDiarios = clicksTotales.toDouble() / divisor

        return ResumenClickLibroPorUsuarioDTO(
            libroMasClickado = libroMasClickeado,
            totalClicks = clicksTotales,
            promedioClicksDiarios = promedioClicksDiarios,
        )
    }

    private fun LibroClickLog.toLocalDateOrNull() = try {
        ZonedDateTime.parse(clickTimestamp).toLocalDate()
    } catch (_: DateTimeParseException) {
        null
    }
}

