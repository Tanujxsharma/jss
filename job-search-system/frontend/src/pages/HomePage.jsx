import { ArrowRight, ArrowUpRight, Check, Compass, Leaf, Search, Sparkles } from 'lucide-react'
import { Link } from 'react-router-dom'
import { PublicNav } from '../components/layout/PublicNav'
import { Button } from '../components/ui/Button'

const benefits = [
  { icon: Compass, title: 'Find your direction', text: 'Search roles by the things that matter: skills, place, and the kind of work you want to do.' },
  { icon: Leaf, title: 'Grow at your pace', text: 'Save promising opportunities and keep your applications together in one quiet corner.' },
  { icon: Sparkles, title: 'Meet better matches', text: 'A thoughtful profile helps the right teams see what makes your experience distinct.' },
]

export function HomePage() {
  return (
    <div className="min-h-screen bg-cream-50">
      <PublicNav />
      <main>
        <section className="relative overflow-hidden">
          <div className="absolute -right-32 -top-20 h-[30rem] w-[30rem] rounded-full bg-olive-100/70 blur-3xl" />
          <div className="absolute right-[15%] top-32 h-48 w-48 rounded-full border border-olive-200/80" />
          <div className="relative mx-auto grid max-w-7xl items-center gap-14 px-5 py-16 sm:py-24 lg:grid-cols-[1.05fr_0.95fr] lg:px-8 lg:py-28">
            <div className="animate-fade-in max-w-2xl">
              <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-olive-200 bg-white/80 px-3 py-1.5 text-xs font-semibold text-olive-800">
                <span className="h-2 w-2 rounded-full bg-accent-500" /> A more considered way to find work
              </div>
              <h1 className="font-display text-5xl font-semibold leading-[1.08] tracking-tight text-olive-900 sm:text-6xl lg:text-7xl">
                Your next chapter starts <span className="italic text-olive-500">somewhere good.</span>
              </h1>
              <p className="mt-6 max-w-lg text-base leading-7 text-muted sm:text-lg sm:leading-8">
                Find work that fits your life, not the other way around. Thoughtful roles, genuine teams, and a little less noise.
              </p>
              <div className="mt-8 flex flex-wrap items-center gap-3">
                <Button to="/auth/register" variant="accent" size="lg">Find your next role <ArrowRight size={17} /></Button>
                <Link to="/auth/register?role=EMPLOYER" className="inline-flex items-center gap-1.5 px-3 py-3 text-sm font-semibold text-olive-800 hover:text-olive-600">I’m hiring <ArrowUpRight size={15} /></Link>
              </div>
              <div className="mt-9 flex flex-wrap gap-x-6 gap-y-2 text-xs font-medium text-muted">
                {['A profile that feels like you', 'A clearer application journey', 'No endless scrolling'].map((item) => <span key={item} className="inline-flex items-center gap-1.5"><Check size={14} className="text-olive-600" />{item}</span>)}
              </div>
            </div>
            <div className="relative mx-auto w-full max-w-xl">
              <div className="relative aspect-[1.05] overflow-hidden rounded-[2.5rem] bg-olive-800 p-6 shadow-card sm:p-8">
                <div className="absolute -right-8 -top-8 h-56 w-56 rounded-full bg-olive-600/70" />
                <div className="absolute bottom-[-5rem] left-[-4rem] h-72 w-72 rounded-full bg-olive-700/80" />
                <div className="absolute right-10 top-12 h-32 w-32 rounded-full bg-accent-500 shadow-lg" />
                <div className="absolute bottom-12 left-9 right-9 rounded-2xl border border-white/30 bg-white/95 p-5 shadow-card sm:p-6">
                  <div className="flex items-start justify-between gap-3">
                    <div className="flex gap-3">
                      <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-olive-100 text-olive-800"><Search size={20} /></div>
                      <div><p className="font-semibold text-ink">A role that feels right</p><p className="mt-1 text-xs text-muted">Curated around what matters to you</p></div>
                    </div>
                    <span className="rounded-full bg-cream-200 px-2.5 py-1 text-[10px] font-bold uppercase tracking-wide text-olive-800">Good fit</span>
                  </div>
                  <div className="my-5 h-px bg-olive-100" />
                  <div className="flex items-center justify-between gap-3">
                    <div><p className="font-display text-2xl font-semibold text-olive-900">Product Designer</p><p className="mt-1 text-sm text-muted">Root &amp; Branch · Hybrid</p></div>
                    <span className="flex h-10 w-10 items-center justify-center rounded-full bg-accent-500 text-white"><ArrowUpRight size={18} /></span>
                  </div>
                  <div className="mt-5 flex gap-2"><span className="rounded-full bg-olive-50 px-3 py-1.5 text-xs text-olive-800">Design systems</span><span className="rounded-full bg-olive-50 px-3 py-1.5 text-xs text-olive-800">Research</span></div>
                </div>
                <div className="absolute left-7 top-8 rounded-xl border border-white/40 bg-white/90 px-3 py-2 shadow-soft">
                  <span className="text-xs font-semibold text-olive-900">Make room for good work</span>
                </div>
              </div>
              <div className="absolute -bottom-5 -left-2 rounded-xl border border-olive-100 bg-white px-4 py-3 shadow-soft sm:-left-8"><p className="font-display text-lg font-semibold text-olive-900">A little more human.</p><p className="text-xs text-muted">Built for people, not volume.</p></div>
            </div>
          </div>
        </section>
        <section className="border-y border-olive-100 bg-white/70">
          <div className="mx-auto grid max-w-7xl gap-8 px-5 py-14 sm:grid-cols-3 sm:gap-10 lg:px-8 lg:py-20">
            {benefits.map(({ icon: Icon, title, text }, index) => (
              <article key={title} className="animate-fade-in" style={{ animationDelay: `${index * 80}ms` }}>
                <span className="mb-5 flex h-11 w-11 items-center justify-center rounded-xl bg-olive-100 text-olive-800"><Icon size={20} /></span>
                <h2 className="font-display text-2xl font-semibold text-olive-900">{title}</h2>
                <p className="mt-3 max-w-sm text-sm leading-6 text-muted">{text}</p>
              </article>
            ))}
          </div>
        </section>
        <section className="mx-auto flex max-w-7xl flex-col items-start justify-between gap-6 px-5 py-16 sm:flex-row sm:items-center lg:px-8 lg:py-20">
          <div><p className="text-xs font-bold uppercase tracking-[0.16em] text-olive-600">Your next step</p><h2 className="mt-3 font-display text-3xl font-semibold text-olive-900 sm:text-4xl">Make space for what’s next.</h2></div>
          <Button to="/auth/register" variant="accent" size="lg">Create your free account <ArrowRight size={17} /></Button>
        </section>
      </main>
      <footer className="border-t border-olive-100 px-5 py-6 text-center text-xs text-muted">Goodwork · Made for meaningful work</footer>
    </div>
  )
}
