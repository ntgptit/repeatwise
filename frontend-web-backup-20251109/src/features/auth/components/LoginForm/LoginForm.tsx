import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Form, FormField } from '@/design-system/components/patterns/Form'
import { Input } from '@/design-system/components/primitives/Input'
import { Button } from '@/design-system/components/primitives/Button'
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

    if (!formData.identifier) {
      errors.identifier = 'Vui lòng nhập tên đăng nhập hoặc email'
    }

    if (!formData.password) {
      errors.password = 'Vui lòng nhập mật khẩu'
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
      navigate(APP_ROUTES.DASHBOARD)
    } catch (err) {
      // store đã xử lý lỗi
    }
  }

  return (
    <Form onSubmit={handleSubmit} spacing="lg" className="w-full">
      <FormField
        label="Tên đăng nhập hoặc Email"
        htmlFor="identifier"
        required
        error={validationErrors.identifier}
      >
        <Input
          id="identifier"
          name="identifier"
          type="text"
          required
          autoComplete="username"
          placeholder="Nhập tên đăng nhập hoặc email"
          value={formData.identifier}
          onChange={handleChange}
          fullWidth
        />
      </FormField>

      <FormField label="Mật khẩu" htmlFor="password" required error={validationErrors.password}>
        <Input
          id="password"
          name="password"
          type={showPassword ? 'text' : 'password'}
          required
          autoComplete="current-password"
          placeholder="Nhập mật khẩu"
          value={formData.password}
          onChange={handleChange}
          fullWidth
          rightIcon={
            <button
              type="button"
              onClick={() => setShowPassword((prev) => !prev)}
              className="text-xs font-semibold text-primary hover:text-primary/80"
            >
              {showPassword ? 'Ẩn' : 'Hiện'}
            </button>
          }
        />
      </FormField>

      {error ? (
        <div className="rounded-xl border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive shadow-sm">
          {error}
        </div>
      ) : null}

      <div className="space-y-4">
        <Button type="submit" fullWidth loading={isLoading}>
          {isLoading ? 'Đang đăng nhập...' : 'Đăng nhập'}
        </Button>

        <div className="space-y-2 text-center text-sm text-muted-foreground">
          <Link
            to={APP_ROUTES.FORGOT_PASSWORD}
            className="font-medium text-primary transition hover:text-primary/80"
            onClick={(e) => {
              e.preventDefault()
              alert('Tính năng khôi phục mật khẩu sẽ sớm ra mắt!')
            }}
          >
            Quên mật khẩu?
          </Link>

          <p>
            Chưa có tài khoản?{' '}
            <Link to={APP_ROUTES.REGISTER} className="font-semibold text-primary hover:text-primary/80">
              Đăng ký tại đây
            </Link>
          </p>
        </div>
      </div>
    </Form>
  )
}
