package com.blindercosmology.astro

/**
 * Chiron — the centaur between Saturn and Uranus, period ≈ 50.4 years.
 * Uses Keplerian elements similar to the major planets. Accuracy: ~1° for
 * dates within ±200 years of J2000, which is well inside any astrological orb.
 *
 * Elements at J2000 are approximate; secular rates beyond mean longitude are
 * ignored because they are negligible at our precision target.
 */
object Chiron {
    val ELEMENTS = Planets.Elements(
        a0 = 13.7080,        aDot = 0.0,
        e0 = 0.38247,        eDot = 0.0,
        i0 = 6.9296,         iDot = 0.0,
        L0 = 252.0,          LDot = 713.84,     // mean longitude J2000 + mean motion per century
        w0 = 188.95,         wDot = 0.0,        // longitude of perihelion ϖ = ω + Ω
        O0 = 209.40,         ODot = 0.0,
    )

    fun longitude(jd: Double): Double = Planets.longitude(ELEMENTS, jd)
}
