package com.jackemate.appberdi.entities

import com.google.firebase.firestore.DocumentId
import com.jackemate.appberdi.domain.entities.Link
import com.jackemate.appberdi.domain.shared.logger
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

data class Attraction(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val links: List<Link> = emptyList(),
    val site: String? = null,
    val coverUrl: String? = null,
    val horarios: BusinessHours? = null
) {
    private val log by logger()

    fun getParsedHorarios(): List<Horario> {
        val h = horarios ?: return emptyList()

        return h.values().flatMapIndexed { index: Int, s: String? ->
            val str = s ?: return@flatMapIndexed emptyList()
            val day = DayOfWeek.of(index + 1)
            parseHours(day, str)
        }
    }

    private fun parseHours(day: DayOfWeek, line: String): List<Horario> {
        return line
            .split(",").map { hour ->
                val (open, close) = hour.split("-")
                Horario(
                    day,
                    LocalTime.parse(open),
                    LocalTime.parse(close.replace("00:00", "23:59"))
                )
            }
    }

    fun getProxHorario(): Horario? {
        val today = LocalDateTime.now()
        val todayTime = today.toLocalTime()
        val offset = today.dayOfWeek.value

        // Si hoy es jueves, queremos empezar a buscar a partir de ese día hasta el miércoles
        val days = DayOfWeek.values().toMutableList()
        Collections.rotate(days, -offset + 1)
        log.info("days: $days")

        val horarios = getParsedHorarios()
        log.info("horarios: $horarios")

        days.forEach { dayOfWeek ->
            val result = horarios.firstOrNull {
                // Si es hoy, tiene que ser antes de que cierre, sino, el primer dia que abra
                (today.dayOfWeek == it.day && todayTime < it.close) || dayOfWeek == it.day
            }
            if (result != null) {
                return result
            }
        }
        // No debería pasar a menos que la lista sea vacía
        return null
    }
}

data class BusinessHours(
    val lun: String? = null,
    val mar: String? = null,
    val mie: String? = null,
    val jue: String? = null,
    val vie: String? = null,
    val sab: String? = null,
    val dom: String? = null,
) {
    // Importante: el orden de la lista debe coincidir con el enum de DayOfWeek
    // https://docs.oracle.com/javase/8/docs/api/java/time/DayOfWeek.html
    fun values() = listOf(lun, mar, mie, jue, vie, sab, dom)

    override fun toString(): String {
        val result = StringBuilder()
        lun?.let { result.append("lun: $lun\n") }
        mar?.let { result.append("mar: $mar\n") }
        mie?.let { result.append("mie: $mie\n") }
        jue?.let { result.append("jue: $jue\n") }
        vie?.let { result.append("vie: $vie\n") }
        sab?.let { result.append("sab: $sab\n") }
        dom?.let { result.append("dom: $dom\n") }
        return result.toString()
    }
}

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