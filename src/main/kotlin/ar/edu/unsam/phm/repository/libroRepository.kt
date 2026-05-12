package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Libro
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import ar.edu.unsam.phm.repository.CriterioFiltrosLibrosBuilder


interface LibroRepository : MongoRepository<Libro, String>, LibroRepositoryCustom {
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
        excluirIds: Set<String>,
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
        excluirIds: Set<String>,
        pageable: Pageable,
    ): Page<Libro> {
        val criteria = CriterioFiltrosLibrosBuilder.build(
            LibroFiltrosMongo(
                search = search,
                isbn13 = isbn13,
                paginasMin = paginasMin,
                paginasMax = paginasMax,
                generos = generos,
                soloIdPropietario = soloIdPropietario,
                excluirIdPropietario = excluirIdPropietario,
                excluirIds = excluirIds,
            )
        )
        val query = Query(criteria).with(pageable)
        val countQuery = Query(criteria)

        val libros = mongoTemplate.find(query, Libro::class.java)
        val total = mongoTemplate.count(countQuery, Libro::class.java)

        return PageImpl(libros, pageable, total)
    }
}