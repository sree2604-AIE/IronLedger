package com.ironledger.app.feature.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironledger.app.core.design.EmptyState
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.design.SegmentedControl
import com.ironledger.app.core.design.TransactionRow
import com.ironledger.app.core.design.SectionHeader

@Composable
fun ActivityScreen(
    hideBalances: Boolean,
    viewModel: ActivityViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 22.dp, end = 20.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Activity", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::setQuery,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search transactions") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
        
        if (state.pendingTransactions.isNotEmpty()) {
            item {
                SectionHeader("Pending Review", "Clear All", {})
                Spacer(Modifier.height(8.dp))
                PremiumCard(accent = MaterialTheme.colorScheme.secondary) {
                    Column {
                        state.pendingTransactions.forEach { transaction ->
                            TransactionRow(transaction = transaction, hidden = hideBalances)
                        }
                    }
                }
            }
        }

        item {
            SegmentedControl(
                options = ActivityFilter.entries.map { it.label },
                selected = state.filter.label,
                onSelected = { label ->
                    ActivityFilter.entries.firstOrNull { it.label == label }?.let(viewModel::setFilter)
                }
            )
        }
        item {
            if (state.transactions.isEmpty() && state.pendingTransactions.isEmpty()) {
                EmptyState("No matching activity", "Transactions you record will appear here with account, method, and category context.")
            } else if (state.transactions.isNotEmpty()) {
                PremiumCard {
                    Column {
                        state.transactions.forEach { transaction ->
                            TransactionRow(transaction = transaction, hidden = hideBalances)
                        }
                    }
                }
            }
        }
    }
}
