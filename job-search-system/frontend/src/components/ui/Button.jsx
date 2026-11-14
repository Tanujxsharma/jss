import { LoaderCircle } from 'lucide-react'
import { Link } from 'react-router-dom'

const variants = {
  primary: 'bg-olive-700 text-white shadow-soft hover:bg-olive-800',
  accent: 'bg-accent-500 text-olive-900 shadow-soft hover:bg-accent-400',
  secondary: 'border border-olive-200 bg-white text-olive-800 hover:bg-olive-50',
  ghost: 'text-ink hover:bg-olive-50',
  danger: 'bg-red-700 text-white hover:bg-red-800',
}

export function Button({
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled = false,
  to,
  className = '',
  children,
  type = 'button',
  ...props
}) {
  const sizes = {
    sm: 'px-3 py-2 text-sm',
    md: 'px-4 py-2.5 text-sm',
    lg: 'px-5 py-3 text-base',
  }
  const classes = `inline-flex items-center justify-center gap-2 rounded-xl font-semibold transition-colors duration-150 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-accent-500 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60 ${variants[variant]} ${sizes[size]} ${className}`
  const content = (
    <>
      {loading && <LoaderCircle size={16} className="animate-spin" aria-hidden="true" />}
      {children}
    </>
  )
  if (to) {
    return <Link to={to} className={classes} aria-disabled={disabled || loading || undefined} {...props}>{content}</Link>
  }
  return <button type={type} className={classes} disabled={loading || disabled} {...props}>{content}</button>
}
