import { type JSX } from 'react'
import * as Dialog from '@radix-ui/react-dialog'
import clsx from 'clsx'
import type { DrawerProps } from './Drawer.types'

export const Drawer = ({
  trigger,
  children,
  side = 'right',
  className,
  overlayClassName,
  contentClassName,
  ...props
}: DrawerProps): JSX.Element => {
  const sideClasses =
    side === 'right'
      ? 'right-0 translate-x-full data-[state=open]:translate-x-0'
      : 'left-0 -translate-x-full data-[state=open]:translate-x-0'

  return (
    <Dialog.Root {...props}>
      {trigger ? <Dialog.Trigger asChild>{trigger}</Dialog.Trigger> : null}
      <Dialog.Portal>
        <Dialog.Overlay
          className={clsx('fixed inset-0 bg-background/60 backdrop-blur-sm', overlayClassName)}
        />
        <Dialog.Content
          className={clsx(
            'fixed top-0 h-full w-full max-w-md border-l border-border bg-background p-6 shadow-xl transition-transform duration-300 ease-out data-[state=closed]:duration-200',
            sideClasses,
            contentClassName
          )}
        >
          <div className={clsx('flex h-full flex-col gap-4', className)}>{children}</div>
        </Dialog.Content>
      </Dialog.Portal>
    </Dialog.Root>
  )
}

export default Drawer
