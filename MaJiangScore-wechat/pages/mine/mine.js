const app = getApp()
const api = require('../../utils/api')

Page({
  data: {
    user: null,
    nickname: '',
    avatarUrl: '',
    avatarInitial: '麻',
    winRateText: '0.0',
    histories: []
  },

  onShow() {
    app.ensureLogin()
      .then(() => api.get('/users/me'))
      .then(user => {
        app.globalData.user = user
        this.setProfile(user)
      })
      .catch(err => this.toast(err.message))
  },

  setProfile(user) {
    const histories = (user.histories || []).map(history => Object.assign({}, history, {
      players: (history.players || []).map(player => Object.assign({}, player, {
        initial: (player.userName || '麻').slice(0, 1),
        scoreText: player.score > 0 ? `+${player.score}` : `${player.score}`
      })),
      createdAtText: this.formatTime(history.createdAt)
    }))
    this.setData({
      user: Object.assign({}, user, { histories }),
      nickname: user.nickname || '',
      avatarUrl: user.avatarUrl || '',
      avatarInitial: (user.nickname || '麻').slice(0, 1),
      winRateText: ((user.winRate || 0) * 100).toFixed(1),
      histories
    })
  },

  onNicknameInput(event) {
    this.setData({ nickname: event.detail.value })
  },

  onChooseAvatar(event) {
    this.setData({ avatarUrl: event.detail.avatarUrl })
  },

  saveProfile() {
    const nickname = this.data.nickname.trim()
    if (!nickname) {
      this.toast('请输入昵称')
      return
    }

    app.saveProfile({
      nickname,
      avatarUrl: this.data.avatarUrl
    }).then(user => {
      this.setProfile(user)
      this.toast('已保存')
    }).catch(err => this.toast(err.message))
  },

  formatTime(value) {
    if (!value) return ''
    return value.replace('T', ' ').slice(0, 16)
  },

  toast(title) {
    wx.showToast({ title, icon: 'none' })
  }
})
