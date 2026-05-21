package com.ironledger.app.data.repository

import com.ironledger.app.data.local.ReminderDao
import com.ironledger.app.data.local.ReminderEntity
import com.ironledger.app.data.local.toDomain
import com.ironledger.app.domain.Reminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class DefaultReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {
    override fun observeReminders(): Flow<List<Reminder>> =
        reminderDao.observeReminders().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addReminder(reminder: Reminder) {
        reminderDao.insert(
            ReminderEntity(
                id = UUID.randomUUID().toString(),
                title = reminder.title,
                type = reminder.type.name,
                amountPaise = reminder.amountPaise,
                dueAtEpochMillis = reminder.dueAtEpochMillis,
                repeatIntervalDays = null,
                isPaid = reminder.isPaid,
                createdAtEpochMillis = System.currentTimeMillis()
            )
        )
    }

    override suspend fun markAsPaid(id: String, paid: Boolean) {
        reminderDao.markAsPaid(id, paid)
    }
}
