package ar.edu.unsam.phm.bootstrap

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.TipoLibro
import ar.edu.unsam.phm.domain.TipoLibroStrategy
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.repository.ReservaRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
class BootstrapData(
    val reservaRepository: ReservaRepository
) {

    @Bean
    fun initData() = CommandLineRunner {

        val usuario1 = Usuario(
            "Juan",
            "Perez",
            "Lector de ciencia ficción",
            "juan@mail.com",
            "111111",
            "Buenos Aires",
            id = 1
        )

        val usuario2 = Usuario(
            "Ana",
            "Gomez",
            "Fan de novelas",
            "ana@mail.com",
            "222222",
            "Buenos Aires",
            id = 2
        )

        val libros = (1..17).map {
            Libro(
                titulo = "Libro $it",
                tipoLibro = TipoLibro.COMUN,
                descripcion = "Descripción libro $it",
                genero = Genero.CIENCIA_FICCION,
                autor = "Autor $it",
                cantidadPaginas = 200 + it,
                isbn13 = 100000 + it,
                idioma = Idioma.ESPANOL,
                editorial = "Editorial $it",
                fechaPublicacion = LocalDate.now().minusYears(2),
                estadoLibro = EstadoLibro.EXCELENTE,
                imagenURL = "https://www.giantfreakinrobot.com/wp-content/uploads/2022/08/rick-astley.jpg"
            ).apply {
                calificacion = Calificacion.entries.toTypedArray().random()
            }
        }

        libros.forEachIndexed { index, libro ->

            val reserva = Reserva(
                libro = libro,
                usuarioPrestador = usuario1,
                usuarioLector = usuario2,
                fechaRecogida = LocalDate.now().minusDays((index + 1).toLong()),
                fechaDevolucion = LocalDate.now().plusDays((10 - index).toLong())
            )

            reservaRepository.create(reserva)
        }
    }
}