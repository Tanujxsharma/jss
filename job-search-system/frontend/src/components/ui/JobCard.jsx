import { ArrowUpRight, Bookmark, Building2, MapPin } from 'lucide-react'
import { Link } from 'react-router-dom'
import { Badge } from './Badge'
import { Button } from './Button'
import { Card } from './Card'
import { formatDate, initials } from '../../utils/format'

export function JobCard({ job, onSave, saved = false, saving = false }) {
  const skills = (job.skillsRequired || '').split(',').map((skill) => skill.trim()).filter(Boolean).slice(0, 3)
  return (
    <Card className="p-5 transition duration-200 hover:-translate-y-0.5 hover:border-olive-200 hover:shadow-card sm:p-6">
      <div className="flex items-start justify-between gap-3">
        <div className="flex min-w-0 items-start gap-3.5">
          <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-olive-100 font-display text-lg font-semibold text-olive-800">{initials(job.companyName || job.title)}</div>
          <div className="min-w-0 pt-0.5">
            <Link to={`/jobs/${job.id}`} className="line-clamp-1 font-display text-xl font-semibold text-olive-900 hover:text-olive-700">{job.title}</Link>
            <p className="mt-1 flex items-center gap-1.5 truncate text-sm text-muted"><Building2 size={14} />{job.companyName || 'Company'}</p>
          </div>
        </div>
        {onSave && (
          <Button
            variant="ghost"
            size="sm"
            loading={saving}
            className="shrink-0 !px-2.5"
            onClick={() => onSave(job)}
            aria-label={saved ? 'Remove saved job' : 'Save job'}
          >
            <Bookmark size={17} fill={saved ? 'currentColor' : 'none'} />
          </Button>
        )}
      </div>
      <div className="mt-5 flex flex-wrap items-center gap-x-4 gap-y-2 text-xs text-muted">
        {job.location && <span className="inline-flex items-center gap-1.5"><MapPin size={14} />{job.location}</span>}
        {job.jobType && <Badge tone="neutral">{job.jobType.replaceAll('_', ' ')}</Badge>}
        {job.salaryRange && <span className="font-semibold text-olive-800">{job.salaryRange}</span>}
      </div>
      <p className="mt-4 line-clamp-2 text-sm leading-6 text-muted">{job.description || `Looking for someone with ${job.skillsRequired || 'your experience'} to join the team.`}</p>
      {skills.length > 0 && <div className="mt-4 flex flex-wrap gap-2">{skills.map((skill) => <span key={skill} className="rounded-full bg-olive-50 px-3 py-1 text-xs text-olive-800">{skill}</span>)}</div>}
      <div className="mt-5 flex items-center justify-between border-t border-olive-50 pt-4">
        <span className="text-xs text-muted">Posted {formatDate(job.postedAt)}</span>
        <Link to={`/jobs/${job.id}`} className="inline-flex items-center gap-1 text-sm font-semibold text-olive-800 hover:text-accent-600">View role <ArrowUpRight size={15} /></Link>
      </div>
    </Card>
  )
}
