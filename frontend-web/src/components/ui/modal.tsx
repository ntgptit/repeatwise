/**
 * Modal Component
 * 
 * Wrapper around Dialog for common modal patterns
 * 
 * Features:
 * - Open/close control via props
 * - Overlay + animation
 * - onClose and onConfirm callbacks
 * - No business logic inside
 */

import * as React from 'react'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'

export interface ModalProps {
  /** Controls whether modal is open */
  isOpen: boolean
  /** Called when modal should close */
  onClose: () => void
  /** Modal title */
  title?: string
  /** Modal description */
  description?: string
  /** Modal content */
  children?: React.ReactNode
  /** Show confirm button */
  showConfirm?: boolean
  /** Confirm button label */
  confirmLabel?: string
  /** Confirm button variant */
  confirmVariant?: 'default' | 'destructive' | 'outline' | 'secondary' | 'ghost' | 'link'
  /** Called when confirm button is clicked */
  onConfirm?: () => void | Promise<void>
  /** Show cancel button */
  showCancel?: boolean
  /** Cancel button label */
  cancelLabel?: string
  /** Is confirm action loading */
  isLoading?: boolean
  /** Is confirm action disabled */
  isConfirmDisabled?: boolean
  /** Custom footer content */
  footer?: React.ReactNode
  /** Max width of modal */
  maxWidth?: 'sm' | 'md' | 'lg' | 'xl' | '2xl'
  /** Additional className */
  className?: string
}

const maxWidthClasses = {
  sm: 'max-w-sm',
  md: 'max-w-md',
  lg: 'max-w-lg',
  xl: 'max-w-xl',
  '2xl': 'max-w-2xl',
}

export const Modal = React.memo<ModalProps>(
  ({
    isOpen,
    onClose,
    title,
    description,
    children,
    showConfirm = false,
    confirmLabel = 'Confirm',
    confirmVariant = 'default',
    onConfirm,
    showCancel = true,
    cancelLabel = 'Cancel',
    isLoading = false,
    isConfirmDisabled = false,
    footer,
    maxWidth = 'lg',
    className,
  }) => {
    const handleConfirm = React.useCallback(async () => {
      if (onConfirm) {
        await onConfirm()
      }
    }, [onConfirm])

    return (
      <Dialog open={isOpen} onOpenChange={onClose}>
        <DialogContent className={cn(maxWidthClasses[maxWidth], className)}>
          {(title || description) && (
            <DialogHeader>
              {title && <DialogTitle>{title}</DialogTitle>}
              {description && (
                <DialogDescription>{description}</DialogDescription>
              )}
            </DialogHeader>
          )}
          {children && <div className="py-4">{children}</div>}
          {(showConfirm || showCancel || footer) && (
            <DialogFooter>
              {footer || (
                <>
                  {showCancel && (
                    <Button
                      variant="outline"
                      onClick={onClose}
                      disabled={isLoading}
                    >
                      {cancelLabel}
                    </Button>
                  )}
                  {showConfirm && (
                    <Button
                      variant={confirmVariant}
                      onClick={handleConfirm}
                      isLoading={isLoading}
                      disabled={isConfirmDisabled || isLoading}
                    >
                      {confirmLabel}
                    </Button>
                  )}
                </>
              )}
            </DialogFooter>
          )}
        </DialogContent>
      </Dialog>
    )
  },
)

Modal.displayName = 'Modal'

export { Modal }

