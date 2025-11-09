import { useEffect, useMemo, useState, type JSX } from 'react'
import type {
  CalendarProps,
  CalendarDayInfo,
  CalendarGridProps,
  CalendarHeaderProps,
  WeekdayIndex,
} from './Calendar.types'
import { CalendarHeader } from './CalendarHeader'
import { CalendarGrid } from './CalendarGrid'

const toStartOfDay = (date: Date): Date => {
  const cloned = new Date(date)
  cloned.setHours(0, 0, 0, 0)
  return cloned
}

const startOfMonth = (date: Date): Date => new Date(date.getFullYear(), date.getMonth(), 1)
const endOfMonth = (date: Date): Date => new Date(date.getFullYear(), date.getMonth() + 1, 0)
const addDays = (date: Date, amount: number): Date =>
  new Date(date.getFullYear(), date.getMonth(), date.getDate() + amount)

const startOfWeek = (date: Date, weekStartsOn: WeekdayIndex): Date => {
  const start = toStartOfDay(date)
  const day = start.getDay() as WeekdayIndex
  const diff = (day - weekStartsOn + 7) % 7
  return addDays(start, -diff)
}

const endOfWeek = (date: Date, weekStartsOn: WeekdayIndex): Date => {
  const start = startOfWeek(date, weekStartsOn)
  return addDays(start, 6)
}

const isSameDay = (a: Date | null | undefined, b: Date | null | undefined): boolean => {
  if (!a || !b) {
    return false
  }
  return (
    a.getFullYear() === b.getFullYear() &&
    a.getMonth() === b.getMonth() &&
    a.getDate() === b.getDate()
  )
}

const isBefore = (date: Date, min?: Date): boolean => {
  if (!min) {
    return false
  }
  return toStartOfDay(date).getTime() < toStartOfDay(min).getTime()
}

const isAfter = (date: Date, max?: Date): boolean => {
  if (!max) {
    return false
  }
  return toStartOfDay(date).getTime() > toStartOfDay(max).getTime()
}

export const Calendar = ({
  value = null,
  onChange,
  minDate,
  maxDate,
  disabledDates,
  locale,
  weekStartsOn = 0,
  showOutsideDays = true,
  className,
}: CalendarProps): JSX.Element => {
  const today = useMemo(() => toStartOfDay(new Date()), [])
  const [visibleMonth, setVisibleMonth] = useState<Date>(() => toStartOfDay(value ?? today))

  useEffect(() => {
    if (!value) {
      return
    }

    const next = toStartOfDay(value)
    if (
      next.getFullYear() !== visibleMonth.getFullYear() ||
      next.getMonth() !== visibleMonth.getMonth()
    ) {
      setVisibleMonth(next)
    }
  }, [value, visibleMonth])

  const computedWeeks = useMemo(() => {
    const monthStart = startOfMonth(visibleMonth)
    const monthEnd = endOfMonth(visibleMonth)
    const gridStart = startOfWeek(monthStart, weekStartsOn)
    const gridEnd = endOfWeek(monthEnd, weekStartsOn)

    const days: CalendarDayInfo[][] = []
    let current = gridStart

    while (current <= gridEnd) {
      const week: CalendarDayInfo[] = []
      for (let i = 0; i < 7; i++) {
        const date = current
        const outside = date.getMonth() !== visibleMonth.getMonth()
        const disabledByRange = isBefore(date, minDate) || isAfter(date, maxDate)
        const disabledByPredicate = disabledDates ? disabledDates(date) : false
        const isDisabled =
          Boolean(disabledByPredicate) || disabledByRange || (!showOutsideDays && outside)

        week.push({
          date,
          label: outside && !showOutsideDays ? '' : date.getDate().toString(),
          isToday: isSameDay(date, today),
          isSelected: isSameDay(date, value ?? null),
          isOutsideMonth: outside,
          isDisabled,
        })
        current = addDays(current, 1)
      }
      days.push(week)
    }

    return days
  }, [visibleMonth, weekStartsOn, minDate, maxDate, disabledDates, showOutsideDays, today, value])

  const handleSelect = (date: Date) => {
    const info = toStartOfDay(date)
    if (isBefore(info, minDate) || isAfter(info, maxDate)) {
      return
    }

    if (disabledDates?.(info)) {
      return
    }

    if (!showOutsideDays && info.getMonth() !== visibleMonth.getMonth()) {
      return
    }

    setVisibleMonth(new Date(info.getFullYear(), info.getMonth(), 1))
    onChange?.(info)
  }

  const goToPrevMonth = () => {
    setVisibleMonth(prev => new Date(prev.getFullYear(), prev.getMonth() - 1, 1))
  }

  const goToNextMonth = () => {
    setVisibleMonth(prev => new Date(prev.getFullYear(), prev.getMonth() + 1, 1))
  }

  const goToToday = () => {
    setVisibleMonth(today)
    onChange?.(today)
  }

  const headerProps: CalendarHeaderProps = {
    month: visibleMonth,
    onPrevMonth: goToPrevMonth,
    onNextMonth: goToNextMonth,
    onToday: goToToday,
  }

  if (locale !== undefined) {
    headerProps.locale = locale
  }

  const gridProps: CalendarGridProps = {
    weeks: computedWeeks,
    onSelect: handleSelect,
  }

  if (locale !== undefined) {
    gridProps.locale = locale
  }

  return (
    <div
      className={['rounded-xl border border-border bg-background p-4 shadow-sm', className]
        .filter(Boolean)
        .join(' ')}
    >
      <CalendarHeader {...headerProps} />
      <CalendarGrid {...gridProps} />
    </div>
  )
}

export default Calendar
