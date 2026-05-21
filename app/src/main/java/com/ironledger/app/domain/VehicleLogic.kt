package com.ironledger.app.domain

object VehicleLogic {
    /**
     * Calculates mileage based on distance travelled and fuel used.
     * Formula: Mileage = Distance / Fuel Used
     */
    fun calculateMileage(distanceKm: Double, fuelLitres: Double): Double {
        if (fuelLitres <= 0) return 0.0
        return distanceKm / fuelLitres
    }

    /**
     * Calculates distance between two odometer readings.
     */
    fun calculateDistance(startOdometer: Double, endOdometer: Double): Double {
        return (endOdometer - startOdometer).coerceAtLeast(0.0)
    }
}
