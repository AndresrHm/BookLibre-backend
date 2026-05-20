package ar.edu.unsam.phm.bootstrap

import ar.edu.unsam.phm.domain.EstadoLibro
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Idioma
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.EstadoReserva
import ar.edu.unsam.phm.domain.Rol
import ar.edu.unsam.phm.domain.TipoLibro
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.repository.CalificacionRepository
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.RolRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.time.LocalDate
import kotlin.random.Random

@Configuration
@EnableJpaAuditing
class BootstrapData(
    val reservaRepository: ReservaRepository,
    val usuarioRepository: UsuarioRepository,
    val libroRepository: LibroRepository,
    val calificacionRepository: CalificacionRepository,
    val rolRepository: RolRepository
) {
    lateinit var rolLector: Rol
    lateinit var rolPublicador: Rol

    @Bean
    fun initData() = CommandLineRunner {

        rolLector = Rol().apply { name = TipoUsuario.LECTOR.roleName }
        rolPublicador = Rol().apply { name = TipoUsuario.PUBLICADOR.roleName }
        rolRepository.saveAll(listOf(rolLector, rolPublicador))

        val usuarios = crearUsuarios()
        val libros = crearLibros()
        val reservas = crearReservas(usuarios, libros)

        crearCalificaciones(reservas)
        actualizarCalificacionesLibros()
    }

    // ---------------- Usuarios ----------------
    // Tenemos 12 usuarios, con el quinto podemos entrar fácilmente (por el nombre)

    private fun crearUsuarios(): List<Usuario> {

        val usuarios: List<Usuario> = listOf(
            // Lectores publicadores
            Usuario(
                nombre = "Juan",
                apellido = "Perez",
                descripcion = "Lector de ciencia ficción",
                email = "juan@mail.com",
                celular = "111111",
                ciudad = "Buenos Aires",
                roles = mutableSetOf(rolLector, rolPublicador),
                urlImagen = "https://ui-avatars.com/api/?name=Juan+Perez",
                password = "juan123"
            ).apply { biblioKarma = 2000; id = 1 },

            Usuario(
                nombre = "Ana",
                apellido = "Gomez",
                descripcion = "Fan de novelas",
                email = "ana@mail.com",
                celular = "222222",
                ciudad = "Buenos Aires",
                roles = mutableSetOf(rolLector, rolPublicador),
                urlImagen = "https://ui-avatars.com/api/?name=Ana+Gomez",
                password = "ana123"
            ).apply { biblioKarma = 4000; id = 2 },

            Usuario(
                nombre = "Carlos",
                apellido = "Lopez",
                descripcion = "Devora libros",
                email = "carlos@mail.com",
                celular = "333333",
                ciudad = "Buenos Aires",
                roles = mutableSetOf(rolLector, rolPublicador),
                urlImagen = "https://ui-avatars.com/api/?name=Carlos+Lopez",
                password = "carlos123"
            ).apply { biblioKarma = 1500; id = 3 },

            Usuario(
                nombre = "Lucia",
                apellido = "Martinez",
                descripcion = "Fan del realismo mágico",
                email = "lucia@mail.com",
                celular = "444444",
                ciudad = "Buenos Aires",
                roles = mutableSetOf(rolLector, rolPublicador),
                urlImagen = "https://ui-avatars.com/api/?name=Lucia+Martinez",
                password = "lucia123"
            ).apply { biblioKarma = 3200; id = 4 },

            Usuario(
                nombre = "nombre123",
                apellido = "apellido123",
                descripcion = "descripcion123",
                email = "email123@mail.com",
                celular = "111111",
                ciudad = "Buenos Aires",
                roles = mutableSetOf(rolLector, rolPublicador),
                urlImagen = "https://ui-avatars.com/api/?name=N+1+2+3",
                password = "nombre123"
            ).apply { biblioKarma = 9999; id = 5 },

            // Lectores
            Usuario(
                nombre = "Pedro",
                apellido = "Sosa",
                descripcion = "Lector frecuente",
                email = "pedro@mail.com",
                celular = "666666",
                ciudad = "Buenos Aires",
                roles = mutableSetOf(rolLector),
                urlImagen = "https://ui-avatars.com/api/?name=Pedro+Sosa",
                password = "pedro123"
            ).apply { biblioKarma = 2100; id = 6 },

            Usuario(
                nombre = "Marina",
                apellido = "Ruiz",
                descripcion = "Le gusta la fantasia",
                email = "marina@mail.com",
                celular = "777777",
                ciudad = "Buenos Aires",
                roles = mutableSetOf(rolLector),
                urlImagen = "https://ui-avatars.com/api/?name=Marina+Ruiz",
                password = "marina123"
            ).apply { biblioKarma = 1800; id = 7 },

            Usuario(
                nombre = "Diego",
                apellido = "Fernandez",
                descripcion = "Fan de novelas historicas",
                email = "diego@mail.com",
                celular = "888888",
                ciudad = "Buenos Aires",
                roles = mutableSetOf(rolLector),
                urlImagen = "https://ui-avatars.com/api/?name=Diego+Fernandez",
                password = "diego123"
            ).apply { biblioKarma = 2600; id = 8 },

            Usuario(
                nombre = "Sofia",
                apellido = "Acosta",
                descripcion = "Lee de todo",
                email = "sofia@mail.com",
                celular = "999999",
                ciudad = "Buenos Aires",
                roles = mutableSetOf(rolLector),
                urlImagen = "https://ui-avatars.com/api/?name=Sofia+Acosta",
                password = "sofia123"
            ).apply { biblioKarma = 1750; id = 9 },

            // Publicadores
            Usuario(
                nombre = "Nicolas",
                apellido = "Vega",
                descripcion = "Interesado en ciencia ficcion",
                email = "nicolas@mail.com",
                celular = "101010",
                ciudad = "Buenos Aires",
                roles = mutableSetOf(rolPublicador),
                urlImagen = "https://ui-avatars.com/api/?name=Nicolas+Vega",
                password = "nicolas123"
            ).apply { biblioKarma = 1950; id = 10 },

            Usuario(
                nombre = "Valentina",
                apellido = "Mendez",
                descripcion = "Colecciona clasicos",
                email = "valentina@mail.com",
                celular = "111110",
                ciudad = "Buenos Aires",
                roles = mutableSetOf(rolPublicador),
                urlImagen = "https://ui-avatars.com/api/?name=Valentina+Mendez",
                password = "valentina123"
            ).apply { biblioKarma = 2300; id = 11 },

            Usuario(
                nombre = "Tomas",
                apellido = "Ibarra",
                descripcion = "Lector nocturno",
                email = "tomas@mail.com",
                celular = "121212",
                ciudad = "Buenos Aires",
                roles = mutableSetOf(rolPublicador),
                urlImagen = "https://ui-avatars.com/api/?name=Tomas+Ibarra",
                password = "tomas123"
            ).apply { biblioKarma = 2050; id = 12 },

            )

        usuarios.forEach { it.encriptarPassword() }
        usuarioRepository.saveAll(usuarios)

        return usuarios
    }

    // ---------------- Libros ----------------
    // Tenemos 50 libros, 10 para cada usuario, 17 comunes, 17 TipoLibro.CON_DEDICATORIA y 16 TipoLibro.COLECCIONABLEs.

    private fun crearLibros(): List<Libro> {

        val hoy = LocalDate.now()
        val random = Random(42)
        val libros: List<Libro> = listOf(

            // ----- Libros de usuario id 1
            Libro(
                "Dune",
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.COMUN,
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
                TipoLibro.CON_DEDICATORIA,
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
                TipoLibro.CON_DEDICATORIA,
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
                TipoLibro.CON_DEDICATORIA,
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
                TipoLibro.CON_DEDICATORIA,
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
                TipoLibro.CON_DEDICATORIA,
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
                TipoLibro.CON_DEDICATORIA,
                "Fantasía épica moderna",
                "https://grimoakpress.com/cdn/shop/files/cover-notw-fc.jpg?v=1706902909&width=823",
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
                TipoLibro.CON_DEDICATORIA,
                "Fantasía con sistema de magia",
                "https://covers.openlibrary.org/b/id/15158368-M.jpg",
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
                TipoLibro.CON_DEDICATORIA,
                "Post-apocalíptico",
                "https://covers.openlibrary.org/b/id/14818120-M.jpg",
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
                TipoLibro.CON_DEDICATORIA,
                "Terror clásico",
                "https://covers.openlibrary.org/b/id/12622155-M.jpg",
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
                TipoLibro.CON_DEDICATORIA,
                "Gótico y ciencia ficción",
                "https://covers.openlibrary.org/b/id/15113302-M.jpg",
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
                TipoLibro.CON_DEDICATORIA,
                "Buenas prácticas de programación",
                "https://covers.openlibrary.org/b/id/15126503-M.jpg",
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
                TipoLibro.CON_DEDICATORIA,
                "Desarrollo de software",
                "https://covers.openlibrary.org/b/id/15136784-M.jpg",
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
                TipoLibro.CON_DEDICATORIA,
                "Psicología y toma de decisiones",
                "https://covers.openlibrary.org/b/id/15129456-M.jpg",
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
                TipoLibro.CON_DEDICATORIA,
                "Thriller psicológico",
                "https://covers.openlibrary.org/b/id/15105887-M.jpg",
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
                TipoLibro.CON_DEDICATORIA,
                "Memorias",
                "https://covers.openlibrary.org/b/id/15216613-M.jpg",
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
                TipoLibro.CON_DEDICATORIA,
                "Fábula espiritual",
                "https://covers.openlibrary.org/b/id/15121528-M.jpg",
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
                TipoLibro.CON_DEDICATORIA,
                "Thriller",
                "https://covers.openlibrary.org/b/id/10660297-M.jpg",
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
                TipoLibro.COLECCIONABLE,
                "Tecnothriller",
                "https://danbrown.com/wp-content/uploads/2024/10/Dan-Brown_Digital-Fortress_book-cover-2025.jpg",
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
                TipoLibro.COLECCIONABLE,
                "Misterio",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRqCjOemsH_lGbfwqRKnDzUGqDueaH7lSce2Q&s",
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
                TipoLibro.COLECCIONABLE,
                "Conspiración",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQFin2Zhek2Np48tHxJVm-iXLV62m95hwzXsw&s",
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
                TipoLibro.COLECCIONABLE,
                "Thriller",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSYELT7AuGotjdy16V27RPo0RXmk7mHn0ExVA&s",
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
                TipoLibro.COLECCIONABLE,
                "Suspenso",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQxWtSxdC6vDCA7fR2LCFPSWauWRzKIz_Creg&s",
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
                TipoLibro.COLECCIONABLE,
                "Terror",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTqlyohl7zhBPkbXNkuj1ruX6PGxUifAvaXrw&s",
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
                TipoLibro.COLECCIONABLE,
                "Terror",
                "https://s2982.pcdn.co/wp-content/uploads/2017/03/it-stephen-king-minimalist-kindle-198x300.jpg.optimal.jpg",
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
                TipoLibro.COLECCIONABLE,
                "Terror",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTrQ73mJQNfwVDQ3vIP-JSUSZFQVoCYurgYAg&s",
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
                TipoLibro.COLECCIONABLE,
                "Suspenso",
                "https://http2.mlstatic.com/D_NQ_NP_612525-MLA70630455631_072023-O.webp",
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
                TipoLibro.COLECCIONABLE,
                "Thriller",
                "https://www.amazon.co.uk/Institute-Stephen-King/dp/1529355397",
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
                TipoLibro.COLECCIONABLE,
                "Fantasía",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQnuvRPByL01_3eKROeSmksZYZflZBVTWu1Zw&s",
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
                TipoLibro.COLECCIONABLE,
                "Fantasía",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR48zujd7nOIvlGxwMNYoD22yQ_rvI44YTh5w&s",
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
                TipoLibro.COLECCIONABLE,
                "Fantasía",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBdrOINBhqcIrC2KzQThzs3kLHiejQaJa3KA&s",
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
                TipoLibro.COLECCIONABLE,
                "Fantasía",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSSUqTJfcJ0UuS_l-2LGn5R7xS26egg4qkAeA&s",
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
                TipoLibro.COLECCIONABLE,
                "Fantasía",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRJrbMS15csT1Z7UazSa_wkad0Ae6LFY_IQkg&s",
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
                TipoLibro.COLECCIONABLE,
                "Ciencia",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSQF_mmSciNSvke4-svnhAkvofrb2BK78GnKg&s",
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
        ).map { libro ->
            libro.apply {
                // Fecha aleatoria dentro de los ultimos 365 dias (sin futuras).
                fechaAgregado = hoy.minusDays(random.nextLong(0, 366))
            }
        }

        // Upsert: actualizar si existe (mismo ISBN + propietario), crear si no
        val librosGuardados = libros.map { libroNuevo ->
            val libroExistente = libroRepository.findByIsbn13AndIdPropietario(
                libroNuevo.isbn13,
                libroNuevo.idPropietario
            )
            if (libroExistente != null) {
                // Actualizar libro existente manteniendo su ID
                libroNuevo.id = libroExistente.id
                libroRepository.save(libroNuevo)
            } else {
                // Crear nuevo libro
                libroRepository.save(libroNuevo)
            }
        }

        return librosGuardados
    }

    // ---------------- Reservas ----------------

    fun crearReservas(
        usuarios: List<Usuario>, libros: List<Libro>
    ): List<Reserva> {

        val u1 = usuarios.first { it.id == 1L }
        val u2 = usuarios.first { it.id == 2L }
        val u3 = usuarios.first { it.id == 3L }
        val u4 = usuarios.first { it.id == 4L }
        val u5 = usuarios.first { it.id == 5L }

        val hoy = LocalDate.now()

        val reservas: List<Reserva> = listOf(

            // -------- u1 presta a u2 --------
            Reserva(libros[0].id, u1, u2, hoy.minusDays(12), hoy.minusDays(2)), // devuelto
            Reserva(libros[1].id, u1, u2, hoy.minusDays(5), hoy.plusDays(2)),   // proximo
            Reserva(libros[2].id, u1, u2, hoy.minusDays(2), hoy.plusDays(10)),  // activo
            Reserva(libros[3].id, u1, u2, hoy.minusDays(20), hoy.minusDays(1)),
            Reserva(libros[4].id, u1, u2, hoy.minusDays(3), hoy.plusDays(1)),
            Reserva(libros[5].id, u1, u2, hoy.minusDays(1), hoy.plusDays(15)),
            Reserva(libros[6].id, u1, u2, hoy.minusDays(9), hoy.minusDays(3)),
            Reserva(libros[7].id, u1, u2, hoy.minusDays(6), hoy.plusDays(3)),

            // -------- u2 presta a u1 --------
            Reserva(libros[11].id, u2, u1, hoy.minusDays(14), hoy.minusDays(5)),
            Reserva(libros[12].id, u2, u1, hoy.minusDays(4), hoy.plusDays(2)),
            Reserva(libros[13].id, u2, u1, hoy.minusDays(3), hoy.plusDays(8)),
            Reserva(libros[14].id, u2, u1, hoy.minusDays(16), hoy.minusDays(1)),
            Reserva(libros[15].id, u2, u1, hoy.minusDays(2), hoy.plusDays(1)),
            Reserva(libros[16].id, u2, u1, hoy.minusDays(1), hoy.plusDays(12)),
            Reserva(libros[17].id, u2, u1, hoy.minusDays(11), hoy.minusDays(4)),
            Reserva(libros[18].id, u2, u1, hoy.minusDays(7), hoy.plusDays(3)),

            // -------- u3 presta a u4 --------
            Reserva(libros[20].id, u3, u4, hoy.minusDays(12), hoy.minusDays(4)),
            Reserva(libros[21].id, u3, u4, hoy.minusDays(5), hoy.plusDays(2)),
            Reserva(libros[22].id, u3, u4, hoy.minusDays(2), hoy.plusDays(10)),
            Reserva(libros[23].id, u3, u4, hoy.minusDays(20), hoy.minusDays(5)),
            Reserva(libros[24].id, u3, u4, hoy.minusDays(3), hoy.plusDays(1)),
            Reserva(libros[25].id, u3, u4, hoy.minusDays(1), hoy.plusDays(15)),
            Reserva(libros[26].id, u3, u4, hoy.minusDays(9), hoy.minusDays(3)),
            Reserva(libros[27].id, u3, u4, hoy.minusDays(6), hoy.plusDays(3)),

            // -------- u3 presta a u5 --------
            Reserva(libros[28].id, u3, u5, hoy.minusDays(11), hoy.minusDays(4)),
            Reserva(libros[29].id, u3, u5, hoy.minusDays(1), hoy.plusDays(2)),
            Reserva(libros[20].id, u3, u5, hoy.minusDays(3), hoy.plusDays(8)),
            Reserva(libros[21].id, u3, u5, hoy.minusDays(16), hoy.minusDays(6)),
            Reserva(libros[22].id, u3, u5, hoy.minusDays(10), hoy.minusDays(8)),
            Reserva(libros[23].id, u3, u5, hoy.minusDays(1), hoy.plusDays(12)),
            Reserva(libros[24].id, u3, u5, hoy.minusDays(11), hoy.minusDays(4)),
            Reserva(libros[25].id, u3, u5, hoy.minusDays(7), hoy.minusDays(3)),

            // -------- u4 presta a u3 --------
            Reserva(libros[30].id, u4, u3, hoy.minusDays(14), hoy.minusDays(5)),
            Reserva(libros[31].id, u4, u3, hoy.minusDays(4), hoy.plusDays(2)),
            Reserva(libros[32].id, u4, u3, hoy.minusDays(3), hoy.plusDays(8)),
            Reserva(libros[33].id, u4, u3, hoy.minusDays(16), hoy.minusDays(3)),
            Reserva(libros[34].id, u4, u3, hoy.minusDays(2), hoy.plusDays(1)),
            Reserva(libros[35].id, u4, u3, hoy.minusDays(2), hoy.plusDays(12)),
            Reserva(libros[36].id, u4, u3, hoy.minusDays(11), hoy.minusDays(4)),
            Reserva(libros[37].id, u4, u3, hoy.minusDays(7), hoy.plusDays(3)),

            // -------- u4 presta a u5 --------
            Reserva(libros[38].id, u4, u5, hoy.minusDays(13), hoy.minusDays(3)),
            Reserva(libros[39].id, u4, u5, hoy.minusDays(5), hoy.plusDays(2)),
            Reserva(libros[30].id, u4, u5, hoy.minusDays(2), hoy.plusDays(9)),
            Reserva(libros[31].id, u4, u5, hoy.minusDays(17), hoy.minusDays(6)),
            Reserva(libros[32].id, u4, u5, hoy.minusDays(5), hoy.minusDays(3)),
            Reserva(libros[33].id, u4, u5, hoy.minusDays(1), hoy.plusDays(14)),
            Reserva(libros[34].id, u4, u5, hoy.minusDays(9), hoy.minusDays(4)),
            Reserva(libros[35].id, u4, u5, hoy.minusDays(3), hoy.plusDays(5)),

            // -------- u5 presta a u3 --------
            Reserva(libros[40].id, u5, u3, hoy.minusDays(13), hoy.minusDays(4)),
            Reserva(libros[41].id, u5, u3, hoy.minusDays(5), hoy.plusDays(3)),
            Reserva(libros[42].id, u5, u3, hoy.minusDays(2), hoy.plusDays(7)),
            Reserva(libros[43].id, u5, u3, hoy.minusDays(17), hoy.minusDays(2)),
            Reserva(libros[44].id, u5, u3, hoy.minusDays(3), hoy.plusDays(2)),
            Reserva(libros[45].id, u5, u3, hoy.minusDays(1), hoy.plusDays(11)),
            Reserva(libros[46].id, u5, u3, hoy.minusDays(10), hoy.minusDays(5)),
            Reserva(libros[47].id, u5, u3, hoy.minusDays(6), hoy.plusDays(4)),

            // -------- u5 presta a u4 --------
            Reserva(libros[48].id, u5, u4, hoy.minusDays(12), hoy.minusDays(3)),
            Reserva(libros[49].id, u5, u4, hoy.minusDays(4), hoy.plusDays(2)),
            Reserva(libros[40].id, u5, u4, hoy.minusDays(2), hoy.plusDays(8)),
            Reserva(libros[41].id, u5, u4, hoy.minusDays(18), hoy.minusDays(7)),
            Reserva(libros[42].id, u5, u4, hoy.minusDays(10), hoy.plusDays(5)),
            Reserva(libros[43].id, u5, u4, hoy.minusDays(1), hoy.plusDays(13)),
            Reserva(libros[44].id, u5, u4, hoy.minusDays(9), hoy.minusDays(4)),
            Reserva(libros[45].id, u5, u4, hoy.minusDays(7), hoy.minusDays(3)),

            // Reservas extra para probar paginado sin lógica especial en bootstrap.
            Reserva(libros[0].id, u1, u3, hoy.minusDays(32), hoy.minusDays(22)),
            Reserva(libros[0].id, u1, u4, hoy.minusDays(21), hoy.minusDays(18)),
            Reserva(libros[0].id, u1, u5, hoy.minusDays(17), hoy.minusDays(14)),
            Reserva(libros[0].id, u1, u2, hoy.minusDays(1), hoy.plusDays(5)),
        )

        (reservaRepository.saveAll(reservas))

        // Agregar fechas de reservas a los libros
        reservas.forEach { reserva ->
            libros.find { it.id == reserva.libroId }?.agregarFechaReserva(reserva.fechaRecogida, reserva.fechaDevolucion)
        }

        // Guardar libros actualizados con fechas de reservas
        libroRepository.saveAll(libros)

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
            "Volvería a leerlo",
            "Comentario excesivamente largo: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent at sollicitudin justo, sed tempor mi. Maecenas vehicula fringilla suscipit. Nulla dapibus nam."
        )
        val puntuaciones = listOf(2.5, 3.0, 3.5, 4.0, 4.5, 5.0)
        val calificaciones = reservas
            .filter { it.estado(hoy) == EstadoReserva.DEVUELTO }
            .map { reserva ->
                val usuario = reserva.usuarioLector

                // hashCode() para convertir el String de Mongo en un número
                val libroHash = Math.abs(reserva.libroId.hashCode().toLong())
                val seed = libroHash + (usuario.id ?: 0L)

                val posicion = (seed % puntuaciones.size).toInt()

                Calificacion(
                    usuario = usuario,
                    libro = reserva.libroId,
                    puntuacion = puntuaciones[posicion],
                    comentario = comentarios[posicion % comentarios.size],
                    fecha = reserva.fechaDevolucion
                )
            }
        val calificacionesUnicas = calificaciones
            .sortedBy { it.fecha }
            .associateBy { it.libro to it.usuario.id }
            .values
            .toList()
        calificacionRepository.saveAll(calificacionesUnicas)
    }

    private fun actualizarCalificacionesLibros() {

        val libros = libroRepository.findAll()

        libros.forEach { libro ->

            val promedio = calificacionRepository
                .obtenerPromedioCalificaciones(libro.id)

            val promedioFinal = promedio ?: 0.0

            libro.actualizarCalificacion(promedioFinal)
        }

        libroRepository.saveAll(libros)
    }
}