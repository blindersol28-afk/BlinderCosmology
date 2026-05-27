package com.blindercosmology.astro

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object AstroMath {
    const val DEG_TO_RAD = PI / 180.0
    const val RAD_TO_DEG = 180.0 / PI

    fun Double.toRad(): Double = this * DEG_TO_RAD
    fun Double.toDeg(): Double = this * RAD_TO_DEG

    /** Normalize an angle in degrees to [0, 360). */
    fun norm360(d: Double): Double {
        var x = d % 360.0
        if (x < 0) x += 360.0
        return x
    }

    /** Normalize an angle in degrees to (-180, 180]. */
    fun norm180(d: Double): Double {
        var x = norm360(d)
        if (x > 180.0) x -= 360.0
        return x
    }

    fun sinD(d: Double): Double = sin(d.toRad())
    fun cosD(d: Double): Double = cos(d.toRad())
    fun atan2D(y: Double, x: Double): Double = atan2(y, x).toDeg()

    /** Shortest angular distance between two longitudes, in [0, 180]. */
    fun angularSeparation(a: Double, b: Double): Double {
        val d = abs(norm360(a) - norm360(b))
        return if (d > 180.0) 360.0 - d else d
    }
}
