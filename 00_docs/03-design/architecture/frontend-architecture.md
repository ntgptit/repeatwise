# Frontend Architecture - RepeatWise MVP

## 1. Overview

This document provides comprehensive frontend architecture design for RepeatWise MVP, covering both Web (React + TypeScript + Vite) and Mobile (React Native) applications.

**Key Design Principles**:
- **Component-based architecture**: Reusable, composable UI components
- **Type safety**: TypeScript for compile-time safety
- **State management separation**: Server state (React Query) vs Client state (Context/Zustand)
- **API-first**: Clean integration with backend REST API
- **Performance**: Code splitting, lazy loading, optimized rendering
- **User experience**: Loading states, error handling, responsive design

---

## 2. Technology Stack

### 2.1 Web Application

| Category | Technology | Purpose |
|----------|-----------|---------|
| **Core** | React 18 | UI library with concurrent features |
| **Language** | TypeScript 5.x | Type safety and better DX |
| **Build Tool** | Vite | Fast dev server and optimized builds |
| **State Management** | TanStack Query v5 | Server state management |
| | Context API | Authentication state |
| | Zustand (optional) | UI state (sidebar, modals) |
| **Routing** | React Router v6 | Client-side routing |
| **HTTP Client** | Axios | API calls with interceptors |
| **Styling** | Tailwind CSS | Utility-first CSS framework |
| **UI Components** | Shadcn/ui | Pre-built accessible components |
| **Forms** | React Hook Form | Form management |
| **Validation** | Zod | Schema validation |
| **i18n** | react-i18next | Internationalization (VI/EN) |

### 2.2 Mobile Application

| Category | Technology | Purpose |
|----------|-----------|---------|
| **Core** | React Native 0.73+ | Cross-platform mobile framework |
| **Language** | TypeScript 5.x | Type safety |
| **Navigation** | React Navigation v6 | Native navigation |
| **State Management** | TanStack Query v5 | Server state management |
| | Context API | Authentication state |
| **HTTP Client** | Axios | API calls |
| **UI Library** | React Native Paper | Material Design components |
| **Forms** | React Hook Form | Form management |
| **i18n** | i18n-js | Internationalization |
| **Notifications** | React Native Firebase | Push notifications |

---

## 3. Project Structure

### 3.1 Web Project Structure

```
frontend-web/
├── public/                      # Static assets
│   ├── favicon.ico
│   └── locales/                 # i18n translation files
│       ├── en/
│       │   └── translation.json
│       └── vi/
│           └── translation.json
├── src/
│   ├── main.tsx                 # Application entry point
│   ├── App.tsx                  # Root component
│   ├── vite-env.d.ts            # Vite types
│   │
│   ├── assets/                  # Images, icons, fonts
│   │   ├── images/
│   │   └── icons/
│   │
│   ├── components/              # Reusable UI components
│   │   ├── ui/                  # Shadcn/ui components
│   │   │   ├── button.tsx
│   │   │   ├── input.tsx
│   │   │   ├── dialog.tsx
│   │   │   ├── card.tsx
│   │   │   ├── dropdown-menu.tsx
│   │   │   ├── toast.tsx
│   │   │   └── ...
│   │   ├── common/              # Custom common components
│   │   │   ├── Header.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   ├── Breadcrumb.tsx
│   │   │   ├── LoadingSpinner.tsx
│   │   │   ├── ErrorBoundary.tsx
│   │   │   └── EmptyState.tsx
│   │   ├── folder/              # Folder-specific components
│   │   │   ├── FolderTree.tsx
│   │   │   ├── FolderTreeNode.tsx
│   │   │   ├── FolderCard.tsx
│   │   │   ├── FolderCreateDialog.tsx
│   │   │   ├── FolderEditDialog.tsx
│   │   │   ├── FolderMoveDialog.tsx
│   │   │   └── FolderStats.tsx
│   │   ├── deck/                # Deck-specific components
│   │   │   ├── DeckList.tsx
│   │   │   ├── DeckCard.tsx
│   │   │   ├── DeckCreateDialog.tsx
│   │   │   ├── DeckEditDialog.tsx
│   │   │   └── DeckImportDialog.tsx
│   │   ├── card/                # Card-specific components
│   │   │   ├── CardList.tsx
│   │   │   ├── CardItem.tsx
│   │   │   ├── CardEditor.tsx
│   │   │   └── CardPreview.tsx
│   │   └── review/              # Review session components
│   │       ├── ReviewCard.tsx
│   │       ├── RatingButtons.tsx
│   │       ├── ReviewProgress.tsx
│   │       └── ReviewSummary.tsx
│   │
│   ├── pages/                   # Page components (routes)
│   │   ├── Auth/
│   │   │   ├── LoginPage.tsx
│   │   │   └── RegisterPage.tsx
│   │   ├── Dashboard/
│   │   │   └── DashboardPage.tsx
│   │   ├── Folder/
│   │   │   ├── FolderListPage.tsx
│   │   │   └── FolderDetailPage.tsx
│   │   ├── Deck/
│   │   │   ├── DeckListPage.tsx
│   │   │   └── DeckDetailPage.tsx
│   │   ├── Review/
│   │   │   └── ReviewSessionPage.tsx
│   │   ├── Settings/
│   │   │   ├── SettingsPage.tsx
│   │   │   ├── ProfileSettings.tsx
│   │   │   └── SRSSettings.tsx
│   │   └── Stats/
│   │       └── StatsPage.tsx
│   │
│   ├── services/                # API service layer
│   │   ├── api.ts               # Axios instance with interceptors
│   │   ├── authService.ts       # Authentication API
│   │   ├── folderService.ts     # Folder CRUD + operations
│   │   ├── deckService.ts       # Deck CRUD + operations
│   │   ├── cardService.ts       # Card CRUD
│   │   ├── reviewService.ts     # Review session API
│   │   ├── statsService.ts      # Statistics API
│   │   └── importExportService.ts # Import/Export API
│   │
│   ├── hooks/                   # Custom React hooks
│   │   ├── useAuth.ts           # Auth context hook
│   │   ├── useFolder.ts         # Folder queries & mutations
│   │   ├── useDeck.ts           # Deck queries & mutations
│   │   ├── useCard.ts           # Card queries & mutations
│   │   ├── useReview.ts         # Review queries & mutations
│   │   ├── useStats.ts          # Statistics queries
│   │   ├── useTheme.ts          # Theme management hook
│   │   └── useDebounce.ts       # Debounce utility hook
│   │
│   ├── contexts/                # React Context providers
│   │   ├── AuthContext.tsx      # Auth state & actions
│   │   └── SettingsContext.tsx  # User settings
│   │
│   ├── store/                   # Zustand stores (optional UI state)
│   │   └── uiStore.ts           # Sidebar, modals, theme state
│   │
│   ├── lib/                     # Utility libraries
│   │   ├── utils.ts             # Common utility functions (cn, etc.)
│   │   ├── queryClient.ts       # React Query client configuration
│   │   └── i18n.ts              # i18n configuration
│   │
│   ├── types/                   # TypeScript type definitions
│   │   ├── api.ts               # API request/response types
│   │   ├── entities.ts          # Domain entity types
│   │   └── common.ts            # Common shared types
│   │
│   ├── constants/               # Application constants
│   │   ├── routes.ts            # Route paths
│   │   ├── api.ts               # API endpoints
│   │   └── config.ts            # App configuration
│   │
│   └── styles/                  # Global styles
│       ├── globals.css          # Tailwind imports + global styles
│       └── themes.css           # Theme variables
│
├── index.html                   # HTML entry point
├── tailwind.config.js           # Tailwind configuration
├── components.json              # Shadcn/ui configuration
├── tsconfig.json                # TypeScript configuration
├── vite.config.ts               # Vite build configuration
└── package.json                 # Dependencies
```

### 3.2 Mobile Project Structure

```
frontend-mobile/
├── src/
│   ├── App.tsx                  # Root component
│   │
│   ├── components/              # Reusable UI components
│   │   ├── common/              # Common components
│   │   │   ├── Button.tsx
│   │   │   ├── Input.tsx
│   │   │   ├── Card.tsx
│   │   │   ├── LoadingSpinner.tsx
│   │   │   └── EmptyState.tsx
│   │   ├── folder/              # Folder components
│   │   │   ├── FolderTreeView.tsx
│   │   │   ├── FolderItem.tsx
│   │   │   └── FolderSheet.tsx
│   │   ├── deck/                # Deck components
│   │   │   ├── DeckList.tsx
│   │   │   └── DeckCard.tsx
│   │   ├── card/                # Card components
│   │   │   ├── CardList.tsx
│   │   │   └── CardItem.tsx
│   │   └── review/              # Review components
│   │       ├── ReviewCard.tsx
│   │       └── RatingButtons.tsx
│   │
│   ├── screens/                 # Screen components (navigation destinations)
│   │   ├── Auth/
│   │   │   ├── LoginScreen.tsx
│   │   │   └── RegisterScreen.tsx
│   │   ├── Home/
│   │   │   └── HomeScreen.tsx
│   │   ├── Folder/
│   │   │   ├── FolderListScreen.tsx
│   │   │   └── FolderDetailScreen.tsx
│   │   ├── Deck/
│   │   │   ├── DeckListScreen.tsx
│   │   │   └── DeckDetailScreen.tsx
│   │   ├── Review/
│   │   │   └── ReviewSessionScreen.tsx
│   │   ├── Settings/
│   │   │   └── SettingsScreen.tsx
│   │   └── Stats/
│   │       └── StatsScreen.tsx
│   │
│   ├── navigation/              # React Navigation setup
│   │   ├── AppNavigator.tsx     # Root navigator
│   │   ├── AuthNavigator.tsx    # Auth stack
│   │   └── MainNavigator.tsx    # Main tab navigator
│   │
│   ├── services/                # API service layer (same as web)
│   ├── hooks/                   # Custom hooks (same as web)
│   ├── contexts/                # Context providers (same as web)
│   ├── store/                   # Zustand stores (optional)
│   ├── types/                   # TypeScript types (shared with web)
│   ├── constants/               # Constants (same as web)
│   ├── utils/                   # Utility functions
│   └── notifications/           # Push notification service
│       └── notificationService.ts
│
├── android/                     # Android native code
├── ios/                         # iOS native code
├── tsconfig.json
└── package.json
```

---

## 4. State Management Architecture ⭐

### 4.1 Why NOT Redux for MVP

**Decision**: Use TanStack Query + Context API + Zustand instead of Redux

**Reasons**:
1. **App complexity is manageable**: 6-7 screens, limited shared state
2. **Redux overhead**: ~300 lines boilerplate (store, slices, actions, reducers)
3. **React Query handles server state better**: Built-in caching, refetching, optimistic updates
4. **Bundle size**: Redux Toolkit ~15KB vs React Query ~5KB + Zustand ~1KB
5. **Better Developer Experience**: Less code, easier to learn and maintain
6. **Easier migration**: Can migrate to Redux later if needed

### 4.2 State Management Strategy

| State Type | Technology | Use Cases | Examples |
|------------|-----------|-----------|----------|
| **Server State** | TanStack Query | API data, caching, sync | Folders, decks, cards, reviews, stats |
| **Authentication State** | Context API | User, login status, logout | Current user, access token, auth checks |
| **UI State** | Zustand (optional) | Ephemeral UI state | Sidebar open/closed, modal state, theme |
| **Form State** | React Hook Form | Form values, validation | Create folder, edit card forms |

### 4.3 React Query Configuration

**File**: `src/lib/queryClient.ts`

```typescript
import { QueryClient } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // Stale time: Data considered fresh for 5 minutes
      staleTime: 5 * 60 * 1000,

      // Cache time: Keep unused data in cache for 10 minutes
      gcTime: 10 * 60 * 1000,

      // Retry failed queries 1 time
      retry: 1,

      // Retry delay: exponential backoff
      retryDelay: (attemptIndex) => Math.min(1000 * 2 ** attemptIndex, 30000),

      // Refetch on window focus (disabled for MVP)
      refetchOnWindowFocus: false,

      // Refetch on mount if stale
      refetchOnMount: true,

      // Refetch on reconnect
      refetchOnReconnect: true,
    },
    mutations: {
      // Retry mutations 0 times (fail fast for user actions)
      retry: 0,

      // Optimistic update rollback on error
      onError: (error, variables, context) => {
        // Rollback logic
        if (context?.previousData) {
          queryClient.setQueryData(context.queryKey, context.previousData);
        }
      },
    },
  },
});
```

**App Setup**: `src/App.tsx`

```typescript
import { QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { queryClient } from './lib/queryClient';

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
      {import.meta.env.DEV && <ReactQueryDevtools />}
    </QueryClientProvider>
  );
}
```

### 4.4 React Query Usage Patterns

**Example: Folder Hooks** (`src/hooks/useFolder.ts`)

```typescript
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { folderService } from '@/services/folderService';
import type { Folder, CreateFolderRequest } from '@/types/api';

// Query Keys (centralized)
export const folderKeys = {
  all: ['folders'] as const,
  lists: () => [...folderKeys.all, 'list'] as const,
  list: (filters: string) => [...folderKeys.lists(), { filters }] as const,
  details: () => [...folderKeys.all, 'detail'] as const,
  detail: (id: string) => [...folderKeys.details(), id] as const,
  tree: () => [...folderKeys.all, 'tree'] as const,
  stats: (id: string) => [...folderKeys.all, 'stats', id] as const,
};

// Fetch folder tree
export function useFolderTree() {
  return useQuery({
    queryKey: folderKeys.tree(),
    queryFn: () => folderService.getFolderTree(),
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
}

// Fetch folder details
export function useFolder(folderId: string) {
  return useQuery({
    queryKey: folderKeys.detail(folderId),
    queryFn: () => folderService.getFolder(folderId),
    enabled: !!folderId, // Only fetch if folderId exists
  });
}

// Create folder mutation
export function useCreateFolder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateFolderRequest) => folderService.createFolder(data),

    // Optimistic update
    onMutate: async (newFolder) => {
      // Cancel outgoing queries
      await queryClient.cancelQueries({ queryKey: folderKeys.tree() });

      // Snapshot previous value
      const previousTree = queryClient.getQueryData(folderKeys.tree());

      // Optimistically update
      queryClient.setQueryData(folderKeys.tree(), (old: Folder[]) => [
        ...old,
        { ...newFolder, id: 'temp-id' }, // Temporary ID
      ]);

      return { previousTree };
    },

    // On error: rollback
    onError: (err, newFolder, context) => {
      if (context?.previousTree) {
        queryClient.setQueryData(folderKeys.tree(), context.previousTree);
      }
    },

    // On success: invalidate and refetch
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: folderKeys.tree() });
      queryClient.invalidateQueries({ queryKey: folderKeys.lists() });
    },
  });
}

// Delete folder mutation
export function useDeleteFolder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (folderId: string) => folderService.deleteFolder(folderId),

    onSuccess: () => {
      // Invalidate all folder queries
      queryClient.invalidateQueries({ queryKey: folderKeys.all });
    },
  });
}

// Folder copy with async job polling
export function useCopyFolder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      folderId,
      destinationId
    }: {
      folderId: string;
      destinationId: string;
    }) => {
      const result = await folderService.copyFolder(folderId, destinationId);

      // If async job, poll for status
      if (result.jobId) {
        return pollJobStatus(result.jobId);
      }

      return result;
    },

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: folderKeys.all });
    },
  });
}

// Poll job status helper
async function pollJobStatus(jobId: string): Promise<any> {
  const maxAttempts = 60; // 5 minutes (5s interval)
  let attempts = 0;

  while (attempts < maxAttempts) {
    const status = await folderService.getCopyStatus(jobId);

    if (status.status === 'COMPLETED') {
      return status;
    }

    if (status.status === 'FAILED') {
      throw new Error(status.message);
    }

    // Wait 5 seconds before next poll
    await new Promise(resolve => setTimeout(resolve, 5000));
    attempts++;
  }

  throw new Error('Job timeout');
}
```

### 4.5 Auth Context

**File**: `src/contexts/AuthContext.tsx`

```typescript
import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '@/services/authService';
import type { User } from '@/types/entities';

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  register: (email: string, password: string, name: string) => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  // Initialize auth state on mount
  useEffect(() => {
    const initAuth = async () => {
      try {
        const token = authService.getAccessToken();
        if (token) {
          const userData = await authService.getCurrentUser();
          setUser(userData);
        }
      } catch (error) {
        // Token invalid, clear it
        authService.clearTokens();
      } finally {
        setIsLoading(false);
      }
    };

    initAuth();
  }, []);

  const login = async (email: string, password: string) => {
    const response = await authService.login(email, password);
    authService.setAccessToken(response.accessToken);
    setUser(response.user);
  };

  const logout = async () => {
    await authService.logout();
    authService.clearTokens();
    setUser(null);
  };

  const register = async (email: string, password: string, name: string) => {
    await authService.register(email, password, name);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        logout,
        register
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
```

### 4.6 Zustand UI Store (Optional)

**File**: `src/store/uiStore.ts`

```typescript
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface UIState {
  // Sidebar
  sidebarOpen: boolean;
  toggleSidebar: () => void;

  // Theme
  theme: 'light' | 'dark' | 'system';
  setTheme: (theme: 'light' | 'dark' | 'system') => void;

  // Modal state
  activeModal: string | null;
  openModal: (modalId: string) => void;
  closeModal: () => void;
}

export const useUIStore = create<UIState>()(
  persist(
    (set) => ({
      // Sidebar
      sidebarOpen: true,
      toggleSidebar: () => set((state) => ({ sidebarOpen: !state.sidebarOpen })),

      // Theme
      theme: 'system',
      setTheme: (theme) => set({ theme }),

      // Modal
      activeModal: null,
      openModal: (modalId) => set({ activeModal: modalId }),
      closeModal: () => set({ activeModal: null }),
    }),
    {
      name: 'repeatwise-ui', // localStorage key
      partialize: (state) => ({
        theme: state.theme,
        sidebarOpen: state.sidebarOpen
      }),
    }
  )
);
```

---

## 5. API Integration

### 5.1 Axios Instance Configuration

**File**: `src/services/api.ts`

```typescript
import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { authService } from './authService';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// Create axios instance
export const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000, // 30 seconds
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Send cookies (for refresh token)
});

// Request interceptor: Add auth token
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = authService.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Response interceptor: Handle token refresh
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value?: unknown) => void;
  reject: (reason?: unknown) => void;
}> = [];

const processQueue = (error: Error | null, token: string | null = null) => {
  failedQueue.forEach((promise) => {
    if (error) {
      promise.reject(error);
    } else {
      promise.resolve(token);
    }
  });
  failedQueue = [];
};

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    // If 401 and not already retrying
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // Queue this request while refresh is in progress
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return api(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        // Refresh token
        const response = await authService.refreshToken();
        const newAccessToken = response.accessToken;

        // Update token
        authService.setAccessToken(newAccessToken);

        // Update original request with new token
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

        // Process queued requests
        processQueue(null, newAccessToken);

        // Retry original request
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh failed, logout user
        processQueue(refreshError as Error, null);
        authService.clearTokens();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export default api;
```

### 5.2 Auth Service with Token Management

**File**: `src/services/authService.ts`

```typescript
import api from './api';
import type { LoginResponse, User } from '@/types/api';

// In-memory token storage (more secure than localStorage)
let accessToken: string | null = null;

export const authService = {
  // Login
  async login(email: string, password: string): Promise<LoginResponse> {
    const response = await api.post<LoginResponse>('/auth/login', {
      email,
      password,
    });
    // Refresh token automatically stored in HTTP-only cookie
    return response.data;
  },

  // Register
  async register(email: string, password: string, name: string): Promise<void> {
    await api.post('/auth/register', {
      email,
      password,
      name,
    });
  },

  // Logout
  async logout(): Promise<void> {
    try {
      await api.post('/auth/logout');
    } finally {
      this.clearTokens();
    }
  },

  // Refresh token
  async refreshToken(): Promise<{ accessToken: string }> {
    // Refresh token sent automatically in cookie
    const response = await api.post<{ accessToken: string }>('/auth/refresh');
    return response.data;
  },

  // Get current user
  async getCurrentUser(): Promise<User> {
    const response = await api.get<User>('/auth/me');
    return response.data;
  },

  // Token management (in-memory)
  getAccessToken(): string | null {
    return accessToken;
  },

  setAccessToken(token: string): void {
    accessToken = token;
  },

  clearTokens(): void {
    accessToken = null;
  },
};
```

**Security Benefits**:
- Access token stored in memory (not localStorage) - prevents XSS theft
- Refresh token in HTTP-only cookie - prevents XSS access
- Automatic token refresh with request queuing
- Token cleared on logout or refresh failure

---

## 6. Routing

### 6.1 React Router v6 Setup (Web)

**File**: `src/main.tsx`

```typescript
import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import { ProtectedRoute } from '@/components/common/ProtectedRoute';

// Pages
import LoginPage from '@/pages/Auth/LoginPage';
import RegisterPage from '@/pages/Auth/RegisterPage';
import DashboardPage from '@/pages/Dashboard/DashboardPage';
import FolderListPage from '@/pages/Folder/FolderListPage';
import FolderDetailPage from '@/pages/Folder/FolderDetailPage';
import DeckDetailPage from '@/pages/Deck/DeckDetailPage';
import ReviewSessionPage from '@/pages/Review/ReviewSessionPage';
import SettingsPage from '@/pages/Settings/SettingsPage';
import StatsPage from '@/pages/Stats/StatsPage';

const router = createBrowserRouter([
  // Public routes
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/register',
    element: <RegisterPage />,
  },

  // Protected routes
  {
    path: '/',
    element: <ProtectedRoute />,
    children: [
      {
        index: true,
        element: <DashboardPage />,
      },
      {
        path: 'folders',
        element: <FolderListPage />,
      },
      {
        path: 'folders/:folderId',
        element: <FolderDetailPage />,
      },
      {
        path: 'decks/:deckId',
        element: <DeckDetailPage />,
      },
      {
        path: 'review',
        element: <ReviewSessionPage />,
      },
      {
        path: 'settings',
        element: <SettingsPage />,
      },
      {
        path: 'stats',
        element: <StatsPage />,
      },
    ],
  },
]);

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
    </QueryClientProvider>
  </React.StrictMode>
);
```

**Protected Route Component**:

```typescript
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

export function ProtectedRoute() {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}
```

### 6.2 React Navigation (Mobile)

**File**: `src/navigation/AppNavigator.tsx`

```typescript
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { useAuth } from '@/contexts/AuthContext';
import AuthNavigator from './AuthNavigator';
import MainNavigator from './MainNavigator';

const Stack = createNativeStackNavigator();

export default function AppNavigator() {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <LoadingScreen />;
  }

  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{ headerShown: false }}>
        {!isAuthenticated ? (
          <Stack.Screen name="Auth" component={AuthNavigator} />
        ) : (
          <Stack.Screen name="Main" component={MainNavigator} />
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}
```

**Main Tab Navigator**:

```typescript
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import HomeScreen from '@/screens/Home/HomeScreen';
import FolderListScreen from '@/screens/Folder/FolderListScreen';
import ReviewSessionScreen from '@/screens/Review/ReviewSessionScreen';
import SettingsScreen from '@/screens/Settings/SettingsScreen';

const Tab = createBottomTabNavigator();

export default function MainNavigator() {
  return (
    <Tab.Navigator>
      <Tab.Screen name="Home" component={HomeScreen} />
      <Tab.Screen name="Folders" component={FolderListScreen} />
      <Tab.Screen name="Review" component={ReviewSessionScreen} />
      <Tab.Screen name="Settings" component={SettingsScreen} />
    </Tab.Navigator>
  );
}
```

---

## 7. Error Handling

### 7.1 Error Boundary

**File**: `src/components/common/ErrorBoundary.tsx`

```typescript
import React, { Component, ErrorInfo, ReactNode } from 'react';

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

export class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
    // Send to error tracking service (Sentry, etc.)
  }

  render() {
    if (this.state.hasError) {
      return this.props.fallback || (
        <div className="error-boundary">
          <h2>Something went wrong</h2>
          <p>{this.state.error?.message}</p>
          <button onClick={() => window.location.reload()}>
            Reload Page
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}
```

### 7.2 API Error Handling

**File**: `src/lib/errorHandler.ts`

```typescript
import { AxiosError } from 'axios';
import { toast } from 'sonner'; // or your toast library

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  traceId?: string;
}

export function handleApiError(error: unknown): ApiError | null {
  if (error instanceof AxiosError) {
    const apiError = error.response?.data as ApiError;

    // Show user-friendly toast
    if (apiError?.message) {
      toast.error(apiError.message);
    } else {
      toast.error('An unexpected error occurred');
    }

    return apiError;
  }

  // Unknown error
  toast.error('An unexpected error occurred');
  return null;
}
```

**Usage in Hooks**:

```typescript
export function useCreateFolder() {
  return useMutation({
    mutationFn: (data: CreateFolderRequest) => folderService.createFolder(data),
    onError: (error) => {
      handleApiError(error);
    },
    onSuccess: () => {
      toast.success('Folder created successfully');
    },
  });
}
```

---

## 8. Component Structure

### 8.1 Component Organization Principles

1. **Separation of Concerns**: Smart (container) vs Dumb (presentational) components
2. **Single Responsibility**: Each component has one clear purpose
3. **Composition over Inheritance**: Build complex UIs with simple components
4. **Props Interface**: Clear TypeScript interfaces for all props
5. **Default Props**: Use TypeScript default parameters

### 8.2 Example: Folder Tree Component

**Smart Component**: `src/pages/Folder/FolderListPage.tsx`

```typescript
import { useFolderTree } from '@/hooks/useFolder';
import { FolderTree } from '@/components/folder/FolderTree';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { ErrorState } from '@/components/common/ErrorState';

export default function FolderListPage() {
  const { data: folders, isLoading, error } = useFolderTree();

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return <ErrorState message="Failed to load folders" />;
  }

  return (
    <div className="folder-list-page">
      <header>
        <h1>My Folders</h1>
        <CreateFolderButton />
      </header>

      <FolderTree folders={folders || []} />
    </div>
  );
}
```

**Presentational Component**: `src/components/folder/FolderTree.tsx`

```typescript
import { FolderTreeNode } from './FolderTreeNode';
import type { Folder } from '@/types/entities';

interface FolderTreeProps {
  folders: Folder[];
  onFolderClick?: (folder: Folder) => void;
}

export function FolderTree({ folders, onFolderClick }: FolderTreeProps) {
  if (folders.length === 0) {
    return (
      <EmptyState
        title="No folders yet"
        description="Create your first folder to get started"
        action={<CreateFolderButton />}
      />
    );
  }

  return (
    <div className="folder-tree">
      {folders.map((folder) => (
        <FolderTreeNode
          key={folder.id}
          folder={folder}
          onClick={onFolderClick}
        />
      ))}
    </div>
  );
}
```

---

## 9. Performance Optimization

### 9.1 Code Splitting & Lazy Loading

**Route-based Code Splitting**:

```typescript
import { lazy, Suspense } from 'react';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

// Lazy load pages
const DashboardPage = lazy(() => import('@/pages/Dashboard/DashboardPage'));
const FolderListPage = lazy(() => import('@/pages/Folder/FolderListPage'));
const ReviewSessionPage = lazy(() => import('@/pages/Review/ReviewSessionPage'));

// Wrap with Suspense
function App() {
  return (
    <Suspense fallback={<LoadingSpinner />}>
      <RouterProvider router={router} />
    </Suspense>
  );
}
```

### 9.2 React Query Optimizations

**Prefetching**:

```typescript
// Prefetch folder details on hover
function FolderCard({ folder }: { folder: Folder }) {
  const queryClient = useQueryClient();

  const handleMouseEnter = () => {
    queryClient.prefetchQuery({
      queryKey: folderKeys.detail(folder.id),
      queryFn: () => folderService.getFolder(folder.id),
    });
  };

  return (
    <div onMouseEnter={handleMouseEnter}>
      {/* ... */}
    </div>
  );
}
```

**Pagination**:

```typescript
function DeckList() {
  const [page, setPage] = useState(1);

  const { data, isLoading } = useQuery({
    queryKey: ['decks', { page, limit: 50 }],
    queryFn: () => deckService.getDecks({ page, limit: 50 }),
    keepPreviousData: true, // Keep previous page data while loading
  });

  return (
    <div>
      {data?.decks.map(deck => <DeckCard key={deck.id} deck={deck} />)}
      <Pagination page={page} onChange={setPage} />
    </div>
  );
}
```

### 9.3 Virtual Scrolling

**For Large Lists** (using `react-window`):

```typescript
import { FixedSizeList } from 'react-window';

function CardList({ cards }: { cards: Card[] }) {
  const Row = ({ index, style }: { index: number; style: React.CSSProperties }) => (
    <div style={style}>
      <CardItem card={cards[index]} />
    </div>
  );

  return (
    <FixedSizeList
      height={600}
      itemCount={cards.length}
      itemSize={80}
      width="100%"
    >
      {Row}
    </FixedSizeList>
  );
}
```

### 9.4 Debouncing

**Search Input**:

```typescript
import { useState, useEffect } from 'react';
import { useDebounce } from '@/hooks/useDebounce';

function SearchBar() {
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearchTerm = useDebounce(searchTerm, 300); // 300ms delay

  const { data } = useQuery({
    queryKey: ['search', debouncedSearchTerm],
    queryFn: () => api.search(debouncedSearchTerm),
    enabled: debouncedSearchTerm.length > 2,
  });

  return (
    <input
      type="text"
      value={searchTerm}
      onChange={(e) => setSearchTerm(e.target.value)}
      placeholder="Search..."
    />
  );
}
```

**Debounce Hook**:

```typescript
import { useState, useEffect } from 'react';

export function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
}
```

---

## 10. Theme Implementation

### 10.1 Tailwind Dark Mode

**File**: `tailwind.config.js`

```javascript
module.exports = {
  darkMode: 'class', // Use class-based dark mode
  content: ['./src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        background: 'var(--background)',
        foreground: 'var(--foreground)',
        primary: {
          DEFAULT: 'var(--primary)',
          foreground: 'var(--primary-foreground)',
        },
        // ... more theme colors
      },
    },
  },
  plugins: [],
};
```

**CSS Variables**: `src/styles/globals.css`

```css
@tailwind base;
@tailwind components;
@tailwind utilities;

@layer base {
  :root {
    --background: 0 0% 100%;
    --foreground: 222.2 84% 4.9%;
    --primary: 221.2 83.2% 53.3%;
    --primary-foreground: 210 40% 98%;
    /* ... more variables */
  }

  .dark {
    --background: 222.2 84% 4.9%;
    --foreground: 210 40% 98%;
    --primary: 217.2 91.2% 59.8%;
    --primary-foreground: 222.2 47.4% 11.2%;
    /* ... more variables */
  }
}
```

### 10.2 Theme Toggle Hook

**File**: `src/hooks/useTheme.ts`

```typescript
import { useEffect } from 'react';
import { useUIStore } from '@/store/uiStore';

export function useTheme() {
  const { theme, setTheme } = useUIStore();

  useEffect(() => {
    const root = window.document.documentElement;

    root.classList.remove('light', 'dark');

    if (theme === 'system') {
      const systemTheme = window.matchMedia('(prefers-color-scheme: dark)')
        .matches ? 'dark' : 'light';
      root.classList.add(systemTheme);
    } else {
      root.classList.add(theme);
    }
  }, [theme]);

  return { theme, setTheme };
}
```

**Theme Toggle Component**:

```typescript
import { Moon, Sun, Monitor } from 'lucide-react';
import { useTheme } from '@/hooks/useTheme';

export function ThemeToggle() {
  const { theme, setTheme } = useTheme();

  return (
    <div className="theme-toggle">
      <button
        onClick={() => setTheme('light')}
        className={theme === 'light' ? 'active' : ''}
      >
        <Sun />
      </button>
      <button
        onClick={() => setTheme('dark')}
        className={theme === 'dark' ? 'active' : ''}
      >
        <Moon />
      </button>
      <button
        onClick={() => setTheme('system')}
        className={theme === 'system' ? 'active' : ''}
      >
        <Monitor />
      </button>
    </div>
  );
}
```

---

## 11. Testing Strategy

### 11.1 Unit Testing (Vitest + React Testing Library)

**Test File**: `src/hooks/useFolder.test.ts`

```typescript
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useFolderTree } from './useFolder';
import { folderService } from '@/services/folderService';

// Mock service
vi.mock('@/services/folderService');

function createWrapper() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });

  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
}

describe('useFolderTree', () => {
  it('fetches folder tree successfully', async () => {
    const mockFolders = [
      { id: '1', name: 'Folder 1' },
      { id: '2', name: 'Folder 2' },
    ];

    vi.mocked(folderService.getFolderTree).mockResolvedValue(mockFolders);

    const { result } = renderHook(() => useFolderTree(), {
      wrapper: createWrapper(),
    });

    expect(result.current.isLoading).toBe(true);

    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });

    expect(result.current.data).toEqual(mockFolders);
  });
});
```

---

## 12. Build & Deployment

### 12.1 Vite Build Configuration

**File**: `vite.config.ts`

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: true,
    rollupOptions: {
      output: {
        manualChunks: {
          // Split vendor chunks
          'react-vendor': ['react', 'react-dom', 'react-router-dom'],
          'query-vendor': ['@tanstack/react-query'],
          'ui-vendor': ['@radix-ui/react-dialog', '@radix-ui/react-dropdown-menu'],
        },
      },
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

### 12.2 Environment Variables

**File**: `.env.example`

```bash
# API Base URL
VITE_API_BASE_URL=http://localhost:8080/api

# Environment
VITE_ENV=development
```

---

## 13. Conclusion

This frontend architecture provides:

1. **Type Safety**: TypeScript throughout
2. **Clean State Management**: React Query + Context + Zustand (no Redux overhead)
3. **Secure Auth**: Token refresh with automatic retry
4. **Performance**: Code splitting, lazy loading, caching, virtual scrolling
5. **Developer Experience**: Fast dev server (Vite), hot reload, devtools
6. **Maintainability**: Clear structure, separation of concerns, testable code
7. **User Experience**: Loading states, error handling, responsive design

**Key Decisions**:
- ✅ React Query over Redux for server state
- ✅ Context API for auth (simple, sufficient)
- ✅ Zustand for UI state (optional, lightweight)
- ✅ Axios with interceptors for token refresh
- ✅ Vite for fast builds
- ✅ Shadcn/ui for accessible components

**Migration Path**:
- If state complexity grows: Migrate to Redux Toolkit
- If real-time needed: Add WebSocket support
- If offline needed: Add Service Worker + IndexedDB
