/**
 * UC-001: User Registration Page
 * Based on wireframes in 00_docs/02-system-analysis/07-wireframes-web.md
 */

import {
  Box,
  Card,
  Typography,
  Container,
  Button,
  TextField,
  FormControl,
  Stack,
  Alert,
  CircularProgress,
  InputAdornment,
  IconButton,
} from '@mui/material'
import { styled } from '@mui/material/styles'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Visibility, VisibilityOff } from '@mui/icons-material'
import PageHelmet from 'src/components/PageHelmet'
import Logo from 'src/components/Logo'
import { useAuthStore } from '@/store/slices/auth.slice'

const MainContent = styled(Box)(
  () => `
    height: 100%;
    display: flex;
    flex: 1;
    overflow: auto;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 20px 0;
`
)

function Register() {
  const navigate = useNavigate()
  const { register, isLoading, error, clearError } = useAuthStore()

  const [formData, setFormData] = useState({
    email: '',
    username: '',
    password: '',
    confirmPassword: '',
    name: '',
  })
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [validationErrors, setValidationErrors] = useState({
    email: '',
    username: '',
    password: '',
    confirmPassword: '',
    name: '',
  })
  const [successMessage, setSuccessMessage] = useState('')

  const validateEmail = (email: string): boolean => {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
    return emailRegex.test(email)
  }

  const validateUsername = (username: string): boolean => {
    if (!username) {
      return true
    } // Username is optional
    if (username.length < 3 || username.length > 30) {
      return false
    }
    const usernameRegex = /^[a-z0-9_]+$/
    return usernameRegex.test(username)
  }

  const validateForm = (): boolean => {
    const errors = {
      email: '',
      username: '',
      password: '',
      confirmPassword: '',
      name: '',
    }

    // Email validation
    if (!formData.email.trim()) {
      errors.email = 'Email is required'
    } else if (!validateEmail(formData.email)) {
      errors.email = 'Invalid email format'
    }

    // Username validation (optional but must be valid if provided)
    if (formData.username && !validateUsername(formData.username)) {
      errors.username =
        'Username must be 3-30 lowercase characters (letters, numbers, underscores only)'
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
    return Object.values(errors).every(err => !err)
  }

  const handleRegister = async () => {
    clearError()
    setSuccessMessage('')

    if (!validateForm()) {
      return
    }

    try {
      const sanitizedPayload = {
        ...formData,
        username: formData.username.trim() ? formData.username.trim().toLowerCase() : undefined,
        name: formData.name.trim() || undefined,
      }

      const result = await register(sanitizedPayload)

      // Defensive check: ensure result exists and has success property
      if (!result?.success) {
        // Error is already set in auth store and will be displayed
        // Don't navigate to login page when registration fails
        console.log('Registration failed, staying on register page')
        return
      }

      // Only show success message and redirect if registration succeeded
      setSuccessMessage(result.message || 'Registration successful! Redirecting to login...')

      // Clear form after successful registration
      setFormData({
        email: '',
        username: '',
        password: '',
        confirmPassword: '',
        name: '',
      })

      // Redirect to login after 2 seconds on successful registration
      // setTimeout(() => {
      //   navigate('/login')
      // }, 2000)
    } catch (err) {
      // Catch any unexpected errors (shouldn't happen as register() handles errors internally)
      // Don't navigate to login page when there's an error
      console.error('Unexpected error during registration:', err)
      // Error message should already be set in auth store
    }
  }

  const handleKeyPress = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter') {
      handleRegister()
    }
  }

  const handleChange = (field: string) => (event: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({
      ...prev,
      [field]: event.target.value,
    }))
    // Clear validation error when user types
    setValidationErrors(prev => ({
      ...prev,
      [field]: '',
    }))
  }

  return (
    <>
      <PageHelmet title="Register - RepeatWise" />
      <MainContent>
        <Container maxWidth="sm">
          <Box textAlign="center" mb={3}>
            <Logo />
            <Typography variant="h2" sx={{ my: 2 }}>
              Create Account
            </Typography>
            <Typography variant="h4" color="text.secondary" fontWeight="normal">
              Join RepeatWise today
            </Typography>
          </Box>
          <Card sx={{ p: 4 }}>
            <Stack spacing={3}>
              {error ? (
                <Alert severity="error" onClose={clearError}>
                  {error}
                </Alert>
              ) : null}
              {successMessage ? <Alert severity="success">{successMessage}</Alert> : null}
              <FormControl fullWidth>
                <TextField
                  label="Email *"
                  type="email"
                  value={formData.email}
                  onChange={handleChange('email')}
                  onKeyPress={handleKeyPress}
                  fullWidth
                  variant="outlined"
                  error={Boolean(validationErrors.email)}
                  helperText={validationErrors.email}
                  disabled={isLoading}
                  autoFocus
                />
              </FormControl>
              <FormControl fullWidth>
                <TextField
                  label="Username (optional)"
                  type="text"
                  value={formData.username}
                  onChange={handleChange('username')}
                  onKeyPress={handleKeyPress}
                  fullWidth
                  variant="outlined"
                  error={Boolean(validationErrors.username)}
                  helperText={
                    validationErrors.username ||
                    '3-30 lowercase characters (letters, numbers, underscores only)'
                  }
                  disabled={isLoading}
                />
              </FormControl>
              <FormControl fullWidth>
                <TextField
                  label="Password *"
                  type={showPassword ? 'text' : 'password'}
                  value={formData.password}
                  onChange={handleChange('password')}
                  onKeyPress={handleKeyPress}
                  fullWidth
                  variant="outlined"
                  error={Boolean(validationErrors.password)}
                  helperText={validationErrors.password || 'Minimum 8 characters'}
                  disabled={isLoading}
                  InputProps={{
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton onClick={() => setShowPassword(!showPassword)} edge="end">
                          {showPassword ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                />
              </FormControl>
              <FormControl fullWidth>
                <TextField
                  label="Confirm Password *"
                  type={showConfirmPassword ? 'text' : 'password'}
                  value={formData.confirmPassword}
                  onChange={handleChange('confirmPassword')}
                  onKeyPress={handleKeyPress}
                  fullWidth
                  variant="outlined"
                  error={Boolean(validationErrors.confirmPassword)}
                  helperText={validationErrors.confirmPassword}
                  disabled={isLoading}
                  InputProps={{
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton
                          onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                          edge="end"
                        >
                          {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                />
              </FormControl>
              <FormControl fullWidth>
                <TextField
                  label="Name (optional)"
                  type="text"
                  value={formData.name}
                  onChange={handleChange('name')}
                  onKeyPress={handleKeyPress}
                  fullWidth
                  variant="outlined"
                  error={Boolean(validationErrors.name)}
                  helperText={validationErrors.name}
                  disabled={isLoading}
                />
              </FormControl>
              <Button
                variant="contained"
                size="large"
                fullWidth
                onClick={handleRegister}
                disabled={isLoading}
                startIcon={isLoading ? <CircularProgress size={20} /> : null}
              >
                {isLoading ? 'Creating Account...' : 'Register'}
              </Button>
              <Box textAlign="center">
                <Typography variant="body2" color="text.secondary">
                  Already have an account?{' '}
                  <Button
                    variant="text"
                    size="small"
                    onClick={() => navigate('/login')}
                    disabled={isLoading}
                  >
                    Login
                  </Button>
                </Typography>
              </Box>
            </Stack>
          </Card>
        </Container>
      </MainContent>
    </>
  )
}

export default Register
