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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val accentColor = if (state.type == TransactionType.INCOME) IronColors.AccentGreen else IronColors.TitaniumRed

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 20.dp, top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) { Icon(Icons.Rounded.ArrowBack, null, tint = Color.White) }
                Text(
                    if (state.type == TransactionType.INCOME) "Add Income" else "Add Expense",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Box(Modifier.size(40.dp))
            }
        },
        bottomBar = {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                if (state.error != null) {
                    Text(
                        state.error!!,
                        color = IronColors.TitaniumRed,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Button(
                    onClick = { viewModel.save(onSaved) },
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text(
                        if (state.isSaving) "Saving…" else if (state.type == TransactionType.INCOME) "Save Income" else "Save Expense",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (state.type == TransactionType.INCOME) Color.Black else Color.White
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Amount field
            item {
                PremiumCard {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Amount (₹)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedTextField(
                            value = state.amountText,
                            onValueChange = viewModel::setAmount,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("0.00", style = MaterialTheme.typography.headlineLarge.copy(fontSize = 36.sp)) },
                            textStyle = MaterialTheme.typography.headlineLarge.copy(fontSize = 36.sp, fontWeight = FontWeight.Bold, color = accentColor),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = IronColors.CardBorder,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }
                }
            }

            // Category picker
            item {
                Text("Category", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(state.categories) { category ->
                        CategoryChip(
                            label = category.name,
                            icon = category.iconKey.categoryIcon(state.type),
                            selected = state.selectedCategoryId == category.id,
                            accentColor = accentColor,
                            onClick = { viewModel.selectCategory(category.id) }
                        )
                    }
                }
            }

            // Account picker
            if (state.accounts.isNotEmpty()) {
                item {
                    Text("Account", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.accounts) { account ->
                            val selected = state.selectedAccountId == account.id
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { viewModel.selectAccount(account.id) },
                                color = if (selected) accentColor.copy(alpha = 0.18f) else IronColors.CardBg,
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(account.name, style = MaterialTheme.typography.labelMedium, color = if (selected) accentColor else Color.White)
                                    if (selected) {
                                        Spacer(Modifier.width(6.dp))
                                        Icon(Icons.Rounded.Done, null, modifier = Modifier.size(14.dp), tint = accentColor)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Payment method
            item {
                Text("Payment Method", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(PaymentMethod.entries) { method ->
                        val selected = state.paymentMethod == method
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.selectPaymentMethod(method) },
                            color = if (selected) accentColor.copy(alpha = 0.18f) else IronColors.CardBg,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    method.icon(),
                                    null,
                                    modifier = Modifier.size(15.dp),
                                    tint = if (selected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    method.label(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Note + Date row
            item {
                PremiumCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = state.note,
                            onValueChange = viewModel::setNote,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Note (optional)") },
                            placeholder = { Text("e.g. Dinner with friends") },
                            colors = fieldColors(accentColor),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Rounded.CalendarMonth, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("d MMM yyyy")),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String, icon: ImageVector, selected: Boolean, accentColor: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(if (selected) accentColor.copy(alpha = 0.18f) else IronColors.CardBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = if (selected) accentColor else Color.White, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
private fun fieldColors(accent: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = accent,
    unfocusedBorderColor = IronColors.CardBorder,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    cursorColor = accent
)

private fun PaymentMethod.label(): String = when (this) {
    PaymentMethod.UPI -> "UPI"
    PaymentMethod.CASH -> "Cash"
    PaymentMethod.CARD -> "Card"
    PaymentMethod.NET_BANKING -> "NetBanking"
    PaymentMethod.WALLET -> "Wallet"
}

private fun PaymentMethod.icon(): ImageVector = when (this) {
    PaymentMethod.UPI -> Icons.Rounded.PhoneAndroid
    PaymentMethod.CASH -> Icons.Rounded.Payments
    PaymentMethod.CARD -> Icons.Rounded.CreditCard
    PaymentMethod.NET_BANKING -> Icons.Rounded.AccountBalance
    PaymentMethod.WALLET -> Icons.Rounded.AccountBalanceWallet
}
