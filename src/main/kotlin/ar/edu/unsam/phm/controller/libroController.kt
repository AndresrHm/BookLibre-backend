package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.FiltrosDeLibrosDTO
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.DireccionOrden
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.LibroEdicionDTO
import ar.edu.unsam.phm.dto.LibroUsuarioPerfilDTO
import ar.edu.unsam.phm.dto.OrdenLibroPerfil
import ar.edu.unsam.phm.dto.OrdenHomePage
import ar.edu.unsam.phm.dto.LibroHomeUsuarioDTO
import ar.edu.unsam.phm.dto.FiltroDisponibilidadPerfil
import ar.edu.unsam.phm.dto.FiltrosLibrosQueryDTO
import ar.edu.unsam.phm.dto.toFiltrosDeLibrosDTO
import ar.edu.unsam.phm.mapper.LibroMapper
import ar.edu.unsam.phm.service.CalificacionService
import ar.edu.unsam.phm.service.LibroService
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("libros")
@CrossOrigin("*")
class LibroController(
    val libroService: LibroService,
    val libroMapper: LibroMapper,
    val calificacionService: CalificacionService,
    val usuarioService: UsuarioService,
) {
    @GetMapping("{usuarioId}")
    fun obtenerLibrosDeUsuario(
        @PathVariable usuarioId: Int,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 3, // 3 libros por página, igual que la tabla del front
        @RequestParam orden: OrdenLibroPerfil = OrdenLibroPerfil.FECHA_AGREGADO,
        @RequestParam direccion: DireccionOrden = DireccionOrden.DESCENDENTE,
        @RequestParam filtro: FiltroDisponibilidadPerfil = FiltroDisponibilidadPerfil.TODOS,
    ): PageResponse<LibroUsuarioPerfilDTO> {
        return libroService.obtenerLibrosDeUsuario(usuarioId, page, size, orden, direccion, filtro)
    }

    @PostMapping
    fun crearLibro(@RequestBody libro: LibroEdicionDTO): LibroEdicionDTO {
        val libroConvertido = libroMapper.libroEdicionDTOToLibro(libro)
        val nuevoLibro = libroService.crearLibro(libroConvertido)
        return this.libroMapper.libroToLibroEdicionDTO(nuevoLibro)
    }

    @GetMapping("{idLibro}/edicion")
    fun obtenerLibroParaEdicion(@PathVariable idLibro: Int): LibroEdicionDTO {
        val libroParaEdicion = libroService.obtenerLibroParaEdicion(idLibro)
        return this.libroMapper.libroToLibroEdicionDTO(libroParaEdicion)
    }

    @GetMapping("{idLibro}/detalle")
    fun obtenerLibroDetalle(@PathVariable idLibro: Int): LibroDetalleDTO {
        val libroDetalle = libroService.obtenerLibroDetalle(idLibro)
        val ultimasCalificaciones = calificacionService.obtenerUltimasCalificaciones(idLibro)
        return libroMapper.libroToLibroDetalleDTO(libroDetalle, ultimasCalificaciones)
    }

    @PutMapping
    fun editarLibro(
        @RequestBody libro: LibroEdicionDTO,
        @RequestParam idUsuario: Int,
    ): LibroEdicionDTO {
        val libroObjeto = libroMapper.libroEdicionDTOToLibro(libro)
        val libroEditado = libroService.editarLibro(libroObjeto, idUsuario)
        return libroMapper.libroToLibroEdicionDTO(libroEditado)
    }

    @GetMapping("/filter")
    fun getLibrosFilter(
        @ModelAttribute filtrosQuery: FiltrosLibrosQueryDTO,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 6,
        @RequestParam userId: Int,
        @RequestParam ordenHomePage: OrdenHomePage = OrdenHomePage.NOMBRE_ASC
    ): PageResponse<LibroHomeUsuarioDTO> {
        val filtros = filtrosQuery.toFiltrosDeLibrosDTO(
            usuarioPrestador = filtrosQuery.usuarioPrestadorId?.let { usuarioService.getUsuarioById(it) },
            usuarioLector = filtrosQuery.usuarioLectorId?.let { usuarioService.getUsuarioById(it) },
        )

        val pageResult = libroService.getLibrosPorFiltros(filtros, page, size, ordenHomePage, userId)

        return PageResponse(
            content = pageResult.content.map {
                libroMapper.libroToLibroHomeUsuarioDTO(
                    it,
                    libroService.bringKarma(it, userId, filtros.fechaDesde, filtros.fechaHasta)
                )
            },
            page = pageResult.page,
            size = pageResult.size,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages
        )
    }

    @DeleteMapping("{idLibro}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun borrarLibro(
        @PathVariable idLibro: Int,
        @RequestParam idUsuario: Int
    ) =
        libroService.borrarLibro(idLibro, idUsuario)
}
