const BASE_URL = 'http://localhost:8080/api'

function request(method, path, data) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    wx.request({
      url: `${BASE_URL}${path}`,
      method,
      data,
      header: {
        'content-type': 'application/json',
        Authorization: token ? `Bearer ${token}` : ''
      },
      success: res => {
        const body = res.data || {}
        if (res.statusCode >= 200 && res.statusCode < 300 && body.success) {
          resolve(body.data)
          return
        }
        reject(new Error(body.message || '请求失败'))
      },
      fail: reject
    })
  })
}

module.exports = {
  get(path) {
    return request('GET', path)
  },
  post(path, data) {
    return request('POST', path, data)
  },
  put(path, data) {
    return request('PUT', path, data)
  }
}
