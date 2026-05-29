//
//  MainTabView.swift
//  MaJiangScore
//
//  Created by Yingchu Zhao on 2026/5/27.
//


import SwiftUI

struct MainTabView: View {
    @StateObject private var vm = GameViewModel()

    var body: some View {
        TabView {
            HomeView(vm: vm)
                .tabItem {
                    Label("首页", systemImage: "house")
                }

            MineView(vm: vm)
                .tabItem {
                    Label("我的", systemImage: "person")
                }
        }
    }
}