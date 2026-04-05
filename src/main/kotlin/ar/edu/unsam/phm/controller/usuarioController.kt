//package ar.edu.unsam.phm.controller
//
//import ar.edu.unsam.phm.dto.SealedGetUsuarioDTO
//import ar.edu.unsam.phm.dto.TipoDTOUsuario
//import ar.edu.unsam.phm.dto.UsuarioEdicionDTO
//import ar.edu.unsam.phm.mapper.UsuarioMapper
//import ar.edu.unsam.phm.service.UsuarioService
//import org.springframework.web.bind.annotation.CrossOrigin
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.PathVariable
//import org.springframework.web.bind.annotation.PutMapping
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//
//@RestController
//@RequestMapping("usuario")
//@CrossOrigin("*")
//class UsuarioController(
//    val usuarioService: UsuarioService,
//    val usuarioMapper: UsuarioMapper
//) {
//
//    @PutMapping
//    fun updateUsuario(
//        @RequestBody usuario: UsuarioEdicionDTO
//    ) {
//        val usuarioNuevo = usuarioMapper.usuarioEdicionDTOtoLibro(usuario)
//        usuarioService.usuarioUpdate(usuarioNuevo)
//    }
//
//    @GetMapping("{usuarioId}/{tipoDTO}")
//    fun getUsuarioType(@PathVariable usuarioId: Long, @PathVariable tipoDTO: String): SealedGetUsuarioDTO {
//        val usuario = usuarioService.getUsuarioById(usuarioId)
//        val tipoDTOEnum = usuarioMapper.toTipoDTOUsuario(tipoDTO)
//
//        when (tipoDTOEnum) {
//            TipoDTOUsuario.HEADER -> {
//                val dto = usuarioMapper.toUsuarioHeaderDTO(usuario)
//                return SealedGetUsuarioDTO.Header(dto)
//            }
//
//            TipoDTOUsuario.UPDATE -> {
//                val dto = usuarioMapper.toUsuarioEdicionDTO(usuario)
//                return SealedGetUsuarioDTO.Update(dto)
//            }
//
//            TipoDTOUsuario.PERFIL -> {
//                val dto = usuarioMapper.toUsuarioPerfilDTO(usuario)
//                return SealedGetUsuarioDTO.Perfil(dto)
//            }
//
//            TipoDTOUsuario.KARMA -> {
//                val dto = usuarioMapper.toUsuarioKarma(usuario)
//                return SealedGetUsuarioDTO.Karma(dto)
//            }
//
//            TipoDTOUsuario.TIPOUSUARIO -> {
//                val tipo = usuario.tipoUsuario
//                return SealedGetUsuarioDTO.TipoPerfil(tipo)
//            }
//        }
//    }
//
//}