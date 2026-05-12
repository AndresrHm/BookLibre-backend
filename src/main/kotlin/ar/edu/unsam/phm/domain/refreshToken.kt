package ar.edu.unsam.phm.domain


import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_tokens")
class RefreshToken(
    @Column(unique = true, nullable = false)
    var tokenHash: String,

    @Column(nullable = false)
    var username: String,

    @Column(nullable = false)
    var expirationDate: LocalDateTime
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    var revoked: Boolean = false

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    fun isValid(): Boolean = !revoked && LocalDateTime.now().isBefore(expirationDate)
}
