import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { migrateLegacySession, TOKEN_KEY } from '../api/axiosClient'

const AuthContext = createContext(null)
const USER_KEY = 'jobconnect.user'

function readUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  } catch {
    localStorage.removeItem(USER_KEY)
    return null
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    migrateLegacySession()
    return readUser()
  })

  const signIn = useCallback((authResponse) => {
    localStorage.setItem(TOKEN_KEY, authResponse.token)
    const nextUser = {
      userId: authResponse.userId,
      email: authResponse.email,
      role: authResponse.role,
    }
    localStorage.setItem(USER_KEY, JSON.stringify(nextUser))
    setUser(nextUser)
  }, [])

  const signOut = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    setUser(null)
  }, [])

  useEffect(() => {
    const expire = () => setUser(null)
    window.addEventListener('jobconnect:session-expired', expire)
    return () => window.removeEventListener('jobconnect:session-expired', expire)
  }, [])

  const value = useMemo(
    () => ({ user, isAuthenticated: Boolean(user && localStorage.getItem(TOKEN_KEY)), signIn, signOut }),
    [user, signIn, signOut],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth must be used within AuthProvider')
  return context
}
