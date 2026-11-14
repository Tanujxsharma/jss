import { zodResolver } from '@hookform/resolvers/zod'
import { useCallback, useState } from 'react'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { messageApi } from '../api/messageApi'
import { getApiError } from '../api/axiosClient'
import { Button } from '../components/ui/Button'
import { Card } from '../components/ui/Card'
import { EmptyState } from '../components/ui/EmptyState'
import { Input } from '../components/ui/Input'
import { PageHeader } from '../components/ui/PageHeader'
import { Spinner } from '../components/ui/Spinner'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { useApi } from '../hooks/useApi'
import { useCurrentProfile } from '../hooks/useCurrentProfile'
import { formatDate } from '../utils/format'

const messageSchema = z.object({
  participantId: z.coerce.number().int().positive('Enter a valid profile ID'),
  jobId: z.coerce.number().int().positive('Enter a valid job ID'),
  subject: z.string().trim().min(1, 'A subject is required'),
  messageDescription: z.string().trim().min(1, 'Write a short message'),
})

export function MessagesPage() {
  const { user } = useAuth()
  const { profile, loading: profileLoading, missing } = useCurrentProfile()
  const { notify } = useToast()
  const [sending, setSending] = useState(false)
  const employer = user.role === 'EMPLOYER'
  const request = useCallback(
    () => profile
      ? employer ? messageApi.employerMessages(profile.id) : messageApi.seekerMessages(profile.id)
      : Promise.resolve({ data: [] }),
    [profile?.id, employer],
  )
  const { data: messages, loading, error, refetch } = useApi(request, [request])
  const { register, handleSubmit, reset, formState: { errors } } = useForm({ resolver: zodResolver(messageSchema) })

  const submit = async ({ participantId, jobId, ...message }) => {
    if (!profile?.id) return
    const body = employer
      ? { ...message, employerId: profile.id, jobSeekerId: participantId, jobId }
      : { ...message, jobSeekerId: profile.id, employerId: participantId, jobId }
    setSending(true)
    try {
      if (employer) await messageApi.sendEmployer(body)
      else await messageApi.sendSeeker(body)
      reset()
      notify('Your message has been sent.')
      await refetch()
    } catch (requestError) {
      if (!requestError.toastShown) notify(getApiError(requestError), 'error')
    } finally {
      setSending(false)
    }
  }

  return (
    <>
      <PageHeader eyebrow="Keep in touch" title="Messages." description="A thoughtful note can be the start of a good conversation." />
      {profileLoading || loading ? <Spinner label="Loading your messages…" /> : missing ? (
        <EmptyState title="Set up your profile to message" description="Create your profile to send and receive messages." />
      ) : (
        <div className="grid gap-6 lg:grid-cols-[1.15fr_0.85fr]">
          <section>
            <h2 className="mb-3 font-display text-xl font-semibold text-olive-900">Messages you’ve sent</h2>
            {error ? <Card className="p-6 text-sm text-red-800" role="alert">{error} <button className="ml-1 font-semibold underline" onClick={() => refetch().catch(() => {})}>Try again</button></Card> : messages?.length ? (
              <div className="space-y-3">
                {messages.map((message) => (
                  <Card key={message.messageId} className="p-5">
                    <div className="flex flex-wrap items-start justify-between gap-3">
                      <div><h3 className="font-semibold text-ink">{message.subject}</h3><p className="mt-1 text-xs text-muted">Job #{message.jobId} · {formatDate(message.sentDate)}</p></div>
                      <span className="rounded-full bg-olive-50 px-2.5 py-1 text-xs font-medium text-olive-800">To #{employer ? message.jobSeekerId : message.employerId}</span>
                    </div>
                    <p className="mt-3 whitespace-pre-line text-sm leading-6 text-muted">{message.messageDescription}</p>
                  </Card>
                ))}
              </div>
            ) : <EmptyState title="Your inbox is quiet" description="Messages you send will be collected here." />}
          </section>
          <Card className="h-fit p-5 sm:p-6">
            <h2 className="font-display text-xl font-semibold text-olive-900">Write a message</h2>
            <p className="mt-2 text-sm leading-6 text-muted">Use the related job and profile IDs to address your note to the right person.</p>
            <form onSubmit={handleSubmit(submit)} className="mt-5 space-y-4" noValidate>
              <Input label={employer ? 'Job seeker profile ID' : 'Employer profile ID'} type="number" min="1" error={errors.participantId?.message} {...register('participantId')} />
              <Input label="Related job ID" type="number" min="1" error={errors.jobId?.message} {...register('jobId')} />
              <Input label="Subject" error={errors.subject?.message} {...register('subject')} />
              <div className="space-y-1.5">
                <label htmlFor="messageDescription" className="block text-sm font-semibold text-ink">Your message</label>
                <textarea id="messageDescription" rows={5} {...register('messageDescription')} className="w-full rounded-xl border border-olive-200 bg-white px-3.5 py-3 text-sm text-ink outline-none transition focus:border-olive-500 focus:ring-4 focus:ring-olive-100" />
                {errors.messageDescription && <p className="text-xs text-red-700">{errors.messageDescription.message}</p>}
              </div>
              <Button type="submit" className="w-full" loading={sending}>Send message</Button>
            </form>
          </Card>
        </div>
      )}
    </>
  )
}
