/**
 * Profile Settings Component
 * 
 * UC-005: Update User Profile
 * 
 * Features:
 * - Update name, timezone, language, theme
 * - Form validation
 * - Immediate theme/language application
 * - Success/error handling
 */

import * as React from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { toast } from 'sonner'
import { useAuth } from '@/hooks/domain/useAuth'
import { userApi } from '@/api/modules/user.api'
import { FormInput } from '@/components/common/Form'
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
import { User, Save } from 'lucide-react'
import { useTheme } from 'next-themes'
import { useTranslation } from 'react-i18next'

// Validation schema
const profileSchema = z.object({
  name: z
    .string()
    .min(1, 'Name is required')
    .max(100, 'Name must be 100 characters or less')
    .trim(),
  timezone: z.string().min(1, 'Timezone is required'),
  language: z.enum(['VI', 'EN'], {
    required_error: 'Language is required',
  }),
  theme: z.enum(['LIGHT', 'DARK', 'SYSTEM'], {
    required_error: 'Theme is required',
  }),
})

type ProfileFormData = z.infer<typeof profileSchema>

// Common timezones for Vietnam users
const TIMEZONES = [
  { value: 'Asia/Ho_Chi_Minh', label: 'Asia/Ho_Chi_Minh (GMT+7)' },
  { value: 'Asia/Bangkok', label: 'Asia/Bangkok (GMT+7)' },
  { value: 'Asia/Singapore', label: 'Asia/Singapore (GMT+8)' },
  { value: 'UTC', label: 'UTC (GMT+0)' },
]

export function ProfileSettings() {
  const { user, logout } = useAuth()
  const { theme: currentTheme, setTheme: setThemeMode } = useTheme()
  const { i18n } = useTranslation()

  const [isSubmitting, setIsSubmitting] = React.useState(false)

  const form = useForm<ProfileFormData>({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      name: user?.name || '',
      timezone: user?.timezone || 'Asia/Ho_Chi_Minh',
      language: (user?.language?.toUpperCase() as 'VI' | 'EN') || 'VI',
      theme: (user?.theme?.toUpperCase() as 'LIGHT' | 'DARK' | 'SYSTEM') || 'SYSTEM',
    },
  })

  // Update form when user data changes
  React.useEffect(() => {
    if (user) {
      form.reset({
        name: user.name || '',
        timezone: user.timezone || 'Asia/Ho_Chi_Minh',
        language: (user.language?.toUpperCase() as 'VI' | 'EN') || 'VI',
        theme: (user.theme?.toUpperCase() as 'LIGHT' | 'DARK' | 'SYSTEM') || 'SYSTEM',
      })
    }
  }, [user, form])

  const onSubmit = React.useCallback(
    async (data: ProfileFormData) => {
      setIsSubmitting(true)
      try {
        const response = await userApi.updateProfile(data)
        
        // Update theme immediately if changed
        if (data.theme !== user?.theme?.toUpperCase()) {
          if (data.theme === 'SYSTEM') {
            const systemTheme = window.matchMedia('(prefers-color-scheme: dark)').matches
              ? 'dark'
              : 'light'
            setThemeMode(systemTheme)
          } else {
            setThemeMode(data.theme.toLowerCase() as 'light' | 'dark')
          }
        }

        // Update language immediately if changed
        if (data.language !== user?.language?.toUpperCase()) {
          i18n.changeLanguage(data.language.toLowerCase())
        }

        toast.success('Profile updated successfully')
        
        // Refresh user data
        window.location.reload() // Simple refresh, could be improved with query invalidation
      } catch (error: any) {
        const errorMessage =
          error?.message || 'Failed to update profile. Please try again.'
        toast.error(errorMessage)
      } finally {
        setIsSubmitting(false)
      }
    },
    [user, setThemeMode, i18n],
  )

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center gap-2">
          <User className="h-5 w-5" />
          <CardTitle>Profile Information</CardTitle>
        </div>
        <CardDescription>
          Update your profile settings
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="name">Name *</Label>
            <FormInput
              id="name"
              {...(form.formState.errors.name?.message && {
                error: form.formState.errors.name.message,
              })}
              {...form.register('name')}
              disabled={isSubmitting}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="timezone">Timezone *</Label>
            <Select
              value={form.watch('timezone')}
              onValueChange={(value) => form.setValue('timezone', value)}
              disabled={isSubmitting}
            >
              <SelectTrigger>
                <SelectValue placeholder="Select timezone" />
              </SelectTrigger>
              <SelectContent>
                {TIMEZONES.map((tz) => (
                  <SelectItem key={tz.value} value={tz.value}>
                    {tz.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            {form.formState.errors.timezone?.message && (
              <p className="text-sm text-destructive">
                {form.formState.errors.timezone.message}
              </p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="language">Language *</Label>
            <Select
              value={form.watch('language')}
              onValueChange={(value: 'VI' | 'EN') => form.setValue('language', value)}
              disabled={isSubmitting}
            >
              <SelectTrigger>
                <SelectValue placeholder="Select language" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="VI">Vietnamese</SelectItem>
                <SelectItem value="EN">English</SelectItem>
              </SelectContent>
            </Select>
            {form.formState.errors.language?.message && (
              <p className="text-sm text-destructive">
                {form.formState.errors.language.message}
              </p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="theme">Theme *</Label>
            <Select
              value={form.watch('theme')}
              onValueChange={(value: 'LIGHT' | 'DARK' | 'SYSTEM') =>
                form.setValue('theme', value)
              }
              disabled={isSubmitting}
            >
              <SelectTrigger>
                <SelectValue placeholder="Select theme" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="LIGHT">Light</SelectItem>
                <SelectItem value="DARK">Dark</SelectItem>
                <SelectItem value="SYSTEM">System</SelectItem>
              </SelectContent>
            </Select>
            {form.formState.errors.theme?.message && (
              <p className="text-sm text-destructive">
                {form.formState.errors.theme.message}
              </p>
            )}
          </div>

          <div className="flex justify-end gap-2 pt-4">
            <Button
              type="submit"
              disabled={isSubmitting}
              isLoading={isSubmitting}
              loadingText="Saving..."
            >
              <Save className="mr-2 h-4 w-4" />
              Save Changes
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  )
}

// Default export for compatibility
export default ProfileSettings

