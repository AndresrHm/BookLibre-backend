package ar.edu.unsam.phm.predicate


import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import jakarta.persistence.criteria.Subquery

object LibroSpecifications {

    /** Devuelve libros marcados como activos. */
    fun activo(): Specification<Libro> = Specification { root, _, cb ->
        cb.equal(root.get<Boolean>("activo"), true)
    }

    /** Filtra los libros de un propietario específico. */
    fun dePropietario(idPropietario: Long): Specification<Libro> = Specification { root, _, cb ->
        cb.equal(root.get<Long>("idPropietario"), idPropietario)
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