package ar.edu.unsam.phm.errors

object ErrorStrings {
	// Usuario
	const val USUARIO_TIENE_LIBROS_PRESTADOS = "El usuario tiene libros prestados."
	const val USUARIO_TIENE_DEVOLUCIONES_PENDIENTES = "El usuario tiene devoluciones pendientes."
	const val USUARIO_LOGUEADO_NO_EXISTE = "El usuario logueado no existe"
	const val USUARIO_O_CONTRASENIA_INCORRECTOS = "Usuario o contraseña incorrectos"
	const val USUARIO_NO_PUEDE_CALIFICAR_LIBRO = "El usuario logueado no puede calificar este libro"
	const val USUARIO_NO_PUEDE_RESERVAR_LIBRO_PROPIO = "Un usuario no puede reservar un libro que le pertenece"
	const val USUARIO_ID_REQUERIDO = "El id del usuario es obligatorio"
	const val USUARIO_DEBE_TENER_NOMBRE = "El usuario debe tener nombre"
	const val USUARIO_DEBE_TENER_APELLIDO = "El usuario debe tener apellido"
	const val USUARIO_DEBE_TENER_DESCRIPCION = "El usuario debe tener descripción"
	const val USUARIO_DEBE_TENER_CIUDAD = "El usuario debe tener ciudad"
	const val USUARIO_DEBE_TENER_MAIL = "El usuario debe tener mail"
	const val USUARIO_DEBE_TENER_CELULAR = "El usuario debe tener celular"
	const val USUARIO_DEBE_TENER_TIPO_USUARIO = "El usuario debe tener tipo de usuario"
	const val USUARIO_DEBE_TENER_CONTRASNIA = "El usuario debe tener contraseña"
	const val USUARIO_DEBE_TENER_URL_IMAGEN = "El usuario debe tener URL de imagen"

	// Libro
	const val LIBRO_ID_REQUERIDO = "El libro debe tener id"
	const val LIBRO_DEBE_TENER_IMAGEN = "El libro debe tener una imagen"
	const val LIBRO_DEBE_TENER_TITULO = "El libro debe tener un título"
	const val LIBRO_DEBE_TENER_TIPO = "El libro debe tener un tipo"
	const val LIBRO_DEBE_TENER_GENERO = "El libro debe tener un género"
	const val LIBRO_DEBE_TENER_DESCRIPCION = "El libro debe tener una descripción"
	const val LIBRO_DEBE_TENER_AUTOR = "El libro debe tener un autor"
	const val LIBRO_DEBE_TENER_IDIOMA = "El libro debe tener un idioma"
	const val LIBRO_DEBE_TENER_ESTADO = "El libro debe tener un estado"
	const val LIBRO_DEBE_TENER_EDITORIAL = "El libro debe ser de una editorial"
	const val LIBRO_DEBE_TENER_FECHA_PUBLICACION = "El libro debe tener fecha de publicación"
	const val LIBRO_CANTIDAD_PAGINAS_POSITIVA = "La cantidad de páginas del libro debe ser positiva"
	const val LIBRO_DEBE_TENER_ID_PROPIETARIO = "El libro debe tener id de propietario"
	const val LIBRO_NO_PERTENECE_USUARIO_PRESTADOR = "El libro no pertenece al usuario prestador"
	const val SOLO_LECTOR_PUEDE_RESERVAR = "Solo un Lector puede reservar libros."
	const val SOLO_PUBLICADOR_PUEDE_CREAR_LIBROS = "Solo un Publicador puede crear libros."
	const val SOLO_LECTOR_PUEDE_CALIFICAR = "Solo un Lector puede calificar libros."
	const val PODRA_CALIFICAR_AL_FINALIZAR_RESERVA = "Podrá calificar el libro cuando termine la reserva"

	// Reserva
	const val FECHA_DEVOLUCION_ANTERIOR_A_RECOGIDA = "La fecha de devolución no puede ser anterior a la fecha de recogida"
	const val FECHA_RESERVA_ANTERIOR_A_HOY = "No se puede realizar una reserva para una fecha anterior a la actual"
	const val FECHA_DEVOLUCION_POSTERIOR_RECOGIDA = "fechaDevolucion debe ser posterior a fechaRecogida"
	const val FECHA_DESDE_MAYOR_A_FECHA_HASTA = "fechaDesde no puede ser mayor a fechaHasta"
	const val EXISTE_RESERVA_EN_RANGO = "Existe una reserva en el rango de fechas estipulado"

	// Calificación
	const val COMENTARIO_MAX_500 = "comentario no debe superar los 500 caracteres"
	const val PUNTUACION_ENTRE_05_Y_50 = "puntuacion debe estar entre 0.5 y 5.0"

	// Otros
	const val LIBRO_PRESTADO_NO_SE_PUEDE_BORRAR = "El libro se encuentra prestado"
	const val ISBN13_DEBE_TENER_13_DIGITOS = "El código ISBN-13 debe contener 13 dígitos"
	const val DESCRIPCION_NO_PUEDE_SUPERAR_500 = "La descripción no puede superar los 500 caracteres"
	const val LIBRO_NO_PUEDE_SUPERAR_1499_PAGINAS = "El libro no puede superar las 1499 páginas."
	const val PAGE_NO_PUEDE_SER_NEGATIVO = "page no puede ser negativo"
	const val PAGINAS_MIN_MAYOR_A_PAGINAS_MAX = "paginasMin no puede ser mayor a paginasMax"
	const val SIZE_DEBE_SER_MAYOR_A_CERO = "size debe ser mayor a 0"
	const val SOLO_SE_PUEDE_SUMAR_KARMA_POSITIVO = "Solo se puede sumar karma positivo"
	const val FLAG_DIFF_INVALIDO = "No se reconoce el origen de la busqueda"

	// Auth
	const val CREDENCIALES_INVALIDAS = "Las credenciales ingresadas son inválidas."
	const val TOKEN_EXPIRADO = "El token ha expirado."
	const val REFRESH_TOKEN_INVALIDO_O_EXPIRADO = "El refresh token es inválido o ha expirado."
	const val USUARIO_NO_ENCONTRADO = "Usuario no encontrado."

	fun usuarioNoEncontradoPorId(id: Long) = "No se encontró usuario con id $id"
	fun libroNoEncontradoPorId(id: String) = "No existe un libro con el id $id"
	fun usuarioNoExistePorId(id: Long) = "No existe un usuario con el id $id."
	fun libroNoExistePorId(id: String) = "No existe libro con el id $id"
	fun reservaNoExistePorId(id: Long) = "No existe la reserva con id $id"
	fun usuarioNoEncontradoParaCalificacion(id: Long) = "No se ha encontrado el usuario con id $id"
	fun libroNoPerteneceAlUsuario(libroId: String, idUsuario: Long) = "El libro con id $libroId no pertenece al usuario $idUsuario"
}

