import { useCallback, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { ArrowRight, Search, SlidersHorizontal } from 'lucide-react'
import { jobApi } from '../api/jobApi'
import { basketApi } from '../api/basketApi'
import { applicationApi } from '../api/applicationApi'
import { getApiError } from '../api/axiosClient'
import { useAuth } from '../context/AuthContext'
import { PageHeader } from '../components/ui/PageHeader'
import { Input } from '../components/ui/Input'
import { Button } from '../components/ui/Button'
import { JobCard } from '../components/ui/JobCard'
import { Card } from '../components/ui/Card'
import { Badge } from '../components/ui/Badge'
import { EmptyState } from '../components/ui/EmptyState'
import { JobSkeleton } from '../components/ui/Skeleton'
import { Spinner } from '../components/ui/Spinner'
import { useApi } from '../hooks/useApi'
import { useCurrentProfile } from '../hooks/useCurrentProfile'
import { useToast } from '../context/ToastContext'

export function JobsPage() {
  const [form, setForm] = useState({ title: '', location: '', skills: '', company: '' })
  const [filters, setFilters] = useState({})
  const [savingId, setSavingId] = useState(null)
  const { user } = useAuth()
  const { profile, loading: profileLoading, missing } = useCurrentProfile()
  const { notify } = useToast()
  const request = useCallback(() => jobApi.search(filters), [filters])
  const { data: jobs, loading, error, refetch } = useApi(request, [request])

  const submitSearch = (event) => {
    event.preventDefault()
    setFilters(Object.fromEntries(Object.entries(form).filter(([, value]) => value.trim())))
  }

  const save = async (job) => {
    if (missing) {
      notify('Complete your profile before saving jobs.', 'error')
      return
    }
    if (!profile?.id) return
    setSavingId(job.id)
    try {
      await basketApi.add({ jobSeekerId: profile.id, jobId: job.id })
      notify('Job added to your saved list.')
    } catch (requestError) {
      if (!requestError.toastShown) {
        notify(requestError.response?.status === 409 ? 'You already saved this role.' : 'Could not save this job.', 'error')
      }
    } finally {
      setSavingId(null)
    }
  }

  return (
    <>
      <PageHeader eyebrow="The next chapter" title="Find work that fits." description="A thoughtful search starts with what matters to you. Explore open roles from teams looking for their next good hire." />
      <Card className="mb-7 p-4 sm:p-5">
        <form onSubmit={submitSearch} className="grid gap-3 sm:grid-cols-2 lg:grid-cols-[1.1fr_1fr_1fr_1fr_auto] lg:items-end">
          <Input label="Role or title" placeholder="Product designer" value={form.title} onChange={(event) => setForm({ ...form, title: event.target.value })} />
          <Input label="Location" placeholder="Remote, Portland…" value={form.location} onChange={(event) => setForm({ ...form, location: event.target.value })} />
          <Input label="Skills" placeholder="Research, React…" value={form.skills} onChange={(event) => setForm({ ...form, skills: event.target.value })} />
          <Input label="Company" placeholder="Company name" value={form.company} onChange={(event) => setForm({ ...form, company: event.target.value })} />
          <Button type="submit" className="h-[46px]"><Search size={16} /> Search</Button>
        </form>
      </Card>
      <div className="mb-4 flex items-center justify-between">
        <p className="text-sm font-semibold text-ink">{loading ? 'Finding roles…' : `${jobs?.length || 0} ${jobs?.length === 1 ? 'role' : 'roles'} to explore`}</p>
        <span className="inline-flex items-center gap-1.5 text-xs text-muted"><SlidersHorizontal size={14} /> Most relevant</span>
      </div>
      {profileLoading && <Spinner label="Checking your profile…" className="py-4" />}
      {error ? (
        <Card className="p-8 text-center">
          <p className="font-semibold text-ink">We couldn’t load the job board.</p>
          <p className="mt-2 text-sm text-muted">{error}</p>
          <Button variant="secondary" className="mt-5" onClick={() => refetch().catch(() => {})}>Try again</Button>
        </Card>
      ) : loading ? (
        <div className="grid gap-4 md:grid-cols-2">{[1, 2, 3, 4].map((item) => <JobSkeleton key={item} />)}</div>
      ) : jobs?.length ? (
        <div className="grid gap-4 md:grid-cols-2">
          {jobs.map((job) => <JobCard key={job.id} job={job} onSave={user?.role === 'JOB_SEEKER' ? save : undefined} saving={savingId === job.id} />)}
        </div>
      ) : (
        <EmptyState title="No roles found just yet" description="Try widening your search or check back soon. The right fit can take a little time." action={<Button variant="secondary" onClick={() => { setForm({ title: '', location: '', skills: '', company: '' }); setFilters({}) }}>Clear filters</Button>} />
      )}
    </>
  )
}

export function JobDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()
  const { profile, loading: profileLoading, missing } = useCurrentProfile()
  const { notify } = useToast()
  const [coverLetter, setCoverLetter] = useState('')
  const [applying, setApplying] = useState(false)
  const request = useCallback(() => jobApi.getById(id), [id])
  const { data: job, loading, error, refetch } = useApi(request, [request])

  const apply = async () => {
    if (!job || !profile?.id) return
    setApplying(true)
    try {
      await applicationApi.apply({
        jobId: job.id,
        jobSeekerId: profile.id,
        employerId: job.employerId,
        coverLetter,
      })
      notify('Your application has been sent.')
      navigate('/applications')
    } catch (requestError) {
      if (!requestError.toastShown) notify(getApiError(requestError), 'error')
    } finally {
      setApplying(false)
    }
  }

  if (loading) return <Spinner label="Loading role details…" />
  if (error) return <Card className="p-8 text-center"><h2 className="font-display text-2xl text-olive-900">This role isn’t available.</h2><p className="mt-2 text-sm text-muted">{error}</p><Button variant="secondary" className="mt-5" onClick={() => refetch().catch(() => {})}>Try again</Button></Card>
  if (!job) return null

  const seeker = user?.role === 'JOB_SEEKER'
  return (
    <div className="mx-auto max-w-4xl">
      <Link to="/jobs" className="mb-5 inline-flex text-sm font-semibold text-olive-700 hover:text-olive-500">← Back to roles</Link>
      <Card className="overflow-hidden">
        <div className="bg-olive-900 px-6 py-9 text-cream-50 sm:px-10 sm:py-12">
          <Badge tone="accent">{job.jobType?.replaceAll('_', ' ') || 'Open role'}</Badge>
          <h1 className="mt-4 font-display text-3xl font-semibold sm:text-4xl">{job.title}</h1>
          <p className="mt-2 text-sm text-olive-100">{job.companyName || 'Company'} · {job.location || 'Location flexible'}</p>
          <div className="mt-5 flex flex-wrap gap-2">{job.salaryRange && <span className="rounded-full bg-white/10 px-3 py-1.5 text-xs">{job.salaryRange}</span>}{job.experienceRequired != null && <span className="rounded-full bg-white/10 px-3 py-1.5 text-xs">{job.experienceRequired}+ years experience</span>}</div>
        </div>
        <div className="grid gap-8 p-6 sm:p-10 lg:grid-cols-[1fr_19rem]">
          <article>
            <h2 className="font-display text-2xl font-semibold text-olive-900">About the role</h2>
            <p className="mt-4 whitespace-pre-line text-sm leading-7 text-muted">{job.description || 'The team has not added a full description yet.'}</p>
            <h3 className="mt-8 font-display text-xl font-semibold text-olive-900">What you’ll bring</h3>
            <div className="mt-3 flex flex-wrap gap-2">{(job.skillsRequired || '').split(',').map((skill) => skill.trim()).filter(Boolean).map((skill) => <Badge key={skill} tone="neutral">{skill}</Badge>)}</div>
          </article>
          <aside className="h-fit rounded-2xl border border-olive-100 bg-cream-50 p-5">
            <h2 className="font-display text-xl font-semibold text-olive-900">{seeker ? 'Make it yours' : 'Interested in this role?'}</h2>
            {seeker ? (
              profileLoading ? <Spinner label="Loading profile…" className="py-6" /> : missing ? (
                <><p className="mt-2 text-sm leading-6 text-muted">Complete your candidate profile before sending an application.</p><Button className="mt-5 w-full" onClick={() => navigate('/profile?setup=1')}>Complete profile</Button></>
              ) : (
                <>
                  <label className="mt-4 block text-sm font-semibold text-ink" htmlFor="cover-letter">A note to the team <span className="font-normal text-muted">(optional)</span></label>
                  <textarea id="cover-letter" value={coverLetter} onChange={(event) => setCoverLetter(event.target.value)} rows={5} maxLength={4000} placeholder="Why does this role feel right?" className="mt-2 w-full resize-y rounded-xl border border-olive-200 bg-white px-3.5 py-3 text-sm text-ink outline-none transition placeholder:text-muted/70 focus:border-olive-500 focus:ring-4 focus:ring-olive-100" />
                  <Button loading={applying} className="mt-4 w-full" onClick={apply}>Apply for this role <ArrowRight size={16} /></Button>
                </>
              )
            ) : <><p className="mt-2 text-sm leading-6 text-muted">Explore this role and see the candidates who’ve applied from your applications list.</p><Button variant="secondary" className="mt-5 w-full" onClick={() => navigate('/applications')}>View applications</Button></>}
          </aside>
        </div>
      </Card>
    </div>
  )
}
