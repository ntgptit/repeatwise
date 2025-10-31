/**
 * Breadcrumb Component
 * 
 * Navigation breadcrumb for hierarchical navigation
 * 
 * Features:
 * - Hierarchical navigation
 * - Clickable items
 * - Separator icons
 * - Responsive
 */

import * as React from 'react'
import { Link } from 'react-router-dom'
import { ChevronRight, Home } from 'lucide-react'
import { cn } from '@/lib/utils'

export interface BreadcrumbItem {
  /** Label to display */
  label: string
  /** Link path (optional for last item) */
  href?: string
  /** Icon to display */
  icon?: React.ReactNode
}

export interface BreadcrumbProps {
  /** Breadcrumb items */
  items: BreadcrumbItem[]
  /** Show home icon */
  showHome?: boolean
  /** Home href */
  homeHref?: string
  /** Additional className */
  className?: string
  /** Separator component */
  separator?: React.ReactNode
}

export const Breadcrumb = React.memo<BreadcrumbProps>(
  ({
    items,
    showHome = true,
    homeHref = '/',
    className,
    separator = <ChevronRight className="h-4 w-4 text-muted-foreground" />,
  }) => {
    if (items.length === 0 && !showHome) {
      return null
    }

    return (
      <nav
        className={cn('flex items-center space-x-1 text-sm', className)}
        aria-label="Breadcrumb"
      >
        <ol className="flex items-center space-x-1">
          {showHome && (
            <li>
              <Link
                to={homeHref}
                className="flex items-center text-muted-foreground hover:text-foreground transition-colors"
                aria-label="Home"
              >
                <Home className="h-4 w-4" />
                <span className="sr-only">Home</span>
              </Link>
            </li>
          )}
          {items.map((item, index) => {
            const isLast = index === items.length - 1

            return (
              <React.Fragment key={index}>
                {showHome || index > 0 ? (
                  <li className="flex items-center" aria-hidden="true">
                    {separator}
                  </li>
                ) : null}
                <li>
                  {isLast || !item.href ? (
                    <span
                      className={cn(
                        'flex items-center',
                        isLast
                          ? 'text-foreground font-medium'
                          : 'text-muted-foreground',
                      )}
                      aria-current={isLast ? 'page' : undefined}
                    >
                      {item.icon && (
                        <span className="mr-1" aria-hidden="true">
                          {item.icon}
                        </span>
                      )}
                      {item.label}
                    </span>
                  ) : (
                    <Link
                      to={item.href}
                      className="flex items-center text-muted-foreground hover:text-foreground transition-colors"
                    >
                      {item.icon && (
                        <span className="mr-1" aria-hidden="true">
                          {item.icon}
                        </span>
                      )}
                      {item.label}
                    </Link>
                  )}
                </li>
              </React.Fragment>
            )
          })}
        </ol>
      </nav>
    )
  },
)

Breadcrumb.displayName = 'Breadcrumb'

