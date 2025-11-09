# Frontend Directory Tree

This is a visual representation of the frontend-web directory structure.

## Complete Structure

```
frontend-web/src/
│
├─ 📱 app/                                    # Application Core
│  ├─ router.tsx                              # React Router configuration
│  ├─ App.tsx                                 # Root App component
│  ├─ providers/                              # Context providers
│  │  ├─ index.ts
│  │  ├─ QueryProvider.tsx                    # React Query provider
│  │  ├─ ThemeProvider.tsx                    # Theme context provider
│  │  └─ AuthProvider.tsx                     # Authentication provider
│  └─ index.ts
│
├─ ⚙️ config/                                 # Configuration
│  ├─ index.ts
│  ├─ env.ts                                  # Environment variables
│  ├─ services.config.ts                      # Service configurations
│  └─ app.config.ts                           # App-level config
│
├─ 🌐 api/                                    # API Layer
│  ├─ index.ts
│  ├─ clients/                                # API clients
│  │  ├─ index.ts
│  │  ├─ base.client.ts                       # Base Axios instance
│  │  ├─ auth.client.ts                       # Auth API client
│  │  ├─ user.client.ts                       # User API client
│  │  └─ order.client.ts                      # Order API client
│  ├─ interceptors/                           # Axios interceptors
│  │  ├─ index.ts
│  │  ├─ auth.interceptor.ts                  # JWT token injection
│  │  ├─ error.interceptor.ts                 # Error handling
│  │  ├─ logger.interceptor.ts                # Request/response logging
│  │  └─ retry.interceptor.ts                 # Retry logic
│  └─ types/                                  # API types
│     ├─ index.ts
│     ├─ api-response.ts                      # Generic API response
│     ├─ page-response.ts                     # Paginated response
│     └─ error-response.ts                    # Error response

├─ 🎨 design-system/                          # Design System
│  ├─ index.ts
│  ├─ foundations/                            # Design tokens
│  │  ├─ index.ts
│  │  ├─ colors.ts                            # Color palette
│  │  ├─ typography.ts                        # Font styles
│  │  ├─ spacing.ts                           # Spacing scale
│  │  ├─ breakpoints.ts                       # Responsive breakpoints
│  │  └─ shadows.ts                           # Shadow definitions
│  └─ components/
│     ├─ index.ts
│     ├─ primitives/                          # Atomic components
│     │  ├─ index.ts
│     │  ├─ Button/
│     │  ├─ Input/
│     │  ├─ Checkbox/
│     │  ├─ Select/
│     │  ├─ Radio/
│     │  └─ Badge/
│     ├─ patterns/                            # Composite components
│     │  ├─ index.ts
│     │  ├─ Form/
│     │  ├─ Modal/
│     │  ├─ Drawer/
│     │  └─ Card/
│     └─ widgets/                             # Complex components
│        ├─ index.ts
│        ├─ Calendar/
│        ├─ DatePicker/
│        └─ RichTextEditor/
│
├─ 🔧 common/                                 # Shared Resources
│  ├─ index.ts
│  ├─ components/
│  │  ├─ index.ts
│  │  ├─ data-display/                        # Data display components
│  │  │  ├─ index.ts
│  │  │  ├─ DataTable/                        # ⭐ Generic Table System
│  │  │  │  ├─ index.ts
│  │  │  │  ├─ DataTable.tsx
│  │  │  │  ├─ DataTable.types.ts
│  │  │  │  ├─ components/                    # Table sub-components
│  │  │  │  ├─ hooks/                         # Table hooks
│  │  │  │  ├─ utils/                         # Table utilities
│  │  │  │  └─ README.md
│  │  │  ├─ Pagination/                       # ⭐ Generic Pagination
│  │  │  │  ├─ index.ts
│  │  │  │  ├─ Pagination.tsx
│  │  │  │  ├─ components/
│  │  │  │  ├─ hooks/
│  │  │  │  └─ README.md
│  │  │  ├─ SearchBar/                        # Generic Search
│  │  │  ├─ FilterPanel/                      # Generic Filter
│  │  │  ├─ SortControl/                      # Generic Sort
│  │  │  └─ DateRangePicker/                  # Date Range Picker
│  │  ├─ feedback/                            # Feedback components
│  │  │  ├─ index.ts
│  │  │  ├─ Toast/
│  │  │  ├─ Alert/
│  │  │  ├─ LoadingSkeleton/
│  │  │  └─ EmptyState/
│  │  └─ business/                            # Business components
│  │     ├─ index.ts
│  │     ├─ UserStatusBadge/
│  │     ├─ PriceFormatter/
│  │     └─ DateFormatter/
│  ├─ hooks/
│  │  ├─ index.ts
│  │  ├─ data-management/                     # Data hooks
│  │  │  ├─ index.ts
│  │  │  ├─ useTableState.ts
│  │  │  ├─ usePaginationState.ts
│  │  │  ├─ useFilterState.ts
│  │  │  ├─ useSortState.ts
│  │  │  └─ useSearchState.ts
│  │  ├─ utils/                               # Utility hooks
│  │  │  ├─ index.ts
│  │  │  ├─ useDebounce.ts
│  │  │  ├─ useLocalStorage.ts
│  │  │  ├─ useMediaQuery.ts
│  │  │  ├─ useToggle.ts
│  │  │  └─ useClickOutside.ts
│  │  └─ api/                                 # API hooks
│  │     ├─ index.ts
│  │     ├─ useInfiniteScroll.ts
│  │     └─ useOptimisticUpdate.ts
│  ├─ utils/
│  │  ├─ index.ts
│  │  ├─ date.util.ts
│  │  ├─ string.util.ts
│  │  ├─ number.util.ts
│  │  ├─ validation.util.ts
│  │  ├─ url.util.ts
│  │  └─ array.util.ts
│  ├─ constants/
│  │  ├─ index.ts
│  │  ├─ storage-keys.ts
│  │  ├─ query-keys.ts
│  │  ├─ routes.ts
│  │  ├─ status.ts
│  │  ├─ regex.ts
│  │  └─ table.constants.ts
│  ├─ types/
│  │  ├─ index.ts
│  │  ├─ data-management/
│  │  │  ├─ index.ts
│  │  │  ├─ table.types.ts
│  │  │  ├─ pagination.types.ts
│  │  │  ├─ filter.types.ts
│  │  │  ├─ sort.types.ts
│  │  │  └─ search.types.ts
│  │  └─ common.types.ts
│  ├─ services/
│  │  ├─ index.ts
│  │  ├─ error-handler.service.ts
│  │  ├─ web-storage.service.ts
│  │  ├─ logger.service.ts
│  │  └─ notification.service.ts
│  └─ styles/
│     ├─ index.css
│     ├─ reset.css
│     └─ tailwind.css
│
├─ 💾 store/                                  # State Management
│  ├─ index.ts
│  └─ slices/
│     ├─ index.ts
│     ├─ auth.slice.ts
│     ├─ ui.slice.ts
│     └─ user.slice.ts
│
├─ 🔄 queries/                                # React Query Hooks
│  ├─ index.ts
│  ├─ user/
│  │  ├─ index.ts
│  │  ├─ useUserQuery.ts
│  │  ├─ useUserMutation.ts
│  │  └─ keys.ts
│  ├─ order/
│  │  ├─ index.ts
│  │  ├─ useOrderQuery.ts
│  │  ├─ useOrderMutation.ts
│  │  └─ keys.ts
│  └─ product/
│     ├─ index.ts
│     ├─ useProductQuery.ts
│     ├─ useProductMutation.ts
│     └─ keys.ts
│
├─ 📦 features/                               # Feature Modules
│  ├─ auth/                                   # ✅ Complete
│  │  ├─ index.ts
│  │  ├─ api/                                 # Auth API calls
│  │  ├─ models/                              # DTOs (login, register, token)
│  │  ├─ mappers/                             # Data mappers
│  │  ├─ hooks/                               # Auth hooks
│  │  ├─ components/                          # Auth components
│  │  │  ├─ LoginForm/
│  │  │  ├─ RegisterForm/
│  │  │  └─ ForgotPasswordForm/
│  │  ├─ schemas/                             # Validation schemas
│  │  ├─ types.ts
│  │  ├─ constants.ts
│  │  └─ README.md
│  ├─ user/                                   # ✅ Complete
│  │  ├─ index.ts
│  │  ├─ api/
│  │  ├─ models/
│  │  ├─ mappers/
│  │  ├─ hooks/
│  │  ├─ components/
│  │  │  ├─ UserTable/                        # Uses DataTable
│  │  │  ├─ UserFilter/                       # Uses FilterPanel
│  │  │  ├─ UserForm/
│  │  │  └─ UserDetail/
│  │  ├─ schemas/
│  │  ├─ types.ts
│  │  ├─ constants.ts
│  │  └─ README.md
│  └─ order/                                  # ⚠️ Placeholder
│     ├─ index.ts
│     ├─ api/
│     ├─ models/
│     ├─ mappers/
│     ├─ hooks/
│     ├─ components/
│     ├─ schemas/
│     ├─ types.ts
│     ├─ constants.ts
│     └─ README.md
│
├─ 🔀 modules/                                # Cross-Feature Compositions
│  ├─ index.ts
│  └─ order-management/
│     ├─ index.ts
│     ├─ components/
│     │  ├─ OrderWithUser/
│     │  └─ OrderDashboard/
│     ├─ hooks/
│     └─ types.ts
│
├─ 🏗️ layouts/                               # Layout Components
│  ├─ index.ts
│  ├─ MainLayout/
│  │  ├─ index.ts
│  │  ├─ MainLayout.tsx
│  │  ├─ Header.tsx
│  │  ├─ Sidebar.tsx
│  │  └─ Footer.tsx
│  ├─ AuthLayout/
│  │  ├─ index.ts
│  │  └─ AuthLayout.tsx
│  └─ EmptyLayout/
│     ├─ index.ts
│     └─ EmptyLayout.tsx
│
├─ 📄 pages/                                  # Page Components
│  ├─ index.ts
│  ├─ auth/
│  │  ├─ index.ts
│  │  ├─ LoginPage.tsx
│  │  ├─ RegisterPage.tsx
│  │  └─ ForgotPasswordPage.tsx
│  ├─ user/
│  │  ├─ index.ts
│  │  ├─ UserListPage.tsx
│  │  ├─ UserDetailPage.tsx
│  │  └─ UserCreatePage.tsx
│  ├─ dashboard/
│  │  ├─ index.ts
│  │  └─ DashboardPage.tsx
│  ├─ NotFoundPage.tsx
│  └─ ErrorPage.tsx
│
├─ 🤖 generated/                              # Auto-generated Code
│  ├─ .gitkeep
│  ├─ api.ts
│  └─ README.md
│
├─ 🎭 assets/                                 # Static Assets
│  ├─ images/
│  │  ├─ logo.svg
│  │  └─ .gitkeep
│  ├─ icons/
│  │  └─ .gitkeep
│  └─ fonts/
│     └─ .gitkeep
│
└─ 📘 types/                                  # Global Types
   ├─ env.d.ts                                # Environment types
   ├─ global.d.ts                             # Global declarations
   └─ modules.d.ts                            # Module declarations
```

## Statistics

- **Total TypeScript files**: 347
- **Total Components**: 91
- **Index files**: 106
- **README files**: 10

## Completion Status

| Category | Status | Files |
|----------|--------|-------|
| App Core | ✅ Complete | 7 |
| API Layer | ✅ Complete | 15 |
| Config | ✅ Complete | 4 |
| Design System | ✅ Complete | 40+ |
| Common Components | ✅ Complete | 60+ |
| Features (auth, user) | ✅ Complete | 50+ |
| Features (order) | ⚠️ Placeholder | 1 |
| Layouts | ✅ Complete | 10+ |
| Pages | ✅ Complete | 10+ |
| Store | ✅ Complete | 4 |
| Queries | ✅ Complete | 15+ |

## Key Features

### ✅ Implemented
- Complete design system (primitives, patterns, widgets)
- Generic data components (DataTable, Pagination, FilterPanel)
- Auth feature module (complete)
- User feature module (complete)
- API layer with interceptors
- Type-safe configuration
- React Query integration
- State management setup

### 🔄 Ready for Implementation
- RepeatWise specific features (deck, card, folder, review)
- Additional business components
- i18n support
- Testing infrastructure
- Storybook integration

---

Generated: 2025-11-07
