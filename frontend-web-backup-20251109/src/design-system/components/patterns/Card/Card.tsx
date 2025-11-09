import { type JSX } from 'react'
import clsx from 'clsx'
import type { CardProps } from './Card.types'

export const Card = ({ elevated, borderless, className, children, ...props }: CardProps): JSX.Element => {
  return (
    <div
      className={clsx(
        'rounded-2xl border border-border bg-background p-6 shadow-sm transition-shadow',
        elevated && 'shadow-lg',
        borderless && 'border-transparent',
        className
      )}
      {...props}
    >
      {children}
    </div>
  )
}

export default Card
