package com.ironledger.app.data.repository

import com.ironledger.app.data.local.FuelLogEntity
import com.ironledger.app.data.local.ServiceRecordEntity
import com.ironledger.app.data.local.VehicleDao
import com.ironledger.app.data.local.VehicleEntity
import com.ironledger.app.data.local.toDomain
import com.ironledger.app.domain.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class DefaultVehicleRepository @Inject constructor(
    private val vehicleDao: VehicleDao,
    private val fuelLogDao: com.ironledger.app.data.local.FuelLogDao,
    private val serviceDao: com.ironledger.app.data.local.ServiceRecordDao
) : VehicleRepository {
    override fun observeVehicles(): Flow<List<Vehicle>> =
        vehicleDao.observeVehicles().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addVehicle(vehicle: Vehicle) {
        vehicleDao.insert(
            VehicleEntity(
                id = vehicle.id.ifBlank { UUID.randomUUID().toString() },
                name = vehicle.name,
                type = vehicle.type.name,
                brandModel = vehicle.brandModel,
                year = vehicle.year,
                colorHex = vehicle.colorHex,
                registrationNumber = vehicle.registrationNumber,
                currentMileage = vehicle.currentMileage,
                createdAtEpochMillis = System.currentTimeMillis()
            )
        )
    }

    override suspend fun deleteVehicle(id: String) {
        vehicleDao.delete(id)
    }

    override suspend fun addFuelLog(vehicleId: String, amountPaise: Long, fuelLitres: Double, odometer: Double, station: String?) {
        fuelLogDao.insert(
            FuelLogEntity(
                id = UUID.randomUUID().toString(),
                vehicleId = vehicleId,
                dateEpochMillis = System.currentTimeMillis(),
                odometerValue = odometer,
                fuelQuantityLitres = fuelLitres,
                amountPaise = amountPaise,
                stationName = station
            )
        )
    }

    override suspend fun addServiceRecord(vehicleId: String, amountPaise: Long, type: String, description: String, odometer: Double) {
        serviceDao.insert(
            ServiceRecordEntity(
                id = UUID.randomUUID().toString(),
                vehicleId = vehicleId,
                dateEpochMillis = System.currentTimeMillis(),
                odometerValue = odometer,
                type = type,
                description = description,
                amountPaise = amountPaise
            )
        )
    }
}
