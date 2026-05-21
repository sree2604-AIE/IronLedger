package com.ironledger.app.data.repository

import com.ironledger.app.data.local.AccountDao
import com.ironledger.app.data.local.toDomain
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAccountRepository @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {
    override fun observeAccounts() = accountDao.observeAccounts().map { rows ->
        rows.map { it.toDomain() }
    }
}
