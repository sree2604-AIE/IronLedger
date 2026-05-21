package com.ironledger.app.data.repository

import com.ironledger.app.data.local.CategoryDao
import com.ironledger.app.data.local.toDomain
import com.ironledger.app.domain.TransactionType
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultCategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    override fun observeCategories() = categoryDao.observeCategories().map { rows ->
        rows.map { it.toDomain() }
    }

    override fun observeByType(type: TransactionType) = categoryDao.observeByType(type).map { rows ->
        rows.map { it.toDomain() }
    }
}
