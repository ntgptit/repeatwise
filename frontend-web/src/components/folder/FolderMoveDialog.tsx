/**
 * Folder Move Dialog Component
 * 
 * Dialog for moving a folder to a different parent
 * 
 * Features:
 * - Select new parent folder
 * - Prevent circular references
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
  DialogDescription,
  DialogFooter,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { AlertTriangle } from 'lucide-react'

const moveFolderSchema = z.object({
  newParentId: z.string().nullable(),
})

export type MoveFolderFormData = z.infer<typeof moveFolderSchema>

export interface FolderOption {
  id: string
  name: string
  depth: number
  path: string
}

export interface FolderMoveDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  folderId: string
  folderName: string
  currentParentId?: string | null
  parentOptions: FolderOption[]
  onSubmit: (data: MoveFolderFormData) => void | Promise<void>
  isLoading?: boolean
}

export const FolderMoveDialog = React.memo<FolderMoveDialogProps>(
  ({
    open,
    onOpenChange,
    folderId,
    folderName,
    currentParentId,
    parentOptions,
    onSubmit,
    isLoading = false,
  }) => {
    const {
      handleSubmit,
      setValue,
      watch,
      reset,
      formState: { errors, isSubmitting },
    } = useForm<MoveFolderFormData>({
      resolver: zodResolver(moveFolderSchema),
      defaultValues: {
        newParentId: currentParentId || null,
      },
    })

    const newParentIdValue = watch('newParentId')

    // Filter out current folder and its descendants to prevent circular references
    const folderPath = parentOptions.find((f) => f.id === folderId)?.path || ''
    const availableParents = parentOptions.filter(
      (option) =>
        option.id !== folderId &&
        !option.path.startsWith(folderPath + '/') &&
        option.id !== currentParentId,
    )

    React.useEffect(() => {
      if (open) {
        setValue('newParentId', currentParentId || null)
      }
    }, [open, currentParentId, setValue])

    const handleFormSubmit = async (data: MoveFolderFormData) => {
      await onSubmit(data)
      onOpenChange(false)
    }

    const handleCancel = () => {
      reset()
      onOpenChange(false)
    }

    const isSameParent = newParentIdValue === currentParentId

    return (
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Move Folder</DialogTitle>
            <DialogDescription>
              Move "{folderName}" to a different location
            </DialogDescription>
          </DialogHeader>
          <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
            <div className="space-y-2">
              <label className="text-sm font-medium">New Parent Folder</label>
              <Select
                value={newParentIdValue || 'root'}
                onValueChange={(value) =>
                  setValue('newParentId', value === 'root' ? null : value)
                }
                disabled={isLoading || isSubmitting}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select parent folder" />
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
              {errors.newParentId && (
                <p className="text-sm text-destructive">
                  {errors.newParentId.message}
                </p>
              )}
            </div>

            {isSameParent && (
              <Alert>
                <AlertTriangle className="h-4 w-4" />
                <AlertDescription>
                  Folder is already in this location
                </AlertDescription>
              </Alert>
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
              <Button
                type="submit"
                disabled={isLoading || isSubmitting || isSameParent}
              >
                {isSubmitting ? 'Moving...' : 'Move Folder'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    )
  },
)

FolderMoveDialog.displayName = 'FolderMoveDialog'

