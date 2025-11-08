/**
 * Input Component
 */

import { forwardRef } from 'react';
import { InputProps } from './Input.types';

const sizes = {
  sm: 'h-8 px-3 text-sm',
  md: 'h-10 px-3.5 text-sm',
  lg: 'h-12 px-4 text-base',
};

export const Input = forwardRef<HTMLInputElement, InputProps>(
  (
    {
      size = 'md',
      error,
      label,
      helperText,
      leftIcon,
      rightIcon,
      fullWidth = false,
      className = '',
      ...props
    },
    ref
  ) => {
    const inputClasses = [
      'rounded-lg border transition-colors focus:outline-none focus:ring-2 focus:ring-offset-1',
      error
        ? 'border-destructive focus:ring-destructive'
        : 'border-input focus:ring-ring',
      sizes[size],
      leftIcon && 'pl-10',
      rightIcon && 'pr-10',
      fullWidth && 'w-full',
      'disabled:opacity-50 disabled:cursor-not-allowed',
      className,
    ]
      .filter(Boolean)
      .join(' ');

    return (
      <div className={fullWidth ? 'w-full' : ''}>
        {label && (
          <label className="block text-sm font-medium mb-1.5">{label}</label>
        )}
        <div className="relative">
          {leftIcon && (
            <div className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground">
              {leftIcon}
            </div>
          )}
          <input ref={ref} className={inputClasses} {...props} />
          {rightIcon && (
            <div className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground">
              {rightIcon}
            </div>
          )}
        </div>
        {error && <p className="mt-1 text-sm text-destructive">{error}</p>}
        {helperText && !error && (
          <p className="mt-1 text-sm text-muted-foreground">{helperText}</p>
        )}
      </div>
    );
  }
);

Input.displayName = 'Input';

export default Input;
