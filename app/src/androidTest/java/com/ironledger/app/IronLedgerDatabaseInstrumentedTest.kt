package com.ironledger.app

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ironledger.app.data.local.IronLedgerDatabase
import com.ironledger.app.data.local.SeedData
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IronLedgerDatabaseInstrumentedTest {
    private lateinit var database: IronLedgerDatabase

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, IronLedgerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndQueryTransactionsWithRelations() = runTest {
        val now = System.currentTimeMillis()
        database.accountDao().insertAll(SeedData.accounts(now))
        database.categoryDao().insertAll(SeedData.categories())
        database.transactionDao().insertAll(SeedData.transactions())

        val rows = database.transactionDao().getTransactions()

        assertEquals(7, rows.size)
        assertEquals("Purse Cash", rows.first { it.transaction.id == "seed_food" }.account?.name)
        assertEquals("Food", rows.first { it.transaction.id == "seed_food" }.category?.name)
    }

    @Test
    fun accountBalanceCanBeAdjusted() = runTest {
        val now = System.currentTimeMillis()
        database.accountDao().insertAll(SeedData.accounts(now))

        database.accountDao().adjustBalance("cash_purse", -500_00, now)

        assertEquals(24_180_00L, database.accountDao().getById("cash_purse")?.balancePaise)
    }
}
