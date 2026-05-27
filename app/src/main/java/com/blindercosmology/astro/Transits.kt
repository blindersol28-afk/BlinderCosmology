package com.blindercosmology.astro

import java.time.LocalDate
import java.time.ZoneOffset

object Transits {
    /** Returns transiting planet positions for the given UTC date at midnight. */
    fun forDate(date: LocalDate): Map<Body, Double> {
        val jd = JulianDay.fromUT(date.year, date.monthValue, date.dayOfMonth, 12, 0)
        val map = mutableMapOf<Body, Double>(
            Body.Sun  to Sun.longitude(jd),
            Body.Moon to Moon.longitude(jd),
        )
        map += Planets.all(jd)
        map[Body.NorthNode] = LunarNode.meanNorth(jd)
        return map
    }

    /** Aspects formed by today's transiting bodies to natal placements (tight orbs). */
    fun toNatal(natal: NatalChart, date: LocalDate = LocalDate.now(ZoneOffset.UTC)): List<TransitHit> {
        val trans = forDate(date)
        val hits = mutableListOf<TransitHit>()
        for ((tBody, tLon) in trans) {
            for ((nBody, nLon) in natal.positions) {
                if (nBody == Body.Ascendant || nBody == Body.Midheaven || nBody == Body.NorthNode) {
                    // include — angles + node transits matter
                }
                val sep = AstroMath.angularSeparation(tLon, nLon)
                for (type in AspectType.values()) {
                    val orb = kotlin.math.abs(sep - type.angle)
                    val maxOrb = when (type) {
                        AspectType.Conjunction, AspectType.Opposition -> 2.5
                        AspectType.Trine, AspectType.Square -> 2.0
                        AspectType.Sextile -> 1.5
                        AspectType.Quincunx -> 1.0
                    }
                    if (orb <= maxOrb) {
                        hits += TransitHit(tBody, nBody, type, orb)
                        break
                    }
                }
            }
        }
        return hits.sortedBy { it.orb }
    }
}

data class TransitHit(
    val transiting: Body,
    val natal: Body,
    val type: AspectType,
    val orb: Double,
)
