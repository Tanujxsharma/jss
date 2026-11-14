import { apiClient } from './axiosClient'

export const basketApi = {
  list: (jobSeekerId) => apiClient.get(`/api/basket/${jobSeekerId}`),
  add: (body) => apiClient.post('/api/basket', body),
  remove: (basketId) => apiClient.delete(`/api/basket/${basketId}`),
  removeJob: (jobSeekerId, jobId) =>
    apiClient.delete(`/api/basket/seeker/${jobSeekerId}/job/${jobId}`),
}
