package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.FiltrosDeLibrosDTO
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.DireccionOrden
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.LibroEdicionDTO
import ar.edu.unsam.phm.dto.LibroUsuarioPerfilDTO
import ar.edu.unsam.phm.dto.OrdenLibroPerfil
import ar.edu.unsam.phm.dto.LibroHomeUsuarioDTO
import ar.edu.unsam.phm.mapper.LibroMapper
import ar.edu.unsam.phm.service.CalificacionService
import ar.edu.unsam.phm.service.LibroService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class LibroController(val libroService: LibroService, val libroMapper: LibroMapper, val calificacionService: CalificacionService) {
    @GetMapping("/usuarios/{usuarioId}/libros")
    fun obtenerLibrosDeUsuario(
        @PathVariable usuarioId: Int,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 3, // 3 libros por página, igual que la tabla del front
        @RequestParam orden: OrdenLibroPerfil = OrdenLibroPerfil.FECHA_AGREGADO,
        @RequestParam direccion: DireccionOrden = DireccionOrden.DESCENDENTE,
    ): PageResponse<LibroUsuarioPerfilDTO> {
        // React: guardar content/page/size/totalElements/totalPages en estado.
        // Luego reenviar page/size/orden/direccion cuando cambie tabla o paginado.
        return libroService.obtenerLibrosDeUsuario(usuarioId, page, size, orden, direccion)
    }
    @PostMapping("libro/crear")
    fun crearLibro(@RequestBody libro: LibroEdicionDTO): LibroEdicionDTO {
        val libroConvertido = libroMapper.libroEdicionDTOToLibro(libro)
        val nuevoLibro = libroService.crearLibro(libroConvertido)
        return this.libroMapper.libroToLibroEdicionDTO(nuevoLibro)
    }

    @GetMapping("libro/editar/{idLibro}/{idUsuario}") //Debería poder hacerse sin idLibro
    fun obtenerLibroParaEdicion(@PathVariable idLibro: Int, @PathVariable idUsuario: Int): LibroEdicionDTO {
        val libroParaEdicion = libroService.obtenerLibroParaEdicion(idLibro, idUsuario)
        return this.libroMapper.libroToLibroEdicionDTO(libroParaEdicion)
    }


    @GetMapping("libro/detalle/{idLibro}")
    fun obtenerLibroDetalle(@PathVariable idLibro: Int): LibroDetalleDTO {
        val libroDetalle = libroService.obtenerLibroDetalle(idLibro)
        val ultimasCalificaciones = calificacionService.obtenerUltimasCalificaciones(idLibro)
        return libroMapper.libroToLibroDetalleDTO(libroDetalle, ultimasCalificaciones)
    }

    @PutMapping("libro/editar/{idLibro}/{idUsuario}")//Debería poder hacerse sin idLibro
    fun editarLibro(
        @RequestBody libro: LibroEdicionDTO,
        @PathVariable idLibro: Int,
        @PathVariable idUsuario: Int
    ): LibroEdicionDTO {
        val libroConvertido = libroMapper.libroEdicionDTOToLibro(libro)
        val libroParaEdicion = libroService.editarLibro(libroConvertido, idLibro, idUsuario)
        return this.libroMapper.libroToLibroEdicionDTO(libroParaEdicion)
    }

    @PostMapping("/homePage/user/{idLibro}/filter")
    fun getLibrosFilter(@RequestBody filtros: FiltrosDeLibrosDTO, @PathVariable idLibro: Int): List<LibroHomeUsuarioDTO> {
        // TODO: Utilizar mappers para pasa de DTO's a objetos de dominio y viceversa
        // Obtener filtros
        // val filtros = filtrosMapper.DTOsAObjeto(filtrosDtos)

        val librosFiltrados = libroService.getLibrosPorFiltros(filtros)
        return librosFiltrados.map { libroMapper.libroToLibroHomeUsuarioDTO(it) }
    }
}