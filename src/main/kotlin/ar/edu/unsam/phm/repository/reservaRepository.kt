package ar.edu.unsam.phm.repository
import ar.edu.unsam.phm.domain.Reserva
import org.springframework.stereotype.Component

@Component
open class RepositorioReserva : Repositorio<Reserva>() {

    fun findByLectorID(
        userID: Int,
        page: Int,
        size: Int,
    ): PageResult<Reserva> {

        val filtered = getElementos().filter { it.usuarioLector.id == userID }
        return paginate(page, size, filtered)
    }

    fun findByPrestadorID(
        userID: Int,
        page: Int,
        size: Int,
    ): PageResult<Reserva> {

        val filtered = getElementos().filter { it.usuarioPrestador.id == userID }
        return paginate(page, size, filtered)
    }

    private fun paginate(
        page: Int,
        size: Int,
        filtered: List<Reserva>
    ): PageResult<Reserva> {
        val start = page * size
        val end = minOf(start + size, filtered.size)

        val pageContent =
            if (start >= filtered.size) emptyList()
            else filtered.subList(start, end)

        return PageResult(
            content = pageContent,
            totalElements = filtered.size
        )
    }

    data class PageResult<T>(
        val content: List<T>,
        val totalElements: Int,
    )
}