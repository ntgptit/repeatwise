/**
 * Settings Page
 *
 * Displays user settings and preferences
 *
 * Features:
 * - Profile settings (name, email)
 * - Password change
 * - SRS settings configuration
 * - Preferences (theme, language)
 * - Notification settings
 */

import * as React from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Layout, PageContainer, Section } from '@/components/common/Layout'
import { Header } from '@/components/common/Header'
import { Sidebar } from '@/components/common/Sidebar'
import { Breadcrumb } from '@/components/common/Breadcrumb'
import { LoadingSpinner } from '@/components/common/LoadingSpinner'
import { EmptyState } from '@/components/common/EmptyState'
import { FormInput, FormTextarea } from '@/components/common/Form'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Label } from '@/components/ui/label'
import { Switch } from '@/components/ui/switch'
import { User, Lock, Brain, Globe, Bell, Save } from 'lucide-react'
import { ROUTES } from '@/constants/routes'
import { useAuth } from '@/hooks/domain/useAuth'
import { userApi } from '@/api/modules/user.api'
import { toast } from 'sonner'
import { ProfileSettings } from './ProfileSettings'

// Password schema
const passwordSchema = z
  .object({
    currentPassword: z.string().min(1, 'Current password is required'),
    newPassword: z
      .string()
      .min(8, 'Password must be at least 8 characters'),
    confirmNewPassword: z
      .string()
      .min(1, 'Please confirm your password'),
  })
  .refine((data) => data.newPassword === data.confirmNewPassword, {
    message: 'Passwords do not match',
    path: ['confirmNewPassword'],
  })
  .refine(
    (data) => data.newPassword !== data.currentPassword,
    {
      message: 'New password must be different from current password',
      path: ['newPassword'],
    },
  )

type PasswordFormData = z.infer<typeof passwordSchema>

export function SettingsPage() {
  const navigate = useNavigate()
  const { user, logout } = useAuth()

  // Password form
  const passwordForm = useForm<PasswordFormData>({
    resolver: zodResolver(passwordSchema),
    defaultValues: {
      currentPassword: '',
      newPassword: '',
      confirmNewPassword: '',
    },
  })

  // SRS Settings state
  const [srsSettings, setSrsSettings] = React.useState({
    totalBoxes: 7,
    reviewOrder: 'RANDOM' as 'DUE_DATE_ASC' | 'RANDOM' | 'CURRENT_BOX_ASC',
    newCardsPerDay: 20,
    maxReviewsPerDay: 200,
    forgottenCardAction: 'MOVE_TO_BOX_1' as 'MOVE_TO_BOX_1' | 'MOVE_DOWN_N_BOXES' | 'REPEAT_IN_SESSION',
    moveDownBoxes: 1,
    notificationEnabled: true,
    notificationTime: '09:00',
  })

  // Preferences state
  const [preferences, setPreferences] = React.useState({
    theme: 'system' as 'light' | 'dark' | 'system',
    language: 'vi' as 'vi' | 'en',
  })

  // TODO: Fetch settings from API
  // const {
  //   data: userSettings,
  //   isLoading,
  //   error,
  // } = useQuery({
  //   queryKey: ['settings'],
  //   queryFn: () => settingsApi.getSettings(),
  // })

  const isLoading = false
  const error = null

  const handlePasswordSubmit = React.useCallback(
    async (data: PasswordFormData) => {
      try {
        await userApi.changePassword({
          currentPassword: data.currentPassword,
          newPassword: data.newPassword,
          confirmNewPassword: data.confirmNewPassword,
        })
        
        toast.success(
          'Password changed successfully. Please login with your new password.',
        )
        
        // Logout and redirect to login after password change
        logout({
          onSuccess: () => {
            navigate('/login', { replace: true })
          },
        })
      } catch (error: any) {
        const errorMessage =
          error?.message || 'Failed to change password. Please try again.'
        toast.error(errorMessage)
      }
    },
    [logout, navigate],
  )

  const handleSrsSettingsSave = React.useCallback(async () => {
    try {
      // TODO: Update SRS settings via API
      // await srsSettingsApi.updateSettings(srsSettings)
      toast.success('SRS settings saved successfully')
    } catch (error) {
      console.error('Failed to save SRS settings:', error)
      toast.error('Failed to save SRS settings')
    }
  }, [srsSettings])

  const handlePreferencesSave = React.useCallback(async () => {
    try {
      // TODO: Update preferences via API
      // await preferencesApi.updatePreferences(preferences)
      toast.success('Preferences saved successfully')
    } catch (error) {
      console.error('Failed to save preferences:', error)
      toast.error('Failed to save preferences')
    }
  }, [preferences])


  if (isLoading) {
    return (
      <Layout>
      <Header 
        {...(user && { user: { name: user.name, email: user.email } })}
        onLogout={() => {
          logout({
            onSuccess: () => {
              navigate(ROUTES.LOGIN, { replace: true })
            },
          })
        }}
      />
        <PageContainer sidebar={<Sidebar />}>
          <div className="flex items-center justify-center min-h-screen">
            <LoadingSpinner label="Loading settings..." />
          </div>
        </PageContainer>
      </Layout>
    )
  }

  if (error) {
    return (
      <Layout>
      <Header 
        {...(user && { user: { name: user.name, email: user.email } })}
        onLogout={() => {
          logout({
            onSuccess: () => {
              navigate(ROUTES.LOGIN, { replace: true })
            },
          })
        }}
      />
        <PageContainer sidebar={<Sidebar />}>
          <div className="flex items-center justify-center min-h-screen">
            <EmptyState
              message="Failed to load settings"
              description="Unable to load your settings. Please try again later."
            />
          </div>
        </PageContainer>
      </Layout>
    )
  }

  const breadcrumbItems = [{ label: 'Settings' }]

  return (
    <Layout>
      <Header {...(user && { user: { name: user.name, email: user.email } })} />
      <PageContainer sidebar={<Sidebar />}>
        <div className="container mx-auto px-4 py-6 space-y-6 max-w-4xl">
          {/* Breadcrumb */}
          <Breadcrumb items={breadcrumbItems} />

          {/* Header */}
          <Section>
            <div>
              <h1 className="text-3xl font-bold">Settings</h1>
              <p className="text-muted-foreground mt-1">
                Manage your account settings and preferences
              </p>
            </div>
          </Section>

          {/* Profile Settings */}
          <ProfileSettings />

          {/* Password Settings */}
          <Card>
            <CardHeader>
              <div className="flex items-center gap-2">
                <Lock className="h-5 w-5" />
                <CardTitle>Password</CardTitle>
              </div>
              <CardDescription>
                Change your password
              </CardDescription>
            </CardHeader>
            <CardContent>
              <form
                onSubmit={passwordForm.handleSubmit(handlePasswordSubmit)}
                className="space-y-4"
              >
                <FormInput
                  label="Current Password"
                  type="password"
                  required
                  {...(passwordForm.formState.errors.currentPassword?.message && {
                    error: passwordForm.formState.errors.currentPassword.message,
                  })}
                  {...passwordForm.register('currentPassword')}
                  disabled={passwordForm.formState.isSubmitting}
                />
                <FormInput
                  label="New Password"
                  type="password"
                  required
                  {...(passwordForm.formState.errors.newPassword?.message && {
                    error: passwordForm.formState.errors.newPassword.message,
                  })}
                  {...passwordForm.register('newPassword')}
                  disabled={passwordForm.formState.isSubmitting}
                />
                <FormInput
                  label="Confirm New Password"
                  type="password"
                  required
                  {...(passwordForm.formState.errors.confirmNewPassword?.message && {
                    error: passwordForm.formState.errors.confirmNewPassword.message,
                  })}
                  {...passwordForm.register('confirmNewPassword')}
                  disabled={passwordForm.formState.isSubmitting}
                />
                <div className="rounded-md bg-muted p-3 text-sm text-muted-foreground">
                  ⚠️ You will be logged out from all devices after changing password.
                </div>
                <Button
                  type="submit"
                  disabled={passwordForm.formState.isSubmitting}
                >
                  <Save className="mr-2 h-4 w-4" />
                  {passwordForm.formState.isSubmitting ? 'Changing...' : 'Change Password'}
                </Button>
              </form>
            </CardContent>
          </Card>

          {/* SRS Settings */}
          <Card>
            <CardHeader>
              <div className="flex items-center gap-2">
                <Brain className="h-5 w-5" />
                <CardTitle>SRS Configuration</CardTitle>
              </div>
              <CardDescription>
                Configure your Spaced Repetition System settings
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="totalBoxes">Total Boxes</Label>
                <input
                  id="totalBoxes"
                  type="number"
                  min="3"
                  max="10"
                  value={srsSettings.totalBoxes}
                  onChange={(e) =>
                    setSrsSettings({
                      ...srsSettings,
                      totalBoxes: parseInt(e.target.value, 10),
                    })
                  }
                  className="w-full px-3 py-2 border rounded-md bg-background"
                />
                <p className="text-xs text-muted-foreground">
                  Number of SRS boxes (3-10)
                </p>
              </div>

              <div className="space-y-2">
                <Label htmlFor="reviewOrder">Review Order</Label>
                <Select
                  value={srsSettings.reviewOrder}
                  onValueChange={(value: typeof srsSettings.reviewOrder) =>
                    setSrsSettings({ ...srsSettings, reviewOrder: value })
                  }
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="DUE_DATE_ASC">Due Date (Earliest First)</SelectItem>
                    <SelectItem value="RANDOM">Random</SelectItem>
                    <SelectItem value="CURRENT_BOX_ASC">Current Box (Lower First)</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="newCardsPerDay">New Cards Per Day</Label>
                <input
                  id="newCardsPerDay"
                  type="number"
                  min="1"
                  max="100"
                  value={srsSettings.newCardsPerDay}
                  onChange={(e) =>
                    setSrsSettings({
                      ...srsSettings,
                      newCardsPerDay: parseInt(e.target.value, 10),
                    })
                  }
                  className="w-full px-3 py-2 border rounded-md bg-background"
                />
                <p className="text-xs text-muted-foreground">
                  Maximum new cards introduced daily (1-100)
                </p>
              </div>

              <div className="space-y-2">
                <Label htmlFor="maxReviewsPerDay">Max Reviews Per Day</Label>
                <input
                  id="maxReviewsPerDay"
                  type="number"
                  min="10"
                  max="500"
                  value={srsSettings.maxReviewsPerDay}
                  onChange={(e) =>
                    setSrsSettings({
                      ...srsSettings,
                      maxReviewsPerDay: parseInt(e.target.value, 10),
                    })
                  }
                  className="w-full px-3 py-2 border rounded-md bg-background"
                />
                <p className="text-xs text-muted-foreground">
                  Maximum cards reviewed per day (10-500)
                </p>
              </div>

              <div className="space-y-2">
                <Label htmlFor="forgottenCardAction">Forgotten Card Action</Label>
                <Select
                  value={srsSettings.forgottenCardAction}
                  onValueChange={(value: typeof srsSettings.forgottenCardAction) =>
                    setSrsSettings({ ...srsSettings, forgottenCardAction: value })
                  }
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="MOVE_TO_BOX_1">Move to Box 1</SelectItem>
                    <SelectItem value="MOVE_DOWN_N_BOXES">Move Down N Boxes</SelectItem>
                    <SelectItem value="REPEAT_IN_SESSION">Repeat in Session</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="flex items-center justify-between">
                <div className="space-y-0.5">
                  <Label htmlFor="notificationEnabled">Notifications</Label>
                  <p className="text-xs text-muted-foreground">
                    Enable daily review reminders
                  </p>
                </div>
                <Switch
                  id="notificationEnabled"
                  checked={srsSettings.notificationEnabled}
                  onCheckedChange={(checked) =>
                    setSrsSettings({
                      ...srsSettings,
                      notificationEnabled: checked,
                    })
                  }
                />
              </div>

              <Button onClick={handleSrsSettingsSave}>
                <Save className="mr-2 h-4 w-4" />
                Save SRS Settings
              </Button>
            </CardContent>
          </Card>

          {/* Preferences */}
          <Card>
            <CardHeader>
              <div className="flex items-center gap-2">
                <Globe className="h-5 w-5" />
                <CardTitle>Preferences</CardTitle>
              </div>
              <CardDescription>
                Customize your app preferences
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="theme">Theme</Label>
                <Select
                  value={preferences.theme}
                  onValueChange={(value: typeof preferences.theme) =>
                    setPreferences({ ...preferences, theme: value })
                  }
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="light">Light</SelectItem>
                    <SelectItem value="dark">Dark</SelectItem>
                    <SelectItem value="system">System</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="language">Language</Label>
                <Select
                  value={preferences.language}
                  onValueChange={(value: typeof preferences.language) =>
                    setPreferences({ ...preferences, language: value })
                  }
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="vi">Tiếng Việt</SelectItem>
                    <SelectItem value="en">English</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <Button onClick={handlePreferencesSave}>
                <Save className="mr-2 h-4 w-4" />
                Save Preferences
              </Button>
            </CardContent>
          </Card>
        </div>
      </PageContainer>
    </Layout>
  )
}

// Default export for compatibility
export default SettingsPage

