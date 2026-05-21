package com.ironledger.app.data.repository

import androidx.room.withTransaction
import com.ironledger.app.data.local.AccountDao
import com.ironledger.app.data.local.CategoryDao
import com.ironledger.app.data.local.IronLedgerDatabase
import com.ironledger.app.data.local.SeedData
import com.ironledger.app.data.local.TransactionDao
import com.ironledger.app.data.local.TransactionEntity
import com.ironledger.app.data.local.toDomain
import com.ironledger.app.domain.NewTransaction
import com.ironledger.app.domain.TransactionSource
import com.ironledger.app.domain.TransactionStatus
import com.ironledger.app.domain.TransactionType
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.map

@Singleton
class DefaultTransactionRepository @Inject constructor(
    private val database: IronLedgerDatabase,
    private val accountDao: AccountDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao
) : TransactionRepository {
    override fun observeTransactions() = transactionDao.observeTransactions().map { rows ->
        rows.map { it.toDomain() }
    }

    override suspend fun addTransaction(transaction: NewTransaction, status: TransactionStatus) {
        val now = System.currentTimeMillis()
        database.withTransaction {
            val entity = TransactionEntity(
                id = UUID.randomUUID().toString(),
                type = transaction.type,
                amountPaise = transaction.amountPaise,
                accountId = transaction.accountId,
                toAccountId = transaction.toAccountId,
                categoryId = transaction.categoryId,
                paymentMethod = transaction.paymentMethod,
                note = transaction.note.trim(),
                occurredAtEpochMillis = transaction.occurredAtEpochMillis,
                createdAtEpochMillis = now,
                source = TransactionSource.MANUAL,
                status = status
            )
            transactionDao.insert(entity)
            if (status == TransactionStatus.CONFIRMED) {
                when (transaction.type) {
                    TransactionType.EXPENSE -> accountDao.adjustBalance(transaction.accountId, -transaction.amountPaise, now)
                    TransactionType.INCOME -> accountDao.adjustBalance(transaction.accountId, transaction.amountPaise, now)
                    TransactionType.TRANSFER -> {
                        accountDao.adjustBalance(transaction.accountId, -transaction.amountPaise, now)
                        transaction.toAccountId?.let { accountDao.adjustBalance(it, transaction.amountPaise, now) }
                    }
                }
            }
        }
    }

    override suspend fun seedIfEmpty() {
        database.withTransaction {
            val now = System.currentTimeMillis()
            if (accountDao.count() == 0) accountDao.insertAll(SeedData.accounts(now))
            if (categoryDao.count() == 0) categoryDao.insertAll(SeedData.categories())
            if (transactionDao.count() == 0) transactionDao.insertAll(SeedData.transactions())
        }
    }
}
