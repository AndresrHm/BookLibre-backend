package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.TipoLibro
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.LibroUsuarioPerfilDTO
import ar.edu.unsam.phm.dto.FiltrosDeLibros
import ar.edu.unsam.phm.dto.FiltrosLibrosQueryDTO
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.LibroEdicionDTO
import ar.edu.unsam.phm.dto.LibroFiltradoDTO
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.stereotype.Component

@Component
class LibroMapper(
    private val libroRepository: LibroRepository,
    private val usuarioService: UsuarioService
) {
    fun fromEdicionDTO(libroEdicionDTO: LibroEdicionDTO): Libro {
        val libro = Libro(
            titulo = libroEdicionDTO.titulo,
            tipoLibro = if (libroEdicionDTO.tipoLibro == "conDedicatoria") TipoLibro.CON_DEDICATORIA else if (libroEdicionDTO.tipoLibro == "coleccionable") TipoLibro.COLECCIONABLE else TipoLibro.COMUN,
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

        libro.init()

        return libro
    }

    fun toPerfilUsuarioDTO(libro: Libro, disponibilidad: String): LibroUsuarioPerfilDTO =
        LibroUsuarioPerfilDTO(
            id = libro.id,
            titulo = libro.titulo,
            portada = libro.portada,
            genero = libro.genero,
            autor = libro.autor,
            fechaAgregado = libro.getFechaAsString(),
            disponibilidad = disponibilidad
        )

    fun toEdicionDTO(libro: Libro): LibroEdicionDTO {
        val libroEdicionDTO = LibroEdicionDTO(
            id = libro.id!!,
            titulo = libro.titulo,
            tipoLibro = libro.tipoComoTexto(),
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

    fun toDetalleDTO(
        libro: Libro,
        rating: Double,
        cantidadCalificaciones: Long,
        libroCalificacionesRecientes: List<Calificacion>
    ): LibroDetalleDTO {
        val libroDetalle = LibroDetalleDTO(
            id = libro.id,
            titulo = libro.titulo,
            tipoLibro = libro.tipoComoTexto(),
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
            rating = rating,
            cantidadCalificaciones = cantidadCalificaciones,
            calificacionesRecientes = CalificacionMapper.toDTO(libroCalificacionesRecientes),
            idPropietario = libro.idPropietario,
            activo = libro.activo
        )
        return libroDetalle
    }

    fun toFiltradoDTO(
        libro: Libro,
        rating: Double,
        karmaForUserLector: Int,
        disponibilidad: String
    ): LibroFiltradoDTO {
        val libroDetalle = LibroFiltradoDTO(
            id = libro.id,
            titulo = libro.titulo,
            tipoLibro = libro.tipoComoTexto(),
            descripcion = libro.descripcion,
            imagenUrl = libro.portada,
            genero = libro.genero,
            autor = libro.autor,
            cantidadPaginas = libro.cantidadPaginas,
            isbn13 = libro.isbn13,
            idioma = libro.idioma,
            editorial = libro.editorial,
            fechaAgregado = libro.fechaPublicacion,
            estado = libro.estadoLibro,
            rating = rating,
            biblioKarma = karmaForUserLector,
//            TODO: Testing
            //userCreador = libroRepository.getNickUsuarioCreador(libro.id),
            userCreador = usuarioService.getNombreDeUsuario(libro.idPropietario) ?: "NERR",
            disponibilidad = disponibilidad
        )
        return libroDetalle
    }

    fun filtroDTOtoFiltro(
        filtrosQuery: FiltrosLibrosQueryDTO,
        usuarioPrestador: Usuario?,
        usuarioLector: Usuario?
    ): FiltrosDeLibros {
        val filtros = FiltrosDeLibros(
            generos = filtrosQuery.generos,
            fechaDesde = filtrosQuery.fechaDesde,
            fechaHasta = filtrosQuery.fechaHasta,
            paginasMin = filtrosQuery.paginasMin,
            paginasMax = filtrosQuery.paginasMax,
            usuarioPrestador = usuarioPrestador,
            usuarioLector = usuarioLector,
            isbn13 = filtrosQuery.isbn13,
            search = filtrosQuery.search
        )
        return filtros
    }
}