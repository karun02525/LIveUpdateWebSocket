//
//  ContentView.swift
//  iosApp
//
//  Created by Kumar, Karun (893) (EXT) on 21/04/26.
//


import SwiftUI

struct ContentViewLiveUpdate: View {
    @StateObject private var socketManager = SocketManager()
    @State private var vehicleId = "MH01AB1234"
    @State private var lat = "19.0760"
    @State private var lng = "72.8777"
    @State private var speed = "60"
    @State private var status = "running"
    
    var body: some View {
        VStack(spacing: 20) {
            // Status
            HStack {
                Text(socketManager.isConnected ? "🟢 Connected" : "🔴 Disconnected")
                    .font(.headline)
                Spacer()
            }
            
            // Input Form
            Form {
                Section("Vehicle Data") {
                    TextField("Vehicle ID", text: $vehicleId)
                    TextField("Latitude", text: $lat)
                    TextField("Longitude", text: $lng)
                    TextField("Speed (km/h)", text: $speed)
                    TextField("Status", text: $status)
                    
                    Button("Send Data") {
                        socketManager.sendVehicleData(
                            vehicleId: vehicleId,
                            lat: Double(lat) ?? 0,
                            lng: Double(lng) ?? 0,
                            speed: Int(speed) ?? 0,
                            status: status
                        )
                    }
                    .disabled(!socketManager.isConnected)
                }
            }
            
            // Received Vehicles
            Text("Received Vehicles (\(socketManager.vehicles.count))")
                .font(.headline)
            
            List(socketManager.vehicles, id: \.vehicleId) { vehicle in
                VStack(alignment: .leading, spacing: 8) {
                    Text("🚗 \(vehicle.vehicleId)")
                        .font(.headline)
                    Text("📍 Lat: \(vehicle.lat), Lng: \(vehicle.lng)")
                        .font(.caption)
                    Text("⚡ Speed: \(vehicle.speed) km/h")
                        .font(.caption)
                    Text("🔴 Status: \(vehicle.status)")
                        .font(.caption)
                        .foregroundColor(vehicle.status == "running" ? .green : .red)
                }
            }
            
            Spacer()
        }
        .padding()
        .onAppear {
            socketManager.connect()
        }
        .onDisappear {
            socketManager.disconnect()
        }
    }
}

#Preview {
    ContentView()
}
