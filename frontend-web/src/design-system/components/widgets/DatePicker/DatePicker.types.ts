import type { InputHTMLAttributes } from 'react'
import type { CalendarProps } from '../Calendar/Calendar.types'

export interface DatePickerProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'value' | 'onChange'> {
  value?: Date | null
  defaultValue?: Date | null
  onChange?: (date: Date | null) => void
  locale?: string
  formatOptions?: Intl.DateTimeFormatOptions
  minDate?: Date
  maxDate?: Date
  disabledDates?: CalendarProps['disabledDates']
  weekStartsOn?: CalendarProps['weekStartsOn']
  showOutsideDays?: CalendarProps['showOutsideDays']
  withClearButton?: boolean
  label?: string
  helperText?: string
  error?: string
  fullWidth?: boolean
}
