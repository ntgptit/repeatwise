/**
 * Form Components
 * 
 * Reusable form field components with validation and labels
 */

import * as React from 'react'
import { cn } from '@/lib/utils'
import { Label } from '@/components/ui/label'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import type { InputHTMLAttributes, TextareaHTMLAttributes } from 'react'

interface FormFieldProps extends React.HTMLAttributes<HTMLDivElement> {
  label?: string
  error?: string
  required?: boolean
  description?: string
}

export function FormField({
  label,
  error,
  required,
  description,
  children,
  className,
  ...props
}: FormFieldProps) {
  return (
    <div className={cn('space-y-2', className)} {...props}>
      {label && (
        <Label>
          {label}
          {required && <span className="text-destructive ml-1">*</span>}
        </Label>
      )}
      {description && (
        <p className="text-sm text-muted-foreground">{description}</p>
      )}
      {children}
      {error && (
        <p className="text-sm text-destructive" role="alert">
          {error}
        </p>
      )}
    </div>
  )
}

interface FormInputProps
  extends InputHTMLAttributes<HTMLInputElement>,
    Omit<FormFieldProps, 'children'> {}

export const FormInput = React.forwardRef<HTMLInputElement, FormInputProps>(
  ({ label, error, required, description, className, ...props }, ref) => {
    return (
      <FormField label={label} error={error} required={required} description={description}>
        <Input
          ref={ref}
          className={cn(error && 'border-destructive', className)}
          {...props}
        />
      </FormField>
    )
  },
)
FormInput.displayName = 'FormInput'

interface FormTextareaProps
  extends TextareaHTMLAttributes<HTMLTextAreaElement>,
    Omit<FormFieldProps, 'children'> {}

export const FormTextarea = React.forwardRef<
  HTMLTextAreaElement,
  FormTextareaProps
>(({ label, error, required, description, className, ...props }, ref) => {
  return (
    <FormField label={label} error={error} required={required} description={description}>
      <Textarea
        ref={ref}
        className={cn(error && 'border-destructive', className)}
        {...props}
      />
    </FormField>
  )
})
FormTextarea.displayName = 'FormTextarea'

interface CharacterCounterProps {
  current: number
  max: number
  className?: string
}

export function CharacterCounter({
  current,
  max,
  className,
}: CharacterCounterProps) {
  const isWarning = current > max * 0.8
  const isError = current > max

  return (
    <p
      className={cn(
        'text-xs text-right',
        isError && 'text-destructive',
        isWarning && !isError && 'text-orange-500',
        className,
      )}
    >
      {current}/{max} characters
    </p>
  )
}

interface PasswordStrengthProps {
  password: string
  className?: string
}

export function PasswordStrength({
  password,
  className,
}: PasswordStrengthProps) {
  const getStrength = (pwd: string): {
    score: number
    label: string
    color: string
  } => {
    if (!pwd) return { score: 0, label: '', color: '' }

    let score = 0
    if (pwd.length >= 8) score++
    if (pwd.length >= 12) score++
    if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) score++
    if (/\d/.test(pwd)) score++
    if (/[^a-zA-Z\d]/.test(pwd)) score++

    if (score <= 2)
      return { score, label: 'Weak', color: 'bg-destructive' }
    if (score <= 3)
      return { score, label: 'Fair', color: 'bg-orange-500' }
    if (score <= 4)
      return { score, label: 'Good', color: 'bg-blue-500' }
    return { score, label: 'Strong', color: 'bg-green-500' }
  }

  const { score, label, color } = getStrength(password)

  if (!password) return null

  return (
    <div className={cn('space-y-1', className)}>
      <div className="flex items-center justify-between text-xs">
        <span className="text-muted-foreground">Password strength:</span>
        <span className="font-medium">{label}</span>
      </div>
      <div className="h-1.5 w-full bg-muted rounded-full overflow-hidden">
        <div
          className={cn('h-full transition-all duration-300', color)}
          style={{ width: `${(score / 5) * 100}%` }}
        />
      </div>
    </div>
  )
}
