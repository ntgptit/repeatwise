# Frontend Structure Documentation

This directory contains the complete frontend architecture for the RepeatWise application.

## Quick Links

- [STRUCTURE.md](./STRUCTURE.md) - Overview of directory structure and key principles
- [DIRECTORY_TREE.md](./DIRECTORY_TREE.md) - Visual tree representation with statistics
- [ARCHITECTURE.md](./ARCHITECTURE.md) - Detailed architecture guide and best practices

## Project Statistics

- **Total Files**: 347+ TypeScript files
- **Components**: 91+ React components
- **Index Files**: 106 for clean exports
- **Documentation**: 10 README files

## Structure Summary

```
src/
├── app/              # Application core (router, providers)
├── api/              # API clients and interceptors
├── config/           # Configuration files
├── design-system/    # UI components (primitives, patterns, widgets)
├── common/           # Shared components, hooks, utils
├── features/         # Feature modules (auth, user, order)
├── modules/          # Cross-feature compositions
├── layouts/          # Layout components
├── pages/            # Page components
├── queries/          # React Query hooks
├── store/            # State management
├── assets/           # Static assets
├── types/            # Global type definitions
└── generated/        # Auto-generated code
```

## Key Features

### ✅ Implemented

- Complete design system (primitives, patterns, widgets)
- Generic data components (DataTable, Pagination, FilterPanel, SearchBar)
- Auth feature module with login, register, forgot password
- User feature module with CRUD operations
- API layer with interceptors (auth, error, logger, retry)
- Type-safe configuration
- React Query integration
- State management setup

### 🎯 Next Steps for RepeatWise

- Implement Deck management feature
- Implement Card management feature
- Implement Folder system feature
- Implement Review/SRS feature
- Implement Statistics dashboard
- Add i18n support
- Add testing infrastructure
- Setup Storybook

## Architecture Highlights

### 1. Feature-Based Architecture

Each feature is self-contained:

```
features/[name]/
├── api/          # API endpoints
├── models/       # DTOs and interfaces
├── mappers/      # Data transformation
├── components/   # UI components
├── hooks/        # Business logic
├── schemas/      # Validation
└── types.ts      # Types
```

### 2. Design System

Three-tier hierarchy:

- **Primitives**: Button, Input, Checkbox, Select, Radio, Badge
- **Patterns**: Form, Modal, Drawer, Card
- **Widgets**: Calendar, DatePicker, RichTextEditor

### 3. Common Layer

Generic reusable components:

- **Data Display**: DataTable, Pagination, SearchBar, FilterPanel, SortControl
- **Feedback**: Toast, Alert, LoadingSkeleton, EmptyState
- **Business**: Formatters and domain-specific components

### 4. Clean Imports

Every directory has index.ts for clean imports:

```typescript
// Good
import { UserTable } from '@/features/user';

// Avoid
import { UserTable } from '@/features/user/components/UserTable/UserTable';
```

## Current Implementation Status

| Module | Status | Description |
|--------|--------|-------------|
| App Core | ✅ Complete | Router, providers, App.tsx |
| API Layer | ✅ Complete | Clients, interceptors, types |
| Config | ✅ Complete | Environment, app, services config |
| Design System | ✅ Complete | 13 components across 3 tiers |
| Common Components | ✅ Complete | 13 generic components |
| Common Hooks | ✅ Complete | 12 utility and data hooks |
| Common Utils | ✅ Complete | 7 utility modules |
| Auth Feature | ✅ Complete | Login, register, forgot password |
| User Feature | ✅ Complete | CRUD, table, filter, form |
| Order Feature | ⚠️ Placeholder | Structure only |
| Layouts | ✅ Complete | Main, Auth, Empty layouts |
| Pages | ✅ Complete | Auth, user, dashboard pages |
| Store | ✅ Complete | Auth, UI, User slices |
| Queries | ✅ Complete | User, Order, Product queries |

## Technology Stack

- **Framework**: React 18+ with TypeScript
- **Build Tool**: Vite
- **Styling**: Tailwind CSS
- **State Management**: Redux Toolkit / Zustand
- **Data Fetching**: React Query (TanStack Query)
- **Forms**: React Hook Form
- **Validation**: Zod / Yup
- **HTTP Client**: Axios
- **Routing**: React Router v6

## Getting Started

1. **Explore the structure**:

   ```bash
   cat DIRECTORY_TREE.md
   ```

2. **Understand the architecture**:

   ```bash
   cat ARCHITECTURE.md
   ```

3. **Check specific feature documentation**:

   ```bash
   cat src/features/auth/README.md
   cat src/features/user/README.md
   ```

4. **View component documentation**:

   ```bash
   cat src/common/components/data-display/DataTable/README.md
   ```

## File Naming Conventions

- Components: `UserTable.tsx` (PascalCase)
- Hooks: `useUserTable.ts` (camelCase with use prefix)
- Utils: `date.util.ts` (camelCase with .util suffix)
- Types: `User.types.ts` (PascalCase with .types suffix)
- Constants: `user.constants.ts` (camelCase with .constants suffix)
- Services: `auth.service.ts` (camelCase with .service suffix)

## Best Practices

1. **Components**: Keep small, single responsibility
2. **Hooks**: Extract business logic from components
3. **Types**: Use strict TypeScript, no any
4. **Imports**: Use path aliases (@/)
5. **State**: Server state in React Query, global in store, local in useState
6. **Testing**: Unit tests for utils/hooks, integration for features
7. **Documentation**: README in each feature directory

## Contributing

When adding new features:

1. Follow the feature-based structure
2. Create complete directory structure (api, models, components, etc.)
3. Add index.ts for clean exports
4. Include README.md with feature documentation
5. Add types and constants files
6. Write tests alongside implementation

## Maintenance Notes

- All directories have been created
- Index files are in place for clean imports
- README files exist for major sections
- Empty directories have .gitkeep files
- Structure follows the proposed architecture exactly

---

For detailed information, see the linked documentation files above.

Generated: 2025-11-07
