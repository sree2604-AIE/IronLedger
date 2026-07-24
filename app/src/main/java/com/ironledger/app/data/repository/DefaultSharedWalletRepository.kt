package com.ironledger.app.data.repository

import com.ironledger.app.data.local.SharedWalletDao
import com.ironledger.app.data.local.SharedWalletEntity
import com.ironledger.app.data.local.WalletExpenseEntity
import com.ironledger.app.data.local.WalletSettlementEntity
import com.ironledger.app.domain.SharedWallet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class DefaultSharedWalletRepository @Inject constructor(
    private val walletDao: SharedWalletDao,
    private val expenseDao: com.ironledger.app.data.local.WalletExpenseDao,
    private val settlementDao: com.ironledger.app.data.local.WalletSettlementDao
) : SharedWalletRepository {
    override fun observeWallets(): Flow<List<SharedWallet>> =
        walletDao.observeWallets().map { entities ->
            entities.map { entity ->
                SharedWallet(
                    id = entity.id,
                    name = entity.name,
                    totalBalancePaise = entity.totalBalancePaise
                )
            }
        }

    override suspend fun addWallet(wallet: SharedWallet) {
        walletDao.insert(
            SharedWalletEntity(
                id = wallet.id.ifBlank { UUID.randomUUID().toString() },
                name = wallet.name,
                totalBalancePaise = wallet.totalBalancePaise,
                createdAtEpochMillis = System.currentTimeMillis()
            )
        )
    }

    override suspend fun deleteWallet(id: String) {
        walletDao.delete(id)
    }

    override suspend fun addExpense(walletId: String, amountPaise: Long, description: String, memberId: String) {
        expenseDao.insert(
            WalletExpenseEntity(
                id = UUID.randomUUID().toString(),
                walletId = walletId,
                amountPaise = amountPaise,
                description = description,
                dateEpochMillis = System.currentTimeMillis(),
                paidByMemberId = memberId
            )
        )
    }

    override suspend fun settle(walletId: String, fromMemberId: String, toMemberId: String, amountPaise: Long) {
        settlementDao.insert(
            WalletSettlementEntity(
                id = UUID.randomUUID().toString(),
                walletId = walletId,
                fromMemberId = fromMemberId,
                toMemberId = toMemberId,
                amountPaise = amountPaise,
                dateEpochMillis = System.currentTimeMillis(),
                status = "SETTLED"
            )
        )
    }
}
