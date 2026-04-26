package ar.edu.unsam.phm.repository


import ar.edu.unsam.phm.domain.RefreshToken
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RepoRefreshToken : CrudRepository<RefreshToken, Long> {
    fun findByTokenHash(tokenHash: String): Optional<RefreshToken>
    fun findByUsername(username: String): Optional<RefreshToken>
    fun deleteByUsername(username: String)
    fun findAllByUsername(username: String): List<RefreshToken>
}

