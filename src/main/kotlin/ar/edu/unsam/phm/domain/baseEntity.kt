import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import jakarta.persistence.EntityListeners
import java.time.LocalDateTime

@MappedSuperclass // Indica que es una clase para heredar, no es una tabla propia
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    //Se actualiza automáticamente al actualizar un objeto
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
}