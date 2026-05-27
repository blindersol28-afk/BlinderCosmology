package com.blindercosmology.astro

import com.blindercosmology.astro.AstroMath.atan2D
import com.blindercosmology.astro.AstroMath.cosD
import com.blindercosmology.astro.AstroMath.norm360
import com.blindercosmology.astro.AstroMath.sinD
import com.blindercosmology.astro.AstroMath.toRad
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.tan

/**
 * Ascendant, Midheaven, and Placidus house cusps.
 * Inputs: local sidereal time (deg) and geographic latitude (deg, +N).
 */
object Houses {
    data class HouseChart(
        val ascendant: Double,
        val midheaven: Double,
        val cusps: List<Double>, // size 12, cusp index 0 = house 1 (Ascendant)
    )

    fun compute(lstDeg: Double, latitudeDeg: Double, obliquityDeg: Double): HouseChart {
        val ramc = norm360(lstDeg)
        val mc = midheaven(ramc, obliquityDeg)
        val asc = ascendant(ramc, latitudeDeg, obliquityDeg)
        val cusps = MutableList(12) { 0.0 }
        cusps[0] = asc                                 // house 1
        cusps[9] = mc                                  // house 10
        cusps[3] = norm360(mc + 180.0)                 // house 4
        cusps[6] = norm360(asc + 180.0)                // house 7
        // Placidus intermediates
        cusps[10] = placidus(ramc, latitudeDeg, obliquityDeg, 11)
        cusps[11] = placidus(ramc, latitudeDeg, obliquityDeg, 12)
        cusps[1]  = placidus(ramc, latitudeDeg, obliquityDeg, 2)
        cusps[2]  = placidus(ramc, latitudeDeg, obliquityDeg, 3)
        // Opposites
        cusps[4] = norm360(cusps[10] + 180.0)  // house 5 opposite 11
        cusps[5] = norm360(cusps[11] + 180.0)  // house 6 opposite 12
        cusps[7] = norm360(cusps[1] + 180.0)   // house 8 opposite 2
        cusps[8] = norm360(cusps[2] + 180.0)   // house 9 opposite 3
        return HouseChart(asc, mc, cusps)
    }

    fun midheaven(ramcDeg: Double, eDeg: Double): Double {
        return norm360(atan2D(sinD(ramcDeg), cosD(ramcDeg) * cosD(eDeg)))
    }

    fun ascendant(ramcDeg: Double, phiDeg: Double, eDeg: Double): Double {
        val ra = ramcDeg
        // Standard formula. Quadrant fix: ASC must be ~90° east of MC, i.e., RA(asc) ≈ RAMC + 90.
        val y = -cosD(ra)
        val x = sinD(ra) * cosD(eDeg) + tan(phiDeg.toRad()) * sinD(eDeg)
        var asc = atan2D(y, x)
        asc = norm360(asc)
        val mc = midheaven(ra, eDeg)
        // Ensure ASC is forward of MC (in zodiacal direction).
        val diff = norm360(asc - mc)
        if (diff < 0.0 || diff > 270.0) asc = norm360(asc + 180.0)
        return asc
    }

    /**
     * Placidus intermediate cusp (house 11, 12, 2, or 3). Iterative semi-arc method.
     * Falls back to equal house if latitude > 66.5° (Placidus undefined near poles).
     */
    fun placidus(ramcDeg: Double, phiDeg: Double, eDeg: Double, house: Int): Double {
        if (abs(phiDeg) > 66.0) {
            // Equal house fallback
            val asc = ascendant(ramcDeg, phiDeg, eDeg)
            val offset = when (house) {
                11 -> 300.0; 12 -> 330.0; 2 -> 30.0; 3 -> 60.0; else -> 0.0
            }
            return norm360(asc + offset)
        }
        val (f, baseOffset, lowerHemi) = when (house) {
            11 -> Triple(1.0 / 3.0, 30.0, false)
            12 -> Triple(2.0 / 3.0, 60.0, false)
            2  -> Triple(2.0 / 3.0, 120.0, true)
            3  -> Triple(1.0 / 3.0, 150.0, true)
            else -> error("Placidus only for intermediate cusps")
        }
        // Initial RA guess
        var ra = norm360(ramcDeg + baseOffset)
        for (it in 0 until 25) {
            val sinDec = sinD(ra) * sinD(eDeg)
            val dec = asin(sinDec.coerceIn(-1.0, 1.0)) * AstroMath.RAD_TO_DEG
            val cosArg = -tan(phiDeg.toRad()) * tan(dec.toRad())
            if (cosArg <= -1.0 || cosArg >= 1.0) {
                // semi-arc undefined: fall back to equal house
                val asc = ascendant(ramcDeg, phiDeg, eDeg)
                val offset = when (house) {
                    11 -> 300.0; 12 -> 330.0; 2 -> 30.0; 3 -> 60.0; else -> 0.0
                }
                return norm360(asc + offset)
            }
            val semiArc = acos(cosArg) * AstroMath.RAD_TO_DEG  // 0..180
            val H = f * semiArc
            val raNew = if (!lowerHemi) norm360(ramcDeg + H)
                        else norm360(ramcDeg + 180.0 + (180.0 - H))
            if (abs(norm360(raNew - ra + 180.0) - 180.0) < 1e-6) {
                ra = raNew; break
            }
            ra = raNew
        }
        // Ecliptic longitude from RA, with proper quadrant
        var lon = atan2D(sinD(ra), cosD(ra) * cosD(eDeg))
        lon = norm360(lon)
        // Ensure cusp is in the correct half (forward of MC by the expected ~30° each)
        val mc = midheaven(ramcDeg, eDeg)
        val expectedOffset = when (house) {
            11 -> 30.0; 12 -> 60.0; 2 -> 120.0; 3 -> 150.0; else -> 0.0
        }
        val expected = norm360(mc + expectedOffset)
        if (AstroMath.angularSeparation(lon, expected) > 90.0) {
            lon = norm360(lon + 180.0)
        }
        return lon
    }

    /**
     * Vertex — the western intersection of the prime vertical with the ecliptic.
     * Astrologically: the point of "fated" encounters.
     *
     * Formula: tan(λ_V) = −cos(RAMC) / (sin(RAMC)·cos(ε) − cot(φ)·sin(ε)),
     * then add 180° because we want the WEST intersection (the raw atan2 gives the
     * Anti-Vertex in the east). cot(φ) is equivalent to tan(90°−φ).
     *
     * Near the equator (|φ| ≲ 2°) the Vertex becomes geometrically unstable; the
     * formula still returns a value but should be treated as low-confidence.
     */
    fun vertex(ramcDeg: Double, phiDeg: Double, eDeg: Double): Double {
        val ra = ramcDeg
        val cotPhi = 1.0 / tan(phiDeg.toRad())
        val y = -cosD(ra)
        val x = sinD(ra) * cosD(eDeg) - cotPhi * sinD(eDeg)
        val antiVertex = norm360(atan2D(y, x))
        return norm360(antiVertex + 180.0)
    }

    /** Returns 1..12 for the house containing the given ecliptic longitude. */
    fun houseOf(longitude: Double, cusps: List<Double>): Int {
        val lon = norm360(longitude)
        for (i in 0 until 12) {
            val a = cusps[i]
            val b = cusps[(i + 1) % 12]
            val span = norm360(b - a)
            val into = norm360(lon - a)
            if (into < span || span == 0.0) return i + 1
        }
        return 1
    }
}
