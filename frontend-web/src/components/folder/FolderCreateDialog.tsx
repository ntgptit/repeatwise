/**
 * Folder Create Dialog Component
 * 
 * Dialog for creating a new folder
 * 
 * Features:
 * - Name and description inputs
 * - Parent folder selection (optional)
 * - Validation
 * - Max depth check
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

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
  parentId: z.string().nullable().optional(),
})

export type FolderFormData = z.infer<typeof folderSchema>

export interface FolderOption {
  id: string
  name: string
  depth: number
}

export interface FolderCreateDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onSubmit: (data: FolderFormData) => void | Promise<void>
  parentOptions?: FolderOption[]
  maxDepth?: number
  isLoading?: boolean
}

export const FolderCreateDialog = React.memo<FolderCreateDialogProps>(
  ({
    open,
    onOpenChange,
    onSubmit,
    parentOptions = [],
    maxDepth = 10,
    isLoading = false,
  }) => {
    const {
      register,
      handleSubmit,
      watch,
      setValue,
      reset,
      formState: { errors, isSubmitting },
    } = useForm<FolderFormData>({
      resolver: zodResolver(folderSchema),
      defaultValues: {
        name: '',
        description: '',
        parentId: null,
      },
    })

    const nameValue = watch('name')
    const descriptionValue = watch('description')
    const parentIdValue = watch('parentId')

    // Filter parent options based on max depth
    const availableParents = parentOptions.filter(
      (option) => option.depth < maxDepth - 1,
    )

    const handleFormSubmit = async (data: FolderFormData) => {
      await onSubmit(data)
      reset()
      onOpenChange(false)
    }

    const handleCancel = () => {
      reset()
      onOpenChange(false)
    }

    return (
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Create New Folder</DialogTitle>
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

            {availableParents.length > 0 && (
              <div className="space-y-2">
                <label className="text-sm font-medium">Parent Folder</label>
                <Select
                  value={parentIdValue || 'root'}
                  onValueChange={(value) =>
                    setValue('parentId', value === 'root' ? null : value)
                  }
                  disabled={isLoading || isSubmitting}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select parent folder (optional)" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="root">Root (No parent)</SelectItem>
                    {availableParents.map((parent) => (
                      <SelectItem key={parent.id} value={parent.id}>
                        {'  '.repeat(parent.depth)}
                        {parent.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            )}

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
                {isSubmitting ? 'Creating...' : 'Create Folder'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    )
  },
)

FolderCreateDialog.displayName = 'FolderCreateDialog'

