package com.ironledger.app.data.repository

import com.ironledger.app.data.local.SharedWalletDao
import com.ironledger.app.data.local.SharedWalletEntity
import com.ironledger.app.domain.SharedWallet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class DefaultSharedWalletRepository @Inject constructor(
    private val walletDao: SharedWalletDao
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
                id = UUID.randomUUID().toString(),
                name = wallet.name,
                totalBalancePaise = wallet.totalBalancePaise,
                createdAtEpochMillis = System.currentTimeMillis()
            )
        )
    }
}
