package ar.edu.unsam.phm.bootstrap

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.lector
import ar.edu.unsam.phm.domain.publicador
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.comun
import ar.edu.unsam.phm.domain.lectorPublicador
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

        val usuarios = crearUsuarios()
        val libros = crearLibros()

        crearReservas(usuarios, libros)
    }

    // ---------------- USERS ----------------

    private fun crearUsuarios(): List<Usuario> {

        val usuarios = listOf(

            Usuario(
                id = 1,
                nombre = "Juan",
                apellido = "Perez",
                descripcion = "Lector de ciencia ficción",
                email = "juan@mail.com",
                celular = "111111",
                ciudad = "Buenos Aires",
                tipoUsuario = lectorPublicador,
                urlImagen = "https://ui-avatars.com/api/?name=Juan+Perez"
            ).apply { biblioKarma = 2000 },

            Usuario(
                id = 2,
                nombre = "Ana",
                apellido = "Gomez",
                descripcion = "Fan de novelas",
                email = "ana@mail.com",
                celular = "222222",
                ciudad = "Buenos Aires",
                tipoUsuario = lectorPublicador,
                urlImagen = "https://ui-avatars.com/api/?name=Ana+Gomez"
            ).apply { biblioKarma = 4000 }

        )

        usuarios.forEach(usuarioRepository::create)

        return usuarios
    }

    // ---------------- BOOKS ----------------

    private fun crearLibros(): List<Libro> {

        val libros = listOf(

            // ---------------- USUARIO 1 ----------------

            Libro("Dune", comun, "Sci-fi clásica", "/asd", Genero.CIENCIA_FICCION, "Frank Herbert", 412, 9780441172719L, Idioma.INGLES, "Chilton Books", LocalDate.of(1965,8,1), EstadoLibro.MUY_BUENO,1),

            Libro("Foundation", comun, "Saga Fundación", "/asd", Genero.CIENCIA_FICCION, "Isaac Asimov", 255, 9780553293357L, Idioma.INGLES, "Gnome Press", LocalDate.of(1951,1,1), EstadoLibro.BUENO,1),

            Libro("Neuromancer", comun, "Cyberpunk clásico", "/asd", Genero.CIENCIA_FICCION, "William Gibson", 271, 9780441569595L, Idioma.INGLES, "Ace", LocalDate.of(1984,7,1), EstadoLibro.MUY_BUENO,1),

            Libro("Hyperion", comun, "Ópera espacial", "/asd", Genero.CIENCIA_FICCION, "Dan Simmons", 482, 9780553283686L, Idioma.INGLES, "Doubleday", LocalDate.of(1989,5,26), EstadoLibro.EXCELENTE,1),

            Libro("Snow Crash", comun, "Cyberpunk", "/asd", Genero.CIENCIA_FICCION, "Neal Stephenson", 480, 9780553380958L, Idioma.INGLES, "Bantam", LocalDate.of(1992,6,1), EstadoLibro.BUENO,1),

            Libro("The Left Hand of Darkness", comun, "Sci-fi social", "/asd", Genero.CIENCIA_FICCION, "Ursula K. Le Guin", 304, 9780441478125L, Idioma.INGLES, "Ace", LocalDate.of(1969,1,1), EstadoLibro.BUENO,1),

            Libro("The Hobbit", comun, "Fantasía", "/asd", Genero.CIENCIA_FICCION, "J.R.R. Tolkien", 310, 9780547928227L, Idioma.INGLES, "Allen & Unwin", LocalDate.of(1937,9,21), EstadoLibro.MUY_BUENO,1),

            Libro("The Lord of the Rings", comun, "Fantasía épica", "/asd",Genero.CIENCIA_FICCION, "J.R.R. Tolkien", 1178, 9780618640157L, Idioma.INGLES, "Allen & Unwin", LocalDate.of(1954,7,29), EstadoLibro.BUENO,1),

            Libro("Brave New World", comun, "Distopía", "/asd", Genero.CIENCIA_FICCION, "Aldous Huxley", 268, 9780060850524L, Idioma.INGLES, "Chatto & Windus", LocalDate.of(1932,1,1), EstadoLibro.REGULAR,1),

            Libro("Fahrenheit 451", comun, "Distopía", "/asd", Genero.CIENCIA_FICCION, "Ray Bradbury", 194, 9781451673319L, Idioma.INGLES, "Ballantine Books", LocalDate.of(1953,10,19), EstadoLibro.EXCELENTE,1),

            Libro("The Martian", comun, "Sci-fi moderna", "/asd", Genero.CIENCIA_FICCION, "Andy Weir", 369, 9780804139021L, Idioma.INGLES, "Crown", LocalDate.of(2014,2,11), EstadoLibro.MUY_BUENO,1),

            // ---------------- USUARIO 2 ----------------

            Libro("1984", comun, "Distopía política", "/asd", Genero.CIENCIA_FICCION, "George Orwell", 328, 9780451524935L, Idioma.INGLES, "Secker & Warburg", LocalDate.of(1949,6,8), EstadoLibro.BUENO,2),

            Libro("Animal Farm", comun, "Sátira política", "/asd", Genero.DRAMA, "George Orwell", 112, 9780451526342L, Idioma.INGLES, "Secker & Warburg", LocalDate.of(1945,8,17), EstadoLibro.BUENO,2),

            Libro("El Aleph", comun, "Cuentos", "/asd", Genero.DRAMA, "Jorge Luis Borges", 180, 9780307474728L, Idioma.ESPANOL, "Losada", LocalDate.of(1949,6,1), EstadoLibro.BUENO,2),

            Libro("Rayuela", comun, "Literatura latinoamericana", "/asd", Genero.LITERATURA_CLASICA, "Julio Cortázar", 736, 9788437604578L, Idioma.ESPANOL, "Sudamericana", LocalDate.of(1963,6,28), EstadoLibro.BUENO,2),

            Libro("Don Quijote de la Mancha", comun, "Clásico español", "/asd", Genero.LITERATURA_CLASICA, "Miguel de Cervantes", 863, 9780060934347L, Idioma.ESPANOL, "Francisco de Robles", LocalDate.of(1605,1,16), EstadoLibro.REGULAR,2),

            Libro("Cien años de soledad", comun, "Realismo mágico", "/asd", Genero.LITERATURA_CLASICA, "Gabriel García Márquez", 417, 9780307474728L, Idioma.ESPANOL, "Sudamericana", LocalDate.of(1967,5,30), EstadoLibro.EXCELENTE,2),

            Libro("The Catcher in the Rye", comun, "Novela clásica", "/asd", Genero.DRAMA, "J.D. Salinger", 214, 9780316769488L, Idioma.INGLES, "Little Brown", LocalDate.of(1951,7,16), EstadoLibro.BUENO,2),

            Libro("To Kill a Mockingbird", comun, "Drama judicial", "/asd", Genero.DRAMA, "Harper Lee", 281, 9780061120084L, Idioma.INGLES, "J.B. Lippincott", LocalDate.of(1960,7,11), EstadoLibro.MUY_BUENO,2),

            Libro("The Great Gatsby", comun, "Clásico americano", "/asd", Genero.DRAMA, "F. Scott Fitzgerald", 180, 9780743273565L, Idioma.INGLES, "Scribner", LocalDate.of(1925,4,10), EstadoLibro.BUENO,2),

            Libro("Atomic Habits", comun, "Hábitos", "/asd", Genero.AUTOAYUDA, "James Clear", 320, 9780735211292L, Idioma.INGLES, "Penguin", LocalDate.of(2018,10,16), EstadoLibro.EXCELENTE,2),

            Libro("Sapiens", comun, "Historia de la humanidad", "/asd", Genero.LITERATURA_CLASICA, "Yuval Noah Harari", 498, 9780062316097L, Idioma.INGLES, "Harper", LocalDate.of(2011,1,1), EstadoLibro.MUY_BUENO,2)
        )

        libros.forEach(libroRepository::create)

        return libros
    }

    // ---------------- RESERVAS ----------------

    private fun crearReservas(
        usuarios: List<Usuario>,
        libros: List<Libro>
    ) {

        val u1 = usuarios.first { it.id == 1 }
        val u2 = usuarios.first { it.id == 2 }

        val hoy = LocalDate.now()

        val reservas = listOf(

            // -------- u1 presta a u2 --------

            Reserva(libros[0], u1, u2, hoy.minusDays(12), hoy.minusDays(2)), // devuelto
            Reserva(libros[1], u1, u2, hoy.minusDays(5), hoy.plusDays(2)),   // proximo
            Reserva(libros[2], u1, u2, hoy.minusDays(2), hoy.plusDays(10)),  // activo
            Reserva(libros[3], u1, u2, hoy.minusDays(20), hoy.minusDays(1)),
            Reserva(libros[4], u1, u2, hoy.minusDays(3), hoy.plusDays(1)),
            Reserva(libros[5], u1, u2, hoy.minusDays(1), hoy.plusDays(15)),
            Reserva(libros[6], u1, u2, hoy.minusDays(9), hoy.minusDays(3)),
            Reserva(libros[7], u1, u2, hoy.minusDays(6), hoy.plusDays(3)),

            // -------- u2 presta a u1 --------

            Reserva(libros[11], u2, u1, hoy.minusDays(14), hoy.minusDays(5)),
            Reserva(libros[12], u2, u1, hoy.minusDays(4), hoy.plusDays(2)),
            Reserva(libros[13], u2, u1, hoy.minusDays(3), hoy.plusDays(8)),
            Reserva(libros[14], u2, u1, hoy.minusDays(16), hoy.minusDays(1)),
            Reserva(libros[15], u2, u1, hoy.minusDays(2), hoy.plusDays(1)),
            Reserva(libros[16], u2, u1, hoy.minusDays(1), hoy.plusDays(12)),
            Reserva(libros[17], u2, u1, hoy.minusDays(11), hoy.minusDays(4)),
            Reserva(libros[18], u2, u1, hoy.minusDays(7), hoy.plusDays(3))
        )

        reservas.forEach(reservaRepository::create)

        val usuario = Usuario(
            id = 1,
            nombre = "Juan",
            apellido = "Perez",
            descripcion = "Lector de ciencia ficción",
            email = "juan@mail.com",
            celular = "111111",
            ciudad = "Buenos Aires",
            tipoUsuario = lectorPublicador,
            urlImagen = "https://ui-avatars.com/api/?name=Juan+Perez"
        ).apply { biblioKarma = 2000 }

        usuarioRepository.create(usuario)
    }


}
