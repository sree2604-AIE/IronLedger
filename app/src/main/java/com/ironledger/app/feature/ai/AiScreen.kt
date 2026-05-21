package com.ironledger.app.feature.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironledger.app.core.design.IconBadge
import com.ironledger.app.core.design.IronColors
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.money.MoneyFormatter

@Composable
fun AiScreen(
    hideBalances: Boolean,
    viewModel: AiViewModel = hiltViewModel()
) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text("AI Insights", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        item {
            PremiumCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                        Text("Financial Health", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Excellent", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("You're doing great! Keep tracking to reach your goal.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    HealthDial(85)
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Insights for You", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                InsightPriorityCard("You spent 15% more on Food this month.", IronColors.TitaniumRed)
                InsightPriorityCard("Subscription waste detected. You can save up to ₹1,340 / month.", IronColors.Gold)
                InsightPriorityCard("Your savings rate is 32%. Good job for 35%.", IronColors.AccentGreen)
            }
        }
    }
}

@Composable
private fun HealthDial(score: Int) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(110.dp)) {
        androidx.compose.foundation.Canvas(Modifier.size(110.dp)) {
            drawCircle(IronColors.CardBorder, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 10.dp.toPx()))
            drawArc(
                brush = Brush.sweepGradient(listOf(IronColors.AccentGreen, IronColors.Gold, IronColors.AccentGreen)),
                startAngle = -90f,
                sweepAngle = 360f * score / 100f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(score.toString(), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Text("/100", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun InsightPriorityCard(text: String, iconColor: Color) {
    PremiumCard(contentPadding = 14.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(iconColor.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                androidx.compose.material3.Icon(Icons.Rounded.AutoAwesome, null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium, color = Color.White)
        }
    }
}
