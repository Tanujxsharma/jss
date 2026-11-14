import { useCallback, useEffect, useState } from 'react'
import { profileApi } from '../api/profileApi'
import { getApiError } from '../api/axiosClient'
import { useAuth } from '../context/AuthContext'

export function useCurrentProfile() {
  const { user } = useAuth()
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(Boolean(user))
  const [error, setError] = useState('')
  const [missing, setMissing] = useState(false)

  const load = useCallback(async () => {
    if (!user) return null
    setLoading(true)
    setError('')
    setMissing(false)
    try {
      const response =
        user.role === 'JOB_SEEKER'
          ? await profileApi.getJobSeekerByUser(user.userId)
          : await profileApi.getEmployerByUser(user.userId)
      setProfile(response.data)
      return response.data
    } catch (requestError) {
      if (requestError.response?.status === 404) {
        setMissing(true)
        setProfile(null)
        return null
      }
      setError(getApiError(requestError))
      throw requestError
    } finally {
      setLoading(false)
    }
  }, [user])

  useEffect(() => {
    if (user) load().catch(() => {})
  }, [user, load])

  return { profile, setProfile, loading, error, missing, refetch: load }
}
