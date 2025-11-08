import type { HTMLAttributes } from 'react'

export type BadgeVariant = 'default' | 'success' | 'warning' | 'error' | 'info'

export interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: BadgeVariant
}
