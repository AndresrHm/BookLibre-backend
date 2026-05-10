package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.errors.BusinessException
import ar.edu.unsam.phm.errors.NotFoundException
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UsuarioService(
    val usuarioRepository: UsuarioRepository,
    val reservaRepository: ReservaRepository,
    val libroRepository: LibroRepository
) {

    fun getUsuarioById(id: Long) =
        usuarioRepository.findByIdOrNull(id) ?: throw NotFoundException("No se encontró usuario con id $id")


    fun getUsuarioByNombre(nombre: String) =
        usuarioRepository.findByNombre(nombre) ?: throw NotFoundException("No se encontró usuario con nombre $nombre")

    @Transactional
    fun usuarioUpdate(usuario: Usuario): Usuario {
        when {
            usuario.esLectorPublicador() -> {
                // Si es lector/publicador, no debe eliminarse nada
            }
            // Para eliminar usuario, no acá
            // Si el usuario tiene libros prestados, puede ser eliminado, pero la baja es lógica...
            usuario.esLector() -> {

                libroRepository.deleteByIdPropietario(usuario.id!!)
            }

            usuario.esPublicador() ->
                if (
                    reservaRepository.countByUsuarioPrestadorId(usuario.id!!) > 0
                ) {
                    throw BusinessException(ErrorStrings.USUARIO_TIENE_DEVOLUCIONES_PENDIENTES)
                }
        }
        usuarioRepository.save(usuario)
        return usuario
    }


    fun obtenerCantidadDeLibrosPrestados(usuarioId: Long): Int {
        return reservaRepository.findAll()
            .filter { it.usuarioPrestador.id == usuarioId }
            .toList()
            .size
    }

    fun obtenerCantidadDeLibrosLeidos(usuarioId: Long): Int {
        return reservaRepository.findAll()
            .filter { it.usuarioLector.id == usuarioId }
            .toList()
            .size
    }

    fun getNombreDeUsuario(id: Long): String? {
        return usuarioRepository.findByIdOrNull(id)?.nombre
    }

    // por ahora dejo email, no deberíamos permitir que un email sea compartido (podríamos meter validaciones)
    fun validarExistenciaUsuario(usuarioId: Long): Usuario =
        usuarioRepository.findById(usuarioId).orElseThrow { BusinessException(ErrorStrings.CREDENCIALES_INVALIDAS) }
}

