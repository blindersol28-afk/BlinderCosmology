package com.blindercosmology.astro

/**
 * Mean Black Moon Lilith — the mean position of the lunar apogee
 * (the farthest point of the Moon's orbit from Earth).
 *
 * Lilith = perigee + 180°. Mean perigee longitude is from Brown's lunar theory.
 * Meeus (Astronomical Algorithms, ch. 47).
 */
object Lilith {
    fun meanLongitude(jd: Double): Double {
        val t = JulianDay.centuriesT(jd)
        val perigee = 83.3532465 +
            4069.0137287 * t -
            0.01032 * t * t -
            t * t * t / 80053.0 +
            t * t * t * t / 18999000.0
        return AstroMath.norm360(perigee + 180.0)
    }
}
