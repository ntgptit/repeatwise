export interface RichTextEditorProps {
  value?: string
  defaultValue?: string
  onChange?: (value: string) => void
  placeholder?: string
  minHeight?: number | string
  disabled?: boolean
  toolbar?: boolean
  className?: string
  label?: string
  helperText?: string
  error?: string
}

export interface RichTextEditorToolbarProps {
  disabled?: boolean
  onCommand: (command: string, value?: string) => void
}

import type { RefObject } from 'react'

export interface RichTextEditorEditorProps {
  value: string
  placeholder?: string
  minHeight?: number | string
  disabled?: boolean
  onInput: (value: string) => void
  editorRef: RefObject<HTMLDivElement>
}
