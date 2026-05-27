package com.blindercosmology.interpret

import com.blindercosmology.astro.AspectType
import com.blindercosmology.astro.Body
import com.blindercosmology.astro.NatalChart
import com.blindercosmology.astro.Sign
import com.blindercosmology.astro.Transits
import com.blindercosmology.numerology.Numerology
import java.time.LocalDate

data class DailyHoroscope(
    val dateLabel: String,
    val personalDayNumber: Int,
    val moonSign: Sign,
    val moonSignNote: String,
    val highlights: List<String>,
    val advice: String,
)

object DailyHoroscopeGenerator {
    fun forToday(chart: NatalChart, today: LocalDate = LocalDate.now()): DailyHoroscope {
        val trans = Transits.forDate(today)
        val moonSign = Sign.fromLongitude(trans[Body.Moon]!!).first
        val moonNote = transitingMoonNote(moonSign)
        val hits = Transits.toNatal(chart, today)
        val highlights = hits.take(5).map { h ->
            "Transiting ${h.transiting.displayName} ${h.type.displayName.lowercase()} natal ${h.natal.displayName} " +
                "(orb ${"%.1f°".format(h.orb)}) — ${aspectFlavorShort(h.type, h.transiting, h.natal)}"
        }
        val pd = Numerology.personalYear(chart.info.month, chart.info.day, today.year) // personal year as anchor
        val personalDay = Numerology.reduce(pd + sumDigits(today.monthValue) + sumDigits(today.dayOfMonth))

        val advice = composeAdvice(personalDay, hits, moonSign, chart)

        return DailyHoroscope(
            dateLabel = today.toString(),
            personalDayNumber = personalDay,
            moonSign = moonSign,
            moonSignNote = moonNote,
            highlights = highlights,
            advice = advice,
        )
    }

    private fun sumDigits(n: Int): Int {
        var s = 0; var x = n
        while (x > 0) { s += x % 10; x /= 10 }
        return s
    }

    private fun transitingMoonNote(s: Sign): String = when (s) {
        Sign.Aries -> "Mood is short-fused and action-ready. Don't pick fights you don't want to finish."
        Sign.Taurus -> "Slow down, eat well, touch grass. Don't force the day."
        Sign.Gemini -> "Lots of chatter — good for calls, messages, errands. Bad for deep focus."
        Sign.Cancer -> "Sensitive day. Home, family, food, and rest restore you."
        Sign.Leo -> "Visibility and play work in your favour. Express, perform, lead."
        Sign.Virgo -> "A precise day — handle details, organise, clean up loose ends."
        Sign.Libra -> "Relational and aesthetic — soften, partner, arrange beauty."
        Sign.Scorpio -> "Intensity rises. Good for deep work, not for casual conversation."
        Sign.Sagittarius -> "Optimism, adventure, learning. A good day to expand your map."
        Sign.Capricorn -> "Get serious; ship something. Authority works in your favour today."
        Sign.Aquarius -> "Sideways thinking and good friend energy. Unusual angles work."
        Sign.Pisces -> "Dreamy, intuitive, slightly unfocused. Art yes, contracts no."
    }

    private fun aspectFlavorShort(t: AspectType, a: Body, b: Body): String = when (t) {
        AspectType.Conjunction -> "fresh start in the ${b.displayName}/${a.displayName} theme"
        AspectType.Opposition  -> "a pull — something is asking for balance"
        AspectType.Trine       -> "a green light; use it"
        AspectType.Square      -> "friction worth pressing through"
        AspectType.Sextile     -> "an opportunity if you reach for it"
        AspectType.Quincunx    -> "an awkward note — adjust, don't force"
    }

    private fun composeAdvice(
        personalDay: Int,
        hits: List<com.blindercosmology.astro.TransitHit>,
        moonSign: Sign,
        chart: NatalChart,
    ): String = buildString {
        append("Personal Day $personalDay — ")
        append(when (personalDay) {
            1  -> "begin something. Today's seed is real."
            2  -> "cooperate, listen, partner. Don't push solo."
            3  -> "express, socialise, create. Light energy."
            4  -> "build, organise, work the foundation."
            5  -> "expect change; flexibility wins."
            6  -> "tend home, family, responsibilities — and yourself."
            7  -> "withdraw, reflect, study. Avoid noise."
            8  -> "handle business — power day for ambition and money matters."
            9  -> "let go, complete, release. Endings clear the way."
            11 -> "high-intuition day; trust the signals."
            22 -> "build something that lasts; large vision possible."
            33 -> "show up in service to someone who needs you."
            else -> "stay grounded."
        })
        append("\n\nThe Moon in ${moonSign.displayName} colours the mood: ")
        append(transitingMoonNote(moonSign).lowercase())
        if (hits.isNotEmpty()) {
            append("\n\nThe sharpest contact for your chart: transiting ${hits[0].transiting.displayName} ")
            append("${hits[0].type.displayName.lowercase()} your natal ${hits[0].natal.displayName}. ")
            append("Pay attention to themes around your ")
            val natalPlc = chart.placements.firstOrNull { it.body == hits[0].natal }
            if (natalPlc != null) {
                append("${com.blindercosmology.interpret.Library.houseFocus[natalPlc.house]}.")
            } else append("inner weather.")
        }
    }
}
