# 📋 Checklist phát triển Common Files

## 🎯 TIER 1: FOUNDATION (Bắt buộc trước khi làm bất kỳ feature nào)

### Design System Foundations

- [ ] `design-system/foundations/colors.ts` - Color palette
- [ ] `design-system/foundations/typography.ts` - Typography scale
- [ ] `design-system/foundations/spacing.ts` - Spacing scale
- [ ] `design-system/foundations/breakpoints.ts` - Responsive breakpoints
- [ ] `design-system/foundations/shadows.ts` - Shadow definitions

### Common Types

- [ ] `common/types/data-management/pagination.types.ts`
- [ ] `common/types/data-management/filter.types.ts`
- [ ] `common/types/data-management/sort.types.ts`
- [ ] `common/types/data-management/index.ts`

### Configuration

- [ ] `config/env.config.ts` - Environment variables
- [ ] `config/app.config.ts` - App constants
- [ ] `config/api.config.ts` - API configuration
- [ ] `config/services.config.ts` - Services config

### Common Utils

- [ ] `common/utils/string.util.ts` - String utilities
- [ ] `common/utils/number.util.ts` - Number utilities
- [ ] `common/utils/date.util.ts` - Date utilities
- [ ] `common/utils/validation.util.ts` - Validation helpers
- [ ] `common/utils/array.util.ts` - Array utilities
- [ ] `common/utils/url.util.ts` - URL utilities
- [ ] `common/utils/index.ts`

---

## ⚙️ TIER 2: CORE INFRASTRUCTURE

### Common Hooks - Utils

- [ ] `common/hooks/utils/useDebounce.ts`
- [ ] `common/hooks/utils/useToggle.ts`
- [ ] `common/hooks/utils/useLocalStorage.ts`
- [ ] `common/hooks/utils/useMediaQuery.ts`
- [ ] `common/hooks/utils/useClickOutside.ts`
- [ ] `common/hooks/utils/index.ts`

### Common Services

- [ ] `common/services/web-storage.service.ts`
- [ ] `common/services/notification.service.ts`
- [ ] `common/services/index.ts`

### API Layer

- [ ] `api/types/api-response.ts`
- [ ] `api/types/error-response.ts`
- [ ] `api/types/page-response.ts`
- [ ] `api/types/index.ts`
- [ ] `api/clients/base.client.ts`
- [ ] `api/interceptors/error.interceptor.ts`
- [ ] `api/interceptors/auth.interceptor.ts`
- [ ] `api/interceptors/logger.interceptor.ts`
- [ ] `api/interceptors/retry.interceptor.ts`
- [ ] `api/interceptors/index.ts`

### Design System Primitives (6 components)

- [ ] `Button` - Button component với variants
- [ ] `Input` - Input field component
- [ ] `Select` - Select dropdown component
- [ ] `Checkbox` - Checkbox component
- [ ] `Radio` - Radio button component
- [ ] `Badge` - Badge component

---

## 🎨 TIER 3: DESIGN SYSTEM PATTERNS

### Form Pattern

- [ ] `design-system/components/patterns/Form/Form.tsx`
- [ ] `design-system/components/patterns/Form/FormField.tsx`
- [ ] `design-system/components/patterns/Form/FormError.tsx`
- [ ] `design-system/components/patterns/Form/Form.types.ts`

### Modal & Drawer

- [ ] `design-system/components/patterns/Modal/Modal.tsx`
- [ ] `design-system/components/patterns/Modal/ModalHeader.tsx`
- [ ] `design-system/components/patterns/Modal/ModalBody.tsx`
- [ ] `design-system/components/patterns/Modal/ModalFooter.tsx`
- [ ] `design-system/components/patterns/Drawer/Drawer.tsx`
- [ ] `design-system/components/patterns/Card/Card.tsx`

---

## 📊 TIER 4: DATA MANAGEMENT

### Data Management Hooks

- [ ] `common/hooks/data-management/usePaginationState.ts`
- [ ] `common/hooks/data-management/useSortState.ts`
- [ ] `common/hooks/data-management/useFilterState.ts`
- [ ] `common/hooks/data-management/useSearchState.ts`
- [ ] `common/hooks/data-management/useTableState.ts`
- [ ] `common/hooks/data-management/index.ts`

### DataTable (Component quan trọng nhất)

- [ ] `common/components/data-display/DataTable/DataTable.tsx`
- [ ] `common/components/data-display/DataTable/DataTable.types.ts`
- [ ] `common/components/data-display/DataTable/components/TableHeader.tsx`
- [ ] `common/components/data-display/DataTable/components/TableBody.tsx`
- [ ] `common/components/data-display/DataTable/components/TableRow.tsx`
- [ ] `common/components/data-display/DataTable/components/TableCell.tsx`
- [ ] `common/components/data-display/DataTable/components/TableFooter.tsx`
- [ ] `common/components/data-display/DataTable/components/TableToolbar.tsx`
- [ ] `common/components/data-display/DataTable/components/EmptyState.tsx`
- [ ] `common/components/data-display/DataTable/components/LoadingState.tsx`
- [ ] `common/components/data-display/DataTable/hooks/useTable.ts`

### Pagination

- [ ] `common/components/data-display/Pagination/Pagination.tsx`
- [ ] `common/components/data-display/Pagination/Pagination.types.ts`
- [ ] `common/components/data-display/Pagination/components/PaginationInfo.tsx`
- [ ] `common/components/data-display/Pagination/components/PaginationControls.tsx`

### SearchBar

- [ ] `common/components/data-display/SearchBar/SearchBar.tsx`
- [ ] `common/components/data-display/SearchBar/SearchBar.types.ts`
- [ ] `common/components/data-display/SearchBar/hooks/useSearch.ts`

### FilterPanel

- [ ] `common/components/data-display/FilterPanel/FilterPanel.tsx`
- [ ] `common/components/data-display/FilterPanel/FilterPanel.types.ts`
- [ ] `common/components/data-display/FilterPanel/components/FilterField.tsx`

### SortControl

- [ ] `common/components/data-display/SortControl/SortControl.tsx`
- [ ] `common/components/data-display/SortControl/SortControl.types.ts`

---

## 💬 TIER 5: FEEDBACK & BUSINESS

### Feedback Components

- [ ] `common/components/feedback/Toast/Toast.tsx`
- [ ] `common/components/feedback/Alert/Alert.tsx`
- [ ] `common/components/feedback/LoadingSkeleton/LoadingSkeleton.tsx`
- [ ] `common/components/feedback/EmptyState/EmptyState.tsx`

### Business Components

- [ ] `common/components/business/DateFormatter/DateFormatter.tsx`
- [ ] `common/components/business/PriceFormatter/PriceFormatter.tsx`
- [ ] `common/components/business/UserStatusBadge/UserStatusBadge.tsx`

### API Hooks

- [ ] `common/hooks/api/useOptimisticUpdate.ts`
- [ ] `common/hooks/api/useInfiniteScroll.ts`

---

## 📦 TIER 6: WIDGETS (Optional, có thể làm sau)

- [ ] `design-system/components/widgets/Calendar/Calendar.tsx`
- [ ] `design-system/components/widgets/DatePicker/DatePicker.tsx`
- [ ] `design-system/components/widgets/RichTextEditor/RichTextEditor.tsx`

---

## 📝 Index Files cần update

Sau khi hoàn thành mỗi tier, cần update các file `index.ts`:

- [ ] `design-system/foundations/index.ts`
- [ ] `design-system/components/primitives/index.ts`
- [ ] `design-system/components/patterns/index.ts`
- [ ] `design-system/components/widgets/index.ts`
- [ ] `design-system/components/index.ts`
- [ ] `design-system/index.ts`
- [ ] `common/types/index.ts`
- [ ] `common/utils/index.ts`
- [ ] `common/hooks/index.ts`
- [ ] `common/services/index.ts`
- [ ] `common/components/data-display/index.ts`
- [ ] `common/components/feedback/index.ts`
- [ ] `common/components/business/index.ts`
- [ ] `common/components/index.ts`
- [ ] `common/index.ts`
- [ ] `config/index.ts`
- [ ] `api/index.ts`

---

## 🎯 Milestone Goals

### Milestone 1: Có thể bắt đầu làm Feature Auth

✅ Cần: Tier 1 + Tier 2 (Button, Input, Form) + Toast

### Milestone 2: Có thể bắt đầu làm Feature User (CRUD)

✅ Cần: Milestone 1 + DataTable + Pagination + SearchBar + FilterPanel

### Milestone 3: Có thể bắt đầu làm các Feature RepeatWise

✅ Cần: Milestone 2 + Tất cả feedback components

### Milestone 4: Production Ready

✅ Cần: Tất cả Tiers + Testing + Documentation

---

**Progress**: 0/~150 files completed
**Last updated**: 2025-11-08
