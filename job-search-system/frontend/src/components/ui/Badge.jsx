const styles = {
  active: 'bg-olive-100 text-olive-800',
  closed: 'bg-stone-100 text-stone-700',
  pending: 'bg-cream-200 text-ink',
  shortlisted: 'bg-olive-100 text-olive-800',
  hired: 'bg-emerald-100 text-emerald-800',
  rejected: 'bg-red-100 text-red-800',
  neutral: 'bg-olive-50 text-muted',
  accent: 'bg-accent-400 text-olive-900',
}

export function Badge({ children, tone, className = '' }) {
  const key = tone || String(children || '').toLowerCase()
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-semibold ${styles[key] || styles.neutral} ${className}`}>
      {children}
    </span>
  )
}
