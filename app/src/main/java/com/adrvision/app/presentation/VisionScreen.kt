package com.adrvision.app.presentation

import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun VisionScreen(viewModel: VisionViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = if (state.isFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    controller = cameraController
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay Bounding Boxes & HUD
        Canvas(modifier = Modifier.fillMaxSize()) {
            state.objects.forEach { obj ->
                drawRect(
                    color = Color(obj.color),
                    topLeft = androidx.compose.ui.geometry.Offset(obj.boundingBox.left, obj.boundingBox.top),
                    size = androidx.compose.ui.geometry.Size(obj.boundingBox.width(), obj.boundingBox.height()),
                    style = Stroke(width = 4f)
                )
            }
        }

        // Top Telemetry Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "⚡ ${state.fps} FPS • ${state.inferenceTimeMs}ms",
                    color = Color(0xFF00E5FF),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "ADR Vision • by ductri (0826130621)",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
