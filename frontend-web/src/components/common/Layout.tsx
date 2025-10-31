/**
 * Layout Components
 * 
 * Common layout wrappers for consistent page structure
 */

import * as React from 'react'
import { cn } from '@/lib/utils'

interface LayoutProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode
}

export function Layout({ children, className, ...props }: LayoutProps) {
  return (
    <div className={cn('min-h-screen bg-background', className)} {...props}>
      {children}
    </div>
  )
}

interface PageContainerProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode
  sidebar?: React.ReactNode
}

export function PageContainer({
  children,
  sidebar,
  className,
  ...props
}: PageContainerProps) {
  return (
    <div className={cn('flex h-screen overflow-hidden', className)} {...props}>
      {sidebar && (
        <aside className="hidden lg:flex lg:w-64 lg:flex-col lg:border-r">
          {sidebar}
        </aside>
      )}
      <main className="flex-1 overflow-y-auto">{children}</main>
    </div>
  )
}

interface SectionProps extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode
  title?: string
  description?: string
  actions?: React.ReactNode
}

export function Section({
  children,
  title,
  description,
  actions,
  className,
  ...props
}: SectionProps) {
  return (
    <section className={cn('space-y-4 p-6', className)} {...props}>
      {(title || description || actions) && (
        <div className="flex items-center justify-between">
          <div>
            {title && (
              <h2 className="text-2xl font-semibold tracking-tight">
                {title}
              </h2>
            )}
            {description && (
              <p className="text-sm text-muted-foreground mt-1">
                {description}
              </p>
            )}
          </div>
          {actions && <div className="flex items-center gap-2">{actions}</div>}
        </div>
      )}
      {children}
    </section>
  )
}
