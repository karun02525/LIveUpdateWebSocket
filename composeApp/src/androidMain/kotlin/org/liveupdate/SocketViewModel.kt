package org.liveupdate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


class SocketViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<VehicleData>>(emptyList())
    val messages: StateFlow<List<VehicleData>> = _messages

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private var currentSession: WebSocketSession? = null

    private val client = HttpClient(OkHttp) {
        install(WebSockets) {
            pingInterval = 15.seconds
        }
    }

    fun connect(host: String = "192.168.0.37", port: Int = 8080, path: String = "/ws") {
        viewModelScope.launch {
            try {
                println("🔄 Connecting to ws://$host:$port$path")
                _isConnected.value = true

                client.webSocket(host = host, port = port, path = path) {
                    currentSession = this
                    println("✅ WebSocket connected!")

                    // Start auto-send in background
                    viewModelScope.launch {
                        autoSendVehicleData()
                    }

                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val messageText = frame.readText()
                            println("📨 Received: $messageText")

                            try {
                                val vehicleData = Json.decodeFromString<VehicleData>(messageText)
                                _messages.value += vehicleData
                                println("✅ Vehicle parsed: ${vehicleData.vehicleId}")
                            } catch (e: Exception) {
                                println("❌ Parse error: ${e.message}")
                            }
                        }
                    }
                }
                _isConnected.value = false
                println("⛔ Disconnected")
            } catch (e: Exception) {
                println("❌ Error: ${e.message}")
                _isConnected.value = false
            }
        }
    }

    // Auto-send random vehicle data every 5 minutes
    private suspend fun autoSendVehicleData() {
        while (_isConnected.value) {
            try {
                delay(1.seconds)  // Wait 1 seconds

                val vehicle = generateRandomVehicleData()
                val jsonString = Json.encodeToString(vehicle)

                println("📤 Auto-sending: $jsonString")
                currentSession?.send(Frame.Text(jsonString))
                println("✅ Auto-sent vehicle data")

            } catch (e: Exception) {
                println("❌ Auto-send error: ${e.message}")
            }
        }
    }

    // Generate random vehicle data
    private fun generateRandomVehicleData(): VehicleData {
        val baseLatitude = 19.0760
        val baseLongitude = 72.8777

        // Random offset (±0.01 degrees ≈ ±1km)
        val latOffset = (Random.nextDouble() - 0.5) * 0.02
        val lngOffset = (Random.nextDouble() - 0.5) * 0.02

        // Random speed between 0 and 120 km/h
        val speed = Random.nextInt(0, 120)

        // Random status
        val status = listOf("running", "stopped", "idle", "parked").random()

        return VehicleData(
            vehicleId = "MH01AB1234",
            lat = baseLatitude + latOffset,
            lng = baseLongitude + lngOffset,
            speed = speed,
            status = status
        )
    }

    fun disconnect() {
        viewModelScope.launch {
            currentSession?.close()
            _isConnected.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}