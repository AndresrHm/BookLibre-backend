package ar.edu.unsam.phm.domain

fun <T> paginate(
    page: Int,
    size: Int,
    elements: List<T>
): PageResult<T> {

    val total = elements.size

    val content = elements
        .drop(page * size)
        .take(size)

    return PageResult(
        content = content,
        totalElements = total
    )
}

data class PageResult<T>(
    val content: List<T>,
    val totalElements: Int,
)

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int,
)