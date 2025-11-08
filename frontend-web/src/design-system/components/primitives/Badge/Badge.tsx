import { forwardRef } from 'react'
import type { BadgeProps } from './Badge.types'

const variants = {
  default: 'bg-secondary text-secondary-foreground',
  success: 'bg-success text-success-foreground',
  warning: 'bg-warning text-warning-foreground',
  error: 'bg-destructive text-destructive-foreground',
  info: 'bg-info text-info-foreground',
}

export const Badge = forwardRef<HTMLSpanElement, BadgeProps>(
  ({ variant = 'default', className = '', children, ...props }, ref) => {
    const classes = [
      'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold',
      variants[variant],
      className,
    ]
      .filter(Boolean)
      .join(' ')

    return (
      <span ref={ref} className={classes} {...props}>
        {children}
      </span>
    )
  }
)

Badge.displayName = 'Badge'
export default Badge
