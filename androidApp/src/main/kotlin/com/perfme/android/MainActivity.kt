package com.perfme.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.perfme.android.ui.navigation.perfMeNavigation
import com.perfme.android.ui.theme.perfMeTheme

/**
 * Main activity for PerfMe Android app
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            perfMeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    perfMeNavigation(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
