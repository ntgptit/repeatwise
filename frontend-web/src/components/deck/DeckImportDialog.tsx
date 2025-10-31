/**
 * Deck Import Dialog Component
 * 
 * Dialog for importing cards into a deck
 * 
 * Features:
 * - File upload
 * - CSV/Excel import
 * - Preview imported cards
 */

import * as React from 'react'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { FileUpload } from '@/components/common/FileUpload'

export interface DeckImportDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onImport: (file: File) => void | Promise<void>
  isLoading?: boolean
  accept?: string
}

export const DeckImportDialog = React.memo<DeckImportDialogProps>(
  ({
    open,
    onOpenChange,
    onImport,
    isLoading = false,
    accept = '.csv,.xlsx',
  }) => {
    const handleFilesSelected = async (files: File[]) => {
      if (files.length > 0) {
        await onImport(files[0])
        onOpenChange(false)
      }
    }

    return (
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Import Cards</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <p className="text-sm text-muted-foreground">
              Upload a CSV or Excel file to import cards into this deck. The
              file should have columns: front, back
            </p>
            <FileUpload
              accept={accept}
              maxFiles={1}
              onFilesSelected={handleFilesSelected}
              disabled={isLoading}
            />
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={isLoading}
            >
              Cancel
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    )
  },
)

DeckImportDialog.displayName = 'DeckImportDialog'

