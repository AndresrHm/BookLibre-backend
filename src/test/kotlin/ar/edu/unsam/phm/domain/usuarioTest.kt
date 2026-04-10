package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.BusinessException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class UsuarioSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest
    describe("Test de Usuario") {
        val usuario = crearUsuario()
        it("un usuario puede sumar karma") {
            usuario.biblioKarma.shouldBe(0)

            usuario.sumarKarma(100)

            usuario.biblioKarma.shouldBe(100)
        }

        it("un usuario no puede sumar karma menor a 1") {
            usuario.biblioKarma.shouldBe(0)

            shouldThrow<BusinessException> {
                usuario.sumarKarma(0)
            }
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
    tipoUsuario: TipoUsuario = TipoUsuario.LECTOR,
    urlImagen: String = "imagenUsuario"
): Usuario {
    return Usuario(
        nombre = nombre,
        apellido = apellido,
        descripcion = descripcion,
        email = email,
        celular = celular,
        ciudad = ciudad,
        tipoUsuario = tipoUsuario,
        urlImagen = urlImagen
    )
}