package ar.edu.unsam.phm.utils

import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun daysBetween(fechaInicio: LocalDate, fechaFin: LocalDate): Int {
    return ChronoUnit.DAYS.between(fechaInicio, fechaFin).toInt()
}