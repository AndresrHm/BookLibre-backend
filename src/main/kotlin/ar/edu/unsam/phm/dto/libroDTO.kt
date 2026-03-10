package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Resenia
import ar.edu.unsam.phm.domain.TipoLibro
import java.time.LocalDate

data class LibroEdicionDTO(
    val id: Int,
    val imagenUrl: String,
    val titulo: String,
    val tipoLibro: TipoLibro,
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

fun LibroEdicionDTO.toLibro(): Libro {
    val libro = Libro(
        titulo = this.titulo,
        tipoLibro = this.tipoLibro,
        descripcion = this.descripcion,
        portada = this.imagenUrl,
        genero = this.genero,
        autor = this.autor,
        cantidadPaginas = this.cantidadPaginas,
        isbn13 = this.isbn13,
        idioma = this.idioma,
        editorial = this.editorial,
        fechaPublicacion = this.fechaPublicacion,
        estadoLibro = this.estado,
        idPropietario = this.idPropietario
    )

    libro.id = this.id

    return libro
}

fun Libro.toLibroEdicionDTO(): LibroEdicionDTO {
    val libro = LibroEdicionDTO(
        id = this.id,
        titulo = this.titulo,
        tipoLibro = this.tipoLibro,
        descripcion = this.descripcion,
        imagenUrl = this.portada,
        genero = this.genero,
        autor = this.autor,
        cantidadPaginas = this.cantidadPaginas,
        isbn13 = this.isbn13,
        idioma = this.idioma,
        editorial = this.editorial,
        fechaPublicacion = this.fechaPublicacion,
        estado = this.estadoLibro,
        idPropietario = this.idPropietario
    )
    return libro
}

data class LibroDetalleDTO (
    val id: Int,
    val imagenUrl: String,
    val titulo: String,
    val tipoLibro: TipoLibro,
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
    val reseniasRecientes: List<Resenia> //NIY
)


fun Libro.toLibroDetalleDTO(): LibroDetalleDTO {
    val libro = LibroDetalleDTO(
        id = this.id,
        titulo = this.titulo,
        tipoLibro = this.tipoLibro,
        descripcion = this.descripcion,
        imagenUrl = this.portada,
        genero = this.genero,
        autor = this.autor,
        cantidadPaginas = this.cantidadPaginas,
        isbn13 = this.isbn13,
        idioma = this.idioma,
        editorial = this.editorial,
        fechaPublicacion = this.fechaPublicacion,
        estado = this.estadoLibro,
        rating = this.rating(),
        cantidadResenias = this.cantidadResenias(),
        reseniasRecientes = this.ultimasResenias()
    )
    return libro
}

/*
data class LibroDTO(
    val id: Long,
    val titulo: String,
    val autor: String,
    val imagenUrl: String,
    val ratingPromedio: Double
)

data class LibroPrestadoDTO(
    val reservaId: Long,
    val titulo: String,
    val autor: String,
    val imagenUrl: String,
    val ratingLibro: Double,
    val prestadoPor: String,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val estado: EstadoReserva,
    val puedeCalificar: Boolean
)

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)

*/