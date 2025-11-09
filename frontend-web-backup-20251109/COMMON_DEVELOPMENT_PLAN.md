# Kế hoạch phát triển Common Files - Frontend Web

## 📋 Tổng quan

Tất cả các file TypeScript trong `frontend-web/src` hiện đang **RỖNG** (0 bytes). Cần phát triển các file common theo thứ tự ưu tiên để xây dựng nền tảng cho việc phát triển các module nghiệp vụ.

**Tổng số file cần phát triển**: ~350+ files

## 🎯 Chiến lược phát triển

Phát triển theo 5 tầng (Tiers) từ foundation đến business components, đảm bảo mỗi tầng hoàn thiện trước khi chuyển sang tầng tiếp theo.

---

## 🏗️ TIER 1: FOUNDATION (Ưu tiên cao nhất)

### 1.1 Design System Foundations
📁 `design-system/foundations/`

| File | Mô tả | Dependencies | Ưu tiên |
|------|-------|--------------|---------|
| `colors.ts` | Định nghĩa color palette | Tailwind config | ⭐⭐⭐⭐⭐ |
| `typography.ts` | Font sizes, weights, line heights | None | ⭐⭐⭐⭐⭐ |
| `spacing.ts` | Spacing scale (margin, padding) | None | ⭐⭐⭐⭐⭐ |
| `breakpoints.ts` | Responsive breakpoints | None | ⭐⭐⭐⭐⭐ |
| `shadows.ts` | Box shadow definitions | None | ⭐⭐⭐⭐ |

**Lý do ưu tiên**: Tất cả components sẽ sử dụng các giá trị này.

### 1.2 Common Types
📁 `common/types/`

| File | Mô tả | Dependencies | Ưu tiên |
|------|-------|--------------|---------|
| `data-management/index.ts` | PaginationParams, SortParams, FilterParams | None | ⭐⭐⭐⭐⭐ |
| `data-management/pagination.types.ts` | Pagination interfaces | None | ⭐⭐⭐⭐⭐ |
| `data-management/filter.types.ts` | Filter interfaces | None | ⭐⭐⭐⭐⭐ |
| `data-management/sort.types.ts` | Sort interfaces | None | ⭐⭐⭐⭐⭐ |

**Lý do ưu tiên**: Cần cho tất cả CRUD operations.

### 1.3 Configuration
📁 `config/`

| File | Mô tả | Dependencies | Ưu tiên |
|------|-------|--------------|---------|
| `env.config.ts` | Environment variables | None | ⭐⭐⭐⭐⭐ |
| `app.config.ts` | App constants (name, version) | env.config | ⭐⭐⭐⭐⭐ |
| `api.config.ts` | API endpoints, timeouts | env.config | ⭐⭐⭐⭐⭐ |
| `services.config.ts` | Services configuration | env.config | ⭐⭐⭐⭐ |

**Lý do ưu tiên**: Cần cho toàn bộ application.

### 1.4 Common Utils
📁 `common/utils/`

| File | Mô tả | Dependencies | Ưu tiên |
|------|-------|--------------|---------|
| `string.util.ts` | String manipulation (trim, format) | None | ⭐⭐⭐⭐⭐ |
| `number.util.ts` | Number formatting | None | ⭐⭐⭐⭐⭐ |
| `date.util.ts` | Date formatting, parsing | None | ⭐⭐⭐⭐⭐ |
| `validation.util.ts` | Common validation functions | None | ⭐⭐⭐⭐ |
| `array.util.ts` | Array operations | None | ⭐⭐⭐⭐ |
| `url.util.ts` | URL manipulation | None | ⭐⭐⭐ |

**Lý do ưu tiên**: Sử dụng rộng rãi trong toàn bộ app.

**Thời gian ước tính Tier 1**: 2-3 ngày

---

## ⚙️ TIER 2: CORE INFRASTRUCTURE (Ưu tiên cao)

### 2.1 Common Hooks - Utils
📁 `common/hooks/utils/`

| File | Mô tả | Dependencies | Ưu tiên |
|------|-------|--------------|---------|
| `useDebounce.ts` | Debounce value changes | React | ⭐⭐⭐⭐⭐ |
| `useToggle.ts` | Toggle boolean state | React | ⭐⭐⭐⭐⭐ |
| `useLocalStorage.ts` | LocalStorage sync | React | ⭐⭐⭐⭐⭐ |
| `useMediaQuery.ts` | Responsive breakpoints | breakpoints.ts | ⭐⭐⭐⭐ |
| `useClickOutside.ts` | Detect outside clicks | React | ⭐⭐⭐⭐ |

**Lý do ưu tiên**: Sử dụng trong nhiều components.

### 2.2 Common Services
📁 `common/services/`

| File | Mô tả | Dependencies | Ưu tiên |
|------|-------|--------------|---------|
| `web-storage.service.ts` | LocalStorage/SessionStorage wrapper | None | ⭐⭐⭐⭐⭐ |
| `notification.service.ts` | Toast notifications | sonner | ⭐⭐⭐⭐⭐ |

**Lý do ưu tiên**: Core services cho app.

### 2.3 API Layer - Base
📁 `api/`

| File | Mô tả | Dependencies | Ưu tiên |
|------|-------|--------------|---------|
| `clients/base.client.ts` | Axios base client | axios, api.config | ⭐⭐⭐⭐⭐ |
| `types/api-response.ts` | Generic API response types | None | ⭐⭐⭐⭐⭐ |
| `types/error-response.ts` | Error response types | None | ⭐⭐⭐⭐⭐ |
| `types/page-response.ts` | Pagination response types | pagination.types | ⭐⭐⭐⭐⭐ |

**Lý do ưu tiên**: Foundation cho tất cả API calls.

### 2.4 API Interceptors
📁 `api/interceptors/`

| File | Mô tả | Dependencies | Ưu tiên |
|------|-------|--------------|---------|
| `error.interceptor.ts` | Global error handling | base.client | ⭐⭐⭐⭐⭐ |
| `auth.interceptor.ts` | Attach JWT tokens | base.client, storage | ⭐⭐⭐⭐⭐ |
| `logger.interceptor.ts` | Request/response logging | base.client | ⭐⭐⭐⭐ |
| `retry.interceptor.ts` | Auto retry failed requests | base.client | ⭐⭐⭐ |

**Lý do ưu tiên**: Critical cho API communication.

### 2.5 Design System Primitives
📁 `design-system/components/primitives/`

| Component | Dependencies | Ưu tiên |
|-----------|--------------|---------|
| **Button** | foundations, CVA | ⭐⭐⭐⭐⭐ |
| **Input** | foundations | ⭐⭐⭐⭐⭐ |
| **Select** | Radix UI, foundations | ⭐⭐⭐⭐⭐ |
| **Checkbox** | Radix UI, foundations | ⭐⭐⭐⭐ |
| **Radio** | Radix UI, foundations | ⭐⭐⭐⭐ |
| **Badge** | foundations, CVA | ⭐⭐⭐⭐ |

**Lý do ưu tiên**: Building blocks cho tất cả UI.

**Thời gian ước tính Tier 2**: 3-4 ngày

---

## 🎨 TIER 3: DESIGN SYSTEM PATTERNS (Ưu tiên trung bình)

### 3.1 Form Components
📁 `design-system/components/patterns/Form/`

| File | Dependencies | Ưu tiên |
|------|--------------|---------|
| `Form.tsx` | react-hook-form, primitives | ⭐⭐⭐⭐⭐ |
| `FormField.tsx` | Form.tsx | ⭐⭐⭐⭐⭐ |
| `FormError.tsx` | Form.tsx | ⭐⭐⭐⭐⭐ |
| `Form.types.ts` | react-hook-form | ⭐⭐⭐⭐⭐ |

**Lý do ưu tiên**: Cần cho tất cả forms.

### 3.2 Modal & Drawer
📁 `design-system/components/patterns/`

| Component | Dependencies | Ưu tiên |
|-----------|--------------|---------|
| **Modal** | Radix Dialog, primitives | ⭐⭐⭐⭐⭐ |
| **Drawer** | Radix Dialog, primitives | ⭐⭐⭐⭐ |
| **Card** | primitives | ⭐⭐⭐⭐ |

**Lý do ưu tiên**: Dùng nhiều trong features.

**Thời gian ước tính Tier 3**: 2-3 ngày

---

## 📊 TIER 4: DATA MANAGEMENT COMPONENTS (Ưu tiên trung bình)

### 4.1 Common Hooks - Data Management
📁 `common/hooks/data-management/`

| File | Dependencies | Ưu tiên |
|------|--------------|---------|
| `usePaginationState.ts` | React, pagination.types | ⭐⭐⭐⭐⭐ |
| `useSortState.ts` | React, sort.types | ⭐⭐⭐⭐⭐ |
| `useFilterState.ts` | React, filter.types | ⭐⭐⭐⭐⭐ |
| `useSearchState.ts` | React, useDebounce | ⭐⭐⭐⭐⭐ |
| `useTableState.ts` | All above hooks | ⭐⭐⭐⭐⭐ |

**Lý do ưu tiên**: Core cho DataTable và CRUD operations.

### 4.2 DataTable Component
📁 `common/components/data-display/DataTable/`

| File | Dependencies | Ưu tiên |
|------|--------------|---------|
| `DataTable.tsx` | primitives, hooks | ⭐⭐⭐⭐⭐ |
| `DataTable.types.ts` | data-management types | ⭐⭐⭐⭐⭐ |
| `components/TableHeader.tsx` | DataTable | ⭐⭐⭐⭐⭐ |
| `components/TableBody.tsx` | DataTable | ⭐⭐⭐⭐⭐ |
| `components/TableRow.tsx` | DataTable | ⭐⭐⭐⭐⭐ |
| `components/TableCell.tsx` | DataTable | ⭐⭐⭐⭐⭐ |
| `components/TableFooter.tsx` | DataTable | ⭐⭐⭐⭐ |
| `components/TableToolbar.tsx` | DataTable, SearchBar | ⭐⭐⭐⭐ |
| `components/EmptyState.tsx` | DataTable | ⭐⭐⭐⭐ |
| `components/LoadingState.tsx` | DataTable | ⭐⭐⭐⭐ |
| `hooks/useTable.ts` | useTableState | ⭐⭐⭐⭐⭐ |
| `hooks/useTableSort.ts` | useSortState | ⭐⭐⭐⭐ |
| `hooks/useTableSelection.ts` | React | ⭐⭐⭐⭐ |

**Lý do ưu tiên**: Component quan trọng nhất cho hiển thị dữ liệu.

### 4.3 Pagination Component
📁 `common/components/data-display/Pagination/`

| File | Dependencies | Ưu tiên |
|------|--------------|---------|
| `Pagination.tsx` | primitives, usePaginationState | ⭐⭐⭐⭐⭐ |
| `Pagination.types.ts` | pagination.types | ⭐⭐⭐⭐⭐ |
| `components/PaginationInfo.tsx` | Pagination | ⭐⭐⭐⭐ |
| `components/PaginationControls.tsx` | Pagination | ⭐⭐⭐⭐ |
| `hooks/usePagination.ts` | usePaginationState | ⭐⭐⭐⭐ |

**Lý do ưu tiên**: Dùng cho tất cả list views.

### 4.4 SearchBar Component
📁 `common/components/data-display/SearchBar/`

| File | Dependencies | Ưu tiên |
|------|--------------|---------|
| `SearchBar.tsx` | Input, useSearchState | ⭐⭐⭐⭐⭐ |
| `SearchBar.types.ts` | None | ⭐⭐⭐⭐⭐ |
| `hooks/useSearch.ts` | useSearchState, useDebounce | ⭐⭐⭐⭐ |

**Lý do ưu tiên**: Tính năng search rất phổ biến.

### 4.5 FilterPanel Component
📁 `common/components/data-display/FilterPanel/`

| File | Dependencies | Ưu tiên |
|------|--------------|---------|
| `FilterPanel.tsx` | Form, useFilterState | ⭐⭐⭐⭐ |
| `FilterPanel.types.ts` | filter.types | ⭐⭐⭐⭐ |
| `components/FilterField.tsx` | FilterPanel | ⭐⭐⭐⭐ |
| `hooks/useFilter.ts` | useFilterState | ⭐⭐⭐⭐ |

**Lý do ưu tiên**: Cần cho advanced filtering.

### 4.6 SortControl Component
📁 `common/components/data-display/SortControl/`

| File | Dependencies | Ưu tiên |
|------|--------------|---------|
| `SortControl.tsx` | Button, useSortState | ⭐⭐⭐⭐ |
| `SortControl.types.ts` | sort.types | ⭐⭐⭐⭐ |
| `hooks/useSort.ts` | useSortState | ⭐⭐⭐⭐ |

**Lý do ưu tiên**: Tính năng sort phổ biến.

### 4.7 DateRangePicker Component
📁 `common/components/data-display/DateRangePicker/`

| File | Dependencies | Ưu tiên |
|------|--------------|---------|
| `DateRangePicker.tsx` | DatePicker widget | ⭐⭐⭐ |
| `DateRangePicker.types.ts` | None | ⭐⭐⭐ |
| `hooks/useDateRange.ts` | React | ⭐⭐⭐ |

**Lý do ưu tiên**: Hữu ích cho filtering theo ngày.

**Thời gian ước tính Tier 4**: 4-5 ngày

---

## 💬 TIER 5: FEEDBACK & BUSINESS COMPONENTS

### 5.1 Feedback Components
📁 `common/components/feedback/`

| Component | Dependencies | Ưu tiên |
|-----------|--------------|---------|
| **Toast** | sonner | ⭐⭐⭐⭐⭐ |
| **Alert** | primitives | ⭐⭐⭐⭐ |
| **LoadingSkeleton** | primitives | ⭐⭐⭐⭐ |
| **EmptyState** | primitives | ⭐⭐⭐⭐ |

**Lý do ưu tiên**: UX feedback quan trọng.

### 5.2 Business Components
📁 `common/components/business/`

| Component | Dependencies | Ưu tiên |
|-----------|--------------|---------|
| **DateFormatter** | date.util | ⭐⭐⭐ |
| **PriceFormatter** | number.util | ⭐⭐⭐ |
| **UserStatusBadge** | Badge | ⭐⭐⭐ |

**Lý do ưu tiên**: Specific cho business logic.

### 5.3 Common Hooks - API
📁 `common/hooks/api/`

| File | Dependencies | Ưu tiên |
|------|--------------|---------|
| `useOptimisticUpdate.ts` | React Query | ⭐⭐⭐ |
| `useInfiniteScroll.ts` | React Query | ⭐⭐⭐ |

**Lý do ưu tiên**: Advanced API patterns.

**Thời gian ước tính Tier 5**: 2-3 ngày

---

## 📦 TIER 6: WIDGETS & ADVANCED COMPONENTS

### 6.1 Design System Widgets
📁 `design-system/components/widgets/`

| Component | Dependencies | Ưu tiên |
|-----------|--------------|---------|
| **DatePicker** | Radix UI, Calendar | ⭐⭐⭐ |
| **Calendar** | Radix UI | ⭐⭐⭐ |
| **RichTextEditor** | Third-party lib | ⭐⭐ |

**Lý do ưu tiên**: Advanced features, có thể dùng sau.

**Thời gian ước tính Tier 6**: 3-4 ngày

---

## 📈 Tổng kết & Khuyến nghị

### Thứ tự triển khai đề xuất

**GIAI ĐOẠN 1 (Tuần 1): Foundation**
1. ✅ Design System Foundations (1 ngày)
2. ✅ Common Types (0.5 ngày)
3. ✅ Configuration (0.5 ngày)
4. ✅ Common Utils (1 ngày)

**GIAI ĐOẠN 2 (Tuần 2): Core Infrastructure**
1. ✅ Common Hooks - Utils (1 ngày)
2. ✅ Common Services (0.5 ngày)
3. ✅ API Layer Base + Interceptors (1.5 ngày)
4. ✅ Design System Primitives (1 ngày)

**GIAI ĐOẠN 3 (Tuần 3): Patterns & Data Management Hooks**
1. ✅ Form Components (1 ngày)
2. ✅ Modal & Drawer (0.5 ngày)
3. ✅ Card (0.5 ngày)
4. ✅ Common Hooks - Data Management (1 ngày)

**GIAI ĐOẠN 4 (Tuần 4): Data Components**
1. ✅ DataTable (2 ngày)
2. ✅ Pagination (1 ngày)
3. ✅ SearchBar (0.5 ngày)
4. ✅ FilterPanel (0.5 ngày)
5. ✅ SortControl (0.5 ngày)

**GIAI ĐOẠN 5 (Tuần 5): Feedback & Polish**
1. ✅ Feedback Components (1 ngày)
2. ✅ Business Components (0.5 ngày)
3. ✅ DateRangePicker (1 ngày)
4. ✅ Testing & Documentation (1.5 ngày)

**GIAI ĐOẠN 6 (Tùy chọn): Widgets**
1. ⏸️ Calendar & DatePicker (2 ngày)
2. ⏸️ RichTextEditor (1 ngày)

### Tổng thời gian ước tính
- **Core development**: 4-5 tuần
- **With widgets**: 5-6 tuần

### Các điểm lưu ý

1. **Testing**: Viết test song song với code
2. **Documentation**: Viết JSDoc comments
3. **Storybook**: Setup cho design system
4. **Accessibility**: Đảm bảo a11y cho tất cả components
5. **Performance**: Optimize re-renders với React.memo, useMemo, useCallback

### Dependencies quan trọng

Đã có trong `package.json`:
- ✅ React 19
- ✅ TypeScript
- ✅ Tailwind CSS
- ✅ Radix UI
- ✅ React Hook Form
- ✅ Zod
- ✅ Axios
- ✅ React Query
- ✅ Zustand
- ✅ Sonner (Toast)
- ✅ CVA (Class Variance Authority)
- ✅ lucide-react (Icons)

### Checklist cho mỗi component

- [ ] Component implementation
- [ ] Type definitions
- [ ] Props validation
- [ ] Error handling
- [ ] Loading states
- [ ] Empty states
- [ ] Accessibility (ARIA labels, keyboard navigation)
- [ ] Responsive design
- [ ] Dark mode support
- [ ] Unit tests
- [ ] Integration tests
- [ ] Storybook story
- [ ] Documentation
- [ ] Index exports

---

**Tạo ngày**: 2025-11-08
**Phiên bản**: 1.0
**Người tạo**: Claude AI
