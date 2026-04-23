package ar.edu.unsam.phm.utils

private const val MAX_SEARCH_LENGTH = 100

fun sanitizarSearch(input: String?): String? {
    val trimmed = input?.trim().orEmpty()
    if (trimmed.isBlank()) return ""

    // Quita tags HTML simples y caracteres peligrosos.
    val sinTags = trimmed.replace(Regex("<[^>]*>"), " ")
    val permitido = sinTags.replace(Regex("[^\\p{L}\\p{N}\\s._-]"), " ")
    val normalizado = permitido.replace(Regex("\\s+"), " ").trim()

    return normalizado.take(MAX_SEARCH_LENGTH).ifBlank { null }
}
