package com.blindercosmology.ai

import com.blindercosmology.astro.Body
import com.blindercosmology.astro.NatalChart
import com.blindercosmology.interpret.Reading

/**
 * Wraps Gemini to expand a rule-based [Reading] into per-section deeper interpretations,
 * grounded in the actual chart placements (so the AI explains the *why*, not invents new facts).
 */
class ReadingExpander(private val gemini: GeminiClient) {

    enum class Section(val title: String, val ruleText: (Reading) -> String) {
        Personality("Personality",     { it.personality }),
        Mission    ("Life Mission",    { it.mission }),
        Relationships("Relationships", { it.relationships }),
        Money      ("Money",           { it.money }),
        Career     ("Career",          { it.career }),
        Health     ("Health",          { it.health }),
        Numerology ("Numerology",      { it.numerology }),
    }

    val available: Boolean get() = gemini.isConfigured

    suspend fun expand(
        section: Section,
        reading: Reading,
        chart: NatalChart,
        userName: String,
    ): GeminiClient.Result {
        if (!gemini.isConfigured) return GeminiClient.Result.Err("AI not configured.")
        val placements = summarizePlacements(chart)
        val aspects = summarizeAspects(chart)
        val ruleText = section.ruleText(reading)

        val system = """
            You are a thoughtful astrology interpreter writing in a terse, literate voice
            inspired by film-noir narration — direct, unsentimental, no mystical fluff.
            Your job is to take a rule-based astrology reading and:
              1. Explain WHY each claim follows from the actual chart placements.
              2. Add nuance, drawing on aspects and house emphasis the rule engine simplified.
              3. End with two or three specific, actionable pieces of advice.
            Never invent placements. Only reference what is in the data provided.
            Address the user by name once, in the first sentence. Avoid bullet points; write flowing prose.
            Stay under 220 words.
        """.trimIndent()

        val prompt = """
            User: $userName
            Section to expand: ${section.title}

            Natal placements:
            $placements

            Active aspects (tight orbs, top 8):
            $aspects

            Rule-based interpretation for this section:
            ---
            $ruleText
            ---

            Now write the expanded interpretation for "${section.title}".
        """.trimIndent()

        return gemini.generate(prompt = prompt, systemInstruction = system, temperature = 0.75)
    }

    private fun summarizePlacements(chart: NatalChart): String = buildString {
        for (p in chart.placements) {
            if (p.body == Body.Ascendant) {
                append("Ascendant ${p.sign.displayName} ${"%.1f".format(p.degreeInSign)}°\n")
                continue
            }
            if (p.body == Body.Midheaven) {
                append("Midheaven ${p.sign.displayName} ${"%.1f".format(p.degreeInSign)}°\n")
                continue
            }
            append("${p.body.displayName} in ${p.sign.displayName} ${"%.1f".format(p.degreeInSign)}° (house ${p.house})\n")
        }
    }.trimEnd()

    private fun summarizeAspects(chart: NatalChart): String =
        chart.aspects.take(8).joinToString("\n") { a ->
            "${a.a.displayName} ${a.type.displayName.lowercase()} ${a.b.displayName} (orb ${"%.1f".format(a.orb)}°)"
        }.ifBlank { "(no major aspects within standard orbs)" }
}
