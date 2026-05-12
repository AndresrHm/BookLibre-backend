package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Genero
import org.springframework.data.mongodb.core.query.Criteria

data class LibroFiltrosMongo(
    val search: String?,
    val isbn13: Long?,
    val paginasMin: Int?,
    val paginasMax: Int?,
    val generos: List<Genero>?,
    val soloIdPropietario: Long?,
    val excluirIdPropietario: Long?,
    val excluirIds: Set<String>,
)

object CriterioFiltrosLibrosBuilder {
    fun build(filtros: LibroFiltrosMongo): Criteria {
        val parts = mutableListOf(Criteria.where("activo").`is`(true))

        filtros.search?.trim()?.takeIf { it.isNotBlank() }?.let {
            parts.add(Criteria.where("titulo").regex(it, "i"))
        }
        filtros.isbn13?.let { parts.add(Criteria.where("isbn13").`is`(it)) }
        filtros.paginasMin?.let { parts.add(Criteria.where("cantidadPaginas").gte(it)) }
        filtros.paginasMax?.let { parts.add(Criteria.where("cantidadPaginas").lte(it)) }
        filtros.generos?.takeIf { it.isNotEmpty() }?.let {
            parts.add(Criteria.where("genero").`in`(it.map { genero -> genero.name }))
        }
        filtros.soloIdPropietario?.let { parts.add(Criteria.where("idPropietario").`is`(it)) }
        filtros.excluirIdPropietario?.let { parts.add(Criteria.where("idPropietario").ne(it)) }
        filtros.excluirIds.takeIf { it.isNotEmpty() }?.let { parts.add(Criteria.where("_id").nin(it)) }

        return Criteria().andOperator(*parts.toTypedArray())
    }
}

