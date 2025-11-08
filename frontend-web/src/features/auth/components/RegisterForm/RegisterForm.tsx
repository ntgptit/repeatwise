import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuthStore } from '@/store/slices/auth.slice'
import { APP_ROUTES } from '@/config/app.config'
import type { RegisterRequest } from '@/api/clients/auth.client'

export default function RegisterForm() {
  const navigate = useNavigate()
  const { register, isLoading, error, clearError } = useAuthStore()

  const [formData, setFormData] = useState<RegisterRequest>({
    email: '',
    username: '',
    password: '',
    confirmPassword: '',
    name: '',
  })

  const [validationErrors, setValidationErrors] = useState<Record<string, string>>({})
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)

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

    // Email validation
    if (!formData.email) {
      errors.email = 'Email is required'
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      errors.email = 'Invalid email format'
    }

    // Username validation (optional)
    if (formData.username && !/^[a-z0-9_]{3,30}$/.test(formData.username)) {
      errors.username = 'Username must be 3-30 characters, lowercase alphanumeric + underscore only'
    }

    // Password validation
    if (!formData.password) {
      errors.password = 'Password is required'
    } else if (formData.password.length < 8) {
      errors.password = 'Password must be at least 8 characters'
    }

    // Confirm password validation
    if (!formData.confirmPassword) {
      errors.confirmPassword = 'Please confirm your password'
    } else if (formData.password !== formData.confirmPassword) {
      errors.confirmPassword = 'Passwords do not match'
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
      await register(formData)
      // Success - redirect to login
      navigate(APP_ROUTES.LOGIN)
      // TODO: Show success toast
    } catch (err) {
      // Error is handled by store
      console.error('Registration failed:', err)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Email */}
      <div>
        <label htmlFor="email" className="block text-sm font-medium mb-2">
          Email *
        </label>
        <input
          id="email"
          name="email"
          type="email"
          required
          value={formData.email}
          onChange={handleChange}
          className={`w-full px-3 py-2 border rounded-md ${
            validationErrors.email ? 'border-red-500' : 'border-gray-300'
          }`}
          placeholder="user@example.com"
        />
        {validationErrors.email && (
          <p className="mt-1 text-sm text-red-500">{validationErrors.email}</p>
        )}
      </div>

      {/* Username (optional) */}
      <div>
        <label htmlFor="username" className="block text-sm font-medium mb-2">
          Username <span className="text-gray-500">(optional)</span>
        </label>
        <input
          id="username"
          name="username"
          type="text"
          value={formData.username}
          onChange={handleChange}
          className={`w-full px-3 py-2 border rounded-md ${
            validationErrors.username ? 'border-red-500' : 'border-gray-300'
          }`}
          placeholder="john_doe123"
        />
        {validationErrors.username && (
          <p className="mt-1 text-sm text-red-500">{validationErrors.username}</p>
        )}
      </div>

      {/* Name (optional) */}
      <div>
        <label htmlFor="name" className="block text-sm font-medium mb-2">
          Name <span className="text-gray-500">(optional)</span>
        </label>
        <input
          id="name"
          name="name"
          type="text"
          value={formData.name}
          onChange={handleChange}
          className="w-full px-3 py-2 border border-gray-300 rounded-md"
          placeholder="John Doe"
        />
      </div>

      {/* Password */}
      <div>
        <label htmlFor="password" className="block text-sm font-medium mb-2">
          Password *
        </label>
        <div className="relative">
          <input
            id="password"
            name="password"
            type={showPassword ? 'text' : 'password'}
            required
            value={formData.password}
            onChange={handleChange}
            className={`w-full px-3 py-2 border rounded-md ${
              validationErrors.password ? 'border-red-500' : 'border-gray-300'
            }`}
            placeholder="Min 8 characters"
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

      {/* Confirm Password */}
      <div>
        <label htmlFor="confirmPassword" className="block text-sm font-medium mb-2">
          Confirm Password *
        </label>
        <div className="relative">
          <input
            id="confirmPassword"
            name="confirmPassword"
            type={showConfirmPassword ? 'text' : 'password'}
            required
            value={formData.confirmPassword}
            onChange={handleChange}
            className={`w-full px-3 py-2 border rounded-md ${
              validationErrors.confirmPassword ? 'border-red-500' : 'border-gray-300'
            }`}
            placeholder="Re-enter password"
          />
          <button
            type="button"
            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-gray-600"
          >
            {showConfirmPassword ? 'Hide' : 'Show'}
          </button>
        </div>
        {validationErrors.confirmPassword && (
          <p className="mt-1 text-sm text-red-500">{validationErrors.confirmPassword}</p>
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
        {isLoading ? 'Creating account...' : 'Register'}
      </button>

      {/* Login Link */}
      <p className="text-center text-sm text-gray-600">
        Already have an account?{' '}
        <Link to={APP_ROUTES.LOGIN} className="text-blue-600 hover:underline">
          Login here
        </Link>
      </p>
    </form>
  )
}
