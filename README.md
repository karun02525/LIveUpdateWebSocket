 <img width="926" height="532" alt="Screenshot 2026-04-21 at 11 04 00 AM" src="https://github.com/user-attachments/assets/be58bfc7-3d0e-494a-a066-c57de9574658" />

┌─────────────────────────────────────────────────────┐
│          Kotlin/Ktor WebSocket Server               │
│          (Port 8080, 192.168.0.37)                  │
│                                                     │
│  ┌───────────────────────────────────────────────┐  │
│  │    Connection Pool (Synchronized Set)         │  │
│  │    - Android Clients                          │  │
│  │    - iOS Clients                              │  │
│  │    - Web/Test Clients                         │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
           │               │               │
    ┌──────▼──────┐ ┌─────▼──────┐ ┌─────▼──────┐
    │  Android    │ │    iOS     │ │   Bruno    │
    │  App        │ │    App     │ │   (Test)   │
    └─────────────┘ └────────────┘ └────────────┘

Client 1 (Android)
    ├─► Connect to ws://server:8080/ws
    ├─► Send JSON: {"vehicleId": "MH01AB1234", ...}
    │
    └─► Server receives
        ├─► Parse JSON
        ├─► Log message
        └─► Broadcast to ALL clients
            ├──► Client 1 (Android)
            ├──► Client 2 (iOS)
            └──► Client 3 (Other)
            
1. CLIENT CONNECTS
   └─► Server: println("✅ Client connected")

2. MESSAGE RECEIVED
   ├─► Client sends JSON
   ├─► Server: println("📨 Received: ...")
   └─► Server broadcasts

3. MESSAGE SENT
   ├─► Server: println("📤 Sent to client")
   └─► All clients receive

4. CLIENT DISCONNECTS
   └─► Server: println("❌ Client disconnected")


🌐 Deployment
Prerequisites for Deployment
 Server IP configured
 Firewall ports open
 SSL certificates (for production)
 Load balancer (if needed)
 Backup strategy
 Monitoring setup
 
Deployment Checklist
 Backend tested in staging
 Android app signed
 iOS app provisioned
 Server logs monitored
 Rollback plan ready
 Team notified   


👥 Team Notes
Developer: Karun Kumar
Contact: karunkumar02525@gmail.com

Role: Full-Stack Developer

Timezone: IST (UTC+5:30)

Meeting Notes
Last Meeting: April 21, 2026

 Project kickoff
 Architecture review
 Tech stack approved
 Timeline established
 

   
            
    
