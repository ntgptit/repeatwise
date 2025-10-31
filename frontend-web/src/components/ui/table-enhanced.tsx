/**
 * Table Component with Generic Support
 * 
 * Enhanced table component with column configuration and generic type support
 * 
 * Features:
 * - Generic type support <T>
 * - Column configuration via props
 * - Sort and pagination hooks (external)
 * - No rendering logic inside
 */

import * as React from 'react'
import {
  Table as BaseTable,
  TableHeader,
  TableBody,
  TableFooter,
  TableHead,
  TableRow,
  TableCell,
  TableCaption,
} from '@/components/ui/table'
import { cn } from '@/lib/utils'

export interface TableColumn<T> {
  /** Column key (unique identifier) */
  key: string
  /** Column header label */
  header: string | React.ReactNode
  /** Accessor function to get cell value */
  accessor?: (row: T) => React.ReactNode
  /** Cell renderer function */
  cell?: (row: T, index: number) => React.ReactNode
  /** Column alignment */
  align?: 'left' | 'center' | 'right'
  /** Column width */
  width?: string | number
  /** Is column sortable */
  sortable?: boolean
  /** Column className */
  className?: string
  /** Header className */
  headerClassName?: string
}

export interface TableProps<T> {
  /** Array of data rows */
  data: T[]
  /** Column configuration */
  columns: TableColumn<T>[]
  /** Table caption */
  caption?: string
  /** Row key extractor */
  getRowKey?: (row: T, index: number) => string | number
  /** Row click handler */
  onRowClick?: (row: T, index: number) => void
  /** Row className generator */
  getRowClassName?: (row: T, index: number) => string
  /** Empty state message */
  emptyMessage?: string
  /** Loading state */
  isLoading?: boolean
  /** Loading message */
  loadingMessage?: string
  /** Additional className */
  className?: string
}

export function Table<T extends Record<string, unknown>>({
  data,
  columns,
  caption,
  getRowKey = (_, index) => index,
  onRowClick,
  getRowClassName,
  emptyMessage = 'No data available',
  isLoading = false,
  loadingMessage = 'Loading...',
  className,
}: TableProps<T>) {
  if (isLoading) {
    return (
      <BaseTable className={className}>
        {caption && <TableCaption>{caption}</TableCaption>}
        <TableBody>
          <TableRow>
            <TableCell colSpan={columns.length} className="text-center py-8">
              {loadingMessage}
            </TableCell>
          </TableRow>
        </TableBody>
      </BaseTable>
    )
  }

  if (data.length === 0) {
    return (
      <BaseTable className={className}>
        {caption && <TableCaption>{caption}</TableCaption>}
        <TableBody>
          <TableRow>
            <TableCell colSpan={columns.length} className="text-center py-8 text-muted-foreground">
              {emptyMessage}
            </TableCell>
          </TableRow>
        </TableBody>
      </BaseTable>
    )
  }

  return (
    <BaseTable className={className}>
      {caption && <TableCaption>{caption}</TableCaption>}
      <TableHeader>
        <TableRow>
          {columns.map((column) => (
            <TableHead
              key={column.key}
              className={cn(
                column.align === 'right' && 'text-right',
                column.align === 'center' && 'text-center',
                column.headerClassName,
              )}
              style={column.width ? { width: column.width } : undefined}
            >
              {column.header}
            </TableHead>
          ))}
        </TableRow>
      </TableHeader>
      <TableBody>
        {data.map((row, rowIndex) => {
          const rowKey = getRowKey(row, rowIndex)
          const rowClassName = getRowClassName?.(row, rowIndex)

          return (
            <TableRow
              key={rowKey}
              className={cn(
                onRowClick && 'cursor-pointer',
                rowClassName,
              )}
              onClick={() => onRowClick?.(row, rowIndex)}
            >
              {columns.map((column) => {
                const cellContent = column.cell
                  ? column.cell(row, rowIndex)
                  : column.accessor
                    ? column.accessor(row)
                    : (row[column.key] as React.ReactNode)

                return (
                  <TableCell
                    key={column.key}
                    className={cn(
                      column.align === 'right' && 'text-right',
                      column.align === 'center' && 'text-center',
                      column.className,
                    )}
                  >
                    {cellContent}
                  </TableCell>
                )
              })}
            </TableRow>
          )
        })}
      </TableBody>
    </BaseTable>
  )
}

// Re-export base table components for advanced usage
export {
  BaseTable,
  TableHeader,
  TableBody,
  TableFooter,
  TableHead,
  TableRow,
  TableCell,
  TableCaption,
}

