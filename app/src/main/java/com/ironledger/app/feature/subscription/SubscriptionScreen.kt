package com.ironledger.app.feature.subscription

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
import androidx.compose.material.icons.rounded.Subscriptions
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
import com.ironledger.app.core.money.MoneyFormatter
import com.ironledger.app.domain.Subscription
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun SubscriptionScreen(
    hideBalances: Boolean,
    onBack: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val subscriptions by viewModel.subscriptions.collectAsStateWithLifecycle()
    val monthlyTotal = subscriptions.sumOf { if (it.billingCycle == "YEARLY") it.amountPaise / 12 else it.amountPaise }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add Subscription */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Rounded.Add, "Add Subscription")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, top = 22.dp, end = 20.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Subscriptions", style = MaterialTheme.typography.headlineMedium)
                Text("Manage recurring premium services", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            item {
                MetricTile("Total Monthly Commitment", monthlyTotal, hideBalances, positive = false)
            }

            items(subscriptions) { subscription ->
                SubscriptionCard(subscription = subscription, hideBalances = hideBalances)
            }
        }
    }
}

@Composable
private fun SubscriptionCard(subscription: Subscription, hideBalances: Boolean) {
    PremiumCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row {
                IconBadge(Icons.Rounded.Subscriptions, MaterialTheme.colorScheme.primary)
                Spacer(Modifier.padding(horizontal = 6.dp))
                Column {
                    Text(subscription.name, style = MaterialTheme.typography.labelLarge)
                    Text("Next: ${formatDate(subscription.nextBillingDateEpochMillis)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text(MoneyFormatter.formatINR(subscription.amountPaise, hidden = hideBalances), style = MaterialTheme.typography.labelLarge)
                Text(subscription.billingCycle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun formatDate(epoch: Long): String {
    val formatter = DateTimeFormatter.ofPattern("d MMM")
    return Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()).format(formatter)
}
