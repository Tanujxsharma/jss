import { apiClient } from './axiosClient'

export const profileApi = {
  getJobSeekerByUser: (userId) => apiClient.get(`/api/jobseekers/user/${userId}`),
  getJobSeekerById: (id) => apiClient.get(`/api/jobseekers/${id}`),
  createJobSeeker: (body) => apiClient.post('/api/jobseekers', body),
  updateJobSeeker: (id, body) => apiClient.put(`/api/jobseekers/${id}`, body),
  uploadResume: (id, file) => {
    const formData = new FormData()
    formData.append('file', file)
    return apiClient.post(`/api/jobseekers/${id}/resume`, formData)
  },
  searchCandidates: (params) => apiClient.get('/api/jobseekers/search', { params }),
  getEmployerByUser: (userId) => apiClient.get(`/api/employers/user/${userId}`),
  getEmployerById: (id) => apiClient.get(`/api/employers/${id}`),
  createEmployer: (body) => apiClient.post('/api/employers', body),
  updateEmployer: (id, body) => apiClient.put(`/api/employers/${id}`, body),
  listEmployers: () => apiClient.get('/api/employers'),
}
