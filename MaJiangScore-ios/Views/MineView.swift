//
//  MineView.swift
//  MaJiangScore
//
//  Created by Yingchu Zhao on 2026/5/27.
//


import SwiftUI
import AuthenticationServices

struct MineView: View {
    @ObservedObject var vm: GameViewModel
    @State private var editingName = ""
    @State private var showNameEditor = false
    @State private var showAvatarEditor = false

    private let avatarOptions = [
        "person.crop.circle.fill",
        "person.crop.square.fill",
        "person.circle.fill",
        "face.smiling.fill",
        "star.circle.fill",
        "heart.circle.fill"
    ]

    var body: some View {
        NavigationStack {
            ZStack {
                List {
                    Section {
                        VStack(spacing: 12) {
                            Button {
                                showAvatarEditor = true
                            } label: {
                                Image(systemName: vm.user.avatarSystemName)
                                    .font(.system(size: 72))
                                    .foregroundColor(.blue)
                            }
                            .buttonStyle(.plain)

                            Button {
                                editingName = vm.user.name
                                showNameEditor = true
                            } label: {
                                Text(vm.user.name)
                                    .font(.title2)
                                    .bold()
                            }
                            .buttonStyle(.plain)

                            SignInWithAppleButton(.signIn) { request in
                                request.requestedScopes = [.fullName, .email]
                            } onCompletion: { result in
                                switch result {
                                case .success(let authResults):
                                    handleAppleLogin(authResults)
                                case .failure(let error):
                                    print("Apple 登录失败：\(error.localizedDescription)")
                                }
                            }
                            .frame(height: 44)
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                    }

                    Section("数据统计") {
                        HStack {
                            Text("胜场")
                            Spacer()
                            Text("\(vm.user.winCount)")
                        }

                        HStack {
                            Text("负场")
                            Spacer()
                            Text("\(vm.user.loseCount)")
                        }

                        HStack {
                            Text("胜率")
                            Spacer()
                            Text("\(vm.user.winRate * 100, specifier: "%.1f")%")
                        }

                        HStack {
                            Text("总积分")
                            Spacer()
                            Text("\(vm.user.totalScore)")
                        }
                    }

                    Section("历史战绩") {
                        if vm.records.isEmpty {
                            Text("暂无历史战绩")
                                .foregroundColor(.secondary)
                        } else {
                            ForEach(vm.records) { record in
                                Text("\(record.winner) \(record.scoreChange) 分")
                            }
                        }
                    }
                }
                .navigationTitle("我的")

                if showAvatarEditor {
                    popupBackdrop {
                        VStack(spacing: 16) {
                            Text("更改头像")
                                .font(.headline)

                            LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 3), spacing: 16) {
                                ForEach(avatarOptions, id: \.self) { avatar in
                                    Button {
                                        vm.updateUserAvatar(avatar)
                                        showAvatarEditor = false
                                    } label: {
                                        Image(systemName: avatar)
                                            .font(.system(size: 42))
                                            .foregroundColor(.blue)
                                            .frame(width: 64, height: 64)
                                            .background(Color(.secondarySystemBackground))
                                            .clipShape(Circle())
                                    }
                                    .buttonStyle(.plain)
                                }
                            }

                            Button("取消") {
                                showAvatarEditor = false
                            }
                            .buttonStyle(.bordered)
                        }
                    }
                }

                if showNameEditor {
                    popupBackdrop {
                        VStack(spacing: 16) {
                            Text("更改昵称")
                                .font(.headline)

                            TextField("昵称", text: $editingName)
                                .textFieldStyle(.roundedBorder)

                            HStack {
                                Button("取消") {
                                    showNameEditor = false
                                    editingName = ""
                                }
                                .buttonStyle(.bordered)

                                Button("保存") {
                                    let name = editingName.trimmingCharacters(in: .whitespacesAndNewlines)
                                    if !name.isEmpty {
                                        vm.updateUserName(name)
                                    }
                                    showNameEditor = false
                                    editingName = ""
                                }
                                .buttonStyle(.borderedProminent)
                            }
                        }
                    }
                }
            }
        }
    }

    private func handleAppleLogin(_ authResults: ASAuthorization) {
        if let credential = authResults.credential as? ASAuthorizationAppleIDCredential {
            let fullName = credential.fullName
            let name = [fullName?.familyName, fullName?.givenName]
                .compactMap { $0 }
                .joined()

            if !name.isEmpty {
                vm.updateUserName(name)
            } else {
                vm.updateUserName("Apple 用户")
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
