package com.blindercosmology.interpret

import com.blindercosmology.astro.AspectType
import com.blindercosmology.astro.Body
import com.blindercosmology.astro.NatalChart
import com.blindercosmology.astro.Placement
import com.blindercosmology.astro.Sign
import com.blindercosmology.numerology.Numerology

data class Reading(
    val openingTitle: String,
    val personality: String,
    val mission: String,
    val relationships: String,
    val money: String,
    val career: String,
    val health: String,
    val numerology: String,
)

object ReadingGenerator {

    fun generate(chart: NatalChart, fullName: String?): Reading {
        val info = chart.info
        val sun  = placement(chart, Body.Sun)
        val moon = placement(chart, Body.Moon)
        val asc  = chart.ascendant.let { Sign.fromLongitude(it).first }
        val venus = placement(chart, Body.Venus)
        val mars  = placement(chart, Body.Mars)
        val mercury = placement(chart, Body.Mercury)
        val jupiter = placement(chart, Body.Jupiter)
        val saturn  = placement(chart, Body.Saturn)
        val mc = Sign.fromLongitude(chart.midheaven).first

        val title = "${sun.sign.glyph} ${sun.sign.displayName} Sun · " +
            "${moon.sign.glyph} ${moon.sign.displayName} Moon · " +
            "${asc.glyph} ${asc.displayName} Rising"

        val personality = buildString {
            append(Library.sunBySign[sun.sign]); append(' ')
            append(Library.moonBySign[moon.sign]); append(' ')
            append(Library.ascBySign[asc])
            append("\n\nYour Sun in the ${ordinal(sun.house)} house puts your essential business in the realm of ${Library.houseFocus[sun.house]}. ")
            append("Your Moon in the ${ordinal(moon.house)} house means you find emotional ground through ${Library.houseFocus[moon.house]}. ")
            val tightAspectsToLuminaries = chart.aspects.filter {
                (it.a == Body.Sun || it.b == Body.Sun || it.a == Body.Moon || it.b == Body.Moon) && it.orb <= 4.0
            }.take(3)
            if (tightAspectsToLuminaries.isNotEmpty()) {
                append("\n\nKey wiring: ")
                append(tightAspectsToLuminaries.joinToString("; ") {
                    "${it.a.displayName} ${it.type.displayName.lowercase()} ${it.b.displayName} — ${Library.aspectFlavor[it.type]}"
                })
                append('.')
            }
        }

        val mission = buildString {
            append("Your Midheaven sits in $mc — your public direction tilts ")
            append(when (mc) {
                Sign.Aries -> "toward pioneering and visible initiative."
                Sign.Taurus -> "toward building enduring, tangible value."
                Sign.Gemini -> "toward communication, media, and intellectual versatility."
                Sign.Cancer -> "toward caregiving, hospitality, or family-rooted work."
                Sign.Leo -> "toward visible leadership and creative authority."
                Sign.Virgo -> "toward craft, service, and analytic mastery."
                Sign.Libra -> "toward partnership, aesthetics, and arbitration."
                Sign.Scorpio -> "toward investigative, transformative, or financial depth work."
                Sign.Sagittarius -> "toward teaching, publishing, travel, or law."
                Sign.Capricorn -> "toward authority, structure, and institution-building."
                Sign.Aquarius -> "toward innovation, technology, or social change."
                Sign.Pisces -> "toward healing, art, the unseen — vocation as devotion."
            })
            append(" Your North Node — the medicine your soul came to take — ")
            val node = chart.placements.first { it.body == Body.NorthNode }
            append("is in ${node.sign.displayName} in the ${ordinal(node.house)} house. ")
            append("Lean into ${nodeVerb(node.sign)} through ${Library.houseFocus[node.house]}. ")
            append("That direction will feel unfamiliar at first; that's how you know it's the right one.")
        }

        val relationships = buildString {
            append(Library.venusBySign[venus.sign]); append(' ')
            append("Venus operating in the ${ordinal(venus.house)} house means love and money flow through ${Library.houseFocus[venus.house]}. ")
            append("\n\n")
            append(Library.marsBySign[mars.sign]); append(' ')
            append("In the ${ordinal(mars.house)} house, your drive expresses through ${Library.houseFocus[mars.house]}. ")
            append("\n\nWhat you seek in a partner: ")
            append(seekInPartner(chart))
            // Vertex — the point of "fated" encounters.
            chart.placements.firstOrNull { it.body == Body.Vertex }?.let { vx ->
                append("\n\nYour Vertex — the door through which fated encounters tend to walk — is in ")
                append("${vx.sign.displayName} in the ${ordinal(vx.house)} house. ")
                append("Significant people often arrive through ${Library.houseFocus[vx.house]}; ")
                append("the ${vx.sign.displayName} quality is the texture of their first impression.")
            }
            // Lilith — the raw, unsanctioned desire side of intimacy.
            chart.placements.firstOrNull { it.body == Body.Lilith }?.let { li ->
                append("\n\nBlack Moon Lilith in ${li.sign.displayName} (${ordinal(li.house)} house) ")
                append("points to where you've felt unwelcome to be wild — and where reclaiming that ")
                append("raw appetite, in measured doses, will free you.")
            }
        }

        val money = buildString {
            val second = chart.placements.firstOrNull { it.house == 2 }
            val eighth = chart.placements.firstOrNull { it.house == 8 }
            append("Your 2nd house — money you earn, what you value — begins in ${signOfCusp(chart, 1)}. ")
            append(moneyByCuspSign(signOfCusp(chart, 1)))
            if (second != null) append(" The presence of ${second.body.displayName} there sharpens the picture — ${moneyBodyHouse(second.body)}. ")
            append("\n\nYour 8th house — shared money, debts, inheritances, deep resources — opens in ${signOfCusp(chart, 7)}. ")
            append(eighthByCuspSign(signOfCusp(chart, 7)))
            if (eighth != null) append(" With ${eighth.body.displayName} active here, ${eighthBodyNote(eighth.body)}. ")
            append("\n\nJupiter — your luck and abundance signal — is in ${jupiter.sign.displayName} in the ${ordinal(jupiter.house)} house. ")
            append("Fortune favors you most when you're occupied with ${Library.houseFocus[jupiter.house]}.")
            // Part of Fortune — the place of natural ease and material flow.
            chart.placements.firstOrNull { it.body == Body.Fortune }?.let { pf ->
                append("\n\nThe Part of Fortune sits in ${pf.sign.displayName}, ${ordinal(pf.house)} house — ")
                append("look for ease, flow, and quiet luck through ${Library.houseFocus[pf.house]}.")
            }
        }

        val career = buildString {
            append("Vocational signature: ${mc.displayName} Midheaven, ")
            append("${ordinal(saturn.house)}-house Saturn in ${saturn.sign.displayName}. ")
            append("Saturn tells you where you'll do your slowest, most rewarding work — ")
            append("expect mastery in matters of ${Library.houseFocus[saturn.house]}, earned the hard way. ")
            append("\n\nMercury in ${mercury.sign.displayName} (${ordinal(mercury.house)} house) shapes how you think and communicate at work — ")
            append(mercuryWork(mercury.sign))
            append(' ')
            append("\n\nRoles that fit the shape of this chart: ")
            append(careerSuggestions(sun.sign, mc, mars.sign))
        }

        val health = buildString {
            append("The 6th house — daily routine, body — opens in ${signOfCusp(chart, 5)}, ")
            append(healthByCuspSign(signOfCusp(chart, 5)))
            append(" Mars in ${mars.sign.displayName} suggests ")
            append(marsHealth(mars.sign))
            append(" Your Moon in ${moon.sign.displayName} indicates your nervous system needs ")
            append(moonHealth(moon.sign))
            // Chiron — the wound and the healing capacity that grows from it.
            chart.placements.firstOrNull { it.body == Body.Chiron }?.let { ch ->
                append("\n\nChiron — your old wound, and the place you can teach others to heal — sits in ")
                append("${ch.sign.displayName} in the ${ordinal(ch.house)} house. ")
                append("Expect the soreness, and the medicine, to live in matters of ")
                append("${Library.houseFocus[ch.house]}.")
            }
        }

        val numerology = buildString {
            val lp = Numerology.lifePath(info.year, info.month, info.day)
            append("Life Path $lp: ")
            append(Numerology.lifePathMeaning[lp] ?: "an unusual signature.")
            Library.numerologyDetail[lp]?.let {
                append("\n\nWhat to do with it: ${it.verbCluster}. Career colours: ${it.careerCluster}.")
            }
            if (!fullName.isNullOrBlank()) {
                val exp = Numerology.expression(fullName)
                val soul = Numerology.soulUrge(fullName)
                val per = Numerology.personality(fullName)
                append("\n\nFrom your name:")
                append("\n• Expression $exp — how the world meets you on the surface.")
                append("\n• Soul Urge $soul — what your interior actually wants.")
                append("\n• Personality $per — the mask you wear.")
            }
        }

        return Reading(
            openingTitle = title,
            personality = personality.trim(),
            mission = mission.trim(),
            relationships = relationships.trim(),
            money = money.trim(),
            career = career.trim(),
            health = health.trim(),
            numerology = numerology.trim(),
        )
    }

    private fun placement(chart: NatalChart, body: Body): Placement =
        chart.placements.first { it.body == body }

    private fun signOfCusp(chart: NatalChart, idx: Int): Sign =
        Sign.fromLongitude(chart.cusps[idx]).first

    private fun ordinal(n: Int): String = when (n) {
        1 -> "1st"; 2 -> "2nd"; 3 -> "3rd"
        21 -> "21st"; 22 -> "22nd"; 23 -> "23rd"
        else -> "${n}th"
    }

    private fun nodeVerb(s: Sign): String = when (s) {
        Sign.Aries -> "self-direction and the courage to put your name first"
        Sign.Taurus -> "stability, embodiment, and earning your own ground"
        Sign.Gemini -> "curiosity, questions, and the everyday conversation"
        Sign.Cancer -> "feeling, family, and emotional honesty"
        Sign.Leo -> "self-expression and being seen on your own terms"
        Sign.Virgo -> "discernment, craft, and the small daily disciplines"
        Sign.Libra -> "partnership and learning to share the centre"
        Sign.Scorpio -> "depth, intimacy, and the willingness to die a small death"
        Sign.Sagittarius -> "belief, exploration, and the larger view"
        Sign.Capricorn -> "structure, accountability, and the long climb"
        Sign.Aquarius -> "community and a vision beyond yourself"
        Sign.Pisces -> "surrender, art, and the dissolving of the small self"
    }

    private fun seekInPartner(chart: NatalChart): String {
        val seventh = signOfCusp(chart, 6)
        return when (seventh) {
            Sign.Aries -> "someone with edge and decisiveness — they push you to act."
            Sign.Taurus -> "stability you can lean on — steady, sensual, slow to leave."
            Sign.Gemini -> "a mind that can keep up — banter, variety, ideas as foreplay."
            Sign.Cancer -> "emotional fluency and a willingness to make a home with you."
            Sign.Leo -> "warmth, presence, generosity — someone who'll show up out loud."
            Sign.Virgo -> "thoughtfulness in detail — devotion shown through small acts."
            Sign.Libra -> "grace, fairness, and someone who treats partnership as an art."
            Sign.Scorpio -> "intensity — no surface relationships; transformation through love."
            Sign.Sagittarius -> "a companion in growth — someone who travels well, literally or not."
            Sign.Capricorn -> "maturity, commitment, and a partner who's serious about building something."
            Sign.Aquarius -> "an unconventional equal — a friend first, then everything else."
            Sign.Pisces -> "soul-level resonance — empathic, creative, sometimes elusive."
        }
    }

    private fun moneyByCuspSign(s: Sign): String = when (s) {
        Sign.Aries -> "You earn through initiative and risk; multiple income lines suit you."
        Sign.Taurus -> "You earn steadily — slow, accumulating, comfort-focused."
        Sign.Gemini -> "Several income streams; you make money with words, ideas, and connections."
        Sign.Cancer -> "Income tied to home, family, food, or care — and it ebbs and flows."
        Sign.Leo -> "You earn from visibility, creativity, leadership."
        Sign.Virgo -> "Money comes from precision, skill, and being the person who does it right."
        Sign.Libra -> "Income through partnership, aesthetics, mediation."
        Sign.Scorpio -> "Wealth via depth — other people's money, taxes, investments, hidden value."
        Sign.Sagittarius -> "Money through teaching, travel, publishing, the international."
        Sign.Capricorn -> "Long-arc earning. Authority and patience compound for you."
        Sign.Aquarius -> "Income from the unusual angle — tech, communities, future-facing work."
        Sign.Pisces -> "Earnings from art, healing, behind-the-scenes work — be careful with leaks."
    }

    private fun moneyBodyHouse(body: Body): String = when (body) {
        Body.Sun -> "money is woven into identity — your earning is personal"
        Body.Moon -> "income fluctuates with your moods and seasons"
        Body.Mercury -> "writing, speaking, trading move the dial"
        Body.Venus -> "money flows easily, especially through beauty, art, or partnership"
        Body.Mars -> "you earn through drive and direct action — and you can burn it as fast"
        Body.Jupiter -> "expect expansion, occasional luck, generosity that returns"
        Body.Saturn -> "money is serious and earned slowly — restrictions early teach lasting discipline"
        Body.Uranus -> "income is irregular, sudden gains and losses; freelance suits"
        Body.Neptune -> "watch for vagueness, leaks, or unrealistic schemes; intuitive choices win"
        Body.Pluto -> "money carries power and transformation; control issues need management"
        Body.NorthNode -> "your growth is via earning your own resources"
        else -> "an unusual but defining feature here"
    }

    private fun eighthByCuspSign(s: Sign): String = when (s) {
        Sign.Aries -> "you take risks with shared resources; act, then negotiate."
        Sign.Taurus -> "you build wealth slowly through joint ventures; loyalty in financial bonds."
        Sign.Gemini -> "negotiation, contracts, and dual income with partners."
        Sign.Cancer -> "money entwined with family, inheritance, emotional bonds."
        Sign.Leo -> "the spotlight follows your shared resources — visibility through partnership wealth."
        Sign.Virgo -> "you manage shared resources meticulously; you catch the leak."
        Sign.Libra -> "harmonious financial partnerships; legal agreements are your edge."
        Sign.Scorpio -> "deep transformation through shared resources; powerful financial intuition."
        Sign.Sagittarius -> "international or philosophical dimensions to shared money."
        Sign.Capricorn -> "ambitious about shared wealth; long-term investment thinking."
        Sign.Aquarius -> "unconventional shared finances; crowdfunding, collective ventures."
        Sign.Pisces -> "intuitive about shared resources; watch boundaries with joint money."
    }

    private fun eighthBodyNote(body: Body): String = when (body) {
        Body.Sun -> "your identity goes through transformations via intimacy and shared resources"
        Body.Moon -> "you have psychic radar for hidden things; intimacy reshapes your emotional core"
        Body.Mercury -> "you investigate, communicate about taboos, hidden subjects"
        Body.Venus -> "love, money, and intimacy interweave — partners materially affect your life"
        Body.Mars -> "passionate, sometimes combative around shared resources, intimacy"
        Body.Jupiter -> "expansion through inheritance, investments, or partner's resources"
        Body.Saturn -> "lessons around shared money and intimacy; mastery comes through restriction"
        Body.Uranus -> "sudden shifts in shared finances or sexual life"
        Body.Neptune -> "spiritual or confused experience of intimacy and shared resources"
        Body.Pluto -> "deep power dynamics in shared resources; transformation is the recurring theme"
        else -> "this house carries unusual significance"
    }

    private fun mercuryWork(s: Sign): String = when (s) {
        Sign.Aries -> "you think fast and shoot from the hip; learn to pause before sending."
        Sign.Taurus -> "you think slowly, deeply, and well; never rush you."
        Sign.Gemini -> "lightning mind, hungry for variety; finish what you start."
        Sign.Cancer -> "you think with feeling; intuitive memory is a superpower."
        Sign.Leo -> "you communicate with flair; people remember what you say."
        Sign.Virgo -> "razor-sharp analysis; precision is your edge — perfectionism, the cost."
        Sign.Libra -> "you weigh both sides — decisions take time but tend to be fair."
        Sign.Scorpio -> "deep, investigative, secretive; you see what others won't say."
        Sign.Sagittarius -> "big-picture thinker; tactical details bore you."
        Sign.Capricorn -> "strategic, structured, serious thinker; built for the long plan."
        Sign.Aquarius -> "original, sideways, future-focused; you see what's coming."
        Sign.Pisces -> "intuitive, poetic, sometimes vague — write your good ideas down quickly."
    }

    private fun careerSuggestions(sun: Sign, mc: Sign, mars: Sign): String {
        val pool = setOf(sun, mc, mars)
        val parts = mutableListOf<String>()
        if (Sign.Aries in pool || Sign.Leo in pool) parts += "founding roles, sport, performance, military"
        if (Sign.Taurus in pool || Sign.Capricorn in pool) parts += "real estate, finance, agriculture, master crafts"
        if (Sign.Gemini in pool || Sign.Virgo in pool) parts += "writing, editing, education, analytics"
        if (Sign.Cancer in pool || Sign.Pisces in pool) parts += "care work, hospitality, mental health, the arts"
        if (Sign.Libra in pool || Sign.Aquarius in pool) parts += "design, diplomacy, technology, law"
        if (Sign.Scorpio in pool || Sign.Sagittarius in pool) parts += "research, investigation, publishing, international work"
        if (parts.isEmpty()) parts += "leadership and skilled-craft work"
        return parts.joinToString("; ") + "."
    }

    private fun healthByCuspSign(s: Sign): String = when (s) {
        Sign.Aries -> "your body wants intensity — interval training, sport, heat."
        Sign.Taurus -> "watch the throat and the neck; food, garden, slow walks suit you."
        Sign.Gemini -> "nerves and lungs — breathwork is medicine; avoid info overload."
        Sign.Cancer -> "stomach and digestion respond to your mood; tend the gut."
        Sign.Leo -> "your heart literally needs joy and play; sedentary lives hurt you."
        Sign.Virgo -> "your gut and nervous system; pristine routines do you well, perfectionism does not."
        Sign.Libra -> "kidneys, balance; harmony in environment is medicine."
        Sign.Scorpio -> "reproductive and elimination systems; depth therapies suit you."
        Sign.Sagittarius -> "hips, liver, sciatica; movement and being outdoors are vital."
        Sign.Capricorn -> "bones, knees, teeth; structure and weight-bearing work serve you."
        Sign.Aquarius -> "circulation, ankles, nervous system; community keeps you well."
        Sign.Pisces -> "feet, immunity, sensitivity to substances; water work is healing."
    }

    private fun marsHealth(s: Sign): String = when (s) {
        Sign.Aries, Sign.Scorpio -> "high-intensity output works — burn off excess."
        Sign.Taurus, Sign.Capricorn -> "endurance work — walking, weights, long form."
        Sign.Gemini, Sign.Virgo -> "varied exercise — yoga + cardio + skill sport."
        Sign.Cancer, Sign.Pisces -> "swimming, dance, water — fluid movement settles you."
        Sign.Leo, Sign.Sagittarius -> "play, sport, anything where you can compete or perform."
        Sign.Libra, Sign.Aquarius -> "team sports, partner workouts, social movement."
    }

    private fun moonHealth(s: Sign): String = when (s) {
        Sign.Aries -> "physical release — when you can't move, you can't sleep."
        Sign.Taurus -> "physical comfort, food, touch, beauty."
        Sign.Gemini -> "conversation, books, mental novelty — and protection from overload."
        Sign.Cancer -> "home, family, water, retreat — rest is non-negotiable."
        Sign.Leo -> "joy, recognition, creative expression."
        Sign.Virgo -> "order, routine, useful daily tasks — and permission to be imperfect."
        Sign.Libra -> "beauty, harmony, pleasant company; conflict is toxic to you."
        Sign.Scorpio -> "depth, privacy, intimacy with the right person — solitude in measured doses."
        Sign.Sagittarius -> "movement, nature, freedom, philosophical conversation."
        Sign.Capricorn -> "achievement, structure, but also permission to soften — work is your default coping."
        Sign.Aquarius -> "intellectual stimulation, friend communities, space to be different."
        Sign.Pisces -> "art, music, water, sleep, solitude — and a strong filter for what you take in."
    }
}
