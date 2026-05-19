package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.LibroClickLog
import ar.edu.unsam.phm.repository.LibroClickLogRepository
import ar.edu.unsam.phm.repository.LibroRepository
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class LibroClickLogServiceTest : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    val clickLogRepository = mockk<LibroClickLogRepository>(relaxed = true)
    val libroRepository = mockk<LibroRepository>(relaxed = true)
    val service = LibroClickLogService(clickLogRepository, libroRepository)

    describe("obtenerClicksPorLibro") {
        it("devuelve los clicks del libro en orden descendente") {
            val click1 = crearClickLog(libroId = "libro-a", libroTitulo = "Libro A", usuarioId = 1L, propietarioId = 42L, timestamp = "2026-05-10T09:00:00Z")
            val click2 = crearClickLog(libroId = "libro-a", libroTitulo = "Libro A", usuarioId = 2L, propietarioId = 42L, timestamp = "2026-05-10T10:00:00Z")
            every { clickLogRepository.findByLibroIdOrderByClickTimestampDesc("libro-a") } returns listOf(click2, click1)

            val clicks = service.obtenerClicksPorLibro("libro-a")

            clicks shouldBe listOf(click2, click1)
            verify(exactly = 1) { clickLogRepository.findByLibroIdOrderByClickTimestampDesc("libro-a") }
        }
    }

    describe("obtenerLogsResumenClickLibroPorUsuario") {
        it("prioriza los logs nuevos que ya traen el id del dueño del libro") {
            val usuarioId = 42L
            every { libroRepository.findByIdPropietario(usuarioId) } returns emptyList()
            every { clickLogRepository.findByLibroPropietarioId(usuarioId) } returns listOf(
                crearClickLog(libroId = "libro-a", libroTitulo = "Libro A", usuarioId = 1L, propietarioId = usuarioId, timestamp = "2026-05-10T09:00:00Z"),
                crearClickLog(libroId = "libro-a", libroTitulo = "Libro A", usuarioId = 2L, propietarioId = usuarioId, timestamp = "2026-05-10T10:00:00Z"),
                crearClickLog(libroId = "libro-a", libroTitulo = "Libro A", usuarioId = 3L, propietarioId = usuarioId, timestamp = "2026-05-10T11:00:00Z"),
                crearClickLog(libroId = "libro-b", libroTitulo = "Libro B", usuarioId = 4L, propietarioId = usuarioId, timestamp = "2026-05-11T12:00:00Z"),
            )
            every { clickLogRepository.findByLibroIdIn(any()) } returns emptyList()
            every { clickLogRepository.findByLibroTituloIn(any()) } returns emptyList()

            val logs = service.obtenerLogsResumenClickLibroPorUsuario(usuarioId)

            logs.size shouldBe 4
            verify(exactly = 1) { libroRepository.findByIdPropietario(usuarioId) }
            verify(exactly = 1) { clickLogRepository.findByLibroPropietarioId(usuarioId) }
        }

        it("usa los títulos legacy cuando todavía no existe el id del dueño en los logs") {
            val usuarioId = 7L
            every { libroRepository.findByIdPropietario(usuarioId) } returns listOf(
                mockk {
                    every { id } returns "libro-x"
                    every { titulo } returns "Libro Legacy"
                }
            )
            every { clickLogRepository.findByLibroPropietarioId(usuarioId) } returns emptyList()
            every { clickLogRepository.findByLibroIdIn(setOf("libro-x")) } returns emptyList()
            every { clickLogRepository.findByLibroTituloIn(setOf("Libro Legacy")) } returns listOf(
                crearClickLog(libroId = "id-viejo-1", libroTitulo = "Libro Legacy", usuarioId = 100L, propietarioId = null, timestamp = "2026-05-10T09:00:00Z"),
                crearClickLog(libroId = "id-viejo-2", libroTitulo = "Libro Legacy", usuarioId = 101L, propietarioId = null, timestamp = "2026-05-11T09:00:00Z"),
            )

            val logs = shouldNotThrowAny { service.obtenerLogsResumenClickLibroPorUsuario(usuarioId) }

            logs.size shouldBe 2
        }
    }
})


private fun crearClickLog(
    libroId: String,
    libroTitulo: String,
    usuarioId: Long,
    propietarioId: Long?,
    timestamp: String,
): LibroClickLog = LibroClickLog(
    libroId = libroId,
    libroTitulo = libroTitulo,
    usuarioId = usuarioId,
    libroPropietarioId = propietarioId,
    clickTimestamp = timestamp,
)




