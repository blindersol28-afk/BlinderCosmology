package com.blindercosmology.astro

enum class AspectType(val displayName: String, val angle: Double, val defaultOrb: Double, val symbol: String) {
    Conjunction("Conjunction", 0.0,   8.0, "☌"),
    Opposition ("Opposition", 180.0,  8.0, "☍"),
    Trine      ("Trine",      120.0,  7.0, "△"),
    Square     ("Square",      90.0,  7.0, "□"),
    Sextile    ("Sextile",     60.0,  5.0, "✶"),
    Quincunx   ("Quincunx",   150.0,  3.0, "⚻"),
}

data class Aspect(
    val a: Body,
    val b: Body,
    val type: AspectType,
    val orb: Double,
    val exactAngle: Double,
)

object Aspects {
    fun detect(positions: Map<Body, Double>, orbScale: Double = 1.0): List<Aspect> {
        val list = positions.entries.toList()
        val result = mutableListOf<Aspect>()
        for (i in list.indices) {
            for (j in i + 1 until list.size) {
                val a = list[i]; val b = list[j]
                val sep = AstroMath.angularSeparation(a.value, b.value)
                for (type in AspectType.values()) {
                    val orb = kotlin.math.abs(sep - type.angle)
                    if (orb <= type.defaultOrb * orbScale) {
                        result += Aspect(a.key, b.key, type, orb, sep)
                        break  // strongest match wins
                    }
                }
            }
        }
        return result.sortedBy { it.orb }
    }
}
