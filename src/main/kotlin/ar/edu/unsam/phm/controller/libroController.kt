package ar.edu.unsam.phm.controller

import ActionResponse
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.DireccionOrden
import ar.edu.unsam.phm.dto.LibroEdicionDTO
import ar.edu.unsam.phm.dto.OrdenLibroPerfil
import ar.edu.unsam.phm.dto.OrdenHomePage
import ar.edu.unsam.phm.dto.LibroFiltradoDTO
import ar.edu.unsam.phm.dto.FiltroDisponibilidadPerfil
import ar.edu.unsam.phm.dto.FiltrosLibrosQueryDTO
import ar.edu.unsam.phm.dto.GetLibroDTO
import ar.edu.unsam.phm.dto.OrigenLibro
import ar.edu.unsam.phm.dto.SealedGetLibroDTO
import ar.edu.unsam.phm.mapper.LibroMapper
import ar.edu.unsam.phm.service.CalificacionService
import ar.edu.unsam.phm.service.LibroService
import ar.edu.unsam.phm.service.ReservaService
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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDate

@RestController
@RequestMapping("libros")
@CrossOrigin("*")
class LibroController(
    val libroService: LibroService,
    val reservaService: ReservaService,
    val usuarioService: UsuarioService,
    val calificacionService: CalificacionService,
    val libroMapper: LibroMapper,
) {

    @PostMapping
    fun crearLibro(@RequestBody libro: LibroEdicionDTO): ResponseEntity<ActionResponse> {
        val libroConvertido = libroMapper.libroEdicionDTOToLibro(libro)
        val nuevoLibro = libroService.crearLibro(libroConvertido)
        libroMapper.libroToLibroEdicionDTO(nuevoLibro)

        val mensajeExito = "Se creó el libro ${nuevoLibro.titulo} exitosamente"
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ActionResponse(mensajeExito))
    }

    @GetMapping("{idLibro}/{tipoDTO}")
    fun obtenerLibro(@PathVariable idLibro: Int, @PathVariable tipoDTO: GetLibroDTO): SealedGetLibroDTO {
        val libro = libroService.obtenerLibro(idLibro)

        when (tipoDTO) {
            GetLibroDTO.EDICION -> {
                val dto = libroMapper.libroToLibroEdicionDTO(libro)
                return SealedGetLibroDTO.Edicion(dto)
            }

            GetLibroDTO.DETALLE -> {
                val promedio = calificacionService.promedioCalificaciones(idLibro)
                val cantidad = calificacionService.cantidadCalificaciones(idLibro)
                val ultimas = calificacionService.ultimasCalificaciones(idLibro)

                val dtoDetalle = libroMapper.libroToLibroDetalleDTO(
                    libro, promedio, cantidad, ultimas
                )
                return SealedGetLibroDTO.Detalle(dtoDetalle)
            }
        }
    }

    @PutMapping
    fun editarLibro(
        @RequestBody libro: LibroEdicionDTO,
        @RequestParam idUsuario: Int,
    ): ResponseEntity<ActionResponse> {
        val libroObjeto = libroMapper.libroEdicionDTOToLibro(libro)
        val libroEditado = libroService.editarLibro(libroObjeto, idUsuario)
        libroMapper.libroToLibroEdicionDTO(libroEditado)

        val mensajeExito = "Se editó el libro ${libroEditado.titulo} exitosamente"
        return ResponseEntity
            .ok()
            .body(ActionResponse(mensajeExito))
    }

    @GetMapping("")
    fun getLibrosFilter(

        @RequestParam flagDiff : OrigenLibro,
        @ModelAttribute filtrosQuery: FiltrosLibrosQueryDTO,
        @RequestParam page: Int = 0,
        @RequestParam size: Int,
        @RequestParam userId: Int,
        @RequestParam ordenHomePage : OrdenHomePage ? = OrdenHomePage.NOMBRE_ASC,
        @RequestParam ordenPerfilPage: OrdenLibroPerfil ? = OrdenLibroPerfil.FECHA_AGREGADO,
        @RequestParam direccionPerfilPage: DireccionOrden ? = DireccionOrden.DESCENDENTE,
        @RequestParam disponibilidadLibrosPerfil: FiltroDisponibilidadPerfil ? = FiltroDisponibilidadPerfil.TODOS,

    ): PageResponse<LibroFiltradoDTO> {
        val filtrosDeHome = libroMapper.filtroDTOtoLibroFiltro(
            filtrosQuery,
            usuarioPrestador = filtrosQuery.usuarioPrestadorId?.let { usuarioService.getUsuarioById(it) },
            usuarioLector = filtrosQuery.usuarioLectorId?.let { usuarioService.getUsuarioById(it) },
        )

        val pageResult = libroService.getLibrosPorFiltros(
            filtrosDeHome,
            page,
            size,
            ordenHomePage!!,
            userId,
            flagDiff,
            disponibilidadLibrosPerfil!!,
            ordenPerfilPage!!,
            direccionPerfilPage!!)

        return PageResponse(
            content = pageResult.content.map {
                libroMapper.libroToLibroFiltradoDTO(
                    it,
                    calificacionService.promedioCalificaciones(it.id),
                    libroService.bringKarma(
                        it,
                        reservaService.cantidadReservas(it.id),
                        userId,
                        filtrosDeHome.fechaDesde,
                        filtrosDeHome.fechaHasta,
                    ),
                    libroService.disponibilidadLibro(it.id)
                )
            } ,
            page = pageResult.page,
            size = pageResult.size,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages
        )
    }

    @DeleteMapping("{idLibro}")
    fun borrarLibro(
        @PathVariable idLibro: Int,
        @RequestParam idUsuario: Int
    ): ResponseEntity<ActionResponse> {
        libroService.borrarLibro(idLibro, idUsuario)

        val mensajeExito = "El libro fue eliminado exitosamente."
        return ResponseEntity
            .ok()
            .body(ActionResponse(mensajeExito))
    }
}