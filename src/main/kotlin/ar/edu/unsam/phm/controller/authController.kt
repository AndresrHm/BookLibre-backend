package ar.edu.unsam.phm.controller

import ActionResponse
import ar.edu.unsam.phm.dto.LoginRequestDTO
import ar.edu.unsam.phm.dto.RegisterRequestDTO
import ar.edu.unsam.phm.mapper.UsuarioMapper
import ar.edu.unsam.phm.service.LoginRegistroService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin("*")
@RestController
@RequestMapping("/auth")
class ControllerLogin(val loginRegistroService: LoginRegistroService, private val usuarioMapper: UsuarioMapper) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequestDTO): Long {
        val usuario = loginRegistroService.buscarUsuarioContraseniaUsuario(
            loginRequest.username,
            loginRequest.password
        )
        return usuario.id ?: throw IllegalStateException("El usuario no existe")
    }

    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequestDTO): ResponseEntity<ActionResponse> {
        loginRegistroService.crearUsuario(usuarioMapper.registerRequestDTOtoUsuario(registerRequest))

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ActionResponse("Usuario creado exitosamente"))
    }
}
