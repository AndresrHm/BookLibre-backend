package ar.edu.unsam.phm.dto

data class LibroClickLogDTO(
    val libroId: String,
    val libroTitulo: String,
    val usuarioId: Long,
    val clickTimestamp: String,
)

data class LibroMasClickeadoDTO(
    val titulo: String,
    val clicks: Long,
)

data class ResumenClickLibroPorUsuarioDTO(
    val libroMasClickado: LibroMasClickeadoDTO,
    val totalClicks: Long,
    val promedioClicksDiarios: Double,
)


