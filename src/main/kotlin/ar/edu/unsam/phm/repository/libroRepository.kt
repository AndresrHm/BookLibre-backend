package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Libro
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import ar.edu.unsam.phm.utils.CriterioFiltrosLibrosBuilder
import ar.edu.unsam.phm.utils.LibroFiltrosMongo
import ar.edu.unsam.phm.dto.FiltroDisponibilidadPerfil


interface LibroRepository : MongoRepository<Libro, String>, LibroRepositoryCustom {
    fun deleteByIdPropietario(idPropietario: Long)
    fun findByActivoTrue(): List<Libro>
    fun findByActivoTrueAndIdPropietario(idPropietario: Long): List<Libro>
    fun findByIdPropietario(idPropietario: Long): List<Libro>
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
        disponibilidad: FiltroDisponibilidadPerfil = FiltroDisponibilidadPerfil.TODOS,
    ): Page<Libro>

    fun buscarConFiltrosSinPaginacion(
        search: String?,
        isbn13: Long?,
        paginasMin: Int?,
        paginasMax: Int?,
        generos: List<Genero>?,
        soloIdPropietario: Long?,
        excluirIdPropietario: Long?,
        excluirIds: Set<String>,
        sort: Sort,
        skip: Long,
        limit: Int,
        disponibilidad: FiltroDisponibilidadPerfil = FiltroDisponibilidadPerfil.TODOS,
    ): List<Libro>

    fun contarConFiltros(
        search: String?,
        isbn13: Long?,
        paginasMin: Int?,
        paginasMax: Int?,
        generos: List<Genero>?,
        soloIdPropietario: Long?,
        excluirIdPropietario: Long?,
        excluirIds: Set<String>,
        disponibilidad: FiltroDisponibilidadPerfil = FiltroDisponibilidadPerfil.TODOS,
    ): Long
}

@Repository
class LibroRepositoryImpl(private val mongoTemplate: MongoTemplate) : LibroRepositoryCustom {
    private fun criterioFiltros(
        search: String?,
        isbn13: Long?,
        paginasMin: Int?,
        paginasMax: Int?,
        generos: List<Genero>?,
        soloIdPropietario: Long?,
        excluirIdPropietario: Long?,
        excluirIds: Set<String>,
        disponibilidad: FiltroDisponibilidadPerfil,
    ) = CriterioFiltrosLibrosBuilder.build(
        LibroFiltrosMongo(
            search = search,
            isbn13 = isbn13,
            paginasMin = paginasMin,
            paginasMax = paginasMax,
            generos = generos,
            soloIdPropietario = soloIdPropietario,
            excluirIdPropietario = excluirIdPropietario,
            excluirIds = excluirIds,
            disponibilidad = disponibilidad,
        )
    )

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
        disponibilidad: FiltroDisponibilidadPerfil,
    ): Page<Libro> {
        val criteria = criterioFiltros(
            search = search,
            isbn13 = isbn13,
            paginasMin = paginasMin,
            paginasMax = paginasMax,
            generos = generos,
            soloIdPropietario = soloIdPropietario,
            excluirIdPropietario = excluirIdPropietario,
            excluirIds = excluirIds,
            disponibilidad = disponibilidad,
        )
        val query = Query(criteria).with(pageable)
        val countQuery = Query(criteria)

        val libros = mongoTemplate.find(query, Libro::class.java)
        val total = mongoTemplate.count(countQuery, Libro::class.java)

        return PageImpl(libros, pageable, total)
    }

    override fun buscarConFiltrosSinPaginacion(
        search: String?,
        isbn13: Long?,
        paginasMin: Int?,
        paginasMax: Int?,
        generos: List<Genero>?,
        soloIdPropietario: Long?,
        excluirIdPropietario: Long?,
        excluirIds: Set<String>,
        sort: Sort,
        skip: Long,
        limit: Int,
        disponibilidad: FiltroDisponibilidadPerfil,
    ): List<Libro> {
        val criteria = criterioFiltros(
            search = search,
            isbn13 = isbn13,
            paginasMin = paginasMin,
            paginasMax = paginasMax,
            generos = generos,
            soloIdPropietario = soloIdPropietario,
            excluirIdPropietario = excluirIdPropietario,
            excluirIds = excluirIds,
            disponibilidad = disponibilidad,
        )
        val query = Query(criteria)
            .with(sort)
            .skip(skip)
            .limit(limit)

        return mongoTemplate.find(query, Libro::class.java)
    }

    override fun contarConFiltros(
        search: String?,
        isbn13: Long?,
        paginasMin: Int?,
        paginasMax: Int?,
        generos: List<Genero>?,
        soloIdPropietario: Long?,
        excluirIdPropietario: Long?,
        excluirIds: Set<String>,
        disponibilidad: FiltroDisponibilidadPerfil,
    ): Long {
        val criteria = criterioFiltros(
            search = search,
            isbn13 = isbn13,
            paginasMin = paginasMin,
            paginasMax = paginasMax,
            generos = generos,
            soloIdPropietario = soloIdPropietario,
            excluirIdPropietario = excluirIdPropietario,
            excluirIds = excluirIds,
            disponibilidad = disponibilidad,
        )
        return mongoTemplate.count(Query(criteria), Libro::class.java)
    }
}