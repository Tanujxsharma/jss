import { apiClient } from './axiosClient'

export const messageApi = {
  sendEmployer: (body) => apiClient.post('/api/messages/employer', body),
  sendSeeker: (body) => apiClient.post('/api/messages/seeker', body),
  employerMessages: (employerId) => apiClient.get(`/api/messages/employer/${employerId}`),
  seekerMessages: (seekerId) => apiClient.get(`/api/messages/seeker/${seekerId}`),
  conversation: (params) => apiClient.get('/api/messages/conversation', { params }),
}
