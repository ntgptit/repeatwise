/**
 * File Upload Component
 * 
 * Drag & drop file upload with validation
 */

import * as React from 'react'
import { cn } from '@/lib/utils'
import { Button } from '@/components/ui/button'
import { Upload, File, X } from 'lucide-react'

interface FileUploadProps {
  accept?: string
  maxSize?: number // in bytes
  maxFiles?: number
  onFilesSelected: (files: File[]) => void
  onError?: (error: string) => void
  className?: string
  disabled?: boolean
}

export function FileUpload({
  accept,
  maxSize = 50 * 1024 * 1024, // 50MB default
  maxFiles = 1,
  onFilesSelected,
  onError,
  className,
  disabled,
}: FileUploadProps) {
  const [files, setFiles] = React.useState<File[]>([])
  const [isDragging, setIsDragging] = React.useState(false)
  const fileInputRef = React.useRef<HTMLInputElement>(null)

  const validateFile = (file: File): string | null => {
    if (maxSize && file.size > maxSize) {
      return `File size exceeds ${Math.round(maxSize / 1024 / 1024)}MB limit`
    }
    if (accept) {
      const acceptedTypes = accept.split(',').map((type) => type.trim())
      const fileExtension = '.' + file.name.split('.').pop()?.toLowerCase()
      const fileType = file.type

      const isAccepted = acceptedTypes.some((type) => {
        if (type.startsWith('.')) {
          return type === fileExtension
        }
        return fileType.match(type.replace('*', '.*'))
      })

      if (!isAccepted) {
        return `File type not supported. Accepted: ${accept}`
      }
    }
    return null
  }

  const handleFiles = (fileList: FileList | null) => {
    if (!fileList || fileList.length === 0) return

    const newFiles = Array.from(fileList)
    const errors: string[] = []

    // Validate files
    newFiles.forEach((file) => {
      const error = validateFile(file)
      if (error) {
        errors.push(`${file.name}: ${error}`)
      }
    })

    if (errors.length > 0) {
      onError?.(errors.join('\n'))
      return
    }

    // Check max files limit
    const totalFiles = files.length + newFiles.length
    if (maxFiles && totalFiles > maxFiles) {
      onError?.(`Maximum ${maxFiles} file(s) allowed`)
      return
    }

    const updatedFiles = [...files, ...newFiles]
    setFiles(updatedFiles)
    onFilesSelected(updatedFiles)
  }

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(false)
    if (disabled) return

    handleFiles(e.dataTransfer.files)
  }

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault()
    if (!disabled) {
      setIsDragging(true)
    }
  }

  const handleDragLeave = () => {
    setIsDragging(false)
  }

  const handleFileInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    handleFiles(e.target.files)
  }

  const handleRemoveFile = (index: number) => {
    const updatedFiles = files.filter((_, i) => i !== index)
    setFiles(updatedFiles)
    onFilesSelected(updatedFiles)
  }

  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes'
    const k = 1024
    const sizes = ['Bytes', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
  }

  return (
    <div className={cn('space-y-4', className)}>
      <div
        onDrop={handleDrop}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        className={cn(
          'border-2 border-dashed rounded-lg p-8 text-center transition-colors',
          isDragging && !disabled
            ? 'border-primary bg-primary/5'
            : 'border-muted-foreground/25',
          disabled && 'opacity-50 cursor-not-allowed',
        )}
      >
        <Upload className="mx-auto h-12 w-12 text-muted-foreground mb-4" />
        <div className="space-y-2">
          <p className="text-sm font-medium">
            Drag & drop files here, or{' '}
            <button
              type="button"
              onClick={() => fileInputRef.current?.click()}
              disabled={disabled}
              className="text-primary underline-offset-4 hover:underline"
            >
              browse
            </button>
          </p>
          <p className="text-xs text-muted-foreground">
            {accept && `Accepted: ${accept}`}
            {maxSize && ` • Max size: ${Math.round(maxSize / 1024 / 1024)}MB`}
            {maxFiles && maxFiles > 1 && ` • Max files: ${maxFiles}`}
          </p>
        </div>
        <input
          ref={fileInputRef}
          type="file"
          accept={accept}
          multiple={maxFiles ? maxFiles > 1 : false}
          onChange={handleFileInputChange}
          className="hidden"
          disabled={disabled}
        />
      </div>

      {files.length > 0 && (
        <div className="space-y-2">
          <p className="text-sm font-medium">Selected files:</p>
          <div className="space-y-2">
            {files.map((file, index) => (
              <div
                key={index}
                className="flex items-center justify-between p-3 border rounded-lg"
              >
                <div className="flex items-center gap-3">
                  <File className="h-5 w-5 text-muted-foreground" />
                  <div>
                    <p className="text-sm font-medium">{file.name}</p>
                    <p className="text-xs text-muted-foreground">
                      {formatFileSize(file.size)}
                    </p>
                  </div>
                </div>
                <Button
                  variant="ghost"
                  size="icon"
                  onClick={() => handleRemoveFile(index)}
                  disabled={disabled}
                >
                  <X className="h-4 w-4" />
                  <span className="sr-only">Remove file</span>
                </Button>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
