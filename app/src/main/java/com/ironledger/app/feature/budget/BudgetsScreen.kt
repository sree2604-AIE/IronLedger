package com.ironledger.app.feature.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironledger.app.core.design.IronColors
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.design.ProgressLine
import com.ironledger.app.core.money.MoneyFormatter

@Composable
fun BudgetsScreen(
    hideBalances: Boolean,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add Budget */ },
                containerColor = IronColors.AccentGreen,
                contentColor = Color.Black
            ) {
                Icon(Icons.Rounded.Add, "Add Budget")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column {
                    Text("Budgets", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Overall Budget", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        if (hideBalances) "₹ 60,000 / ₹ 80,000" else "₹ 60,000 / ₹ 80,000",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    ProgressLine(progress = 0.75f, color = IronColors.AccentGreen)
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    BudgetItem("Housing", 18_000_00, 20_000_00, 0.90f, IronColors.Blue)
                    BudgetItem("Food", 8_500_00, 10_000_00, 0.85f, IronColors.Gold)
                    BudgetItem("Transport", 4_200_00, 8_000_00, 0.52f, IronColors.Emerald)
                    BudgetItem("Shopping", 4_800_00, 6_000_00, 0.80f, IronColors.Warning)
                    BudgetItem("Entertainment", 3_300_00, 4_000_00, 0.82f, IronColors.TitaniumRed)
                }
            }
        }
    }
}

@Composable
private fun BudgetItem(label: String, spent: Long, budget: Long, progress: Float, color: Color) {
    PremiumCard(contentPadding = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = color)
            }
            ProgressLine(progress = progress, color = color)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(MoneyFormatter.formatINR(spent, compact = true), style = MaterialTheme.typography.labelSmall)
                Text(MoneyFormatter.formatINR(budget, compact = true), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
