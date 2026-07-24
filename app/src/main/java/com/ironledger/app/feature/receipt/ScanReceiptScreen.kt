package com.ironledger.app.feature.receipt

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ironledger.app.core.design.IronColors
import com.ironledger.app.core.design.SegmentedControl

@Composable
fun ScanReceiptScreen(
    onBack: () -> Unit
) {
    var flashOn by remember { mutableStateOf(false) }
    var captureMode by remember { mutableStateOf(true) }   // true = camera view, false = review form
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food & Dining") }

    val categories = listOf("Food & Dining", "Shopping", "Transport", "Fuel", "Medical", "Bills")

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 20.dp, top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, null, tint = Color.White) }
                Text("Scan Receipt", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                IconButton(onClick = { flashOn = !flashOn }) {
                    Icon(if (flashOn) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff, null, tint = if (flashOn) IronColors.Gold else Color.White)
                }
            }
        },
        bottomBar = {
            if (captureMode) {
                CaptureControls(
                    onGallery = { captureMode = false },
                    onCapture = { captureMode = false; amount = ""; category = "Food & Dining" }
                )
            } else {
                ReviewForm(
                    amount = amount,
                    category = category,
                    categories = categories,
                    onAmountChange = { amount = it },
                    onCategoryChange = { category = it },
                    onSave = onBack,   // In a real implementation, this would navigate to AddTransactionScreen pre-filled
                    onRescan = { captureMode = true }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (captureMode) {
                CameraViewfinder()
            } else {
                // Simulated scanned receipt background
                Box(
                    Modifier.fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Black, IronColors.CardBg)))
                )
            }
        }
    }
}

@Composable
private fun CameraViewfinder() {
    val transition = rememberInfiniteTransition(label = "scan")
    val scanY by transition.animateFloat(
        initialValue = -200f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.82f)
            .height(440.dp)
            .border(2.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Animated scan line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .offset(y = scanY.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, IronColors.AccentGreen, Color.Transparent)
                    )
                )
        )
        // Corner brackets
        CornerBrackets()

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Rounded.CameraAlt, null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(40.dp))
            Spacer(Modifier.height(12.dp))
            Text(
                "Align receipt within frame",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CornerBrackets() {
    val cornerColor = IronColors.AccentGreen
    val size = 20.dp
    val thickness = 3.dp
    Box(Modifier.fillMaxSize()) {
        // Top-left
        Box(Modifier.align(Alignment.TopStart).padding(4.dp)) {
            Box(Modifier.width(size).height(thickness).background(cornerColor).align(Alignment.TopStart))
            Box(Modifier.width(thickness).height(size).background(cornerColor).align(Alignment.TopStart))
        }
        // Top-right
        Box(Modifier.align(Alignment.TopEnd).padding(4.dp)) {
            Box(Modifier.width(size).height(thickness).background(cornerColor).align(Alignment.TopEnd))
            Box(Modifier.width(thickness).height(size).background(cornerColor).align(Alignment.TopEnd))
        }
        // Bottom-left
        Box(Modifier.align(Alignment.BottomStart).padding(4.dp)) {
            Box(Modifier.width(size).height(thickness).background(cornerColor).align(Alignment.BottomStart))
            Box(Modifier.width(thickness).height(size).background(cornerColor).align(Alignment.BottomStart))
        }
        // Bottom-right
        Box(Modifier.align(Alignment.BottomEnd).padding(4.dp)) {
            Box(Modifier.width(size).height(thickness).background(cornerColor).align(Alignment.BottomEnd))
            Box(Modifier.width(thickness).height(size).background(cornerColor).align(Alignment.BottomEnd))
        }
    }
}

@Composable
private fun CaptureControls(onGallery: () -> Unit, onCapture: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 40.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onGallery, modifier = Modifier.size(48.dp)) {
            Icon(Icons.Rounded.PhotoLibrary, null, tint = Color.White, modifier = Modifier.size(28.dp))
        }
        // Shutter button
        Box(
            modifier = Modifier
                .size(72.dp)
                .border(4.dp, Color.White, CircleShape)
                .padding(6.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onCapture),
            contentAlignment = Alignment.Center
        ) {}
        Box(Modifier.size(48.dp))
    }
}

@Composable
private fun ReviewForm(
    amount: String,
    category: String,
    categories: List<String>,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSave: () -> Unit,
    onRescan: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Review Scanned Expense", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))

        OutlinedTextField(
            value = amount,
            onValueChange = { onAmountChange(it.filter { c -> c.isDigit() || c == '.' }) },
            label = { Text("Amount (₹)", color = Color.White.copy(alpha = 0.7f)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = MaterialTheme.typography.headlineMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IronColors.AccentGreen,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                cursorColor = IronColors.AccentGreen
            )
        )

        SegmentedControl(
            options = categories.take(3),
            selected = category,
            onSelected = onCategoryChange
        )
        SegmentedControl(
            options = categories.drop(3),
            selected = category,
            onSelected = onCategoryChange
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onRescan,
                modifier = Modifier.weight(1f).height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IronColors.CardBorder),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Re-scan", color = Color.White) }

            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f).height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IronColors.AccentGreen),
                shape = RoundedCornerShape(16.dp),
                enabled = amount.isNotBlank()
            ) { Text("Save Expense", color = Color.Black, fontWeight = FontWeight.Bold) }
        }
    }
}
