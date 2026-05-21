package com.ironledger.app.feature.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironledger.app.core.design.IronColors
import com.ironledger.app.core.design.PremiumCard

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 20.dp, top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, null, tint = Color.White) }
                Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(64.dp).clip(CircleShape).background(IronColors.CardBorder), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Person, null, modifier = Modifier.size(32.dp), tint = Color.White)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Arjun Sharma", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("arjun.sharma@ledger.com", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item {
                SettingsSection("Preferences") {
                    SettingsItem("Currency", "INR (₹)", Icons.Rounded.CurrencyExchange)
                    SettingsItem("Theme", "Dark", Icons.Rounded.Settings)
                    SettingsItem("Language", "English", Icons.Rounded.Language)
                    SettingsItem("Notifications", "Enabled", Icons.Rounded.Notifications)
                    SettingsItem("Security", "Biometric / PIN", Icons.Rounded.Security)
                }
            }

            item {
                SettingsSection("Data & More") {
                    SettingsItem("Backup & Restore", null, Icons.Rounded.Backup)
                    SettingsItem("Export Data", null, Icons.Rounded.ArrowForwardIos)
                    SettingsItem("About IronLedger", "v1.0.0", Icons.Rounded.Info)
                }
            }

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
        Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 12.dp))
        PremiumCard(contentPadding = 4.dp) {
            Column {
                content()
            }
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
