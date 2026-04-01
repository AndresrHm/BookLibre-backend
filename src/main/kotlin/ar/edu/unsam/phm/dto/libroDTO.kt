package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Usuario
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

sealed class SealedGetLibroDTO(val tipo: String) {
    data class Edicion(
        val libro: LibroEdicionDTO
    ) : SealedGetLibroDTO("EDICION")
    data class Detalle(
        val libro: LibroDetalleDTO
    ): SealedGetLibroDTO("DETALLE")
}

sealed class SealedGetLibroFiltradoDTO(val tipo: String) {
    data class Home(
        val libro: LibroFiltradoDTO
    ) : SealedGetLibroFiltradoDTO("HOME")
    data class Perfil(
        val libro: LibroUsuarioPerfilDTO
    ) : SealedGetLibroFiltradoDTO("PERFIL")
}

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
    val cantidadCalificaciones: Int,
    val calificacionesRecientes: List<CalificacionDTO>,
    val idPropietario: Int,
    val activo: Boolean,
)

data class LibroFiltradoDTO(
    val id: Int,
    val imagenUrl: String,
    val titulo: String,
    val tipoLibro: String?,
    val genero: Genero?,
    val autor: String,
    val idioma: Idioma?,
    val estado: EstadoLibro?,
    val editorial: String?,
    val fechaAgregado: LocalDate?,
    val cantidadPaginas: Int?,
    val isbn13: Long?,
    val descripcion: String?,
    val rating: Double?,
    val biblioKarma : Int?,
    val userCreador : String,
    val disponibilidad: String
)

data class FiltrosDeLibros(
    val generos: List<Genero>?,
    val paginasMin: Int?,
    val paginasMax: Int?,
    val fechaDesde: LocalDate?,
    val fechaHasta: LocalDate?,
    val isbn13: Long?,
    val usuarioPrestador: Usuario?,
    val search: String?,
    val usuarioLector: Usuario?
)

// DTO de transporte para GET /homePage/user/filter (query params)
data class FiltrosLibrosQueryDTO(
    val generos: List<Genero>? = null,
    val paginasMin: Int? = null,
    val paginasMax: Int? = null,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val fechaDesde: LocalDate? = null,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val fechaHasta: LocalDate? = null,
    val isbn13: Long? = null,
    val usuarioPrestadorId: Int? = null,
    val search: String? = null,
    val usuarioLectorId: Int? = null,
)


fun FiltrosLibrosQueryDTO.DtoToLibroFiltro(
    usuarioPrestador: Usuario?,
    usuarioLector: Usuario?,
): FiltrosDeLibros {
    return FiltrosDeLibros(
        generos = generos,
        paginasMin = paginasMin,
        paginasMax = paginasMax,
        fechaDesde = fechaDesde,
        fechaHasta = fechaHasta,
        isbn13 = isbn13,
        usuarioPrestador = usuarioPrestador,
        search = search,
        usuarioLector = usuarioLector,
    )
}

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

enum class OrdenHomePage {
    NOMBRE_ASC
    , NOMBRE_DESC
    , AUTOR_ASC
    , AUTOR_DESC
    , USER_ASC
    , USER_DESC
}

enum class GetLibroDTO {
    DETALLE,
    EDICION
}

enum class OrigenLibro {
    HOME,
    PERFIL
}
