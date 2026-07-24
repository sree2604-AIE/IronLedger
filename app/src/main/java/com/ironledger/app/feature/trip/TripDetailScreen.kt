package com.ironledger.app.feature.trip

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ironledger.app.core.design.IronColors
import com.ironledger.app.core.design.PremiumCard
import com.ironledger.app.core.design.ProgressLine
import com.ironledger.app.core.money.MoneyFormatter
import com.ironledger.app.domain.Trip
import com.ironledger.app.domain.TripMember

@Composable
fun TripDetailScreen(
    tripId: String,
    hideBalances: Boolean,
    onBack: () -> Unit,
    onAddExpense: () -> Unit = {},
    onSettleUp: () -> Unit = {},
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val trip = state.trip
    val members = state.members

    if (trip == null) {
        // Loading or not found — show minimal scaffold
        Scaffold(containerColor = Color.Transparent) { _ ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading trip…", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onAddExpense,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IronColors.CardBg),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Add Expense", color = Color.White)
                }
                Button(
                    onClick = onSettleUp,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IronColors.Gold),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Settle Up", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(Color.Gray.copy(alpha = 0.2f))
                            .background(Brush.verticalGradient(listOf(Color.Transparent, IronColors.Black)))
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 20.dp, top = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, null, tint = Color.White) }
                    }
                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                        Text(trip.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text(
                            if (trip.isActive) "Active Trip" else "Completed",
                            color = if (trip.isActive) IronColors.AccentGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    TripStat("Total Expense", trip.totalSpentPaise, hideBalances)
                    TripStat("Budget", trip.budgetPaise, hideBalances)
                    TripStat("Remaining", (trip.budgetPaise - trip.totalSpentPaise).coerceAtLeast(0L), hideBalances)
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text("Budget Status", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    val progress = trip.totalSpentPaise.toFloat() / trip.budgetPaise.coerceAtLeast(1L)
                    val progressColor = if (progress > 1f) MaterialTheme.colorScheme.error else IronColors.AccentGreen
                    ProgressLine(progress = progress.coerceIn(0f, 1f), color = progressColor)
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(MoneyFormatter.formatINR(trip.totalSpentPaise, hidden = hideBalances), style = MaterialTheme.typography.labelSmall)
                        Text(MoneyFormatter.formatINR(trip.budgetPaise, hidden = hideBalances), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            if (members.isNotEmpty()) {
                item {
                    Text(
                        "Members (${members.size})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 20.dp, top = 32.dp, bottom = 12.dp)
                    )
                }
                items(members) { member ->
                    MemberRow(member)
                }
            }
        }
    }
}

@Composable
private fun TripStat(label: String, amountPaise: Long, hidden: Boolean) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(MoneyFormatter.formatINR(amountPaise, hidden = hidden), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MemberRow(member: TripMember) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(CircleShape).background(IronColors.CardBorder), contentAlignment = Alignment.Center) {
                Text(member.name.take(1), fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(member.name, style = MaterialTheme.typography.labelLarge)
                Text(if (member.isOwner) "Admin" else "Member", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (!member.isOwner) {
            Text("Settle", color = IronColors.Gold, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}
