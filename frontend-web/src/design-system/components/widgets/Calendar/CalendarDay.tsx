import clsx from 'clsx'
import type { CalendarDayProps } from './Calendar.types'

const baseStyles =
  'relative flex h-9 w-9 items-center justify-center rounded-md text-sm font-medium transition-colors focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-40'

const selectedStyles = 'bg-primary text-primary-foreground shadow'
const todayIndicatorStyles =
  'after:absolute after:bottom-1 after:left-1/2 after:h-1 after:w-1 after:-translate-x-1/2 after:rounded-full after:bg-primary'

export const CalendarDay = ({ day, onSelect }: CalendarDayProps): JSX.Element => {
  const { date, label, isDisabled, isSelected, isOutsideMonth, isToday } = day

  return (
    <button
      type="button"
      className={clsx(
        baseStyles,
        isSelected && selectedStyles,
        !isSelected && !isOutsideMonth && 'hover:bg-muted/60',
        !isSelected && isOutsideMonth && 'text-muted-foreground/70',
        isToday && !isSelected && todayIndicatorStyles
      )}
      onClick={() => onSelect(date)}
      disabled={isDisabled}
      aria-pressed={isSelected}
      aria-label={date.toDateString()}
    >
      {label}
    </button>
  )
}

export default CalendarDay
