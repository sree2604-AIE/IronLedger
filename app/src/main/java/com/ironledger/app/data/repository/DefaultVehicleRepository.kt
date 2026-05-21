package com.ironledger.app.data.repository

import com.ironledger.app.data.local.VehicleDao
import com.ironledger.app.data.local.VehicleEntity
import com.ironledger.app.data.local.toDomain
import com.ironledger.app.domain.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class DefaultVehicleRepository @Inject constructor(
    private val vehicleDao: VehicleDao
) : VehicleRepository {
    override fun observeVehicles(): Flow<List<Vehicle>> =
        vehicleDao.observeVehicles().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addVehicle(vehicle: Vehicle) {
        vehicleDao.insert(
            VehicleEntity(
                id = UUID.randomUUID().toString(),
                name = vehicle.name,
                type = vehicle.type.name,
                brandModel = vehicle.brandModel,
                registrationNumber = vehicle.registrationNumber,
                currentMileage = vehicle.currentMileage,
                createdAtEpochMillis = System.currentTimeMillis()
            )
        )
    }
}
