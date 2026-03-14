package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.dto.LibroUsuarioPerfilDTO

fun Libro.toLibroPerfilUsuarioDTO(): LibroUsuarioPerfilDTO = LibroUsuarioPerfilDTO(
    id = this.id,
    titulo = this.titulo,
    portada = this.portada,
    genero = this.genero,
    autor = this.autor,
    fechaAgregado = this.getFechaAsString()
)

