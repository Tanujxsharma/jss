import { Link } from 'react-router-dom'
import { ArrowUpRight, Sprout } from 'lucide-react'
import { Button } from '../ui/Button'
import { useAuth } from '../../context/AuthContext'

export function PublicNav() {
  const { isAuthenticated } = useAuth()
  return (
    <header className="sticky top-0 z-40 border-b border-olive-100 bg-cream-50/90 backdrop-blur-xl">
      <nav className="mx-auto flex max-w-7xl items-center justify-between px-5 py-4 lg:px-8" aria-label="Main navigation">
        <Link to="/" className="flex items-center gap-2.5">
          <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-olive-800 text-cream-50">
            <Sprout size={19} />
          </span>
          <span className="font-display text-xl font-bold tracking-tight text-olive-900">goodwork<span className="text-accent-500">.</span></span>
        </Link>
        <div className="flex items-center gap-3">
          <Link to="/jobs" className="hidden text-sm font-medium text-muted transition hover:text-olive-800 sm:inline">Explore jobs</Link>
          {isAuthenticated ? (
            <Button to="/dashboard" size="sm">Open dashboard <ArrowUpRight size={15} /></Button>
          ) : (
            <>
              <Link to="/auth/login" className="hidden text-sm font-semibold text-olive-800 transition hover:text-olive-600 sm:inline">Log in</Link>
              <Button to="/auth/register" variant="accent" size="sm">Get started <ArrowUpRight size={15} /></Button>
            </>
          )}
        </div>
      </nav>
    </header>
  )
}
