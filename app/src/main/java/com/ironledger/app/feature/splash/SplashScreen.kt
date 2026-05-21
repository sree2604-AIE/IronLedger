package com.ironledger.app.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ironledger.app.R
import com.ironledger.app.core.design.IronColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.iron_pulse))
    animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)
    val alpha = androidx.compose.runtime.remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(700))
        delay(900)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(IronColors.Amoled, IronColors.Graphite, IronColors.Amoled)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(composition = composition, iterations = LottieConstants.IterateForever, modifier = Modifier.size(260.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            IronMark(modifier = Modifier.size(96.dp))
            Spacer(Modifier.height(22.dp))
            Text("IronLedger", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text("Manage. Track. Grow.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun IronMark(modifier: Modifier = Modifier) {
    val accent = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier) {
        val shield = Path().apply {
            moveTo(size.width / 2f, 0f)
            lineTo(size.width, size.height * 0.22f)
            lineTo(size.width * 0.86f, size.height * 0.82f)
            lineTo(size.width / 2f, size.height)
            lineTo(size.width * 0.14f, size.height * 0.82f)
            lineTo(0f, size.height * 0.22f)
            close()
        }
        drawPath(shield, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.18f), accent.copy(alpha = 0.18f))))
        drawPath(shield, color = accent, style = Stroke(width = 3.dp.toPx()))
        drawLine(accent, Offset(size.width * 0.34f, size.height * 0.28f), Offset(size.width * 0.34f, size.height * 0.72f), strokeWidth = 4.dp.toPx())
        drawLine(accent, Offset(size.width * 0.66f, size.height * 0.28f), Offset(size.width * 0.66f, size.height * 0.72f), strokeWidth = 4.dp.toPx())
        drawLine(IronColors.Silver, Offset(size.width * 0.5f, size.height * 0.22f), Offset(size.width * 0.5f, size.height * 0.78f), strokeWidth = 5.dp.toPx())
    }
}
