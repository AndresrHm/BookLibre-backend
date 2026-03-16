package ar.edu.unsam.phm.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val PATRON_FECHA = "dd/MM/yyyy"
val formateadorFecha: DateTimeFormatter = DateTimeFormatter.ofPattern(PATRON_FECHA)

fun LocalDate.formatear(): String = formateadorFecha.format(this)

