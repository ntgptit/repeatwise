import { useEffect, useState } from 'react'
import clsx from 'clsx'
import type { RichTextEditorEditorProps } from './RichTextEditor.types'

const sanitizeValue = (value: string): string => {
  if (!value) {
    return ''
  }
  return value.replace(/<script[^>]*>([\S\s]*?)<\/script>/gim, '')
}

export const RichTextEditorContent = ({
  value,
  placeholder,
  minHeight = 160,
  disabled,
  onInput,
  editorRef,
}: RichTextEditorEditorProps): JSX.Element => {
  const [isFocused, setFocused] = useState(false)

  useEffect(() => {
    const element = editorRef.current
    if (!element) {
      return
    }

    const sanitized = sanitizeValue(value)
    if (sanitized !== element.innerHTML) {
      element.innerHTML = sanitized
    }
  }, [value, editorRef])

  const handleInput = () => {
    const element = editorRef.current
    if (!element) {
      return
    }
    onInput(element.innerHTML)
  }

  return (
    <div className="relative">
      {!value && !isFocused && placeholder ? (
        <span className="pointer-events-none absolute left-3 top-3 text-sm text-muted-foreground">
          {placeholder}
        </span>
      ) : null}
      <div
        ref={editorRef}
        className={clsx(
          'prose prose-sm max-w-none rounded-b-lg border border-t-0 border-border bg-background px-3 py-3 outline-none focus-within:ring-2 focus-within:ring-primary',
          disabled && 'pointer-events-none opacity-60'
        )}
        style={{ minHeight: typeof minHeight === 'number' ? `${minHeight}px` : minHeight }}
        contentEditable={!disabled}
        role="textbox"
        aria-multiline="true"
        onInput={handleInput}
        onFocus={() => setFocused(true)}
        onBlur={() => setFocused(false)}
        suppressContentEditableWarning
      />
    </div>
  )
}

export default RichTextEditorContent
