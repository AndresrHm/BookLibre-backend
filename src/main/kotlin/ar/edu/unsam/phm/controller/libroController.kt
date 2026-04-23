package ar.edu.unsam.phm.controller

import ActionResponse
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.DireccionOrden
import ar.edu.unsam.phm.dto.FiltroDisponibilidadPerfil
import ar.edu.unsam.phm.dto.LibroEdicionDTO
import ar.edu.unsam.phm.dto.FiltrosLibrosQueryDTO
import ar.edu.unsam.phm.dto.GetLibroDTO
import ar.edu.unsam.phm.dto.OrdenHomePage
import ar.edu.unsam.phm.dto.OrdenLibroPerfil
import ar.edu.unsam.phm.dto.OrdenamientosLibrosDTO
import ar.edu.unsam.phm.dto.OrigenLibro
import ar.edu.unsam.phm.dto.SealedGetLibroDTO
import ar.edu.unsam.phm.dto.SealedGetLibroFiltradoDTO
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
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("libros")
class LibroController(
    val libroService: LibroService,
    val reservaService: ReservaService,
    val usuarioService: UsuarioService,
    val calificacionService: CalificacionService,
    val libroMapper: LibroMapper,
) {

    @PostMapping
    fun crearLibro(@RequestBody libro: LibroEdicionDTO): ResponseEntity<ActionResponse> {
        val libroConvertido = libroMapper.toLibro(libro)
        val nuevoLibro = libroService.crearLibro(libroConvertido)
        libroMapper.toLibroEdicionDTO(nuevoLibro)

        val mensajeExito = "Se creó el libro ${nuevoLibro.titulo} exitosamente"
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ActionResponse(mensajeExito))
    }

    @GetMapping("{idLibro}/{tipoDTO}")
    fun obtenerLibro(@PathVariable idLibro: Long, @PathVariable tipoDTO: GetLibroDTO): SealedGetLibroDTO {
        val libro = libroService.obtenerLibro(idLibro)

        when (tipoDTO) {
            GetLibroDTO.EDICION -> {
                val dto = libroMapper.toLibroEdicionDTO(libro)
                return SealedGetLibroDTO.Edicion(dto)
            }

            GetLibroDTO.DETALLE -> {
                val promedio = libro.calificacionPromedio()
                val cantidad = calificacionService.cantidadCalificaciones(idLibro)
                val ultimas = calificacionService.ultimasCalificaciones(idLibro)

                val dtoDetalle = libroMapper.toDetalleLibroDTO(
                    libro, promedio, cantidad, ultimas
                )
                return SealedGetLibroDTO.Detalle(dtoDetalle)
            }
        }
    }

    @PutMapping
    fun editarLibro(
        @RequestBody libro: LibroEdicionDTO,
        @RequestParam idUsuario: Long,
    ): ResponseEntity<ActionResponse> {
        val libroObjeto = libroMapper.toLibro(libro)
        val libroEditado = libroService.editarLibro(libroObjeto, idUsuario)
        libroMapper.toLibroEdicionDTO(libroEditado)

        val mensajeExito = "Se editó el libro ${libroEditado.titulo} exitosamente"
        return ResponseEntity
            .ok()
            .body(ActionResponse(mensajeExito))
    }

    @GetMapping
    fun getLibrosFilter(
        authentication: Authentication,
        @RequestParam flagDiff: OrigenLibro,
        @ModelAttribute filtrosQuery: FiltrosLibrosQueryDTO,
        @ModelAttribute ordenamientosLibros: OrdenamientosLibrosDTO,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 6,
        @RequestParam(required = false) usuarioPrestador: String?,
    ): PageResponse<SealedGetLibroFiltradoDTO> {
        val userId = authentication.name.toLong()

        val usuarioPrestadorFiltrado: Usuario? = filtrosQuery.usuarioPrestadorId
            ?.let { usuarioService.getUsuarioById(it) }
            ?: usuarioPrestador
                ?.takeIf { it.isNotBlank() }
                ?.let { usuarioService.getUsuarioByNombre(it).firstOrNull() }

        val filtrosDeHome = libroMapper.toLibroFiltro(
            filtrosQuery,
            usuarioPrestador = usuarioPrestadorFiltrado,
            usuarioLector = filtrosQuery.usuarioLectorId?.let { usuarioService.getUsuarioById(it) },
        )

        val pageResult = libroService.getLibrosPorFiltros(
            filtrosDeHome,
            page,
            size,
            ordenamientosLibros.ordenHomePage!!,
            userId,
            flagDiff,
            ordenamientosLibros.disponibilidadLibrosPerfil!!,
            ordenamientosLibros.ordenPerfilPage!!,
            ordenamientosLibros.direccionPerfilPage!!
        )

        return PageResponse(
            content = pageResult.content.mapNotNull { libro ->
                // Validar que el libro tiene ID antes de procesar
                val libroId = libro.id ?: return@mapNotNull null

                when (flagDiff) {
                    OrigenLibro.HOME -> SealedGetLibroFiltradoDTO.Home(
                        libroMapper.toLibroFiltradoDTO(
                            libro,
                            libro.calificacionPromedio(),
                            libroService.bringKarma(
                                libro,
                                reservaService.cantidadReservas(libroId),
                                userId,
                                filtrosDeHome.fechaDesde,
                                filtrosDeHome.fechaHasta,
                            ),
                            libroService.disponibilidadLibro(libroId)
                        )
                    )

                    OrigenLibro.PERFIL -> SealedGetLibroFiltradoDTO.Perfil(
                        libroMapper.toLibroPerfilUsuarioDTO(
                            libro,
                            libroService.disponibilidadLibro(libro.id!!)
                        )
                    )
                }

            },
            page = pageResult.page,
            size = pageResult.size,
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages
        )
    }

    @GetMapping("prestadoresDisponibles")
    fun getPrestadoresDisponibles(
        @RequestParam userId: Long,
        @RequestParam(required = false, defaultValue = "") search: String,
    ): List<String> = libroService.obtenerPrestadoresDisponibles(search, userId)

    @DeleteMapping("{idLibro}")
    fun borrarLibro(
        @PathVariable idLibro: Long,
        @RequestParam idUsuario: Long
    ): ResponseEntity<ActionResponse> {
        libroService.borrarLibro(idLibro, idUsuario)

        val mensajeExito = "El libro fue eliminado exitosamente."
        return ResponseEntity
            .ok()
            .body(ActionResponse(mensajeExito))
    }
}
