export function Card({ children, className = '', ...props }) {
  return (
    <section
      className={`rounded-2xl border border-olive-100 bg-white shadow-soft ${className}`}
      {...props}
    >
      {children}
    </section>
  )
}
