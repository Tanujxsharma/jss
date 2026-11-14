import { lazy, Suspense } from 'react'
import { Navigate, Route, Routes, useLocation } from 'react-router-dom'
import { ApiHealthCheck } from './components/ui/ApiHealthCheck'
import { Spinner } from './components/ui/Spinner'
import { AppLayout } from './components/layout/AppLayout'
import { useAuth } from './context/AuthContext'

const ApplicationsPage = lazy(() => import('./pages/ApplicationsPage').then((module) => ({ default: module.ApplicationsPage })))
const CandidatesPage = lazy(() => import('./pages/CandidatesPage').then((module) => ({ default: module.CandidatesPage })))
const DashboardPage = lazy(() => import('./pages/DashboardPage').then((module) => ({ default: module.DashboardPage })))
const EmployerJobsPage = lazy(() => import('./pages/EmployerJobsPage').then((module) => ({ default: module.EmployerJobsPage })))
const JobFormPage = lazy(() => import('./pages/EmployerJobsPage').then((module) => ({ default: module.JobFormPage })))
const HomePage = lazy(() => import('./pages/HomePage').then((module) => ({ default: module.HomePage })))
const JobDetailPage = lazy(() => import('./pages/JobsPage').then((module) => ({ default: module.JobDetailPage })))
const JobsPage = lazy(() => import('./pages/JobsPage').then((module) => ({ default: module.JobsPage })))
const LoginPage = lazy(() => import('./pages/AuthPages').then((module) => ({ default: module.LoginPage })))
const RegisterPage = lazy(() => import('./pages/AuthPages').then((module) => ({ default: module.RegisterPage })))
const MessagesPage = lazy(() => import('./pages/MessagesPage').then((module) => ({ default: module.MessagesPage })))
const NotFoundPage = lazy(() => import('./pages/NotFoundPage').then((module) => ({ default: module.NotFoundPage })))
const ProfilePage = lazy(() => import('./pages/ProfilePage').then((module) => ({ default: module.ProfilePage })))
const SavedJobsPage = lazy(() => import('./pages/SavedJobsPage').then((module) => ({ default: module.SavedJobsPage })))

function RequireAuth() {
  const { isAuthenticated } = useAuth()
  const location = useLocation()
  return isAuthenticated ? <AppLayout /> : <Navigate to="/auth/login" replace state={{ from: location }} />
}

function RoleRoute({ roles, children }) {
  const { user } = useAuth()
  return roles.includes(user?.role) ? children : <Navigate to="/dashboard" replace />
}

function PublicOnly({ children }) {
  const { isAuthenticated } = useAuth()
  return isAuthenticated ? <Navigate to="/dashboard" replace /> : children
}

function App() {
  return (
    <>
      <ApiHealthCheck />
      <Suspense fallback={<Spinner label="Opening JobConnect…" />}>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/auth/login" element={<PublicOnly><LoginPage /></PublicOnly>} />
          <Route path="/auth/register" element={<PublicOnly><RegisterPage /></PublicOnly>} />
          <Route element={<RequireAuth />}>
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/jobs" element={<JobsPage />} />
            <Route path="/jobs/:id" element={<JobDetailPage />} />
            <Route path="/applications" element={<ApplicationsPage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/messages" element={<MessagesPage />} />
            <Route path="/saved" element={<RoleRoute roles={['JOB_SEEKER']}><SavedJobsPage /></RoleRoute>} />
            <Route path="/employer/jobs" element={<RoleRoute roles={['EMPLOYER']}><EmployerJobsPage /></RoleRoute>} />
            <Route path="/employer/jobs/new" element={<RoleRoute roles={['EMPLOYER']}><JobFormPage /></RoleRoute>} />
            <Route path="/employer/jobs/:id/edit" element={<RoleRoute roles={['EMPLOYER']}><JobFormPage /></RoleRoute>} />
            <Route path="/candidates" element={<RoleRoute roles={['EMPLOYER']}><CandidatesPage /></RoleRoute>} />
          </Route>
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </Suspense>
    </>
  )
}

export default App
