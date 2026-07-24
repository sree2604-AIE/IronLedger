package com.ironledger.app.feature.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.ReminderRepository
import com.ironledger.app.data.repository.TransactionRepository
import com.ironledger.app.domain.NewTransaction
import com.ironledger.app.domain.PaymentMethod
import com.ironledger.app.domain.Reminder
import com.ironledger.app.domain.TransactionStatus
import com.ironledger.app.domain.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    val reminders: StateFlow<List<Reminder>> = reminderRepository.observeReminders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun markAsPaid(reminder: Reminder) {
        viewModelScope.launch {
            val wasPaid = reminder.isPaid
            reminderRepository.markAsPaid(reminder.id, !wasPaid)
            // When marking as paid, auto-create an expense transaction if amount is known
            if (!wasPaid && reminder.amountPaise != null && reminder.amountPaise > 0) {
                runCatching {
                    transactionRepository.addTransaction(
                        NewTransaction(
                            type = TransactionType.EXPENSE,
                            amountPaise = reminder.amountPaise,
                            accountId = "cash_purse", // default account; user can change from Activity
                            categoryId = null,
                            paymentMethod = PaymentMethod.UPI,
                            note = reminder.title,
                            occurredAtEpochMillis = System.currentTimeMillis()
                        ),
                        status = TransactionStatus.CONFIRMED
                    )
                }
            }
        }
    }

    fun addReminder(title: String, type: String, amountPaise: Long?, dueAtEpochMillis: Long) {
        viewModelScope.launch {
            reminderRepository.addReminder(
                Reminder(
                    id = java.util.UUID.randomUUID().toString(),
                    title = title,
                    type = com.ironledger.app.domain.ReminderType.valueOf(type),
                    amountPaise = amountPaise,
                    dueAtEpochMillis = dueAtEpochMillis,
                    isPaid = false
                )
            )
        }
    }
}
