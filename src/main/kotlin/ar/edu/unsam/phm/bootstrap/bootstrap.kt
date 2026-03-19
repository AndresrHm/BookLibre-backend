package ar.edu.unsam.phm.bootstrap

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.comun
import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.coleccionable
import ar.edu.unsam.phm.domain.conDedicatoria
import ar.edu.unsam.phm.dto.EstadoReserva
import ar.edu.unsam.phm.repository.CalificacionRepository
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
    val libroRepository: LibroRepository,
    val calificacionRepository: CalificacionRepository
) {

    @Bean
    fun initData() = CommandLineRunner {

        val usuarios = crearUsuarios()
        val libros = crearLibros()
        val reservas = crearReservas(usuarios, libros)

        crearCalificaciones(reservas)
    }

    // ---------------- Usuarios ----------------
    // Tenemos 12 usuarios, con el quinto podemos entrar fácilmente (por el nombre)

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
                tipoUsuario = TipoUsuario.LECTOR,
                urlImagen = "https://ui-avatars.com/api/?name=Juan+Perez"
            ).apply { biblioKarma = 2000;passwordSesion = "juan123" },

            Usuario(
                id = 2,
                nombre = "Ana",
                apellido = "Gomez",
                descripcion = "Fan de novelas",
                email = "ana@mail.com",
                celular = "222222",
                ciudad = "Buenos Aires",
                tipoUsuario = TipoUsuario.LECTOR,
                urlImagen = "https://ui-avatars.com/api/?name=Ana+Gomez"
            ).apply { biblioKarma = 4000; passwordSesion = "ana123" },

            Usuario(
                id = 3,
                nombre = "Carlos",
                apellido = "Lopez",
                descripcion = "Devora libros",
                email = "carlos@mail.com",
                celular = "333333",
                ciudad = "Buenos Aires",
                tipoUsuario = TipoUsuario.LECTOR,
                urlImagen = "https://ui-avatars.com/api/?name=Carlos+Lopez"
            ).apply { biblioKarma = 1500; passwordSesion = "carlos123" },

            Usuario(
                id = 4,
                nombre = "Lucia",
                apellido = "Martinez",
                descripcion = "Fan del realismo mágico",
                email = "lucia@mail.com",
                celular = "444444",
                ciudad = "Buenos Aires",
                tipoUsuario = TipoUsuario.LECTOR,
                urlImagen = "https://ui-avatars.com/api/?name=Lucia+Martinez"
            ).apply { biblioKarma = 3200; passwordSesion = "lucia123" },

            Usuario(
                id = 5,
                nombre = "nombre123",
                apellido = "apellido123",
                descripcion = "descripcion123",
                email = "email123@mail.com",
                celular = "111111",
                ciudad = "Buenos Aires",
                tipoUsuario = TipoUsuario.LECTOR,
                urlImagen = "https://ui-avatars.com/api/?name=N+1+2+3"
            ).apply { biblioKarma = 9999; passwordSesion = "nombre123" },

            Usuario(
                id = 6,
                nombre = "Pedro",
                apellido = "Sosa",
                descripcion = "Lector frecuente",
                email = "pedro@mail.com",
                celular = "666666",
                ciudad = "Buenos Aires",
                tipoUsuario = TipoUsuario.LECTOR,
                urlImagen = "https://ui-avatars.com/api/?name=Pedro+Sosa"
            ).apply { biblioKarma = 2100; passwordSesion = "pedro123" },

            Usuario(
                id = 7,
                nombre = "Marina",
                apellido = "Ruiz",
                descripcion = "Le gusta la fantasia",
                email = "marina@mail.com",
                celular = "777777",
                ciudad = "Buenos Aires",
                tipoUsuario = TipoUsuario.LECTOR,
                urlImagen = "https://ui-avatars.com/api/?name=Marina+Ruiz"
            ).apply { biblioKarma = 1800; passwordSesion = "marina123" },

            Usuario(
                id = 8,
                nombre = "Diego",
                apellido = "Fernandez",
                descripcion = "Fan de novelas historicas",
                email = "diego@mail.com",
                celular = "888888",
                ciudad = "Buenos Aires",
                tipoUsuario = TipoUsuario.LECTOR,
                urlImagen = "https://ui-avatars.com/api/?name=Diego+Fernandez"
            ).apply { biblioKarma = 2600; passwordSesion = "diego123" },

            Usuario(
                id = 9,
                nombre = "Sofia",
                apellido = "Acosta",
                descripcion = "Lee de todo",
                email = "sofia@mail.com",
                celular = "999999",
                ciudad = "Buenos Aires",
                tipoUsuario = TipoUsuario.LECTOR,
                urlImagen = "https://ui-avatars.com/api/?name=Sofia+Acosta"
            ).apply { biblioKarma = 1750; passwordSesion = "sofia123" },

            Usuario(
                id = 10,
                nombre = "Nicolas",
                apellido = "Vega",
                descripcion = "Interesado en ciencia ficcion",
                email = "nicolas@mail.com",
                celular = "101010",
                ciudad = "Buenos Aires",
                tipoUsuario = TipoUsuario.LECTOR,
                urlImagen = "https://ui-avatars.com/api/?name=Nicolas+Vega"
            ).apply { biblioKarma = 1950; passwordSesion = "nicolas123" },

            Usuario(
                id = 11,
                nombre = "Valentina",
                apellido = "Mendez",
                descripcion = "Colecciona clasicos",
                email = "valentina@mail.com",
                celular = "111110",
                ciudad = "Buenos Aires",
                tipoUsuario = TipoUsuario.LECTOR,
                urlImagen = "https://ui-avatars.com/api/?name=Valentina+Mendez"
            ).apply { biblioKarma = 2300; passwordSesion = "valentina123" },

            Usuario(
                id = 12,
                nombre = "Tomas",
                apellido = "Ibarra",
                descripcion = "Lector nocturno",
                email = "tomas@mail.com",
                celular = "121212",
                ciudad = "Buenos Aires",
                tipoUsuario = TipoUsuario.LECTOR,
                urlImagen = "https://ui-avatars.com/api/?name=Tomas+Ibarra"
            ).apply { biblioKarma = 2050; passwordSesion = "tomas123" },

            )

        usuarios.forEach(usuarioRepository::create)

        return usuarios
    }

    // ---------------- Libros ----------------
    // Tenemos 50 libros, 10 para cada usuario, 17 comunes, 17 conDedicatoria y 16 coleccionables.

    private fun crearLibros(): List<Libro> {

        val libros = listOf(

            // ----- Libros de usuario id 1
            Libro(
                "Dune",
                comun,
                "Sci-fi clásica",
                "https://covers.openlibrary.org/b/id/15092781-L.jpg",
                Genero.CIENCIA_FICCION,
                "Frank Herbert",
                412,
                9780441172719L,
                Idioma.INGLES,
                "Chilton Books",
                LocalDate.of(1965, 8, 1),
                EstadoLibro.MUY_BUENO,
                1
            ),

            Libro(
                "Foundation",
                comun,
                "Saga Fundación",
                "https://covers.openlibrary.org/b/id/14612980-L.jpg",
                Genero.CIENCIA_FICCION,
                "Isaac Asimov",
                255,
                9780553293357L,
                Idioma.INGLES,
                "Gnome Press",
                LocalDate.of(1951, 1, 1),
                EstadoLibro.BUENO,
                1
            ),

            Libro(
                "Neuromancer",
                comun,
                "Cyberpunk clásico",
                "https://covers.openlibrary.org/b/id/8904053-L.jpg",
                Genero.CIENCIA_FICCION,
                "William Gibson",
                271,
                9780441569595L,
                Idioma.INGLES,
                "Ace",
                LocalDate.of(1984, 7, 1),
                EstadoLibro.MUY_BUENO,
                1
            ),

            Libro(
                "Hyperion",
                comun,
                "Ópera espacial",
                "https://covers.openlibrary.org/b/id/6926279-L.jpg",
                Genero.CIENCIA_FICCION,
                "Dan Simmons",
                482,
                9780553283686L,
                Idioma.INGLES,
                "Doubleday",
                LocalDate.of(1989, 5, 26),
                EstadoLibro.EXCELENTE,
                1
            ),

            Libro(
                "Snow Crash",
                comun,
                "Cyberpunk",
                "https://covers.openlibrary.org/b/id/7004665-L.jpg",
                Genero.CIENCIA_FICCION,
                "Neal Stephenson",
                480,
                9780553380958L,
                Idioma.INGLES,
                "Bantam",
                LocalDate.of(1992, 6, 1),
                EstadoLibro.BUENO,
                1
            ),

            Libro(
                "The Left Hand of Darkness",
                comun,
                "Sci-fi social",
                "https://covers.openlibrary.org/b/id/14920821-L.jpg",
                Genero.CIENCIA_FICCION,
                "Ursula K. Le Guin",
                304,
                9780441478125L,
                Idioma.INGLES,
                "Ace",
                LocalDate.of(1969, 1, 1),
                EstadoLibro.BUENO,
                1
            ),

            Libro(
                "The Hobbit",
                comun,
                "Fantasía",
                "https://covers.openlibrary.org/b/id/14627222-L.jpg",
                Genero.CIENCIA_FICCION,
                "J.R.R. Tolkien",
                310,
                9780547928227L,
                Idioma.INGLES,
                "Allen & Unwin",
                LocalDate.of(1937, 9, 21),
                EstadoLibro.MUY_BUENO,
                1
            ),

            Libro(
                "The Lord of the Rings",
                comun,
                "Fantasía épica",
                "https://covers.openlibrary.org/b/id/15131000-L.jpg",
                Genero.CIENCIA_FICCION,
                "J.R.R. Tolkien",
                1178,
                9780618640157L,
                Idioma.INGLES,
                "Allen & Unwin",
                LocalDate.of(1954, 7, 29),
                EstadoLibro.BUENO,
                1
            ),

            Libro(
                "Brave New World",
                comun,
                "Distopía",
                "https://covers.openlibrary.org/b/id/14846947-L.jpg",
                Genero.CIENCIA_FICCION,
                "Aldous Huxley",
                268,
                9780060850524L,
                Idioma.INGLES,
                "Chatto & Windus",
                LocalDate.of(1932, 1, 1),
                EstadoLibro.REGULAR,
                1
            ),

            Libro(
                "Fahrenheit 451",
                comun,
                "Distopía",
                "https://www.slate.com/content/dam/slideshows/arts/brow_beat/2012/06/06/ray-bradburys-fahrenheit-451-book-covers-through-time/jcr%3Acontent/slideshow/7/images%252Fslides%252F1976_1.jpg",
                Genero.CIENCIA_FICCION,
                "Ray Bradbury",
                194,
                9781451673319L,
                Idioma.INGLES,
                "Ballantine Books",
                LocalDate.of(1953, 10, 19),
                EstadoLibro.EXCELENTE,
                1
            ),

            // ----- Libros de usuario id 2
            Libro(
                "The Martian",
                comun,
                "Sci-fi moderna",
                "https://covers.openlibrary.org/b/id/14641755-L.jpg",
                Genero.CIENCIA_FICCION,
                "Andy Weir",
                369,
                9780804139021L,
                Idioma.INGLES,
                "Crown",
                LocalDate.of(2014, 2, 11),
                EstadoLibro.MUY_BUENO,
                1
            ),

            Libro(
                "1984",
                comun,
                "Distopía política",
                "https://covers.openlibrary.org/b/id/12693610-L.jpg",
                Genero.CIENCIA_FICCION,
                "George Orwell",
                328,
                9780451524935L,
                Idioma.INGLES,
                "Secker & Warburg",
                LocalDate.of(1949, 6, 8),
                EstadoLibro.BUENO,
                2
            ),

            Libro(
                "Animal Farm",
                comun,
                "Sátira política",
                "https://covers.openlibrary.org/b/id/15139354-L.jpg",
                Genero.DRAMA,
                "George Orwell",
                112,
                9780451526342L,
                Idioma.INGLES,
                "Secker & Warburg",
                LocalDate.of(1945, 8, 17),
                EstadoLibro.BUENO,
                2
            ),

            Libro(
                "El Aleph",
                comun,
                "Cuentos",
                "https://covers.openlibrary.org/b/id/14826417-L.jpg",
                Genero.DRAMA,
                "Jorge Luis Borges",
                180,
                9780307474728L,
                Idioma.ESPANOL,
                "Losada",
                LocalDate.of(1949, 6, 1),
                EstadoLibro.BUENO,
                2
            ),

            Libro(
                "Rayuela",
                comun,
                "Literatura latinoamericana",
                "https://covers.openlibrary.org/b/id/15103307-L.jpg",
                Genero.LITERATURA_CLASICA,
                "Julio Cortázar",
                736,
                9788437604578L,
                Idioma.ESPANOL,
                "Sudamericana",
                LocalDate.of(1963, 6, 28),
                EstadoLibro.BUENO,
                2
            ),

            Libro(
                "Don Quijote de la Mancha",
                comun,
                "Clásico español",
                "https://covers.openlibrary.org/b/id/13990987-L.jpg",
                Genero.LITERATURA_CLASICA,
                "Miguel de Cervantes",
                863,
                9780060934347L,
                Idioma.ESPANOL,
                "Francisco de Robles",
                LocalDate.of(1605, 1, 16),
                EstadoLibro.REGULAR,
                2
            ),

            Libro(
                "Cien años de soledad",
                comun,
                "Realismo mágico",
                "https://covers.openlibrary.org/b/id/14831384-L.jpg",
                Genero.LITERATURA_CLASICA,
                "Gabriel García Márquez",
                417,
                9780307474728L,
                Idioma.ESPANOL,
                "Sudamericana",
                LocalDate.of(1967, 5, 30),
                EstadoLibro.EXCELENTE,
                2
            ),

            Libro(
                "The Catcher in the Rye",
                conDedicatoria,
                "Novela clásica",
                "https://covers.openlibrary.org/b/id/15172466-L.jpg",
                Genero.DRAMA,
                "J.D. Salinger",
                214,
                9780316769488L,
                Idioma.INGLES,
                "Little Brown",
                LocalDate.of(1951, 7, 16),
                EstadoLibro.BUENO,
                2
            ),

            Libro(
                "To Kill a Mockingbird",
                conDedicatoria,
                "Drama judicial",
                "https://covers.openlibrary.org/b/id/14817209-L.jpg",
                Genero.DRAMA,
                "Harper Lee",
                281,
                9780061120084L,
                Idioma.INGLES,
                "J.B. Lippincott",
                LocalDate.of(1960, 7, 11),
                EstadoLibro.MUY_BUENO,
                2
            ),

            Libro(
                "The Great Gatsby",
                conDedicatoria,
                "Clásico americano",
                "https://covers.openlibrary.org/b/id/15144214-L.jpg",
                Genero.DRAMA,
                "F. Scott Fitzgerald",
                180,
                9780743273565L,
                Idioma.INGLES,
                "Scribner",
                LocalDate.of(1925, 4, 10),
                EstadoLibro.BUENO,
                2
            ),

            // ----- Libros de usuario id 3
            Libro(
                "Atomic Habits",
                conDedicatoria,
                "Hábitos",
                "https://covers.openlibrary.org/b/id/15108516-L.jpg",
                Genero.AUTOAYUDA,
                "James Clear",
                320,
                9780735211292L,
                Idioma.INGLES,
                "Penguin",
                LocalDate.of(2018, 10, 16),
                EstadoLibro.EXCELENTE,
                3
            ),

            Libro(
                "Sapiens",
                conDedicatoria,
                "Historia de la humanidad",
                "https://covers.openlibrary.org/b/id/8284312-L.jpg",
                Genero.LITERATURA_CLASICA,
                "Yuval Noah Harari",
                498,
                9780062316097L,
                Idioma.INGLES,
                "Harper",
                LocalDate.of(2011, 1, 1),
                EstadoLibro.MUY_BUENO,
                3
            ),

            Libro(
                "The Name of the Wind",
                conDedicatoria,
                "Fantasía épica moderna",
                "https://covers.openlibrary.org/b/id/8231856-L.jpg",
                Genero.CIENCIA_FICCION,
                "Patrick Rothfuss",
                662,
                9780756404741L,
                Idioma.INGLES,
                "DAW Books",
                LocalDate.of(2007, 3, 27),
                EstadoLibro.MUY_BUENO,
                3
            ),

            Libro(
                "Mistborn: The Final Empire",
                conDedicatoria,
                "Fantasía con sistema de magia",
                "https://covers.openlibrary.org/b/id/8231991-L.jpg",
                Genero.CIENCIA_FICCION,
                "Brandon Sanderson",
                541,
                9780765311788L,
                Idioma.INGLES,
                "Tor Books",
                LocalDate.of(2006, 7, 17),
                EstadoLibro.EXCELENTE,
                3
            ),

            Libro(
                "The Road",
                conDedicatoria,
                "Post-apocalíptico",
                "https://covers.openlibrary.org/b/id/7222246-L.jpg",
                Genero.DRAMA,
                "Cormac McCarthy",
                287,
                9780307387899L,
                Idioma.INGLES,
                "Knopf",
                LocalDate.of(2006, 9, 26),
                EstadoLibro.BUENO,
                3
            ),

            Libro(
                "Dracula",
                conDedicatoria,
                "Terror clásico",
                "https://covers.openlibrary.org/b/id/8235116-L.jpg",
                Genero.DRAMA,
                "Bram Stoker",
                418,
                9780486411095L,
                Idioma.INGLES,
                "Archibald Constable",
                LocalDate.of(1897, 5, 26),
                EstadoLibro.REGULAR,
                3
            ),

            Libro(
                "Frankenstein",
                conDedicatoria,
                "Gótico y ciencia ficción",
                "https://covers.openlibrary.org/b/id/8226191-L.jpg",
                Genero.CIENCIA_FICCION,
                "Mary Shelley",
                280,
                9780486282114L,
                Idioma.INGLES,
                "Lackington Hughes Harding",
                LocalDate.of(1818, 1, 1),
                EstadoLibro.BUENO,
                3
            ),

            Libro(
                "Clean Code",
                conDedicatoria,
                "Buenas prácticas de programación",
                "https://covers.openlibrary.org/b/id/9641984-L.jpg",
                Genero.AUTOAYUDA,
                "Robert C. Martin",
                464,
                9780132350884L,
                Idioma.INGLES,
                "Prentice Hall",
                LocalDate.of(2008, 8, 1),
                EstadoLibro.EXCELENTE,
                3
            ),

            Libro(
                "The Pragmatic Programmer",
                conDedicatoria,
                "Desarrollo de software",
                "https://covers.openlibrary.org/b/id/8097446-L.jpg",
                Genero.AUTOAYUDA,
                "Andrew Hunt",
                352,
                9780201616224L,
                Idioma.INGLES,
                "Addison-Wesley",
                LocalDate.of(1999, 10, 30),
                EstadoLibro.MUY_BUENO,
                3
            ),

            Libro(
                "Thinking, Fast and Slow",
                conDedicatoria,
                "Psicología y toma de decisiones",
                "https://covers.openlibrary.org/b/id/8228691-L.jpg",
                Genero.AUTOAYUDA,
                "Daniel Kahneman",
                499,
                9780374533557L,
                Idioma.INGLES,
                "Farrar, Straus and Giroux",
                LocalDate.of(2011, 10, 25),
                EstadoLibro.MUY_BUENO,
                3
            ),

            // ----- Libros de usuario id 4
            Libro(
                "The Silent Patient",
                conDedicatoria,
                "Thriller psicológico",
                "https://covers.openlibrary.org/b/id/9259251-L.jpg",
                Genero.DRAMA,
                "Alex Michaelides",
                336,
                9781250301697L,
                Idioma.INGLES,
                "Celadon Books",
                LocalDate.of(2019, 2, 5),
                EstadoLibro.EXCELENTE,
                4
            ),

            Libro(
                "Educated",
                conDedicatoria,
                "Memorias",
                "https://covers.openlibrary.org/b/id/9251996-L.jpg",
                Genero.DRAMA,
                "Tara Westover",
                334,
                9780399590504L,
                Idioma.INGLES,
                "Random House",
                LocalDate.of(2018, 2, 20),
                EstadoLibro.MUY_BUENO,
                4
            ),

            Libro(
                "The Alchemist",
                conDedicatoria,
                "Fábula espiritual",
                "https://covers.openlibrary.org/b/id/8128691-L.jpg",
                Genero.DRAMA,
                "Paulo Coelho",
                208,
                9780061122415L,
                Idioma.INGLES,
                "HarperOne",
                LocalDate.of(1988, 1, 1),
                EstadoLibro.BUENO,
                4
            ),

            Libro(
                "Inferno",
                conDedicatoria,
                "Thriller",
                "https://covers.openlibrary.org/b/id/8231995-L.jpg",
                Genero.DRAMA,
                "Dan Brown",
                480,
                9781400079155L,
                Idioma.INGLES,
                "Doubleday",
                LocalDate.of(2013, 5, 14),
                EstadoLibro.BUENO,
                4
            ),

            Libro(
                "Digital Fortress",
                coleccionable,
                "Tecnothriller",
                "https://covers.openlibrary.org/b/id/8231996-L.jpg",
                Genero.DRAMA,
                "Dan Brown",
                384,
                9780312335167L,
                Idioma.INGLES,
                "St. Martin's Press",
                LocalDate.of(1998, 1, 1),
                EstadoLibro.REGULAR,
                4
            ),

            Libro(
                "Angels & Demons",
                coleccionable,
                "Misterio",
                "https://covers.openlibrary.org/b/id/8231997-L.jpg",
                Genero.DRAMA,
                "Dan Brown",
                616,
                9780743493468L,
                Idioma.INGLES,
                "Pocket Books",
                LocalDate.of(2000, 5, 1),
                EstadoLibro.MUY_BUENO,
                4
            ),

            Libro(
                "The Da Vinci Code",
                coleccionable,
                "Conspiración",
                "https://covers.openlibrary.org/b/id/8231998-L.jpg",
                Genero.DRAMA,
                "Dan Brown",
                489,
                9780307474278L,
                Idioma.INGLES,
                "Doubleday",
                LocalDate.of(2003, 3, 18),
                EstadoLibro.MUY_BUENO,
                4
            ),

            Libro(
                "The Girl with the Dragon Tattoo",
                coleccionable,
                "Thriller",
                "https://covers.openlibrary.org/b/id/8231999-L.jpg",
                Genero.DRAMA,
                "Stieg Larsson",
                465,
                9780307454546L,
                Idioma.INGLES,
                "Norstedts",
                LocalDate.of(2005, 8, 1),
                EstadoLibro.EXCELENTE,
                4
            ),

            Libro(
                "Gone Girl",
                coleccionable,
                "Suspenso",
                "https://covers.openlibrary.org/b/id/8232000-L.jpg",
                Genero.DRAMA,
                "Gillian Flynn",
                422,
                9780307588371L,
                Idioma.INGLES,
                "Crown",
                LocalDate.of(2012, 6, 5),
                EstadoLibro.MUY_BUENO,
                4
            ),

            Libro(
                "The Shining",
                coleccionable,
                "Terror",
                "https://covers.openlibrary.org/b/id/8232001-L.jpg",
                Genero.DRAMA,
                "Stephen King",
                447,
                9780307743657L,
                Idioma.INGLES,
                "Doubleday",
                LocalDate.of(1977, 1, 28),
                EstadoLibro.EXCELENTE,
                4
            ),

            // ----- Libros de usuario id 5
            Libro(
                "It",
                coleccionable,
                "Terror",
                "https://covers.openlibrary.org/b/id/8232002-L.jpg",
                Genero.DRAMA,
                "Stephen King",
                1138,
                9781501142970L,
                Idioma.INGLES,
                "Viking",
                LocalDate.of(1986, 9, 15),
                EstadoLibro.BUENO,
                5
            ),

            Libro(
                "Carrie",
                coleccionable,
                "Terror",
                "https://covers.openlibrary.org/b/id/8232003-L.jpg",
                Genero.DRAMA,
                "Stephen King",
                199,
                9780307743664L,
                Idioma.INGLES,
                "Doubleday",
                LocalDate.of(1974, 4, 5),
                EstadoLibro.MUY_BUENO,
                5
            ),

            Libro(
                "The Outsider",
                coleccionable,
                "Suspenso",
                "https://covers.openlibrary.org/b/id/8232004-L.jpg",
                Genero.DRAMA,
                "Stephen King",
                576,
                9781501180989L,
                Idioma.INGLES,
                "Scribner",
                LocalDate.of(2018, 5, 22),
                EstadoLibro.EXCELENTE,
                5
            ),

            Libro(
                "The Institute",
                coleccionable,
                "Thriller",
                "https://covers.openlibrary.org/b/id/8232005-L.jpg",
                Genero.DRAMA,
                "Stephen King",
                576,
                9781982110567L,
                Idioma.INGLES,
                "Scribner",
                LocalDate.of(2019, 9, 10),
                EstadoLibro.MUY_BUENO,
                5
            ),

            Libro(
                "A Game of Thrones",
                coleccionable,
                "Fantasía",
                "https://covers.openlibrary.org/b/id/8232006-L.jpg",
                Genero.DRAMA,
                "George R. R. Martin",
                694,
                9780553103540L,
                Idioma.INGLES,
                "Bantam",
                LocalDate.of(1996, 8, 6),
                EstadoLibro.EXCELENTE,
                5
            ),

            Libro(
                "A Clash of Kings",
                coleccionable,
                "Fantasía",
                "https://covers.openlibrary.org/b/id/8232007-L.jpg",
                Genero.DRAMA,
                "George R. R. Martin",
                768,
                9780553108033L,
                Idioma.INGLES,
                "Bantam",
                LocalDate.of(1998, 11, 16),
                EstadoLibro.MUY_BUENO,
                5
            ),

            Libro(
                "A Storm of Swords",
                coleccionable,
                "Fantasía",
                "https://covers.openlibrary.org/b/id/8232008-L.jpg",
                Genero.DRAMA,
                "George R. R. Martin",
                973,
                9780553106633L,
                Idioma.INGLES,
                "Bantam",
                LocalDate.of(2000, 8, 8),
                EstadoLibro.EXCELENTE,
                5
            ),

            Libro(
                "A Feast for Crows",
                coleccionable,
                "Fantasía",
                "https://covers.openlibrary.org/b/id/8232009-L.jpg",
                Genero.DRAMA,
                "George R. R. Martin",
                753,
                9780553801507L,
                Idioma.INGLES,
                "Bantam",
                LocalDate.of(2005, 10, 17),
                EstadoLibro.BUENO,
                5
            ),

            Libro(
                "A Dance with Dragons",
                coleccionable,
                "Fantasía",
                "https://covers.openlibrary.org/b/id/8232010-L.jpg",
                Genero.DRAMA,
                "George R. R. Martin",
                1016,
                9780553801477L,
                Idioma.INGLES,
                "Bantam",
                LocalDate.of(2011, 7, 12),
                EstadoLibro.MUY_BUENO,
                5
            ),

            Libro(
                "Brief Answers to the Big Questions",
                coleccionable,
                "Ciencia",
                "https://covers.openlibrary.org/b/id/8232011-L.jpg",
                Genero.LITERATURA_CLASICA,
                "Stephen Hawking",
                256,
                9781984819192L,
                Idioma.INGLES,
                "Bantam",
                LocalDate.of(2018, 10, 16),
                EstadoLibro.EXCELENTE,
                5
            ),
        )

        libros.forEach(libroRepository::create)

        return libros
    }

    // ---------------- Reservas ----------------

    fun crearReservas(
        usuarios: List<Usuario>, libros: List<Libro>
    ): List<Reserva> {

        val u1 = usuarios.first { it.id == 1 }
        val u2 = usuarios.first { it.id == 2 }
        val u3 = usuarios.first { it.id == 3 }
        val u4 = usuarios.first { it.id == 4 }
        val u5 = usuarios.first { it.id == 5 }
        val u6 = usuarios.first { it.id == 6 }
        val u7 = usuarios.first { it.id == 7 }
        val u8 = usuarios.first { it.id == 8 }
        val u9 = usuarios.first { it.id == 9 }
        val u10 = usuarios.first { it.id == 10 }
        val u11 = usuarios.first { it.id == 11 }
        val u12 = usuarios.first { it.id == 12 }

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
            Reserva(libros[18], u2, u1, hoy.minusDays(7), hoy.plusDays(3)),

            // -------- u3 presta a u4 --------
            Reserva(libros[20], u3, u4, hoy.minusDays(12), hoy.minusDays(2)),
            Reserva(libros[21], u3, u4, hoy.minusDays(5), hoy.plusDays(2)),
            Reserva(libros[22], u3, u4, hoy.minusDays(2), hoy.plusDays(10)),
            Reserva(libros[23], u3, u4, hoy.minusDays(20), hoy.minusDays(1)),
            Reserva(libros[24], u3, u4, hoy.minusDays(3), hoy.plusDays(1)),
            Reserva(libros[25], u3, u4, hoy.minusDays(1), hoy.plusDays(15)),
            Reserva(libros[26], u3, u4, hoy.minusDays(9), hoy.minusDays(3)),
            Reserva(libros[27], u3, u4, hoy.minusDays(6), hoy.plusDays(3)),

            // -------- u3 presta a u5 --------
            Reserva(libros[28], u3, u5, hoy.minusDays(11), hoy.minusDays(4)),
            Reserva(libros[29], u3, u5, hoy.minusDays(4), hoy.plusDays(2)),
            Reserva(libros[20], u3, u5, hoy.minusDays(3), hoy.plusDays(8)),
            Reserva(libros[21], u3, u5, hoy.minusDays(16), hoy.minusDays(1)),
            Reserva(libros[22], u3, u5, hoy.minusDays(2), hoy.plusDays(1)),
            Reserva(libros[23], u3, u5, hoy.minusDays(1), hoy.plusDays(12)),
            Reserva(libros[24], u3, u5, hoy.minusDays(11), hoy.minusDays(4)),
            Reserva(libros[25], u3, u5, hoy.minusDays(7), hoy.plusDays(3)),

            // -------- u4 presta a u3 --------
            Reserva(libros[30], u4, u3, hoy.minusDays(14), hoy.minusDays(5)),
            Reserva(libros[31], u4, u3, hoy.minusDays(4), hoy.plusDays(2)),
            Reserva(libros[32], u4, u3, hoy.minusDays(3), hoy.plusDays(8)),
            Reserva(libros[33], u4, u3, hoy.minusDays(16), hoy.minusDays(1)),
            Reserva(libros[34], u4, u3, hoy.minusDays(2), hoy.plusDays(1)),
            Reserva(libros[35], u4, u3, hoy.minusDays(1), hoy.plusDays(12)),
            Reserva(libros[36], u4, u3, hoy.minusDays(11), hoy.minusDays(4)),
            Reserva(libros[37], u4, u3, hoy.minusDays(7), hoy.plusDays(3)),

            // -------- u4 presta a u5 --------
            Reserva(libros[38], u4, u5, hoy.minusDays(13), hoy.minusDays(3)),
            Reserva(libros[39], u4, u5, hoy.minusDays(5), hoy.plusDays(2)),
            Reserva(libros[30], u4, u5, hoy.minusDays(2), hoy.plusDays(9)),
            Reserva(libros[31], u4, u5, hoy.minusDays(17), hoy.minusDays(2)),
            Reserva(libros[32], u4, u5, hoy.minusDays(3), hoy.plusDays(1)),
            Reserva(libros[33], u4, u5, hoy.minusDays(1), hoy.plusDays(14)),
            Reserva(libros[34], u4, u5, hoy.minusDays(9), hoy.minusDays(4)),
            Reserva(libros[35], u4, u5, hoy.minusDays(6), hoy.plusDays(5)),

            // -------- u5 presta a u3 --------
            Reserva(libros[40], u5, u3, hoy.minusDays(13), hoy.minusDays(4)),
            Reserva(libros[41], u5, u3, hoy.minusDays(5), hoy.plusDays(3)),
            Reserva(libros[42], u5, u3, hoy.minusDays(2), hoy.plusDays(7)),
            Reserva(libros[43], u5, u3, hoy.minusDays(17), hoy.minusDays(1)),
            Reserva(libros[44], u5, u3, hoy.minusDays(3), hoy.plusDays(2)),
            Reserva(libros[45], u5, u3, hoy.minusDays(1), hoy.plusDays(11)),
            Reserva(libros[46], u5, u3, hoy.minusDays(10), hoy.minusDays(5)),
            Reserva(libros[47], u5, u3, hoy.minusDays(6), hoy.plusDays(4)),

            // -------- u5 presta a u4 --------
            Reserva(libros[48], u5, u4, hoy.minusDays(12), hoy.minusDays(3)),
            Reserva(libros[49], u5, u4, hoy.minusDays(4), hoy.plusDays(2)),
            Reserva(libros[40], u5, u4, hoy.minusDays(2), hoy.plusDays(8)),
            Reserva(libros[41], u5, u4, hoy.minusDays(18), hoy.minusDays(1)),
            Reserva(libros[42], u5, u4, hoy.minusDays(3), hoy.plusDays(1)),
            Reserva(libros[43], u5, u4, hoy.minusDays(1), hoy.plusDays(13)),
            Reserva(libros[44], u5, u4, hoy.minusDays(9), hoy.minusDays(4)),
            Reserva(libros[45], u5, u4, hoy.minusDays(7), hoy.plusDays(3)),

            // Reseñas extra para probar paginado sin lógica especial en bootstrap.
            Reserva(libros[0], u1, u3, hoy.minusDays(32), hoy.minusDays(22)),
            Reserva(libros[0], u1, u4, hoy.minusDays(28), hoy.minusDays(18)),
            Reserva(libros[0], u1, u5, hoy.minusDays(24), hoy.minusDays(14)),
            Reserva(libros[0], u1, u2, hoy.minusDays(40), hoy.minusDays(30)),
            Reserva(libros[0], u1, u3, hoy.minusDays(39), hoy.minusDays(29)),
            Reserva(libros[0], u1, u4, hoy.minusDays(38), hoy.minusDays(28)),
            Reserva(libros[0], u1, u5, hoy.minusDays(37), hoy.minusDays(27)),
            Reserva(libros[0], u1, u2, hoy.minusDays(36), hoy.minusDays(26)),
            Reserva(libros[0], u1, u3, hoy.minusDays(35), hoy.minusDays(25)),
            Reserva(libros[0], u1, u4, hoy.minusDays(34), hoy.minusDays(24)),
            Reserva(libros[0], u1, u5, hoy.minusDays(33), hoy.minusDays(23)),
            Reserva(libros[0], u1, u2, hoy.minusDays(31), hoy.minusDays(21)),
            Reserva(libros[0], u1, u3, hoy.minusDays(30), hoy.minusDays(20)),
            Reserva(libros[0], u1, u6, hoy.minusDays(29), hoy.minusDays(19)),
            Reserva(libros[0], u1, u7, hoy.minusDays(27), hoy.minusDays(17)),
            Reserva(libros[0], u1, u8, hoy.minusDays(26), hoy.minusDays(16)),
            Reserva(libros[0], u1, u9, hoy.minusDays(25), hoy.minusDays(15)),
            Reserva(libros[0], u1, u10, hoy.minusDays(23), hoy.minusDays(13)),
            Reserva(libros[0], u1, u11, hoy.minusDays(22), hoy.minusDays(12)),
            Reserva(libros[0], u1, u12, hoy.minusDays(21), hoy.minusDays(11)),
        )

        reservas.forEach(reservaRepository::create)

        reservas.forEach {
            it.usuarioLector.agregarLibroLeido()
        }

        // Vincula cada libro al usuario propietario persistido en memoria.
        usuarios.forEach { usuario ->
            libros.filter { it.idPropietario == usuario.id }.forEach(usuario::agregarLibroParaPrestar)
        }

        return reservas
    }

    // ---------------- Calificaciones ----------------

    private fun crearCalificaciones(
        reservas: List<Reserva>,
    ) {
        val hoy = LocalDate.now()
        val comentarios = listOf(
            "Muy bueno",
            "Excelente lectura",
            "Me encantó",
            "Interesante",
            "Recomendado",
            "No era lo que esperaba",
            "Volvería a leerlo"
        )
        val puntuaciones = listOf(2.5, 3.0, 3.5, 4.0, 4.5, 5.0)
        val calificaciones = reservas
            .filter { it.estado(hoy) == EstadoReserva.DEVUELTO }
            .map { reserva ->
                val libro = reserva.libro
                val usuario = reserva.usuarioLector
                val seed = libro.id + usuario.id
                Calificacion(
                    reserva = reserva,
                    puntuacion = puntuaciones[seed % puntuaciones.size],
                    comentario = comentarios[seed % comentarios.size],
                    fecha = reserva.fechaDevolucion
                )
            }
        val calificacionesUnicas = calificaciones
            .sortedBy { it.fecha }
            .associateBy { it.reserva.libro.id to it.reserva.usuarioLector.id }
            .values
            .toList()
        calificacionesUnicas.forEach {
            calificacionRepository.agregarCalificacion(it)
        }
        calificacionesUnicas
            .groupBy { it.reserva.libro.id }
            .forEach { (libroID, reseniasLibro) ->
                val promedio = calificacionRepository.obtenerPromedio(libroID)
                repeat(reseniasLibro.size) {
                    libroRepository.actualizarRating(libroID, promedio)
                }
            }
    }
}