package com.ironledger.app.feature.emi

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironledger.app.core.design.IconBadge
import com.ironledger.app.core.design.MetricTile
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.design.ProgressLine
import com.ironledger.app.core.money.MoneyFormatter
import com.ironledger.app.domain.Emi

@Composable
fun EmiScreen(
    hideBalances: Boolean,
    onBack: () -> Unit,
    viewModel: EmiViewModel = hiltViewModel()
) {
    val emis by viewModel.emis.collectAsStateWithLifecycle()
    val totalMonthlyEmi = emis.sumOf { it.monthlyAmountPaise }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add EMI */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Rounded.Add, "Add EMI")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, top = 22.dp, end = 20.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("EMI Tracker", style = MaterialTheme.typography.headlineMedium)
                Text("Track loans and monthly commitments", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            item {
                MetricTile("Total EMI Commitment", totalMonthlyEmi, hideBalances, positive = false)
            }

            items(emis) { emi ->
                EmiCard(emi = emi, hideBalances = hideBalances)
            }
        }
    }
}

@Composable
private fun EmiCard(emi: Emi, hideBalances: Boolean) {
    val progress = (emi.totalTenureMonths - emi.remainingTenureMonths).toFloat() / emi.totalTenureMonths
    PremiumCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    IconBadge(Icons.Rounded.CreditCard, MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.padding(horizontal = 6.dp))
                    Column {
                        Text(emi.loanName, style = MaterialTheme.typography.labelLarge)
                        Text("${emi.remainingTenureMonths} months remaining", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Text(MoneyFormatter.formatINR(emi.monthlyAmountPaise, hidden = hideBalances), style = MaterialTheme.typography.labelLarge)
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tenure Progress", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
                ProgressLine(progress = progress, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
