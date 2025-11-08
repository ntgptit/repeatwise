import { forwardRef } from 'react';
import { SelectProps } from './Select.types';

export const Select = forwardRef<HTMLSelectElement, SelectProps>(
  ({ options, label, error, helperText, fullWidth = false, className = '', ...props }, ref) => {
    const selectClasses = [
      'h-10 px-3.5 text-sm rounded-lg border transition-colors focus:outline-none focus:ring-2',
      error ? 'border-destructive focus:ring-destructive' : 'border-input focus:ring-ring',
      fullWidth && 'w-full',
      'disabled:opacity-50 disabled:cursor-not-allowed',
      className,
    ].filter(Boolean).join(' ');

    return (
      <div className={fullWidth ? 'w-full' : ''}>
        {label && <label className="block text-sm font-medium mb-1.5">{label}</label>}
        <select ref={ref} className={selectClasses} {...props}>
          {options.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
        {error && <p className="mt-1 text-sm text-destructive">{error}</p>}
        {helperText && !error && <p className="mt-1 text-sm text-muted-foreground">{helperText}</p>}
      </div>
    );
  }
);

Select.displayName = 'Select';
export default Select;
