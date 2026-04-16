package ar.edu.unsam.phm.predicate

import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import jakarta.persistence.criteria.Subquery
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

object LibroSpecifications {

    /** Devuelve libros marcados como activos. */
    fun activo(): Specification<Libro> = Specification { root, _, cb ->
        cb.equal(root.get<Boolean>("activo"), true)
    }

    /** Filtra los libros de un propietario específico. */
    fun dePropietario(idPropietario: Long): Specification<Libro> = Specification { root, _, cb ->
        cb.equal(root.get<Long>("idPropietario"), idPropietario)
    }

    fun tituloContiene(searchPattern: String?): Specification<Libro>? =
        searchPattern
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?.lowercase()
            ?.let { pattern ->
                Specification { root, _, cb ->
                    cb.like(cb.lower(root.get("titulo")), "%$pattern%")
                }
            }

    fun isbnIgual(isbn13: Long?): Specification<Libro>? = isbn13?.let { isbn ->
        Specification { root, _, cb -> cb.equal(root.get<Long>("isbn13"), isbn) }
    }

    fun paginasMinimas(paginasMin: Int?): Specification<Libro>? = paginasMin?.let { min ->
        Specification { root, _, cb -> cb.greaterThanOrEqualTo(root.get("cantidadPaginas"), min) }
    }

    fun paginasMaximas(paginasMax: Int?): Specification<Libro>? = paginasMax?.let { max ->
        Specification { root, _, cb -> cb.lessThanOrEqualTo(root.get("cantidadPaginas"), max) }
    }

    fun generoEn(generos: List<Genero>?): Specification<Libro>? =
        generos
            ?.takeIf { it.isNotEmpty() }
            ?.let { generosFiltrados ->
                Specification { root, _, _ -> root.get<Genero>("genero").`in`(generosFiltrados) }
            }

    fun excluirPropietario(usuarioId: Long): Specification<Libro> = Specification { root, _, cb ->
        cb.notEqual(root.get<Long>("idPropietario"), usuarioId)
    }

    fun overlapReserva(fechaDesde: LocalDate, fechaHasta: LocalDate): Specification<Libro> =
        Specification { root, query, cb ->
            val subquery: Subquery<Long> = query.subquery(Long::class.java)
            val reservaRoot = subquery.from(Reserva::class.java)

            subquery.select(cb.literal(1L))
            subquery.where(
                cb.equal(reservaRoot.get<Libro>("libro"), root),
                cb.lessThanOrEqualTo(reservaRoot.get<LocalDate>("fechaRecogida"), fechaHasta),
                cb.greaterThanOrEqualTo(reservaRoot.get<LocalDate>("fechaDevolucion"), fechaDesde),
            )
            cb.exists(subquery)
        }

    fun disponibleEntreFechas(fechaDesde: LocalDate, fechaHasta: LocalDate): Specification<Libro> =
        Specification.not(overlapReserva(fechaDesde, fechaHasta))

    fun filtrosHome(
        search: String?,
        isbn13: Long?,
        paginasMin: Int?,
        paginasMax: Int?,
        generos: List<Genero>?,
        usuarioPrestadorId: Long?,
        userId: Long,
        fechaDesde: LocalDate?,
        fechaHasta: LocalDate?,
    ): Specification<Libro> {
        val specPropietario = usuarioPrestadorId
            ?.let { dePropietario(it) }
            ?: excluirPropietario(userId)

        val specDisponibilidad = if (fechaDesde != null && fechaHasta != null) {
            disponibleEntreFechas(fechaDesde, fechaHasta)
        } else {
            null
        }

        val specs = listOfNotNull(
            activo(),
            tituloContiene(search),
            isbnIgual(isbn13),
            paginasMinimas(paginasMin),
            paginasMaximas(paginasMax),
            generoEn(generos),
            specPropietario,
            specDisponibilidad,
        )

        return specs.drop(1).fold(specs.first()) { acc, spec -> acc.and(spec) }
    }

    /** Detecta los libros que están reservados en la fecha actual. */
    fun reservadoHoy(): Specification<Libro> = Specification { root, query, cb ->
        val subquery: Subquery<Long> = query.subquery(Long::class.java)
        val reservaRoot = subquery.from(Reserva::class.java)
        val hoy = LocalDate.now()

        subquery.select(cb.literal(1L))
        subquery.where(
            cb.equal(reservaRoot.get<Libro>("libro"), root),
            cb.lessThanOrEqualTo(reservaRoot.get<LocalDate>("fechaRecogida"), hoy),
            cb.greaterThanOrEqualTo(reservaRoot.get<LocalDate>("fechaDevolucion"), hoy)
        )
        cb.exists(subquery)
    }

    /** Devuelve los libros disponibles hoy, es decir, los que no están reservados. */
    fun disponibleHoy(): Specification<Libro> = Specification.not(reservadoHoy())
}