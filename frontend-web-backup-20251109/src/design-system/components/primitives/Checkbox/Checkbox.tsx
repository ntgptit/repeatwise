import { forwardRef } from 'react'
import type { CheckboxProps } from './Checkbox.types'

export const Checkbox = forwardRef<HTMLInputElement, CheckboxProps>(
  ({ label, error, className = '', ...props }, ref) => {
    return (
      <div>
        <label className="inline-flex items-center cursor-pointer">
          <input
            ref={ref}
            type="checkbox"
            className={`w-4 h-4 rounded border-input text-primary focus:ring-2 focus:ring-ring ${className}`}
            {...props}
          />
          {label ? <span className="ml-2 text-sm">{label}</span> : null}
        </label>
        {error ? <p className="mt-1 text-sm text-destructive">{error}</p> : null}
      </div>
    )
  }
)

Checkbox.displayName = 'Checkbox'
export default Checkbox
