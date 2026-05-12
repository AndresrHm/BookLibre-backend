package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.errors.ErrorStrings
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

sealed class SealedGetLibroDTO(val tipo: String) {
    data class Edicion(
        val libro: LibroEdicionDTO
    ) : SealedGetLibroDTO("EDICION")

    data class Detalle(
        val libro: LibroDetalleDTO
    ) : SealedGetLibroDTO("DETALLE")
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
    @field:NotNull(message = ErrorStrings.LIBRO_ID_REQUERIDO)
    val id: String,
    @field:NotBlank(message = ErrorStrings.LIBRO_DEBE_TENER_IMAGEN)
    val imagenUrl: String,
    @field:NotBlank(message = ErrorStrings.LIBRO_DEBE_TENER_TITULO)
    val titulo: String,
    @field:NotNull(message = ErrorStrings.LIBRO_DEBE_TENER_TIPO)
    val tipoLibro: String,
    @field:NotNull(message = ErrorStrings.LIBRO_DEBE_TENER_GENERO)
    val genero: Genero,
    @field:NotBlank(message = ErrorStrings.LIBRO_DEBE_TENER_AUTOR)
    val autor: String,
    @field:NotNull(message = ErrorStrings.LIBRO_DEBE_TENER_IDIOMA)
    val idioma: Idioma,
    @field:NotNull(message = ErrorStrings.LIBRO_DEBE_TENER_ESTADO)
    val estado: EstadoLibro,
    @field:NotBlank(message = ErrorStrings.LIBRO_DEBE_TENER_EDITORIAL)
    val editorial: String,
    @field:NotNull(message = ErrorStrings.LIBRO_DEBE_TENER_FECHA_PUBLICACION)
    val fechaPublicacion: LocalDate,
    @field:Positive(message = ErrorStrings.LIBRO_CANTIDAD_PAGINAS_POSITIVA)
    @field:Max(value = 1499, message = ErrorStrings.LIBRO_NO_PUEDE_SUPERAR_1499_PAGINAS)
    val cantidadPaginas: Int,
    @field:Min(value = 1000000000000, message = ErrorStrings.ISBN13_DEBE_TENER_13_DIGITOS)
    @field:Max(value = 9999999999999, message = ErrorStrings.ISBN13_DEBE_TENER_13_DIGITOS)
    val isbn13: Long,
    @field:NotBlank(message = ErrorStrings.LIBRO_DEBE_TENER_DESCRIPCION)
    @field:Size(max = 500, message = ErrorStrings.DESCRIPCION_NO_PUEDE_SUPERAR_500)
    val descripcion: String,
    @field:NotNull(message = ErrorStrings.LIBRO_DEBE_TENER_ID_PROPIETARIO)
    val idPropietario: Long
)

data class LibroDetalleDTO(
    val id: String,
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
    val cantidadCalificaciones: Long,
    val calificacionesRecientes: List<CalificacionDTO>,
    val idPropietario: Long,
    val activo: Boolean,
    val usuarioEsPropietario: Boolean
)

data class LibroFiltradoDTO(
    val id: String,
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
    val biblioKarma: Int?,
    val userCreador: String,
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
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val fechaDesde: LocalDate? = null,
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val fechaHasta: LocalDate? = null,
    val isbn13: Long? = null,
    val usuarioPrestadorId: Long? = null,
    val search: String? = null,
    val usuarioLectorId: Long? = null,
)

data class OrdenamientosLibrosDTO(
    val ordenHomePage: OrdenHomePage? = OrdenHomePage.NOMBRE_ASC,
    val ordenPerfilPage: OrdenLibroPerfil? = OrdenLibroPerfil.FECHA_AGREGADO,
    val direccionPerfilPage: DireccionOrden? = DireccionOrden.DESCENDENTE,
    val disponibilidadLibrosPerfil: FiltroDisponibilidadPerfil? = FiltroDisponibilidadPerfil.TODOS,
)


data class LibroUsuarioPerfilDTO(
    val id: String,
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
    NOMBRE_ASC, NOMBRE_DESC, AUTOR_ASC, AUTOR_DESC, USER_ASC, USER_DESC
}

enum class GetLibroDTO {
    DETALLE,
    EDICION
}

enum class OrigenLibro {
    HOME,
    PERFIL
}
