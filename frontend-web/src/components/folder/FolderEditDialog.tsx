/**
 * Folder Edit Dialog Component
 * 
 * Dialog for editing an existing folder
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

const folderSchema = z.object({
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

export type FolderFormData = z.infer<typeof folderSchema>

export interface FolderEditDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  initialData: FolderFormData
  onSubmit: (data: FolderFormData) => void | Promise<void>
  isLoading?: boolean
}

export const FolderEditDialog = React.memo<FolderEditDialogProps>(
  ({ open, onOpenChange, initialData, onSubmit, isLoading = false }) => {
    const {
      register,
      handleSubmit,
      watch,
      reset,
      formState: { errors, isSubmitting },
    } = useForm<FolderFormData>({
      resolver: zodResolver(folderSchema),
      defaultValues: initialData,
    })

    const nameValue = watch('name')
    const descriptionValue = watch('description')

    React.useEffect(() => {
      if (open) {
        reset(initialData)
      }
    }, [open, initialData, reset])

    const handleFormSubmit = async (data: FolderFormData) => {
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
            <DialogTitle>Edit Folder</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
            <FormInput
              label="Name"
              required
              error={errors.name?.message}
              placeholder="Enter folder name..."
              maxLength={100}
              {...register('name')}
              disabled={isLoading || isSubmitting}
              aria-label="Folder name"
            />
            <CharacterCounter current={nameValue?.length || 0} max={100} />

            <FormTextarea
              label="Description"
              error={errors.description?.message}
              placeholder="Enter folder description (optional)..."
              rows={3}
              maxLength={500}
              {...register('description')}
              disabled={isLoading || isSubmitting}
              aria-label="Folder description"
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

FolderEditDialog.displayName = 'FolderEditDialog'

