package com.ironledger.app.feature.vault

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.NotificationImportant
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironledger.app.core.design.IconBadge
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.design.SegmentedControl
import com.ironledger.app.domain.AccentColor
import com.ironledger.app.domain.ThemeMode

@Composable
fun VaultScreen(
    hideBalances: Boolean,
    onNavigate: (String) -> Unit,
    viewModel: VaultViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 22.dp, end = 20.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Vault", style = MaterialTheme.typography.headlineMedium)
            Text("Executive controls and future finance modules", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            PremiumCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Preferences", style = MaterialTheme.typography.titleMedium)
                    SegmentedControl(
                        options = ThemeMode.entries.map { it.label() },
                        selected = preferences.themeMode.label(),
                        onSelected = { label -> ThemeMode.entries.firstOrNull { it.label() == label }?.let(viewModel::setTheme) }
                    )
                    SegmentedControl(
                        options = AccentColor.entries.map { it.label() },
                        selected = preferences.accentColor.label(),
                        onSelected = { label -> AccentColor.entries.firstOrNull { it.label() == label }?.let(viewModel::setAccent) }
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Hidden Mode", style = MaterialTheme.typography.labelLarge)
                            Text("Blur balances and hide amounts", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                        }
                        Switch(checked = hideBalances, onCheckedChange = viewModel::setHidden)
                    }
                }
            }
        }
        item {
            Text("Finance Modules", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                moduleRows.chunked(2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { module ->
                            ModuleCard(
                                module = module,
                                modifier = Modifier.weight(1f).clickable {
                                    module.route?.let { onNavigate(it) }
                                }
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ModuleCard(module: VaultModule, modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier, contentPadding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            IconBadge(module.icon, MaterialTheme.colorScheme.primary)
            Text(module.title, style = MaterialTheme.typography.labelLarge)
            Text(module.subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private data class VaultModule(val title: String, val subtitle: String, val icon: ImageVector, val route: String? = null)

private val moduleRows = listOf(
    VaultModule("Accounts", "Bank and wallet analytics", Icons.Rounded.AccountBalance, com.ironledger.app.core.navigation.Routes.ACTIVITY),
    VaultModule("Subscriptions", "Renewals and recurring totals", Icons.Rounded.Subscriptions, com.ironledger.app.core.navigation.Routes.SUBSCRIPTIONS),
    VaultModule("EMI Tracker", "Balance and due months", Icons.Rounded.CreditCard, com.ironledger.app.core.navigation.Routes.EMIS),
    VaultModule("Budgets", "Housing, Food, Shopping tracking", Icons.Rounded.Analytics, com.ironledger.app.core.navigation.Routes.BUDGETS),
    VaultModule("Vehicles", "Fuel, service, insurance", Icons.Rounded.DirectionsCar, com.ironledger.app.core.navigation.Routes.VEHICLES),
    VaultModule("Trips", "Budget and group splits", Icons.Rounded.Hotel, com.ironledger.app.core.navigation.Routes.TRIPS),
    VaultModule("Shared Wallets", "Family and roommate funds", Icons.Rounded.Group, com.ironledger.app.core.navigation.Routes.SHARED_WALLETS),
    VaultModule("Security", "PIN, biometric, hidden mode", Icons.Rounded.Security, com.ironledger.app.core.navigation.Routes.SETTINGS),
    VaultModule("Reminders", "Bills, EMIs, insurance alerts", Icons.Rounded.NotificationImportant, com.ironledger.app.core.navigation.Routes.REMINDERS),
    VaultModule("Cloud Backup", "Firebase and Drive ready", Icons.Rounded.CloudUpload, com.ironledger.app.core.navigation.Routes.SETTINGS)
)

private fun ThemeMode.label(): String = when (this) {
    ThemeMode.DARK -> "Dark"
    ThemeMode.AMOLED -> "AMOLED"
    ThemeMode.GLASS -> "Glass"
}

private fun AccentColor.label(): String = when (this) {
    AccentColor.EMERALD -> "Emerald"
    AccentColor.BLUE -> "Blue"
    AccentColor.GOLD -> "Gold"
    AccentColor.TITANIUM_RED -> "Red"
}
