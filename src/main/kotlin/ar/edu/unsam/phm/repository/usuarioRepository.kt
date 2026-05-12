package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Usuario
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UsuarioRepository : CrudRepository<Usuario, Long> {
    fun findByNombre(nombre: String): MutableList<Usuario>

    fun findByEmail(email: String): Usuario?

    fun findFirstByNombre(nombre: String): Usuario?
}
