import { forwardRef } from 'react'

export const Input = forwardRef(function Input(
  { label, error, id, className = '', hint, ...props },
  ref,
) {
  const inputId = id || props.name
  return (
    <div className="space-y-1.5">
      {label && (
        <label htmlFor={inputId} className="block text-sm font-semibold text-ink">
          {label}
        </label>
      )}
      <input
        ref={ref}
        id={inputId}
        aria-invalid={Boolean(error)}
        aria-describedby={error ? `${inputId}-error` : undefined}
        className={`w-full rounded-xl border bg-white px-3.5 py-3 text-sm text-ink outline-none transition placeholder:text-muted/70 focus:border-olive-500 focus:ring-4 focus:ring-olive-100 ${error ? 'border-red-400' : 'border-olive-200'} ${className}`}
        {...props}
      />
      {hint && !error && <p className="text-xs text-muted">{hint}</p>}
      {error && (
        <p id={`${inputId}-error`} className="text-xs font-medium text-red-700">
          {error}
        </p>
      )}
    </div>
  )
})
