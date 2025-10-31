/**
 * Modal Components
 * 
 * Confirmation dialogs and multi-step wizards
 */

import * as React from 'react'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'

interface ConfirmationDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  title: string
  description?: string
  confirmLabel?: string
  cancelLabel?: string
  variant?: 'default' | 'destructive'
  onConfirm: () => void
  onCancel?: () => void
}

export function ConfirmationDialog({
  open,
  onOpenChange,
  title,
  description,
  confirmLabel = 'Confirm',
  cancelLabel = 'Cancel',
  variant = 'default',
  onConfirm,
  onCancel,
}: ConfirmationDialogProps) {
  const handleConfirm = () => {
    onConfirm()
    onOpenChange(false)
  }

  const handleCancel = () => {
    onCancel?.()
    onOpenChange(false)
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{title}</DialogTitle>
          {description && <DialogDescription>{description}</DialogDescription>}
        </DialogHeader>
        <DialogFooter>
          <Button variant="outline" onClick={handleCancel}>
            {cancelLabel}
          </Button>
          <Button
            variant={variant === 'destructive' ? 'destructive' : 'default'}
            onClick={handleConfirm}
          >
            {confirmLabel}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}

interface MultiStepWizardProps {
  steps: Array<{ title: string; description?: string; content: React.ReactNode }>
  currentStep: number
  onStepChange: (step: number) => void
  onComplete?: () => void
  onCancel?: () => void
  nextLabel?: string
  previousLabel?: string
  finishLabel?: string
  cancelLabel?: string
  className?: string
}

export function MultiStepWizard({
  steps,
  currentStep,
  onStepChange,
  onComplete,
  onCancel,
  nextLabel = 'Next',
  previousLabel = 'Previous',
  finishLabel = 'Finish',
  cancelLabel = 'Cancel',
  className,
}: MultiStepWizardProps) {
  const isFirstStep = currentStep === 0
  const isLastStep = currentStep === steps.length - 1

  const handleNext = () => {
    if (isLastStep) {
      onComplete?.()
    } else {
      onStepChange(currentStep + 1)
    }
  }

  const handlePrevious = () => {
    if (!isFirstStep) {
      onStepChange(currentStep - 1)
    }
  }

  const currentStepData = steps[currentStep]

  return (
    <div className={cn('space-y-6', className)}>
      {/* Step indicator */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="text-sm font-medium">
            Step {currentStep + 1} of {steps.length}
          </span>
        </div>
        <div className="flex gap-1">
          {steps.map((_, index) => (
            <div
              key={index}
              className={cn(
                'h-2 w-2 rounded-full',
                index === currentStep
                  ? 'bg-primary'
                  : index < currentStep
                    ? 'bg-primary/50'
                    : 'bg-muted',
              )}
            />
          ))}
        </div>
      </div>

      {/* Step content */}
      <div className="space-y-4">
        <div>
          <h3 className="text-lg font-semibold">{currentStepData.title}</h3>
          {currentStepData.description && (
            <p className="text-sm text-muted-foreground mt-1">
              {currentStepData.description}
            </p>
          )}
        </div>
        <div>{currentStepData.content}</div>
      </div>

      {/* Navigation buttons */}
      <div className="flex items-center justify-between pt-4 border-t">
        <Button variant="outline" onClick={handlePrevious} disabled={isFirstStep}>
          {previousLabel}
        </Button>
        <div className="flex gap-2">
          {onCancel && (
            <Button variant="ghost" onClick={onCancel}>
              {cancelLabel}
            </Button>
          )}
          <Button onClick={handleNext}>
            {isLastStep ? finishLabel : nextLabel}
          </Button>
        </div>
      </div>
    </div>
  )
}
