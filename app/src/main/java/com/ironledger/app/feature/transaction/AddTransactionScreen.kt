package com.ironledger.app.feature.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironledger.app.core.design.AccountCard
import com.ironledger.app.core.design.IconBadge
import com.ironledger.app.core.design.IronColors
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.design.SegmentedControl
import com.ironledger.app.core.design.categoryIcon
import com.ironledger.app.domain.PaymentMethod
import com.ironledger.app.domain.TransactionType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AddTransactionScreen(
    onClose: () -> Unit,
    onSaved: () -> Unit,
    viewModel: TransactionEditorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 20.dp, top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) { Icon(Icons.Rounded.ArrowBack, null, tint = Color.White) }
                Text(if (state.type == TransactionType.INCOME) "Add Income" else "Add Expense", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Box(Modifier.size(40.dp)) // Placeholder for symmetry
            }
        },
        bottomBar = {
            Box(Modifier.padding(20.dp)) {
                Button(
                    onClick = { viewModel.save(onSaved) },
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IronColors.AccentGreen)
                ) {
                    Text(if (state.isSaving) "Processing..." else "Save Expense", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(state.categories) { category ->
                        CategoryChip(
                            label = category.name,
                            icon = category.iconKey.categoryIcon(state.type),
                            selected = state.selectedCategoryId == category.id,
                            onClick = { viewModel.selectCategory(category.id) }
                        )
                    }
                }
            }
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Amount", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("₹", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(8.dp))
                        Text(state.amountText.ifBlank { "0" }, style = MaterialTheme.typography.displayLarge.copy(fontSize = 48.sp), fontWeight = FontWeight.Bold)
                    }
                }
            }
            item {
                PremiumCard {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Details", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        DetailRow("Category", state.categories.find { it.id == state.selectedCategoryId }?.name ?: "Select", Icons.Rounded.Done)
                        DetailRow("Payment Method", state.paymentMethod.name.replace("_", " "), Icons.Rounded.Done)
                        DetailRow("Date", LocalDate.now().format(DateTimeFormatter.ofPattern("d MMM yyyy")), Icons.Rounded.Done)
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = state.note,
                    onValueChange = viewModel::setNote,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Note (Optional)") },
                    placeholder = { Text("e.g. Dinner with friends") },
                    colors = fieldColors(),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (selected) IronColors.AccentGreen else IronColors.CardBg)
                .padding(1.dp)
                .clip(CircleShape)
                .background(if (selected) IronColors.AccentGreen else IronColors.CardBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = if (selected) Color.Black else Color.White, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DetailRow(label: String, value: String, icon: ImageVector) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = IronColors.AccentGreen)
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    cursorColor = MaterialTheme.colorScheme.primary
)

private fun PaymentMethod.label(): String = name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
