import { type JSX } from 'react'
import * as Dialog from '@radix-ui/react-dialog'
import clsx from 'clsx'
import type { ModalProps } from './Modal.types'

export const Modal = ({
  trigger,
  children,
  className,
  overlayClassName,
  contentClassName,
  ...props
}: ModalProps): JSX.Element => {
  return (
    <Dialog.Root {...props}>
      {trigger ? <Dialog.Trigger asChild>{trigger}</Dialog.Trigger> : null}
      <Dialog.Portal>
        <Dialog.Overlay
          className={clsx('fixed inset-0 bg-background/70 backdrop-blur-sm', overlayClassName)}
        />
        <Dialog.Content
          className={clsx(
            'fixed left-1/2 top-1/2 w-full max-w-lg -translate-x-1/2 -translate-y-1/2 rounded-2xl border border-border bg-background p-6 shadow-lg focus:outline-none',
            contentClassName
          )}
        >
          <div className={clsx('flex flex-col gap-4', className)}>{children}</div>
        </Dialog.Content>
      </Dialog.Portal>
    </Dialog.Root>
  )
}

export default Modal
