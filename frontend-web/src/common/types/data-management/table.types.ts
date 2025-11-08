/**
 * Table Types
 *
 * Type definitions for data table components.
 *
 * @module common/types/data-management/table
 */

import type { ReactNode } from 'react';
import type { PaginationState, PaginationActions } from './pagination.types';
import type { SortState, SortActions } from './sort.types';
import type { FilterState, FilterActions } from './filter.types';
import type { SearchState, SearchActions } from './search.types';

/**
 * Table column definition
 */
export interface TableColumn<T = unknown> {
  /**
   * Unique column ID
   */
  id: string;

  /**
   * Column header text
   */
  header: ReactNode | ((column: TableColumn<T>) => ReactNode);

  /**
   * Accessor function or property key
   */
  accessor?: keyof T | ((row: T) => unknown);

  /**
   * Cell render function
   */
  cell?: (row: T, column: TableColumn<T>) => ReactNode;

  /**
   * Column width (CSS value)
   */
  width?: string | number;

  /**
   * Minimum column width
   */
  minWidth?: string | number;

  /**
   * Maximum column width
   */
  maxWidth?: string | number;

  /**
   * Whether column is sortable
   */
  sortable?: boolean;

  /**
   * Sort field name (if different from id)
   */
  sortField?: string;

  /**
   * Whether column is filterable
   */
  filterable?: boolean;

  /**
   * Whether column is resizable
   */
  resizable?: boolean;

  /**
   * Whether column is visible
   */
  visible?: boolean;

  /**
   * Whether column can be hidden by user
   */
  hideable?: boolean;

  /**
   * Column alignment
   */
  align?: 'left' | 'center' | 'right';

  /**
   * Whether column is frozen/sticky
   */
  sticky?: 'left' | 'right' | false;

  /**
   * Custom CSS class name
   */
  className?: string;

  /**
   * Custom header className
   */
  headerClassName?: string;

  /**
   * Custom cell className
   */
  cellClassName?: string | ((row: T) => string);

  /**
   * Footer content
   */
  footer?: ReactNode | ((column: TableColumn<T>) => ReactNode);

  /**
   * Additional metadata
   */
  meta?: Record<string, unknown>;
}

/**
 * Table row definition
 */
export interface TableRow<T = unknown> {
  /**
   * Unique row ID
   */
  id: string | number;

  /**
   * Row data
   */
  data: T;

  /**
   * Whether row is selected
   */
  selected?: boolean;

  /**
   * Whether row is expanded (for nested tables)
   */
  expanded?: boolean;

  /**
   * Whether row is disabled
   */
  disabled?: boolean;

  /**
   * Custom row className
   */
  className?: string;

  /**
   * Additional metadata
   */
  meta?: Record<string, unknown>;
}

/**
 * Table selection mode
 */
export type SelectionMode = 'none' | 'single' | 'multiple';

/**
 * Table density/size
 */
export type TableDensity = 'compact' | 'normal' | 'comfortable';

/**
 * Table state
 */
export interface TableState<T = unknown> extends PaginationState, SortState, FilterState, SearchState {
  /**
   * Table data rows
   */
  rows: T[];

  /**
   * Selected row IDs
   */
  selectedRows: Set<string | number>;

  /**
   * Expanded row IDs (for nested tables)
   */
  expandedRows: Set<string | number>;

  /**
   * Loading state
   */
  isLoading: boolean;

  /**
   * Error state
   */
  error: Error | null;

  /**
   * Column visibility map
   */
  columnVisibility: Map<string, boolean>;

  /**
   * Column order (array of column IDs)
   */
  columnOrder: string[];

  /**
   * Column widths
   */
  columnWidths: Map<string, number>;
}

/**
 * Table actions
 */
export interface TableActions<T = unknown>
  extends PaginationActions,
    SortActions,
    FilterActions,
    SearchActions {
  /**
   * Select a row
   */
  selectRow: (rowId: string | number) => void;

  /**
   * Deselect a row
   */
  deselectRow: (rowId: string | number) => void;

  /**
   * Toggle row selection
   */
  toggleRowSelection: (rowId: string | number) => void;

  /**
   * Select all rows
   */
  selectAllRows: () => void;

  /**
   * Deselect all rows
   */
  deselectAllRows: () => void;

  /**
   * Toggle all rows selection
   */
  toggleAllRowsSelection: () => void;

  /**
   * Expand a row
   */
  expandRow: (rowId: string | number) => void;

  /**
   * Collapse a row
   */
  collapseRow: (rowId: string | number) => void;

  /**
   * Toggle row expansion
   */
  toggleRowExpansion: (rowId: string | number) => void;

  /**
   * Set column visibility
   */
  setColumnVisibility: (columnId: string, visible: boolean) => void;

  /**
   * Toggle column visibility
   */
  toggleColumnVisibility: (columnId: string) => void;

  /**
   * Reorder columns
   */
  reorderColumns: (columnIds: string[]) => void;

  /**
   * Resize column
   */
  resizeColumn: (columnId: string, width: number) => void;

  /**
   * Reset table state
   */
  resetTable: () => void;

  /**
   * Refresh/reload data
   */
  refreshData: () => void;
}

/**
 * Complete table hook return type
 */
export interface UseTableReturn<T = unknown> extends TableState<T>, TableActions<T> {
  /**
   * Get selected rows data
   */
  getSelectedRows: () => T[];

  /**
   * Get all row IDs
   */
  getAllRowIds: () => Array<string | number>;

  /**
   * Check if row is selected
   */
  isRowSelected: (rowId: string | number) => boolean;

  /**
   * Check if all rows are selected
   */
  areAllRowsSelected: () => boolean;

  /**
   * Check if some rows are selected
   */
  areSomeRowsSelected: () => boolean;

  /**
   * Get visible columns
   */
  getVisibleColumns: () => Array<TableColumn<T>>;
}

/**
 * Table configuration options
 */
export interface TableOptions<T = unknown> {
  /**
   * Table columns
   */
  columns: Array<TableColumn<T>>;

  /**
   * Table data
   */
  data: T[];

  /**
   * Row ID accessor
   */
  getRowId?: (row: T, index: number) => string | number;

  /**
   * Selection mode
   */
  selectionMode?: SelectionMode;

  /**
   * Table density
   */
  density?: TableDensity;

  /**
   * Enable pagination
   */
  enablePagination?: boolean;

  /**
   * Enable sorting
   */
  enableSorting?: boolean;

  /**
   * Enable filtering
   */
  enableFiltering?: boolean;

  /**
   * Enable search
   */
  enableSearch?: boolean;

  /**
   * Enable row selection
   */
  enableRowSelection?: boolean;

  /**
   * Enable column visibility toggle
   */
  enableColumnVisibility?: boolean;

  /**
   * Enable column reordering
   */
  enableColumnReordering?: boolean;

  /**
   * Enable column resizing
   */
  enableColumnResizing?: boolean;

  /**
   * Initial state
   */
  initialState?: Partial<TableState<T>>;

  /**
   * Callback when table state changes
   */
  onStateChange?: (state: TableState<T>) => void;

  /**
   * Callback when row is clicked
   */
  onRowClick?: (row: T, event: React.MouseEvent) => void;

  /**
   * Callback when row is double clicked
   */
  onRowDoubleClick?: (row: T, event: React.MouseEvent) => void;

  /**
   * Callback when selection changes
   */
  onSelectionChange?: (selectedRows: T[]) => void;
}

/**
 * Table action definition (for action column)
 */
export interface TableAction<T = unknown> {
  /**
   * Action ID
   */
  id: string;

  /**
   * Action label
   */
  label: string;

  /**
   * Action icon
   */
  icon?: ReactNode;

  /**
   * Action handler
   */
  onClick: (row: T) => void;

  /**
   * Whether action is visible for this row
   */
  visible?: (row: T) => boolean;

  /**
   * Whether action is disabled for this row
   */
  disabled?: (row: T) => boolean;

  /**
   * Action variant/style
   */
  variant?: 'default' | 'primary' | 'danger' | 'success' | 'warning';

  /**
   * Confirmation message before executing
   */
  confirmMessage?: string | ((row: T) => string);
}
