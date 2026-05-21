package com.ironledger.app.domain

object TripLogic {
    /**
     * Calculates the expense per person for a trip.
     * Formula: Per Person Expense = Total Expense / Members
     */
    fun calculatePerPersonExpense(totalExpensePaise: Long, memberCount: Int): Long {
        if (memberCount <= 0) return 0
        return totalExpensePaise / memberCount
    }

    /**
     * Calculates fuel cost for a trip.
     * Formula: Fuel Cost = (Distance / Mileage) * Fuel Price
     */
    fun calculateFuelCost(distanceKm: Double, mileageKmpl: Double, fuelPrice: Double): Long {
        if (mileageKmpl <= 0) return 0
        val fuelNeeded = distanceKm / mileageKmpl
        return (fuelNeeded * fuelPrice * 100).toLong() // In Paise
    }
}
