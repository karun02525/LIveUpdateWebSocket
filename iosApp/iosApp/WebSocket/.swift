//
//  ContentView.swift
//  iosApp
//
//  Created by Kumar, Karun (893) (EXT) on 21/04/26.
//


import SwiftUI

struct ContentViewScoket: View {
    @StateObject private var socketManager = SocketManager()
    @State private var messageText = ""
    
    var body: some View {
        VStack(spacing: 20) {
            // Status
            Text(socketManager.isConnected ? "🟢 Connected" : "🔴 Disconnected")
                .font(.headline)
            
            // Messages List
            ScrollView {
                VStack(alignment: .leading, spacing: 10) {
                    ForEach(socketManager.messages, id: \.self) { message in
                        Text(message)
                            .padding(10)
                            .background(Color.blue.opacity(0.1))
                            .cornerRadius(8)
                    }
                }
                .padding()
            }
            
            // Send Message
            HStack {
                TextField("Enter message", text: $messageText)
                    .textFieldStyle(.roundedBorder)
                
                Button("Send") {
                    socketManager.sendMessage(messageText)
                    messageText = ""
                }
                .buttonStyle(.borderedProminent)
            }
            .padding()
            
            Spacer()
        }
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
