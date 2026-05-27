package com.blindercosmology.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.blindercosmology.ui.theme.Brass
import com.blindercosmology.ui.theme.Sepia

@Composable
fun PaintingHeader(title: String, subtitle: String? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.displayMedium, textAlign = TextAlign.Center)
        Canvas(modifier = Modifier.fillMaxWidth().height(8.dp).padding(top = 4.dp)) {
            val w = size.width
            val y = size.height / 2
            // Art-deco style: two thin lines with a diamond in the middle
            drawLine(color = Brass, start = Offset(0f, y), end = Offset(w * 0.42f, y), strokeWidth = 1.5f)
            drawLine(color = Brass, start = Offset(w * 0.58f, y), end = Offset(w, y), strokeWidth = 1.5f)
            val cx = w / 2
            val d = 4f
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(cx, y - d); lineTo(cx + d, y); lineTo(cx, y + d); lineTo(cx - d, y); close()
            }
            drawPath(path, color = Sepia)
        }
        if (subtitle != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Sepia.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
            )
        }
    }
}
