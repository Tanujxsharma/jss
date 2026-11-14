import { useCallback, useState } from 'react'
import { Search, UserRound } from 'lucide-react'
import { profileApi } from '../api/profileApi'
import { Button } from '../components/ui/Button'
import { Card } from '../components/ui/Card'
import { EmptyState } from '../components/ui/EmptyState'
import { Input } from '../components/ui/Input'
import { PageHeader } from '../components/ui/PageHeader'
import { Spinner } from '../components/ui/Spinner'
import { useApi } from '../hooks/useApi'
import { initials } from '../utils/format'

export function CandidatesPage() {
  const [values, setValues] = useState({ skills: '', location: '', minExperience: '' })
  const [filters, setFilters] = useState({})
  const request = useCallback(() => profileApi.searchCandidates(filters), [filters])
  const { data: candidates, loading, error, refetch } = useApi(request, [request])

  const submit = (event) => {
    event.preventDefault()
    setFilters(Object.fromEntries(Object.entries(values).filter(([, value]) => value.trim())))
  }

  return (
    <>
      <PageHeader eyebrow="Find your next teammate" title="Meet the talent." description="Search candidate profiles by their skills, location, and experience." />
      <Card className="mb-6 p-4 sm:p-5">
        <form onSubmit={submit} className="grid gap-3 sm:grid-cols-2 lg:grid-cols-[1fr_1fr_1fr_auto] lg:items-end">
          <Input label="Skills" placeholder="Java, product design…" value={values.skills} onChange={(event) => setValues({ ...values, skills: event.target.value })} />
          <Input label="Location" placeholder="City or region" value={values.location} onChange={(event) => setValues({ ...values, location: event.target.value })} />
          <Input label="Minimum experience" type="number" min="0" step="0.5" placeholder="Years" value={values.minExperience} onChange={(event) => setValues({ ...values, minExperience: event.target.value })} />
          <Button type="submit"><Search size={16} /> Find talent</Button>
        </form>
      </Card>
      {loading ? <Spinner label="Searching candidate profiles…" /> : error ? (
        <Card className="p-8 text-center"><p className="font-semibold text-ink">Candidate search isn’t available.</p><p className="mt-2 text-sm text-muted">{error}</p><Button variant="secondary" className="mt-5" onClick={() => refetch().catch(() => {})}>Try again</Button></Card>
      ) : candidates?.length ? (
        <div className="grid gap-4 md:grid-cols-2">
          {candidates.map((candidate) => (
            <Card key={candidate.id} className="p-5 sm:p-6">
              <div className="flex items-start gap-3.5">
                <span className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-olive-100 font-display text-lg font-semibold text-olive-800">{initials(candidate.fullName)}</span>
                <div className="min-w-0 flex-1"><h2 className="font-display text-xl font-semibold text-olive-900">{candidate.fullName}</h2><p className="mt-1 truncate text-sm text-muted">{candidate.location || 'Location not listed'} · {candidate.experienceYears ?? 0} years experience</p></div>
                <span className="text-olive-600"><UserRound size={18} /></span>
              </div>
              <p className="mt-4 line-clamp-3 text-sm leading-6 text-muted">{candidate.summary || 'This candidate has not added a summary yet.'}</p>
              <p className="mt-4 text-xs font-semibold uppercase tracking-wide text-olive-700">Skills</p>
              <p className="mt-1 text-sm text-ink">{candidate.skills || 'Not listed'}</p>
              {candidate.email && <a className="mt-4 inline-block text-sm font-semibold text-olive-800 underline" href={`mailto:${candidate.email}`}>{candidate.email}</a>}
            </Card>
          ))}
        </div>
      ) : <EmptyState title="No profiles match those filters" description="Try searching with a broader skill or location to discover more candidates." action={<Button variant="secondary" onClick={() => { setValues({ skills: '', location: '', minExperience: '' }); setFilters({}) }}>Clear filters</Button>} />}
    </>
  )
}
