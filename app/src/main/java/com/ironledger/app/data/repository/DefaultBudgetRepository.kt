package com.ironledger.app.data.repository

import com.ironledger.app.data.local.BudgetDao
import com.ironledger.app.data.local.BudgetEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultBudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {
    override fun observeBudgets(monthYear: String): Flow<List<BudgetEntity>> =
        budgetDao.observeBudgets(monthYear)

    override suspend fun setBudget(categoryId: String, amountPaise: Long, monthYear: String) {
        budgetDao.insert(
            BudgetEntity(
                id = UUID.randomUUID().toString(),
                categoryId = categoryId,
                amountPaise = amountPaise,
                monthYear = monthYear
            )
        )
    }
}
