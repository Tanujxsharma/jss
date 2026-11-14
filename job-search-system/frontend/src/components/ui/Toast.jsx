import { CheckCircle2, CircleAlert, X } from 'lucide-react'

export function Toast({ id, message, type = 'success', onClose }) {
  const Icon = type === 'error' ? CircleAlert : CheckCircle2
  return (
    <div
      key={id}
      className="animate-fade-in flex items-start gap-3 rounded-xl border border-olive-100 bg-white p-4 shadow-card"
      role={type === 'error' ? 'alert' : 'status'}
    >
      <Icon className={type === 'error' ? 'mt-0.5 text-red-600' : 'mt-0.5 text-olive-700'} size={18} />
      <p className="flex-1 text-sm text-ink">{message}</p>
      <button onClick={onClose} className="text-muted transition hover:text-ink" aria-label="Dismiss notification">
        <X size={16} />
      </button>
    </div>
  )
}
