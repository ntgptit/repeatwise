/**
 * Card Editor Component
 * 
 * Form component for creating and editing cards
 * 
 * Features:
 * - Front and back text inputs
 * - Character counter (max 5000 chars)
 * - Validation
 * - Keyboard shortcuts (Ctrl+Enter to submit)
 */

import * as React from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { FormTextarea, CharacterCounter } from '@/components/common/Form'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { cn } from '@/lib/utils'

const cardSchema = z.object({
  front: z
    .string()
    .min(1, 'Front text is required')
    .max(5000, 'Front text must be 5000 characters or less')
    .trim(),
  back: z
    .string()
    .min(1, 'Back text is required')
    .max(5000, 'Back text must be 5000 characters or less')
    .trim(),
})

export type CardFormData = z.infer<typeof cardSchema>

export interface CardEditorProps {
  initialData?: Partial<CardFormData>
  onSubmit: (data: CardFormData) => void | Promise<void>
  onCancel?: () => void
  isLoading?: boolean
  submitLabel?: string
  cancelLabel?: string
  className?: string
  title?: string
}

export const CardEditor = React.memo<CardEditorProps>(
  ({
    initialData,
    onSubmit,
    onCancel,
    isLoading = false,
    submitLabel = 'Save',
    cancelLabel = 'Cancel',
    className,
    title = 'Edit Card',
  }) => {
    const {
      register,
      handleSubmit,
      watch,
      formState: { errors, isSubmitting },
    } = useForm<CardFormData>({
      resolver: zodResolver(cardSchema),
      defaultValues: {
        front: initialData?.front || '',
        back: initialData?.back || '',
      },
    })

    const frontValue = watch('front')
    const backValue = watch('back')

    const handleFormSubmit = async (data: CardFormData) => {
      await onSubmit(data)
    }

    const handleKeyDown = (e: React.KeyboardEvent) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
        e.preventDefault()
        handleSubmit(handleFormSubmit)()
      }
    }

    return (
      <Card className={cn('w-full', className)}>
        <CardHeader>
          <CardTitle>{title}</CardTitle>
        </CardHeader>
        <CardContent>
          <form
            onSubmit={handleSubmit(handleFormSubmit)}
            className="space-y-4"
            onKeyDown={handleKeyDown}
          >
            <FormTextarea
              label="Front"
              required
              error={errors.front?.message}
              placeholder="Enter the question or front side of the card..."
              rows={4}
              maxLength={5000}
              {...register('front')}
              disabled={isLoading || isSubmitting}
              aria-label="Card front text"
            />
            <CharacterCounter
              current={frontValue?.length || 0}
              max={5000}
            />

            <FormTextarea
              label="Back"
              required
              error={errors.back?.message}
              placeholder="Enter the answer or back side of the card..."
              rows={4}
              maxLength={5000}
              {...register('back')}
              disabled={isLoading || isSubmitting}
              aria-label="Card back text"
            />
            <CharacterCounter
              current={backValue?.length || 0}
              max={5000}
            />

            <div className="flex justify-end gap-2 pt-4">
              {onCancel && (
                <Button
                  type="button"
                  variant="outline"
                  onClick={onCancel}
                  disabled={isLoading || isSubmitting}
                >
                  {cancelLabel}
                </Button>
              )}
              <Button
                type="submit"
                disabled={isLoading || isSubmitting}
                aria-label={submitLabel}
              >
                {isSubmitting ? 'Saving...' : submitLabel}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    )
  },
)

CardEditor.displayName = 'CardEditor'

