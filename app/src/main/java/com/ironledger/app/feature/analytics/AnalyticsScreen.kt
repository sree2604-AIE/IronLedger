package com.ironledger.app.feature.analytics

import android.graphics.Color as AndroidColor
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.ironledger.app.core.design.IronColors
import com.ironledger.app.core.design.MetricTile
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.design.ProgressLine
import com.ironledger.app.core.design.SectionHeader
import com.ironledger.app.core.design.SegmentedControl
import com.ironledger.app.core.money.MoneyFormatter
import com.ironledger.app.domain.AnalyticsSnapshot

@Composable
fun AnalyticsScreen(
    hideBalances: Boolean,
    onBack: () -> Unit = {},
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val snapshot by viewModel.snapshot.collectAsStateWithLifecycle()
    var tab by remember { mutableStateOf("Overview") }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 22.dp, end = 20.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, null, tint = Color.White) }
                Text("Analytics", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(14.dp))
            SegmentedControl(
                options = listOf("Overview", "Spending", "Income", "Trends"),
                selected = tab,
                onSelected = { tab = it }
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                MetricTile("Expense", snapshot.totalExpensePaise, hideBalances, Modifier.weight(1f), positive = false)
                MetricTile("Income", snapshot.totalIncomePaise, hideBalances, Modifier.weight(1f), positive = true)
            }
        }
        item {
            when (tab) {
                "Overview" -> OverviewAnalytics(snapshot, hideBalances)
                "Spending" -> SpendingAnalytics(snapshot, hideBalances)
                "Income" -> IncomeAnalytics(snapshot, hideBalances)
                else -> TrendAnalytics(snapshot, hideBalances)
            }
        }
    }
}

@Composable
private fun OverviewAnalytics(snapshot: AnalyticsSnapshot, hidden: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PremiumCard {
            SectionHeader("Spending Overview")
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                PieChartView(snapshot, modifier = Modifier.size(160.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f).padding(start = 20.dp)) {
                    val totalExp = snapshot.totalExpensePaise.takeIf { it > 0 } ?: 1L
                    val legendColors = listOf(IronColors.Blue, IronColors.AccentGreen, IronColors.Gold, IronColors.Warning)
                    snapshot.topCategories.take(4).forEachIndexed { i, cat ->
                        val pct = (cat.amountPaise * 100 / totalExp).toInt()
                        CategoryLegendItem(cat.categoryName, "$pct%", legendColors.getOrElse(i) { IronColors.Blue })
                    }
                    if (snapshot.topCategories.isEmpty()) {
                        CategoryLegendItem("No data", "0%", IronColors.Blue)
                    }
                }
            }
        }
        PremiumCard {
            SectionHeader("Spending Trend")
            Spacer(Modifier.height(12.dp))
            LineChartView(snapshot)
        }
        CategoryList(snapshot, hidden)
    }
}

@Composable
private fun CategoryLegendItem(label: String, percent: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(color))
            Spacer(Modifier.size(8.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(percent, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SpendingAnalytics(snapshot: AnalyticsSnapshot, hidden: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CategoryList(snapshot, hidden)
        PremiumCard {
            SectionHeader("Payment Methods")
            Spacer(Modifier.height(12.dp))
            snapshot.paymentMethods.forEach { method ->
                val max = snapshot.paymentMethods.maxOfOrNull { it.amountPaise }?.coerceAtLeast(1L) ?: 1L
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(method.paymentMethod.name.replace("_", " "), style = MaterialTheme.typography.labelLarge)
                    Text(MoneyFormatter.formatINR(method.amountPaise, hidden = hidden), color = MaterialTheme.colorScheme.primary)
                }
                ProgressLine(progress = method.amountPaise.toFloat() / max, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun IncomeAnalytics(snapshot: AnalyticsSnapshot, hidden: Boolean) {
    PremiumCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Income vs Expense", style = MaterialTheme.typography.titleMedium)
            Text("Savings ratio: ${snapshot.savingsRatioPercent}%", color = MaterialTheme.colorScheme.onSurfaceVariant)
            ProgressLine(progress = snapshot.savingsRatioPercent / 100f, color = MaterialTheme.colorScheme.primary)
            Text("Recorded income: ${MoneyFormatter.formatINR(snapshot.totalIncomePaise, hidden = hidden)}")
            Text("Recorded expense: ${MoneyFormatter.formatINR(snapshot.totalExpensePaise, hidden = hidden)}", color = IronColors.Negative)
        }
    }
}

@Composable
private fun TrendAnalytics(snapshot: AnalyticsSnapshot, hidden: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PremiumCard {
            SectionHeader("Daily Spend")
            Spacer(Modifier.height(12.dp))
            BarChartView(snapshot)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            PremiumCard(modifier = Modifier.weight(1f)) {
                Text("Daily Average", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(MoneyFormatter.formatINR(snapshot.averageDailySpendPaise, compact = true, hidden = hidden), style = MaterialTheme.typography.titleLarge)
            }
            PremiumCard(modifier = Modifier.weight(1f)) {
                Text("Predicted Month End", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(MoneyFormatter.formatINR(snapshot.predictedMonthEndPaise, compact = true, hidden = hidden), style = MaterialTheme.typography.titleLarge, color = IronColors.Warning)
            }
        }
    }
}

@Composable
private fun CategoryList(snapshot: AnalyticsSnapshot, hidden: Boolean) {
    PremiumCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader("Top Categories")
            val max = snapshot.topCategories.maxOfOrNull { it.amountPaise }?.coerceAtLeast(1L) ?: 1L
            snapshot.topCategories.forEach { item ->
                val color = item.accentHex.toAndroidColor(MaterialTheme.colorScheme.primary.toArgb())
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(item.categoryName, style = MaterialTheme.typography.labelLarge)
                    Text(MoneyFormatter.formatINR(item.amountPaise, hidden = hidden), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                ProgressLine(progress = item.amountPaise.toFloat() / max, color = androidx.compose.ui.graphics.Color(color))
            }
            if (snapshot.topCategories.isEmpty()) {
                Text("No expense data for this month yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PieChartView(snapshot: AnalyticsSnapshot, modifier: Modifier = Modifier) {
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val fallback = MaterialTheme.colorScheme.primary.toArgb()
    AndroidView(
        modifier = modifier.height(260.dp),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                setUsePercentValues(false)
                setHoleColor(AndroidColor.TRANSPARENT)
                setTransparentCircleAlpha(0)
                holeRadius = 62f
                legend.textColor = textColor
                setEntryLabelColor(textColor)
                isRotationEnabled = false
            }
        },
        update = { chart ->
            val entries = snapshot.topCategories.map { PieEntry((it.amountPaise / 100f).coerceAtLeast(1f), it.categoryName) }
            val colors = snapshot.topCategories.map { it.accentHex.toAndroidColor(fallback) }.ifEmpty { listOf(fallback) }
            val dataSet = PieDataSet(entries.ifEmpty { listOf(PieEntry(1f, "No data")) }, "").apply {
                this.colors = colors
                valueTextColor = textColor
                valueTextSize = 11f
                sliceSpace = 3f
            }
            chart.centerText = MoneyFormatter.formatINR(snapshot.totalExpensePaise, compact = true)
            chart.setCenterTextColor(textColor)
            chart.data = PieData(dataSet)
            chart.invalidate()
        }
    )
}

@Composable
private fun LineChartView(snapshot: AnalyticsSnapshot) {
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val accent = MaterialTheme.colorScheme.primary.toArgb()
    AndroidView(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                axisRight.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.textColor = textColor
                axisLeft.textColor = textColor
                setTouchEnabled(false)
            }
        },
        update = { chart ->
            val entries = snapshot.dailyTrend.mapIndexed { index, point -> Entry(index.toFloat(), point.amountPaise / 100f) }
            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String =
                    snapshot.dailyTrend.getOrNull(value.toInt())?.label ?: ""
            }
            val dataSet = LineDataSet(entries, "Spending").apply {
                color = accent
                setCircleColor(accent)
                lineWidth = 3f
                circleRadius = 4f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                valueTextColor = textColor
            }
            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}

@Composable
private fun BarChartView(snapshot: AnalyticsSnapshot) {
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val accent = MaterialTheme.colorScheme.primary.toArgb()
    AndroidView(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                axisRight.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.textColor = textColor
                axisLeft.textColor = textColor
                setTouchEnabled(false)
            }
        },
        update = { chart ->
            val entries = snapshot.dailyTrend.mapIndexed { index, point -> BarEntry(index.toFloat(), point.amountPaise / 100f) }
            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String =
                    snapshot.dailyTrend.getOrNull(value.toInt())?.label ?: ""
            }
            chart.data = BarData(BarDataSet(entries, "Daily").apply {
                color = accent
                valueTextColor = textColor
            })
            chart.invalidate()
        }
    )
}

private fun String.toAndroidColor(fallback: Int): Int = runCatching { AndroidColor.parseColor(this) }.getOrDefault(fallback)
