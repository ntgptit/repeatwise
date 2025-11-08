export type WeekdayIndex = 0 | 1 | 2 | 3 | 4 | 5 | 6

export type CalendarSelectionMode = 'single'

export interface CalendarProps {
  value?: Date | null
  onChange?: (date: Date) => void
  minDate?: Date
  maxDate?: Date
  disabledDates?: (date: Date) => boolean
  locale?: string
  weekStartsOn?: WeekdayIndex
  showOutsideDays?: boolean
  className?: string
}

export interface CalendarHeaderProps {
  month: Date
  locale?: string
  onPrevMonth: () => void
  onNextMonth: () => void
  onToday?: () => void
}

export interface CalendarDayInfo {
  date: Date
  label: string
  isToday: boolean
  isSelected: boolean
  isOutsideMonth: boolean
  isDisabled: boolean
}

export interface CalendarDayProps {
  day: CalendarDayInfo
  onSelect: (date: Date) => void
}

export interface CalendarGridProps {
  weeks: CalendarDayInfo[][]
  onSelect: (date: Date) => void
  locale?: string
}
