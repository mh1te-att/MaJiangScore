//
//  Player.swift
//  MaJiangScore
//
//  Created by Yingchu Zhao on 2026/5/27.
//


import Foundation

struct Player: Identifiable, Codable {
    var id = UUID()
    var name: String
    var win: Int = 0
    var lose: Int = 0
    var score: Int = 0

    var avatarSystemName: String {
        let avatars = [
            "person.crop.circle.fill",
            "person.crop.square.fill",
            "person.circle.fill",
            "face.smiling.fill"
        ]

        return avatars[Int(id.uuidString.hashValue.magnitude % UInt(avatars.count))]
    }

    var winRate: Double {
        let total = win + lose
        return total == 0 ? 0 : Double(win) / Double(total)
    }
}
