/**
 * Login Page Component
 * 
 * Authentication page for user login
 * 
 * Features:
 * - Email and password input with validation
 * - Form validation using react-hook-form and zod
 * - Error handling and display
 * - Loading state during login
 * - Redirect after successful login
 * - Link to register page
 */

import * as React from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Eye, EyeOff, LogIn } from 'lucide-react'
import { toast } from 'sonner'
import { useAuth } from '@/hooks/domain/useAuth'
import { FormInput } from '@/components/common/Form'
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
const loginSchema = z.object({
  usernameOrEmail: z
    .string()
    .min(1, 'Username or email is required'),
  password: z
    .string()
    .min(1, 'Password is required'),
})

type LoginFormData = z.infer<typeof loginSchema>

export function LoginPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const { login, isLoggingIn, isAuthenticated } = useAuth()
  const [showPassword, setShowPassword] = React.useState(false)

  // Get redirect path from location state, default to dashboard
  const from = (location.state as { from?: { pathname?: string } })?.from?.pathname || ROUTES.DASHBOARD

  // Redirect if already authenticated
  React.useEffect(() => {
    if (isAuthenticated) {
      navigate(from, { replace: true })
    }
  }, [isAuthenticated, navigate, from])

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      usernameOrEmail: '',
      password: '',
    },
  })

  const onSubmit = React.useCallback(
    (data: LoginFormData) => {
      login(data, {
        onSuccess: () => {
          toast.success('Login successful! Welcome back.')
          navigate(from, { replace: true })
        },
        onError: (error) => {
          const errorMessage =
            error?.message || 'Invalid username/email or password'
          toast.error(errorMessage)
        },
      })
    },
    [login, navigate, from],
  )

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-background to-muted/20 p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1 text-center">
          <CardTitle className="text-2xl font-bold">Welcome back</CardTitle>
          <CardDescription>
            Enter your email and password to access your account
          </CardDescription>
        </CardHeader>
        <form onSubmit={handleSubmit(onSubmit)}>
          <CardContent className="space-y-4">
            <FormInput
              label="Username or Email"
              type="text"
              placeholder="username or name@example.com"
              autoComplete="username"
              required
              {...(errors.usernameOrEmail?.message && {
                error: errors.usernameOrEmail.message,
              })}
              {...register('usernameOrEmail')}
            />

            <div className="space-y-2">
              <FormInput
                label="Password"
                type={showPassword ? 'text' : 'password'}
                placeholder="Enter your password"
                autoComplete="current-password"
                required
                {...(errors.password?.message && { error: errors.password.message })}
                {...register('password')}
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
                <Link
                  to={ROUTES.REGISTER}
                  className="text-sm text-primary hover:underline"
                >
                  Forgot password?
                </Link>
              </div>
            </div>
          </CardContent>
          <CardFooter className="flex flex-col space-y-4">
            <Button
              type="submit"
              className="w-full"
              isLoading={isLoggingIn}
              loadingText="Signing in..."
              disabled={isLoggingIn}
            >
              <LogIn className="mr-2 h-4 w-4" />
              Sign in
            </Button>
            <div className="text-center text-sm text-muted-foreground">
              Don't have an account?{' '}
              <Link
                to={ROUTES.REGISTER}
                className="text-primary hover:underline font-medium"
              >
                Sign up
              </Link>
            </div>
          </CardFooter>
        </form>
      </Card>
    </div>
  )
}

