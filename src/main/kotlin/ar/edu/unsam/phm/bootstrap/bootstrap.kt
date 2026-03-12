package ar.edu.unsam.phm.bootstrap

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Lector
import ar.edu.unsam.phm.domain.Publicador
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.comun
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
class BootstrapData(
    val reservaRepository: ReservaRepository,
    val usuarioRepository: UsuarioRepository,
    val libroRepository: LibroRepository
) {

    @Bean
    fun initData() = CommandLineRunner {

        val usuario1 = Usuario(
            nombre = "Juan",
            apellido = "Perez",
            descripcion = "Lector de ciencia ficción",
            email = "juan@mail.com",
            celular = "111111",
            ciudad = "Buenos Aires",
            tipoUsuario = mutableSetOf<TipoUsuario>(Lector, Publicador),
            urlImagen = "https://ui-avatars.com/api/?name=Juan+Perez",
            id = 1
        )

        val usuario2 = Usuario(
            nombre = "Ana",
            apellido = "Gomez",
            descripcion = "Fan de novelas",
            email = "ana@mail.com",
            celular = "222222",
            ciudad = "Buenos Aires",
            tipoUsuario = mutableSetOf<TipoUsuario>(Publicador),
            urlImagen = "https://ui-avatars.com/api/?name=Ana+Gomez",
            id = 2
        )

        usuarioRepository.create(usuario1)
        usuarioRepository.create(usuario2)

        usuario1.biblioKarma = 2000
        usuario2.biblioKarma = 4000

        val libros = (1..17).map {
            Libro(
                titulo = "Libro $it",
                tipoLibro = comun,
                descripcion = "Descripción libro $it",
                genero = Genero.CIENCIA_FICCION,
                autor = "Autor $it",
                cantidadPaginas = 200 + it,
                isbn13 = 100000000000,
                idioma = Idioma.ESPANOL,
                editorial = "Editorial $it",
                fechaPublicacion = LocalDate.now().minusYears(2),
                estadoLibro = EstadoLibro.EXCELENTE,
                portada = "https://www.giantfreakinrobot.com/wp-content/uploads/2022/08/rick-astley.jpg",
                idPropietario = 1
            )
        }

        libros.forEachIndexed { index, libro ->

            // como está hecho, el usuario 2 es lector de los 17 libros del usuario 1, puede calificar cualquiera.
            val reserva = Reserva(
                libro = libro,
                usuarioPrestador = usuario1,
                usuarioLector = usuario2,
                fechaRecogida = LocalDate.now().minusMonths((index + 1).toLong()),
                fechaDevolucion = LocalDate.now().minusDays((10 - index).toLong())
            )

            reservaRepository.create(reserva)
        }

        libroRepository.create(libros[0])

        for (i in 1..3) {
            usuario1.agregarLibroParaPrestar(libros[i])
        }

    }
}