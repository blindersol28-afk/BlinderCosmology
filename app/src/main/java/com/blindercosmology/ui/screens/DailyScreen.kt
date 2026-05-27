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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.blindercosmology.astro.ChartBuilder
import com.blindercosmology.data.ProfileEntity
import com.blindercosmology.data.toBirthInfo
import com.blindercosmology.interpret.DailyHoroscopeGenerator
import com.blindercosmology.ui.theme.*
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyScreen(
    profile: ProfileEntity?,
    onBack: () -> Unit,
    onSetupMainUser: () -> Unit,
) {
    Scaffold(
        containerColor = InkBlack,
        topBar = {
            TopAppBar(
                title = { Text("TODAY") },
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
        Column(modifier = Modifier
            .padding(pad)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)) {

            if (profile == null) {
                Text(
                    "Daily horoscope is for the main user. Set up your chart first.",
                    style = MaterialTheme.typography.bodyLarge, color = Bone,
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onSetupMainUser,
                    colors = ButtonDefaults.buttonColors(containerColor = Sepia, contentColor = InkBlack),
                    shape = RoundedCornerShape(2.dp),
                ) { Text("Set up main user") }
                return@Column
            }

            val today = LocalDate.now()
            val chart = remember(profile.id) { ChartBuilder.build(profile.toBirthInfo()) }
            val horoscope = remember(profile.id, today) {
                DailyHoroscopeGenerator.forToday(chart, today)
            }

            Text(horoscope.dateLabel, style = MaterialTheme.typography.labelLarge, color = Sepia)
            Spacer(Modifier.height(4.dp))
            Text("Hello, ${profile.fullName}.", style = MaterialTheme.typography.headlineLarge, color = Bone)

            Spacer(Modifier.height(24.dp))
            Card(
                title = "MOON IN ${horoscope.moonSign.displayName.uppercase()}",
                body = horoscope.moonSignNote,
            )
            Spacer(Modifier.height(12.dp))
            Card(
                title = "PERSONAL DAY ${horoscope.personalDayNumber}",
                body = horoscope.advice,
            )

            if (horoscope.highlights.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text("ACTIVE TRANSITS", style = MaterialTheme.typography.labelLarge, color = Sepia)
                Spacer(Modifier.height(8.dp))
                horoscope.highlights.forEach {
                    Text("• $it", style = MaterialTheme.typography.bodyMedium, color = Bone,
                        modifier = Modifier.padding(vertical = 4.dp))
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun Card(title: String, body: String) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Smoke, RoundedCornerShape(2.dp))
        .border(1.dp, Sepia.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
        .padding(16.dp)) {
        Column {
            Text(title, style = MaterialTheme.typography.labelLarge, color = Sepia)
            Spacer(Modifier.height(8.dp))
            Text(body, style = MaterialTheme.typography.bodyLarge, color = Bone)
        }
    }
}
