//
//  VehicleData.swift
//  iosApp
//
//  Created by Kumar, Karun (893) (EXT) on 21/04/26.
//


struct VehicleData: Codable {
    let vehicleId: String
    let lat: Double
    let lng: Double
    let speed: Int
    let status: String
}