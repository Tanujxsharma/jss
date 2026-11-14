export function Table({ columns, rows, rowKey = 'id', empty }) {
  if (!rows.length) return empty || null
  return (
    <div className="overflow-x-auto">
      <table className="w-full min-w-[640px] border-collapse text-left text-sm">
        <thead>
          <tr className="border-b border-olive-100 text-xs uppercase tracking-wide text-muted">
            {columns.map((column) => <th key={column.key} className="px-4 py-3 font-semibold">{column.label}</th>)}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, index) => (
            <tr key={row[rowKey] ?? index} className={`border-b border-olive-50 last:border-0 ${index % 2 ? 'bg-cream-50/60' : 'bg-white'}`}>
              {columns.map((column) => (
                <td key={column.key} className="px-4 py-3.5 align-middle text-ink">
                  {column.render ? column.render(row) : row[column.key] ?? '—'}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
