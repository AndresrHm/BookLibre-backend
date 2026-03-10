package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.dto.FiltrosDeLibrosDTO
import ar.edu.unsam.phm.service.LibroService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class LibrosController(val libroService: LibroService) {

    //Refactorizar para que devuelva los parametros a travez de un DTO
    @GetMapping ("/homePage/user/{id}/filter")
    //@ApiOperation(value = "Devuelve los libros con filtro de un usuario en especifico")
    fun getLibrosFilter(@RequestBody filtros: FiltrosDeLibrosDTO, @PathVariable id: Int): List<Libro> {
        return libroService.getLibrosPorFiltros(filtros)
    }
}