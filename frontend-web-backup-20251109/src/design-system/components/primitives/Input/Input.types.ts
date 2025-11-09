/**
 * Input Types
 */

/**
 * Input Types
 */
import type { InputHTMLAttributes } from 'react'

export type InputSize = 'sm' | 'md' | 'lg'

type NativeInputProps = Omit<InputHTMLAttributes<HTMLInputElement>, 'size'>

export interface InputProps extends NativeInputProps {
  size?: InputSize
  error?: string
  label?: string
  helperText?: string
  leftIcon?: React.ReactNode
  rightIcon?: React.ReactNode
  fullWidth?: boolean
}
