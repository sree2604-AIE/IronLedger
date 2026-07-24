package com.ironledger.app.data.local

import com.ironledger.app.domain.AccountType
import com.ironledger.app.domain.CategoryGroup
import com.ironledger.app.domain.PaymentMethod
import com.ironledger.app.domain.TransactionSource
import com.ironledger.app.domain.TransactionStatus
import com.ironledger.app.domain.TransactionType
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object SeedData {
    fun accounts(now: Long): List<AccountEntity> = listOf(
        AccountEntity("cash_purse", "Purse Cash", AccountType.CASH, 24_680_00, "Cash", null, 0, now),
        AccountEntity("bank_hdfc", "HDFC Platinum", AccountType.BANK, 4_85_200_00, "HDFC Bank", "2084", 1, now),
        AccountEntity("bank_sbi", "SBI Salary", AccountType.BANK, 2_60_000_00, "SBI", "6201", 2, now),
        AccountEntity("investments", "Investments", AccountType.INVESTMENT, 3_12_450_00, "Market", null, 3, now),
        AccountEntity("savings", "Emergency Savings", AccountType.SAVINGS, 63_350_00, "Vault", null, 4, now),
        AccountEntity("bank_icici", "ICICI Savings", AccountType.BANK, 1_20_000_00, "ICICI", "4412", 5, now)
    )

    /** Fresh-install default — one ₹0 Cash wallet. User adds their own accounts from here. */
    fun defaultAccounts(now: Long): List<AccountEntity> = listOf(
        AccountEntity("cash_purse", "Cash Wallet", AccountType.CASH, 0L, "Cash", null, 0, now)
    )

    fun categories(): List<CategoryEntity> {
        val rows = mutableListOf<CategoryEntity>()
        var order = 0
        fun add(id: String, name: String, group: CategoryGroup, type: TransactionType, color: String, icon: String) {
            rows += CategoryEntity(id, name, group, type, color, icon, order++)
        }

        add("cat_grocery", "Grocery", CategoryGroup.DAILY, TransactionType.EXPENSE, "#20D47B", "cart")
        add("cat_food", "Food", CategoryGroup.DAILY, TransactionType.EXPENSE, "#F7B955", "food")
        add("cat_milk", "Milk", CategoryGroup.DAILY, TransactionType.EXPENSE, "#78A6FF", "milk")
        add("cat_vegetables", "Vegetables", CategoryGroup.DAILY, TransactionType.EXPENSE, "#53C96B", "leaf")
        add("cat_electricity", "Electricity", CategoryGroup.BILLS, TransactionType.EXPENSE, "#EFCB68", "bolt")
        add("cat_mobile", "Mobile Recharge", CategoryGroup.BILLS, TransactionType.EXPENSE, "#A28BFF", "phone")
        add("cat_wifi", "WiFi", CategoryGroup.BILLS, TransactionType.EXPENSE, "#4DC9FF", "wifi")
        add("cat_ott", "OTT", CategoryGroup.BILLS, TransactionType.EXPENSE, "#FF5D73", "screen")
        add("cat_petrol", "Petrol", CategoryGroup.TRANSPORT, TransactionType.EXPENSE, "#23B26D", "fuel")
        add("cat_toll", "Toll", CategoryGroup.TRANSPORT, TransactionType.EXPENSE, "#9AA3B2", "road")
        add("cat_parking", "Parking", CategoryGroup.TRANSPORT, TransactionType.EXPENSE, "#A3A0FB", "parking")
        add("cat_service", "Vehicle Service", CategoryGroup.VEHICLE, TransactionType.EXPENSE, "#C7CED8", "service")
        add("cat_insurance", "Insurance", CategoryGroup.VEHICLE, TransactionType.EXPENSE, "#A68B5B", "shield")
        add("cat_emi", "EMI", CategoryGroup.FINANCE, TransactionType.EXPENSE, "#FF8C42", "bank")
        add("cat_credit_card", "Credit Card", CategoryGroup.FINANCE, TransactionType.EXPENSE, "#D86666", "card")
        add("cat_movies", "Movies", CategoryGroup.ENTERTAINMENT, TransactionType.EXPENSE, "#D54D9B", "movie")
        add("cat_trips", "Trips", CategoryGroup.ENTERTAINMENT, TransactionType.EXPENSE, "#49A7FF", "flight")
        add("cat_shopping", "Shopping", CategoryGroup.SHOPPING, TransactionType.EXPENSE, "#B45CFF", "bag")
        add("cat_electronics", "Electronics", CategoryGroup.SHOPPING, TransactionType.EXPENSE, "#67D6FF", "chip")
        add("cat_medical", "Medical", CategoryGroup.HEALTH, TransactionType.EXPENSE, "#FF6E7F", "health")
        add("cat_gym", "Gym", CategoryGroup.HEALTH, TransactionType.EXPENSE, "#20D47B", "fitness")
        add("cat_salary", "Salary", CategoryGroup.INCOME, TransactionType.INCOME, "#20D47B", "income")
        add("cat_cash_deposit", "Cash Deposit", CategoryGroup.INCOME, TransactionType.INCOME, "#69D388", "deposit")
        add("cat_refund", "Refund", CategoryGroup.INCOME, TransactionType.INCOME, "#9ED2FF", "refund")
        return rows
    }

    fun transactions(zoneId: ZoneId = ZoneId.systemDefault()): List<TransactionEntity> {
        fun at(daysAgo: Long, hour: Int, minute: Int): Long =
            LocalDate.now(zoneId)
                .minusDays(daysAgo)
                .atTime(LocalTime.of(hour, minute))
                .atZone(zoneId)
                .toInstant()
                .toEpochMilli()

        val created = System.currentTimeMillis()
        return listOf(
            TransactionEntity(id = "seed_salary", type = TransactionType.INCOME, amountPaise = 1_25_000_00, accountId = "bank_sbi", toAccountId = null, categoryId = "cat_salary", vehicleId = null, tripId = null, odometerValue = null, fuelQuantity = null, paidByMemberId = null, paymentMethod = PaymentMethod.NET_BANKING, note = "Monthly salary", occurredAtEpochMillis = at(3, 9, 0), createdAtEpochMillis = created, source = TransactionSource.SEED, status = TransactionStatus.CONFIRMED),
            TransactionEntity(id = "seed_petrol", type = TransactionType.EXPENSE, amountPaise = 2_560_00, accountId = "bank_hdfc", toAccountId = null, categoryId = "cat_petrol", vehicleId = "user_car_1", tripId = null, odometerValue = 12450.0, fuelQuantity = 25.0, paidByMemberId = null, paymentMethod = PaymentMethod.UPI, note = "Petrol for i10", occurredAtEpochMillis = at(1, 8, 30), createdAtEpochMillis = created, source = TransactionSource.SEED, status = TransactionStatus.CONFIRMED),
            TransactionEntity(id = "seed_food", type = TransactionType.EXPENSE, amountPaise = 850_00, accountId = "cash_purse", toAccountId = null, categoryId = "cat_food", vehicleId = null, tripId = null, odometerValue = null, fuelQuantity = null, paidByMemberId = null, paymentMethod = PaymentMethod.CASH, note = "Dinner with friends", occurredAtEpochMillis = at(0, 20, 15), createdAtEpochMillis = created, source = TransactionSource.SEED, status = TransactionStatus.CONFIRMED),
            TransactionEntity(id = "seed_electricity", type = TransactionType.EXPENSE, amountPaise = 1_120_00, accountId = "bank_sbi", toAccountId = null, categoryId = "cat_electricity", vehicleId = null, tripId = null, odometerValue = null, fuelQuantity = null, paidByMemberId = null, paymentMethod = PaymentMethod.UPI, note = "Electricity bill", occurredAtEpochMillis = at(2, 18, 10), createdAtEpochMillis = created, source = TransactionSource.SEED, status = TransactionStatus.CONFIRMED),
            TransactionEntity(id = "seed_netflix", type = TransactionType.EXPENSE, amountPaise = 649_00, accountId = "bank_hdfc", toAccountId = null, categoryId = "cat_ott", vehicleId = null, tripId = null, odometerValue = null, fuelQuantity = null, paidByMemberId = null, paymentMethod = PaymentMethod.CARD, note = "Netflix renewal", occurredAtEpochMillis = at(4, 7, 45), createdAtEpochMillis = created, source = TransactionSource.SEED, status = TransactionStatus.CONFIRMED),
            TransactionEntity(id = "seed_grocery", type = TransactionType.EXPENSE, amountPaise = 3_240_00, accountId = "bank_hdfc", toAccountId = null, categoryId = "cat_grocery", vehicleId = null, tripId = null, odometerValue = null, fuelQuantity = null, paidByMemberId = null, paymentMethod = PaymentMethod.UPI, note = "Weekly grocery", occurredAtEpochMillis = at(5, 19, 0), createdAtEpochMillis = created, source = TransactionSource.SEED, status = TransactionStatus.CONFIRMED),
            TransactionEntity(id = "seed_shopping", type = TransactionType.EXPENSE, amountPaise = 7_850_00, accountId = "bank_hdfc", toAccountId = null, categoryId = "cat_shopping", vehicleId = null, tripId = null, odometerValue = null, fuelQuantity = null, paidByMemberId = null, paymentMethod = PaymentMethod.CARD, note = "Online shopping", occurredAtEpochMillis = at(6, 16, 20), createdAtEpochMillis = created, source = TransactionSource.SEED, status = TransactionStatus.CONFIRMED)
        )
    }

    fun vehicles(): List<VehicleEntity> = listOf(
        VehicleEntity(
            id = "user_car_1",
            name = "My i10",
            type = "CAR",
            brandModel = "Hyundai i10",
            year = "2010",
            colorHex = "#0000FF",
            registrationNumber = "KA 03 MR 2120",
            currentMileage = 14.5,
            createdAtEpochMillis = System.currentTimeMillis()
        )
    )

    fun reminders(): List<ReminderEntity> {
        val now = System.currentTimeMillis()
        val day = 86_400_000L
        return listOf(
            ReminderEntity(id = "rem_eb", title = "Electricity Bill", type = "EB_BILL", amountPaise = 1_120_00, dueAtEpochMillis = now + 3 * day, repeatIntervalDays = 30, isPaid = false, createdAtEpochMillis = now),
            ReminderEntity(id = "rem_wifi", title = "Airtel Broadband", type = "SUBSCRIPTION", amountPaise = 799_00, dueAtEpochMillis = now + 7 * day, repeatIntervalDays = 30, isPaid = false, createdAtEpochMillis = now),
            ReminderEntity(id = "rem_ins", title = "Car Insurance Premium", type = "INSURANCE", amountPaise = 12_500_00, dueAtEpochMillis = now + 15 * day, repeatIntervalDays = 365, isPaid = false, createdAtEpochMillis = now),
            ReminderEntity(id = "rem_mobile", title = "Mobile Recharge", type = "MOBILE", amountPaise = 299_00, dueAtEpochMillis = now + 2 * day, repeatIntervalDays = 28, isPaid = false, createdAtEpochMillis = now),
            ReminderEntity(id = "rem_emi", title = "Home Loan EMI", type = "EMI", amountPaise = 18_500_00, dueAtEpochMillis = now + 5 * day, repeatIntervalDays = 30, isPaid = false, createdAtEpochMillis = now),
        )
    }

    fun subscriptions(): List<SubscriptionEntity> {
        val now = System.currentTimeMillis()
        val day = 86_400_000L
        return listOf(
            SubscriptionEntity(id = "sub_netflix", name = "Netflix", amountPaise = 649_00, billingCycle = "MONTHLY", nextBillingDateEpochMillis = now + 12 * day, category = "OTT", createdAtEpochMillis = now),
            SubscriptionEntity(id = "sub_spotify", name = "Spotify", amountPaise = 119_00, billingCycle = "MONTHLY", nextBillingDateEpochMillis = now + 5 * day, category = "Music", createdAtEpochMillis = now),
            SubscriptionEntity(id = "sub_prime", name = "Amazon Prime", amountPaise = 1_499_00, billingCycle = "YEARLY", nextBillingDateEpochMillis = now + 120 * day, category = "OTT", createdAtEpochMillis = now),
            SubscriptionEntity(id = "sub_yt", name = "YouTube Premium", amountPaise = 189_00, billingCycle = "MONTHLY", nextBillingDateEpochMillis = now + 20 * day, category = "OTT", createdAtEpochMillis = now),
            SubscriptionEntity(id = "sub_airtel", name = "Airtel Broadband", amountPaise = 799_00, billingCycle = "MONTHLY", nextBillingDateEpochMillis = now + 7 * day, category = "Internet", createdAtEpochMillis = now),
        )
    }

    fun emis(): List<EmiEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            EmiEntity(id = "emi_homeloan", loanName = "Home Loan – SBI", monthlyAmountPaise = 18_500_00, totalTenureMonths = 240, remainingTenureMonths = 196, startDateEpochMillis = now - 44L * 30 * 86_400_000L, createdAtEpochMillis = now),
            EmiEntity(id = "emi_carloan", loanName = "Car Loan – HDFC", monthlyAmountPaise = 6_200_00, totalTenureMonths = 60, remainingTenureMonths = 22, startDateEpochMillis = now - 38L * 30 * 86_400_000L, createdAtEpochMillis = now),
            EmiEntity(id = "emi_personal", loanName = "Personal Loan", monthlyAmountPaise = 3_800_00, totalTenureMonths = 24, remainingTenureMonths = 9, startDateEpochMillis = now - 15L * 30 * 86_400_000L, createdAtEpochMillis = now),
        )
    }
    fun sharedWallets(): List<SharedWalletEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            SharedWalletEntity(id = "sw_household", name = "Household Fund", totalBalancePaise = 8_500_00, createdAtEpochMillis = now - 90L * 86_400_000L),
            SharedWalletEntity(id = "sw_goa", name = "Goa Tripmates", totalBalancePaise = 2_200_00, createdAtEpochMillis = now - 65L * 86_400_000L)
        )
    }

    fun trips(): List<TripEntity> {
        val now = System.currentTimeMillis()
        val day = 86_400_000L
        return listOf(
            TripEntity(
                id = "trip_goa",
                name = "Goa Family Trip",
                budgetPaise = 45_000_00,
                startDateEpochMillis = now - 60 * day,
                endDateEpochMillis = now - 53 * day,
                isActive = false,
                createdAtEpochMillis = now - 65 * day
            ),
            TripEntity(
                id = "trip_ooty",
                name = "Ooty Getaway",
                budgetPaise = 20_000_00,
                startDateEpochMillis = now - 30 * day,
                endDateEpochMillis = now - 26 * day,
                isActive = false,
                createdAtEpochMillis = now - 35 * day
            ),
            TripEntity(
                id = "trip_kerala",
                name = "Kerala Backwaters",
                budgetPaise = 60_000_00,
                startDateEpochMillis = now + 10 * day,
                endDateEpochMillis = null,
                isActive = true,
                createdAtEpochMillis = now
            )
        )
    }
}
