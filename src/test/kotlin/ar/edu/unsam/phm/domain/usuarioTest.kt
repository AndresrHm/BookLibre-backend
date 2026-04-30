package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.errors.BusinessException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class UsuarioSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    // Inicializamos variables acá (se crean de nuevo por cada 'it')
    val rolLector = crearRol(nameCreacion = "LECTOR")
    val rolPublicador = crearRol(nameCreacion = "PUBLICADOR")
    val usuario = crearUsuario()

    describe("Test de Usuario: Karma") {
        it("un usuario puede sumar karma") {
            usuario.sumarKarma(100)
            usuario.biblioKarma shouldBe 100
        }

        it("un usuario no puede sumar karma menor a 1") {
            shouldThrow<BusinessException> { usuario.sumarKarma(0) }
        }
    }

    describe("Test de Usuario: Roles") {
        it("esLector: true si tiene rol, false si no") {
            // Caso true
            val usuarioLector = crearUsuario(roles = mutableSetOf(rolLector))
            usuarioLector.esLector() shouldBe true

            // Caso false (el usuario base no tiene roles)
            usuario.esLector() shouldBe false
        }

        it("esPublicador: true si tiene rol, false si no") {
            // Caso true
            val usuarioPub = crearUsuario(roles = mutableSetOf(rolPublicador))
            usuarioPub.esPublicador() shouldBe true

            // Caso false
            usuario.esPublicador() shouldBe false
        }

        it("esLectorPublicador: lógica combinada") {
            val ambos = crearUsuario(roles = mutableSetOf(rolLector, rolPublicador))
            val soloLector = crearUsuario(roles = mutableSetOf(rolLector))

            ambos.esLectorPublicador() shouldBe true
            soloLector.esLectorPublicador() shouldBe false
        }
    }

    describe("Test de Usuario: Seguridad y Persistencia") {
        it("encriptar y validar credenciales") {
            val usuarioConPass = crearUsuario(password = "pass123")
            usuarioConPass.encriptarPassword()

            // Camino feliz (match)
            usuarioConPass.validarCredenciales("pass123")

            // Camino fallido (throws)
            shouldThrow<BusinessException> {
                usuarioConPass.validarCredenciales("wrong")
            }
        }
    }

    describe("Test de Usuario: Otros métodos") {
        it("agregar libro a lista de préstamos") {
            val libro = crearLibro()
            usuario.agregarLibroParaPrestar(libro)
            usuario.listaDeLibrosParaPrestar.size shouldBe 1
        }

        it("formatear fecha") {
            usuario.getFechaCreacionAsString().isNotEmpty() shouldBe true
        }
    }
})

fun crearUsuario(
    id: Long = 1,
    nombre: String = "NombreUsuario",
    apellido: String = "ApellidoUsuario",
    descripcion: String = "DescripcionUsuario",
    email: String = "emailusuario@gmail.com",
    celular: String = "1111111111",
    ciudad: String = "CiudadUsuario",
    roles: MutableSet<Rol> = mutableSetOf<Rol>(),
    password: String = "passwordUsuario",
    urlImagen: String = "imagenUsuario"
): Usuario {
    return Usuario(
        nombre = nombre,
        apellido = apellido,
        descripcion = descripcion,
        email = email,
        celular = celular,
        ciudad = ciudad,
        roles = roles,
        password = password,
        urlImagen = urlImagen
    )
}

fun crearRol(
    idCreacion: Long = 1,
    nameCreacion: String = TipoUsuario.LECTOR.roleName
): Rol {
    return Rol().apply {
        id = idCreacion
        name = nameCreacion
    }
}