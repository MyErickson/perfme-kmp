package com.perfme.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.perfme.android.ui.navigation.PerfMeNavigation
import com.perfme.android.ui.theme.PerfMeTheme

/**
 * Main activity for PerfMe Android app
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PerfMeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PerfMeNavigation(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}