import { useEffect } from 'react'
import { X } from 'lucide-react'

export function Modal({ open, onClose, title, children }) {
  useEffect(() => {
    if (!open) return undefined
    const closeOnEscape = (event) => {
      if (event.key === 'Escape') onClose()
    }
    window.addEventListener('keydown', closeOnEscape)
    return () => window.removeEventListener('keydown', closeOnEscape)
  }, [open, onClose])

  if (!open) return null
  return (
    <div
      className="fixed inset-0 z-[70] flex items-center justify-center bg-olive-900/45 p-4"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) onClose()
      }}
      role="presentation"
    >
      <section className="animate-fade-in w-full max-w-lg rounded-2xl bg-white p-6 shadow-card" role="dialog" aria-modal="true" aria-labelledby="modal-title">
        <div className="mb-5 flex items-center justify-between gap-4">
          <h2 id="modal-title" className="font-display text-2xl font-semibold text-ink">{title}</h2>
          <button onClick={onClose} className="rounded-lg p-2 text-muted hover:bg-olive-50 hover:text-ink" aria-label="Close dialog">
            <X size={18} />
          </button>
        </div>
        {children}
      </section>
    </div>
  )
}
