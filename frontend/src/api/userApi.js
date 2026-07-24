import axiosClient from './axiosClient'

/**
 * API quản lý người dùng (dành cho Admin)
 */
const userApi = {
  list: () => {
    return axiosClient.get('/users')
  },

  create: (data) => {
    return axiosClient.post('/users', data)
  },

  update: (id, data) => {
    return axiosClient.put(`/users/${id}`, data)
  },

  remove: (id) => {
    return axiosClient.delete(`/users/${id}`)
  },

  activate: (id) => {
    return axiosClient.patch(`/users/${id}/activate`)
  },

  deactivate: (id) => {
    return axiosClient.patch(`/users/${id}/deactivate`)
  },

  /**
   * Lấy danh sách tất cả người dùng
   */
  getAll: (params) => {
    return axiosClient.get('/users', { params })
  },

  /**
   * Lấy thông tin chi tiết người dùng
   */
  getById: (id) => {
    return axiosClient.get(`/users/${id}`)
  },

  /**
   * Cập nhật trạng thái khóa / mở khóa tài khoản người dùng
   *
   * @param {string} id - UUID của người dùng
   * @param {boolean} locked - true: khóa (deactivate), false: mở khóa (activate)
   */
  updateStatus: (id, locked) => {
    return locked
      ? axiosClient.patch(`/users/${id}/deactivate`)
      : axiosClient.patch(`/users/${id}/activate`)
  },
}

export default userApi
