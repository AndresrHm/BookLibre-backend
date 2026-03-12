package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.FiltrosDeLibrosDTO
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.LibroEdicionDTO
import ar.edu.unsam.phm.dto.toLibroDetalleDTO
import ar.edu.unsam.phm.dto.toLibroEdicionDTO
import ar.edu.unsam.phm.service.LibroService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class LibroController(val libroService: LibroService) {
    @PostMapping("libro/crear")
    fun crearLibro(@RequestBody libro: LibroEdicionDTO): LibroEdicionDTO {
        val nuevoLibro = libroService.crearLibro(libro)
        return nuevoLibro.toLibroEdicionDTO()
    }

    @GetMapping("libro/editar/{idLibro}/{idUsuario}") //Debería poder hacerse sin idLibro
    fun obtenerLibroParaEdicion(@PathVariable idLibro: Int, @PathVariable idUsuario: Int): LibroEdicionDTO {
        val libroParaEdicion = libroService.obtenerLibroParaEdicion(idLibro, idUsuario)
        return libroParaEdicion.toLibroEdicionDTO()
    }

    @GetMapping("libro/detalle/{idLibro}")
    fun obtenerLibroDetalle(@PathVariable idLibro: Int): LibroDetalleDTO {
        val libroDetalle = libroService.obtenerLibroDetalle(idLibro)
        return libroDetalle.toLibroDetalleDTO()
    }

    @PutMapping("libro/editar/{idLibro}/{idUsuario}")//Debería poder hacerse sin idLibro
    fun editarLibro(
        @RequestBody libro: LibroEdicionDTO,
        @PathVariable idLibro: Int,
        @PathVariable idUsuario: Int
    ): LibroEdicionDTO {
        val libroParaEdicion = libroService.editarLibro(libro, idLibro, idUsuario)
        return libroParaEdicion.toLibroEdicionDTO()
    }

    @PostMapping("/homePage/user/{id}/filter")
    fun getLibrosFilter(@RequestBody filtros: FiltrosDeLibrosDTO, @PathVariable id: Int): List<LibroDetalleDTO> {
        // TODO: Utilizar mappers para pasa de DTO's a objetos de dominio y viceversa
        // Obtener filtros
        // val filtros = filtrosMapper.DTOsAObjeto(filtrosDtos)

        val librosFiltrados = libroService.getLibrosPorFiltros(filtros)
        return librosFiltrados.map { it.toLibroDetalleDTO() }
    }

//    @PostMapping("client/register")
//    fun register(@RequestBody request: ClientRegisterRequest): ClientLoginResponse {
//        return clientService.register(request)
//    }
//
//    @GetMapping("client/{clientID}")
//    fun getUser(@PathVariable clientID: Int): ClientInfoDTO {
//        return clientService.getUser(clientID).toClientInfoDTO()
//    }
//
//    @PutMapping("client/{clientID}")
//    fun updateUser(@PathVariable clientID: Int, @RequestBody updateRequest: ClientInfoDTO): ClientInfoDTO {
//        return clientService.updateUser(clientID, updateRequest).toClientInfoDTO()
//    }
//
//    @GetMapping("client/availableIngredients/{clientID}")
//    fun getClientAvailableIngredients(@PathVariable clientID: Int): List<ClientIngredientDTO> {
//        return clientService.getAvailableIngredients(clientID).map { it.toClientIngredientDTO() }
//    }
}