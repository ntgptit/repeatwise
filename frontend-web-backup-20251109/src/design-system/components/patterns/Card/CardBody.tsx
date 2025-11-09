import { type JSX } from 'react'
import clsx from 'clsx'
import type { CardBodyProps } from './Card.types'

export const CardBody = ({ className, children, ...props }: CardBodyProps): JSX.Element => {
  return (
    <div className={clsx('flex flex-col gap-3 text-sm text-foreground', className)} {...props}>
      {children}
    </div>
  )
}

export default CardBody
