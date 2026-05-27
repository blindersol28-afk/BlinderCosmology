package com.blindercosmology.astro

enum class Element { Fire, Earth, Air, Water }
enum class Modality { Cardinal, Fixed, Mutable }

enum class Sign(
    val displayName: String,
    val glyph: String,
    val element: Element,
    val modality: Modality,
    val ruler: String,
) {
    Aries      ("Aries",       "♈", Element.Fire,  Modality.Cardinal, "Mars"),
    Taurus     ("Taurus",      "♉", Element.Earth, Modality.Fixed,    "Venus"),
    Gemini     ("Gemini",      "♊", Element.Air,   Modality.Mutable,  "Mercury"),
    Cancer     ("Cancer",      "♋", Element.Water, Modality.Cardinal, "Moon"),
    Leo        ("Leo",         "♌", Element.Fire,  Modality.Fixed,    "Sun"),
    Virgo      ("Virgo",       "♍", Element.Earth, Modality.Mutable,  "Mercury"),
    Libra      ("Libra",       "♎", Element.Air,   Modality.Cardinal, "Venus"),
    Scorpio    ("Scorpio",     "♏", Element.Water, Modality.Fixed,    "Pluto"),
    Sagittarius("Sagittarius", "♐", Element.Fire,  Modality.Mutable,  "Jupiter"),
    Capricorn  ("Capricorn",   "♑", Element.Earth, Modality.Cardinal, "Saturn"),
    Aquarius   ("Aquarius",    "♒", Element.Air,   Modality.Fixed,    "Uranus"),
    Pisces     ("Pisces",      "♓", Element.Water, Modality.Mutable,  "Neptune");

    companion object {
        /** Sign and degree within sign for an ecliptic longitude in [0, 360). */
        fun fromLongitude(lon: Double): Pair<Sign, Double> {
            val normalized = ((lon % 360.0) + 360.0) % 360.0
            val idx = (normalized / 30.0).toInt().coerceIn(0, 11)
            return values()[idx] to (normalized - idx * 30.0)
        }

        fun byIndex(i: Int): Sign = values()[((i % 12) + 12) % 12]
    }
}

enum class Body(val displayName: String, val glyph: String) {
    Sun     ("Sun",     "☉"),
    Moon    ("Moon",    "☽"),
    Mercury ("Mercury", "☿"),
    Venus   ("Venus",   "♀"),
    Mars    ("Mars",    "♂"),
    Jupiter ("Jupiter", "♃"),
    Saturn  ("Saturn",  "♄"),
    Uranus  ("Uranus",  "♅"),
    Neptune ("Neptune", "♆"),
    Pluto   ("Pluto",   "♇"),
    Chiron   ("Chiron",  "⚷"),
    Lilith   ("Black Moon Lilith", "⚸"),
    NorthNode("North Node", "☊"),
    Fortune  ("Part of Fortune", "⊗"),
    Vertex   ("Vertex", "Vx"),
    Ascendant("Ascendant", "ASC"),
    Midheaven("Midheaven", "MC"),
}
