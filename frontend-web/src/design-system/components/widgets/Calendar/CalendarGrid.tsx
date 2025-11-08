import { useMemo } from 'react'
import type { CalendarGridProps } from './Calendar.types'
import { CalendarDay } from './CalendarDay'

const formatWeekday = (date: Date, locale?: string): string => {
  return new Intl.DateTimeFormat(locale ?? undefined, { weekday: 'short' })
    .format(date)
    .toUpperCase()
}

export const CalendarGrid = ({ weeks, onSelect, locale }: CalendarGridProps): JSX.Element => {
  const weekdayLabels = useMemo(() => {
    if (weeks.length === 0 || weeks[0].length === 0) {
      return []
    }

    return weeks[0].map(day => formatWeekday(day.date, locale))
  }, [weeks, locale])

  return (
    <div className="space-y-1">
      <div className="grid grid-cols-7 text-center text-xs font-semibold uppercase text-muted-foreground">
        {weekdayLabels.map(label => (
          <span key={label}>{label}</span>
        ))}
      </div>
      <div className="grid grid-cols-7 gap-1 text-center">
        {weeks.map(week =>
          week.map(day => (
            <CalendarDay key={day.date.toISOString()} day={day} onSelect={onSelect} />
          ))
        )}
      </div>
    </div>
  )
}

export default CalendarGrid
