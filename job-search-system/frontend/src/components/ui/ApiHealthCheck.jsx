import { useEffect, useRef } from 'react'
import { apiClient } from '../../api/axiosClient'

export function ApiHealthCheck() {
  const checked = useRef(false)

  useEffect(() => {
    if (!import.meta.env.DEV || checked.current) return
    checked.current = true
    apiClient.get('/api/jobs', { suppressToast: true })
      .then(() => console.info('[Goodwork API] Gateway is reachable; GET /api/jobs succeeded.'))
      .catch((error) => {
        const status = error.response?.status
        if (status === 401 || status === 403) {
          console.info(`[Goodwork API] Gateway is reachable; GET /api/jobs returned ${status} (authentication/role required).`)
        } else if (status) {
          console.warn(`[Goodwork API] Gateway responded with HTTP ${status} to GET /api/jobs.`)
        } else if (error.code === 'ERR_NETWORK') {
          console.warn('[Goodwork API] No readable response to GET /api/jobs: likely CORS blocked, or the gateway is unreachable. The browser does not distinguish these cases.')
        } else {
          console.warn('[Goodwork API] Could not reach GET /api/jobs:', error.message)
        }
      })
  }, [])

  return null
}
