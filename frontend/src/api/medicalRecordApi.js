import axiosClient from './axiosClient'

const medicalRecordApi = {
  getAll: (params) => {
    return axiosClient.get('/medical-records', { params })
  },
  getById: (id) => {
    return axiosClient.get(`/medical-records/${id}`)
  },
  getByPatient: (patientId, params) => {
    return axiosClient.get(`/medical-records/by-patient/${patientId}`, { params })
  },
  getByDoctor: (doctorId, params) => {
    return axiosClient.get(`/medical-records/by-doctor/${doctorId}`, { params })
  },
  create: (data) => {
    return axiosClient.post('/medical-records', data)
  },
  update: (id, data) => {
    return axiosClient.put(`/medical-records/${id}`, data)
  },
  delete: (id) => {
    return axiosClient.delete(`/medical-records/${id}`)
  },
}

export default medicalRecordApi
