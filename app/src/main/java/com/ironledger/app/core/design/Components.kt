package com.ironledger.app.core.design

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.LocalGasStation
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ironledger.app.core.money.MoneyFormatter
import com.ironledger.app.domain.Account
import com.ironledger.app.domain.AccountType
import com.ironledger.app.domain.Category
import com.ironledger.app.domain.LedgerTransaction
import com.ironledger.app.domain.TransactionType
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PremiumBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        IronColors.Graphite.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        content()
    }
}

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary,
    contentPadding: Dp = 18.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = IronColors.CardBg),
        border = BorderStroke(1.dp, IronColors.CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.02f),
                            Color.Transparent
                        )
                    )
                )
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        if (action != null && onAction != null) {
            Text(
                text = action,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.clickable(onClick = onAction)
            )
        }
    }
}

@Composable
fun MetricTile(
    label: String,
    amountPaise: Long,
    hidden: Boolean,
    modifier: Modifier = Modifier,
    positive: Boolean = true
) {
    PremiumCard(modifier = modifier, contentPadding = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                MoneyFormatter.formatINR(amountPaise, compact = true, hidden = hidden),
                style = MaterialTheme.typography.titleMedium,
                color = if (positive) MaterialTheme.colorScheme.primary else IronColors.Negative,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AccountCard(account: Account, hidden: Boolean, modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier, contentPadding = 14.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(icon = account.type.icon(), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(account.name, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(account.institution ?: account.type.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(MoneyFormatter.formatINR(account.balancePaise, hidden = hidden), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun TransactionRow(transaction: LedgerTransaction, hidden: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tint = when (transaction.type) {
            TransactionType.INCOME -> MaterialTheme.colorScheme.primary
            TransactionType.EXPENSE -> IronColors.Warning
            TransactionType.TRANSFER -> IronColors.Blue
        }
        IconBadge(icon = transaction.category?.iconKey.categoryIcon(transaction.type), tint = tint, size = 42.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = transaction.note.ifBlank { transaction.category?.name ?: transaction.type.name.lowercase().replaceFirstChar { it.uppercase() } },
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${transaction.category?.name ?: transaction.type.name} - ${transaction.dateLabel()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        val sign = when (transaction.type) {
            TransactionType.INCOME -> "+"
            TransactionType.EXPENSE -> "-"
            TransactionType.TRANSFER -> ""
        }
        Text(
            text = if (hidden) "INR ****" else sign + MoneyFormatter.formatINR(transaction.amountPaise).removePrefix("INR "),
            color = if (transaction.type == TransactionType.INCOME) MaterialTheme.colorScheme.primary else IronColors.Negative,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun SegmentedControl(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Row(Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            options.forEach { item ->
                val active = item == selected
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSelected(item) },
                    color = if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else Color.Transparent,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = item,
                        modifier = Modifier.padding(vertical = 9.dp),
                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressLine(progress: Float, color: Color, modifier: Modifier = Modifier) {
    val animated by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), label = "progress")
    Canvas(modifier = modifier.height(8.dp).fillMaxWidth()) {
        drawLine(
            color = IronColors.Titanium.copy(alpha = 0.16f),
            start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
            end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2),
            strokeWidth = size.height,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
            end = androidx.compose.ui.geometry.Offset(size.width * animated, size.height / 2),
            strokeWidth = size.height,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun IconBadge(icon: ImageVector, tint: Color, modifier: Modifier = Modifier, size: Dp = 38.dp) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(size * 0.52f))
    }
}

@Composable
fun EmptyState(title: String, body: String, modifier: Modifier = Modifier) {
    PremiumCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

fun AccountType.icon(): ImageVector = when (this) {
    AccountType.CASH -> Icons.Rounded.Payments
    AccountType.BANK -> Icons.Rounded.AccountBalance
    AccountType.WALLET -> Icons.Rounded.AccountBalanceWallet
    AccountType.SAVINGS -> Icons.Rounded.AccountBalanceWallet
    AccountType.INVESTMENT -> Icons.Rounded.TrendingUp
}

fun String?.categoryIcon(type: TransactionType): ImageVector = when (this) {
    "food" -> Icons.Rounded.Restaurant
    "fuel" -> Icons.Rounded.LocalGasStation
    "bag", "cart" -> Icons.Rounded.ShoppingBag
    "bolt" -> Icons.Rounded.Bolt
    "card" -> Icons.Rounded.CreditCard
    "income", "deposit", "refund" -> Icons.Rounded.TrendingUp
    else -> when (type) {
        TransactionType.INCOME -> Icons.Rounded.TrendingUp
        TransactionType.EXPENSE -> Icons.Rounded.TrendingDown
        TransactionType.TRANSFER -> Icons.Rounded.AccountBalanceWallet
    }
}

private fun LedgerTransaction.dateLabel(): String {
    val formatter = DateTimeFormatter.ofPattern("d MMM, h:mm a")
    return Instant.ofEpochMilli(occurredAtEpochMillis).atZone(ZoneId.systemDefault()).format(formatter)
}
