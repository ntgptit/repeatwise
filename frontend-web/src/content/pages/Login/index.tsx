/**
 * UC-002: User Login Page
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
`
)

function Login() {
  const navigate = useNavigate()
  const { login, isLoading, error, clearError } = useAuthStore()

  const [formData, setFormData] = useState({
    usernameOrEmail: '',
    password: '',
  })
  const [showPassword, setShowPassword] = useState(false)
  const [validationErrors, setValidationErrors] = useState({
    usernameOrEmail: '',
    password: '',
  })

  const validateForm = (): boolean => {
    const errors = {
      usernameOrEmail: '',
      password: '',
    }

    if (!formData.usernameOrEmail.trim()) {
      errors.usernameOrEmail = 'Username or email is required'
    }

    if (!formData.password) {
      errors.password = 'Password is required'
    }

    setValidationErrors(errors)
    return !errors.usernameOrEmail && !errors.password
  }

  const handleLogin = async () => {
    clearError()

    if (!validateForm()) {
      return
    }

    try {
      await login(formData)
      // Navigate to dashboard on successful login
      navigate('/dashboards/crypto')
    } catch (err) {
      // Error is handled by auth store
      console.error('Login failed:', err)
    }
  }

  const handleKeyPress = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter') {
      handleLogin()
    }
  }

  const handleChange = (field: string) => (event: React.ChangeEvent<HTMLInputElement>) => {
    setFormData((prev) => ({
      ...prev,
      [field]: event.target.value,
    }))
    // Clear validation error when user types
    setValidationErrors((prev) => ({
      ...prev,
      [field]: '',
    }))
  }

  return (
    <>
      <PageHelmet title="Login - RepeatWise" />
      <MainContent>
        <Container maxWidth="sm">
          <Box textAlign="center" mb={3}>
            <Logo />
            <Typography variant="h2" sx={{ my: 2 }}>
              Welcome to RepeatWise
            </Typography>
            <Typography variant="h4" color="text.secondary" fontWeight="normal">
              Sign in to your account
            </Typography>
          </Box>
          <Card sx={{ p: 4 }}>
            <Stack spacing={3}>
              {error && (
                <Alert severity="error" onClose={clearError}>
                  {error}
                </Alert>
              )}
              <FormControl fullWidth>
                <TextField
                  label="Username or Email"
                  type="text"
                  value={formData.usernameOrEmail}
                  onChange={handleChange('usernameOrEmail')}
                  onKeyPress={handleKeyPress}
                  fullWidth
                  variant="outlined"
                  error={!!validationErrors.usernameOrEmail}
                  helperText={validationErrors.usernameOrEmail}
                  disabled={isLoading}
                  autoFocus
                />
              </FormControl>
              <FormControl fullWidth>
                <TextField
                  label="Password"
                  type={showPassword ? 'text' : 'password'}
                  value={formData.password}
                  onChange={handleChange('password')}
                  onKeyPress={handleKeyPress}
                  fullWidth
                  variant="outlined"
                  error={!!validationErrors.password}
                  helperText={validationErrors.password}
                  disabled={isLoading}
                  InputProps={{
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton
                          onClick={() => setShowPassword(!showPassword)}
                          edge="end"
                        >
                          {showPassword ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                />
              </FormControl>
              <Button
                variant="contained"
                size="large"
                fullWidth
                onClick={handleLogin}
                disabled={isLoading}
                startIcon={isLoading && <CircularProgress size={20} />}
              >
                {isLoading ? 'Signing In...' : 'Sign In'}
              </Button>
              <Box textAlign="center">
                <Typography variant="body2" color="text.secondary">
                  Don&apos;t have an account?{' '}
                  <Button
                    variant="text"
                    size="small"
                    onClick={() => navigate('/register')}
                    disabled={isLoading}
                  >
                    Sign Up
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

export default Login
