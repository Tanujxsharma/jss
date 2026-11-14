import { ArrowLeft } from 'lucide-react'
import { Button } from '../components/ui/Button'

export function NotFoundPage() {
  return (
    <div className="flex min-h-[65vh] flex-col items-center justify-center px-5 text-center">
      <p className="text-sm font-bold uppercase tracking-[0.18em] text-accent-600">404 · A small detour</p>
      <h1 className="mt-4 font-display text-5xl font-semibold text-olive-900">This page wandered off.</h1>
      <p className="mt-4 max-w-md text-sm leading-6 text-muted">The link may have changed, or this page may no longer be here. Let’s find your way back.</p>
      <Button to="/" className="mt-7"><ArrowLeft size={16} /> Back to JobConnect</Button>
    </div>
  )
}
