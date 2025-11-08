import { useMemo } from 'react'
import { ChevronLeft, ChevronRight } from 'lucide-react'
import { Button } from '@/design-system/components/primitives/Button'
import type { CalendarHeaderProps } from './Calendar.types'

const monthFormatter = (month: Date, locale?: string): string => {
  return new Intl.DateTimeFormat(locale ?? undefined, {
    month: 'long',
    year: 'numeric',
  }).format(month)
}

export const CalendarHeader = ({
  month,
  locale,
  onPrevMonth,
  onNextMonth,
  onToday,
}: CalendarHeaderProps): JSX.Element => {
  const label = useMemo(() => monthFormatter(month, locale), [month, locale])

  return (
    <div className="mb-3 flex items-center justify-between gap-2">
      <div className="flex items-center gap-2">
        <Button
          size="sm"
          variant="ghost"
          type="button"
          onClick={onPrevMonth}
          aria-label="Previous month"
        >
          <ChevronLeft className="h-4 w-4" />
        </Button>
        <Button
          size="sm"
          variant="ghost"
          type="button"
          onClick={onNextMonth}
          aria-label="Next month"
        >
          <ChevronRight className="h-4 w-4" />
        </Button>
      </div>
      <div className="flex items-center gap-2">
        <span className="font-semibold capitalize text-foreground">{label}</span>
        {onToday ? (
          <Button size="sm" variant="ghost" type="button" onClick={onToday}>
            Today
          </Button>
        ) : null}
      </div>
    </div>
  )
}

export default CalendarHeader
