import { type JSX } from 'react'
import clsx from 'clsx'
import type { FormFieldProps, FormSpacing } from './Form.types'
import { FormError } from './FormError'

const spacingMap: Record<FormSpacing, string> = {
  sm: 'space-y-1.5',
  md: 'space-y-2',
  lg: 'space-y-3',
}

export const FormField = ({
  label,
  htmlFor,
  helperText,
  error,
  required,
  inline,
  spacing = 'md',
  children,
  className,
  ...props
}: FormFieldProps): JSX.Element => {
  const LabelComponent = htmlFor ? 'label' : 'span'

  return (
    <div
      className={clsx(spacingMap[spacing], inline && 'flex items-start gap-3', className)}
      {...props}
    >
      {label ? (
        <LabelComponent
          htmlFor={htmlFor}
          className={clsx('text-sm font-medium text-foreground', inline ? 'pt-2 min-w-[120px]' : '')}
        >
          {label}
          {required ? <span className="ml-1 text-destructive">*</span> : null}
        </LabelComponent>
      ) : null}
      <div className={clsx('flex flex-1 flex-col', inline && 'space-y-1')}>
        {children}
        {helperText && !error ? (
          <span className="text-xs text-muted-foreground">{helperText}</span>
        ) : null}
        {error ? <FormError message={error} /> : null}
      </div>
    </div>
  )
}

export default FormField
