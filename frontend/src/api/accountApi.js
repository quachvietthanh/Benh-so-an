import axiosClient from './axiosClient'

const accountApi = {
  // NCL-01-CN-002-TC-01: tạo tài khoản mới
  create: (data) => axiosClient.post('/accounts', data),

  getAll: (params) => axiosClient.get('/accounts', { params }),

  getById: (id) => axiosClient.get(`/accounts/${id}`),

  // NCL-01-CN-003-TC-01: gán vai trò cho tài khoản
  updateRole: (id, roleId) =>
    axiosClient.patch(`/accounts/${id}/role`, { roleId }),

  // NCL-01-CN-004-TC-01: khóa / mở tài khoản, kèm lý do (lưu lịch sử)
  updateStatus: (id, status, reason) =>
    axiosClient.patch(`/accounts/${id}/status`, { status, reason }),
}

export default accountApi
