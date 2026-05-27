package com.blindercosmology.interpret

import com.blindercosmology.astro.AspectType
import com.blindercosmology.astro.Body
import com.blindercosmology.astro.Sign

/**
 * Interpretation library. Each entry is a short standalone line that can be stitched
 * into a longer reading. Lines are written with a Peaky-Blinders cadence: terse,
 * declarative, unsentimental.
 */
object Library {

    val sunBySign: Map<Sign, String> = mapOf(
        Sign.Aries to "Your core fire is direct — you act, then think. Initiative is your oxygen.",
        Sign.Taurus to "Steady at the bone. You build slowly and you don't bluff. Comfort is a value, not a vice.",
        Sign.Gemini to "Two minds in one chest. You learn at speed and bore at speed; variety keeps you sane.",
        Sign.Cancer to "You feel before you think. Loyalty runs deep; your home is a fortress.",
        Sign.Leo to "Born to be seen. You give heat, you demand the room — at your best, generously.",
        Sign.Virgo to "Precision and service. You notice the thread out of place that nobody else sees.",
        Sign.Libra to "Balance is your weather. You weigh, charm, and pair — alone is harder than it should be.",
        Sign.Scorpio to "Intensity is a feature, not a flaw. You go where others won't and bring back what they wouldn't dare touch.",
        Sign.Sagittarius to "A horizon-chaser. You need belief, distance, and the freedom to misbehave with ideas.",
        Sign.Capricorn to "Built for the long climb. You'd rather earn it than be handed it.",
        Sign.Aquarius to "Wired sideways to the room. Your loyalty is to the future, not the present consensus.",
        Sign.Pisces to "Permeable. You catch what others miss — moods, dreams, undertows. Boundary work matters.",
    )

    val moonBySign: Map<Sign, String> = mapOf(
        Sign.Aries to "Feelings move fast and burn hot. You self-regulate by doing, not by talking.",
        Sign.Taurus to "Emotional ballast. You need physical safety — food, touch, stability — to feel okay.",
        Sign.Gemini to "You think your feelings before you feel them. Journaling, conversation: that's your therapy.",
        Sign.Cancer to "Tidal inner life. You belong to your people; you carry their weather.",
        Sign.Leo to "You need to be witnessed. Recognition isn't vanity for you — it's nourishment.",
        Sign.Virgo to "You soothe yourself by ordering small things. Routine is medicine.",
        Sign.Libra to "Peace is the goal; conflict drains you faster than it should. Beauty restores you.",
        Sign.Scorpio to "All-or-nothing inner gears. You bond hard, withdraw harder. Privacy is non-negotiable.",
        Sign.Sagittarius to "You feel best in motion. Stagnation curdles into restlessness fast.",
        Sign.Capricorn to "Reserved. You may have learned early that feelings were inconvenient — they're not.",
        Sign.Aquarius to "You process emotion by stepping back from it. Friends are your true family.",
        Sign.Pisces to "Boundless empathy. Without a sieve, other people's feelings become yours.",
    )

    val ascBySign: Map<Sign, String> = mapOf(
        Sign.Aries to "You arrive sharp. People meet a doer; sometimes they miss the depth behind the edge.",
        Sign.Taurus to "Steady presence. You walk into a room without rushing it.",
        Sign.Gemini to "Quick, curious, talkative on contact. You read a room with your tongue.",
        Sign.Cancer to "You approach softly, shell first. People feel held before they know why.",
        Sign.Leo to "You don't enter — you arrive. The dial-up is real, even when you try to dim it.",
        Sign.Virgo to "Polished, observant, slightly reserved. You scan before you commit.",
        Sign.Libra to "Charm is your first language. Symmetry, manner, and easy aesthetic.",
        Sign.Scorpio to "Magnetic and contained. You give little away on first meeting and you know it.",
        Sign.Sagittarius to "Open-faced, opinion-ready. You laugh loud and you mean it.",
        Sign.Capricorn to "Composed. You read as older and more capable than your years.",
        Sign.Aquarius to "Unusual on arrival. There's always a detail — the cut, the take, the angle — that's off-script.",
        Sign.Pisces to "Dreamy edges. People can't quite place you, which is part of the spell.",
    )

    val venusBySign: Map<Sign, String> = mapOf(
        Sign.Aries to "You love at speed — the chase is the dopamine. Cool partners bore you.",
        Sign.Taurus to "Sensual, loyal, slow to start, slow to leave. Beauty and good food are love languages.",
        Sign.Gemini to "Words are foreplay. You need someone who can talk your weather as well as match it.",
        Sign.Cancer to "Love is tied to safety and feeding. You attach quickly and remember everything.",
        Sign.Leo to "Grand-gesture love. You want to be adored — and to adore back, loudly.",
        Sign.Virgo to "You love through service: the texts, the lists, the showing up. Receive it, too.",
        Sign.Libra to "Romance is an art form. Partnership matters more to you than you sometimes admit.",
        Sign.Scorpio to "All-the-way intimacy. You don't want surface; you want the soul or nothing.",
        Sign.Sagittarius to "Love-with-an-escape-hatch. You need a partner who travels well — physically or mentally.",
        Sign.Capricorn to "You love seriously. Commitment is the gift; play takes practice.",
        Sign.Aquarius to "Friendship-first love. You need space and unconventional terms.",
        Sign.Pisces to "Boundary-less romance. You merge fast — protect your sense of self in love.",
    )

    val marsBySign: Map<Sign, String> = mapOf(
        Sign.Aries to "Pure thrust. You go straight at it — your timing is now.",
        Sign.Taurus to "Slow ignition, long endurance. You wear opponents down rather than overwhelm them.",
        Sign.Gemini to "You fight with words and angles. Scattered effort is the trap.",
        Sign.Cancer to "Indirect. You strike when something you love is threatened.",
        Sign.Leo to "Theatrical force. You need the audience as much as the cause.",
        Sign.Virgo to "Methodical, sharp, precise. You work the technique until it's unbeatable.",
        Sign.Libra to "Conflict-averse but cunning. You win by strategy and persuasion.",
        Sign.Scorpio to "Surgical. You don't telegraph; you act, and you act once.",
        Sign.Sagittarius to "Crusading. You go in for the cause more than the win.",
        Sign.Capricorn to "Ambition with patience. You play the long game and you don't blink.",
        Sign.Aquarius to "Unpredictable force. You attack from the angle nobody saw.",
        Sign.Pisces to "Diffuse energy. Channeled through art, water, devotion — formidable; otherwise slips.",
    )

    val houseFocus: Map<Int, String> = mapOf(
        1  to "self-presentation, body, first impressions",
        2  to "money, values, possessions, self-worth",
        3  to "communication, siblings, short trips, learning",
        4  to "home, family roots, private life, foundations",
        5  to "creativity, romance, children, play, performance",
        6  to "work, daily routine, health, service",
        7  to "partnerships, marriage, open enemies, mirrors",
        8  to "intimacy, shared resources, transformation, the hidden",
        9  to "philosophy, travel, higher study, belief",
        10 to "career, public reputation, vocation",
        11 to "friends, networks, hopes, future",
        12 to "the unconscious, retreat, hidden enemies, dissolution",
    )

    val aspectFlavor: Map<AspectType, String> = mapOf(
        AspectType.Conjunction to "fused — one signature",
        AspectType.Opposition to "in tension — pulling on each other",
        AspectType.Trine to "flowing — natural ease",
        AspectType.Square to "in friction — friction that makes you stronger",
        AspectType.Sextile to "supportive — an open door if you walk through it",
        AspectType.Quincunx to "off-key — needs adjustment, never quite resolves",
    )

    val numerologyDetail: Map<Int, NumDetail> = mapOf(
        1  to NumDetail("lead, originate, claim your road", "self-employment, founding, single-author work"),
        2  to NumDetail("connect, mediate, partner", "counseling, diplomacy, design partnerships"),
        3  to NumDetail("express, create, communicate", "writing, performance, media, teaching"),
        4  to NumDetail("build, structure, persist", "engineering, finance, trades, operations"),
        5  to NumDetail("explore, change, sell", "sales, travel, media, anything dynamic"),
        6  to NumDetail("nurture, beautify, take responsibility", "caregiving, hospitality, design, family business"),
        7  to NumDetail("investigate, deepen, withdraw to know", "research, analysis, spirituality, specialist crafts"),
        8  to NumDetail("organize power, money, and authority", "executive roles, finance, real estate, scale"),
        9  to NumDetail("serve, complete, release", "humanitarian, the arts, late-stage healing work"),
        11 to NumDetail("inspire and channel", "spiritual teaching, the arts, public-facing vision work"),
        22 to NumDetail("manifest at scale", "founding institutions, large infrastructure, civic building"),
        33 to NumDetail("teach through service", "healing, ministry, deep mentorship"),
    )
}

data class NumDetail(val verbCluster: String, val careerCluster: String)
