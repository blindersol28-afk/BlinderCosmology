package com.blindercosmology.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.blindercosmology.astro.BirthInfo
import com.blindercosmology.data.ProfileRepository
import com.blindercosmology.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    repo: ProfileRepository,
    isMainUser: Boolean,
    onChartReady: (name: String, info: BirthInfo) -> Unit,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }
    var tz by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("") }
    var lng by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = InkBlack,
        topBar = {
            TopAppBar(
                title = { Text(if (isMainUser) "MAIN USER" else "GUEST READING") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Sepia)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = InkBlack, titleContentColor = Sepia
                ),
            )
        },
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                if (isMainUser) "Enter once. We'll keep the chart for daily readings."
                else "Enter the birth details — nothing saved.",
                style = MaterialTheme.typography.bodyMedium,
                color = Sepia.copy(alpha = 0.8f),
            )
            Spacer(Modifier.height(16.dp))

            BlinderField("Full name", name, KeyboardType.Text) { name = it }
            Spacer(Modifier.height(8.dp))

            Row {
                BlinderField("Year (e.g. 1990)", year, KeyboardType.Number, modifier = Modifier.weight(1f)) { year = it }
                Spacer(Modifier.width(8.dp))
                BlinderField("Month (1-12)", month, KeyboardType.Number, modifier = Modifier.weight(1f)) { month = it }
                Spacer(Modifier.width(8.dp))
                BlinderField("Day (1-31)", day, KeyboardType.Number, modifier = Modifier.weight(1f)) { day = it }
            }
            Spacer(Modifier.height(8.dp))

            Row {
                BlinderField("Hour (0-23)", hour, KeyboardType.Number, modifier = Modifier.weight(1f)) { hour = it }
                Spacer(Modifier.width(8.dp))
                BlinderField("Minute (0-59)", minute, KeyboardType.Number, modifier = Modifier.weight(1f)) { minute = it }
                Spacer(Modifier.width(8.dp))
                BlinderField("UTC offset (e.g. 3)", tz, KeyboardType.Number, modifier = Modifier.weight(1f)) { tz = it }
            }
            Spacer(Modifier.height(8.dp))

            Row {
                BlinderField("Latitude (+N)", lat, KeyboardType.Number, modifier = Modifier.weight(1f)) { lat = it }
                Spacer(Modifier.width(8.dp))
                BlinderField("Longitude (+E)", lng, KeyboardType.Number, modifier = Modifier.weight(1f)) { lng = it }
            }
            Spacer(Modifier.height(8.dp))
            BlinderField("Place label (optional)", place, KeyboardType.Text) { place = it }

            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = Blood, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val info = parse(year, month, day, hour, minute, tz, lat, lng, place)
                    if (info == null) {
                        error = "Some fields are missing or invalid."
                        return@Button
                    }
                    if (isMainUser) {
                        scope.launch { repo.saveMainUser(name.ifBlank { "You" }, info) }
                    }
                    onChartReady(name.ifBlank { "Guest" }, info)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Sepia, contentColor = InkBlack),
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (isMainUser) "SAVE & CAST CHART" else "CAST CHART")
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlinderField(
    label: String,
    value: String,
    keyboard: KeyboardType,
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Sepia,
            unfocusedBorderColor = Brass.copy(alpha = 0.5f),
            focusedLabelColor = Sepia,
            unfocusedLabelColor = Brass.copy(alpha = 0.7f),
            focusedTextColor = Bone,
            unfocusedTextColor = Bone,
            cursorColor = Sepia,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}

private fun parse(
    year: String, month: String, day: String,
    hour: String, minute: String, tz: String,
    lat: String, lng: String, place: String,
): BirthInfo? {
    val y = year.toIntOrNull() ?: return null
    val mo = month.toIntOrNull() ?: return null
    val d = day.toIntOrNull() ?: return null
    val hr = hour.toIntOrNull() ?: return null
    val mn = minute.toIntOrNull() ?: return null
    val t = tz.toDoubleOrNull() ?: return null
    val la = lat.toDoubleOrNull() ?: return null
    val lo = lng.toDoubleOrNull() ?: return null
    if (mo !in 1..12 || d !in 1..31 || hr !in 0..23 || mn !in 0..59) return null
    if (la !in -90.0..90.0 || lo !in -180.0..180.0) return null
    return BirthInfo(y, mo, d, hr, mn, t, la, lo, place)
}
