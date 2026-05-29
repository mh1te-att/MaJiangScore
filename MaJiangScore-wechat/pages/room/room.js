const app = getApp()
const api = require('../../utils/api')

Page({
  data: {
    roomCode: '',
    room: null,
    spendPlayer: null,
    spendAmount: '',
    batchMode: false,
    batchSpends: {},
    loading: false
  },

  onLoad(options) {
    this.setData({ roomCode: options.roomCode || '' })
    this.loadRoom()
  },

  onPullDownRefresh() {
    this.loadRoom().finally(() => wx.stopPullDownRefresh())
  },

  loadRoom() {
    if (!this.data.roomCode) return Promise.resolve()
    return api.get(`/rooms/${this.data.roomCode}`)
      .then(room => this.setRoom(room))
      .catch(err => this.toast(err.message))
  },

  setRoom(room) {
    const players = (room.players || []).map(player => Object.assign({}, player, {
      initial: (player.name || '麻').slice(0, 1),
      isMe: player.id === room.currentPlayerId,
      statusText: player.id === room.currentPlayerId ? '自己不可支出' : `当前积分 ${player.score}`
    }))
    this.setData({ room: Object.assign({}, room, { players }) })
  },

  selectPlayer(event) {
    const player = event.currentTarget.dataset.player
    if (!player || player.id === this.data.room.currentPlayerId) return
    this.setData({ spendPlayer: player, spendAmount: '' })
  },

  closeSpend() {
    this.setData({ spendPlayer: null, spendAmount: '' })
  },

  onSpendInput(event) {
    this.setData({ spendAmount: event.detail.value.replace(/\D/g, '') })
  },

  confirmSpend() {
    const amount = Number(this.data.spendAmount)
    if (!amount) {
      this.toast('请输入支出积分')
      return
    }
    this.setData({ loading: true })
    api.post(`/rooms/${this.data.room.roomCode}/spend`, {
      playerId: this.data.spendPlayer.id,
      amount
    }).then(room => {
      this.setRoom(room)
      this.setData({ spendPlayer: null, spendAmount: '' })
    })
      .catch(err => this.toast(err.message))
      .finally(() => this.setData({ loading: false }))
  },

  openBatch() {
    this.setData({ batchMode: true, batchSpends: {} })
  },

  closeBatch() {
    this.setData({ batchMode: false, batchSpends: {} })
  },

  onBatchInput(event) {
    const playerId = event.currentTarget.dataset.id
    const batchSpends = Object.assign({}, this.data.batchSpends, {
      [playerId]: event.detail.value.replace(/\D/g, '')
    })
    this.setData({ batchSpends })
  },

  confirmBatch() {
    const spends = {}
    Object.keys(this.data.batchSpends).forEach(key => {
      const amount = Number(this.data.batchSpends[key])
      if (amount > 0) spends[key] = amount
    })
    if (!Object.keys(spends).length) {
      this.toast('请输入支出积分')
      return
    }
    this.setData({ loading: true })
    api.post(`/rooms/${this.data.room.roomCode}/batch-spend`, { spends })
      .then(room => {
        this.setRoom(room)
        this.setData({ batchMode: false, batchSpends: {} })
      })
      .catch(err => this.toast(err.message))
      .finally(() => this.setData({ loading: false }))
  },

  handleReturnHome() {
    wx.showModal({
      title: '返回首页',
      content: '本局还在房间内，选择留在房间继续记分，或结算本局后返回首页。',
      cancelText: '留在房间',
      confirmText: '结算本局',
      success: res => {
        if (res.confirm) {
          this.settleRoom()
        }
      }
    })
  },

  settleRoom() {
    if (!this.data.room) return
    this.setData({ loading: true })
    api.post(`/rooms/${this.data.room.roomCode}/settle`, {})
      .then(() => {
        app.globalData.currentRoom = null
        wx.showToast({ title: '已结算', icon: 'success' })
        setTimeout(() => wx.switchTab({ url: '/pages/index/index' }), 500)
      })
      .catch(err => this.toast(err.message))
      .finally(() => this.setData({ loading: false }))
  },

  toast(title) {
    wx.showToast({ title, icon: 'none' })
  }
})
