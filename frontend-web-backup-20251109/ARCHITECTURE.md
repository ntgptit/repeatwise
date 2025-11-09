# Frontend Architecture Guide

## Overview

This document provides a comprehensive guide to the frontend architecture of the RepeatWise application. The structure follows modern best practices including atomic design, feature-based architecture, and clear separation of concerns.

## Architecture Principles

### 1. Separation of Concerns
- **API Layer**: Handles all HTTP communication
- **Business Logic**: Lives in features and hooks
- **UI Components**: Pure presentation in design-system
- **State Management**: Centralized in store and queries

### 2. Feature-Based Structure
Each feature is self-contained with all necessary code.

### 3. Design System
Three-tier component hierarchy:
- **Primitives**: Atomic UI elements (Button, Input)
- **Patterns**: Composite components (Form, Modal)
- **Widgets**: Complex components (Calendar, Editor)

### 4. Common Layer
Shared, reusable resources for generic business components, hooks, and utilities.

## Import Paths

Use TypeScript path aliases for clean imports:

```typescript
// Good
import { Button } from '@/design-system';
import { UserTable } from '@/features/user';
import { formatDate } from '@/common/utils';

// Avoid
import { Button } from '../../../../design-system/components/primitives/Button';
```

## Naming Conventions

| Type | Convention | Example |
|------|-----------|---------|
| Components | PascalCase | UserTable.tsx |
| Hooks | camelCase + use | useUserTable.ts |
| Utils | camelCase + .util | date.util.ts |
| Types | PascalCase + .types | User.types.ts |
| Constants | camelCase + .constants | user.constants.ts |
| Services | camelCase + .service | auth.service.ts |

## Data Flow

### 1. Server State (React Query)
API → React Query → Component

Use for: Server data, API responses, Cached data

### 2. Global State (Redux/Zustand)
Action → Store → Component

Use for: Auth state, UI preferences, App-wide state

### 3. Local State (useState)
Component → useState → Component

Use for: Form inputs, Modal open/close, Component-specific state

## Best Practices

### Components
1. Keep components small - Single responsibility
2. Extract logic to hooks - Business logic outside JSX
3. Use TypeScript strictly - No any types
4. Prop validation - Use interfaces for props
5. Memoization - Use React.memo for expensive components

### Hooks
1. Single responsibility - One concern per hook
2. Return objects - For multiple values
3. Descriptive names - Clear purpose
4. Dependencies - Careful with useEffect deps

### API Calls
1. Use React Query - For all server state
2. Error handling - Handle errors gracefully
3. Loading states - Show loading indicators
4. Optimistic updates - For better UX

### Type Safety
1. No any - Use unknown if needed
2. Strict null checks - Handle undefined/null
3. Type inference - Let TS infer when possible
4. Generic types - For reusable code

## Testing Strategy

### Unit Tests
- Utils functions
- Hooks
- Pure components

### Integration Tests
- Feature flows
- API mocking
- User interactions

### E2E Tests
- Critical user journeys
- Full app flows

## Performance Optimization

1. Code splitting - Lazy load routes
2. Memoization - useMemo, useCallback, React.memo
3. Virtual scrolling - For large lists
4. Image optimization - Lazy load images
5. Bundle analysis - Monitor bundle size

## Next Steps for RepeatWise

### Upcoming Features to Implement:

1. **Deck Management**
   - Create features/deck/
   - CRUD operations
   - Deck statistics

2. **Card Management**
   - Create features/card/
   - Card editor
   - Import/export

3. **Folder System**
   - Create features/folder/
   - Tree view
   - Drag and drop

4. **Review System**
   - Create features/review/
   - SRS algorithm
   - Review session UI

5. **Statistics Dashboard**
   - Create features/stats/
   - Charts and graphs
   - Progress tracking

---

Last Updated: 2025-11-07
