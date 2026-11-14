import { useState } from 'react'
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom'
import {
  BriefcaseBusiness,
  ChevronDown,
  CircleUserRound,
  LogOut,
  Menu,
  MessageSquareText,
  Search,
  Sprout,
  UserRoundSearch,
  X,
} from 'lucide-react'
import { useAuth } from '../../context/AuthContext'
import { initials } from '../../utils/format'

export function AppLayout() {
  const { user, signOut } = useAuth()
  const [menuOpen, setMenuOpen] = useState(false)
  const [userMenuOpen, setUserMenuOpen] = useState(false)
  const navigate = useNavigate()
  const employer = user?.role === 'EMPLOYER'

  const links = employer
    ? [
        { to: '/dashboard', label: 'Overview', icon: BriefcaseBusiness },
        { to: '/employer/jobs', label: 'Your listings', icon: Search },
        { to: '/applications', label: 'Applications', icon: CircleUserRound },
        { to: '/candidates', label: 'Talent', icon: UserRoundSearch },
        { to: '/messages', label: 'Messages', icon: MessageSquareText },
      ]
    : [
        { to: '/dashboard', label: 'Overview', icon: BriefcaseBusiness },
        { to: '/jobs', label: 'Find a job', icon: Search },
        { to: '/applications', label: 'Applications', icon: CircleUserRound },
        { to: '/saved', label: 'Saved jobs', icon: Sprout },
        { to: '/messages', label: 'Messages', icon: MessageSquareText },
      ]

  const logOut = () => {
    signOut()
    navigate('/')
  }

  return (
    <div className="min-h-screen bg-cream-50">
      <header className="sticky top-0 z-40 border-b border-olive-100 bg-cream-50/95 backdrop-blur-xl">
        <div className="mx-auto flex h-[68px] max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
          <div className="flex items-center gap-8">
            <Link to="/dashboard" className="flex items-center gap-2.5">
              <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-olive-800 text-cream-50"><Sprout size={19} /></span>
              <span className="font-display text-xl font-bold tracking-tight text-olive-900">jobconnect<span className="text-accent-500">.</span></span>
            </Link>
            <nav className="hidden items-center gap-1 lg:flex" aria-label="Main navigation">
              {links.map(({ to, label }) => (
                <NavLink
                  key={to}
                  to={to}
                  end={to === '/dashboard'}
                  className={({ isActive }) => `rounded-lg px-3 py-2 text-sm font-medium transition ${isActive ? 'bg-olive-100 text-olive-900' : 'text-muted hover:bg-olive-50 hover:text-olive-800'}`}
                >
                  {label}
                </NavLink>
              ))}
            </nav>
          </div>
          <div className="relative flex items-center gap-2">
            <button
              onClick={() => setUserMenuOpen((open) => !open)}
              className="hidden items-center gap-2 rounded-xl px-2 py-1.5 transition hover:bg-olive-50 sm:flex"
              aria-expanded={userMenuOpen}
              aria-label="Open account menu"
            >
              <span className="flex h-9 w-9 items-center justify-center rounded-full bg-olive-100 text-xs font-bold text-olive-800">{initials(user?.email || '')}</span>
              <span className="max-w-36 text-left">
                <span className="block truncate text-xs font-semibold text-ink">{user?.email}</span>
                <span className="block text-[11px] text-muted">{employer ? 'Employer account' : 'Job seeker account'}</span>
              </span>
              <ChevronDown size={15} className="text-muted" />
            </button>
            <button
              className="rounded-lg p-2 text-olive-800 hover:bg-olive-50 lg:hidden"
              onClick={() => setMenuOpen((open) => !open)}
              aria-label={menuOpen ? 'Close navigation' : 'Open navigation'}
              aria-expanded={menuOpen}
            >
              {menuOpen ? <X size={20} /> : <Menu size={20} />}
            </button>
            {userMenuOpen && (
              <div className="absolute right-0 top-12 z-50 w-48 rounded-xl border border-olive-100 bg-white p-2 shadow-card">
                <Link to="/profile" onClick={() => setUserMenuOpen(false)} className="block rounded-lg px-3 py-2 text-sm text-ink hover:bg-olive-50">Your profile</Link>
                <button onClick={logOut} className="flex w-full items-center gap-2 rounded-lg px-3 py-2 text-left text-sm text-red-700 hover:bg-red-50"><LogOut size={15} /> Sign out</button>
              </div>
            )}
          </div>
        </div>
        {menuOpen && (
          <nav className="border-t border-olive-100 bg-white px-4 py-3 lg:hidden" aria-label="Mobile navigation">
            <div className="mx-auto grid max-w-7xl grid-cols-2 gap-1">
              {links.map(({ to, label, icon: Icon }) => (
                <NavLink key={to} to={to} onClick={() => setMenuOpen(false)} className={({ isActive }) => `flex items-center gap-2 rounded-lg px-3 py-2.5 text-sm font-medium ${isActive ? 'bg-olive-100 text-olive-900' : 'text-muted hover:bg-olive-50'}`}>
                  <Icon size={16} />{label}
                </NavLink>
              ))}
              <Link to="/profile" onClick={() => setMenuOpen(false)} className="flex items-center gap-2 rounded-lg px-3 py-2.5 text-sm font-medium text-muted hover:bg-olive-50"><CircleUserRound size={16} /> Profile</Link>
              <button onClick={logOut} className="flex items-center gap-2 rounded-lg px-3 py-2.5 text-left text-sm font-medium text-red-700 hover:bg-red-50"><LogOut size={16} /> Sign out</button>
            </div>
          </nav>
        )}
      </header>
      <main className="animate-fade-in mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8 lg:py-10">
        <Outlet />
      </main>
      <footer className="border-t border-olive-100 px-5 py-6 text-center text-xs text-muted">
        Find your next chapter, one good thing at a time.
      </footer>
    </div>
  )
}

export function AuthShell({ children, eyebrow = 'A better kind of job search' }) {
  return (
    <div className="min-h-screen bg-cream-100">
      <div className="mx-auto grid min-h-screen max-w-6xl items-center gap-10 px-4 py-8 lg:grid-cols-[1fr_0.85fr] lg:px-10">
        <div className="hidden rounded-[2rem] bg-olive-900 p-10 text-cream-50 shadow-card lg:block">
          <Link to="/" className="inline-flex items-center gap-2 font-display text-xl font-semibold"><Sprout size={19} /> jobconnect<span className="text-accent-400">.</span></Link>
          <div className="mt-24 max-w-md">
            <p className="mb-4 text-xs font-bold uppercase tracking-[0.2em] text-olive-200">{eyebrow}</p>
            <h1 className="font-display text-5xl leading-[1.13]">Work should fit the life you want.</h1>
            <p className="mt-6 text-base leading-7 text-olive-100/80">A calmer place to find thoughtful teams, meaningful roles, and your next right step.</p>
          </div>
          <div className="mt-20 flex items-end justify-between">
            <p className="max-w-56 text-xs leading-5 text-olive-200">Less noise. Better work. A little more human.</p>
            <div className="relative h-32 w-32 rounded-full border border-olive-300/30 bg-olive-800">
              <div className="absolute right-1 top-2 h-16 w-16 rounded-full bg-accent-500/90" />
              <div className="absolute bottom-1 left-0 h-20 w-20 rounded-full bg-olive-500/70" />
            </div>
          </div>
        </div>
        <div className="mx-auto w-full max-w-md">
          <Link to="/" className="mb-8 inline-flex items-center gap-2 font-display text-xl font-semibold text-olive-900 lg:hidden"><Sprout size={19} /> jobconnect<span className="text-accent-500">.</span></Link>
          {children}
        </div>
      </div>
    </div>
  )
}
