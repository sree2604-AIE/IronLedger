package com.ironledger.app.core.money

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

object MoneyFormatter {
    private val indianLocale = Locale.Builder().setLanguage("en").setRegion("IN").build()

    fun formatINR(paise: Long, compact: Boolean = false, hidden: Boolean = false): String {
        if (hidden) return "INR ****"
        if (compact) return compactINR(paise)

        val formatter = NumberFormat.getCurrencyInstance(indianLocale).apply {
            currency = Currency.getInstance("INR")
            maximumFractionDigits = if (paise % 100L == 0L) 0 else 2
            minimumFractionDigits = 0
        }
        return formatter.format(paise / 100.0).replace("₹", "INR ")
    }

    fun parseToPaise(input: String): Long? {
        val normalized = input
            .replace(",", "")
            .replace("INR", "", ignoreCase = true)
            .replace("₹", "")
            .trim()
        if (normalized.isBlank()) return null
        val value = normalized.toDoubleOrNull() ?: return null
        if (value <= 0.0) return null
        return (value * 100.0).roundToLong()
    }

    private fun compactINR(paise: Long): String {
        val rupees = paise / 100.0
        val sign = if (rupees < 0) "-" else ""
        val abs = rupees.absoluteValue
        val value = when {
            abs >= 10_000_000 -> "%.2fCr".format(abs / 10_000_000)
            abs >= 100_000 -> "%.2fL".format(abs / 100_000)
            abs >= 1_000 -> "%.1fK".format(abs / 1_000)
            else -> "%.0f".format(abs)
        }.trimEnd('0').trimEnd('.')
        return "${sign}INR $value"
    }
}
