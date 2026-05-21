package com.ironledger.app.feature.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.ReminderRepository
import com.ironledger.app.domain.Reminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
) : ViewModel() {
    val reminders: StateFlow<List<Reminder>> = reminderRepository.observeReminders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun markAsPaid(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.markAsPaid(reminder.id, !reminder.isPaid)
            // TODO: Also create a transaction if it was marked as paid
        }
    }
}
