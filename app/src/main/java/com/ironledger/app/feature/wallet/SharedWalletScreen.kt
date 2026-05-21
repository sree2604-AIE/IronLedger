package com.ironledger.app.feature.wallet

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
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Payments
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
import com.ironledger.app.core.design.MetricTile
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.money.MoneyFormatter
import com.ironledger.app.domain.SharedWallet

@Composable
fun SharedWalletScreen(
    hideBalances: Boolean,
    onBack: () -> Unit,
    viewModel: SharedWalletViewModel = hiltViewModel()
) {
    val wallets by viewModel.wallets.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Create Shared Wallet */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Rounded.Add, "New Shared Wallet")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, top = 22.dp, end = 20.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Text("Shared Wallets", style = MaterialTheme.typography.headlineMedium)
                Text("Manage collective funds and contributions", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            items(wallets) { wallet ->
                WalletCard(wallet = wallet, hideBalances = hideBalances)
            }

            if (wallets.isEmpty()) {
                item {
                    PremiumCard {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Rounded.Group, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(12.dp))
                            Text("No shared wallets yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletCard(wallet: SharedWallet, hideBalances: Boolean) {
    PremiumCard {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBadge(Icons.Rounded.Payments, MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.size(12.dp))
                    Column {
                        Text(wallet.name, style = MaterialTheme.typography.titleLarge)
                        Text("${wallet.members.size} members", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            MetricTile("Collective Balance", wallet.totalBalancePaise, hideBalances, positive = true)
        }
    }
}
