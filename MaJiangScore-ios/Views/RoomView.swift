//
//  RoomView.swift
//  MaJiangScore
//
//  Created by Yingchu Zhao on 2026/5/27.
//

import SwiftUI

struct RoomView: View {

    @ObservedObject var vm: GameViewModel
    @State private var selectedPlayer: Player?
    @State private var spendText = ""
    @State private var showBatchSpend = false
    @State private var batchSpendTexts: [UUID: String] = [:]

    var body: some View {
        ZStack {
            VStack(alignment: .leading, spacing: 20) {
                if let room = vm.currentRoom {
                    roomHeader(room)

                    Divider()

                    Text("玩家列表")
                        .font(.headline)

                    playerScroller(room.players)

                    scoreHistory

                    Spacer()

                    Button {
                        batchSpendTexts = Dictionary(
                            uniqueKeysWithValues: room.players.map { ($0.id, "") }
                        )
                        showBatchSpend = true
                    } label: {
                        Text("批量支出")
                            .frame(maxWidth: .infinity)
                            .padding()
                    }
                    .buttonStyle(.borderedProminent)
                }
            }
            .padding()

            if let selectedPlayer {
                popupBackdrop {
                    singleSpendPopup(for: selectedPlayer)
                }
            }

            if showBatchSpend, let room = vm.currentRoom {
                popupBackdrop {
                    batchSpendPopup(for: room.players)
                }
            }
        }
        .navigationTitle("房间")
        .navigationBarTitleDisplayMode(.inline)
    }

    private func roomHeader(_ room: Room) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            Text("房间号 \(room.roomCode)")
                .font(.headline)

            Text("房主：\(room.ownerName)")
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
    }

    private func playerScroller(_ players: [Player]) -> some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(alignment: .top, spacing: 18) {
                ForEach(players) { player in
                    Button {
                        guard !vm.isCurrentPlayer(player) else { return }
                        selectedPlayer = player
                        spendText = ""
                    } label: {
                        playerAvatar(player)
                            .opacity(vm.isCurrentPlayer(player) ? 0.55 : 1)
                    }
                    .buttonStyle(.plain)
                    .disabled(vm.isCurrentPlayer(player))
                }
            }
            .padding(.vertical, 4)
        }
    }

    private var scoreHistory: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text("历史积分变动")
                .font(.headline)

            if vm.records.isEmpty {
                Text("暂无积分变动")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            } else {
                ScrollView {
                    VStack(spacing: 8) {
                        ForEach(vm.records) { record in
                            HStack {
                                Text(record.winner)
                                Spacer()
                                Text("\(record.scoreChange)")
                                    .foregroundColor(record.scoreChange < 0 ? .red : .green)
                            }
                            .font(.subheadline)
                            .padding(.vertical, 6)
                            .padding(.horizontal, 10)
                            .background(Color(.secondarySystemBackground))
                            .cornerRadius(8)
                        }
                    }
                }
                .frame(maxHeight: 180)
            }
        }
    }

    private func playerAvatar(_ player: Player) -> some View {
        VStack(spacing: 8) {
            Image(systemName: player.avatarSystemName)
                .font(.system(size: 42))
                .foregroundColor(.blue)
                .frame(width: 64, height: 64)
                .background(Color(.secondarySystemBackground))
                .clipShape(Circle())

            Text(player.name)
                .font(.subheadline)
                .lineLimit(1)

            Text("积分 \(player.score)")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .frame(width: 86)
    }

    private func singleSpendPopup(for player: Player) -> some View {
        VStack(spacing: 16) {
            Text("支出积分")
                .font(.headline)

            playerAvatar(player)

            TextField("输入支出积分", text: $spendText)
                .keyboardType(.numberPad)
                .textFieldStyle(.roundedBorder)

            HStack {
                Button("取消") {
                    selectedPlayer = nil
                    spendText = ""
                }
                .buttonStyle(.bordered)

                Button("确认") {
                    if let amount = Int(spendText) {
                        vm.spendScore(playerID: player.id, amount: amount)
                    }
                    selectedPlayer = nil
                    spendText = ""
                }
                .buttonStyle(.borderedProminent)
            }
        }
    }

    private func batchSpendPopup(for players: [Player]) -> some View {
        VStack(spacing: 16) {
            Text("批量支出")
                .font(.headline)

            ScrollView {
                VStack(spacing: 12) {
                    ForEach(players) { player in
                        HStack(spacing: 12) {
                            Image(systemName: player.avatarSystemName)
                                .font(.system(size: 28))
                                .foregroundColor(.blue)
                                .frame(width: 44, height: 44)
                                .background(Color(.secondarySystemBackground))
                                .clipShape(Circle())

                            VStack(alignment: .leading, spacing: 4) {
                                Text(player.name)
                                    .font(.headline)

                                Text(vm.isCurrentPlayer(player) ? "自己不可支出" : "当前积分：\(player.score)")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }

                            Spacer()

                            TextField("支出", text: Binding(
                                get: { batchSpendTexts[player.id, default: ""] },
                                set: { batchSpendTexts[player.id] = $0 }
                            ))
                            .keyboardType(.numberPad)
                            .multilineTextAlignment(.trailing)
                            .frame(width: 90)
                            .textFieldStyle(.roundedBorder)
                            .disabled(vm.isCurrentPlayer(player))
                        }
                    }
                }
            }
            .frame(maxHeight: 320)

            HStack {
                Button("取消") {
                    showBatchSpend = false
                }
                .buttonStyle(.bordered)

                Button("批量支出") {
                    let spends = batchSpendTexts.compactMapValues { Int($0) }
                    vm.batchSpendScore(spends)
                    showBatchSpend = false
                }
                .buttonStyle(.borderedProminent)
            }
        }
    }

    private func popupBackdrop<Content: View>(@ViewBuilder content: () -> Content) -> some View {
        Color.black.opacity(0.25)
            .ignoresSafeArea()
            .overlay {
                content()
                    .padding()
                    .frame(maxWidth: 340)
                    .background(Color(.systemBackground))
                    .cornerRadius(12)
                    .shadow(radius: 12)
                    .padding()
            }
    }
}
