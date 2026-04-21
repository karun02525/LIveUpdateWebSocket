import Foundation
import Combine
import Starscream

class SocketManager: NSObject, ObservableObject, WebSocketDelegate {
    
    @Published var isConnected = false
    @Published var vehicles: [VehicleData] = []
    
    private var socket: WebSocket?
    
    func connect() {
        var request = URLRequest(url: URL(string: "ws://192.168.0.37:8080/ws")!)
        request.timeoutInterval = 5
        
        socket = WebSocket(request: request)
        socket?.delegate = self
        socket?.connect()
    }
    
    func sendVehicleData(vehicleId: String, lat: Double, lng: Double, speed: Int, status: String) {
        let vehicle = VehicleData(
            vehicleId: vehicleId,
            lat: lat,
            lng: lng,
            speed: speed,
            status: status
        )
        
        do {
            let jsonData = try JSONEncoder().encode(vehicle)
            let jsonString = String(data: jsonData, encoding: .utf8) ?? ""
            print("📤 Sending: \(jsonString)")
            socket?.write(string: jsonString)
        } catch {
            print("❌ Encoding error: \(error.localizedDescription)")
        }
    }
    
    func disconnect() {
        socket?.disconnect()
    }
    
    // MARK: - WebSocketDelegate
    func didReceive(event: Starscream.WebSocketEvent, client: Starscream.WebSocketClient) {
        DispatchQueue.main.async {
            switch event {
            case .connected:
                self.isConnected = true
                print("✅ WebSocket connected")
                
            case .disconnected:
                self.isConnected = false
                print("❌ Disconnected")
                
            case .text(let message):
                print("📨 Received: \(message)")
                
                // Parse JSON
                if let data = message.data(using: .utf8) {
                    do {
                        let vehicleData = try JSONDecoder().decode(VehicleData.self, from: data)
                        self.vehicles.append(vehicleData)
                        print("✅ Vehicle parsed: \(vehicleData.vehicleId)")
                    } catch {
                        print("❌ Parse error: \(error.localizedDescription)")
                    }
                }
                
            case .error(let error):
                print("⚠️ WebSocket error: \(error?.localizedDescription ?? "Unknown")")
                
            default:
                break
            }
        }
    }
    
    deinit {
        disconnect()
    }
}
