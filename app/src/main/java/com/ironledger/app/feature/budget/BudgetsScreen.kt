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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironledger.app.core.design.IronColors
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.design.ProgressLine
import com.ironledger.app.core.money.MoneyFormatter

@Composable
fun BudgetsScreen(
    hideBalances: Boolean,
    onBack: () -> Unit,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val budgets: List<BudgetUiItem> by viewModel.budgets.collectAsStateWithLifecycle()
    val totalBudget = budgets.sumOf { it.budgetPaise }
    val totalSpent = budgets.sumOf { it.spentPaise }
    val overallProgress = if (totalBudget > 0L) totalSpent.toFloat() / totalBudget else 0f

    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddBudgetDialog(
            categories = budgets.map { it.categoryName }.distinct(),
            onDismiss = { showAddDialog = false },
            onConfirm = { categoryName, amountPaise ->
                viewModel.addBudget(categoryName, amountPaise)
                showAddDialog = false
            }
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 20.dp, top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color.White) }
                Text("Budgets", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
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
                    Text("Overall Budget", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        MoneyFormatter.formatINR(totalSpent, hidden = hideBalances) + " / " + MoneyFormatter.formatINR(totalBudget, hidden = hideBalances),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    ProgressLine(progress = overallProgress, color = IronColors.AccentGreen)
                }
            }

            items(budgets) { budget ->
                BudgetItem(
                    label = budget.categoryName,
                    spent = budget.spentPaise,
                    budget = budget.budgetPaise,
                    hidden = hideBalances,
                    progress = if (budget.budgetPaise > 0) budget.spentPaise.toFloat() / budget.budgetPaise else 0f,
                    color = try { Color(android.graphics.Color.parseColor(budget.accentHex)) } catch (e: Exception) { MaterialTheme.colorScheme.primary }
                )
            }
        }
    }
}

@Composable
private fun AddBudgetDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, Long) -> Unit
) {
    var categoryText by remember { mutableStateOf(categories.firstOrNull() ?: "") }
    var amountText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = IronColors.CardBg,
        title = { Text("Set Budget", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = categoryText,
                    onValueChange = { categoryText = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IronColors.AccentGreen,
                        unfocusedBorderColor = IronColors.CardBorder
                    ),
                    singleLine = true
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Monthly Budget (INR)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IronColors.AccentGreen,
                        unfocusedBorderColor = IronColors.CardBorder
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = (amountText.toDoubleOrNull() ?: 0.0) * 100
                    if (categoryText.isNotBlank() && amount > 0) {
                        onConfirm(categoryText, amount.toLong())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IronColors.AccentGreen)
            ) {
                Text("Set Budget", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun BudgetItem(label: String, spent: Long, budget: Long, progress: Float, color: Color, hidden: Boolean = false) {
    PremiumCard(contentPadding = 16.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                val pct = (progress * 100).toInt()
                Text(
                    "$pct%",
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        pct >= 100 -> IronColors.TitaniumRed
                        pct >= 80 -> IronColors.Warning
                        else -> color
                    }
                )
            }
            ProgressLine(progress = progress.coerceIn(0f, 1f), color = when {
                progress >= 1f -> IronColors.TitaniumRed
                progress >= 0.8f -> IronColors.Warning
                else -> color
            })
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(MoneyFormatter.formatINR(spent, compact = true, hidden = hidden), style = MaterialTheme.typography.labelSmall)
                Text("of " + MoneyFormatter.formatINR(budget, compact = true, hidden = hidden), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
