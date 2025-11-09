import type { DetailedHTMLProps, FormHTMLAttributes, HTMLAttributes, ReactNode } from 'react'

export type FormSpacing = 'sm' | 'md' | 'lg'

export interface FormProps extends DetailedHTMLProps<FormHTMLAttributes<HTMLFormElement>, HTMLFormElement> {
  spacing?: FormSpacing
  fullWidth?: boolean
}

export interface FormFieldProps extends HTMLAttributes<HTMLDivElement> {
  label?: ReactNode
  htmlFor?: string
  helperText?: ReactNode
  error?: ReactNode
  required?: boolean
  inline?: boolean
  spacing?: FormSpacing
  children: ReactNode
}

export interface FormErrorProps extends HTMLAttributes<HTMLParagraphElement> {
  message?: ReactNode
}
