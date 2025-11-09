import { type JSX } from 'react'
import clsx from 'clsx'
import type { ModalBodyProps } from './Modal.types'

export const ModalBody = ({ children, className, ...props }: ModalBodyProps): JSX.Element => {
  return (
    <div className={clsx('text-sm text-foreground', className)} {...props}>
      {children}
    </div>
  )
}

export default ModalBody
