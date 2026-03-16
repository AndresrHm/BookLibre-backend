package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.dto.LibroUsuarioPerfilDTO
import ar.edu.unsam.phm.domain.coleccionable
import ar.edu.unsam.phm.domain.comun
import ar.edu.unsam.phm.domain.conDedicatoria
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.LibroEdicionDTO
import ar.edu.unsam.phm.dto.LibroHomeUsuarioDTO
import ar.edu.unsam.phm.repository.LibroRepository
import org.springframework.stereotype.Component

@Component
class LibroMapper(
    private val libroRepository: LibroRepository
) {
    fun libroEdicionDTOToLibro(libroEdicionDTO: LibroEdicionDTO): Libro {
        val libro = Libro(
            titulo = libroEdicionDTO.titulo,
            tipoLibro = if (libroEdicionDTO.tipoLibro == "conDedicatoria") conDedicatoria else if (libroEdicionDTO.tipoLibro == "coleccionable") coleccionable else comun,
            descripcion = libroEdicionDTO.descripcion,
            portada = libroEdicionDTO.imagenUrl,
            genero = libroEdicionDTO.genero,
            autor = libroEdicionDTO.autor,
            cantidadPaginas = libroEdicionDTO.cantidadPaginas,
            isbn13 = libroEdicionDTO.isbn13,
            idioma = libroEdicionDTO.idioma,
            editorial = libroEdicionDTO.editorial,
            fechaPublicacion = libroEdicionDTO.fechaPublicacion,
            estadoLibro = libroEdicionDTO.estado,
            idPropietario = libroEdicionDTO.idPropietario
        )

        libro.id = libroEdicionDTO.id

        return libro
    }

    fun libroToLibroPerfilUsuarioDTO(libro: Libro): LibroUsuarioPerfilDTO = LibroUsuarioPerfilDTO(
        id = libro.id,
        titulo = libro.titulo,
        portada = libro.portada,
        genero = libro.genero,
        autor = libro.autor,
        fechaAgregado = libro.getFechaAsString(),
        disponibilidad = libroRepository.verDisponibilidadDelLibro(libro.id)
    )
    fun libroToLibroEdicionDTO(libro: Libro): LibroEdicionDTO {
        val libroEdicionDTO = LibroEdicionDTO(
            id = libro.id,
            titulo = libro.titulo,
            tipoLibro = if (libro.tipoLibro == conDedicatoria) "conDedicatoria" else if (libro.tipoLibro == coleccionable) "coleccionable" else "comun",
            descripcion = libro.descripcion,
            imagenUrl = libro.portada,
            genero = libro.genero,
            autor = libro.autor,
            cantidadPaginas = libro.cantidadPaginas,
            isbn13 = libro.isbn13,
            idioma = libro.idioma,
            editorial = libro.editorial,
            fechaPublicacion = libro.fechaPublicacion,
            estado = libro.estadoLibro,
            idPropietario = libro.idPropietario
        )
        return libroEdicionDTO
    }

    fun libroToLibroDetalleDTO(libro: Libro, libroReseniasRecientes: List<Calificacion>): LibroDetalleDTO {
        val libroDetalle = LibroDetalleDTO(
            id = libro.id,
            titulo = libro.titulo,
            tipoLibro = if (libro.tipoLibro == conDedicatoria) "conDedicatoria" else if (libro.tipoLibro == coleccionable) "coleccionable" else "comun",
            descripcion = libro.descripcion,
            imagenUrl = libro.portada,
            genero = libro.genero,
            autor = libro.autor,
            cantidadPaginas = libro.cantidadPaginas,
            isbn13 = libro.isbn13,
            idioma = libro.idioma,
            editorial = libro.editorial,
            fechaPublicacion = libro.fechaPublicacion,
            estado = libro.estadoLibro,
            rating = libro.rating(),
            cantidadResenias = libro.cantidadResenias,
            reseniasRecientes = libroReseniasRecientes
        )
        return libroDetalle
    }

    fun libroToLibroHomeUsuarioDTO(libro: Libro): LibroHomeUsuarioDTO {
        val libroDetalle = LibroHomeUsuarioDTO(
            id = libro.id,
            titulo = libro.titulo,
            tipoLibro = if (libro.tipoLibro == conDedicatoria) "conDedicatoria" else if (libro.tipoLibro == coleccionable) "coleccionable" else "comun",
            descripcion = libro.descripcion,
            imagenUrl = libro.portada,
            genero = libro.genero,
            autor = libro.autor,
            cantidadPaginas = libro.cantidadPaginas,
            isbn13 = libro.isbn13,
            idioma = libro.idioma,
            editorial = libro.editorial,
            fechaPublicacion = libro.fechaPublicacion,
            estado = libro.estadoLibro,
            rating = libro.rating()
        )
        return libroDetalle
    }
}