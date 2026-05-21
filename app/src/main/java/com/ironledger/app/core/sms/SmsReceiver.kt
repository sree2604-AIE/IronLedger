package com.ironledger.app.core.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.ironledger.app.data.repository.TransactionRepository
import com.ironledger.app.domain.NewTransaction
import com.ironledger.app.domain.PaymentMethod
import com.ironledger.app.domain.TransactionStatus
import com.ironledger.app.domain.TransactionType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    @Inject lateinit var transactionRepository: TransactionRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (msg in messages) {
                val body = msg.messageBody
                if (isBankTransaction(body)) {
                    val amount = extractAmount(body)
                    val bank = extractBank(body)
                    if (amount != null) {
                        savePendingTransaction(amount, bank, body)
                    }
                }
            }
        }
    }

    private fun savePendingTransaction(amount: Double, bank: String, body: String) {
        CoroutineScope(Dispatchers.IO).launch {
            transactionRepository.addTransaction(
                NewTransaction(
                    type = TransactionType.EXPENSE,
                    amountPaise = (amount * 100).toLong(),
                    accountId = "bank_hdfc", // Default or detect from SMS
                    categoryId = null,
                    paymentMethod = PaymentMethod.NET_BANKING,
                    note = "SMS from $bank: $body",
                    occurredAtEpochMillis = System.currentTimeMillis()
                ),
                status = TransactionStatus.PENDING_REVIEW
            )
        }
    }

    private fun isBankTransaction(body: String): Boolean {
        val keywords = listOf("debited", "credited", "spent", "vpa", "bank", "account")
        return keywords.any { body.contains(it, ignoreCase = true) } && body.contains(Regex("\\d+(\\.\\d+)?"))
    }

    private fun extractAmount(body: String): Double? {
        val regex = Regex("(?:Rs|INR|spent)\\.?\\s*([\\d,]+(?:\\.\\d+)?)")
        return regex.find(body)?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()
    }

    private fun extractBank(body: String): String {
        val banks = listOf("SBI", "HDFC", "ICICI", "AXIS", "KOTAK", "PNB")
        return banks.firstOrNull { body.contains(it, ignoreCase = true) } ?: "Unknown Bank"
    }
}
