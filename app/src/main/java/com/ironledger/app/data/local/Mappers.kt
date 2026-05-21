package com.ironledger.app.data.local

import com.ironledger.app.domain.Account
import com.ironledger.app.domain.Category
import com.ironledger.app.domain.LedgerTransaction

fun AccountEntity.toDomain(): Account = Account(
    id = id,
    name = name,
    type = type,
    balancePaise = balancePaise,
    institution = institution,
    mask = mask
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    group = groupName,
    transactionType = transactionType,
    accentHex = accentHex,
    iconKey = iconKey
)

fun TransactionWithRelations.toDomain(): LedgerTransaction = LedgerTransaction(
    id = transaction.id,
    type = transaction.type,
    amountPaise = transaction.amountPaise,
    account = account?.toDomain(),
    toAccount = toAccount?.toDomain(),
    category = category?.toDomain(),
    paymentMethod = transaction.paymentMethod,
    note = transaction.note,
    occurredAtEpochMillis = transaction.occurredAtEpochMillis,
    createdAtEpochMillis = transaction.createdAtEpochMillis,
    source = transaction.source,
    status = transaction.status
)

fun VehicleEntity.toDomain(): com.ironledger.app.domain.Vehicle = com.ironledger.app.domain.Vehicle(
    id = id,
    name = name,
    type = com.ironledger.app.domain.VehicleType.valueOf(type),
    brandModel = brandModel,
    registrationNumber = registrationNumber,
    currentMileage = currentMileage
)

fun TripEntity.toDomain(): com.ironledger.app.domain.Trip = com.ironledger.app.domain.Trip(
    id = id,
    name = name,
    budgetPaise = budgetPaise,
    startDateEpochMillis = startDateEpochMillis,
    endDateEpochMillis = endDateEpochMillis,
    isActive = isActive
)

fun ReminderEntity.toDomain(): com.ironledger.app.domain.Reminder = com.ironledger.app.domain.Reminder(
    id = id,
    title = title,
    type = com.ironledger.app.domain.ReminderType.valueOf(type),
    amountPaise = amountPaise,
    dueAtEpochMillis = dueAtEpochMillis,
    isPaid = isPaid
)

fun SubscriptionEntity.toDomain(): com.ironledger.app.domain.Subscription = com.ironledger.app.domain.Subscription(
    id = id,
    name = name,
    amountPaise = amountPaise,
    billingCycle = billingCycle,
    nextBillingDateEpochMillis = nextBillingDateEpochMillis,
    category = category
)

fun EmiEntity.toDomain(): com.ironledger.app.domain.Emi = com.ironledger.app.domain.Emi(
    id = id,
    loanName = loanName,
    monthlyAmountPaise = monthlyAmountPaise,
    totalTenureMonths = totalTenureMonths,
    remainingTenureMonths = remainingTenureMonths,
    startDateEpochMillis = startDateEpochMillis
)
