import { apiClient } from './axiosClient'

export const authApi = {
  register: (body) => apiClient.post('/api/auth/register', body),
  login: (body) => apiClient.post('/api/auth/login', body),
}
