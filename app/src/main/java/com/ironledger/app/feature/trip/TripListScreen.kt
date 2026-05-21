package com.ironledger.app.feature.trip

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironledger.app.core.design.IconBadge
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.design.ProgressLine
import com.ironledger.app.core.money.MoneyFormatter
import com.ironledger.app.domain.Trip
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun TripListScreen(
    hideBalances: Boolean,
    onBack: () -> Unit,
    viewModel: TripViewModel = hiltViewModel()
) {
    val trips by viewModel.trips.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add Trip */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Rounded.Add, "Add Trip")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, top = 22.dp, end = 20.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Text("Trips", style = MaterialTheme.typography.headlineMedium)
                Text("Executive travel and group expense management", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            items(trips) { trip ->
                TripCard(trip = trip, hideBalances = hideBalances)
            }

            if (trips.isEmpty()) {
                item {
                    PremiumCard {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Rounded.Hotel, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(12.dp))
                            Text("No trips recorded yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TripCard(trip: Trip, hideBalances: Boolean) {
    val progress = if (trip.budgetPaise > 0) trip.totalSpentPaise.toFloat() / trip.budgetPaise else 0f
    PremiumCard {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBadge(Icons.Rounded.Hotel, MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.size(12.dp))
                    Column {
                        Text(trip.name, style = MaterialTheme.typography.titleLarge)
                        Text(formatDate(trip.startDateEpochMillis), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Icon(Icons.Rounded.Group, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Budget Progress", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (progress > 1f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
                ProgressLine(progress = progress, color = if (progress > 1f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Spent", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(MoneyFormatter.formatINR(trip.totalSpentPaise, hidden = hideBalances), style = MaterialTheme.typography.labelLarge)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Budget", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(MoneyFormatter.formatINR(trip.budgetPaise, hidden = hideBalances), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

private fun formatDate(epoch: Long): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    return Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()).format(formatter)
}
