import { useCallback } from 'react'
import { basketApi } from '../api/basketApi'
import { jobApi } from '../api/jobApi'
import { Button } from '../components/ui/Button'
import { Card } from '../components/ui/Card'
import { EmptyState } from '../components/ui/EmptyState'
import { JobCard } from '../components/ui/JobCard'
import { PageHeader } from '../components/ui/PageHeader'
import { Spinner } from '../components/ui/Spinner'
import { useToast } from '../context/ToastContext'
import { useApi } from '../hooks/useApi'
import { useCurrentProfile } from '../hooks/useCurrentProfile'

export function SavedJobsPage() {
  const { profile, loading: profileLoading, missing } = useCurrentProfile()
  const { notify } = useToast()
  const request = useCallback(async () => {
    if (!profile?.id) return { data: [] }
    const basketResponse = await basketApi.list(profile.id)
    const items = await Promise.all(basketResponse.data.map(async (item) => {
      try {
        const jobResponse = await jobApi.getById(item.jobId)
        return { basket: item, job: jobResponse.data }
      } catch (requestError) {
        if (requestError.response?.status === 404) return { basket: item, job: null }
        throw requestError
      }
    }))
    return { data: items }
  }, [profile?.id])
  const { data: items, loading, error, refetch } = useApi(request, [request])

  const remove = async (item) => {
    try {
      await basketApi.remove(item.basket.basketId)
      notify('Removed from your saved roles.')
      await refetch()
    } catch (requestError) {
      if (!requestError.toastShown) notify(requestError.userMessage || 'Could not remove this role.', 'error')
    }
  }

  return (
    <>
      <PageHeader eyebrow="Keep close" title="Saved jobs." description="A shortlist for the roles you want to come back to." />
      {profileLoading || loading ? <Spinner label="Gathering your saved jobs…" /> : missing ? (
        <EmptyState title="Create your profile first" description="Your candidate profile is needed to save jobs." />
      ) : error ? (
        <Card className="p-8 text-center"><p className="font-semibold text-ink">Your saved jobs didn’t load.</p><p className="mt-2 text-sm text-muted">{error}</p><Button variant="secondary" className="mt-5" onClick={() => refetch().catch(() => {})}>Try again</Button></Card>
      ) : items?.length ? (
        <div className="grid gap-4 md:grid-cols-2">
          {items.map((item) => item.job ? <div key={item.basket.basketId} className="space-y-2"><JobCard job={item.job} /><div className="flex justify-end"><Button size="sm" variant="ghost" onClick={() => remove(item)}>Remove saved role</Button></div></div> : (
            <Card key={item.basket.basketId} className="flex items-center justify-between gap-4 p-5">
              <p className="text-sm text-muted">Role #{item.basket.jobId} is no longer available.</p>
              <Button size="sm" variant="secondary" onClick={() => remove(item)}>Remove</Button>
            </Card>
          ))}
        </div>
      ) : <EmptyState title="Your shortlist is ready" description="Save a role when it catches your eye, and it’ll be waiting here when you’re ready." action={<Button to="/jobs">Explore open roles</Button>} />}
    </>
  )
}
