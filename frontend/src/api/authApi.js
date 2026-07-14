import axiosClient from './axiosClient'

const authApi = {
  // NCL-01-CN-001-TC-01/02: đăng nhập đúng/sai tài khoản
  login: (username, password) =>
    axiosClient.post('/auth/login', { username, password }),

  logout: () => axiosClient.post('/auth/logout'),

  getCurrentUser: () => axiosClient.get('/auth/me'),
}

export default authApi
