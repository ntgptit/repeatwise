import { type JSX } from 'react'
import clsx from 'clsx'
import type { FormErrorProps } from './Form.types'

export const FormError = ({ message, className, children, ...props }: FormErrorProps): JSX.Element | null => {
  const content = message ?? children

  if (!content) {
    return null
  }

  return (
    <p className={clsx('text-xs text-destructive', className)} role="alert" {...props}>
      {content}
    </p>
  )
}

export default FormError
