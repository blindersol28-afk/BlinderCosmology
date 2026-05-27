package com.blindercosmology.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.CircularProgressIndicator
import com.blindercosmology.BuildConfig
import com.blindercosmology.ai.GeminiClient
import com.blindercosmology.ai.ReadingExpander
import com.blindercosmology.astro.AspectType
import com.blindercosmology.astro.BirthInfo
import com.blindercosmology.astro.Body
import com.blindercosmology.astro.ChartBuilder
import com.blindercosmology.astro.Element
import com.blindercosmology.astro.NatalChart
import com.blindercosmology.interpret.Reading
import com.blindercosmology.interpret.ReadingGenerator
import com.blindercosmology.ui.components.BirthDetailsHeader
import com.blindercosmology.ui.components.ChartStatsRow
import com.blindercosmology.ui.components.ChartWheel
import com.blindercosmology.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(fullName: String, info: BirthInfo, onBack: () -> Unit) {
    val chart = remember(info) { ChartBuilder.build(info) }
    val reading = remember(chart, fullName) { ReadingGenerator.generate(chart, fullName) }
    val expander = remember {
        ReadingExpander(GeminiClient(BuildConfig.GEMINI_API_KEY))
    }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Chart Wheel", "Planets", "Aspects", "Full Reading")

    Scaffold(
        containerColor = InkBlack,
        topBar = {
            Column(modifier = Modifier.background(InkBlack)) {
                TopAppBar(
                    title = { Text(fullName, maxLines = 1) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Sepia)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = InkBlack, titleContentColor = Sepia
                    ),
                )
                Text(
                    reading.openingTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Sepia.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = InkBlack,
                    contentColor = Sepia,
                ) {
                    tabs.forEachIndexed { i, label ->
                        Tab(
                            selected = selectedTab == i,
                            onClick = { selectedTab = i },
                            text = { Text(label, style = MaterialTheme.typography.labelLarge) },
                            selectedContentColor = Sepia,
                            unselectedContentColor = Bone.copy(alpha = 0.6f),
                        )
                    }
                }
            }
        },
    ) { pad ->
        Box(modifier = Modifier.padding(pad).fillMaxSize()) {
            when (selectedTab) {
                0 -> WheelTab(chart, fullName)
                1 -> PlanetsTab(chart)
                2 -> AspectsTab(chart)
                3 -> ReadingTab(reading, chart, fullName, expander)
            }
        }
    }
}

@Composable
private fun WheelTab(chart: NatalChart, fullName: String) {
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        BirthDetailsHeader(chart, fullName)
        Spacer(Modifier.height(12.dp))
        ChartWheel(chart = chart)
        Spacer(Modifier.height(12.dp))
        ChartStatsRow(chart)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun PlanetsTab(chart: NatalChart) {
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)) {
        TableHeader(listOf("Body", "Sign", "Degree", "House"))
        chart.placements.forEach { p ->
            val deg = degreeFormat(p.degreeInSign)
            TableRow(listOf(
                "${p.body.glyph}  ${p.body.displayName}",
                "${p.sign.glyph} ${p.sign.displayName}",
                deg,
                if (p.body == Body.Ascendant || p.body == Body.Midheaven) "—" else "${p.house}"
            ))
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Birth time: ${chart.info.year}-${"%02d".format(chart.info.month)}-${"%02d".format(chart.info.day)} " +
                "${"%02d".format(chart.info.hour)}:${"%02d".format(chart.info.minute)} " +
                "(UTC ${if (chart.info.utcOffsetHours >= 0) "+" else ""}${chart.info.utcOffsetHours})",
            style = MaterialTheme.typography.bodyMedium, color = Sepia.copy(alpha = 0.7f),
        )
        Text(
            "Lat ${"%.3f".format(chart.info.latitude)}, Lon ${"%.3f".format(chart.info.longitude)}",
            style = MaterialTheme.typography.bodyMedium, color = Sepia.copy(alpha = 0.7f),
        )
        Spacer(Modifier.height(48.dp))
    }
}

private fun degreeFormat(d: Double): String {
    val deg = d.toInt()
    val minF = (d - deg) * 60.0
    val min = minF.toInt()
    return "${deg}° ${"%02d".format(min)}′"
}

@Composable
private fun AspectsTab(chart: NatalChart) {
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)) {
        TableHeader(listOf("Bodies", "Aspect", "Orb"))
        chart.aspects.forEach { a ->
            TableRow(listOf(
                "${a.a.glyph}  ${a.b.glyph}",
                "${a.type.symbol}  ${a.type.displayName}",
                "%.2f°".format(a.orb)
            ), color = aspectColor(a.type))
        }
        if (chart.aspects.isEmpty()) {
            Text("No aspects within standard orbs.", color = Bone.copy(alpha = 0.7f))
        }
        Spacer(Modifier.height(48.dp))
    }
}

private fun aspectColor(t: AspectType) = when (t) {
    AspectType.Conjunction -> Sepia
    AspectType.Opposition  -> FireCol
    AspectType.Trine       -> AirCol
    AspectType.Square      -> FireCol.copy(alpha = 0.85f)
    AspectType.Sextile     -> WaterCol
    AspectType.Quincunx    -> EarthCol
}

@Composable
private fun TableHeader(columns: List<String>) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(SmokeLight)
        .padding(vertical = 10.dp, horizontal = 8.dp)) {
        columns.forEach {
            Text(it.uppercase(), style = MaterialTheme.typography.labelLarge,
                color = Sepia, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun TableRow(columns: List<String>, color: androidx.compose.ui.graphics.Color = Bone) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp, horizontal = 8.dp)) {
        columns.forEach {
            Text(it, style = MaterialTheme.typography.bodyMedium,
                color = color, modifier = Modifier.weight(1f))
        }
    }
    HorizontalDivider(color = Brass.copy(alpha = 0.2f), thickness = 0.5.dp)
}

private sealed class AiState {
    object Idle : AiState()
    object Loading : AiState()
    data class Loaded(val text: String) : AiState()
    data class Error(val message: String) : AiState()
}

@Composable
private fun ReadingTab(
    reading: Reading,
    chart: NatalChart,
    userName: String,
    expander: ReadingExpander,
) {
    val scope = rememberCoroutineScope()
    val aiStates = remember { mutableStateMapOf<ReadingExpander.Section, AiState>() }

    fun fetch(section: ReadingExpander.Section) {
        if (!expander.available) return
        aiStates[section] = AiState.Loading
        scope.launch {
            val result = expander.expand(section, reading, chart, userName)
            aiStates[section] = when (result) {
                is GeminiClient.Result.Ok  -> AiState.Loaded(result.text.trim())
                is GeminiClient.Result.Err -> AiState.Error(result.message)
            }
        }
    }

    // Auto-trigger AI for all sections when this tab is first composed.
    LaunchedEffect(Unit) {
        if (expander.available) {
            ReadingExpander.Section.values().forEach { fetch(it) }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 12.dp)) {

        // Chart travels with the reading so the page is self-contained.
        BirthDetailsHeader(chart, userName)
        Spacer(Modifier.height(12.dp))
        ChartWheel(chart = chart)
        Spacer(Modifier.height(12.dp))
        ChartStatsRow(chart)
        Spacer(Modifier.height(20.dp))

        if (!expander.available) {
            Text(
                "AI deepening is off — add geminiApiKey to local.properties to enable.",
                style = MaterialTheme.typography.bodyMedium,
                color = Sepia.copy(alpha = 0.7f),
            )
            Spacer(Modifier.height(4.dp))
        }

        Section(reading, ReadingExpander.Section.Personality, aiStates, ::fetch, expander.available)
        Section(reading, ReadingExpander.Section.Mission,      aiStates, ::fetch, expander.available)
        Section(reading, ReadingExpander.Section.Relationships,aiStates, ::fetch, expander.available)
        Section(reading, ReadingExpander.Section.Money,        aiStates, ::fetch, expander.available)
        Section(reading, ReadingExpander.Section.Career,       aiStates, ::fetch, expander.available)
        Section(reading, ReadingExpander.Section.Health,       aiStates, ::fetch, expander.available)
        Section(reading, ReadingExpander.Section.Numerology,   aiStates, ::fetch, expander.available)
        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun Section(
    reading: Reading,
    section: ReadingExpander.Section,
    aiStates: Map<ReadingExpander.Section, AiState>,
    onRetry: (ReadingExpander.Section) -> Unit,
    aiAvailable: Boolean,
) {
    Spacer(Modifier.height(16.dp))
    Text(section.title.uppercase(), style = MaterialTheme.typography.headlineMedium, color = Sepia)
    Spacer(Modifier.height(4.dp))
    HorizontalDivider(color = Brass.copy(alpha = 0.6f), thickness = 0.8.dp)
    Spacer(Modifier.height(10.dp))
    Text(section.ruleText(reading), style = MaterialTheme.typography.bodyLarge, color = Bone)

    if (!aiAvailable) return
    val state = aiStates[section] ?: AiState.Idle
    Spacer(Modifier.height(12.dp))
    AiPanel(state = state, onRetry = { onRetry(section) })
}

@Composable
private fun AiPanel(state: AiState, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SmokeLight, androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
            .border(0.5.dp, Sepia.copy(alpha = 0.45f), androidx.compose.foundation.shape.RoundedCornerShape(2.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("DEEPENING", style = MaterialTheme.typography.labelLarge, color = Sepia)
                Spacer(Modifier.weight(1f))
                when (state) {
                    AiState.Loading -> CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Sepia,
                        strokeWidth = 1.5.dp,
                    )
                    is AiState.Error -> TextButton(onClick = onRetry) {
                        Text("Retry", color = Sepia)
                    }
                    else -> {}
                }
            }
            Spacer(Modifier.height(6.dp))
            when (state) {
                AiState.Idle, AiState.Loading -> Text(
                    "Consulting the wider sky…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Bone.copy(alpha = 0.7f),
                )
                is AiState.Loaded -> Text(
                    state.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Bone,
                )
                is AiState.Error -> Text(
                    "Couldn't reach the oracle (${state.message}).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Blood,
                )
            }
        }
    }
}
