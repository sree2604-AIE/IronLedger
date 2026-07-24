package com.ironledger.app.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironledger.app.core.design.AccountCard
import com.ironledger.app.core.design.IconBadge
import com.ironledger.app.core.design.IronColors
import com.ironledger.app.core.design.MetricTile
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.design.ProgressLine
import com.ironledger.app.core.design.SectionHeader
import com.ironledger.app.core.design.TransactionRow
import com.ironledger.app.core.design.categoryIcon
import com.ironledger.app.core.money.MoneyFormatter
import com.ironledger.app.domain.InsightTone
import com.ironledger.app.domain.TransactionType
import java.util.Calendar
import kotlin.math.sin

@Composable
fun HomeScreen(
    hideBalances: Boolean,
    onToggleHidden: (Boolean) -> Unit,
    onSeeActivity: () -> Unit,
    onSeeAnalytics: () -> Unit,
    onAddExpense: () -> Unit,
    onAddIncome: () -> Unit,
    onVoiceEntry: () -> Unit,
    onScanReceipt: () -> Unit,
    userName: String = "",
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, top = 22.dp, end = 20.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    val greeting = when {
                        hour < 12 -> "Good Morning,"
                        hour < 17 -> "Good Afternoon,"
                        else -> "Good Evening,"
                    }
                    val emoji = when {
                        hour < 12 -> "\uD83C\uDF05" // sunrise
                        hour < 17 -> "\u2600\uFE0F" // sun
                        else -> "\uD83C\uDF19"      // crescent moon
                    }
                    Text(greeting, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (userName.isNotBlank()) "$userName $emoji" else "IronLedger",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { /* Notification */ }) {
                        Icon(Icons.Rounded.Notifications, null, tint = Color.White)
                    }
                    IconButton(onClick = { onToggleHidden(!hideBalances) }) {
                        Icon(
                            if (hideBalances) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
        item { NetWorthCard(amountPaise = state.summary.totalBalancePaise, hidden = hideBalances, changePct = state.netWorthChangePct) }
        item {
            SectionHeader("Accounts", "See All", onSeeActivity)
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.accounts.take(4).chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { account ->
                            AccountCard(account = account, hidden = hideBalances, modifier = Modifier.weight(1f))
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
        item {
            SectionHeader("This Month Overview", "Analytics", onSeeAnalytics)
            Spacer(Modifier.height(12.dp))
            PremiumCard(contentPadding = 12.dp) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    MetricOverviewItem("Income", state.summary.monthlyIncomePaise, hideBalances, color = IronColors.AccentGreen)
                    MetricOverviewItem("Expense", state.summary.monthlyExpensePaise, hideBalances, color = IronColors.Negative)
                    MetricOverviewItem("Balance", state.summary.monthlyIncomePaise - state.summary.monthlyExpensePaise, hideBalances, color = IronColors.Blue)
                }
            }
        }
        item {
            SectionHeader("Quick Actions")
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                QuickActionItem("Add Expense", Icons.Rounded.Remove, IronColors.Negative, onAddExpense)
                QuickActionItem("Add Income", Icons.Rounded.Add, IronColors.AccentGreen, onAddIncome)
                QuickActionItem("Scan Receipt", Icons.Rounded.QrCodeScanner, IronColors.Blue, onScanReceipt)
                QuickActionItem("Voice Entry", Icons.Rounded.Mic, IronColors.Gold, onVoiceEntry)
            }
        }
        item { HealthCard(score = state.summary.financialHealthScore, ratio = state.summary.savingsRatioPercent) }
        item {
            SectionHeader("AI Financial Insights")
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                state.summary.insights.forEach { insight ->
                    PremiumCard(contentPadding = 14.dp) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val tint = when (insight.tone) {
                                InsightTone.GOOD -> MaterialTheme.colorScheme.primary
                                InsightTone.WATCH -> IronColors.Warning
                                InsightTone.INFO -> IronColors.Blue
                            }
                            IconBadge(icon = "income".categoryIcon(TransactionType.INCOME), tint = tint)
                            Spacer(Modifier.size(12.dp))
                            Column {
                                Text(insight.title, style = MaterialTheme.typography.labelLarge)
                                Text(insight.body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
        item {
            SectionHeader("Recent Transactions", "See All", onSeeActivity)
            Spacer(Modifier.height(8.dp))
            PremiumCard {
                Column {
                    state.summary.recentTransactions.forEach { transaction ->
                        TransactionRow(transaction = transaction, hidden = hideBalances)
                    }
                    if (state.summary.recentTransactions.isEmpty()) {
                        Text("No transactions yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        if (state.upcomingReminders.isNotEmpty()) {
            item {
                SectionHeader("Upcoming Reminders")
                Spacer(Modifier.height(12.dp))
                PremiumCard(contentPadding = 14.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        state.upcomingReminders.forEach { reminder ->
                            ReminderLine(
                                title = reminder.title,
                                date = formatReminderDate(reminder.dueAtEpochMillis),
                                amountPaise = reminder.amountPaise ?: 0L,
                                hidden = hideBalances
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NetWorthCard(amountPaise: Long, hidden: Boolean, changePct: Double?) {
    PremiumCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Total Net Worth", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(
                        MoneyFormatter.formatINR(amountPaise, hidden = hidden),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (changePct != null) {
                        val arrow = if (changePct >= 0) "▲" else "▼"
                        val pctStr = String.format("%.1f", Math.abs(changePct))
                        val changeColor = if (changePct >= 0) IronColors.AccentGreen else MaterialTheme.colorScheme.error
                        Text("$arrow $pctStr% vs last month", color = changeColor, style = MaterialTheme.typography.labelSmall)
                    } else {
                        Text("Track spending to see trends", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                    }
                }
                SparkLine(modifier = Modifier.size(width = 120.dp, height = 60.dp))
            }
        }
    }
}

@Composable
private fun SparkLine(modifier: Modifier = Modifier) {
    val color = IronColors.AccentGreen
    Canvas(modifier = modifier) {
        val points = (0..8).map { index ->
            val x = size.width * index / 8f
            val y = size.height * (0.6f - (index * 0.05f) + sin(index.toFloat() * 1.5f) * 0.2f)
            Offset(x, y)
        }
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(
            path = path,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
        )
        // Glow effect
        drawPath(
            path = path,
            color = color.copy(alpha = 0.3f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
        )
        points.lastOrNull()?.let {
            drawCircle(color, radius = 4.dp.toPx(), center = it)
        }
    }
}

@Composable
private fun QuickActionItem(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(IronColors.CardBorder)
                .padding(1.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(IronColors.CardBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun MetricOverviewItem(label: String, amountPaise: Long, hidden: Boolean, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(
            MoneyFormatter.formatINR(amountPaise, compact = true, hidden = hidden),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun HealthCard(score: Int, ratio: Int) {
    val accent = MaterialTheme.colorScheme.primary
    PremiumCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Financial Health", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(if (score >= 80) "Excellent" else if (score >= 65) "Stable" else "Needs Attention", style = MaterialTheme.typography.headlineMedium)
                Text("Savings ratio: $ratio%", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                ProgressLine(progress = ratio / 100f, color = MaterialTheme.colorScheme.primary)
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(92.dp)) {
                Canvas(modifier = Modifier.size(92.dp)) {
                    drawCircle(IronColors.Titanium.copy(alpha = 0.15f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx()))
                    drawArc(
                        brush = Brush.sweepGradient(listOf(accent, IronColors.Gold, accent)),
                        startAngle = -90f,
                        sweepAngle = 360f * score / 100f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(score.toString(), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                    Text("/100", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun ReminderLine(title: String, date: String, amountPaise: Long, hidden: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(date, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
        }
        Text(MoneyFormatter.formatINR(amountPaise, hidden = hidden), color = IronColors.Warning, style = MaterialTheme.typography.labelLarge)
    }
}

private fun formatReminderDate(epochMillis: Long): String {
    val diff = epochMillis - System.currentTimeMillis()
    val days = (diff / 86_400_000L).toInt()
    return when {
        days <= 0 -> "Due today"
        days == 1 -> "Tomorrow"
        days <= 7 -> "In $days days"
        else -> {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("d MMM")
            java.time.Instant.ofEpochMilli(epochMillis)
                .atZone(java.time.ZoneId.systemDefault())
                .format(formatter)
        }
    }
}
