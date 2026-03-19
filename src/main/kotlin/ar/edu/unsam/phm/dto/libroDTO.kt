package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Usuario
import java.time.LocalDate

data class LibroEdicionDTO(
    val id: Int,
    val imagenUrl: String,
    val titulo: String,
    val tipoLibro: String,
    val genero: Genero,
    val autor: String,
    val idioma: Idioma,
    val estado: EstadoLibro,
    val editorial: String,
    val fechaPublicacion: LocalDate,
    val cantidadPaginas: Int,
    val isbn13: Long,
    val descripcion: String,
    val idPropietario: Int
)

data class LibroDetalleDTO(
    val id: Int,
    val imagenUrl: String,
    val titulo: String,
    val tipoLibro: String,
    val genero: Genero,
    val autor: String,
    val idioma: Idioma,
    val estado: EstadoLibro,
    val editorial: String,
    val fechaPublicacion: LocalDate,
    val cantidadPaginas: Int,
    val isbn13: Long,
    val descripcion: String,
    val rating: Double,
    val cantidadResenias: Int,
    val reseniasRecientes: List<CalificacionDTO>,
    val idPropietario: Int
)

data class LibroHomeUsuarioDTO(
    val id: Int,
    val imagenUrl: String,
    val titulo: String,
    val tipoLibro: String,
    val genero: Genero,
    val autor: String,
    val idioma: Idioma,
    val estado: EstadoLibro,
    val editorial: String,
    val fechaPublicacion: LocalDate,
    val cantidadPaginas: Int,
    val isbn13: Long,
    val descripcion: String,
    val rating: Double,
    val biblioKarma : Int
)

data class FiltrosDeLibrosDTO(
    val genero: Genero?,
    val paginasMin: Int?,
    val paginasMax: Int?,
    val fechaDesde: LocalDate?,
    val fechaHasta: LocalDate?,
    val isbn13: Long?,
    val usuarioPrestador: Usuario?,
    val search: String?,
    val usuarioLector: Usuario?
)

data class LibroUsuarioPerfilDTO(
    val id: Int,
    val titulo: String,
    val autor: String,
    val portada: String,
    val genero: Genero,
    val fechaAgregado: String,
    val disponibilidad: String
)

enum class OrdenLibroPerfil {
    TITULO,
    DISPONIBLE_PRESTADO,
    FECHA_AGREGADO
}

enum class DireccionOrden {
    ASCENDENTE,
    DESCENDENTE
}

enum class FiltroDisponibilidadPerfil {
    TODOS,
    DISPONIBLES,
    PRESTADOS
}
