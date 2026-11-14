import { apiClient } from './axiosClient'

export const applicationApi = {
  apply: (body) => apiClient.post('/api/applications', body),
  byJob: (jobId) => apiClient.get(`/api/applications/job/${jobId}`),
  bySeeker: (seekerId) => apiClient.get(`/api/applications/seeker/${seekerId}`),
  byEmployer: (employerId) => apiClient.get(`/api/applications/employer/${employerId}`),
  updateStatus: (id, status) => apiClient.patch(`/api/applications/${id}/status`, { status }),
}
