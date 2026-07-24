package com.ironledger.app.data.repository

import com.ironledger.app.data.local.AccountDao
import com.ironledger.app.data.local.AccountEntity
import com.ironledger.app.data.local.IronLedgerDatabase
import com.ironledger.app.data.local.TransactionDao
import com.ironledger.app.data.local.toDomain
import androidx.room.withTransaction
import com.ironledger.app.domain.Account
import com.ironledger.app.domain.TransactionType
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.map

@Singleton
class DefaultAccountRepository @Inject constructor(
    private val database: IronLedgerDatabase,
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao
) : AccountRepository {
    override fun observeAccounts() = accountDao.observeAccounts().map { rows ->
        rows.map { it.toDomain() }
    }

    override suspend fun addAccount(account: Account) {
        accountDao.insertAll(
            listOf(
                AccountEntity(
                    id = UUID.randomUUID().toString(),
                    name = account.name,
                    type = account.type,
                    balancePaise = account.balancePaise,
                    institution = account.institution,
                    mask = account.mask,
                    updatedAtEpochMillis = System.currentTimeMillis()
                )
            )
        )
    }

    override suspend fun deleteAccount(id: String) {
        // In production, we might want to move transactions to 'Unassigned' or delete them
        database.withTransaction {
            // implementation for clean deletion
        }
    }

    override suspend fun recalculateBalances() {
        database.withTransaction {
            val allTransactions = transactionDao.getTransactions()
            val accounts = accountDao.getAccounts()
            val updatedAccounts = accounts.map { account ->
                var newBalance = 0L
                allTransactions.forEach { wrapper ->
                    val t = wrapper.transaction
                    when (t.type) {
                        TransactionType.INCOME -> if (t.accountId == account.id) newBalance += t.amountPaise
                        TransactionType.EXPENSE -> if (t.accountId == account.id) newBalance -= t.amountPaise
                        TransactionType.TRANSFER -> {
                            if (t.accountId == account.id) newBalance -= t.amountPaise
                            if (t.toAccountId == account.id) newBalance += t.amountPaise
                        }
                    }
                }
                account.copy(balancePaise = newBalance, updatedAtEpochMillis = System.currentTimeMillis())
            }
            accountDao.insertAll(updatedAccounts)
        }
    }
}
