import { useMemo, useRef, useState } from 'react'
import clsx from 'clsx'
import type { RichTextEditorProps } from './RichTextEditor.types'
import { RichTextEditorToolbar } from './Toolbar'
import { RichTextEditorContent } from './Editor'

const sanitizeDefault = (value?: string): string => value ?? ''

export const RichTextEditor = ({
  value,
  defaultValue,
  onChange,
  placeholder,
  minHeight,
  disabled,
  toolbar = true,
  className,
  label,
  helperText,
  error,
}: RichTextEditorProps): JSX.Element => {
  const isControlled = value !== undefined
  const [internalValue, setInternalValue] = useState(() => sanitizeDefault(defaultValue))
  const editorRef = useRef<HTMLDivElement>(null)

  const contentValue = useMemo(() => sanitizeDefault(isControlled ? value : internalValue), [isControlled, value, internalValue])

  const setValue = (next: string) => {
    if (!isControlled) {
      setInternalValue(next)
    }
    onChange?.(next)
  }

  const handleCommand = (command: string, commandValue?: string) => {
    if (disabled) {
      return
    }

    const editableElement = editorRef.current
    const doc = typeof document !== 'undefined' ? document : undefined
    if (!editableElement || !doc) {
      return
    }

    editableElement.focus()

    if (command === 'createLink') {
      const url = globalThis.prompt?.('Enter URL')
      if (!url) {
        return
      }
      doc.execCommand('createLink', false, url)
      setValue(editableElement.innerHTML)
      return
    }

    doc.execCommand(command, false, commandValue ?? '')
    setValue(editableElement.innerHTML)
  }

  return (
    <div className={clsx('flex flex-col gap-2', className)}>
      {label ? <span className="text-sm font-medium text-foreground">{label}</span> : null}
      <div className={clsx('overflow-hidden rounded-lg border border-border', disabled && 'opacity-70')}>
        {toolbar ? <RichTextEditorToolbar disabled={disabled} onCommand={handleCommand} /> : null}
        <RichTextEditorContent
          value={contentValue}
          placeholder={placeholder}
          minHeight={minHeight}
          disabled={disabled}
          onInput={setValue}
          editorRef={editorRef}
        />
      </div>
      {helperText ? <span className="text-xs text-muted-foreground">{helperText}</span> : null}
      {error ? <span className="text-xs text-destructive">{error}</span> : null}
    </div>
  )
}

export default RichTextEditor
