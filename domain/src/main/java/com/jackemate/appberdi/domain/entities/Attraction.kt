package com.jackemate.appberdi.domain.entities

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

data class Attraction(
    val name: String = "",
    val description: String = "",
    val links: List<Link> = emptyList(),
    val site: String? = null,
    val horarios: BusinessHours? = null
) {
    fun getParsedHorarios(): List<Horario> {
        val h = horarios ?: return emptyList()

        // Importante: el orden de la lista debe coincidir con el enum de DayOfWeek
        // https://docs.oracle.com/javase/8/docs/api/java/time/DayOfWeek.html
        return listOf(h.lun, h.mar, h.mie, h.jue, h.vie, h.sab, h.dom)
            .flatMapIndexed { index: Int, s: String? ->
                val day = DayOfWeek.of(index + 1)
                val h = s ?: return@flatMapIndexed emptyList()
                parseHours(day, h)
            }
    }

    private fun parseHours(day: DayOfWeek, h: String): List<Horario> {
        return h.split(",").map {
            val split = it.split("-")
            val open = LocalTime.parse(split[0])
            val close = LocalTime.parse(split[1])
            Horario(day, open, close)
        }
    }

    fun getProxHorario(): Horario? {
        val today = LocalDateTime.now()
        val time = today.toLocalTime()
        val offset = today.dayOfWeek.value

        // Si hoy es jueves, queremos empezar a buscar por ese día hasta el miércoles
        val days = DayOfWeek.values().toMutableList()
        Collections.rotate(days, -offset + 1)

        val horarios = getParsedHorarios()

        days.forEach { dayOfWeek ->
            val result = horarios.firstOrNull {
                it.day == dayOfWeek && time < it.close
            }
            if (result != null) {
                return result
            }
        }
        // No debería pasar a menos que la lista sea vacía
        return null
    }

}

data class BusinessHours (
    val lun: String? = null,
    val mar: String? = null,
    val mie: String? = null,
    val jue: String? = null,
    val vie: String? = null,
    val sab: String? = null,
    val dom: String? = null,
    val finde: String? = null,
    val habil: String? = null,
)

data class Horario(
    val day: DayOfWeek,
    val open: LocalTime,
    val close: LocalTime
) {
    fun isNowOpen(): Boolean {
        val today = LocalDateTime.now()
        val time = today.toLocalTime()
        val dayOfWeek = today.dayOfWeek

        return day == dayOfWeek && open < time && time < close
    }
}