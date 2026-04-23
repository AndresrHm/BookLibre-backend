package ar.edu.unsam.phm.predicate

import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import jakarta.persistence.criteria.Subquery
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

object LibroSpecifications {

    object LibroSpecifications {

        private fun <T> eq(field: String, value: T?): Specification<Libro>? =
            value?.let {
                Specification { root, _, cb -> cb.equal(root.get<T>(field), it) }
            }

        private fun likeIgnoreCase(field: String, value: String?): Specification<Libro>? =
            value
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?.lowercase()
                ?.let { pattern ->
                    Specification { root, _, cb ->
                        cb.like(cb.lower(root.get(field)), "%$pattern%")
                    }
                }

        private fun <T : Comparable<T>> gte(field: String, value: T?): Specification<Libro>? =
            value?.let {
                Specification { root, _, cb ->
                    cb.greaterThanOrEqualTo(root.get(field), it)
                }
            }

        private fun <T : Comparable<T>> lte(field: String, value: T?): Specification<Libro>? =
            value?.let {
                Specification { root, _, cb ->
                    cb.lessThanOrEqualTo(root.get(field), it)
                }
            }

        private fun <T> inside(field: String, values: List<T>?): Specification<Libro>? =
            values
                ?.takeIf { it.isNotEmpty() }
                ?.let {
                    Specification { root, _, _ ->
                        root.get<T>(field).`in`(it)
                    }
                }

        private fun andAll(specs: List<Specification<Libro>?>): Specification<Libro> =
            specs
                .filterNotNull()
                .reduce { acc, spec -> acc.and(spec) }

        fun activo(): Specification<Libro> =
            Specification { root, _, cb -> cb.isTrue(root.get("activo")) }

        fun dePropietario(id: Long): Specification<Libro> =
            Specification { root, _, cb -> cb.equal(root.get<Long>("idPropietario"), id) }

        fun excluirPropietario(id: Long): Specification<Libro> =
            Specification { root, _, cb -> cb.notEqual(root.get<Long>("idPropietario"), id) }

        fun overlapReserva(fechaDesde: LocalDate, fechaHasta: LocalDate): Specification<Libro> =
            Specification { root, query, cb ->

                val sub = query.subquery(Long::class.java)
                val reserva = sub.from(Reserva::class.java)

                sub.select(cb.literal(1L))
                sub.where(
                    cb.equal(reserva.get<Libro>("libro"), root),
                    cb.lessThanOrEqualTo(reserva.get("fechaRecogida"), fechaHasta),
                    cb.greaterThanOrEqualTo(reserva.get("fechaDevolucion"), fechaDesde),
                )

                cb.exists(sub)
            }

        fun disponibleEntreFechas(fechaDesde: LocalDate, fechaHasta: LocalDate): Specification<Libro> =
            Specification.not(overlapReserva(fechaDesde, fechaHasta))

        // =========================
        // Composición principal
        // =========================

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

            val propietarioSpec =
                usuarioPrestadorId?.let { dePropietario(it) }
                    ?: excluirPropietario(userId)

            val disponibilidadSpec =
                if (fechaDesde != null && fechaHasta != null)
                    disponibleEntreFechas(fechaDesde, fechaHasta)
                else null

            return andAll(
                listOf(
                    activo(),
                    likeIgnoreCase("titulo", search),
                    eq("isbn13", isbn13),
                    gte("cantidadPaginas", paginasMin),
                    lte("cantidadPaginas", paginasMax),
                    inside("genero", generos),
                    propietarioSpec,
                    disponibilidadSpec
                )
            )
        }
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