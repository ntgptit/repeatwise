import { type JSX } from 'react'
import clsx from 'clsx'
import type { FormProps, FormSpacing } from './Form.types'

const spacingMap: Record<FormSpacing, string> = {
  sm: 'space-y-3',
  md: 'space-y-4',
  lg: 'space-y-6',
}

export const Form = ({ spacing = 'md', fullWidth, className, children, ...props }: FormProps): JSX.Element => {
  return (
    <form
      className={clsx(spacingMap[spacing], fullWidth && 'w-full', className)}
      {...props}
    >
      {children}
    </form>
  )
}

export default Form
