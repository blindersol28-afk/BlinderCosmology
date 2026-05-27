package com.blindercosmology.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.blindercosmology.astro.Body
import com.blindercosmology.astro.Element
import com.blindercosmology.astro.Modality
import com.blindercosmology.astro.NatalChart
import com.blindercosmology.ui.theme.AirCol
import com.blindercosmology.ui.theme.Bone
import com.blindercosmology.ui.theme.Brass
import com.blindercosmology.ui.theme.EarthCol
import com.blindercosmology.ui.theme.FireCol
import com.blindercosmology.ui.theme.Sepia
import com.blindercosmology.ui.theme.Smoke
import com.blindercosmology.ui.theme.WaterCol

@Composable
fun BirthDetailsHeader(chart: NatalChart, name: String) {
    val info = chart.info
    val dateStr = "${info.year}-${"%02d".format(info.month)}-${"%02d".format(info.day)}"
    val timeStr = "${"%02d".format(info.hour)}:${"%02d".format(info.minute)}"
    val tzStr = if (info.utcOffsetHours >= 0) "+${info.utcOffsetHours}" else "${info.utcOffsetHours}"
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(2.dp))
            .background(Smoke)
            .border(1.dp, Sepia.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
            .padding(14.dp),
    ) {
        Column {
            Text("BIRTH CHART", style = MaterialTheme.typography.labelLarge, color = Sepia)
            Spacer(Modifier.height(4.dp))
            Text(name, style = MaterialTheme.typography.titleLarge, color = Bone)
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = Brass.copy(alpha = 0.3f), thickness = 0.5.dp)
            Spacer(Modifier.height(6.dp))
            DetailLine("Date", "$dateStr  $timeStr  (UTC $tzStr)")
            if (info.placeLabel.isNotBlank()) DetailLine("Place", info.placeLabel)
            DetailLine("Lat / Lon", "${formatLat(info.latitude)}  ${formatLon(info.longitude)}")
            DetailLine("System", "Placidus")
        }
    }
}

@Composable
private fun DetailLine(k: String, v: String) {
    Row(modifier = Modifier.padding(vertical = 1.dp)) {
        Text(k, style = MaterialTheme.typography.bodyMedium, color = Sepia.copy(alpha = 0.7f),
            modifier = Modifier.width(80.dp))
        Text(v, style = MaterialTheme.typography.bodyMedium, color = Bone)
    }
}

@Composable
fun ChartStatsRow(chart: NatalChart) {
    val (elementCounts, modalityCounts) = countSignsAndModalities(chart)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatsCard(
            title = "ELEMENTS",
            items = Element.values().map { it.name.take(3).uppercase() to (elementCounts[it] ?: 0) },
            colors = Element.values().map { elementColor(it) },
            modifier = Modifier.weight(1f),
        )
        StatsCard(
            title = "MODALITIES",
            items = Modality.values().map { it.name.take(3).uppercase() to (modalityCounts[it] ?: 0) },
            colors = List(3) { Sepia },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatsCard(
    title: String,
    items: List<Pair<String, Int>>,
    colors: List<androidx.compose.ui.graphics.Color>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(Smoke)
            .border(1.dp, Sepia.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
            .padding(10.dp),
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.labelLarge, color = Sepia)
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                items.forEachIndexed { i, (label, count) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(label, style = MaterialTheme.typography.bodyMedium,
                            color = colors.getOrElse(i) { Bone }, fontWeight = FontWeight.Bold)
                        Text("$count", style = MaterialTheme.typography.headlineMedium, color = Bone)
                    }
                }
            }
        }
    }
}

private val excludedFromCounts = setOf(
    Body.Ascendant, Body.Midheaven,
    Body.NorthNode, Body.Chiron, Body.Lilith, Body.Vertex, Body.Fortune,
)

private fun countSignsAndModalities(chart: NatalChart): Pair<Map<Element, Int>, Map<Modality, Int>> {
    val el = mutableMapOf<Element, Int>()
    val mo = mutableMapOf<Modality, Int>()
    for (p in chart.placements) {
        if (p.body in excludedFromCounts) continue
        el.merge(p.sign.element, 1, Int::plus)
        mo.merge(p.sign.modality, 1, Int::plus)
    }
    return el to mo
}

private fun elementColor(e: Element) = when (e) {
    Element.Fire  -> FireCol
    Element.Earth -> EarthCol
    Element.Air   -> AirCol
    Element.Water -> WaterCol
}

private fun formatLat(lat: Double): String {
    val hemi = if (lat >= 0) "N" else "S"
    val v = kotlin.math.abs(lat)
    val deg = v.toInt()
    val min = ((v - deg) * 60).toInt()
    return "${deg}°${"%02d".format(min)}′$hemi"
}

private fun formatLon(lon: Double): String {
    val hemi = if (lon >= 0) "E" else "W"
    val v = kotlin.math.abs(lon)
    val deg = v.toInt()
    val min = ((v - deg) * 60).toInt()
    return "${deg}°${"%02d".format(min)}′$hemi"
}
