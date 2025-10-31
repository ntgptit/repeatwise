/**
 * Action Components
 * 
 * Action button groups and menus
 */

import * as React from 'react'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { MoreHorizontal, Edit, Copy, Trash2, Move, Share } from 'lucide-react'
import { cn } from '@/lib/utils'

interface ActionButtonGroupProps {
  actions: Array<{
    label: string
    onClick: () => void
    variant?: 'default' | 'destructive' | 'outline' | 'secondary'
    icon?: React.ReactNode
  }>
  className?: string
}

export function ActionButtonGroup({
  actions,
  className,
}: ActionButtonGroupProps) {
  return (
    <div className={cn('flex items-center gap-2', className)}>
      {actions.map((action, index) => (
        <Button
          key={index}
          variant={action.variant || 'outline'}
          size="sm"
          onClick={action.onClick}
        >
          {action.icon && <span className="mr-2">{action.icon}</span>}
          {action.label}
        </Button>
      ))}
    </div>
  )
}

interface ActionMenuProps {
  actions: Array<{
    label: string
    onClick: () => void
    icon?: React.ReactNode
    variant?: 'default' | 'destructive'
    separator?: boolean
  }>
  trigger?: React.ReactNode
  className?: string
}

export function ActionMenu({
  actions,
  trigger,
  className,
}: ActionMenuProps) {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        {trigger || (
          <Button variant="ghost" size="icon" className={className}>
            <MoreHorizontal className="h-4 w-4" />
            <span className="sr-only">Open menu</span>
          </Button>
        )}
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        {actions.map((action, index) => (
          <React.Fragment key={index}>
            {action.separator && index > 0 && <DropdownMenuSeparator />}
            <DropdownMenuItem
              onClick={action.onClick}
              className={cn(
                action.variant === 'destructive' &&
                  'text-destructive focus:text-destructive',
              )}
            >
              {action.icon && <span className="mr-2">{action.icon}</span>}
              {action.label}
            </DropdownMenuItem>
          </React.Fragment>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  )
}

interface QuickActionsProps {
  actions: Array<{
    label: string
    onClick: () => void
    icon?: React.ReactNode
    variant?: 'default' | 'outline' | 'secondary'
  }>
  className?: string
}

export function QuickActions({ actions, className }: QuickActionsProps) {
  return (
    <div className={cn('grid grid-cols-2 gap-2', className)}>
      {actions.map((action, index) => (
        <Button
          key={index}
          variant={action.variant || 'outline'}
          onClick={action.onClick}
          className="justify-start"
        >
          {action.icon && <span className="mr-2">{action.icon}</span>}
          {action.label}
        </Button>
      ))}
    </div>
  )
}

// Common action items
export const commonActions = {
  edit: { label: 'Edit', icon: <Edit className="h-4 w-4" /> },
  copy: { label: 'Copy', icon: <Copy className="h-4 w-4" /> },
  move: { label: 'Move', icon: <Move className="h-4 w-4" /> },
  share: { label: 'Share', icon: <Share className="h-4 w-4" /> },
  delete: {
    label: 'Delete',
    icon: <Trash2 className="h-4 w-4" />,
    variant: 'destructive' as const,
  },
}
