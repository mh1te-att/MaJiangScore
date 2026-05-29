const api = require('./utils/api')

App({
  globalData: {
    user: null,
    currentRoom: null
  },

  ensureLogin() {
    return new Promise((resolve, reject) => {
      const token = wx.getStorageSync('token')
      if (token) {
        api.get('/users/me').then(user => {
          this.globalData.user = user
          resolve(user)
        }).catch(() => this.loginByWechat(resolve, reject))
        return
      }

      this.loginByWechat(resolve, reject)
    })
  },

  loginByWechat(resolve, reject) {
    wx.login({
      success: ({ code }) => {
        api.post('/auth/wechat', {
          code,
          nickname: wx.getStorageSync('nickname') || '',
          avatarUrl: wx.getStorageSync('avatarUrl') || ''
        }).then(({ token: nextToken, user }) => {
          wx.setStorageSync('token', nextToken)
          this.globalData.user = user
          resolve(user)
        }).catch(reject)
      },
      fail: reject
    })
  },

  saveProfile(profile) {
    return api.put('/users/me', profile).then(user => {
      this.globalData.user = user
      if (user.nickname) wx.setStorageSync('nickname', user.nickname)
      if (user.avatarUrl) wx.setStorageSync('avatarUrl', user.avatarUrl)
      return user
    })
  }
})
