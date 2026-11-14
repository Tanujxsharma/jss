import { forwardRef } from 'react'

export const Select = forwardRef(function Select(
  { label, error, id, className = '', children, ...props },
  ref,
) {
  const selectId = id || props.name
  return (
    <div className="space-y-1.5">
      {label && (
        <label htmlFor={selectId} className="block text-sm font-semibold text-ink">
          {label}
        </label>
      )}
      <select
        ref={ref}
        id={selectId}
        aria-invalid={Boolean(error)}
        className={`w-full rounded-xl border bg-white px-3.5 py-3 text-sm text-ink outline-none transition focus:border-olive-500 focus:ring-4 focus:ring-olive-100 ${error ? 'border-red-400' : 'border-olive-200'} ${className}`}
        {...props}
      >
        {children}
      </select>
      {error && <p className="text-xs font-medium text-red-700">{error}</p>}
    </div>
  )
})
