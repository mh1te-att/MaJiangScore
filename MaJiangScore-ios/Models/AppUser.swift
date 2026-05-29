//
//  AppUser.swift
//  MaJiangScore
//
//  Created by Yingchu Zhao on 2026/5/27.
//


import Foundation

struct AppUser: Codable {
    var name: String = "未登录用户"
    var avatarSystemName: String = "person.crop.circle.fill"
    var winCount: Int = 0
    var loseCount: Int = 0
    var totalScore: Int = 0

    var winRate: Double {
        let total = winCount + loseCount
        return total == 0 ? 0 : Double(winCount) / Double(total)
    }
}