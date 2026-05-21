package com.ironledger.app.feature.reminder

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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.NotificationImportant
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.ironledger.app.core.money.MoneyFormatter
import com.ironledger.app.domain.Reminder
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ReminderScreen(
    hideBalances: Boolean,
    onBack: () -> Unit,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add Reminder */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Rounded.Add, "Add Reminder")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, top = 22.dp, end = 20.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Reminders", style = MaterialTheme.typography.headlineMedium)
                Text("Smart alerts for bills, EMIs, and renewals", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            items(reminders) { reminder ->
                ReminderCard(reminder = reminder, hideBalances = hideBalances, onToggle = { viewModel.markAsPaid(reminder) })
            }

            if (reminders.isEmpty()) {
                item {
                    EmptyState("No reminders", "Add bills or insurance dues to stay on top of your executive commitments.")
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(reminder: Reminder, hideBalances: Boolean, onToggle: () -> Unit) {
    PremiumCard(accent = if (reminder.isPaid) Color.Gray else MaterialTheme.colorScheme.primary) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                IconButton(onClick = onToggle) {
                    Icon(
                        if (reminder.isPaid) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (reminder.isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }
                Spacer(Modifier.size(8.dp))
                Column {
                    Text(
                        reminder.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (reminder.isPaid) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        formatDate(reminder.dueAtEpochMillis),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            reminder.amountPaise?.let {
                Text(
                    MoneyFormatter.formatINR(it, hidden = hideBalances),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (reminder.isPaid) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, body: String) {
    PremiumCard {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Rounded.NotificationImportant, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

private fun formatDate(epoch: Long): String {
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    return Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()).format(formatter)
}
