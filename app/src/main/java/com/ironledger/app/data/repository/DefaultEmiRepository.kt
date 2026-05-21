package com.ironledger.app.data.repository

import com.ironledger.app.data.local.EmiDao
import com.ironledger.app.data.local.EmiEntity
import com.ironledger.app.data.local.toDomain
import com.ironledger.app.domain.Emi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class DefaultEmiRepository @Inject constructor(
    private val emiDao: EmiDao
) : EmiRepository {
    override fun observeEmis(): Flow<List<Emi>> =
        emiDao.observeEmis().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addEmi(emi: Emi) {
        emiDao.insert(
            EmiEntity(
                id = UUID.randomUUID().toString(),
                loanName = emi.loanName,
                monthlyAmountPaise = emi.monthlyAmountPaise,
                totalTenureMonths = emi.totalTenureMonths,
                remainingTenureMonths = emi.remainingTenureMonths,
                startDateEpochMillis = emi.startDateEpochMillis,
                createdAtEpochMillis = System.currentTimeMillis()
            )
        )
    }
}
