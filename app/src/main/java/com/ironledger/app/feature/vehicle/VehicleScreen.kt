package com.ironledger.app.feature.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.LocalGasStation
import androidx.compose.material.icons.rounded.LocalParking
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Toll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironledger.app.core.design.IronColors
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.money.MoneyFormatter
import com.ironledger.app.domain.Vehicle

@Composable
fun VehicleScreen(
    hideBalances: Boolean,
    onBack: () -> Unit,
    onAddVehicle: () -> Unit,
    viewModel: VehicleViewModel = hiltViewModel()
) {
    val vehicles by viewModel.vehicles.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddVehicle,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Rounded.Add, "Add Vehicle")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 20.dp, top = 22.dp, end = 20.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, null, tint = Color.White)
                    }
                    Text("My Vehicles", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }

            items(vehicles) { vehicle ->
                VehicleCard(
                    vehicle = vehicle,
                    hideBalances = hideBalances,
                    onLogFuel = { amountPaise, litres, odometer, station ->
                        viewModel.logFuel(vehicle.id, vehicle.name, amountPaise, litres, odometer, station)
                    },
                    onLogService = { amountPaise, desc, odometer ->
                        viewModel.logService(vehicle.id, vehicle.name, amountPaise, desc, odometer)
                    }
                )
            }

            if (vehicles.isEmpty()) {
                item {
                    PremiumCard {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Rounded.DirectionsCar, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("No vehicles tracked yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Tap + to add your first vehicle.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VehicleCard(
    vehicle: Vehicle,
    hideBalances: Boolean,
    onLogFuel: (Long, Double, Double, String?) -> Unit,
    onLogService: (Long, String, Double) -> Unit
) {
    var showFuelDialog by remember { mutableStateOf(false) }
    var showServiceDialog by remember { mutableStateOf(false) }

    if (showFuelDialog) {
        FuelDialog(
            vehicleName = vehicle.name,
            onDismiss = { showFuelDialog = false },
            onConfirm = { amountPaise, litres, odometer, station ->
                onLogFuel(amountPaise, litres, odometer, station)
                showFuelDialog = false
            }
        )
    }

    if (showServiceDialog) {
        ServiceDialog(
            vehicleName = vehicle.name,
            onDismiss = { showServiceDialog = false },
            onConfirm = { amountPaise, desc, odometer ->
                onLogService(amountPaise, desc, odometer)
                showServiceDialog = false
            }
        )
    }

    val vehicleColor = try {
        Color(android.graphics.Color.parseColor(vehicle.colorHex ?: "#FFFFFF"))
    } catch (e: Exception) {
        Color.White
    }

    PremiumCard(contentPadding = 0.dp) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(vehicle.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(IronColors.AccentGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(vehicle.type.name.lowercase().replaceFirstChar { it.uppercase() }, color = IronColors.AccentGreen, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                        }
                    }
                    Text("${vehicle.brandModel}${vehicle.year?.let { " ($it)" } ?: ""}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    vehicle.registrationNumber?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Icon(Icons.Rounded.Settings, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            }

            Spacer(Modifier.height(12.dp))

            // 3D Hero Car Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(IronColors.CardBg, vehicleColor.copy(alpha = 0.12f), IronColors.CardBg)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(20.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = (-10).dp)
                        .graphicsLayer { alpha = 0.3f }
                        .background(Brush.radialGradient(listOf(Color.Black, Color.Transparent)))
                )
                Icon(
                    imageVector = Icons.Rounded.DirectionsCar,
                    contentDescription = null,
                    modifier = Modifier.size(160.dp).graphicsLayer { rotationY = -25f; rotationX = 5f },
                    tint = vehicleColor
                )
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.linearGradient(0.0f to Color.White.copy(alpha = 0.1f), 0.4f to Color.Transparent, 1.0f to Color.White.copy(alpha = 0.05f))
                    )
                )
            }

            Spacer(Modifier.height(20.dp))

            // Metrics Grid — real data from vehicle.totalExpensesPaise
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                VehicleMetric("Total Expenses", vehicle.totalExpensesPaise, hideBalances, Icons.Rounded.Receipt, IronColors.Emerald)
                VehicleMetric("Mileage", (vehicle.currentMileage.toLong() * 100), hideBalances, Icons.Rounded.LocalGasStation, IronColors.Blue, suffix = " km", paise = false)
                VehicleMetric("Reg. Year", 0L, hideBalances, Icons.Rounded.Build, IronColors.Gold, textOverride = vehicle.year ?: "—")
            }

            Spacer(Modifier.height(24.dp))

            // Quick Action Buttons — now tappable
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VehicleActionButton(Icons.Rounded.LocalGasStation, "Fuel", IronColors.Emerald) { showFuelDialog = true }
                VehicleActionButton(Icons.Rounded.Build, "Service", IronColors.Gold) { showServiceDialog = true }
                VehicleActionButton(Icons.Rounded.Toll, "Toll", IronColors.Blue) { showServiceDialog = true }
                VehicleActionButton(Icons.Rounded.LocalParking, "Park", IronColors.Warning) { showServiceDialog = true }
            }

            Spacer(Modifier.height(24.dp))

            // Expense Trend bar chart
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Expense Trend", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("This Year", style = MaterialTheme.typography.labelSmall, color = IronColors.AccentGreen)
                }
                Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Bottom) {
                    val trend = listOf(20, 25, 30, 18, 35, 45, 22, 15, 38, 42, 50, 28)
                    trend.forEachIndexed { i, h ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(h.dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (i == 10) IronColors.AccentGreen else IronColors.CardBorder)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FuelDialog(
    vehicleName: String,
    onDismiss: () -> Unit,
    onConfirm: (Long, Double, Double, String?) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var litres by remember { mutableStateOf("") }
    var odometer by remember { mutableStateOf("") }
    var station by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = IronColors.CardBg,
        title = { Text("Log Fuel — $vehicleName", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                IronField("Amount (₹)", amount, KeyboardType.Decimal) { amount = it.filter { c -> c.isDigit() || c == '.' } }
                IronField("Fuel (litres)", litres, KeyboardType.Decimal) { litres = it.filter { c -> c.isDigit() || c == '.' } }
                IronField("Odometer (km)", odometer, KeyboardType.Number) { odometer = it.filter { c -> c.isDigit() } }
                IronField("Station (optional)", station, KeyboardType.Text) { station = it }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val a = ((amount.toDoubleOrNull() ?: 0.0) * 100).toLong()
                    val l = litres.toDoubleOrNull() ?: 0.0
                    val o = odometer.toDoubleOrNull() ?: 0.0
                    if (a > 0) onConfirm(a, l, o, station.ifBlank { null })
                },
                colors = ButtonDefaults.buttonColors(containerColor = IronColors.Emerald)
            ) { Text("Save", color = Color.Black, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun ServiceDialog(
    vehicleName: String,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var odometer by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = IronColors.CardBg,
        title = { Text("Log Expense — $vehicleName", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                IronField("Amount (₹)", amount, KeyboardType.Decimal) { amount = it.filter { c -> c.isDigit() || c == '.' } }
                IronField("Description", description, KeyboardType.Text) { description = it }
                IronField("Odometer (km)", odometer, KeyboardType.Number) { odometer = it.filter { c -> c.isDigit() } }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val a = ((amount.toDoubleOrNull() ?: 0.0) * 100).toLong()
                    val o = odometer.toDoubleOrNull() ?: 0.0
                    if (a > 0) onConfirm(a, description.ifBlank { "Vehicle expense" }, o)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IronColors.Gold)
            ) { Text("Save", color = Color.Black, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun IronField(label: String, value: String, keyboard: KeyboardType, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = IronColors.CardBorder
        )
    )
}

@Composable
private fun VehicleActionButton(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(IronColors.CardBorder.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(14.dp), tint = color)
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White)
    }
}

@Composable
private fun VehicleMetric(
    label: String,
    amountPaise: Long,
    hidden: Boolean,
    icon: ImageVector,
    color: Color,
    suffix: String = "",
    paise: Boolean = true,
    textOverride: String? = null
) {
    Column(horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(12.dp), tint = color)
            Spacer(Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            textOverride ?: if (paise) MoneyFormatter.formatINR(amountPaise, compact = true, hidden = hidden) else "${amountPaise}$suffix",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
