import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '@/store/slices/auth.slice'
import { authClient, Language, Theme, type UpdateProfileRequest, type ChangePasswordRequest } from '@/api/clients/auth.client'
import { APP_ROUTES } from '@/config/app.config'

export default function SettingsPage() {
  const navigate = useNavigate()
  const { user, setUser, logout } = useAuthStore()
  const [activeTab, setActiveTab] = useState<'profile' | 'password'>('profile')
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)

  // Profile form state
  const [profileData, setProfileData] = useState<UpdateProfileRequest>({
    name: user?.name || '',
    username: user?.username || '',
    timezone: user?.timezone || 'Asia/Ho_Chi_Minh',
    language: user?.language || Language.VI,
    theme: user?.theme || Theme.SYSTEM,
  })

  // Password form state
  const [passwordData, setPasswordData] = useState<ChangePasswordRequest>({
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: '',
  })

  const [showPasswords, setShowPasswords] = useState({
    current: false,
    new: false,
    confirm: false,
  })

  // UC-005: Update User Profile
  const handleProfileSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setSuccess(null)
    setIsLoading(true)

    try {
      const response = await authClient.updateProfile(profileData)
      setUser(response.user)
      setSuccess(response.message || 'Profile updated successfully')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update profile')
    } finally {
      setIsLoading(false)
    }
  }

  // UC-006: Change Password
  const handlePasswordSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setSuccess(null)

    // Validate passwords match
    if (passwordData.newPassword !== passwordData.confirmNewPassword) {
      setError('Passwords do not match')
      return
    }

    setIsLoading(true)

    try {
      const response = await authClient.changePassword(passwordData)
      setSuccess(response.message || 'Password changed successfully. Please login again.')

      // Logout and redirect to login
      setTimeout(async () => {
        await logout()
        navigate(APP_ROUTES.LOGIN)
      }, 2000)
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to change password')
      setIsLoading(false)
    }
  }

  // UC-004: Logout
  const handleLogout = async () => {
    try {
      await logout()
      navigate(APP_ROUTES.LOGIN)
    } catch (err) {
      // Error is handled by store, always redirect to login
      navigate(APP_ROUTES.LOGIN)
    }
  }

  if (!user) {
    navigate(APP_ROUTES.LOGIN)
    return null
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold">Settings</h1>
          <button
            onClick={handleLogout}
            className="px-4 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700"
          >
            Logout
          </button>
        </div>

        <div className="bg-white rounded-lg shadow-md">
          {/* Tabs */}
          <div className="border-b border-gray-200">
            <div className="flex">
              <button
                onClick={() => setActiveTab('profile')}
                className={`px-6 py-3 font-medium ${
                  activeTab === 'profile'
                    ? 'border-b-2 border-blue-600 text-blue-600'
                    : 'text-gray-600 hover:text-gray-800'
                }`}
              >
                Profile Settings
              </button>
              <button
                onClick={() => setActiveTab('password')}
                className={`px-6 py-3 font-medium ${
                  activeTab === 'password'
                    ? 'border-b-2 border-blue-600 text-blue-600'
                    : 'text-gray-600 hover:text-gray-800'
                }`}
              >
                Change Password
              </button>
            </div>
          </div>

          {/* Content */}
          <div className="p-6">
            {error && (
              <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-md">
                <p className="text-sm text-red-600">{error}</p>
              </div>
            )}

            {success && (
              <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-md">
                <p className="text-sm text-green-600">{success}</p>
              </div>
            )}

            {/* Profile Tab */}
            {activeTab === 'profile' && (
              <form onSubmit={handleProfileSubmit} className="space-y-6">
                <div>
                  <label className="block text-sm font-medium mb-2">Email</label>
                  <input
                    type="email"
                    value={user.email}
                    disabled
                    className="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-100"
                  />
                  <p className="mt-1 text-xs text-gray-500">Email cannot be changed</p>
                </div>

                <div>
                  <label className="block text-sm font-medium mb-2">Name</label>
                  <input
                    type="text"
                    value={profileData.name}
                    onChange={(e) => setProfileData({ ...profileData, name: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium mb-2">Username</label>
                  <input
                    type="text"
                    value={profileData.username}
                    onChange={(e) => setProfileData({ ...profileData, username: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md"
                    pattern="^[a-z0-9_]{3,30}$"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium mb-2">Timezone</label>
                  <select
                    value={profileData.timezone}
                    onChange={(e) => setProfileData({ ...profileData, timezone: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md"
                  >
                    <option value="Asia/Ho_Chi_Minh">Asia/Ho Chi Minh (GMT+7)</option>
                    <option value="Asia/Bangkok">Asia/Bangkok (GMT+7)</option>
                    <option value="UTC">UTC</option>
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium mb-2">Language</label>
                  <div className="flex gap-4">
                    <label className="flex items-center">
                      <input
                        type="radio"
                        value={Language.EN}
                        checked={profileData.language === Language.EN}
                        onChange={(e) => setProfileData({ ...profileData, language: e.target.value as Language })}
                        className="mr-2"
                      />
                      English
                    </label>
                    <label className="flex items-center">
                      <input
                        type="radio"
                        value={Language.VI}
                        checked={profileData.language === Language.VI}
                        onChange={(e) => setProfileData({ ...profileData, language: e.target.value as Language })}
                        className="mr-2"
                      />
                      Vietnamese
                    </label>
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium mb-2">Theme</label>
                  <div className="flex gap-4">
                    <label className="flex items-center">
                      <input
                        type="radio"
                        value={Theme.LIGHT}
                        checked={profileData.theme === Theme.LIGHT}
                        onChange={(e) => setProfileData({ ...profileData, theme: e.target.value as Theme })}
                        className="mr-2"
                      />
                      Light
                    </label>
                    <label className="flex items-center">
                      <input
                        type="radio"
                        value={Theme.DARK}
                        checked={profileData.theme === Theme.DARK}
                        onChange={(e) => setProfileData({ ...profileData, theme: e.target.value as Theme })}
                        className="mr-2"
                      />
                      Dark
                    </label>
                    <label className="flex items-center">
                      <input
                        type="radio"
                        value={Theme.SYSTEM}
                        checked={profileData.theme === Theme.SYSTEM}
                        onChange={(e) => setProfileData({ ...profileData, theme: e.target.value as Theme })}
                        className="mr-2"
                      />
                      System
                    </label>
                  </div>
                </div>

                <button
                  type="submit"
                  disabled={isLoading}
                  className="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50"
                >
                  {isLoading ? 'Saving...' : 'Save Changes'}
                </button>
              </form>
            )}

            {/* Password Tab */}
            {activeTab === 'password' && (
              <form onSubmit={handlePasswordSubmit} className="space-y-6">
                <div className="p-4 bg-yellow-50 border border-yellow-200 rounded-md">
                  <p className="text-sm text-yellow-800">
                    You will be logged out from all devices after changing your password.
                  </p>
                </div>

                <div>
                  <label className="block text-sm font-medium mb-2">Current Password</label>
                  <div className="relative">
                    <input
                      type={showPasswords.current ? 'text' : 'password'}
                      value={passwordData.currentPassword}
                      onChange={(e) => setPasswordData({ ...passwordData, currentPassword: e.target.value })}
                      required
                      className="w-full px-3 py-2 border border-gray-300 rounded-md"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPasswords({ ...showPasswords, current: !showPasswords.current })}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-gray-600"
                    >
                      {showPasswords.current ? 'Hide' : 'Show'}
                    </button>
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium mb-2">New Password</label>
                  <div className="relative">
                    <input
                      type={showPasswords.new ? 'text' : 'password'}
                      value={passwordData.newPassword}
                      onChange={(e) => setPasswordData({ ...passwordData, newPassword: e.target.value })}
                      required
                      minLength={8}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md"
                      placeholder="Min 8 characters"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPasswords({ ...showPasswords, new: !showPasswords.new })}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-gray-600"
                    >
                      {showPasswords.new ? 'Hide' : 'Show'}
                    </button>
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium mb-2">Confirm New Password</label>
                  <div className="relative">
                    <input
                      type={showPasswords.confirm ? 'text' : 'password'}
                      value={passwordData.confirmNewPassword}
                      onChange={(e) => setPasswordData({ ...passwordData, confirmNewPassword: e.target.value })}
                      required
                      className="w-full px-3 py-2 border border-gray-300 rounded-md"
                      placeholder="Re-enter new password"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPasswords({ ...showPasswords, confirm: !showPasswords.confirm })}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-gray-600"
                    >
                      {showPasswords.confirm ? 'Hide' : 'Show'}
                    </button>
                  </div>
                </div>

                <button
                  type="submit"
                  disabled={isLoading}
                  className="w-full px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 disabled:opacity-50"
                >
                  {isLoading ? 'Changing Password...' : 'Change Password'}
                </button>
              </form>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
