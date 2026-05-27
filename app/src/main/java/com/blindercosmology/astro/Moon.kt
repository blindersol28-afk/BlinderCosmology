package com.blindercosmology.astro

import com.blindercosmology.astro.AstroMath.norm360
import com.blindercosmology.astro.AstroMath.sinD

object Moon {
    /**
     * Geocentric ecliptic longitude of the Moon in degrees.
     * Truncated ELP-2000 / Meeus ch. 47 — top ~15 periodic terms.
     * Accurate to ≈0.1° which is well within an astrological orb.
     */
    fun longitude(jd: Double): Double {
        val t = JulianDay.centuriesT(jd)
        val lp = norm360(218.3164477 + 481267.88123421 * t -
            0.0015786 * t * t + t * t * t / 538841.0 - t * t * t * t / 65194000.0)
        val d  = norm360(297.8501921 + 445267.1114034 * t -
            0.0018819 * t * t + t * t * t / 545868.0 - t * t * t * t / 113065000.0)
        val m  = norm360(357.5291092 + 35999.0502909 * t -
            0.0001536 * t * t + t * t * t / 24490000.0)
        val mp = norm360(134.9633964 + 477198.8675055 * t +
            0.0087414 * t * t + t * t * t / 69699.0 - t * t * t * t / 14712000.0)
        val f  = norm360(93.272095 + 483202.0175233 * t -
            0.0036539 * t * t - t * t * t / 3526000.0 + t * t * t * t / 863310000.0)

        // Periodic terms — coefficient in degrees × 1e-6, multiplied below.
        // (D, M, M', F, sigma_l in micro-degrees)
        val terms = listOf(
            doubleArrayOf(0.0, 0.0,  1.0, 0.0,  6288774.0),
            doubleArrayOf(2.0, 0.0, -1.0, 0.0,  1274027.0),
            doubleArrayOf(2.0, 0.0,  0.0, 0.0,   658314.0),
            doubleArrayOf(0.0, 0.0,  2.0, 0.0,   213618.0),
            doubleArrayOf(0.0, 1.0,  0.0, 0.0,  -185116.0),
            doubleArrayOf(0.0, 0.0,  0.0, 2.0,  -114332.0),
            doubleArrayOf(2.0, 0.0, -2.0, 0.0,    58793.0),
            doubleArrayOf(2.0,-1.0, -1.0, 0.0,    57066.0),
            doubleArrayOf(2.0, 0.0,  1.0, 0.0,    53322.0),
            doubleArrayOf(2.0,-1.0,  0.0, 0.0,    45758.0),
            doubleArrayOf(0.0, 1.0, -1.0, 0.0,   -40923.0),
            doubleArrayOf(1.0, 0.0,  0.0, 0.0,   -34720.0),
            doubleArrayOf(0.0, 1.0,  1.0, 0.0,   -30383.0),
            doubleArrayOf(2.0, 0.0,  0.0,-2.0,    15327.0),
            doubleArrayOf(0.0, 0.0,  1.0, 2.0,   -12528.0),
            doubleArrayOf(0.0, 0.0,  1.0,-2.0,    10980.0),
            doubleArrayOf(4.0, 0.0, -1.0, 0.0,    10675.0),
            doubleArrayOf(0.0, 0.0,  3.0, 0.0,    10034.0),
            doubleArrayOf(4.0, 0.0, -2.0, 0.0,     8548.0),
        )
        var sigma = 0.0
        for (term in terms) {
            val arg = term[0] * d + term[1] * m + term[2] * mp + term[3] * f
            sigma += term[4] * sinD(arg)
        }
        return norm360(lp + sigma / 1_000_000.0)
    }
}
