package com.ironledger.app.feature.settings

import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironledger.app.core.design.IronColors
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.design.SegmentedControl
import com.ironledger.app.domain.AccentColor
import com.ironledger.app.domain.ThemeMode

@Composable
fun SettingsScreen(
    hideBalances: Boolean = false,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 20.dp, top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color.White) }
                Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Profile section
            item {
                var showNameDialog by remember { mutableStateOf(false) }
                val displayName = preferences.userName.ifBlank { "IronLedger User" }
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { showNameDialog = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(64.dp).clip(CircleShape).background(IronColors.CardBorder),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Tap to edit name", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Rounded.ArrowForwardIos, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            
                if (showNameDialog) {
                    var nameInput by remember { mutableStateOf(preferences.userName) }
                    AlertDialog(
                        onDismissRequest = { showNameDialog = false },
                        title = { Text("Edit Name") },
                        text = {
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Your name") },
                                singleLine = true
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.setUserName(nameInput.trim())
                                showNameDialog = false
                            }) { Text("Save") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showNameDialog = false }) { Text("Cancel") }
                        }
                    )
                }
            }

            // Appearance section
            item {
                SettingsSection("Appearance") {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Theme", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        SegmentedControl(
                            options = ThemeMode.entries.map { it.label() },
                            selected = preferences.themeMode.label(),
                            onSelected = { label -> ThemeMode.entries.firstOrNull { it.label() == label }?.let(viewModel::setTheme) }
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("Accent Color", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        SegmentedControl(
                            options = AccentColor.entries.map { it.label() },
                            selected = preferences.accentColor.label(),
                            onSelected = { label -> AccentColor.entries.firstOrNull { it.label() == label }?.let(viewModel::setAccent) }
                        )
                        HorizontalDivider(color = IronColors.CardBorder)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Hidden Mode", style = MaterialTheme.typography.labelLarge)
                                Text("Blur all balance amounts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = preferences.hideBalances,
                                onCheckedChange = viewModel::setHidden
                            )
                        }
                    }
                }
            }

            // Preferences section
            item {
                SettingsSection("Preferences") {
                    SettingsItem("Currency", "INR (₹)", Icons.Rounded.CurrencyExchange)
                    SettingsItem("Language", "English", Icons.Rounded.Language)
                    SettingsItem("Notifications", "Enabled", Icons.Rounded.Notifications)
                    SettingsItem("Security", "Biometric / PIN", Icons.Rounded.Security)
                }
            }

            // Data section
            item {
                val versionName = try {
                    context.packageManager.getPackageInfo(context.packageName, 0).versionName
                } catch (e: PackageManager.NameNotFoundException) { "1.0.0" }
                SettingsSection("Data & More") {
                    SettingsItem("Backup & Restore", null, Icons.Rounded.Backup)
                    SettingsItem("Export Data", null, Icons.Rounded.Share)
                    SettingsItem("About IronLedger", "v$versionName", Icons.Rounded.Info)
                }
            }

            // Logout
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Logout, null, tint = IronColors.Negative)
                    Spacer(Modifier.width(12.dp))
                    Text("Logout", color = IronColors.Negative, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        PremiumCard(contentPadding = 4.dp) {
            Column { content() }
        }
    }
}

@Composable
private fun SettingsItem(label: String, value: String?, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable {}.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color.White)
            Spacer(Modifier.width(16.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value != null) {
                Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(12.dp))
            }
            Icon(Icons.Rounded.ArrowForwardIos, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

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
