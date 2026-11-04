package com.example.memoryleak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.memoryleak.model.MemoryLeakInfo
import com.example.memoryleak.ui.HomeScreen
import com.example.memoryleak.ui.LeakDetailScreen
import com.example.memoryleak.ui.theme.MemoryLeakTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoryLeakTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MemoryLeakApp()
                }
            }
        }
    }
}

@Composable
fun MemoryLeakApp() {
    var selectedLeak by remember { mutableStateOf<MemoryLeakInfo?>(null) }

    if (selectedLeak != null) {
        BackHandler {
            selectedLeak = null
        }
        LeakDetailScreen(
            leak = selectedLeak!!,
            onBack = { selectedLeak = null }
        )
    } else {
        HomeScreen(
            onLeakSelected = { leak ->
                selectedLeak = leak
            }
        )
    }
}
