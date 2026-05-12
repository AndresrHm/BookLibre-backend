package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.utils.CriterioFiltrosLibrosBuilder
import ar.edu.unsam.phm.utils.LibroFiltrosMongo
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class LibroFiltrosCriteriaBuilderTest : DescribeSpec({
    describe("LibroFiltrosCriteriaBuilder") {
        it("solo aplica activo=true cuando no hay filtros opcionales") {
            val criteria = CriterioFiltrosLibrosBuilder.build(
                LibroFiltrosMongo(
                    search = null,
                    isbn13 = null,
                    paginasMin = null,
                    paginasMax = null,
                    generos = null,
                    soloIdPropietario = null,
                    excluirIdPropietario = null,
                    excluirIds = emptySet(),
                )
            )

            val json = criteria.criteriaObject.toJson()

            json shouldContain "\"activo\": true"
            json shouldBe "{\"\$and\": [{\"activo\": true}]}"
        }

        it("agrega criterios para filtros combinados") {
            val criteria = CriterioFiltrosLibrosBuilder.build(
                LibroFiltrosMongo(
                    search = " Harry ",
                    isbn13 = 9781234567890,
                    paginasMin = 120,
                    paginasMax = 480,
                    generos = listOf(Genero.CIENCIA_FICCION, Genero.DRAMA),
                    soloIdPropietario = 22,
                    excluirIdPropietario = 9,
                    excluirIds = setOf("L1", "L2"),
                )
            )

            val json = criteria.criteriaObject.toJson()

            json shouldContain "\"titulo\""
            json shouldContain "Harry"
            json shouldContain "\"isbn13\": 9781234567890"
            json shouldContain "\"cantidadPaginas\""
            json shouldContain "\"\$gte\": 120"
            json shouldContain "\"\$lte\": 480"
            json shouldContain "CIENCIA_FICCION"
            json shouldContain "DRAMA"
            json shouldContain "\"idPropietario\": 22"
            json shouldContain "\"\$ne\": 9"
            json shouldContain "\"_id\""
            json shouldContain "L1"
            json shouldContain "L2"
        }

        it("ignora search en blanco, generos vacios y excluirIds vacio") {
            val criteria = CriterioFiltrosLibrosBuilder.build(
                LibroFiltrosMongo(
                    search = "   ",
                    isbn13 = null,
                    paginasMin = null,
                    paginasMax = null,
                    generos = emptyList(),
                    soloIdPropietario = null,
                    excluirIdPropietario = null,
                    excluirIds = emptySet(),
                )
            )

            val json = criteria.criteriaObject.toJson()

            json shouldContain "\"activo\": true"
            json.contains("titulo") shouldBe false
            json.contains("genero") shouldBe false
            json.contains("_id") shouldBe false
        }
    }
})

