package com.blindercosmology.astro

data class BirthInfo(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val utcOffsetHours: Double,
    val latitude: Double,
    val longitude: Double,
    val placeLabel: String = "",
)

data class Placement(
    val body: Body,
    val longitude: Double,
    val sign: Sign,
    val degreeInSign: Double,
    val house: Int,
)

data class NatalChart(
    val info: BirthInfo,
    val jd: Double,
    val obliquity: Double,
    val ascendant: Double,
    val midheaven: Double,
    val cusps: List<Double>,
    val positions: Map<Body, Double>,
    val placements: List<Placement>,
    val aspects: List<Aspect>,
)

object ChartBuilder {
    fun build(info: BirthInfo): NatalChart {
        val jd = JulianDay.fromLocal(
            info.year, info.month, info.day, info.hour, info.minute, info.utcOffsetHours
        )
        val obl = JulianDay.meanObliquity(jd)
        val lst = JulianDay.lstDegrees(jd, info.longitude)
        val houses = Houses.compute(lst, info.latitude, obl)

        val sunLon = Sun.longitude(jd)
        val moonLon = Moon.longitude(jd)
        val asc = houses.ascendant

        val positions = mutableMapOf<Body, Double>(
            Body.Sun  to sunLon,
            Body.Moon to moonLon,
        )
        positions += Planets.all(jd)
        positions[Body.Chiron]    = Chiron.longitude(jd)
        positions[Body.Lilith]    = Lilith.meanLongitude(jd)
        positions[Body.NorthNode] = LunarNode.meanNorth(jd)
        positions[Body.Ascendant] = asc
        positions[Body.Midheaven] = houses.midheaven
        positions[Body.Vertex]    = Houses.vertex(lst, info.latitude, obl)

        // Part of Fortune — depends on whether the Sun was above the horizon at birth.
        val sunHouse = Houses.houseOf(sunLon, houses.cusps)
        val isDayChart = sunHouse in 7..12
        val fortune = if (isDayChart)
            AstroMath.norm360(asc + moonLon - sunLon)
        else
            AstroMath.norm360(asc + sunLon - moonLon)
        positions[Body.Fortune] = fortune

        val placements = positions.map { (body, lon) ->
            val (sign, deg) = Sign.fromLongitude(lon)
            Placement(body, lon, sign, deg, Houses.houseOf(lon, houses.cusps))
        }.sortedBy { it.body.ordinal }

        // Aspects: include planets + luminaries + Chiron + node + angles. Skip Lilith,
        // Vertex, and Fortune — they're sensitive points but their aspect lines turn
        // the table to noise. They're still rendered on the wheel as static markers.
        val aspectBodies = positions.filterKeys {
            it !in setOf(Body.Lilith, Body.Vertex, Body.Fortune)
        }
        val aspects = Aspects.detect(aspectBodies)

        return NatalChart(
            info = info,
            jd = jd,
            obliquity = obl,
            ascendant = asc,
            midheaven = houses.midheaven,
            cusps = houses.cusps,
            positions = positions,
            placements = placements,
            aspects = aspects,
        )
    }
}
