package com.blindercosmology.astro

import kotlin.math.floor

object JulianDay {
    /**
     * Julian Day from civil date + time in Universal Time.
     * Algorithm from Jean Meeus, Astronomical Algorithms ch. 7.
     */
    fun fromUT(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int = 0): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        val dayFrac = day + (hour + minute / 60.0 + second / 3600.0) / 24.0
        return floor(365.25 * (y + 4716)) +
            floor(30.6001 * (m + 1)) +
            dayFrac + b - 1524.5
    }

    /**
     * Julian Day from civil local date+time and UTC offset in hours
     * (e.g., EAT = +3, PST = -8). The offset is subtracted to get UT.
     */
    fun fromLocal(
        year: Int, month: Int, day: Int,
        hour: Int, minute: Int,
        utcOffsetHours: Double,
        second: Int = 0
    ): Double {
        val utHour = hour - utcOffsetHours
        // We fold the offset directly into the time portion; allow non-integer hours.
        return fromUT(year, month, day, 0, 0, 0) + (utHour + minute / 60.0 + second / 3600.0) / 24.0
    }

    /** Centuries since J2000.0 (TT — we approximate TT≈UT for non-physics use). */
    fun centuriesT(jd: Double): Double = (jd - 2451545.0) / 36525.0

    /** Greenwich Mean Sidereal Time in degrees at the given Julian Day. */
    fun gmstDegrees(jd: Double): Double {
        val t = centuriesT(jd)
        val gmst = 280.46061837 +
            360.98564736629 * (jd - 2451545.0) +
            0.000387933 * t * t -
            t * t * t / 38710000.0
        return AstroMath.norm360(gmst)
    }

    /** Local Sidereal Time in degrees for an east longitude. */
    fun lstDegrees(jd: Double, eastLongitudeDeg: Double): Double {
        return AstroMath.norm360(gmstDegrees(jd) + eastLongitudeDeg)
    }

    /** Mean obliquity of the ecliptic in degrees. Meeus eq. 22.2 (simplified). */
    fun meanObliquity(jd: Double): Double {
        val t = centuriesT(jd)
        return 23.43929111 -
            0.013004167 * t -
            1.6389e-7 * t * t +
            5.0361e-7 * t * t * t
    }
}
