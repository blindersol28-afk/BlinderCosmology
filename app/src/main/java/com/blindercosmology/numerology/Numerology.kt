package com.blindercosmology.numerology

object Numerology {
    private val letterValues: Map<Char, Int> = buildMap {
        val rows = listOf(
            "AJS" to 1, "BKT" to 2, "CLU" to 3,
            "DMV" to 4, "ENW" to 5, "FOX" to 6,
            "GPY" to 7, "HQZ" to 8, "IR" to 9,
        )
        for ((letters, v) in rows) for (c in letters) put(c, v)
    }
    private val vowels = setOf('A', 'E', 'I', 'O', 'U')

    /** Reduce a number to a single digit, preserving master numbers 11/22/33. */
    fun reduce(n: Int): Int {
        var x = kotlin.math.abs(n)
        while (x > 9 && x != 11 && x != 22 && x != 33) {
            var s = 0
            var y = x
            while (y > 0) { s += y % 10; y /= 10 }
            x = s
        }
        return x
    }

    fun lifePath(year: Int, month: Int, day: Int): Int {
        val sum = digitsSum(year) + digitsSum(month) + digitsSum(day)
        return reduce(sum)
    }

    private fun digitsSum(n: Int): Int {
        var s = 0; var x = n
        while (x > 0) { s += x % 10; x /= 10 }
        return s
    }

    fun expression(fullName: String): Int {
        val sum = fullName.uppercase().filter { it in letterValues }.sumOf { letterValues[it]!! }
        return reduce(sum)
    }

    fun soulUrge(fullName: String): Int {
        val sum = fullName.uppercase()
            .filter { it in letterValues && it in vowels }
            .sumOf { letterValues[it]!! }
        return reduce(sum)
    }

    fun personality(fullName: String): Int {
        val sum = fullName.uppercase()
            .filter { it in letterValues && it !in vowels }
            .sumOf { letterValues[it]!! }
        return reduce(sum)
    }

    /** Personal year for a given calendar year. */
    fun personalYear(birthMonth: Int, birthDay: Int, calendarYear: Int): Int {
        return reduce(digitsSum(birthMonth) + digitsSum(birthDay) + digitsSum(calendarYear))
    }

    val lifePathMeaning: Map<Int, String> = mapOf(
        1  to "The Pioneer — born to lead, independent, original. Your road asks you to forge your own path even when nobody is walking beside you.",
        2  to "The Diplomat — sensitive, cooperative, intuitive. Your gift is bringing opposites into rapport; your shadow is over-accommodation.",
        3  to "The Creator — expressive, charismatic, lively. You're here to communicate beauty and to refuse the grey of conformity.",
        4  to "The Builder — disciplined, loyal, foundational. Your destiny is constructed brick by brick, not handed down. You make things that last.",
        5  to "The Explorer — restless, magnetic, freedom-driven. Routine will rot you; movement, change, and risk will feed you.",
        6  to "The Caretaker — responsible, nurturing, devoted. You hold space for others — beware of carrying their weight as your own.",
        7  to "The Mystic — analytical, introspective, truth-seeking. You're here to look behind the curtain, not to applaud the show.",
        8  to "The Sovereign — ambitious, authoritative, resource-savvy. Power and money are tools for your higher work; learn to wield them cleanly.",
        9  to "The Humanitarian — compassionate, artistic, idealistic. You arrived old. Your work is to give without losing yourself in the giving.",
        11 to "Master Visionary — heightened intuition, called to inspire. The wiring is sensitive; the mission is to illuminate.",
        22 to "Master Builder — the architect of large dreams. You can ground the visionary's spark into structures that outlast you.",
        33 to "Master Teacher — selfless service through wisdom. Rare path; you teach by embodying.",
    )
}
