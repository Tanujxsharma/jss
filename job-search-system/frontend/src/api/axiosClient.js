import axios from 'axios'

const TOKEN_KEY = 'jobconnect.token'
const LEGACY_TOKEN_KEY = 'goodwork.token'
const USER_KEY = 'jobconnect.user'
const LEGACY_USER_KEY = 'goodwork.user'

export function migrateLegacySession() {
  const token = localStorage.getItem(LEGACY_TOKEN_KEY)
  const user = localStorage.getItem(LEGACY_USER_KEY)
  if (!localStorage.getItem(TOKEN_KEY) && token) localStorage.setItem(TOKEN_KEY, token)
  if (!localStorage.getItem(USER_KEY) && user) localStorage.setItem(USER_KEY, user)
  localStorage.removeItem(LEGACY_TOKEN_KEY)
  localStorage.removeItem(LEGACY_USER_KEY)
}

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

apiClient.interceptors.request.use((config) => {
  migrateLegacySession()
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    const message =
      error.response?.data?.message ||
      error.response?.data?.error ||
      error.message ||
      'Something went wrong. Please try again.'
    error.userMessage = message

    if (status === 401) {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
      window.dispatchEvent(new CustomEvent('jobconnect:session-expired'))
      if (!window.location.pathname.startsWith('/auth/')) {
        window.location.assign('/auth/login')
      }
    } else if ((status === 403 || status >= 500) && !error.config?.suppressToast) {
      error.toastShown = true
      window.dispatchEvent(
        new CustomEvent('jobconnect:toast', {
          detail: {
            type: 'error',
            message:
              status === 403
                ? 'You do not have permission to do that.'
                : 'The service is having trouble. Please try again shortly.',
          },
        }),
      )
    }

    return Promise.reject(error)
  },
)

export const getApiError = (error) =>
  error?.userMessage || error?.response?.data?.message || error?.message || 'Request failed.'

export { TOKEN_KEY }
