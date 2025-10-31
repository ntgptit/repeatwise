/**
 * Header Component
 * 
 * Application header with logo, navigation, user menu, and theme toggle
 * 
 * Features:
 * - Logo and branding
 * - Search bar
 * - User menu dropdown
 * - Theme toggle
 * - Mobile responsive
 */

import * as React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { SearchBar } from './Navigation'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  Moon,
  Sun,
  User,
  Settings,
  LogOut,
  Search,
  Menu,
} from 'lucide-react'
import { useTheme } from 'next-themes'
import { cn } from '@/lib/utils'

export interface HeaderProps {
  /** User information */
  user?: {
    name?: string
    email?: string
    avatar?: string
  }
  /** Search handler */
  onSearch?: (query: string) => void
  /** Logout handler */
  onLogout?: () => void
  /** Mobile menu toggle handler */
  onMobileMenuToggle?: () => void
  /** Show search bar */
  showSearch?: boolean
  /** Additional className */
  className?: string
}

export const Header = React.memo<HeaderProps>(
  ({
    user,
    onSearch,
    onLogout,
    onMobileMenuToggle,
    showSearch = true,
    className,
  }) => {
  const { theme, setTheme } = useTheme()
  const navigate = useNavigate()
  const [mounted, setMounted] = React.useState(false)

  React.useEffect(() => {
    setMounted(true)
  }, [])

  const toggleTheme = () => {
    setTheme(theme === 'dark' ? 'light' : 'dark')
  }

  // Prevent hydration mismatch
  if (!mounted) {
    return (
      <header
        className={cn(
          'sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60',
          className,
        )}
        role="banner"
      >
        <div className="container flex h-16 items-center justify-between px-4">
          <div className="flex items-center gap-4">
            <Link
              to="/"
              className="flex items-center gap-2 font-semibold text-lg"
              aria-label="RepeatWise Home"
            >
              <span className="text-primary">RepeatWise</span>
            </Link>
          </div>
        </div>
      </header>
    )
  }

  return (
      <header
        className={cn(
          'sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60',
          className,
        )}
        role="banner"
      >
        <div className="container flex h-16 items-center justify-between px-4">
          {/* Left: Logo and Mobile Menu */}
          <div className="flex items-center gap-4">
            {onMobileMenuToggle && (
              <Button
                variant="ghost"
                size="icon"
                className="lg:hidden"
                onClick={onMobileMenuToggle}
                aria-label="Toggle menu"
              >
                <Menu className="h-5 w-5" />
              </Button>
            )}
            <Link
              to="/"
              className="flex items-center gap-2 font-semibold text-lg"
              aria-label="RepeatWise Home"
            >
              <span className="text-primary">RepeatWise</span>
            </Link>
          </div>

          {/* Center: Search Bar */}
          {showSearch && onSearch && (
            <div className="hidden md:flex flex-1 max-w-md mx-8">
              <SearchBar
                placeholder="Search decks, cards..."
                onSearch={onSearch}
                className="w-full"
              />
            </div>
          )}

          {/* Right: Actions */}
          <div className="flex items-center gap-2">
            {/* Search Button (Mobile) */}
            {showSearch && onSearch && (
              <Button
                variant="ghost"
                size="icon"
                className="md:hidden"
                onClick={() => {
                  // Mobile search could trigger a modal or drawer
                  // For now, just focus on search
                }}
                aria-label="Search"
              >
                <Search className="h-5 w-5" />
              </Button>
            )}

            {/* Theme Toggle */}
            <Button
              variant="ghost"
              size="icon"
              onClick={toggleTheme}
              aria-label="Toggle theme"
            >
              <Sun className="h-5 w-5 rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
              <Moon className="absolute h-5 w-5 rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
            </Button>

            {/* User Menu */}
            {user ? (
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" size="icon" className="relative">
                    {user.avatar ? (
                      <img
                        src={user.avatar}
                        alt={user.name || user.email || 'User'}
                        className="h-8 w-8 rounded-full"
                      />
                    ) : (
                      <User className="h-5 w-5" />
                    )}
                    <span className="sr-only">User menu</span>
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-56">
                  <DropdownMenuLabel>
                    <div className="flex flex-col space-y-1">
                      {user.name && (
                        <p className="text-sm font-medium">{user.name}</p>
                      )}
                      {user.email && (
                        <p className="text-xs text-muted-foreground">
                          {user.email}
                        </p>
                      )}
                    </div>
                  </DropdownMenuLabel>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem onClick={() => navigate('/profile')}>
                    <User className="mr-2 h-4 w-4" />
                    Profile
                  </DropdownMenuItem>
                  <DropdownMenuItem onClick={() => navigate('/settings')}>
                    <Settings className="mr-2 h-4 w-4" />
                    Settings
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem
                    onClick={onLogout}
                    className="text-destructive focus:text-destructive"
                  >
                    <LogOut className="mr-2 h-4 w-4" />
                    Logout
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            ) : (
              <div className="flex items-center gap-2">
                <Button variant="ghost" onClick={() => navigate('/login')}>
                  Login
                </Button>
                <Button onClick={() => navigate('/register')}>
                  Sign Up
                </Button>
              </div>
            )}
          </div>
        </div>
      </header>
    )
  },
)

Header.displayName = 'Header'

