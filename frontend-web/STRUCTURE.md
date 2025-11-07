# Frontend Web Structure

This document describes the architecture and folder structure of the frontend-web application.

## Directory Structure Overview

```
src/
├── app/                    # Application core (App.tsx, router, providers)
├── api/                    # API clients and interceptors
├── config/                 # Configuration files
├── design-system/          # Design system (primitives, patterns, widgets)
├── common/                 # Shared components, hooks, utils
├── features/               # Feature modules (auth, user, order)
├── modules/                # Cross-feature compositions
├── layouts/                # Layout components
├── pages/                  # Page components
├── queries/                # React Query hooks
├── store/                  # State management
├── assets/                 # Static assets
├── types/                  # Global type definitions
└── generated/              # Auto-generated code
```

## Key Principles

### 1. Design System First
- **Primitives**: Atomic components (Button, Input, etc.)
- **Patterns**: Composite components (Form, Modal, Card)
- **Widgets**: Complex components (Calendar, DatePicker, RichTextEditor)

### 2. Feature-Based Architecture
Each feature is self-contained with:
- `api/` - API endpoints
- `models/` - DTOs and data models
- `mappers/` - Data transformation
- `components/` - Feature-specific UI
- `hooks/` - Feature-specific logic
- `schemas/` - Validation schemas
- `types.ts` - Type definitions
- `constants.ts` - Feature constants

### 3. Common Layer
Generic, reusable components and utilities:
- **data-display**: DataTable, Pagination, SearchBar, FilterPanel
- **feedback**: Toast, Alert, LoadingSkeleton, EmptyState
- **business**: Domain-specific formatters and badges

### 4. Clear Separation of Concerns
- `api/` - HTTP clients and interceptors
- `queries/` - React Query hooks for data fetching
- `store/` - Global state management
- `common/` - Shared utilities and components
- `features/` - Business logic and feature-specific code

## Current Status

✅ **Completed Structures:**
- Application core (app/, router, providers)
- API layer (clients, interceptors, types)
- Configuration layer
- Design system foundations and components
- Common components (DataTable, Pagination, FilterPanel, etc.)
- Common hooks (data-management, utils, api)
- Common utilities and services
- Features: auth, user (with full structure)
- Layouts: MainLayout, AuthLayout, EmptyLayout
- Pages structure
- Queries structure
- Store structure
- Type definitions

## File Naming Conventions

- **Components**: PascalCase (e.g., `UserTable.tsx`)
- **Hooks**: camelCase with `use` prefix (e.g., `useUserTable.ts`)
- **Utils**: camelCase with `.util.ts` suffix (e.g., `date.util.ts`)
- **Types**: PascalCase with `.types.ts` suffix (e.g., `User.types.ts`)
- **Constants**: camelCase with `.constants.ts` suffix
- **Services**: camelCase with `.service.ts` suffix

## Import/Export Strategy

All directories have `index.ts` files for clean imports:

```typescript
// ✅ Good
import { UserTable } from '@/features/user';

// ❌ Avoid
import { UserTable } from '@/features/user/components/UserTable/UserTable';
```

## Next Steps

1. Implement RepeatWise-specific features (deck, card, folder, review)
2. Create domain-specific components
3. Implement SRS algorithm logic
4. Add i18n support
5. Add testing infrastructure

---

Generated: $(date)
