import { type JSX } from 'react'
import clsx from 'clsx'
import type { CardHeaderProps } from './Card.types'

export const CardHeader = ({ title, description, actions, className, children, ...props }: CardHeaderProps): JSX.Element => {
  return (
    <header className={clsx('mb-4 flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between', className)} {...props}>
      <div className="space-y-1">
        {title ? <h3 className="text-lg font-semibold text-foreground">{title}</h3> : null}
        {description ? <p className="text-sm text-muted-foreground">{description}</p> : null}
        {children}
      </div>
      {actions ? <div className="flex items-center gap-2">{actions}</div> : null}
    </header>
  )
}

export default CardHeader
