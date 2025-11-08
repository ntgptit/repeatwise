import { type JSX } from 'react'
import * as Dialog from '@radix-ui/react-dialog'
import { X } from 'lucide-react'
import clsx from 'clsx'
import type { ModalHeaderProps } from './Modal.types'

export const ModalHeader = ({ title, description, withClose = true }: ModalHeaderProps): JSX.Element => {
  return (
    <header className="flex items-start justify-between gap-4">
      <div className="space-y-1">
        {title ? (
          <Dialog.Title className="text-lg font-semibold text-foreground">{title}</Dialog.Title>
        ) : null}
        {description ? (
          <Dialog.Description className="text-sm text-muted-foreground">
            {description}
          </Dialog.Description>
        ) : null}
      </div>
      {withClose ? (
        <Dialog.Close asChild>
          <button
            type="button"
            className={clsx(
              'inline-flex h-8 w-8 items-center justify-center rounded-full text-muted-foreground transition-colors hover:bg-muted focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2'
            )}
            aria-label="Close"
          >
            <X className="h-4 w-4" />
          </button>
        </Dialog.Close>
      ) : null}
    </header>
  )
}

export default ModalHeader
