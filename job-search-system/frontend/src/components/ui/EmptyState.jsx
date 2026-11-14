import { BriefcaseBusiness } from 'lucide-react'

export function EmptyState({ title, description, action }) {
  return (
    <div className="rounded-2xl border border-dashed border-olive-200 bg-cream-50 px-6 py-12 text-center">
      <span className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-olive-100 text-olive-700">
        <BriefcaseBusiness size={22} />
      </span>
      <h3 className="font-display text-xl font-semibold text-ink">{title}</h3>
      <p className="mx-auto mt-2 max-w-md text-sm leading-6 text-muted">{description}</p>
      {action && <div className="mt-5">{action}</div>}
    </div>
  )
}
