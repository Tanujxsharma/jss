import { useCallback, useEffect, useState } from 'react'
import { getApiError } from '../api/axiosClient'

export function useApi(request, dependencies = []) {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const refetch = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const response = await request()
      setData(response.data)
      return response.data
    } catch (requestError) {
      setError(getApiError(requestError))
      throw requestError
    } finally {
      setLoading(false)
    }
    // The request and its dependencies are supplied by each screen.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, dependencies)

  useEffect(() => {
    let active = true
    setLoading(true)
    setError('')
    request()
      .then((response) => {
        if (active) setData(response.data)
      })
      .catch((requestError) => {
        if (active) setError(getApiError(requestError))
      })
      .finally(() => {
        if (active) setLoading(false)
      })
    return () => {
      active = false
    }
    // The request and its dependencies are supplied by each screen.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, dependencies)

  return { data, loading, error, refetch }
}
