import { useCallback, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { applicationApi } from '../api/applicationApi'
import { Button } from '../components/ui/Button'
import { Badge } from '../components/ui/Badge'
import { Card } from '../components/ui/Card'
import { EmptyState } from '../components/ui/EmptyState'
import { PageHeader } from '../components/ui/PageHeader'
import { Select } from '../components/ui/Select'
import { Spinner } from '../components/ui/Spinner'
import { Table } from '../components/ui/Table'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { useApi } from '../hooks/useApi'
import { useCurrentProfile } from '../hooks/useCurrentProfile'
import { formatDate } from '../utils/format'

const STATUSES = ['PENDING', 'SHORTLISTED', 'REJECTED', 'HIRED']

export function ApplicationsPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const { notify } = useToast()
  const { profile, loading: profileLoading, missing } = useCurrentProfile()
  const [updating, setUpdating] = useState(null)
  const employer = user.role === 'EMPLOYER'
  const request = useCallback(
    () => profile
      ? employer ? applicationApi.byEmployer(profile.id) : applicationApi.bySeeker(profile.id)
      : Promise.resolve({ data: [] }),
    [profile?.id, employer],
  )
  const { data: applications, loading, error, refetch } = useApi(request, [request])

  const updateStatus = async (application, status) => {
    setUpdating(application.id)
    try {
      await applicationApi.updateStatus(application.id, status)
      notify('Application status updated.')
      await refetch()
    } catch (requestError) {
      if (!requestError.toastShown) notify(requestError.userMessage || 'Could not update this application.', 'error')
    } finally {
      setUpdating(null)
    }
  }

  const columns = [
    { key: 'id', label: 'Application', render: (row) => <span className="font-semibold">#{row.id}</span> },
    { key: 'jobId', label: 'Role', render: (row) => <Link className="font-semibold text-olive-800 hover:underline" to={`/jobs/${row.jobId}`}>View job #{row.jobId}</Link> },
    { key: employer ? 'jobSeekerId' : 'employerId', label: employer ? 'Candidate ID' : 'Employer ID', render: (row) => employer ? `#${row.jobSeekerId}` : `#${row.employerId}` },
    { key: 'appliedAt', label: 'Applied', render: (row) => formatDate(row.appliedAt) },
    { key: 'status', label: 'Status', render: (row) => employer ? (
      <Select aria-label={`Update status for application ${row.id}`} value={row.status} disabled={updating === row.id} onChange={(event) => updateStatus(row, event.target.value)} className="!w-36 !py-2">
        {STATUSES.map((status) => <option key={status} value={status}>{status}</option>)}
      </Select>
    ) : <Badge>{row.status}</Badge> },
  ]

  return (
    <>
      <PageHeader eyebrow={employer ? 'Your hiring desk' : 'Your progress'} title={employer ? 'Applications received.' : 'Your applications.'} description={employer ? 'Every application is a person ready for their next chapter.' : 'Keep track of the roles you’ve put your hand up for.'} />
      {profileLoading ? <Spinner label="Loading profile…" /> : missing ? (
        <EmptyState title="Set up your profile to continue" description="Your profile is needed to view or manage applications." action={<Button onClick={() => navigate('/profile?setup=1')}>Complete profile</Button>} />
      ) : error ? (
        <Card className="p-8 text-center"><p className="font-semibold text-ink">Applications didn’t load.</p><p className="mt-2 text-sm text-muted">{error}</p><Button variant="secondary" className="mt-5" onClick={() => refetch().catch(() => {})}>Try again</Button></Card>
      ) : loading ? <Spinner label="Gathering applications…" /> : applications?.length ? (
        <Card className="overflow-hidden p-2 sm:p-4"><Table columns={columns} rows={applications} /></Card>
      ) : <EmptyState title="Nothing here yet" description={employer ? 'When someone applies to one of your roles, their application will appear here.' : 'The applications you send will be collected here.'} action={<Button to={employer ? '/employer/jobs/new' : '/jobs'}>{employer ? 'Post a role' : 'Explore jobs'}</Button>} />}
    </>
  )
}
