import axiosClient from './axiosClient'

const authApi = {
  login: (credentials) => {
    return axiosClient.post('/auth/login', credentials)
  },
}

export default authApi
