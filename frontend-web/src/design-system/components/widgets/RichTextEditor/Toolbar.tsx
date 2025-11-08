import { Bold, Italic, Underline, List, ListOrdered, Quote, Undo2, Redo2 } from 'lucide-react'
import { Button } from '@/design-system/components/primitives/Button'
import type { RichTextEditorToolbarProps } from './RichTextEditor.types'

const toolbarButtons: Array<{ icon: JSX.Element; label: string; command: string; value?: string }> = [
  { icon: <Bold className="h-4 w-4" />, label: 'Bold', command: 'bold' },
  { icon: <Italic className="h-4 w-4" />, label: 'Italic', command: 'italic' },
  { icon: <Underline className="h-4 w-4" />, label: 'Underline', command: 'underline' },
  { icon: <List className="h-4 w-4" />, label: 'Bulleted list', command: 'insertUnorderedList' },
  { icon: <ListOrdered className="h-4 w-4" />, label: 'Numbered list', command: 'insertOrderedList' },
  { icon: <Quote className="h-4 w-4" />, label: 'Quote', command: 'formatBlock', value: 'blockquote' },
]

const historyButtons: Array<{ icon: JSX.Element; label: string; command: string }> = [
  { icon: <Undo2 className="h-4 w-4" />, label: 'Undo', command: 'undo' },
  { icon: <Redo2 className="h-4 w-4" />, label: 'Redo', command: 'redo' },
]

export const RichTextEditorToolbar = ({ disabled, onCommand }: RichTextEditorToolbarProps): JSX.Element => {
  return (
    <div className="flex items-center justify-between gap-2 rounded-t-lg border border-border bg-muted/40 px-2 py-2">
      <div className="flex items-center gap-1">
        {toolbarButtons.map(button => (
          <Button
            key={button.label}
            type="button"
            size="sm"
            variant="ghost"
            className="h-8 w-8 p-0"
            onClick={() => onCommand(button.command, button.value)}
            disabled={disabled}
            aria-label={button.label}
          >
            {button.icon}
          </Button>
        ))}
      </div>
      <div className="flex items-center gap-1">
        {historyButtons.map(button => (
          <Button
            key={button.label}
            type="button"
            size="sm"
            variant="ghost"
            className="h-8 w-8 p-0"
            onClick={() => onCommand(button.command)}
            disabled={disabled}
            aria-label={button.label}
          >
            {button.icon}
          </Button>
        ))}
      </div>
    </div>
  )
}

export default RichTextEditorToolbar
