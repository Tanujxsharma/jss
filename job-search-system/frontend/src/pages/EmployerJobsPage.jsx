import { useCallback, useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { zodResolver } from '@hookform/resolvers/zod'
import { BriefcaseBusiness, Pencil, Plus, Trash2 } from 'lucide-react'
import { useForm } from 'react-hook-form'
import { jobApi } from '../api/jobApi'
import { getApiError } from '../api/axiosClient'
import { Badge } from '../components/ui/Badge'
import { Button } from '../components/ui/Button'
import { Card } from '../components/ui/Card'
import { EmptyState } from '../components/ui/EmptyState'
import { Input } from '../components/ui/Input'
import { Modal } from '../components/ui/Modal'
import { PageHeader } from '../components/ui/PageHeader'
import { Select } from '../components/ui/Select'
import { Spinner } from '../components/ui/Spinner'
import { useToast } from '../context/ToastContext'
import { useApi } from '../hooks/useApi'
import { useCurrentProfile } from '../hooks/useCurrentProfile'
import { formatDate } from '../utils/format'
import { jobSchema } from '../utils/schemas'

export function EmployerJobsPage() {
  const { profile, loading: profileLoading, missing } = useCurrentProfile()
  const { notify } = useToast()
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [working, setWorking] = useState(null)
  const request = useCallback(
    () => profile ? jobApi.byEmployer(profile.id) : Promise.resolve({ data: [] }),
    [profile?.id],
  )
  const { data: jobs, loading, error, refetch } = useApi(request, [request])

  const runAction = async (job, action) => {
    setWorking(job.id)
    try {
      if (action === 'close') {
        await jobApi.close(job.id)
        notify('This role is now closed.')
      } else {
        await jobApi.remove(job.id)
        notify('The job listing has been deleted.')
      }
      setDeleteTarget(null)
      await refetch()
    } catch (requestError) {
      if (!requestError.toastShown) notify(getApiError(requestError), 'error')
    } finally {
      setWorking(null)
    }
  }

  return (
    <>
      <PageHeader
        eyebrow="Your hiring desk"
        title="Your listings."
        description="Every thoughtful role is a chance to meet someone great."
        action={<Button to="/employer/jobs/new"><Plus size={16} /> Post a role</Button>}
      />
      {profileLoading || loading ? <Spinner label="Loading your listings…" /> : missing ? (
        <EmptyState title="Create your organization profile" description="Your employer profile is needed before you can post or manage roles." />
      ) : error ? (
        <Card className="p-8 text-center"><p className="font-semibold text-ink">Your listings didn’t load.</p><p className="mt-2 text-sm text-muted">{error}</p><Button variant="secondary" className="mt-5" onClick={() => refetch().catch(() => {})}>Try again</Button></Card>
      ) : jobs?.length ? (
        <div className="space-y-3">
          {jobs.map((job) => (
            <Card key={job.id} className="flex flex-col justify-between gap-5 p-5 sm:flex-row sm:items-center sm:p-6">
              <div className="flex min-w-0 items-start gap-4">
                <span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-olive-100 text-olive-800"><BriefcaseBusiness size={19} /></span>
                <div className="min-w-0">
                  <h2 className="truncate font-display text-xl font-semibold text-olive-900">{job.title}</h2>
                  <p className="mt-1 text-sm text-muted">{job.location || 'Location flexible'} · Posted {formatDate(job.postedAt)}</p>
                  <div className="mt-2 flex flex-wrap items-center gap-2"><Badge>{job.status}</Badge>{job.jobType && <Badge tone="neutral">{job.jobType.replaceAll('_', ' ')}</Badge>}</div>
                </div>
              </div>
              <div className="flex flex-wrap gap-2 sm:justify-end">
                <Button to={`/jobs/${job.id}`} variant="secondary" size="sm">View role</Button>
                <Button to={`/employer/jobs/${job.id}/edit`} variant="secondary" size="sm"><Pencil size={14} /> Edit</Button>
                {job.status === 'ACTIVE' && <Button variant="ghost" size="sm" loading={working === job.id} onClick={() => runAction(job, 'close')}>Close</Button>}
                <Button variant="ghost" size="sm" className="!text-red-700 hover:!bg-red-50" onClick={() => setDeleteTarget(job)} aria-label={`Delete ${job.title}`}><Trash2 size={15} /></Button>
              </div>
            </Card>
          ))}
        </div>
      ) : <EmptyState title="Your first role starts here" description="Write a thoughtful listing and help the right person find their next chapter." action={<Button to="/employer/jobs/new"><Plus size={16} /> Post your first role</Button>} />}
      <Modal open={Boolean(deleteTarget)} onClose={() => setDeleteTarget(null)} title="Delete this listing?">
        <p className="text-sm leading-6 text-muted">“{deleteTarget?.title}” will be permanently removed. Applications already submitted will remain in your records.</p>
        <div className="mt-6 flex justify-end gap-3">
          <Button variant="secondary" onClick={() => setDeleteTarget(null)}>Keep listing</Button>
          <Button variant="danger" loading={working === deleteTarget?.id} onClick={() => runAction(deleteTarget, 'delete')}>Delete listing</Button>
        </div>
      </Modal>
    </>
  )
}

export function JobFormPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { notify } = useToast()
  const { profile, loading: profileLoading, missing } = useCurrentProfile()
  const [saving, setSaving] = useState(false)
  const request = useCallback(() => id ? jobApi.getById(id) : Promise.resolve({ data: null }), [id])
  const { data: job, loading, error } = useApi(request, [request])
  const { register, reset, handleSubmit, formState: { errors } } = useForm({
    resolver: zodResolver(jobSchema),
    defaultValues: {
      title: '', companyName: '', location: '', description: '',
      skillsRequired: '', experienceRequired: '', salaryRange: '', jobType: 'FULL_TIME',
    },
  })

  useEffect(() => {
    if (job) reset({
      title: job.title || '',
      companyName: job.companyName || '',
      location: job.location || '',
      description: job.description || '',
      skillsRequired: job.skillsRequired || '',
      experienceRequired: job.experienceRequired ?? '',
      salaryRange: job.salaryRange || '',
      jobType: job.jobType || 'FULL_TIME',
    })
  }, [job, reset])

  const submit = async (values) => {
    if (!profile?.id) return
    setSaving(true)
    try {
      const body = { ...values, employerId: profile.id, experienceRequired: values.experienceRequired ?? null }
      if (id) await jobApi.update(id, body)
      else await jobApi.create(body)
      notify(id ? 'Your listing has been updated.' : 'Your new role is live.')
      navigate('/employer/jobs')
    } catch (requestError) {
      if (!requestError.toastShown) notify(getApiError(requestError), 'error')
    } finally {
      setSaving(false)
    }
  }

  if (profileLoading || loading) return <Spinner label={id ? 'Loading listing…' : 'Checking your account…'} />
  if (error) return <Card className="p-7"><p className="font-semibold text-ink">This listing couldn’t be loaded.</p><p className="mt-2 text-sm text-muted">{error}</p></Card>
  if (missing) return <EmptyState title="Create your employer profile" description="Your organization profile is needed before you can post a role." action={<Button to="/profile?setup=1">Set up profile</Button>} />

  return (
    <>
      <PageHeader eyebrow="Your hiring desk" title={id ? 'Refine your role.' : 'Share a good opportunity.'} description="Clear, human details help the right people see themselves in this work." />
      <Card className="mx-auto max-w-3xl p-5 sm:p-8">
        <form onSubmit={handleSubmit(submit)} className="space-y-5" noValidate>
          <div className="grid gap-5 sm:grid-cols-2">
            <Input label="Job title" placeholder="Senior Product Designer" error={errors.title?.message} {...register('title')} />
            <Input label="Company" placeholder="Your organization" error={errors.companyName?.message} {...register('companyName')} />
            <Input label="Location" placeholder="Remote, city or region" error={errors.location?.message} {...register('location')} />
            <Select label="Employment type" error={errors.jobType?.message} {...register('jobType')}>
              <option value="FULL_TIME">Full time</option><option value="PART_TIME">Part time</option><option value="CONTRACT">Contract</option><option value="INTERNSHIP">Internship</option>
            </Select>
            <Input label="Experience required (years)" type="number" min="0" step="0.5" error={errors.experienceRequired?.message} {...register('experienceRequired')} />
            <Input label="Salary range" placeholder="e.g. $80k–$100k" error={errors.salaryRange?.message} {...register('salaryRange')} />
          </div>
          <Input label="Skills required" hint="Separate skills with commas." placeholder="Figma, research, prototyping" error={errors.skillsRequired?.message} {...register('skillsRequired')} />
          <div className="space-y-1.5">
            <label htmlFor="description" className="block text-sm font-semibold text-ink">Role description <span className="font-normal text-muted">(optional)</span></label>
            <textarea id="description" rows={8} {...register('description')} className="w-full rounded-xl border border-olive-200 bg-white px-3.5 py-3 text-sm text-ink outline-none transition placeholder:text-muted/70 focus:border-olive-500 focus:ring-4 focus:ring-olive-100" placeholder="Tell people about the work, the team, and what a good first year might look like…" />
          </div>
          <div className="flex justify-end gap-3 border-t border-olive-100 pt-5">
            <Button variant="secondary" onClick={() => navigate('/employer/jobs')}>Cancel</Button>
            <Button type="submit" loading={saving}>{id ? 'Save changes' : 'Publish role'}</Button>
          </div>
        </form>
      </Card>
    </>
  )
}
