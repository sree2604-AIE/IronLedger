package com.ironledger.app.core.voice

import com.ironledger.app.domain.TransactionType
import java.util.regex.Pattern

data class VoiceTransaction(
    val amount: Double,
    val category: String?,
    val type: TransactionType = TransactionType.EXPENSE,
    val note: String
)

object VoiceProcessor {
    /**
     * Parses a voice command into a structured transaction.
     * Example: "Spent 300 on petrol" -> { amount: 300, category: "petrol", type: EXPENSE }
     */
    fun parseCommand(text: String): VoiceTransaction? {
        val amountPattern = Pattern.compile("(\\d+(\\.\\d+)?)")
        val matcher = amountPattern.matcher(text)
        
        if (!matcher.find()) return null
        
        val amount = matcher.group(1)?.toDoubleOrNull() ?: return null
        val type = if (text.contains("received", ignoreCase = true) || text.contains("income", ignoreCase = true)) {
            TransactionType.INCOME
        } else {
            TransactionType.EXPENSE
        }
        
        // Simple category extraction: words after "on" or "for"
        val categoryPattern = Pattern.compile("(?:on|for)\\s+(\\w+)", Pattern.CASE_INSENSITIVE)
        val catMatcher = categoryPattern.matcher(text)
        val category = if (catMatcher.find()) catMatcher.group(1) else null
        
        return VoiceTransaction(
            amount = amount,
            category = category,
            type = type,
            note = text
        )
    }
}
