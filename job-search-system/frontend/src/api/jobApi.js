import { apiClient } from './axiosClient'

export const jobApi = {
  search: (params) => apiClient.get('/api/jobs', { params }),
  getById: (id) => apiClient.get(`/api/jobs/${id}`),
  byEmployer: (employerId) => apiClient.get(`/api/jobs/employer/${employerId}`),
  create: (body) => apiClient.post('/api/jobs', body),
  update: (id, body) => apiClient.put(`/api/jobs/${id}`, body),
  close: (id) => apiClient.patch(`/api/jobs/${id}/close`),
  remove: (id) => apiClient.delete(`/api/jobs/${id}`),
}
