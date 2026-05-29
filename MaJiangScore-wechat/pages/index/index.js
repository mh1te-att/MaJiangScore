const app = getApp()
const api = require('../../utils/api')

Page({
  data: {
    user: null,
    winRateText: '0.0',
    avatarInitial: '麻',
    roomCode: '',
    showJoinRoom: false,
    loading: false
  },

  onShow() {
    this.loadUser()
  },

  loadUser() {
    app.ensureLogin()
      .then(user => this.setUser(user))
      .catch(err => this.toast(err.message))
  },

  setUser(user) {
    this.setData({
      user,
      winRateText: ((user.winRate || 0) * 100).toFixed(1),
      avatarInitial: (user.nickname || '麻').slice(0, 1)
    })
  },

  onRoomCodeInput(event) {
    this.setData({ roomCode: event.detail.value.replace(/\D/g, '').slice(0, 4) })
  },

  createRoom() {
    this.setData({ loading: true })
    app.ensureLogin()
      .then(() => api.post('/rooms', {}))
      .then(room => this.enterRoom(room))
      .catch(err => this.toast(err.message))
      .finally(() => this.setData({ loading: false }))
  },

  openJoinRoom() {
    this.setData({ roomCode: '', showJoinRoom: true })
  },

  closeJoinRoom() {
    this.setData({ roomCode: '', showJoinRoom: false })
  },

  joinRoom() {
    const roomCode = this.data.roomCode.trim()
    if (!roomCode) {
      this.toast('请输入房间号')
      return
    }

    this.setData({ loading: true })
    app.ensureLogin()
      .then(() => api.post('/rooms/join', { roomCode }))
      .then(room => this.enterRoom(room))
      .catch(err => this.toast(err.message))
      .finally(() => this.setData({ loading: false }))
  },

  enterRoom(room) {
    app.globalData.currentRoom = room
    this.setData({ showJoinRoom: false, roomCode: '' })
    wx.navigateTo({ url: `/pages/room/room?roomCode=${room.roomCode}` })
  },

  toast(title) {
    wx.showToast({ title, icon: 'none' })
  }
})
