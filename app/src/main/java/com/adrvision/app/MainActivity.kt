package com.adrvision.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.adrvision.app.presentation.VisionScreen
import com.adrvision.app.presentation.VisionViewModel
import com.adrvision.app.ui.theme.AdrVisionTheme

class MainActivity : ComponentActivity() {

    private val viewModel: VisionViewModel by viewModels()
    private var hasCameraPermission by mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(
                this,
                "Ứng dụng cần quyền Camera để nhận diện và giải toán!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkAndRequestCameraPermission()

        setContent {
            AdrVisionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    if (hasCameraPermission) {
                        VisionScreen(viewModel = viewModel)
                    } else {
                        CameraPermissionDeniedContent(
                            onRequestPermission = { checkAndRequestCameraPermission() }
                        )
                    }
                }
            }
        }
    }

    private fun checkAndRequestCameraPermission() {
        val isGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        hasCameraPermission = isGranted
        if (!isGranted) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

@Composable
fun CameraPermissionDeniedContent(
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Yêu cầu quyền Camera",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Text(
                text = "ADR Vision cần quyền Camera để tự động quét giải toán và nhận diện vật thể.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
            ) {
                Text("Cấp Quyền Camera", color = Color.Black)
            }
        }
    }
}
