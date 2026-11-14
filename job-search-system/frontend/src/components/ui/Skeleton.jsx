export function Skeleton({ className = '' }) {
  return <div className={`animate-pulse rounded-lg bg-olive-100/80 ${className}`} aria-hidden="true" />
}

export function JobSkeleton() {
  return (
    <div className="rounded-2xl border border-olive-100 bg-white p-5 shadow-soft">
      <div className="flex items-start gap-4">
        <Skeleton className="h-12 w-12 rounded-xl" />
        <div className="flex-1 space-y-3">
          <Skeleton className="h-4 w-2/3" />
          <Skeleton className="h-3 w-1/3" />
          <Skeleton className="h-3 w-1/2" />
        </div>
      </div>
    </div>
  )
}
