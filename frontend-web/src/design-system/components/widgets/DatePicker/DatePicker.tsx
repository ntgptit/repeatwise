import { useEffect, useMemo, useRef, useState, useId } from 'react'
import clsx from 'clsx'
import { Calendar as CalendarIcon, X } from 'lucide-react'
import { Calendar } from '../Calendar/Calendar'
import type { DatePickerProps } from './DatePicker.types'
import { Button } from '@/design-system/components/primitives/Button'
import useClickOutside from '@/common/hooks/utils/useClickOutside'

const defaultFormat: Intl.DateTimeFormatOptions = { dateStyle: 'medium' }

export const DatePicker = ({
  value,
  defaultValue = null,
  onChange,
  locale,
  formatOptions,
  minDate,
  maxDate,
  disabledDates,
  weekStartsOn,
  showOutsideDays,
  withClearButton = true,
  label,
  helperText,
  error,
  fullWidth,
  className,
  disabled,
  ...rest
}: DatePickerProps): JSX.Element => {
  const isControlled = value !== undefined
  const [internalDate, setInternalDate] = useState<Date | null>(defaultValue)
  const [open, setOpen] = useState(false)
  const containerRef = useRef<HTMLDivElement>(null)
  const inputId = useId()

  const { placeholder, ...inputProps } = rest

  const selectedDate = isControlled ? value ?? null : internalDate

  useEffect(() => {
    if (isControlled && value === undefined) {
      setInternalDate(null)
    }
  }, [isControlled, value])

  useClickOutside(containerRef, () => setOpen(false), open)

  const formatter = useMemo(() => {
    const options = formatOptions ?? defaultFormat
    return new Intl.DateTimeFormat(locale ?? undefined, options)
  }, [locale, formatOptions])

  const formattedValue = selectedDate ? formatter.format(selectedDate) : ''

  const handleSelect = (date: Date) => {
    if (!isControlled) {
      setInternalDate(date)
    }
    onChange?.(date)
    setOpen(false)
  }

  const handleClear = () => {
    if (disabled) {
      return
    }
    if (!isControlled) {
      setInternalDate(null)
    }
    onChange?.(null)
  }

  const inputClasses = clsx(
    'w-full rounded-lg border border-input bg-background px-3 py-2 text-sm shadow-sm transition focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50',
    error && 'border-destructive focus:border-destructive focus:ring-destructive',
    fullWidth && 'w-full'
  )

  const helperClasses = error ? 'text-destructive text-xs mt-1' : 'text-muted-foreground text-xs mt-1'

  return (
    <div className={clsx('flex flex-col gap-2', fullWidth && 'w-full', className)} ref={containerRef}>
      {label ? (
        <label htmlFor={inputId} className={clsx('text-sm font-medium text-foreground', disabled && 'opacity-60')}>
          {label}
        </label>
      ) : null}
      <div className={clsx('relative flex items-center gap-2', fullWidth && 'w-full')}>
        <input
          id={inputId}
          type="text"
          readOnly
          value={formattedValue}
          className={inputClasses}
          placeholder={placeholder}
          onFocus={() => !disabled && setOpen(true)}
          onClick={() => !disabled && setOpen(true)}
          disabled={disabled}
          {...inputProps}
        />
        <Button
          type="button"
          variant="ghost"
          size="sm"
          className="absolute right-2 top-1/2 h-7 w-7 -translate-y-1/2 p-0 text-muted-foreground"
          onClick={() => !disabled && setOpen(prev => !prev)}
          aria-label="Select date"
          disabled={disabled}
        >
          <CalendarIcon className="h-4 w-4" />
        </Button>
        {withClearButton && selectedDate && !disabled ? (
          <Button
            type="button"
            variant="ghost"
            size="sm"
            className="absolute right-10 top-1/2 h-7 w-7 -translate-y-1/2 p-0 text-muted-foreground"
            onClick={handleClear}
            aria-label="Clear date"
          >
            <X className="h-4 w-4" />
          </Button>
        ) : null}
        {open ? (
          <div className="absolute left-0 top-full z-50 mt-2 w-full min-w-[280px] rounded-xl border border-border bg-background p-3 shadow-xl">
            <Calendar
              value={selectedDate}
              onChange={handleSelect}
              minDate={minDate}
              maxDate={maxDate}
              disabledDates={disabledDates}
              locale={locale}
              weekStartsOn={weekStartsOn}
              showOutsideDays={showOutsideDays}
            />
          </div>
        ) : null}
      </div>
      {helperText ? <span className={helperClasses}>{helperText}</span> : null}
      {error ? <span className="text-destructive text-xs">{error}</span> : null}
    </div>
  )
}

export default DatePicker
