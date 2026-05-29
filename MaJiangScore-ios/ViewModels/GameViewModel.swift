//
//  GameViewModel.swift
//  MaJiangScore
//
//  Created by Yingchu Zhao on 2026/5/27.
//


import Foundation
import Combine

class GameViewModel: ObservableObject {
    @Published var user = AppUser()
    @Published var players: [Player] = []
    @Published var records: [GameRecord] = []
    @Published var currentRoom: Room?
    @Published var currentPlayerID: UUID?

    func updateUserName(_ name: String) {
        let oldName = user.name
        user.name = name

        guard var room = currentRoom else { return }
        for index in room.players.indices where room.players[index].id == currentPlayerID {
            room.players[index].name = name
        }
        if room.ownerName == oldName {
            room.ownerName = name
        }
        currentRoom = room
    }

    func updateUserAvatar(_ avatarSystemName: String) {
        user.avatarSystemName = avatarSystemName
    }

    func createRoom(ownerName: String) {
        let code = String(format: "%04d", Int.random(in: 0...9999))
        let owner = Player(name: ownerName)
        currentPlayerID = owner.id
        currentRoom = Room(
            roomCode: code,
            ownerName: ownerName,
            players: [owner]
        )
    }

    @discardableResult
    func joinRoom(roomCode: String, playerName: String) -> Bool {
        guard var room = currentRoom else { return false }
        guard room.roomCode == roomCode else { return false }
        let player = Player(name: playerName)
        currentPlayerID = player.id
        room.players.append(player)
        currentRoom = room
        return true
    }

    func isCurrentPlayer(_ player: Player) -> Bool {
        player.id == currentPlayerID
    }

    func spendScore(playerID: UUID, amount: Int) {
        guard amount > 0, var room = currentRoom else { return }
        guard let index = room.players.firstIndex(where: { $0.id == playerID }) else { return }
        guard room.players[index].id != currentPlayerID else { return }
        let playerName = room.players[index].name
        room.players[index].score -= amount
        records.insert(GameRecord(winner: playerName, scoreChange: -amount), at: 0)
        currentRoom = room
    }

    func batchSpendScore(_ spends: [UUID: Int]) {
        guard var room = currentRoom else { return }

        for index in room.players.indices {
            let playerID = room.players[index].id
            guard playerID != currentPlayerID else { continue }
            guard let amount = spends[playerID], amount > 0 else { continue }
            let playerName = room.players[index].name
            room.players[index].score -= amount
            records.insert(GameRecord(winner: playerName, scoreChange: -amount), at: 0)
        }

        currentRoom = room
    }
}
