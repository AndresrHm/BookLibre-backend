package ar.edu.unsam.phm.predicate

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification


fun reservaPorTipoSpecifications(
    tipoReserva: String,
    userId: Long,
    search: String?
): Specification<Reserva> = Specification<Reserva> { root, query, cb ->
    val condiciones = mutableListOf<Predicate>()

    // Predicate para el filtro de Usuario (según el tipo)
    if (tipoReserva == "TO_ME") {
        // WHERE usuarioLector.id = :userId
        condiciones.add(cb.equal(root.get<Usuario>("usuarioLector").get<Long>("id"), userId))
    } else {
        // WHERE usuarioPrestador.id = :userId
        condiciones.add(cb.equal(root.get<Usuario>("usuarioPrestador").get<Long>("id"), userId))
    }

    // Predicate para el buscador por palabra
    search?.takeIf { it.isNotBlank() }?.let { texto ->
        val textoABuscar = "%${texto.lowercase()}%"

        // Creamos las condiciones de título || autor
        val tituloLike = cb.like(cb.lower(root.get<Libro>("libro").get("titulo")), textoABuscar)
        val autorLike = cb.like(cb.lower(root.get<Libro>("libro").get("autor")), textoABuscar)

        // Las unimos con un OR: (titulo LIKE ... OR autor LIKE ...)
        condiciones.add(cb.or(tituloLike, autorLike))
    }

    // Unimos todas las condiciones con un AND final
    cb.and(*condiciones.toTypedArray())
}