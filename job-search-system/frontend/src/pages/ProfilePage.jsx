import { zodResolver } from '@hookform/resolvers/zod'
import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { profileApi } from '../api/profileApi'
import { getApiError } from '../api/axiosClient'
import { Button } from '../components/ui/Button'
import { Card } from '../components/ui/Card'
import { Input } from '../components/ui/Input'
import { PageHeader } from '../components/ui/PageHeader'
import { Spinner } from '../components/ui/Spinner'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { useCurrentProfile } from '../hooks/useCurrentProfile'
import { employerProfileSchema, seekerProfileSchema } from '../utils/schemas'

export function ProfilePage() {
  const { user } = useAuth()
  const { profile, setProfile, loading, error, missing, refetch } = useCurrentProfile()
  const { notify } = useToast()
  const [saving, setSaving] = useState(false)
  const [resumeBusy, setResumeBusy] = useState(false)
  const seeker = user.role === 'JOB_SEEKER'
  const schema = seeker ? seekerProfileSchema : employerProfileSchema
  const resumeUrl = profile?.resumeUrl
    ? new URL(
        profile.resumeUrl,
        import.meta.env.VITE_JOBSEEKER_SERVICE_URL || 'http://localhost:8084',
      ).toString()
    : ''
  const defaults = seeker
    ? { fullName: '', email: user.email, phone: '', location: '', skills: '', experienceYears: '', summary: '' }
    : { organizationName: '', email: user.email, contactNumber: '', location: '', about: '', website: '' }
  const { register, reset, handleSubmit, formState: { errors } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: defaults,
  })

  useEffect(() => {
    if (profile) reset({ ...defaults, ...profile })
    else if (missing) reset(defaults)
  // The defaults are derived from the signed-in user and profile shape.
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [profile, missing, reset])

  const submit = async (values) => {
    setSaving(true)
    try {
      const body = {
        ...values,
        userId: user.userId,
        ...(!seeker && { contactNumber: values.contactNumber || null }),
      }
      const response = seeker
        ? profile
          ? await profileApi.updateJobSeeker(profile.id, body)
          : await profileApi.createJobSeeker(body)
        : profile
          ? await profileApi.updateEmployer(profile.id, body)
          : await profileApi.createEmployer(body)
      setProfile(response.data)
      notify(profile ? 'Your profile has been updated.' : 'Your profile is ready.')
    } catch (requestError) {
      if (!requestError.toastShown) notify(getApiError(requestError), 'error')
    } finally {
      setSaving(false)
    }
  }

  const uploadResume = async (event) => {
    const file = event.target.files?.[0]
    event.target.value = ''
    if (!file || !profile?.id) return
    if (file.size > 10 * 1024 * 1024) {
      notify('Choose a file smaller than 10 MB.', 'error')
      return
    }
    setResumeBusy(true)
    try {
      const response = await profileApi.uploadResume(profile.id, file)
      setProfile(response.data)
      notify('Your resume has been uploaded.')
    } catch (requestError) {
      if (!requestError.toastShown) notify(getApiError(requestError), 'error')
    } finally {
      setResumeBusy(false)
    }
  }

  if (loading) return <Spinner label="Loading your profile…" />

  return (
    <>
      <PageHeader
        eyebrow={missing ? 'One small step' : 'Your space'}
        title={missing ? 'Let’s get your profile started.' : 'Your profile'}
        description={seeker ? 'Help good teams get to know the person behind the application.' : 'Share what makes your team a good place to work.'}
      />
      {error && <Card className="mb-5 p-4 text-sm text-red-800" role="alert">{error} <button className="ml-2 font-semibold underline" onClick={() => refetch().catch(() => {})}>Try again</button></Card>}
      <div className="grid gap-6 lg:grid-cols-[1fr_19rem]">
        <Card className="p-5 sm:p-8">
          <form onSubmit={handleSubmit(submit)} className="space-y-5" noValidate>
            {seeker ? (
              <>
                <div className="grid gap-5 sm:grid-cols-2">
                  <Input label="Full name" autoComplete="name" error={errors.fullName?.message} {...register('fullName')} />
                  <Input label="Email address" type="email" autoComplete="email" error={errors.email?.message} {...register('email')} />
                  <Input label="Phone number" type="tel" autoComplete="tel" error={errors.phone?.message} {...register('phone')} />
                  <Input label="Location" placeholder="City, region or remote" error={errors.location?.message} {...register('location')} />
                  <Input label="Years of experience" type="number" min="0" step="0.5" error={errors.experienceYears?.message} {...register('experienceYears')} />
                  <Input label="Skills" placeholder="Research, Figma, React…" hint="Separate skills with commas." error={errors.skills?.message} {...register('skills')} />
                </div>
                <div className="space-y-1.5">
                  <label htmlFor="summary" className="block text-sm font-semibold text-ink">A little about you</label>
                  <textarea id="summary" rows={5} {...register('summary')} className="w-full rounded-xl border border-olive-200 bg-white px-3.5 py-3 text-sm text-ink outline-none transition placeholder:text-muted/70 focus:border-olive-500 focus:ring-4 focus:ring-olive-100" placeholder="A short introduction, the work you enjoy, or what you’re looking for…" />
                  {errors.summary && <p className="text-xs text-red-700">{errors.summary.message}</p>}
                </div>
              </>
            ) : (
              <>
                <div className="grid gap-5 sm:grid-cols-2">
                  <Input label="Organization name" autoComplete="organization" error={errors.organizationName?.message} {...register('organizationName')} />
                  <Input label="Contact email" type="email" autoComplete="email" error={errors.email?.message} {...register('email')} />
                  <Input label="Contact number" type="tel" placeholder="10-digit number" error={errors.contactNumber?.message} {...register('contactNumber')} />
                  <Input label="Headquarters" placeholder="City, region or remote" error={errors.location?.message} {...register('location')} />
                  <Input label="Website" type="url" placeholder="https://example.com" error={errors.website?.message} {...register('website')} />
                </div>
                <div className="space-y-1.5">
                  <label htmlFor="about" className="block text-sm font-semibold text-ink">About your organization</label>
                  <textarea id="about" rows={5} {...register('about')} className="w-full rounded-xl border border-olive-200 bg-white px-3.5 py-3 text-sm text-ink outline-none transition placeholder:text-muted/70 focus:border-olive-500 focus:ring-4 focus:ring-olive-100" placeholder="What makes your team a good place to work?" />
                </div>
              </>
            )}
            <div className="flex flex-wrap items-center justify-between gap-3 border-t border-olive-100 pt-5">
              <p className="text-xs text-muted">{missing ? 'Your profile is required for applications and job listings.' : 'Your details are saved to your JobConnect profile.'}</p>
              <Button type="submit" loading={saving}>{profile ? 'Save changes' : 'Create profile'}</Button>
            </div>
          </form>
        </Card>
        {seeker && (
          <Card className="h-fit p-5">
            <h2 className="font-display text-xl font-semibold text-olive-900">Your resume</h2>
            <p className="mt-2 text-sm leading-6 text-muted">Keep your experience ready to share with teams.</p>
            {resumeUrl && <a className="mt-4 block truncate text-sm font-semibold text-olive-700 underline" href={resumeUrl} target="_blank" rel="noreferrer">View current resume</a>}
            <label className={`mt-5 flex cursor-pointer items-center justify-center rounded-xl border border-dashed border-olive-300 bg-cream-50 px-4 py-4 text-center text-sm font-semibold text-olive-800 transition hover:bg-olive-50 ${!profile?.id || resumeBusy ? 'pointer-events-none opacity-50' : ''}`}>
              {resumeBusy ? 'Uploading…' : resumeUrl ? 'Replace resume' : 'Upload a resume'}
              <input type="file" className="sr-only" accept=".pdf,.doc,.docx" disabled={!profile?.id || resumeBusy} onChange={uploadResume} />
            </label>
            <p className="mt-2 text-center text-xs text-muted">PDF or Word document · up to 10 MB</p>
          </Card>
        )}
      </div>
    </>
  )
}
