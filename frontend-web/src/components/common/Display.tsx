/**
 * Display Components
 * 
 * Progress bars, badges, and other display components
 */

import * as React from 'react'
import { cn } from '@/lib/utils'

interface ProgressBarProps {
  value: number
  max?: number
  label?: string
  showPercentage?: boolean
  className?: string
}

export function ProgressBar({
  value,
  max = 100,
  label,
  showPercentage = true,
  className,
}: ProgressBarProps) {
  const percentage = Math.min((value / max) * 100, 100)

  return (
    <div className={cn('space-y-2', className)}>
      {(label || showPercentage) && (
        <div className="flex items-center justify-between text-sm">
          {label && <span className="text-muted-foreground">{label}</span>}
          {showPercentage && (
            <span className="font-medium">{Math.round(percentage)}%</span>
          )}
        </div>
      )}
      <div className="h-2 w-full bg-muted rounded-full overflow-hidden">
        <div
          className="h-full bg-primary transition-all duration-300"
          style={{ width: `${percentage}%` }}
        />
      </div>
    </div>
  )
}

interface StatusBadgeProps {
  status: 'success' | 'warning' | 'error' | 'info' | 'default'
  label: string
  className?: string
}

export function StatusBadge({
  status,
  label,
  className,
}: StatusBadgeProps) {
  const variantStyles = {
    success: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
    warning:
      'bg-orange-100 text-orange-800 dark:bg-orange-900 dark:text-orange-200',
    error: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200',
    info: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200',
    default:
      'bg-muted text-muted-foreground',
  }

  return (
    <span
      className={cn(
        'inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium',
        variantStyles[status],
        className,
      )}
    >
      {label}
    </span>
  )
}

interface KeyboardShortcutsHintProps {
  shortcuts: Array<{ key: string; label: string }>
  className?: string
}

export function KeyboardShortcutsHint({
  shortcuts,
  className,
}: KeyboardShortcutsHintProps) {
  return (
    <div
      className={cn(
        'flex flex-wrap items-center gap-2 text-xs text-muted-foreground',
        className,
      )}
    >
      <span>Keyboard shortcuts:</span>
      {shortcuts.map((shortcut, index) => (
        <div key={index} className="flex items-center gap-1">
          <kbd className="pointer-events-none inline-flex h-5 select-none items-center gap-1 rounded border bg-muted px-1.5 font-mono text-[10px] font-medium text-muted-foreground opacity-100">
            {shortcut.key}
          </kbd>
          <span>=</span>
          <span>{shortcut.label}</span>
        </div>
      ))}
    </div>
  )
}
