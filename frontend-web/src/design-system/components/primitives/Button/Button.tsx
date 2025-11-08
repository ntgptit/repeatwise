/**
 * Button Component
 */

import { forwardRef } from 'react';
import { ButtonProps } from './Button.types';

const baseStyles = 'inline-flex items-center justify-center font-semibold rounded-lg transition-all focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed';

const variants = {
  primary: 'bg-primary text-primary-foreground hover:bg-primary/90 focus:ring-primary',
  secondary: 'bg-secondary text-secondary-foreground hover:bg-secondary/90 focus:ring-secondary',
  outline: 'border-2 border-primary text-primary hover:bg-primary hover:text-primary-foreground focus:ring-primary',
  ghost: 'text-primary hover:bg-primary/10 focus:ring-primary',
  danger: 'bg-destructive text-destructive-foreground hover:bg-destructive/90 focus:ring-destructive',
};

const sizes = {
  sm: 'px-3 py-2 text-sm h-8',
  md: 'px-4 py-2.5 text-sm h-10',
  lg: 'px-6 py-3 text-base h-12',
};

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      variant = 'primary',
      size = 'md',
      fullWidth = false,
      loading = false,
      leftIcon,
      rightIcon,
      children,
      className = '',
      disabled,
      ...props
    },
    ref
  ) => {
    const classes = [
      baseStyles,
      variants[variant],
      sizes[size],
      fullWidth && 'w-full',
      className,
    ]
      .filter(Boolean)
      .join(' ');

    return (
      <button
        ref={ref}
        className={classes}
        disabled={disabled || loading}
        {...props}
      >
        {loading && <span className="mr-2 animate-spin">ó</span>}
        {leftIcon && <span className="mr-2">{leftIcon}</span>}
        {children}
        {rightIcon && <span className="ml-2">{rightIcon}</span>}
      </button>
    );
  }
);

Button.displayName = 'Button';

export default Button;
