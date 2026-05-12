package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Rol
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RolRepository : CrudRepository<Rol, Long> {
	fun findByName(name: String): Rol?
}
