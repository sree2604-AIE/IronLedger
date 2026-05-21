package com.ironledger.app.data.repository

import com.ironledger.app.data.local.TripDao
import com.ironledger.app.data.local.TripEntity
import com.ironledger.app.data.local.toDomain
import com.ironledger.app.domain.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class DefaultTripRepository @Inject constructor(
    private val tripDao: TripDao
) : TripRepository {
    override fun observeTrips(): Flow<List<Trip>> =
        tripDao.observeTrips().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addTrip(trip: Trip) {
        tripDao.insert(
            TripEntity(
                id = UUID.randomUUID().toString(),
                name = trip.name,
                budgetPaise = trip.budgetPaise,
                startDateEpochMillis = trip.startDateEpochMillis,
                endDateEpochMillis = trip.endDateEpochMillis,
                isActive = trip.isActive,
                createdAtEpochMillis = System.currentTimeMillis()
            )
        )
    }
}
