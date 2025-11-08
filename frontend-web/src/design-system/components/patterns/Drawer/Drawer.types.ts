import type { ReactNode } from 'react'
import type * as Dialog from '@radix-ui/react-dialog'

export interface DrawerProps extends Dialog.DialogProps {
  trigger?: ReactNode
  side?: 'left' | 'right'
  className?: string
  overlayClassName?: string
  contentClassName?: string
  children: ReactNode
}
