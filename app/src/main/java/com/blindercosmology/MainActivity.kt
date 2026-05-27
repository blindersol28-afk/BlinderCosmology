package com.blindercosmology

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.blindercosmology.ui.nav.BlinderNavHost
import com.blindercosmology.ui.theme.BlinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            BlinderTheme {
                BlinderNavHost(repo = (application as BlinderApp).repo)
            }
        }
    }
}
