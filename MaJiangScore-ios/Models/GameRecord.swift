//
//  GameRecord.swift
//  MaJiangScore
//
//  Created by Yingchu Zhao on 2026/5/27.
//


import Foundation

struct GameRecord: Identifiable, Codable {
    var id = UUID()
    var date = Date()
    var winner: String
    var scoreChange: Int
}
