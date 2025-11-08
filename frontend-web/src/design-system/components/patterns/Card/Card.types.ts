import type { HTMLAttributes, ReactNode } from 'react'

export interface CardProps extends HTMLAttributes<HTMLDivElement> {
  elevated?: boolean
  borderless?: boolean
}

export interface CardHeaderProps extends HTMLAttributes<HTMLDivElement> {
  title?: ReactNode
  description?: ReactNode
  actions?: ReactNode
}

export type CardBodyProps = HTMLAttributes<HTMLDivElement>

export interface CardFooterProps extends HTMLAttributes<HTMLDivElement> {
  align?: 'left' | 'center' | 'right' | 'between'
}
