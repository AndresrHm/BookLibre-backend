package ar.edu.unsam.phm.controller
import ar.edu.unsam.phm.dto.LoginRequestDTO
import ar.edu.unsam.phm.dto.RegisterRequestDTO
import ar.edu.unsam.phm.mapper.UsuarioMapper
import ar.edu.unsam.phm.service.LoginRegistroService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin("*")
@RestController
@RequestMapping("/login")
class ControllerLogin(val loginRegistroService: LoginRegistroService, private val usuarioMapper: UsuarioMapper) {

    @PostMapping("/iniciarUsuario")
    fun buscarUsuarioContraseniaUsuario(@RequestBody loginRequest: LoginRequestDTO) : Int {
        return loginRegistroService.buscarUsuarioContraseniaUsuario(loginRequest.username, loginRequest.password)
    }

    @PostMapping("/crearUsuario")
    fun crearUsuario(@RequestBody registerRequest: RegisterRequestDTO) {
        return loginRegistroService.crearUsuario(usuarioMapper.registerRequestDTOtoUsuario(registerRequest))
    }
}