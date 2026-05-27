package com.blindercosmology.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.blindercosmology.data.ProfileEntity
import com.blindercosmology.ui.components.PaintingHeader
import com.blindercosmology.ui.theme.Bone
import com.blindercosmology.ui.theme.InkBlack
import com.blindercosmology.ui.theme.Sepia
import com.blindercosmology.ui.theme.Smoke

@Composable
fun HomeScreen(
    mainUser: ProfileEntity?,
    onOpenMainUserChart: () -> Unit,
    onSetupMainUser: () -> Unit,
    onGuestReading: () -> Unit,
    onOpenDaily: () -> Unit,
) {
    Surface(color = InkBlack, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 32.dp),
        ) {
            PaintingHeader(title = "BLINDER COSMOLOGY", subtitle = "by order of the stars")
            Spacer(Modifier.height(48.dp))

            if (mainUser != null) {
                NoirCard(
                    title = "YOUR CHART",
                    body = "${mainUser.fullName} · ${mainUser.placeLabel.ifBlank { "saved" }}",
                    cta = "Open natal chart",
                    onClick = onOpenMainUserChart,
                )
                Spacer(Modifier.height(16.dp))
                NoirCard(
                    title = "TODAY",
                    body = "Daily horoscope, transits, and advice",
                    cta = "Open today's reading",
                    onClick = onOpenDaily,
                )
                Spacer(Modifier.height(16.dp))
                NoirCard(
                    title = "ONE-OFF READING",
                    body = "Read for a friend or a new chart — nothing saved",
                    cta = "Start a guest reading",
                    onClick = onGuestReading,
                )
            } else {
                NoirCard(
                    title = "SET UP YOUR CHART",
                    body = "Enter your birth details once. We'll remember.",
                    cta = "Become the main user",
                    onClick = onSetupMainUser,
                )
                Spacer(Modifier.height(16.dp))
                NoirCard(
                    title = "ONE-OFF READING",
                    body = "Quick reading without saving",
                    cta = "Start a guest reading",
                    onClick = onGuestReading,
                )
            }

            Spacer(Modifier.weight(1f))
            Text(
                "Mathematics. Mythology. Mood.",
                style = MaterialTheme.typography.bodyMedium,
                color = Sepia.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun NoirCard(title: String, body: String, cta: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(2.dp))
            .background(Smoke)
            .border(width = 1.dp, color = Sepia.copy(alpha = 0.5f), shape = RoundedCornerShape(2.dp))
            .padding(20.dp),
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Text(body, style = MaterialTheme.typography.bodyMedium, color = Bone)
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Sepia, contentColor = InkBlack,
                ),
                shape = RoundedCornerShape(2.dp),
            ) { Text(cta) }
        }
    }
}
