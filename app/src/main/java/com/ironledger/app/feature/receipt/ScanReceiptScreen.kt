package com.ironledger.app.feature.receipt

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.PhotoLibrary
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironledger.app.core.design.IronColors

@Composable
fun ScanReceiptScreen(
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = Color.Black,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 20.dp, top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, null, tint = Color.White) }
                Text("Scan Receipt", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                IconButton(onClick = {}) { Icon(Icons.Rounded.FlashOn, null, tint = Color.White) }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) { Icon(Icons.Rounded.PhotoLibrary, null, tint = Color.White, modifier = Modifier.size(28.dp)) }
                    
                    // Large Shutter Button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .border(4.dp, Color.White, CircleShape)
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        // Capture logic
                    }
                    
                    Box(Modifier.size(48.dp)) // Placeholder
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("₹ 550", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Food & Dining", style = MaterialTheme.typography.labelSmall, color = IronColors.AccentGreen)
                }
                
                androidx.compose.material3.Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(0.9f).height(56.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = IronColors.AccentGreen),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Save Expense", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            // Receipt Guidance Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(420.dp)
                    .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            ) {
                // Mock scanning area
            }
        }
    }
}
