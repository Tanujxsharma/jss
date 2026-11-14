import { useEffect, useState } from 'react'
import { ArrowRight, BriefcaseBusiness, CircleUserRound, Compass, Plus, Search, Sparkles } from 'lucide-react'
import { Link } from 'react-router-dom'
import { applicationApi } from '../api/applicationApi'
import { jobApi } from '../api/jobApi'
import { Button } from '../components/ui/Button'
import { Card } from '../components/ui/Card'
import { JobCard } from '../components/ui/JobCard'
import { PageHeader } from '../components/ui/PageHeader'
import { Skeleton } from '../components/ui/Skeleton'
import { Spinner } from '../components/ui/Spinner'
import { useAuth } from '../context/AuthContext'
import { useCurrentProfile } from '../hooks/useCurrentProfile'
import { displayName } from '../utils/format'

export function DashboardPage() {
  const { user } = useAuth()
  const { profile, loading: profileLoading, missing, error } = useCurrentProfile()
  const [jobs, setJobs] = useState([])
  const [applications, setApplications] = useState([])
  const [loadingStats, setLoadingStats] = useState(false)
  const [statsError, setStatsError] = useState('')
  const employer = user.role === 'EMPLOYER'

  useEffect(() => {
    if (!profile?.id) return
    let active = true
    setLoadingStats(true)
    setStatsError('')
    const request = employer
      ? Promise.all([jobApi.byEmployer(profile.id), applicationApi.byEmployer(profile.id)])
      : Promise.all([jobApi.search({}), applicationApi.bySeeker(profile.id)])
    request.then(([jobResponse, applicationResponse]) => {
      if (!active) return
      setJobs(jobResponse.data)
      setApplications(applicationResponse.data)
    }).catch((requestError) => {
      if (active) setStatsError(requestError.userMessage || 'Your dashboard data could not be loaded.')
    }).finally(() => {
      if (active) setLoadingStats(false)
    })
    return () => { active = false }
  }, [employer, profile?.id])

  const display = displayName(user, profile)

  return (
    <>
      <div className="mb-8 rounded-3xl bg-olive-900 px-6 py-7 text-cream-50 shadow-soft sm:px-9 sm:py-9">
        <div className="flex flex-col justify-between gap-6 sm:flex-row sm:items-end">
          <div>
            <p className="mb-2 inline-flex items-center gap-2 text-xs font-semibold uppercase tracking-[0.16em] text-olive-200"><Sparkles size={14} /> Your Goodwork desk</p>
            <h1 className="font-display text-3xl font-semibold sm:text-4xl">A good day to begin, {display}.</h1>
            <p className="mt-3 max-w-xl text-sm leading-6 text-olive-100/80">{employer ? 'Your next great teammate could be one thoughtful listing away.' : 'Your next chapter is taking shape. Here’s where things stand today.'}</p>
          </div>
          <Button to={employer ? '/employer/jobs/new' : '/jobs'} variant="accent" size="lg">{employer ? 'Post a role' : 'Explore roles'} <ArrowRight size={16} /></Button>
        </div>
      </div>
      {profileLoading ? <Spinner label="Preparing your dashboard…" /> : missing ? (
        <Card className="mb-8 flex flex-col items-start justify-between gap-5 p-6 sm:flex-row sm:items-center sm:p-8">
          <div><p className="text-xs font-bold uppercase tracking-[0.14em] text-accent-600">First things first</p><h2 className="mt-2 font-display text-2xl font-semibold text-olive-900">Set up your {employer ? 'organization' : 'candidate'} profile.</h2><p className="mt-2 max-w-xl text-sm leading-6 text-muted">Your account is ready. A few details will help us connect you with the right people and opportunities.</p></div>
          <Button to="/profile?setup=1">Complete profile <ArrowRight size={16} /></Button>
        </Card>
      ) : (
        <>
          {(error || statsError) && <p className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800" role="alert">{error || statsError}</p>}
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {(employer
              ? [
                  { icon: BriefcaseBusiness, label: 'Roles you’ve posted', value: jobs.length },
                  { icon: CircleUserRound, label: 'Applications received', value: applications.length },
                  { icon: Compass, label: 'Open roles', value: jobs.filter((job) => job.status === 'ACTIVE').length },
                ]
              : [
                  { icon: Compass, label: 'Open roles to explore', value: jobs.length },
                  { icon: CircleUserRound, label: 'Applications sent', value: applications.length },
                  { icon: BriefcaseBusiness, label: 'Good next steps', value: profile?.resumeUrl ? 'Ready' : 'Add resume' },
                ]
            ).map(({ icon: Icon, label, value }) => (
              <Card key={label} className="p-5">
                <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-olive-100 text-olive-800"><Icon size={18} /></span>
                <p className="mt-5 text-xs font-semibold uppercase tracking-wide text-muted">{label}</p>
                {loadingStats ? <Skeleton className="mt-2 h-8 w-20" /> : <p className="mt-1 font-display text-3xl font-semibold text-olive-900">{value}</p>}
              </Card>
            ))}
          </div>
          <div className="mt-9 flex items-end justify-between gap-4">
            <div><p className="text-xs font-bold uppercase tracking-[0.15em] text-olive-600">{employer ? 'Your work' : 'Worth a look'}</p><h2 className="mt-2 font-display text-2xl font-semibold text-olive-900">{employer ? 'Recently posted roles' : 'Fresh opportunities'}</h2></div>
            <Link to={employer ? '/employer/jobs' : '/jobs'} className="hidden items-center gap-1 text-sm font-semibold text-olive-800 hover:text-olive-600 sm:inline-flex">{employer ? 'All your listings' : 'Browse all jobs'} <ArrowRight size={15} /></Link>
          </div>
          {loadingStats ? <div className="mt-4 grid gap-4 md:grid-cols-2">{[1, 2].map((item) => <Skeleton key={item} className="h-48 rounded-2xl" />)}</div> : jobs.length ? <div className="mt-4 grid gap-4 md:grid-cols-2">{jobs.slice(0, 2).map((job) => <JobCard key={job.id} job={job} />)}</div> : (
            <Card className="mt-4 p-7 text-center">
              <p className="font-display text-xl font-semibold text-olive-900">{employer ? 'Your first listing starts here.' : 'Good opportunities are on their way.'}</p>
              <p className="mt-2 text-sm text-muted">{employer ? 'Share a role with people looking for their next right step.' : 'Take a look at the current open roles and save the ones that fit.'}</p>
              <Button to={employer ? '/employer/jobs/new' : '/jobs'} className="mt-5">{employer ? <><Plus size={16} /> Create a listing</> : <><Search size={16} /> Find a role</>}</Button>
            </Card>
          )}
        </>
      )}
    </>
  )
}
