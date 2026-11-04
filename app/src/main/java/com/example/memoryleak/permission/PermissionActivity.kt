package com.example.memoryleak.permission

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.memoryleak.ui.theme.MemoryLeakTheme
import kotlinx.coroutines.launch

/**
 * [PermissionActivity] demonstrates requesting permissions sequentially using [RotatablePermissionHelper]
 * and [PermissionBridge] across Activity lifecycle transitions and screen rotations without memory leaks.
 */
class PermissionActivity : ComponentActivity() {

    private lateinit var permissionHelper: RotatablePermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize helper attached to Activity lifecycle
        permissionHelper = RotatablePermissionHelper(this)

        setContent {
            MemoryLeakTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val scope = rememberCoroutineScope()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Permission Request (Rotation & Leak Safe)",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Demonstrates requesting runtime permissions using flow-based channel bridge. Safe against activity leaks and double dialog spawns during rotation."
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = {
                            scope.launch {
                                val granted = permissionHelper.requestPermission(Manifest.permission.CAMERA)
                                val status = if (granted) "Camera Permission Granted" else "Camera Permission Denied"
                                Toast.makeText(this@PermissionActivity, status, Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text("Request Camera Permission")
                        }
                    }
                }
            }
        }
    }
}
