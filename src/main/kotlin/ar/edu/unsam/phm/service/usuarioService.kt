package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.controller.ReservaType
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.UsuarioEdicionDTO
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.mapper.UsuarioMapper
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    val usuarioRepositorio: UsuarioRepository,
    val usuarioMapper: UsuarioMapper,
    val reservaRepositorio: ReservaRepository,
    val libroRepositorio: LibroRepository
) {
    fun usuarioById(id: Int) =
        usuarioRepositorio.getById(id) ?: throw NotFoundException(ErrorStrings.usuarioNoEncontradoPorId(id))

    fun getUsuarioUpdate(id: Int): UsuarioEdicionDTO {
        val usuario = usuarioById(id)
        return usuarioMapper.toUsuarioEdicionDTO(usuario)
    }

    fun getUsuarioTipo(id: Int): TipoUsuario {
        val usuario = usuarioById(id)
        return usuario.tipoUsuario
    }

    fun usuarioUpdate(datosUsuario: UsuarioEdicionDTO): Usuario {
        if (
            camposObligatoriosVacios(datosUsuario)
        ) {
            throw BusinessException(ErrorStrings.CAMPOS_OBLIGATORIOS_INCOMPLETOS)
        }

        when (datosUsuario.tipoUsuario) {
            TipoUsuario.LECTOR -> {
                if (reservaRepositorio.findReservasByType(
                        datosUsuario.id,
                        ReservaType.TO_ME,
                        1,
                        1, ""
                    ).totalElements > 0
                ) {
                    throw BusinessException(ErrorStrings.USUARIO_TIENE_LIBROS_PRESTADOS)
                }
                libroRepositorio.eliminarPorIDPropietario(datosUsuario.id)
            }

            TipoUsuario.PUBLICADOR ->
                if (reservaRepositorio.findReservasByType(
                        datosUsuario.id,
                        ReservaType.BY_ME,
                        1,
                        1,
                        ""
                    ).totalElements > 0
                ) {
                    throw BusinessException(ErrorStrings.USUARIO_TIENE_DEVOLUCIONES_PENDIENTES)
                }

            TipoUsuario.LECTORPUBLICADOR -> {}
        }
        val usuario = usuarioById(datosUsuario.id)
        usuario.update(datosUsuario)
        usuarioRepositorio.update(usuario)
        return usuario
    }

    private fun camposObligatoriosVacios(datosUsuario: UsuarioEdicionDTO): Boolean =
        datosUsuario.nombre == "" ||
                datosUsuario.apellido == "" ||
                datosUsuario.ciudad == ""
}
