import { forwardRef } from 'react';
import { RadioProps } from './Radio.types';

export const Radio = forwardRef<HTMLInputElement, RadioProps>(
  ({ label, className = '', ...props }, ref) => {
    return (
      <label className="inline-flex items-center cursor-pointer">
        <input
          ref={ref}
          type="radio"
          className={`w-4 h-4 border-input text-primary focus:ring-2 focus:ring-ring ${className}`}
          {...props}
        />
        {label && <span className="ml-2 text-sm">{label}</span>}
      </label>
    );
  }
);

Radio.displayName = 'Radio';
export default Radio;
