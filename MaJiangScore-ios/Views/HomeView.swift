//
//  HomeView.swift
//  MaJiangScore
//
//  Created by Yingchu Zhao on 2026/5/27.
//


import SwiftUI

struct HomeView: View {
    @ObservedObject var vm: GameViewModel
    @State private var roomCode = ""
    @State private var showJoinRoom = false
    @State private var navigateToRoom = false
    @State private var joinError = ""

    var body: some View {
        NavigationStack {
            ZStack {
                ScrollView {
                    VStack(spacing: 20) {

                        userHeader

                        roomActions

                    }
                    .padding()
                }

                if showJoinRoom {
                    popupBackdrop {
                        VStack(spacing: 16) {
                            Text("加入房间")
                                .font(.headline)

                            TextField("输入房间号", text: $roomCode)
                                .keyboardType(.numberPad)
                                .textFieldStyle(.roundedBorder)

                            if !joinError.isEmpty {
                                Text(joinError)
                                    .font(.caption)
                                    .foregroundColor(.red)
                            }

                            HStack {
                                Button("取消") {
                                    closeJoinPopup()
                                }
                                .buttonStyle(.bordered)

                                Button("加入") {
                                    joinRoom()
                                }
                                .buttonStyle(.borderedProminent)
                            }
                        }
                    }
                }
            }
            .navigationTitle("麻将记分器")
            .navigationDestination(isPresented: $navigateToRoom) {
                RoomView(vm: vm)
            }
        }
    }

    private var userHeader: some View {
        VStack(spacing: 12) {
            Image(systemName: vm.user.avatarSystemName)
                .font(.system(size: 64))
                .foregroundColor(.blue)

            Text(vm.user.name)
                .font(.title2)
                .bold()

            HStack {
                statItem("胜场", "\(vm.user.winCount)")
                statItem("负场", "\(vm.user.loseCount)")
                statItem("胜率", "\(vm.user.winRate * 100, default: "%.1f")%")
            }

            Text("总积分：\(vm.user.totalScore)")
                .font(.headline)
        }
        .padding()
        .background(Color(.secondarySystemBackground))
        .cornerRadius(16)
    }

    private var roomActions: some View {
        VStack(spacing: 12) {
            Button("创建房间") {
                vm.createRoom(ownerName: vm.user.name)
                navigateToRoom = true
            }
            .buttonStyle(.borderedProminent)

            Button("加入房间") {
                roomCode = ""
                joinError = ""
                showJoinRoom = true
            }
            .buttonStyle(.bordered)
        }
    }

    private func statItem(_ title: String, _ value: String) -> some View {
        VStack {
            Text(value).bold()
            Text(title).font(.caption).foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity)
    }

    private func joinRoom() {
        let code = roomCode.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !code.isEmpty else {
            joinError = "请输入房间号"
            return
        }

        guard vm.joinRoom(roomCode: code, playerName: vm.user.name) else {
            joinError = "房间号不存在"
            return
        }

        closeJoinPopup()
        navigateToRoom = true
    }

    private func closeJoinPopup() {
        roomCode = ""
        joinError = ""
        showJoinRoom = false
    }

    private func popupBackdrop<Content: View>(@ViewBuilder content: () -> Content) -> some View {
        Color.black.opacity(0.25)
            .ignoresSafeArea()
            .overlay {
                content()
                    .padding()
                    .frame(maxWidth: 320)
                    .background(Color(.systemBackground))
                    .cornerRadius(12)
                    .shadow(radius: 12)
                    .padding()
            }
    }
}
