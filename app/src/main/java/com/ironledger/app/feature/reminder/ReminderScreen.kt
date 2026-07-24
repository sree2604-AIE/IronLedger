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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.NotificationImportant
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironledger.app.core.design.IronColors
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.design.SegmentedControl
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
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, type, amount, dueMillis ->
                viewModel.addReminder(title, type, amount, dueMillis)
                showAddDialog = false
            }
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
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
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long?, Long) -> Unit
) {
    val reminderTypes = listOf("EB_BILL", "MOBILE", "INSURANCE", "EMI", "CREDIT_CARD", "SUBSCRIPTION")
    val typeLabels = listOf("EB Bill", "Mobile", "Insurance", "EMI", "Credit Card", "Subscription")

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("EB_BILL") }
    var dueDays by remember { mutableStateOf("7") } // days from now

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = IronColors.CardBg,
        title = { Text("Add Reminder", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("e.g. Electricity Bill") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = IronColors.CardBorder
                    )
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Amount (INR, optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = IronColors.CardBorder
                    )
                )
                OutlinedTextField(
                    value = dueDays,
                    onValueChange = { dueDays = it.filter { c -> c.isDigit() } },
                    label = { Text("Due in (days)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = IronColors.CardBorder
                    )
                )
                Text("Category", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                SegmentedControl(
                    options = typeLabels.take(3), // Show first 3 in row
                    selected = typeLabels.getOrElse(reminderTypes.indexOf(selectedType)) { "EB Bill" },
                    onSelected = { label ->
                        val idx = typeLabels.indexOf(label)
                        if (idx >= 0) selectedType = reminderTypes[idx]
                    }
                )
                SegmentedControl(
                    options = typeLabels.drop(3), // Show last 3 in row
                    selected = typeLabels.getOrElse(reminderTypes.indexOf(selectedType)) { "EB Bill" },
                    onSelected = { label ->
                        val idx = typeLabels.indexOf(label)
                        if (idx >= 0) selectedType = reminderTypes[idx]
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val amountPaise = if (amount.isNotBlank()) ((amount.toDoubleOrNull() ?: 0.0) * 100).toLong().takeIf { it > 0 } else null
                        val daysFromNow = dueDays.toIntOrNull() ?: 7
                        val dueMillis = System.currentTimeMillis() + daysFromNow.toLong() * 24 * 60 * 60 * 1000
                        onConfirm(title, selectedType, amountPaise, dueMillis)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Rounded.NotificationImportant, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

private fun formatDate(epoch: Long): String {
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    return Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()).format(formatter)
}
