/**
 * Deck Edit Dialog Component
 * 
 * Dialog for editing an existing deck
 * 
 * Features:
 * - Pre-filled form
 * - Name and description editing
 * - Validation
 */

import * as React from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog'
import { FormTextarea, FormInput } from '@/components/common/Form'
import { Button } from '@/components/ui/button'
import { CharacterCounter } from '@/components/common/Form'

const deckSchema = z.object({
  name: z
    .string()
    .min(1, 'Name is required')
    .max(100, 'Name must be 100 characters or less')
    .trim(),
  description: z
    .string()
    .max(500, 'Description must be 500 characters or less')
    .trim()
    .optional()
    .or(z.literal('')),
})

export type DeckFormData = z.infer<typeof deckSchema>

export interface DeckEditDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  initialData: DeckFormData
  onSubmit: (data: DeckFormData) => void | Promise<void>
  isLoading?: boolean
}

export const DeckEditDialog = React.memo<DeckEditDialogProps>(
  ({ open, onOpenChange, initialData, onSubmit, isLoading = false }) => {
    const {
      register,
      handleSubmit,
      watch,
      reset,
      formState: { errors, isSubmitting },
    } = useForm<DeckFormData>({
      resolver: zodResolver(deckSchema),
      defaultValues: initialData,
    })

    const nameValue = watch('name')
    const descriptionValue = watch('description')

    React.useEffect(() => {
      if (open) {
        reset(initialData)
      }
    }, [open, initialData, reset])

    const handleFormSubmit = async (data: DeckFormData) => {
      await onSubmit(data)
      onOpenChange(false)
    }

    const handleCancel = () => {
      reset(initialData)
      onOpenChange(false)
    }

    return (
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Edit Deck</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
            <FormInput
              label="Name"
              required
              error={errors.name?.message}
              placeholder="Enter deck name..."
              maxLength={100}
              {...register('name')}
              disabled={isLoading || isSubmitting}
              aria-label="Deck name"
            />
            <CharacterCounter
              current={nameValue?.length || 0}
              max={100}
            />

            <FormTextarea
              label="Description"
              error={errors.description?.message}
              placeholder="Enter deck description (optional)..."
              rows={3}
              maxLength={500}
              {...register('description')}
              disabled={isLoading || isSubmitting}
              aria-label="Deck description"
            />
            <CharacterCounter
              current={descriptionValue?.length || 0}
              max={500}
            />

            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={handleCancel}
                disabled={isLoading || isSubmitting}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={isLoading || isSubmitting}>
                {isSubmitting ? 'Saving...' : 'Save Changes'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    )
  },
)

DeckEditDialog.displayName = 'DeckEditDialog'

