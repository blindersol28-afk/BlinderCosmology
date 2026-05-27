package com.blindercosmology.astro

import com.blindercosmology.astro.AstroMath.atan2D
import com.blindercosmology.astro.AstroMath.cosD
import com.blindercosmology.astro.AstroMath.norm360
import com.blindercosmology.astro.AstroMath.sinD
import com.blindercosmology.astro.AstroMath.toRad
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Planetary positions from heliocentric Keplerian elements.
 * Element set: Standish (JPL) "Keplerian Elements for Approximate Positions of the Major Planets",
 * valid 1800–2050 with reasonable accuracy. Pluto uses Standish's extended set.
 *
 * Each entry: a (AU), e, i, L (mean long), longPerihelion, longAscendingNode — value at J2000
 * plus per-century rate.
 */
object Planets {
    data class Elements(
        val a0: Double, val aDot: Double,
        val e0: Double, val eDot: Double,
        val i0: Double, val iDot: Double,
        val L0: Double, val LDot: Double,
        val w0: Double, val wDot: Double,   // longitude of perihelion ϖ
        val O0: Double, val ODot: Double,   // longitude of ascending node Ω
    )

    // Standish 1800-2050 elements (deg, deg/cy). a/aDot in AU.
    val EARTH = Elements(
        1.00000261,  0.00000562,
        0.01671123, -0.00004392,
       -0.00001531, -0.01294668,
       100.46457166, 35999.37244981,
       102.93768193,   0.32327364,
         0.0,           0.0,
    )
    val MERCURY = Elements(
        0.38709927,  0.00000037,
        0.20563593,  0.00001906,
        7.00497902, -0.00594749,
      252.25032350, 149472.67411175,
       77.45779628,   0.16047689,
       48.33076593,  -0.12534081,
    )
    val VENUS = Elements(
        0.72333566,  0.00000390,
        0.00677672, -0.00004107,
        3.39467605, -0.00078890,
      181.97909950, 58517.81538729,
      131.60246718,   0.00268329,
       76.67984255,  -0.27769418,
    )
    val MARS = Elements(
        1.52371034,  0.00001847,
        0.09339410,  0.00007882,
        1.84969142, -0.00813131,
       -4.55343205, 19140.30268499,
       -23.94362959,  0.44441088,
        49.55953891, -0.29257343,
    )
    val JUPITER = Elements(
        5.20288700, -0.00011607,
        0.04838624, -0.00013253,
        1.30439695, -0.00183714,
        34.39644051, 3034.74612775,
        14.72847983,  0.21252668,
       100.47390909,  0.20469106,
    )
    val SATURN = Elements(
        9.53667594, -0.00125060,
        0.05386179, -0.00050991,
        2.48599187,  0.00193609,
        49.95424423, 1222.49362201,
        92.59887831, -0.41897216,
       113.66242448, -0.28867794,
    )
    val URANUS = Elements(
       19.18916464, -0.00196176,
        0.04725744, -0.00004397,
        0.77263783, -0.00242939,
       313.23810451,  428.48202785,
       170.95427630,   0.40805281,
        74.01692503,   0.04240589,
    )
    val NEPTUNE = Elements(
       30.06992276,  0.00026291,
        0.00859048,  0.00005105,
        1.77004347,  0.00035372,
      -55.12002969,  218.45945325,
        44.96476227, -0.32241464,
       131.78422574, -0.00508664,
    )
    val PLUTO = Elements(
       39.48211675, -0.00031596,
        0.24882730,  0.00005170,
       17.14001206,  0.00004818,
      238.92903833,  145.20780515,
      224.06891629,  -0.04062942,
      110.30393684,  -0.01183482,
    )

    private fun current(el: Elements, t: Double): DoubleArray = doubleArrayOf(
        el.a0 + el.aDot * t,
        el.e0 + el.eDot * t,
        el.i0 + el.iDot * t,
        el.L0 + el.LDot * t,
        el.w0 + el.wDot * t,
        el.O0 + el.ODot * t,
    )

    /** Solve Kepler's equation E - e*sin(E) = M (E, M in degrees). Newton-Raphson. */
    private fun solveKepler(mDeg: Double, e: Double): Double {
        val m = AstroMath.norm360(mDeg).let { if (it > 180) it - 360 else it }
        val mRad = m.toRad()
        var eRad = mRad + e * sin(mRad)
        var dE = 1.0
        var iter = 0
        while (abs(dE) > 1e-9 && iter < 30) {
            dE = (eRad - e * sin(eRad) - mRad) / (1 - e * cos(eRad))
            eRad -= dE
            iter++
        }
        return AstroMath.norm360(eRad * AstroMath.RAD_TO_DEG)
    }

    /** Heliocentric ecliptic rectangular coordinates of orbit (x, y, z) in AU at time t (centuries from J2000). */
    private fun heliocentric(el: Elements, t: Double): DoubleArray {
        val c = current(el, t)
        val a = c[0]; val e = c[1]; val i = c[2]
        val L = c[3]; val w = c[4]; val o = c[5]
        val M = L - w
        val E = solveKepler(M, e)
        // Position in orbital plane
        val xp = a * (cosD(E) - e)
        val yp = a * sqrt(1 - e * e) * sinD(E)
        // Rotate by argument of perihelion (w - O), inclination i, longitude of ascending node O
        val omega = w - o
        val cosOmega = cosD(omega); val sinOmega = sinD(omega)
        val cosO = cosD(o); val sinO = sinD(o)
        val cosI = cosD(i); val sinI = sinD(i)
        val xEcl = (cosOmega * cosO - sinOmega * sinO * cosI) * xp +
            (-sinOmega * cosO - cosOmega * sinO * cosI) * yp
        val yEcl = (cosOmega * sinO + sinOmega * cosO * cosI) * xp +
            (-sinOmega * sinO + cosOmega * cosO * cosI) * yp
        val zEcl = (sinOmega * sinI) * xp + (cosOmega * sinI) * yp
        return doubleArrayOf(xEcl, yEcl, zEcl)
    }

    /** Geocentric ecliptic longitude (deg) of a planet. */
    fun longitude(el: Elements, jd: Double): Double {
        val t = JulianDay.centuriesT(jd)
        val p = heliocentric(el, t)
        val e = heliocentric(EARTH, t)
        val gx = p[0] - e[0]; val gy = p[1] - e[1]
        return AstroMath.norm360(atan2D(gy, gx))
    }

    fun all(jd: Double): Map<Body, Double> = mapOf(
        Body.Mercury to longitude(MERCURY, jd),
        Body.Venus   to longitude(VENUS,   jd),
        Body.Mars    to longitude(MARS,    jd),
        Body.Jupiter to longitude(JUPITER, jd),
        Body.Saturn  to longitude(SATURN,  jd),
        Body.Uranus  to longitude(URANUS,  jd),
        Body.Neptune to longitude(NEPTUNE, jd),
        Body.Pluto   to longitude(PLUTO,   jd),
    )
}

/** Mean lunar node — Meeus eq. 47.7. */
object LunarNode {
    fun meanNorth(jd: Double): Double {
        val t = JulianDay.centuriesT(jd)
        return AstroMath.norm360(
            125.0445479 - 1934.1362891 * t + 0.0020754 * t * t +
                t * t * t / 467441.0 - t * t * t * t / 60616000.0
        )
    }
}
