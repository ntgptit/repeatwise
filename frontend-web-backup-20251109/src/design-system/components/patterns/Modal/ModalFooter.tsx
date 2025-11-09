import { type JSX } from 'react'
import clsx from 'clsx'
import type { ModalFooterProps } from './Modal.types'

const alignmentMap: Record<NonNullable<ModalFooterProps['align']>, string> = {
  left: 'justify-start',
  center: 'justify-center',
  right: 'justify-end',
  between: 'justify-between',
}

export const ModalFooter = ({ align = 'right', className, children, ...props }: ModalFooterProps): JSX.Element => {
  return (
    <footer
      className={clsx('flex items-center gap-2 border-t border-border pt-4', alignmentMap[align], className)}
      {...props}
    >
      {children}
    </footer>
  )
}

export default ModalFooter
