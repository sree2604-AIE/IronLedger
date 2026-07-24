package com.ironledger.app.feature.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ironledger.app.core.design.IronColors
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.design.SegmentedControl
import com.ironledger.app.domain.VehicleType

@Composable
fun AddVehicleScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: VehicleViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("2010") }
    var colorHex by remember { mutableStateOf("#0000FF") } // Default Blue
    var reg by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(VehicleType.CAR) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 20.dp, top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, null, tint = Color.White) }
                Text("Add Vehicle", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                SegmentedControl(
                    options = VehicleType.entries.map { it.name },
                    selected = type.name,
                    onSelected = { type = VehicleType.valueOf(it) }
                )
            }
            item {
                PremiumCard {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Vehicle Name") },
                            placeholder = { Text("e.g. My Cruiser") },
                            colors = fieldColors()
                        )
                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Brand & Model") },
                            placeholder = { Text("e.g. Hyundai i10") },
                            colors = fieldColors()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = year,
                                onValueChange = { year = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Year") },
                                colors = fieldColors()
                            )
                            OutlinedTextField(
                                value = colorHex,
                                onValueChange = { colorHex = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Color (Hex)") },
                                colors = fieldColors()
                            )
                        }
                        OutlinedTextField(
                            value = reg,
                            onValueChange = { reg = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Registration Number") },
                            placeholder = { Text("KA 01 XX 1234") },
                            colors = fieldColors()
                        )
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        viewModel.addVehicle(name, type, model, reg, year, colorHex)
                        onSaved()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IronColors.AccentGreen)
                ) {
                    Text("Register Vehicle", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = IronColors.AccentGreen,
    unfocusedBorderColor = IronColors.CardBorder,
    focusedContainerColor = IronColors.CardBg,
    unfocusedContainerColor = IronColors.CardBg
)
