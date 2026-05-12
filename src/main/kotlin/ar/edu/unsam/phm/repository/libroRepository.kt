package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Libro
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

interface LibroRepository : MongoRepository<Libro, Long>, LibroRepositoryCustom {
    fun deleteByIdPropietario(idPropietario: Long)
    fun findByActivoTrue(): List<Libro>
    fun findByActivoTrueAndIdPropietario(idPropietario: Long): List<Libro>
}

interface LibroRepositoryCustom {
    fun buscarConFiltros(
        search: String?,
        isbn13: Long?,
        paginasMin: Int?,
        paginasMax: Int?,
        generos: List<Genero>?,
        soloIdPropietario: Long?,
        excluirIdPropietario: Long?,
        excluirIds: Set<Long>,
        pageable: Pageable,
    ): Page<Libro>
}

@Repository
class LibroRepositoryImpl(private val mongoTemplate: MongoTemplate) : LibroRepositoryCustom {
    override fun buscarConFiltros(
        search: String?,
        isbn13: Long?,
        paginasMin: Int?,
        paginasMax: Int?,
        generos: List<Genero>?,
        soloIdPropietario: Long?,
        excluirIdPropietario: Long?,
        excluirIds: Set<Long>,
        pageable: Pageable,
    ): Page<Libro> {
        val parts = mutableListOf(Criteria.where("activo").`is`(true))

        search?.trim()?.takeIf { it.isNotBlank() }?.let {
            parts.add(Criteria.where("titulo").regex(it, "i"))
        }
        isbn13?.let { parts.add(Criteria.where("isbn13").`is`(it)) }
        paginasMin?.let { parts.add(Criteria.where("cantidadPaginas").gte(it)) }
        paginasMax?.let { parts.add(Criteria.where("cantidadPaginas").lte(it)) }
        generos?.takeIf { it.isNotEmpty() }?.let {
            parts.add(Criteria.where("genero").`in`(it.map { g -> g.name }))
        }
        soloIdPropietario?.let { parts.add(Criteria.where("idPropietario").`is`(it)) }
        excluirIdPropietario?.let { parts.add(Criteria.where("idPropietario").ne(it)) }
        excluirIds.takeIf { it.isNotEmpty() }?.let { parts.add(Criteria.where("_id").nin(it)) }

        val criteria = Criteria().andOperator(*parts.toTypedArray())
        val query = Query(criteria).with(pageable)
        val countQuery = Query(criteria)

        val libros = mongoTemplate.find(query, Libro::class.java)
        val total = mongoTemplate.count(countQuery, Libro::class.java)

        return PageImpl(libros, pageable, total)
    }
}