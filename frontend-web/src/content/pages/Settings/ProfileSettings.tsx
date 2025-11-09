/**
 * UC-005: Update User Profile
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
  InputLabel,
  Select,
  MenuItem,
  Radio,
  RadioGroup,
  FormControlLabel,
  FormLabel,
  Divider,
} from '@mui/material'
import { useState, useEffect } from 'react'
import { useAuthStore } from '@/store/slices/auth.slice'

function ProfileSettings() {
  const { user, updateProfile, isLoading, error, clearError } = useAuthStore()

  const [formData, setFormData] = useState({
    name: user?.name || '',
    username: user?.username || '',
    timezone: user?.timezone || 'Asia/Ho_Chi_Minh',
    language: user?.language || 'VI',
    theme: user?.theme || 'SYSTEM',
  })
  const [validationErrors, setValidationErrors] = useState({
    name: '',
    username: '',
  })
  const [successMessage, setSuccessMessage] = useState('')

  useEffect(() => {
    if (user) {
      setFormData({
        name: user.name || '',
        username: user.username || '',
        timezone: user.timezone || 'Asia/Ho_Chi_Minh',
        language: user.language || 'VI',
        theme: user.theme || 'SYSTEM',
      })
    }
  }, [user])

  const validateForm = (): boolean => {
    const errors = {
      name: '',
      username: '',
    }

    if (formData.name && formData.name.length > 100) {
      errors.name = 'Name must be 100 characters or less'
    }

    if (formData.username) {
      if (formData.username.length < 3 || formData.username.length > 30) {
        errors.username = 'Username must be 3-30 characters'
      } else if (!/^[a-zA-Z0-9_-]+$/.test(formData.username)) {
        errors.username = 'Username must be alphanumeric + underscore/hyphen only'
      }
    }

    setValidationErrors(errors)
    return !errors.name && !errors.username
  }

  const handleUpdate = async () => {
    clearError()
    setSuccessMessage('')

    if (!validateForm()) {
      return
    }

    try {
      await updateProfile(formData)
      setSuccessMessage('Profile updated successfully!')
    } catch (err) {
      console.error('Profile update failed:', err)
    }
  }

  const handleChange = (field: string) => (
    event: React.ChangeEvent<HTMLInputElement | { value: unknown }>
  ) => {
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
      <CardHeader title="Profile Settings" />
      <Divider />
      <CardContent>
        <Stack spacing={3}>
          {error && (
            <Alert severity="error" onClose={clearError}>
              {error}
            </Alert>
          )}
          {successMessage && <Alert severity="success">{successMessage}</Alert>}

          <TextField
            label="Name"
            value={formData.name}
            onChange={handleChange('name')}
            fullWidth
            error={!!validationErrors.name}
            helperText={validationErrors.name}
            disabled={isLoading}
          />

          <TextField
            label="Username"
            value={formData.username}
            onChange={handleChange('username')}
            fullWidth
            error={!!validationErrors.username}
            helperText={validationErrors.username || '3-30 characters, alphanumeric + underscore/hyphen'}
            disabled={isLoading}
          />

          <FormControl fullWidth>
            <InputLabel>Timezone</InputLabel>
            <Select
              value={formData.timezone}
              label="Timezone"
              onChange={handleChange('timezone') as any}
              disabled={isLoading}
            >
              <MenuItem value="Asia/Ho_Chi_Minh">Asia/Ho_Chi_Minh (GMT+7)</MenuItem>
              <MenuItem value="Asia/Bangkok">Asia/Bangkok (GMT+7)</MenuItem>
              <MenuItem value="Asia/Singapore">Asia/Singapore (GMT+8)</MenuItem>
              <MenuItem value="UTC">UTC</MenuItem>
            </Select>
          </FormControl>

          <FormControl component="fieldset">
            <FormLabel component="legend">Language</FormLabel>
            <RadioGroup
              row
              value={formData.language}
              onChange={handleChange('language')}
            >
              <FormControlLabel value="VI" control={<Radio />} label="Vietnamese" disabled={isLoading} />
              <FormControlLabel value="EN" control={<Radio />} label="English" disabled={isLoading} />
            </RadioGroup>
          </FormControl>

          <FormControl component="fieldset">
            <FormLabel component="legend">Theme</FormLabel>
            <RadioGroup
              row
              value={formData.theme}
              onChange={handleChange('theme')}
            >
              <FormControlLabel value="LIGHT" control={<Radio />} label="Light" disabled={isLoading} />
              <FormControlLabel value="DARK" control={<Radio />} label="Dark" disabled={isLoading} />
              <FormControlLabel value="SYSTEM" control={<Radio />} label="System" disabled={isLoading} />
            </RadioGroup>
          </FormControl>

          <Stack direction="row" spacing={2}>
            <Button
              variant="contained"
              onClick={handleUpdate}
              disabled={isLoading}
              startIcon={isLoading && <CircularProgress size={20} />}
            >
              {isLoading ? 'Saving...' : 'Save Changes'}
            </Button>
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  )
}

export default ProfileSettings
