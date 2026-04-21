package org.liveupdate

import kotlinx.serialization.Serializable

@Serializable
data class VehicleData(
    val vehicleId: String,
    val lat: Double,
    val lng: Double,
    val speed: Int,
    val status: String
)