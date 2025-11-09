/**
 * UC-006: Change Password
 */

import {
  Card,
  CardHeader,
  CardContent,
  TextField,
  Button,
  Stack,
  Alert,
  CircularProgress,
  FormControl,
  InputAdornment,
  IconButton,
  Divider,
} from '@mui/material'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Visibility, VisibilityOff } from '@mui/icons-material'
import { useAuthStore } from '@/store/slices/auth.slice'

function PasswordSettings() {
  const navigate = useNavigate()
  const { changePassword, isLoading, error, clearError } = useAuthStore()

  const [formData, setFormData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: '',
  })
  const [showCurrentPassword, setShowCurrentPassword] = useState(false)
  const [showNewPassword, setShowNewPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [validationErrors, setValidationErrors] = useState({
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: '',
  })
  const [successMessage, setSuccessMessage] = useState('')

  const validateForm = (): boolean => {
    const errors = {
      currentPassword: '',
      newPassword: '',
      confirmNewPassword: '',
    }

    if (!formData.currentPassword) {
      errors.currentPassword = 'Current password is required'
    }

    if (!formData.newPassword) {
      errors.newPassword = 'New password is required'
    } else if (formData.newPassword.length < 8) {
      errors.newPassword = 'New password must be at least 8 characters'
    }

    if (!formData.confirmNewPassword) {
      errors.confirmNewPassword = 'Please confirm your new password'
    } else if (formData.newPassword !== formData.confirmNewPassword) {
      errors.confirmNewPassword = 'Passwords do not match'
    }

    setValidationErrors(errors)
    return Object.values(errors).every((err) => !err)
  }

  const handleChangePassword = async () => {
    clearError()
    setSuccessMessage('')

    if (!validateForm()) {
      return
    }

    try {
      await changePassword(formData)
      setSuccessMessage('Password changed successfully! Redirecting to login...')
      // Clear form
      setFormData({
        currentPassword: '',
        newPassword: '',
        confirmNewPassword: '',
      })
      // Redirect to login after 2 seconds
      setTimeout(() => {
        navigate('/login')
      }, 2000)
    } catch (err) {
      console.error('Password change failed:', err)
    }
  }

  const handleChange = (field: string) => (event: React.ChangeEvent<HTMLInputElement>) => {
    setFormData((prev) => ({
      ...prev,
      [field]: event.target.value,
    }))
    setValidationErrors((prev) => ({
      ...prev,
      [field]: '',
    }))
  }

  return (
    <Card>
      <CardHeader title="Change Password" />
      <Divider />
      <CardContent>
        <Stack spacing={3}>
          <Alert severity="info">
            You will be logged out from all devices after changing your password.
          </Alert>

          {error && (
            <Alert severity="error" onClose={clearError}>
              {error}
            </Alert>
          )}
          {successMessage && <Alert severity="success">{successMessage}</Alert>}

          <FormControl fullWidth>
            <TextField
              label="Current Password"
              type={showCurrentPassword ? 'text' : 'password'}
              value={formData.currentPassword}
              onChange={handleChange('currentPassword')}
              fullWidth
              error={!!validationErrors.currentPassword}
              helperText={validationErrors.currentPassword}
              disabled={isLoading}
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                      edge="end"
                    >
                      {showCurrentPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />
          </FormControl>

          <FormControl fullWidth>
            <TextField
              label="New Password"
              type={showNewPassword ? 'text' : 'password'}
              value={formData.newPassword}
              onChange={handleChange('newPassword')}
              fullWidth
              error={!!validationErrors.newPassword}
              helperText={validationErrors.newPassword || 'Minimum 8 characters'}
              disabled={isLoading}
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      onClick={() => setShowNewPassword(!showNewPassword)}
                      edge="end"
                    >
                      {showNewPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />
          </FormControl>

          <FormControl fullWidth>
            <TextField
              label="Confirm New Password"
              type={showConfirmPassword ? 'text' : 'password'}
              value={formData.confirmNewPassword}
              onChange={handleChange('confirmNewPassword')}
              fullWidth
              error={!!validationErrors.confirmNewPassword}
              helperText={validationErrors.confirmNewPassword}
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

          <Stack direction="row" spacing={2}>
            <Button
              variant="contained"
              color="primary"
              onClick={handleChangePassword}
              disabled={isLoading}
              startIcon={isLoading && <CircularProgress size={20} />}
            >
              {isLoading ? 'Changing Password...' : 'Change Password'}
            </Button>
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  )
}

export default PasswordSettings
