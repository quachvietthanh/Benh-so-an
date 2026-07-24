import axios from 'axios'

const configuredBaseUrl = import.meta.env?.VITE_API_BASE_URL

const publicApiClient = axios.create({
  baseURL: configuredBaseUrl || 'http://127.0.0.1:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
})

export default publicApiClient
