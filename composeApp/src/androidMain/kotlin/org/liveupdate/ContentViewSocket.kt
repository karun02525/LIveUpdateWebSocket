package org.liveupdate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.liveupdate.SocketViewModel
import org.liveupdate.VehicleData

@Composable
fun ContentViewSocket(viewModel: SocketViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.connect(
            host = "192.168.0.37",
            port = 8080,
            path = "/ws"
        )
    }

    Column(
        modifier = Modifier
            .padding(top = 32.dp)
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with Status
        HeaderSection(isConnected = isConnected)

        // Latest Vehicle Data
        if (messages.isNotEmpty()) {
            Text(
                text = "Latest Vehicle",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            VehicleCard(vehicle = messages.last())
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isConnected) "Waiting for data..." else "Connecting...",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }

        // History
        if (messages.size > 1) {
            Text(
                text = "History (${messages.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages.reversed().drop(1)) { vehicle ->
                    VehicleHistoryItem(vehicle)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.disconnect()
        }
    }
}

@Composable
private fun HeaderSection(isConnected: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Real-Time Tracking",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isConnected) "🟢 Connected" else "🔴 Disconnected",
                    fontSize = 14.sp,
                    color = if (isConnected) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = if (isConnected) Color(0xFF4CAF50) else Color(0xFFF44336),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}

@Composable
private fun VehicleCard(vehicle: VehicleData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Vehicle ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🚗 ${vehicle.vehicleId}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(status = vehicle.status)
            }

            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

            // Location
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📍 Latitude",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = String.format("%.4f", vehicle.lat),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📍 Longitude",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = String.format("%.4f", vehicle.lng),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Speed
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "⚡ Speed",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "${vehicle.speed} km/h",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        vehicle.speed > 80 -> Color(0xFFF44336)
                        vehicle.speed > 50 -> Color(0xFFFFC107)
                        else -> Color(0xFF4CAF50)
                    }
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    Box(
        modifier = Modifier
            .background(
                color = when (status) {
                    "running" -> Color(0xFFE8F5E9)
                    "stopped" -> Color(0xFFFFEBEE)
                    "idle" -> Color(0xFFFFF3E0)
                    else -> Color(0xFFF5F5F5)
                },
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = when (status) {
                "running" -> Color(0xFF2E7D32)
                "stopped" -> Color(0xFFC62828)
                "idle" -> Color(0xFFE65100)
                else -> Color.Gray
            }
        )
    }
}

@Composable
private fun VehicleHistoryItem(vehicle: VehicleData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = String.format("📍 (%.4f, %.4f)", vehicle.lat, vehicle.lng),
                    fontSize = 12.sp
                )
                Text(
                    text = "⚡ ${vehicle.speed} km/h",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            StatusBadge(status = vehicle.status)
        }
    }
}