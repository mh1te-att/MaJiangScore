//
//  Room.swift
//  MaJiangScore
//
//  Created by Yingchu Zhao on 2026/5/27.
//


import Foundation

struct Room: Identifiable, Codable {

    var id = UUID()

    // 四位房间号
    var roomCode: String

    // 房主
    var ownerName: String

    // 房间玩家
    var players: [Player]

    // 创建时间
    var createdAt = Date()
}