package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.LibroClickLog
import ar.edu.unsam.phm.dto.LibroClickLogDTO
import ar.edu.unsam.phm.dto.LibroMasClickeadoDTO
import ar.edu.unsam.phm.service.LibroClickLogService
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ClickMapperTest : DescribeSpec({
    val service = mockk<LibroClickLogService>()
    val clickMapper = ClickMapper(service)

    describe("toDTO") {
        it("convierte una lista de logs a DTOs") {
            val logs = listOf(
                crearClickLog(libroId = "libro-a", libroTitulo = "Libro A", usuarioId = 1L, timestamp = "2026-05-10T09:00:00Z"),
                crearClickLog(libroId = "libro-b", libroTitulo = "Libro B", usuarioId = 2L, timestamp = "2026-05-11T09:00:00Z"),
            )

            val dtos = clickMapper.toDTO(logs)

            dtos shouldBe listOf(
                LibroClickLogDTO(libroId = "libro-a", libroTitulo = "Libro A", usuarioId = 1L, clickTimestamp = "2026-05-10T09:00:00Z"),
                LibroClickLogDTO(libroId = "libro-b", libroTitulo = "Libro B", usuarioId = 2L, clickTimestamp = "2026-05-11T09:00:00Z"),
            )
        }
    }

    describe("obtenerClicksPorLibro") {
        it("pide los logs al service y los transforma a DTOs") {
            val logs = listOf(
                crearClickLog(libroId = "libro-a", libroTitulo = "Libro A", usuarioId = 1L, timestamp = "2026-05-10T09:00:00Z"),
            )
            every { service.obtenerClicksPorLibro("libro-a") } returns logs

            val resultado = clickMapper.obtenerClicksPorLibro("libro-a")

            resultado shouldBe listOf(
                LibroClickLogDTO(libroId = "libro-a", libroTitulo = "Libro A", usuarioId = 1L, clickTimestamp = "2026-05-10T09:00:00Z"),
            )
            verify(exactly = 1) { service.obtenerClicksPorLibro("libro-a") }
        }
    }

    describe("obtenerResumenClickLibroPorUsuario") {
        it("arma el resumen desde logs de dominio") {
            val logs = listOf(
                crearClickLog(libroId = "libro-a", libroTitulo = "Libro A", usuarioId = 1L, timestamp = "2026-05-10T09:00:00Z"),
                crearClickLog(libroId = "libro-a", libroTitulo = "Libro A", usuarioId = 2L, timestamp = "2026-05-10T10:00:00Z"),
                crearClickLog(libroId = "libro-a", libroTitulo = "Libro A", usuarioId = 3L, timestamp = "2026-05-11T11:00:00Z"),
                crearClickLog(libroId = "libro-b", libroTitulo = "Libro B", usuarioId = 4L, timestamp = "2026-05-11T12:00:00Z"),
            )
            every { service.obtenerLogsResumenClickLibroPorUsuario(42L) } returns logs

            val resumen = clickMapper.obtenerResumenClickLibroPorUsuario(42L)

            resumen.libroMasClickado shouldBe LibroMasClickeadoDTO(
                titulo = "Libro A",
                clicks = 3,
            )
            resumen.totalClicks shouldBe 4
            resumen.promedioClicksDiarios shouldBe 2.0
            verify(exactly = 1) { service.obtenerLogsResumenClickLibroPorUsuario(42L) }
        }
    }
})

private fun crearClickLog(
    libroId: String,
    libroTitulo: String,
    usuarioId: Long,
    timestamp: String,
): LibroClickLog = LibroClickLog(
    libroId = libroId,
    libroTitulo = libroTitulo,
    usuarioId = usuarioId,
    clickTimestamp = timestamp,
)

