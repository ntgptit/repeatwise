import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuthStore } from '@/store/slices/auth.slice'
import { APP_ROUTES } from '@/config/app.config'
import type { LoginRequest } from '@/api/clients/auth.client'

export default function LoginForm() {
  const navigate = useNavigate()
  const { login, isLoading, error, clearError } = useAuthStore()

  const [formData, setFormData] = useState<LoginRequest>({
    identifier: '',
    password: '',
  })

  const [validationErrors, setValidationErrors] = useState<Record<string, string>>({})
  const [showPassword, setShowPassword] = useState(false)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))

    // Clear error when user starts typing
    if (validationErrors[name]) {
      setValidationErrors((prev) => {
        const newErrors = { ...prev }
        delete newErrors[name]
        return newErrors
      })
    }
    clearError()
  }

  const validate = (): boolean => {
    const errors: Record<string, string> = {}

    // Identifier (username or email) validation
    if (!formData.identifier) {
      errors.identifier = 'Username or email is required'
    }

    // Password validation
    if (!formData.password) {
      errors.password = 'Password is required'
    }

    setValidationErrors(errors)
    return Object.keys(errors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!validate()) {
      return
    }

    try {
      await login(formData)
      // Success - redirect to dashboard
      navigate(APP_ROUTES.DASHBOARD)
    } catch (err) {
      // Error is handled by store
      console.error('Login failed:', err)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Username or Email */}
      <div>
        <label htmlFor="identifier" className="block text-sm font-medium mb-2">
          Username or Email
        </label>
        <input
          id="identifier"
          name="identifier"
          type="text"
          required
          autoComplete="username"
          value={formData.identifier}
          onChange={handleChange}
          className={`w-full px-3 py-2 border rounded-md ${
            validationErrors.identifier ? 'border-red-500' : 'border-gray-300'
          }`}
          placeholder="Enter username or email"
        />
        {validationErrors.identifier && (
          <p className="mt-1 text-sm text-red-500">{validationErrors.identifier}</p>
        )}
      </div>

      {/* Password */}
      <div>
        <label htmlFor="password" className="block text-sm font-medium mb-2">
          Password
        </label>
        <div className="relative">
          <input
            id="password"
            name="password"
            type={showPassword ? 'text' : 'password'}
            required
            autoComplete="current-password"
            value={formData.password}
            onChange={handleChange}
            className={`w-full px-3 py-2 border rounded-md ${
              validationErrors.password ? 'border-red-500' : 'border-gray-300'
            }`}
            placeholder="Enter password"
          />
          <button
            type="button"
            onClick={() => setShowPassword(!showPassword)}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-gray-600"
          >
            {showPassword ? 'Hide' : 'Show'}
          </button>
        </div>
        {validationErrors.password && (
          <p className="mt-1 text-sm text-red-500">{validationErrors.password}</p>
        )}
      </div>

      {/* Server Error */}
      {error && (
        <div className="p-3 bg-red-50 border border-red-200 rounded-md">
          <p className="text-sm text-red-600">{error}</p>
        </div>
      )}

      {/* Submit Button */}
      <button
        type="submit"
        disabled={isLoading}
        className="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {isLoading ? 'Logging in...' : 'Login'}
      </button>

      {/* Links */}
      <div className="space-y-2 text-center text-sm">
        <div>
          <Link
            to={APP_ROUTES.FORGOT_PASSWORD}
            className="text-blue-600 hover:underline"
            onClick={(e) => {
              e.preventDefault()
              alert('Forgot password feature is coming soon!')
            }}
          >
            Forgot password?
          </Link>
        </div>
        <p className="text-gray-600">
          Don't have an account?{' '}
          <Link to={APP_ROUTES.REGISTER} className="text-blue-600 hover:underline">
            Sign up
          </Link>
        </p>
      </div>
    </form>
  )
}
