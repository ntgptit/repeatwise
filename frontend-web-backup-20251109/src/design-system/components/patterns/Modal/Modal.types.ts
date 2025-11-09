import type { ReactNode } from 'react'
import type * as Dialog from '@radix-ui/react-dialog'

export interface ModalProps extends Dialog.DialogProps {
  trigger?: ReactNode
  children: ReactNode
  className?: string
  overlayClassName?: string
  contentClassName?: string
}

export interface ModalHeaderProps {
  title?: ReactNode
  description?: ReactNode
  withClose?: boolean
}

export interface ModalBodyProps {
  children: ReactNode
  className?: string
}

export interface ModalFooterProps {
  children: ReactNode
  align?: 'left' | 'center' | 'right' | 'between'
  className?: string
}
