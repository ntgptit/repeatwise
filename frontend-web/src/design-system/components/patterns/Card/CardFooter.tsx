import { type JSX } from 'react'
import clsx from 'clsx'
import type { CardFooterProps } from './Card.types'

const alignmentMap: Record<NonNullable<CardFooterProps['align']>, string> = {
  left: 'justify-start',
  center: 'justify-center',
  right: 'justify-end',
  between: 'justify-between',
}

export const CardFooter = ({ align = 'right', className, children, ...props }: CardFooterProps): JSX.Element => {
  return (
    <footer
      className={clsx('mt-6 flex items-center gap-2 border-t border-border pt-4', alignmentMap[align], className)}
      {...props}
    >
      {children}
    </footer>
  )
}

export default CardFooter
