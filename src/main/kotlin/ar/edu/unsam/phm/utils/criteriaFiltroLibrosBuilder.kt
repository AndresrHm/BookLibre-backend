package ar.edu.unsam.phm.utils

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
// Inspirados en el predicate usado para la entrega anterior, en este caso
// no se puede usar tal funcionalidad, por lo que se uso un builder de Criteria
// Criteria es un objeto que transforma peticiones de kotlin a querys que mongo pueda
// entender, algo parecido al specification que teniamos anteriormente.

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

