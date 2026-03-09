package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.LibroEditadoDTO
import ar.edu.unsam.phm.dto.toLibroEditadoDTO
import ar.edu.unsam.phm.service.LibroService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class libroController(val libroService: LibroService) {
    @PostMapping("libro/crear")
    fun crearLibro(@RequestBody libro: LibroEditadoDTO): LibroEditadoDTO {
        val nuevoLibro = libroService.crearLibro(libro)
        return nuevoLibro.toLibroEditadoDTO()
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