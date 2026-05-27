package com.blindercosmology.astro

import com.blindercosmology.astro.AstroMath.cosD
import com.blindercosmology.astro.AstroMath.norm360
import com.blindercosmology.astro.AstroMath.sinD

object Sun {
    /**
     * Apparent geocentric ecliptic longitude of the Sun in degrees.
     * Meeus ch. 25, sufficient for ~0.01° precision.
     */
    fun longitude(jd: Double): Double {
        val t = JulianDay.centuriesT(jd)
        val l0 = norm360(280.46646 + 36000.76983 * t + 0.0003032 * t * t)
        val m = norm360(357.52911 + 35999.05029 * t - 0.0001537 * t * t)
        val c = (1.914602 - 0.004817 * t - 0.000014 * t * t) * sinD(m) +
            (0.019993 - 0.000101 * t) * sinD(2 * m) +
            0.000289 * sinD(3 * m)
        val trueLon = l0 + c
        // Apparent (corrected for nutation/aberration approximation)
        val omega = 125.04 - 1934.136 * t
        return norm360(trueLon - 0.00569 - 0.00478 * sinD(omega))
    }

    fun latitude(@Suppress("UNUSED_PARAMETER") jd: Double): Double = 0.0
}
