/**
 * Sidebar Component
 * 
 * Navigation sidebar with folder tree and main navigation
 * 
 * Features:
 * - Collapsible navigation
 * - Folder tree integration
 * - Main navigation links
 * - Accessible keyboard navigation
 */

import * as React from 'react'
import { Link, useLocation } from 'react-router-dom'
import { cn } from '@/lib/utils'
import {
  LayoutDashboard,
  BookOpen,
  FolderTree,
  Settings,
  BarChart3,
} from 'lucide-react'

export interface SidebarItem {
  label: string
  path: string
  icon?: React.ReactNode
  badge?: string | number
}

export interface SidebarProps {
  items?: SidebarItem[]
  className?: string
  folderTree?: React.ReactNode
}

const defaultItems: SidebarItem[] = [
  {
    label: 'Dashboard',
    path: '/dashboard',
    icon: <LayoutDashboard className="h-5 w-5" />,
  },
  {
    label: 'Decks',
    path: '/decks',
    icon: <BookOpen className="h-5 w-5" />,
  },
  {
    label: 'Folders',
    path: '/folders',
    icon: <FolderTree className="h-5 w-5" />,
  },
  {
    label: 'Statistics',
    path: '/stats',
    icon: <BarChart3 className="h-5 w-5" />,
  },
  {
    label: 'Settings',
    path: '/settings',
    icon: <Settings className="h-5 w-5" />,
  },
]

export const Sidebar = React.memo<SidebarProps>(
  ({ items = defaultItems, className, folderTree }) => {
    const location = useLocation()

    return (
      <aside
        className={cn(
          'flex flex-col h-full bg-background border-r',
          className,
        )}
        role="navigation"
        aria-label="Main navigation"
      >
        {/* Folder Tree Section */}
        {folderTree && (
          <div className="border-b p-4">
            <h2 className="text-sm font-semibold mb-2">Folders</h2>
            {folderTree}
          </div>
        )}

        {/* Main Navigation */}
        <nav className="flex-1 overflow-y-auto p-4" aria-label="Main navigation">
          <ul className="space-y-1">
            {items.map((item) => {
              const isActive = location.pathname === item.path

              return (
                <li key={item.path}>
                  <Link
                    to={item.path}
                    className={cn(
                      'flex items-center gap-3 px-3 py-2 rounded-md text-sm font-medium transition-colors',
                      'hover:bg-accent hover:text-accent-foreground',
                      'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2',
                      isActive &&
                        'bg-accent text-accent-foreground font-semibold',
                      !isActive && 'text-muted-foreground',
                    )}
                    aria-current={isActive ? 'page' : undefined}
                  >
                    {item.icon && (
                      <span className="flex-shrink-0" aria-hidden="true">
                        {item.icon}
                      </span>
                    )}
                    <span className="flex-1">{item.label}</span>
                    {item.badge !== undefined && (
                      <span
                        className="flex-shrink-0 px-2 py-0.5 text-xs font-semibold rounded-full bg-primary text-primary-foreground"
                        aria-label={`${item.badge} items`}
                      >
                        {item.badge}
                      </span>
                    )}
                  </Link>
                </li>
              )
            })}
          </ul>
        </nav>
      </aside>
    )
  },
)

Sidebar.displayName = 'Sidebar'

