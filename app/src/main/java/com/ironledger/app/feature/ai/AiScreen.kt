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
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironledger.app.core.design.IronColors
import com.ironledger.app.core.design.MetricTile
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.design.SectionHeader
import com.ironledger.app.core.money.MoneyFormatter
import com.ironledger.app.domain.DashboardSummary
import com.ironledger.app.domain.FinancialInsight
import com.ironledger.app.domain.InsightTone

@Composable
fun AiScreen(
    hideBalances: Boolean,
    viewModel: AiViewModel = hiltViewModel()
) {
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val healthLabel = when {
        summary.financialHealthScore >= 80 -> "Excellent"
        summary.financialHealthScore >= 60 -> "Good"
        summary.financialHealthScore >= 40 -> "Fair"
        else -> "Needs Attention"
    }
    val healthColor = when {
        summary.financialHealthScore >= 80 -> IronColors.AccentGreen
        summary.financialHealthScore >= 60 -> IronColors.Gold
        summary.financialHealthScore >= 40 -> IronColors.Warning
        else -> IronColors.TitaniumRed
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 22.dp, end = 20.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text("AI Insights", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text("Powered by your real spending data", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            PremiumCard {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Financial Health",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            healthLabel,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = healthColor
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Savings rate: ${summary.savingsRatioPercent}% · Top category: ${summary.topCategoryName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    HealthDial(score = summary.financialHealthScore, color = healthColor)
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricTile(
                    label = "Monthly Income",
                    amountPaise = summary.monthlyIncomePaise,
                    hidden = hideBalances,
                    positive = true,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    label = "Monthly Spend",
                    amountPaise = summary.monthlyExpensePaise,
                    hidden = hideBalances,
                    positive = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (summary.insights.isNotEmpty()) {
            item {
                SectionHeader(title = "Smart Insights")
            }
            summary.insights.forEach { insight ->
                item {
                    InsightCard(insight = insight)
                }
            }
        }

        item {
            SectionHeader(title = "Spending Summary")
        }

        item {
            PremiumCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SpendRow("Today", summary.todayExpensePaise, hideBalances, Icons.Rounded.WaterDrop)
                    SpendRow("This Week", summary.weeklyExpensePaise, hideBalances, Icons.Rounded.BarChart)
                    SpendRow("This Month", summary.monthlyExpensePaise, hideBalances, Icons.Rounded.TrendingDown)
                    SpendRow("This Year", summary.yearlyExpensePaise, hideBalances, Icons.Rounded.TrendingUp)
                }
            }
        }
    }
}

@Composable
private fun HealthDial(score: Int, color: Color) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
        androidx.compose.foundation.Canvas(Modifier.size(100.dp)) {
            drawCircle(
                IronColors.CardBorder,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 10.dp.toPx())
            )
            drawArc(
                brush = Brush.sweepGradient(listOf(color.copy(alpha = 0.5f), color, color)),
                startAngle = -90f,
                sweepAngle = 360f * score.coerceIn(0, 100) / 100f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                score.coerceIn(0, 100).toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text("/100", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun InsightCard(insight: FinancialInsight) {
    val (iconColor, icon) = when (insight.tone) {
        InsightTone.GOOD -> IronColors.AccentGreen to Icons.Rounded.TrendingUp
        InsightTone.WATCH -> IronColors.Gold to Icons.Rounded.AutoAwesome
        InsightTone.INFO -> IronColors.Blue to Icons.Rounded.Savings
    }
    PremiumCard(contentPadding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    insight.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
            Text(
                insight.body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun SpendRow(label: String, amountPaise: Long, hidden: Boolean, icon: ImageVector) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(10.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            MoneyFormatter.formatINR(amountPaise, hidden = hidden),
            style = MaterialTheme.typography.labelLarge,
            color = IronColors.Negative
        )
    }
}
