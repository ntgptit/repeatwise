/**
 * Register Page Component
 * 
 * Registration page for new users
 * 
 * Features:
 * - Email, password, and name input with validation
 * - Form validation using react-hook-form and zod
 * - Error handling and display
 * - Loading state during registration
 * - Redirect to login after successful registration
 * - Link to login page
 */

import * as React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Eye, EyeOff, UserPlus } from 'lucide-react'
import { toast } from 'sonner'
import { useAuth } from '@/hooks/domain/useAuth'
import { FormInput } from '@/components/common/Form'
import { PasswordStrength } from '@/components/common/Form'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { ROUTES } from '@/constants/routes'

// Validation schema
const registerSchema = z
  .object({
    email: z
      .string()
      .min(1, 'Email is required')
      .email('Please enter a valid email address'),
    username: z
      .string()
      .optional()
      .refine(
        (val) => !val || (val.length >= 3 && val.length <= 30),
        'Username must be 3-30 characters',
      )
      .refine(
        (val) =>
          !val ||
          /^[a-z0-9_-]+$/.test(val),
        'Username must contain only lowercase letters, numbers, underscores, or hyphens',
      ),
    password: z
      .string()
      .min(1, 'Password is required')
      .min(8, 'Password must be at least 8 characters'),
    confirmPassword: z
      .string()
      .min(1, 'Please confirm your password'),
    name: z
      .string()
      .optional()
      .refine(
        (val) => !val || val.length <= 100,
        'Name must be 100 characters or less',
      ),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Passwords do not match',
    path: ['confirmPassword'],
  })

type RegisterFormData = z.infer<typeof registerSchema>

export function RegisterPage() {
  const navigate = useNavigate()
  const { register: registerUser, isRegistering, isAuthenticated } = useAuth()
  const [showPassword, setShowPassword] = React.useState(false)

  // Redirect if already authenticated
  React.useEffect(() => {
    if (isAuthenticated) {
      navigate(ROUTES.DASHBOARD, { replace: true })
    }
  }, [isAuthenticated, navigate])

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      email: '',
      username: '',
      password: '',
      confirmPassword: '',
      name: '',
    },
  })

  const passwordValue = watch('password')

  const onSubmit = React.useCallback(
    (data: RegisterFormData) => {
      // Prepare request body matching API spec
      const requestData = {
        email: data.email.trim().toLowerCase(),
        password: data.password,
        confirmPassword: data.confirmPassword,
        ...(data.username && { username: data.username.trim() }),
        ...(data.name && { name: data.name.trim() }),
      }

      registerUser(requestData, {
        onSuccess: () => {
          toast.success('Registration successful. Please login.')
          navigate(ROUTES.LOGIN)
        },
        onError: (error) => {
          const errorMessage =
            error?.message || 'Registration failed. Please try again.'
          toast.error(errorMessage)
        },
      })
    },
    [registerUser, navigate],
  )

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-background to-muted/20 p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1 text-center">
          <CardTitle className="text-2xl font-bold">Create an account</CardTitle>
          <CardDescription>
            Enter your information to create your account
          </CardDescription>
        </CardHeader>
        <form onSubmit={handleSubmit(onSubmit)}>
          <CardContent className="space-y-4">
            <FormInput
              label="Email"
              type="email"
              placeholder="name@example.com"
              autoComplete="email"
              required
              {...(errors.email?.message && { error: errors.email.message })}
              {...register('email')}
            />

            <FormInput
              label="Username (optional)"
              type="text"
              placeholder="john_doe123"
              autoComplete="username"
              {...(errors.username?.message && { error: errors.username.message })}
              {...register('username')}
            />
            {watch('username') && (
              <p className="text-xs text-muted-foreground">
                3-30 characters, lowercase letters, numbers, underscores, or hyphens
              </p>
            )}

            <div className="space-y-2">
              <FormInput
                label="Password"
                type={showPassword ? 'text' : 'password'}
                placeholder="Enter your password"
                autoComplete="new-password"
                required
                {...(errors.password?.message && { error: errors.password.message })}
                {...register('password')}
              />
              {passwordValue && <PasswordStrength password={passwordValue} />}
              <p className="text-xs text-muted-foreground">
                Minimum 8 characters
              </p>
            </div>

            <div className="space-y-2">
              <FormInput
                label="Confirm Password"
                type={showPassword ? 'text' : 'password'}
                placeholder="Confirm your password"
                autoComplete="new-password"
                required
                {...(errors.confirmPassword?.message && {
                  error: errors.confirmPassword.message,
                })}
                {...register('confirmPassword')}
              />
              <div className="flex items-center justify-between">
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="text-sm text-muted-foreground hover:text-foreground transition-colors"
                  aria-label={showPassword ? 'Hide password' : 'Show password'}
                >
                  {showPassword ? (
                    <EyeOff className="h-4 w-4 inline mr-1" />
                  ) : (
                    <Eye className="h-4 w-4 inline mr-1" />
                  )}
                  {showPassword ? 'Hide' : 'Show'} password
                </button>
              </div>
            </div>

            <FormInput
              label="Name (optional)"
              type="text"
              placeholder="John Doe"
              autoComplete="name"
              {...(errors.name?.message && { error: errors.name.message })}
              {...register('name')}
            />
          </CardContent>
          <CardFooter className="flex flex-col space-y-4">
            <Button
              type="submit"
              className="w-full"
              isLoading={isRegistering}
              loadingText="Creating account..."
              disabled={isRegistering}
            >
              <UserPlus className="mr-2 h-4 w-4" />
              Create account
            </Button>
            <div className="text-center text-sm text-muted-foreground">
              Already have an account?{' '}
              <Link
                to={ROUTES.LOGIN}
                className="text-primary hover:underline font-medium"
              >
                Sign in
              </Link>
            </div>
          </CardFooter>
        </form>
      </Card>
    </div>
  )
}

// Default export for compatibility
export default RegisterPage

