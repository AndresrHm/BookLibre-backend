package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Libro
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LibroRepository : CrudRepository<Libro, Long>, JpaSpecificationExecutor<Libro> {

    @Modifying
    @Query("""
            UPDATE Libro l
            SET l.activo = false
            where l.idPropietario = :idPropietario""")
    fun eliminarPorIDPropietario(idPropietario: Long)

    fun findByActivoTrue(): List<Libro>
}