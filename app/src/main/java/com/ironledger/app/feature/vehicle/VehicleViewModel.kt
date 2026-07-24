package com.ironledger.app.feature.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironledger.app.data.repository.TransactionRepository
import com.ironledger.app.data.repository.VehicleRepository
import com.ironledger.app.domain.NewTransaction
import com.ironledger.app.domain.PaymentMethod
import com.ironledger.app.domain.TransactionStatus
import com.ironledger.app.domain.TransactionType
import com.ironledger.app.domain.Vehicle
import com.ironledger.app.domain.VehicleType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    val vehicles: StateFlow<List<Vehicle>> = vehicleRepository.observeVehicles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addVehicle(name: String, type: VehicleType, model: String, reg: String, year: String, colorHex: String) {
        viewModelScope.launch {
            vehicleRepository.addVehicle(
                Vehicle(
                    id = "",
                    name = name,
                    type = type,
                    brandModel = model,
                    year = year,
                    colorHex = colorHex,
                    registrationNumber = reg,
                    currentMileage = 0.0
                )
            )
        }
    }

    /** Log a fuel fill-up for the given vehicle and auto-record an expense transaction. */
    fun logFuel(vehicleId: String, vehicleName: String, amountPaise: Long, fuelLitres: Double, odometer: Double, station: String?) {
        viewModelScope.launch {
            vehicleRepository.addFuelLog(
                vehicleId = vehicleId,
                amountPaise = amountPaise,
                fuelLitres = fuelLitres,
                odometer = odometer,
                station = station
            )
            runCatching {
                transactionRepository.addTransaction(
                    NewTransaction(
                        type = TransactionType.EXPENSE,
                        amountPaise = amountPaise,
                        accountId = "cash_purse",
                        categoryId = null,
                        paymentMethod = PaymentMethod.UPI,
                        note = "Fuel — $vehicleName${if (station != null) " @ $station" else ""}",
                        occurredAtEpochMillis = System.currentTimeMillis()
                    ),
                    status = TransactionStatus.CONFIRMED
                )
            }
        }
    }

    /** Log a service record for the given vehicle and auto-record an expense transaction. */
    fun logService(vehicleId: String, vehicleName: String, amountPaise: Long, description: String, odometer: Double) {
        viewModelScope.launch {
            vehicleRepository.addServiceRecord(
                vehicleId = vehicleId,
                amountPaise = amountPaise,
                type = "GENERAL",
                description = description,
                odometer = odometer
            )
            runCatching {
                transactionRepository.addTransaction(
                    NewTransaction(
                        type = TransactionType.EXPENSE,
                        amountPaise = amountPaise,
                        accountId = "cash_purse",
                        categoryId = null,
                        paymentMethod = PaymentMethod.UPI,
                        note = "Service — $vehicleName: $description",
                        occurredAtEpochMillis = System.currentTimeMillis()
                    ),
                    status = TransactionStatus.CONFIRMED
                )
            }
        }
    }
}
