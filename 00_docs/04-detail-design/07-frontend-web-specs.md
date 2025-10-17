# Frontend Web Application Specifications - RepeatWise MVP

## 1. Overview

### 1.1 Tech Stack
- **Core Framework**: React 18 (with concurrent features)
- **Language**: TypeScript 5.x
- **Build Tool**: Vite 5.x
- **State Management**:
  - Server State: TanStack Query v5
  - Auth State: Context API
  - UI State: Zustand
- **Routing**: React Router v6
- **HTTP Client**: Axios
- **Styling**: Tailwind CSS 3.x
- **UI Components**: Shadcn/ui (built on Radix UI)
- **Forms**: React Hook Form + Zod validation
- **i18n**: react-i18next

### 1.2 Why This Stack?
- **No Redux**: Overkill for MVP (6-7 pages), TanStack Query handles server state better
- **TanStack Query Benefits**: Auto caching, refetching, optimistic updates, less boilerplate
- **Vite Benefits**: Fast HMR (<200ms), optimized builds, better DX than CRA
- **Shadcn/ui Benefits**: Copy-paste components, full customization, no npm bloat
- **Bundle Size**: ~45KB gzipped (React + Router + Query + Zustand)

---

## 2. Application Architecture

### 2.1 Folder Structure

```
frontend-web/
├── public/
│   ├── favicon.ico
│   └── locales/
│       ├── en/
│       │   └── translation.json
│       └── vi/
│           └── translation.json
├── src/
│   ├── main.tsx                 # Entry point
│   ├── App.tsx                  # Root component
│   ├── vite-env.d.ts            # Vite types
│   │
│   ├── components/              # Reusable components
│   │   ├── ui/                  # Shadcn components (Button, Dialog, etc.)
│   │   ├── common/              # Custom common components
│   │   ├── folder/              # Folder-specific components
│   │   ├── deck/                # Deck-specific components
│   │   ├── card/                # Card-specific components
│   │   └── review/              # Review session components
│   │
│   ├── pages/                   # Page components (routes)
│   │   ├── Auth/
│   │   ├── Dashboard/
│   │   ├── Folder/
│   │   ├── Deck/
│   │   ├── Review/
│   │   ├── Settings/
│   │   └── Stats/
│   │
│   ├── services/                # API calls
│   │   ├── api.ts               # Axios instance with interceptors
│   │   ├── authService.ts
│   │   ├── folderService.ts
│   │   ├── deckService.ts
│   │   ├── cardService.ts
│   │   ├── reviewService.ts
│   │   ├── statsService.ts
│   │   └── importExportService.ts
│   │
│   ├── hooks/                   # Custom React Query hooks
│   │   ├── useAuth.ts
│   │   ├── useFolder.ts
│   │   ├── useDeck.ts
│   │   ├── useCard.ts
│   │   ├── useReview.ts
│   │   ├── useStats.ts
│   │   ├── useTheme.ts
│   │   └── useDebounce.ts
│   │
│   ├── contexts/                # React Context providers
│   │   ├── AuthContext.tsx
│   │   └── SettingsContext.tsx
│   │
│   ├── store/                   # Zustand stores
│   │   └── uiStore.ts
│   │
│   ├── lib/                     # Utilities
│   │   ├── utils.ts             # cn() helper, etc.
│   │   ├── queryClient.ts       # React Query config
│   │   └── i18n.ts              # i18n config
│   │
│   ├── types/                   # TypeScript types
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
│       ├── globals.css          # Tailwind + global styles
│       └── themes.css           # Theme variables
│
├── index.html
├── tailwind.config.js
├── components.json              # Shadcn config
├── tsconfig.json
├── vite.config.ts
└── package.json
```

### 2.2 State Management Strategy

| State Type | Technology | Use Cases | Examples |
|------------|-----------|-----------|----------|
| **Server State** | TanStack Query | API data, caching, sync | Folders, decks, cards, reviews, stats |
| **Auth State** | Context API | User, login status | Current user, access token, auth checks |
| **UI State** | Zustand | Ephemeral UI state | Sidebar open/closed, modal state, theme |
| **Form State** | React Hook Form | Form values, validation | Create folder, edit card forms |

**Server State (TanStack Query)**:
- Auto caching with configurable staleTime (5 min)
- Auto refetch on window focus
- Optimistic updates for mutations
- Query key structure: `['resource', params]`
- Invalidation on mutations

**Auth State (Context API)**:
- User object
- Access token (in-memory, NOT localStorage for security)
- Refresh token (HTTP-only cookie, managed by backend)
- Login/logout methods
- isAuthenticated flag

**UI State (Zustand)**:
- Sidebar open/closed state
- Theme (light/dark/system)
- Active modal IDs
- Toast notifications queue
- Persisted to localStorage

---

## 3. Component Specifications

### 3.1 Layout Components

#### Component: AppLayout

**Purpose**: Main application layout with sidebar and content area

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| children | ReactNode | Yes | - | Main content to render |

**State (Internal)**:
- None (uses Zustand for sidebar state)

**UI Structure**:
```tsx
<div className="flex h-screen overflow-hidden">
  <Sidebar />
  <div className="flex-1 flex flex-col">
    <Header />
    <main className="flex-1 overflow-auto p-6">
      {children}
    </main>
  </div>
</div>
```

**Styling**:
- Tailwind: `flex`, `h-screen`, `overflow-hidden`
- Responsive: sidebar collapses to drawer on mobile (<768px)
- Smooth transitions: `transition-all duration-300`

---

#### Component: Sidebar

**Purpose**: Navigation sidebar with folder tree

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| None | - | - | - | Controlled by Zustand |

**State (Internal)**:
- sidebarOpen (from Zustand)

**Data Fetching**:
- Hook: `useFolderTree()`
- Endpoint: `GET /api/folders`
- Cache key: `['folders', 'tree']`
- Refetch: On window focus

**Behavior**:
1. On mount: Fetch folder tree
2. On folder click: Navigate to folder detail page
3. On toggle: Animate width (0 → 256px)
4. On mobile: Render as drawer overlay

**UI Structure**:
```tsx
<aside className={cn(
  "bg-white dark:bg-gray-900 border-r",
  sidebarOpen ? "w-64" : "w-0"
)}>
  <div className="p-4">
    <h2>Folders</h2>
    <Button onClick={openCreateFolderDialog}>
      <PlusIcon /> New Folder
    </Button>
  </div>
  <ScrollArea className="flex-1">
    <FolderTree />
  </ScrollArea>
</aside>
```

**Styling**:
- Shadcn: `ScrollArea`, `Button`
- Icons: `lucide-react` (Folder, FolderOpen, ChevronRight, ChevronDown)
- Dark mode: automatic via Tailwind `dark:` variants

---

#### Component: Header

**Purpose**: Top navigation bar with user menu, theme toggle, breadcrumb

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| None | - | - | - | - |

**State (Internal)**:
- user (from AuthContext)

**UI Structure**:
```tsx
<header className="border-b bg-white dark:bg-gray-900 px-6 py-4">
  <div className="flex items-center justify-between">
    <div className="flex items-center gap-4">
      <Button variant="ghost" onClick={toggleSidebar}>
        <MenuIcon />
      </Button>
      <Breadcrumb />
    </div>
    <div className="flex items-center gap-4">
      <ThemeToggle />
      <DropdownMenu>
        <DropdownMenuTrigger>
          <Avatar>{user.name}</Avatar>
        </DropdownMenuTrigger>
        <DropdownMenuContent>
          <DropdownMenuItem onClick={goToSettings}>
            Settings
          </DropdownMenuItem>
          <DropdownMenuItem onClick={logout}>
            Logout
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  </div>
</header>
```

**Styling**:
- Shadcn: `DropdownMenu`, `Avatar`, `Button`
- Height: 64px fixed
- Sticky positioning on scroll

---

### 3.2 Folder Management Components

#### Component: FolderTree

**Purpose**: Display hierarchical folder tree with expand/collapse

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| selectedFolderId | string \| null | No | null | Highlighted folder |
| onSelect | (id: string) => void | No | navigate | Callback on folder click |

**State (Internal)**:
- expanded: Set<string> - Expanded folder IDs (Zustand)

**Data Fetching**:
- Hook: `useFolderTree()`
- Endpoint: `GET /api/folders`
- Cache key: `['folders', 'tree']`
- Refetch: On window focus, on mutation success

**Behavior**:
1. On mount: Fetch folders, build tree structure (client-side)
2. On folder click: Toggle expand/collapse, call onSelect
3. On folder context menu: Show actions (create, rename, move, copy, delete)
4. Tree traversal: Recursive rendering of FolderTreeNode

**UI Structure**:
```tsx
<div className="folder-tree">
  {rootFolders.map(folder => (
    <FolderTreeNode
      key={folder.id}
      folder={folder}
      depth={0}
      expanded={expanded.has(folder.id)}
      selected={folder.id === selectedFolderId}
      onToggle={toggleExpanded}
      onSelect={onSelect}
    />
  ))}
</div>
```

**Styling**:
- Tailwind: `flex flex-col gap-1`
- Indentation: `pl-4` per depth level
- Icons: `Folder`, `FolderOpen`, `ChevronRight`, `ChevronDown`
- Hover: `hover:bg-gray-100 dark:hover:bg-gray-800`
- Selected: `bg-blue-100 dark:bg-blue-900`

**Performance**:
- Virtualize if > 100 folders (react-window)
- Memoize FolderTreeNode with React.memo

---

#### Component: FolderTreeNode

**Purpose**: Single node in folder tree (recursive)

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| folder | Folder | Yes | - | Folder data |
| depth | number | Yes | - | Nesting depth |
| expanded | boolean | Yes | - | Is expanded |
| selected | boolean | Yes | - | Is selected |
| onToggle | (id: string) => void | Yes | - | Toggle expand |
| onSelect | (id: string) => void | Yes | - | Select folder |

**State (Internal)**:
- None

**Behavior**:
1. Click chevron: Toggle expand/collapse (onToggle)
2. Click folder name: Select folder (onSelect)
3. If expanded: Render children recursively

**UI Structure**:
```tsx
<div>
  <div
    className={cn(
      "flex items-center gap-2 px-3 py-2 rounded cursor-pointer",
      selected && "bg-blue-100 dark:bg-blue-900"
    )}
    style={{ paddingLeft: `${depth * 16 + 12}px` }}
  >
    {folder.children.length > 0 && (
      <button onClick={() => onToggle(folder.id)}>
        {expanded ? <ChevronDown /> : <ChevronRight />}
      </button>
    )}
    <div onClick={() => onSelect(folder.id)} className="flex items-center gap-2 flex-1">
      {expanded ? <FolderOpen /> : <Folder />}
      <span>{folder.name}</span>
      <span className="text-gray-500 text-sm ml-auto">
        {folder.totalCards}
      </span>
    </div>
  </div>

  {expanded && folder.children.map(child => (
    <FolderTreeNode
      key={child.id}
      folder={child}
      depth={depth + 1}
      {...otherProps}
    />
  ))}
</div>
```

**Styling**:
- Dynamic padding based on depth
- Icon size: 16px
- Text: `text-sm`
- Smooth expand animation: `transition-all duration-200`

---

#### Component: FolderCard

**Purpose**: Card display of folder info (grid view)

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| folder | Folder | Yes | - | Folder data |
| onClick | () => void | No | - | Click handler |
| onContextMenu | (e: MouseEvent) => void | No | - | Right-click handler |

**State (Internal)**:
- None

**UI Structure**:
```tsx
<Card
  className="p-4 cursor-pointer hover:shadow-lg transition-shadow"
  onClick={onClick}
  onContextMenu={onContextMenu}
>
  <div className="flex items-start gap-3">
    <div className="p-2 bg-blue-100 dark:bg-blue-900 rounded">
      <Folder className="w-6 h-6" />
    </div>
    <div className="flex-1">
      <h3 className="font-semibold">{folder.name}</h3>
      <p className="text-sm text-gray-500 line-clamp-2">
        {folder.description}
      </p>
      <div className="flex gap-4 mt-2 text-xs text-gray-500">
        <span>{folder.totalDecks} decks</span>
        <span>{folder.totalCards} cards</span>
        <span>{folder.dueCards} due</span>
      </div>
    </div>
  </div>
</Card>
```

**Styling**:
- Shadcn: `Card`
- Max width: full in grid, min-width 240px
- Hover: scale(1.02) transform
- Description: line-clamp-2 (max 2 lines)

---

#### Component: CreateFolderDialog

**Purpose**: Modal form to create new folder

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| open | boolean | Yes | - | Dialog open state |
| onOpenChange | (open: boolean) => void | Yes | - | Toggle dialog |
| parentFolderId | string \| null | No | null | Parent folder ID |

**State (Internal)**:
- Form state managed by React Hook Form

**Form Fields**:
- `name` (string, required, max 100 chars)
- `description` (string, optional, max 500 chars)
- `parentFolderId` (string, optional, from props)

**Validation Schema (Zod)**:
```typescript
const schema = z.object({
  name: z.string()
    .min(1, "Folder name is required")
    .max(100, "Folder name must not exceed 100 characters"),
  description: z.string()
    .max(500, "Description must not exceed 500 characters")
    .optional(),
  parentFolderId: z.string().uuid().nullable()
})
```

**Data Mutation**:
- Hook: `useCreateFolder()`
- Endpoint: `POST /api/folders`
- Optimistic Update: Add to cache immediately, rollback on error
- On Success: Close dialog, show toast, invalidate folder queries

**Behavior**:
1. Open dialog: Reset form
2. Submit: Validate, call mutation
3. On success: Close dialog, show success toast
4. On error: Show error below fields

**UI Structure**:
```tsx
<Dialog open={open} onOpenChange={onOpenChange}>
  <DialogContent>
    <DialogHeader>
      <DialogTitle>Create New Folder</DialogTitle>
      <DialogDescription>
        Create a folder to organize your decks
      </DialogDescription>
    </DialogHeader>

    <form onSubmit={handleSubmit(onSubmit)}>
      <div className="space-y-4">
        <div>
          <Label>Folder Name *</Label>
          <Input {...register("name")} />
          {errors.name && (
            <p className="text-red-500 text-sm mt-1">
              {errors.name.message}
            </p>
          )}
        </div>

        <div>
          <Label>Description</Label>
          <Textarea {...register("description")} rows={3} />
          {errors.description && (
            <p className="text-red-500 text-sm mt-1">
              {errors.description.message}
            </p>
          )}
        </div>
      </div>

      <DialogFooter className="mt-6">
        <Button type="button" variant="outline" onClick={onCancel}>
          Cancel
        </Button>
        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting && <Loader2 className="animate-spin" />}
          Create
        </Button>
      </DialogFooter>
    </form>
  </DialogContent>
</Dialog>
```

**Styling**:
- Shadcn: `Dialog`, `Input`, `Textarea`, `Button`, `Label`
- Max width: 500px
- Input focus: blue ring
- Error text: red-500

---

#### Component: MoveFolderDialog

**Purpose**: Modal to select destination folder for move operation

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| open | boolean | Yes | - | Dialog open state |
| onOpenChange | (open: boolean) => void | Yes | - | Toggle dialog |
| folderId | string | Yes | - | Folder to move |

**State (Internal)**:
- selectedDestinationId: string | null

**Data Fetching**:
- Hook: `useFolderTree()`
- Filter: Exclude moving folder and its descendants (prevent circular ref)

**Data Mutation**:
- Hook: `useMoveFolder()`
- Endpoint: `POST /api/folders/{id}/move`
- Validation: Check depth after move <= 10
- On Success: Close dialog, show toast, invalidate folder queries

**Behavior**:
1. On mount: Fetch folders, filter invalid destinations
2. User selects destination from tree
3. Submit: Validate depth, call mutation
4. On error: Show error message

**UI Structure**:
```tsx
<Dialog open={open} onOpenChange={onOpenChange}>
  <DialogContent className="max-w-2xl">
    <DialogHeader>
      <DialogTitle>Move Folder</DialogTitle>
      <DialogDescription>
        Select destination folder
      </DialogDescription>
    </DialogHeader>

    <div className="max-h-[400px] overflow-auto border rounded p-4">
      <div className="mb-4">
        <Button
          variant={selectedDestinationId === null ? "default" : "outline"}
          onClick={() => setSelectedDestinationId(null)}
          className="w-full justify-start"
        >
          <Home className="mr-2" />
          Move to Root
        </Button>
      </div>

      <FolderTree
        selectedFolderId={selectedDestinationId}
        onSelect={setSelectedDestinationId}
        excludedIds={[folderId, ...descendantIds]}
      />
    </div>

    <DialogFooter>
      <Button variant="outline" onClick={onCancel}>
        Cancel
      </Button>
      <Button
        onClick={onSubmit}
        disabled={isMoving}
      >
        {isMoving && <Loader2 className="animate-spin" />}
        Move
      </Button>
    </DialogFooter>
  </DialogContent>
</Dialog>
```

**Validation**:
- Cannot move folder to itself
- Cannot move folder to its descendants
- Check depth after move: `newDepth = destination.depth + folder.depth - currentParent.depth`
- Reject if newDepth > 10

**Error Handling**:
- 422 (Business rule): Show error toast with message from backend
- Example: "Cannot move folder: max depth exceeded"

---

#### Component: CopyFolderDialog

**Purpose**: Modal to select destination and show async copy progress

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| open | boolean | Yes | - | Dialog open state |
| onOpenChange | (open: boolean) => void | Yes | - | Toggle dialog |
| folderId | string | Yes | - | Folder to copy |

**State (Internal)**:
- selectedDestinationId: string | null
- copyMode: 'select' | 'copying' | 'done'
- jobId: string | null (for async operations)
- progress: number (0-100)
- status: 'PROCESSING' | 'COMPLETED' | 'FAILED'

**Data Mutation**:
- Hook: `useCopyFolder()`
- Endpoint: `POST /api/folders/{id}/copy`
- Response: Sync (<= 50 items) or Async (51-500 items) with jobId

**Async Job Polling**:
- Hook: `useCopyStatus(jobId)`
- Endpoint: `GET /api/folders/copy-status/{jobId}`
- Polling Interval: 2 seconds
- Stop Conditions: status = 'COMPLETED' | 'FAILED'

**Behavior**:
1. Mode: 'select' - User selects destination
2. Submit: Call copy mutation
3. If sync: Show success, close dialog
4. If async: Switch to 'copying' mode, start polling
5. Mode: 'copying' - Show progress bar, poll status every 2s
6. Mode: 'done' - Show success/failure message

**UI Structure**:
```tsx
<Dialog open={open} onOpenChange={onOpenChange}>
  <DialogContent className="max-w-2xl">
    {copyMode === 'select' && (
      <>
        <DialogHeader>
          <DialogTitle>Copy Folder</DialogTitle>
          <DialogDescription>
            Select destination folder
          </DialogDescription>
        </DialogHeader>

        <div className="max-h-[400px] overflow-auto border rounded p-4">
          <FolderTree
            selectedFolderId={selectedDestinationId}
            onSelect={setSelectedDestinationId}
          />
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={onCancel}>Cancel</Button>
          <Button onClick={onSubmit} disabled={isCopying}>
            {isCopying && <Loader2 className="animate-spin" />}
            Copy
          </Button>
        </DialogFooter>
      </>
    )}

    {copyMode === 'copying' && (
      <>
        <DialogHeader>
          <DialogTitle>Copying Folder...</DialogTitle>
        </DialogHeader>

        <div className="py-6">
          <Progress value={progress} className="h-2" />
          <p className="text-center text-sm text-gray-500 mt-2">
            {progress}% complete
          </p>
        </div>
      </>
    )}

    {copyMode === 'done' && (
      <>
        <DialogHeader>
          <DialogTitle>
            {status === 'COMPLETED' ? 'Copy Completed' : 'Copy Failed'}
          </DialogTitle>
        </DialogHeader>

        <div className="py-6 text-center">
          {status === 'COMPLETED' ? (
            <CheckCircle className="w-16 h-16 text-green-500 mx-auto" />
          ) : (
            <XCircle className="w-16 h-16 text-red-500 mx-auto" />
          )}
        </div>

        <DialogFooter>
          <Button onClick={() => onOpenChange(false)}>Close</Button>
        </DialogFooter>
      </>
    )}
  </DialogContent>
</Dialog>
```

**Styling**:
- Shadcn: `Progress`, `CheckCircle`, `XCircle` from lucide-react
- Progress bar: height 8px, rounded, animated fill

**Error Handling**:
- Timeout: 5 minutes max for async copy
- Network error: Show retry button
- Backend error: Show error message from response

---

### 3.3 Deck Management Components

#### Component: DeckList

**Purpose**: Grid or list view of decks in a folder

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| folderId | string \| null | No | null | Filter by folder |
| viewMode | 'grid' \| 'list' | No | 'grid' | Display mode |

**State (Internal)**:
- None

**Data Fetching**:
- Hook: `useDecks(folderId)`
- Endpoint: `GET /api/decks?folderId={folderId}`
- Cache key: `['decks', { folderId }]`
- Pagination: 50 per page

**UI Structure**:
```tsx
<div>
  <div className="flex justify-between items-center mb-6">
    <h2 className="text-2xl font-bold">Decks</h2>
    <div className="flex gap-2">
      <Button onClick={openCreateDeckDialog}>
        <Plus /> New Deck
      </Button>
    </div>
  </div>

  {isLoading && <LoadingSpinner />}

  {error && <ErrorState message="Failed to load decks" />}

  {data && data.decks.length === 0 && (
    <EmptyState
      title="No decks yet"
      description="Create your first deck to get started"
      action={<Button onClick={openCreateDeckDialog}>Create Deck</Button>}
    />
  )}

  {data && viewMode === 'grid' && (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      {data.decks.map(deck => (
        <DeckCard key={deck.id} deck={deck} />
      ))}
    </div>
  )}
</div>
```

**Styling**:
- Grid: responsive columns (1 → 2 → 3)
- Gap: 16px
- Card min-width: 280px

---

#### Component: DeckCard

**Purpose**: Card display of deck info with stats

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| deck | Deck | Yes | - | Deck data |
| onClick | () => void | No | - | Click handler |

**State (Internal)**:
- None

**UI Structure**:
```tsx
<Card
  className="p-4 cursor-pointer hover:shadow-lg transition-shadow"
  onClick={onClick}
>
  <div className="flex items-start justify-between mb-3">
    <div className="flex items-center gap-2">
      <BookOpen className="w-5 h-5 text-blue-500" />
      <h3 className="font-semibold">{deck.name}</h3>
    </div>
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="sm">
          <MoreVertical />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent>
        <DropdownMenuItem onClick={onEdit}>Edit</DropdownMenuItem>
        <DropdownMenuItem onClick={onMove}>Move</DropdownMenuItem>
        <DropdownMenuItem onClick={onCopy}>Copy</DropdownMenuItem>
        <DropdownMenuItem onClick={onDelete} className="text-red-500">
          Delete
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  </div>

  <p className="text-sm text-gray-500 line-clamp-2 mb-4">
    {deck.description}
  </p>

  <div className="flex gap-4 text-sm">
    <div className="flex items-center gap-1">
      <Files className="w-4 h-4" />
      <span>{deck.totalCards} cards</span>
    </div>
    <div className="flex items-center gap-1">
      <Clock className="w-4 h-4 text-orange-500" />
      <span>{deck.dueCards} due</span>
    </div>
  </div>

  <div className="mt-4 pt-4 border-t">
    <Button
      className="w-full"
      variant="default"
      onClick={(e) => {
        e.stopPropagation();
        startReview(deck.id);
      }}
      disabled={deck.dueCards === 0}
    >
      {deck.dueCards > 0 ? `Review (${deck.dueCards})` : 'No cards due'}
    </Button>
  </div>
</Card>
```

**Styling**:
- Shadcn: `Card`, `DropdownMenu`, `Button`
- Icons: `lucide-react` (BookOpen, Files, Clock, MoreVertical)
- Hover: shadow-lg transition

---

#### Component: CreateDeckDialog

**Purpose**: Modal form to create new deck

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| open | boolean | Yes | - | Dialog open state |
| onOpenChange | (open: boolean) => void | Yes | - | Toggle dialog |
| folderId | string \| null | No | null | Parent folder ID |

**Form Fields**:
- `name` (string, required, max 100 chars)
- `description` (string, optional, max 500 chars)
- `folderId` (string, optional, from props)

**Validation Schema (Zod)**:
```typescript
const schema = z.object({
  name: z.string()
    .min(1, "Deck name is required")
    .max(100, "Deck name must not exceed 100 characters"),
  description: z.string()
    .max(500, "Description must not exceed 500 characters")
    .optional(),
  folderId: z.string().uuid().nullable()
})
```

**Data Mutation**:
- Hook: `useCreateDeck()`
- Endpoint: `POST /api/decks`
- On Success: Close dialog, show toast, invalidate deck queries, navigate to deck detail

**UI Structure**: Similar to CreateFolderDialog

---

#### Component: DeckStatsCard

**Purpose**: Display deck statistics and review progress

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| deckId | string | Yes | - | Deck ID |

**Data Fetching**:
- Hook: `useDeckStats(deckId)`
- Endpoint: `GET /api/stats/deck/{deckId}`
- Cache key: `['stats', 'deck', deckId]`

**UI Structure**:
```tsx
<Card className="p-6">
  <h3 className="text-lg font-semibold mb-4">Deck Statistics</h3>

  <div className="space-y-3">
    <div className="flex justify-between">
      <span className="text-gray-600">Total Cards</span>
      <span className="font-semibold">{stats.totalCards}</span>
    </div>
    <div className="flex justify-between">
      <span className="text-gray-600">Due Cards</span>
      <span className="font-semibold text-orange-500">
        {stats.dueCards}
      </span>
    </div>
    <div className="flex justify-between">
      <span className="text-gray-600">New Cards</span>
      <span className="font-semibold text-blue-500">
        {stats.newCards}
      </span>
    </div>
    <div className="flex justify-between">
      <span className="text-gray-600">Mature Cards</span>
      <span className="font-semibold text-green-500">
        {stats.matureCards}
      </span>
    </div>
  </div>

  <Separator className="my-4" />

  <div>
    <h4 className="text-sm font-medium mb-2">Last Studied</h4>
    <p className="text-sm text-gray-500">
      {formatDistanceToNow(stats.lastStudiedAt)} ago
    </p>
  </div>
</Card>
```

**Styling**:
- Shadcn: `Card`, `Separator`
- Color coding: orange (due), blue (new), green (mature)

---

### 3.4 Card Management Components

#### Component: CardList

**Purpose**: Paginated list of cards in a deck

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| deckId | string | Yes | - | Deck ID |

**State (Internal)**:
- page: number (pagination)
- searchTerm: string (filter cards)

**Data Fetching**:
- Hook: `useCards(deckId, { page, searchTerm })`
- Endpoint: `GET /api/decks/{deckId}/cards?page={page}&search={searchTerm}`
- Cache key: `['cards', deckId, { page, searchTerm }]`
- Pagination: 100 per page

**Behavior**:
1. On mount: Fetch first page
2. Search: Debounced 300ms
3. Pagination: Fetch next page on button click
4. Virtual scrolling: If > 100 cards (react-window)

**UI Structure**:
```tsx
<div>
  <div className="flex justify-between items-center mb-6">
    <h2 className="text-2xl font-bold">Cards</h2>
    <div className="flex gap-2">
      <Input
        placeholder="Search cards..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        className="w-64"
      />
      <Button onClick={openCreateCardDialog}>
        <Plus /> New Card
      </Button>
      <Button variant="outline" onClick={openImportDialog}>
        <Upload /> Import
      </Button>
      <Button variant="outline" onClick={exportCards}>
        <Download /> Export
      </Button>
    </div>
  </div>

  {isLoading && <LoadingSpinner />}

  {data && data.cards.length === 0 && (
    <EmptyState title="No cards yet" />
  )}

  {data && (
    <div className="space-y-2">
      {data.cards.map(card => (
        <CardItem key={card.id} card={card} />
      ))}
    </div>
  )}

  {data && data.totalPages > 1 && (
    <Pagination
      page={page}
      totalPages={data.totalPages}
      onPageChange={setPage}
    />
  )}
</div>
```

**Performance**:
- Debounce search: 300ms
- Virtual scrolling: Use react-window if > 100 cards
- Memoize CardItem with React.memo

---

#### Component: CardItem

**Purpose**: Display single card front/back with actions

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| card | Card | Yes | - | Card data |

**State (Internal)**:
- isFlipped: boolean (show front or back)

**UI Structure**:
```tsx
<Card className="p-4 hover:shadow-md transition-shadow">
  <div className="flex items-start gap-4">
    <div className="flex-1">
      <div className="flex items-center gap-2 mb-2">
        <Badge variant="outline">
          Box {card.currentBox}
        </Badge>
        <span className="text-xs text-gray-500">
          Due: {format(card.dueDate, 'MMM dd')}
        </span>
      </div>

      <div className="space-y-2">
        <div>
          <p className="text-xs text-gray-500 mb-1">Front:</p>
          <p className="text-sm">{card.front}</p>
        </div>

        {isFlipped && (
          <div>
            <p className="text-xs text-gray-500 mb-1">Back:</p>
            <p className="text-sm">{card.back}</p>
          </div>
        )}
      </div>

      <Button
        variant="ghost"
        size="sm"
        onClick={() => setIsFlipped(!isFlipped)}
        className="mt-2"
      >
        {isFlipped ? 'Hide Answer' : 'Show Answer'}
      </Button>
    </div>

    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="sm">
          <MoreVertical />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent>
        <DropdownMenuItem onClick={onEdit}>Edit</DropdownMenuItem>
        <DropdownMenuItem onClick={onDelete} className="text-red-500">
          Delete
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  </div>
</Card>
```

**Styling**:
- Shadcn: `Card`, `Badge`, `DropdownMenu`
- Flip animation: rotate3d transition (optional)

---

#### Component: CardEditor

**Purpose**: Inline editor for card front/back

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| card | Card \| null | No | null | Existing card or null for new |
| deckId | string | Yes | - | Parent deck ID |
| onSave | () => void | Yes | - | Callback after save |
| onCancel | () => void | Yes | - | Callback on cancel |

**Form Fields**:
- `front` (string, required, max 5000 chars)
- `back` (string, required, max 5000 chars)

**Validation Schema (Zod)**:
```typescript
const schema = z.object({
  front: z.string()
    .min(1, "Front text is required")
    .max(5000, "Front text must not exceed 5000 characters"),
  back: z.string()
    .min(1, "Back text is required")
    .max(5000, "Back text must not exceed 5000 characters")
})
```

**Data Mutation**:
- Hook: `useCreateCard()` or `useUpdateCard()`
- Endpoint: `POST /api/decks/{deckId}/cards` or `PUT /api/cards/{id}`
- On Success: Call onSave, show toast, invalidate cards query

**UI Structure**:
```tsx
<Card className="p-4">
  <form onSubmit={handleSubmit(onSubmit)}>
    <div className="space-y-4">
      <div>
        <Label>Front *</Label>
        <Textarea
          {...register("front")}
          rows={3}
          placeholder="Enter question or prompt..."
        />
        {errors.front && (
          <p className="text-red-500 text-sm mt-1">
            {errors.front.message}
          </p>
        )}
        <p className="text-xs text-gray-500 mt-1">
          {watch("front")?.length || 0} / 5000
        </p>
      </div>

      <div>
        <Label>Back *</Label>
        <Textarea
          {...register("back")}
          rows={3}
          placeholder="Enter answer..."
        />
        {errors.back && (
          <p className="text-red-500 text-sm mt-1">
            {errors.back.message}
          </p>
        )}
        <p className="text-xs text-gray-500 mt-1">
          {watch("back")?.length || 0} / 5000
        </p>
      </div>
    </div>

    <div className="flex gap-2 mt-4">
      <Button type="submit" disabled={isSubmitting}>
        {isSubmitting && <Loader2 className="animate-spin" />}
        Save
      </Button>
      <Button type="button" variant="outline" onClick={onCancel}>
        Cancel
      </Button>
    </div>
  </form>
</Card>
```

**Styling**:
- Shadcn: `Textarea`, `Label`, `Button`
- Character counter: text-xs, gray-500
- Auto-resize textarea (optional)

---

#### Component: ImportCardsDialog

**Purpose**: Modal for bulk import cards from CSV/Excel

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| open | boolean | Yes | - | Dialog open state |
| onOpenChange | (open: boolean) => void | Yes | - | Toggle dialog |
| deckId | string | Yes | - | Target deck ID |

**State (Internal)**:
- file: File | null
- importMode: 'upload' | 'preview' | 'importing' | 'done'
- previewData: Card[] (first 10 rows)
- importResult: { success: number, errors: number, errorDetails: [] }

**Data Mutation**:
- Hook: `useImportCards()`
- Endpoint: `POST /api/decks/{deckId}/cards/import`
- Body: FormData with file
- Response: Import summary

**Behavior**:
1. Mode: 'upload' - User selects file
2. On file select: Validate format, show preview
3. Mode: 'preview' - Show first 10 rows, confirm import
4. Submit: Upload file, show progress
5. Mode: 'importing' - Show progress bar
6. Mode: 'done' - Show summary (success count, error count)

**UI Structure**:
```tsx
<Dialog open={open} onOpenChange={onOpenChange}>
  <DialogContent className="max-w-3xl">
    {importMode === 'upload' && (
      <>
        <DialogHeader>
          <DialogTitle>Import Cards</DialogTitle>
          <DialogDescription>
            Upload CSV or Excel file
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          <div className="border-2 border-dashed rounded-lg p-8 text-center">
            <Input
              type="file"
              accept=".csv,.xlsx"
              onChange={handleFileSelect}
              className="hidden"
              id="file-upload"
            />
            <label
              htmlFor="file-upload"
              className="cursor-pointer"
            >
              <Upload className="w-12 h-12 mx-auto text-gray-400" />
              <p className="mt-2">Click to upload or drag and drop</p>
              <p className="text-sm text-gray-500">CSV or XLSX (max 50MB)</p>
            </label>
          </div>

          <div>
            <Button
              variant="outline"
              onClick={downloadTemplate}
              className="w-full"
            >
              <Download /> Download Template
            </Button>
          </div>
        </div>
      </>
    )}

    {importMode === 'preview' && (
      <>
        <DialogHeader>
          <DialogTitle>Preview Import</DialogTitle>
          <DialogDescription>
            Review first 10 cards before importing
          </DialogDescription>
        </DialogHeader>

        <div className="max-h-[400px] overflow-auto">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Front</TableHead>
                <TableHead>Back</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {previewData.map((card, idx) => (
                <TableRow key={idx}>
                  <TableCell>{card.front}</TableCell>
                  <TableCell>{card.back}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={onCancel}>
            Cancel
          </Button>
          <Button onClick={onSubmit}>
            Import {totalRows} cards
          </Button>
        </DialogFooter>
      </>
    )}

    {importMode === 'importing' && (
      <>
        <DialogHeader>
          <DialogTitle>Importing Cards...</DialogTitle>
        </DialogHeader>

        <div className="py-6">
          <Progress value={progress} className="h-2" />
          <p className="text-center text-sm text-gray-500 mt-2">
            {processedRows} / {totalRows} cards
          </p>
        </div>
      </>
    )}

    {importMode === 'done' && (
      <>
        <DialogHeader>
          <DialogTitle>Import Complete</DialogTitle>
        </DialogHeader>

        <div className="py-6">
          <div className="space-y-2">
            <div className="flex justify-between">
              <span>Successfully imported:</span>
              <span className="font-semibold text-green-500">
                {importResult.success}
              </span>
            </div>
            <div className="flex justify-between">
              <span>Errors:</span>
              <span className="font-semibold text-red-500">
                {importResult.errors}
              </span>
            </div>
          </div>

          {importResult.errors > 0 && (
            <Button
              variant="outline"
              onClick={downloadErrorReport}
              className="w-full mt-4"
            >
              <Download /> Download Error Report
            </Button>
          )}
        </div>

        <DialogFooter>
          <Button onClick={() => onOpenChange(false)}>Close</Button>
        </DialogFooter>
      </>
    )}
  </DialogContent>
</Dialog>
```

**Validation**:
- File size: max 50MB
- Format: .csv or .xlsx only
- Rows: max 10,000 per file
- Columns: minimum 2 (Front, Back)

**Error Handling**:
- Invalid format: Show error message
- Missing columns: Show error with details
- Empty rows: Skip automatically
- Row errors: Show error report with row numbers

---

#### Component: ExportCardsDialog

**Purpose**: Modal to select export format and options

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| open | boolean | Yes | - | Dialog open state |
| onOpenChange | (open: boolean) => void | Yes | - | Toggle dialog |
| deckId | string | Yes | - | Source deck ID |

**State (Internal)**:
- format: 'csv' | 'xlsx'
- filter: 'all' | 'due'

**Behavior**:
1. User selects format (CSV or Excel)
2. User selects filter (All cards or Due cards only)
3. Submit: Trigger download

**Data Fetching**:
- Hook: None (direct file download)
- Endpoint: `GET /api/decks/{deckId}/cards/export?format={format}&filter={filter}`
- Response: File download (Content-Disposition: attachment)

**UI Structure**:
```tsx
<Dialog open={open} onOpenChange={onOpenChange}>
  <DialogContent>
    <DialogHeader>
      <DialogTitle>Export Cards</DialogTitle>
      <DialogDescription>
        Choose format and options
      </DialogDescription>
    </DialogHeader>

    <div className="space-y-4">
      <div>
        <Label>Format</Label>
        <RadioGroup value={format} onValueChange={setFormat}>
          <div className="flex items-center gap-2">
            <RadioGroupItem value="csv" id="csv" />
            <Label htmlFor="csv">CSV (.csv)</Label>
          </div>
          <div className="flex items-center gap-2">
            <RadioGroupItem value="xlsx" id="xlsx" />
            <Label htmlFor="xlsx">Excel (.xlsx)</Label>
          </div>
        </RadioGroup>
      </div>

      <div>
        <Label>Cards to Export</Label>
        <RadioGroup value={filter} onValueChange={setFilter}>
          <div className="flex items-center gap-2">
            <RadioGroupItem value="all" id="all" />
            <Label htmlFor="all">All cards</Label>
          </div>
          <div className="flex items-center gap-2">
            <RadioGroupItem value="due" id="due" />
            <Label htmlFor="due">Due cards only</Label>
          </div>
        </RadioGroup>
      </div>
    </div>

    <DialogFooter>
      <Button variant="outline" onClick={() => onOpenChange(false)}>
        Cancel
      </Button>
      <Button onClick={onExport}>
        <Download /> Export
      </Button>
    </DialogFooter>
  </DialogContent>
</Dialog>
```

**Export Logic**:
```typescript
const onExport = async () => {
  const url = `/api/decks/${deckId}/cards/export?format=${format}&filter=${filter}`;
  const response = await api.get(url, { responseType: 'blob' });

  // Trigger download
  const blob = new Blob([response.data]);
  const link = document.createElement('a');
  link.href = window.URL.createObjectURL(blob);
  link.download = `${deckName}_export_${format}.${format}`;
  link.click();

  onOpenChange(false);
};
```

---

### 3.5 Review Session Components

#### Component: ReviewCard

**Purpose**: Flashcard display with flip animation

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| card | Card | Yes | - | Current card |
| showAnswer | boolean | Yes | - | Show back side |
| onFlip | () => void | Yes | - | Flip card |

**State (Internal)**:
- None

**UI Structure**:
```tsx
<div className="max-w-2xl mx-auto">
  <Card className="min-h-[400px] p-8">
    <div className="flex flex-col h-full">
      <div className="flex-1 flex items-center justify-center">
        {!showAnswer ? (
          <div className="text-center">
            <p className="text-sm text-gray-500 mb-2">Question</p>
            <p className="text-2xl">{card.front}</p>
          </div>
        ) : (
          <div className="text-center space-y-6">
            <div>
              <p className="text-sm text-gray-500 mb-2">Question</p>
              <p className="text-xl text-gray-600">{card.front}</p>
            </div>
            <Separator />
            <div>
              <p className="text-sm text-gray-500 mb-2">Answer</p>
              <p className="text-2xl">{card.back}</p>
            </div>
          </div>
        )}
      </div>

      {!showAnswer && (
        <Button
          onClick={onFlip}
          className="w-full mt-6"
          size="lg"
        >
          Show Answer
        </Button>
      )}
    </div>
  </Card>
</div>
```

**Styling**:
- Shadcn: `Card`, `Separator`, `Button`
- Min height: 400px
- Font size: 2xl (24px) for answer
- Flip animation (optional): CSS transform rotate3d

---

#### Component: RatingButtons

**Purpose**: 4-button rating interface (Again, Hard, Good, Easy)

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| onRate | (rating: Rating) => void | Yes | - | Callback with rating |
| disabled | boolean | No | false | Disable buttons |

**State (Internal)**:
- None

**UI Structure**:
```tsx
<div className="grid grid-cols-4 gap-3 max-w-2xl mx-auto mt-6">
  <Button
    variant="destructive"
    size="lg"
    onClick={() => onRate('AGAIN')}
    disabled={disabled}
    className="flex flex-col gap-2 h-auto py-4"
  >
    <span className="text-2xl">😔</span>
    <span>Again</span>
    <span className="text-xs opacity-70">&lt; 1m</span>
  </Button>

  <Button
    variant="outline"
    size="lg"
    onClick={() => onRate('HARD')}
    disabled={disabled}
    className="flex flex-col gap-2 h-auto py-4 border-orange-500 hover:bg-orange-50"
  >
    <span className="text-2xl">😐</span>
    <span>Hard</span>
    <span className="text-xs opacity-70">&lt; 6m</span>
  </Button>

  <Button
    variant="outline"
    size="lg"
    onClick={() => onRate('GOOD')}
    disabled={disabled}
    className="flex flex-col gap-2 h-auto py-4 border-green-500 hover:bg-green-50"
  >
    <span className="text-2xl">🙂</span>
    <span>Good</span>
    <span className="text-xs opacity-70">next interval</span>
  </Button>

  <Button
    variant="outline"
    size="lg"
    onClick={() => onRate('EASY')}
    disabled={disabled}
    className="flex flex-col gap-2 h-auto py-4 border-blue-500 hover:bg-blue-50"
  >
    <span className="text-2xl">😁</span>
    <span>Easy</span>
    <span className="text-xs opacity-70">4x interval</span>
  </Button>
</div>
```

**Styling**:
- Grid: 4 equal columns
- Button height: auto (vertical padding)
- Color coding: red (again), orange (hard), green (good), blue (easy)
- Keyboard shortcuts: 1, 2, 3, 4 (optional)

---

#### Component: ReviewProgress

**Purpose**: Progress bar and counter for review session

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| current | number | Yes | - | Current card index |
| total | number | Yes | - | Total cards |

**State (Internal)**:
- None

**UI Structure**:
```tsx
<div className="max-w-2xl mx-auto mb-4">
  <div className="flex justify-between items-center mb-2">
    <span className="text-sm text-gray-600">
      Card {current} of {total}
    </span>
    <span className="text-sm text-gray-600">
      {Math.round((current / total) * 100)}%
    </span>
  </div>
  <Progress value={(current / total) * 100} className="h-2" />
</div>
```

**Styling**:
- Shadcn: `Progress`
- Progress bar: height 8px, rounded

---

#### Component: ReviewSummary

**Purpose**: Session results summary after completion

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| stats | ReviewStats | Yes | - | Session statistics |
| onFinish | () => void | Yes | - | Finish callback |

**Data Types**:
```typescript
interface ReviewStats {
  totalCards: number;
  againCount: number;
  hardCount: number;
  goodCount: number;
  easyCount: number;
  durationSeconds: number;
}
```

**UI Structure**:
```tsx
<Card className="max-w-2xl mx-auto p-8">
  <div className="text-center">
    <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4" />
    <h2 className="text-2xl font-bold mb-2">Session Complete!</h2>
    <p className="text-gray-600 mb-6">
      You reviewed {stats.totalCards} cards in {formatDuration(stats.durationSeconds)}
    </p>
  </div>

  <Separator className="my-6" />

  <div className="grid grid-cols-4 gap-4 mb-6">
    <div className="text-center">
      <div className="text-3xl font-bold text-red-500">
        {stats.againCount}
      </div>
      <p className="text-sm text-gray-600">Again</p>
    </div>
    <div className="text-center">
      <div className="text-3xl font-bold text-orange-500">
        {stats.hardCount}
      </div>
      <p className="text-sm text-gray-600">Hard</p>
    </div>
    <div className="text-center">
      <div className="text-3xl font-bold text-green-500">
        {stats.goodCount}
      </div>
      <p className="text-sm text-gray-600">Good</p>
    </div>
    <div className="text-center">
      <div className="text-3xl font-bold text-blue-500">
        {stats.easyCount}
      </div>
      <p className="text-sm text-gray-600">Easy</p>
    </div>
  </div>

  <Button
    onClick={onFinish}
    className="w-full"
    size="lg"
  >
    Finish
  </Button>
</Card>
```

**Styling**:
- Shadcn: `Card`, `Separator`, `CheckCircle`
- Color coding for rating counts
- Large numbers: text-3xl

---

### 3.6 Settings Components

#### Component: SRSSettingsForm

**Purpose**: Form to configure all SRS algorithm settings

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| None | - | - | - | Fetches user settings |

**Data Fetching**:
- Hook: `useSRSSettings()`
- Endpoint: `GET /api/srs/settings`
- Cache key: `['srs', 'settings']`

**Data Mutation**:
- Hook: `useUpdateSRSSettings()`
- Endpoint: `PUT /api/srs/settings`

**Form Fields**:
- `reviewOrder`: 'ASCENDING' | 'DESCENDING' | 'RANDOM'
- `notificationEnabled`: boolean
- `notificationTime`: string (HH:mm format)
- `forgottenCardAction`: 'MOVE_TO_BOX_1' | 'MOVE_DOWN_N_BOXES' | 'STAY_IN_BOX'
- `moveDownBoxes`: number (1-3, conditional on forgottenCardAction)
- `newCardsPerDay`: number (1-100)
- `maxReviewsPerDay`: number (1-500)

**UI Structure**:
```tsx
<Card className="max-w-2xl mx-auto p-6">
  <h2 className="text-2xl font-bold mb-6">SRS Settings</h2>

  <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
    {/* Review Order */}
    <div>
      <Label>Review Order</Label>
      <RadioGroup
        value={watch("reviewOrder")}
        onValueChange={(value) => setValue("reviewOrder", value)}
      >
        <div className="flex items-center gap-2">
          <RadioGroupItem value="ASCENDING" id="asc" />
          <Label htmlFor="asc">Ascending (Box 1 → 7)</Label>
        </div>
        <div className="flex items-center gap-2">
          <RadioGroupItem value="DESCENDING" id="desc" />
          <Label htmlFor="desc">Descending (Box 7 → 1)</Label>
        </div>
        <div className="flex items-center gap-2">
          <RadioGroupItem value="RANDOM" id="random" />
          <Label htmlFor="random">Random</Label>
        </div>
      </RadioGroup>
    </div>

    <Separator />

    {/* Notifications */}
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <Label htmlFor="notif">Enable Notifications</Label>
        <Switch
          id="notif"
          checked={watch("notificationEnabled")}
          onCheckedChange={(checked) => setValue("notificationEnabled", checked)}
        />
      </div>

      {watch("notificationEnabled") && (
        <div>
          <Label>Notification Time</Label>
          <Input
            type="time"
            {...register("notificationTime")}
          />
        </div>
      )}
    </div>

    <Separator />

    {/* Forgotten Card Action */}
    <div>
      <Label>When I press "Again"</Label>
      <RadioGroup
        value={watch("forgottenCardAction")}
        onValueChange={(value) => setValue("forgottenCardAction", value)}
      >
        <div className="flex items-center gap-2">
          <RadioGroupItem value="MOVE_TO_BOX_1" id="box1" />
          <Label htmlFor="box1">Move to Box 1</Label>
        </div>
        <div className="flex items-center gap-2">
          <RadioGroupItem value="MOVE_DOWN_N_BOXES" id="down" />
          <Label htmlFor="down">Move down N boxes</Label>
        </div>
        <div className="flex items-center gap-2">
          <RadioGroupItem value="STAY_IN_BOX" id="stay" />
          <Label htmlFor="stay">Stay in current box</Label>
        </div>
      </RadioGroup>

      {watch("forgottenCardAction") === 'MOVE_DOWN_N_BOXES' && (
        <div className="mt-4">
          <Label>Number of boxes to move down</Label>
          <Input
            type="number"
            min={1}
            max={3}
            {...register("moveDownBoxes", { valueAsNumber: true })}
          />
        </div>
      )}
    </div>

    <Separator />

    {/* Daily Limits */}
    <div className="space-y-4">
      <div>
        <Label>New Cards Per Day</Label>
        <Input
          type="number"
          min={1}
          max={100}
          {...register("newCardsPerDay", { valueAsNumber: true })}
        />
        <p className="text-xs text-gray-500 mt-1">
          Recommended: 20
        </p>
      </div>

      <div>
        <Label>Max Reviews Per Day</Label>
        <Input
          type="number"
          min={1}
          max={500}
          {...register("maxReviewsPerDay", { valueAsNumber: true })}
        />
        <p className="text-xs text-gray-500 mt-1">
          Recommended: 200
        </p>
      </div>
    </div>

    <div className="flex gap-3 pt-4">
      <Button type="submit" disabled={isSubmitting}>
        {isSubmitting && <Loader2 className="animate-spin" />}
        Save Changes
      </Button>
      <Button
        type="button"
        variant="outline"
        onClick={onReset}
      >
        Reset to Default
      </Button>
    </div>
  </form>
</Card>
```

**Validation**:
- newCardsPerDay: 1-100
- maxReviewsPerDay: 1-500
- moveDownBoxes: 1-3 (only if MOVE_DOWN_N_BOXES selected)

**Behavior**:
1. On mount: Fetch current settings
2. On submit: Update settings, show toast
3. On reset: Call reset API, refetch settings

---

#### Component: ProfileForm

**Purpose**: User profile settings (name, timezone, language, theme)

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| None | - | - | - | Uses AuthContext |

**Form Fields**:
- `name`: string
- `timezone`: string (select from list)
- `language`: 'vi' | 'en'
- `theme`: 'light' | 'dark' | 'system'

**Data Mutation**:
- Hook: `useUpdateProfile()`
- Endpoint: `PUT /api/users/profile`

**UI Structure**: Similar to SRSSettingsForm with different fields

---

#### Component: ThemeToggle

**Purpose**: Toggle between light/dark/system theme

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| None | - | - | - | Uses Zustand |

**State**:
- theme (from Zustand uiStore)

**Behavior**:
1. Detect system preference: `window.matchMedia('(prefers-color-scheme: dark)')`
2. User can override: light / dark / system
3. Store preference: localStorage 'theme'
4. Apply: Add/remove 'dark' class on <html>

**UI Structure**:
```tsx
<DropdownMenu>
  <DropdownMenuTrigger asChild>
    <Button variant="ghost" size="sm">
      {theme === 'light' && <Sun />}
      {theme === 'dark' && <Moon />}
      {theme === 'system' && <Monitor />}
    </Button>
  </DropdownMenuTrigger>
  <DropdownMenuContent>
    <DropdownMenuItem onClick={() => setTheme('light')}>
      <Sun className="mr-2" /> Light
    </DropdownMenuItem>
    <DropdownMenuItem onClick={() => setTheme('dark')}>
      <Moon className="mr-2" /> Dark
    </DropdownMenuItem>
    <DropdownMenuItem onClick={() => setTheme('system')}>
      <Monitor className="mr-2" /> System
    </DropdownMenuItem>
  </DropdownMenuContent>
</DropdownMenu>
```

**Theme Hook**:
```typescript
// hooks/useTheme.ts
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

---

### 3.7 Statistics Components

#### Component: StatsOverview

**Purpose**: Dashboard widget showing key statistics

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| None | - | - | - | Fetches user stats |

**Data Fetching**:
- Hook: `useUserStats()`
- Endpoint: `GET /api/stats/user`
- Cache key: `['stats', 'user']`

**UI Structure**:
```tsx
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
  <Card className="p-6">
    <div className="flex items-center gap-3">
      <div className="p-3 bg-blue-100 dark:bg-blue-900 rounded-lg">
        <Flame className="w-6 h-6 text-blue-500" />
      </div>
      <div>
        <p className="text-sm text-gray-600">Streak</p>
        <p className="text-2xl font-bold">{stats.streakDays} days</p>
      </div>
    </div>
  </Card>

  <Card className="p-6">
    <div className="flex items-center gap-3">
      <div className="p-3 bg-green-100 dark:bg-green-900 rounded-lg">
        <BookOpen className="w-6 h-6 text-green-500" />
      </div>
      <div>
        <p className="text-sm text-gray-600">Total Cards</p>
        <p className="text-2xl font-bold">{stats.totalCards}</p>
      </div>
    </div>
  </Card>

  <Card className="p-6">
    <div className="flex items-center gap-3">
      <div className="p-3 bg-orange-100 dark:bg-orange-900 rounded-lg">
        <Clock className="w-6 h-6 text-orange-500" />
      </div>
      <div>
        <p className="text-sm text-gray-600">Due Today</p>
        <p className="text-2xl font-bold">{stats.dueCards}</p>
      </div>
    </div>
  </Card>

  <Card className="p-6">
    <div className="flex items-center gap-3">
      <div className="p-3 bg-purple-100 dark:bg-purple-900 rounded-lg">
        <TrendingUp className="w-6 h-6 text-purple-500" />
      </div>
      <div>
        <p className="text-sm text-gray-600">Study Time</p>
        <p className="text-2xl font-bold">{stats.studyTimeMinutes} min</p>
      </div>
    </div>
  </Card>
</div>
```

**Styling**:
- Grid: responsive 1 → 2 → 4 columns
- Icons: colored backgrounds matching icon color

---

#### Component: BoxDistributionChart

**Purpose**: Bar chart showing card distribution across boxes

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| scope | 'all' \| 'folder' \| 'deck' | No | 'all' | Scope filter |
| scopeId | string \| null | No | null | Scope ID |

**Data Fetching**:
- Hook: `useBoxDistribution(scope, scopeId)`
- Endpoint: `GET /api/stats/box-distribution?scope={scope}&scopeId={scopeId}`
- Cache key: `['stats', 'box-distribution', scope, scopeId]`

**UI Structure**:
```tsx
<Card className="p-6">
  <h3 className="text-lg font-semibold mb-4">Card Distribution by Box</h3>

  <div className="space-y-3">
    {[1, 2, 3, 4, 5, 6, 7].map(box => {
      const count = data?.find(d => d.box === box)?.count || 0;
      const maxCount = Math.max(...(data?.map(d => d.count) || [1]));
      const percentage = (count / maxCount) * 100;

      return (
        <div key={box}>
          <div className="flex justify-between items-center mb-1">
            <span className="text-sm font-medium">Box {box}</span>
            <span className="text-sm text-gray-600">{count} cards</span>
          </div>
          <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
            <div
              className="bg-blue-500 h-2 rounded-full transition-all"
              style={{ width: `${percentage}%` }}
            />
          </div>
        </div>
      );
    })}
  </div>
</Card>
```

**Styling**:
- Bar height: 8px
- Bar color: blue-500
- Smooth animation on data change

---

#### Component: FolderStatsCard

**Purpose**: Recursive folder statistics (total cards, due cards)

**Props**:
| Prop | Type | Required | Default | Description |
|------|------|----------|---------|-------------|
| folderId | string | Yes | - | Folder ID |

**Data Fetching**:
- Hook: `useFolderStats(folderId)`
- Endpoint: `GET /api/stats/folder/{folderId}`
- Cache key: `['stats', 'folder', folderId]`
- Note: Backend calculates recursive stats (includes sub-folders)

**UI Structure**: Similar to StatsOverview but for single folder

---

## 4. Routing Specifications

### 4.1 Route Structure

```typescript
// constants/routes.ts
export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  DASHBOARD: '/dashboard',
  FOLDERS: '/folders',
  FOLDER_DETAIL: '/folders/:id',
  DECK_DETAIL: '/decks/:id',
  REVIEW: '/review',
  REVIEW_CRAM: '/review/cram/:deckId',
  REVIEW_RANDOM: '/review/random/:deckId',
  SETTINGS: '/settings',
  STATS: '/stats',
} as const;
```

### 4.2 Router Configuration

```typescript
// main.tsx
import { createBrowserRouter, RouterProvider } from 'react-router-dom';

const router = createBrowserRouter([
  // Public routes
  {
    path: ROUTES.LOGIN,
    element: <LoginPage />,
  },
  {
    path: ROUTES.REGISTER,
    element: <RegisterPage />,
  },

  // Protected routes
  {
    path: '/',
    element: <ProtectedRoute />, // Wrapper with auth check
    children: [
      {
        index: true,
        element: <Navigate to={ROUTES.DASHBOARD} replace />,
      },
      {
        path: ROUTES.DASHBOARD,
        element: <DashboardPage />,
      },
      {
        path: ROUTES.FOLDERS,
        element: <FolderListPage />,
      },
      {
        path: ROUTES.FOLDER_DETAIL,
        element: <FolderDetailPage />,
      },
      {
        path: ROUTES.DECK_DETAIL,
        element: <DeckDetailPage />,
      },
      {
        path: ROUTES.REVIEW,
        element: <ReviewSessionPage />,
      },
      {
        path: ROUTES.REVIEW_CRAM,
        element: <CramSessionPage />,
      },
      {
        path: ROUTES.REVIEW_RANDOM,
        element: <RandomSessionPage />,
      },
      {
        path: ROUTES.SETTINGS,
        element: <SettingsPage />,
      },
      {
        path: ROUTES.STATS,
        element: <StatisticsPage />,
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

### 4.3 Protected Route Component

```typescript
// components/common/ProtectedRoute.tsx
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { AppLayout } from '@/components/common/AppLayout';

export function ProtectedRoute() {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="h-screen flex items-center justify-center">
        <LoadingSpinner />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to={ROUTES.LOGIN} replace />;
  }

  return (
    <AppLayout>
      <Outlet />
    </AppLayout>
  );
}
```

### 4.4 Navigation Usage

```typescript
// Example: Navigate programmatically
import { useNavigate } from 'react-router-dom';

function FolderCard({ folder }) {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/folders/${folder.id}`);
  };

  return <Card onClick={handleClick}>...</Card>;
}
```

---

## 5. API Integration Specifications

### 5.1 Axios Instance Configuration

```typescript
// services/api.ts
import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { authService } from './authService';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

export const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000, // 30 seconds
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Send cookies (refresh token)
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
        // Refresh token (refresh token sent automatically in cookie)
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

**Key Features**:
- Access token in Authorization header
- Refresh token in HTTP-only cookie (auto-sent)
- Automatic token refresh on 401
- Request queuing during refresh
- Logout on refresh failure

---

### 5.2 Auth Service with Token Management

```typescript
// services/authService.ts
import api from './api';

interface LoginResponse {
  accessToken: string;
  expiresIn: number; // seconds
  user: User;
}

// In-memory token storage (more secure than localStorage)
let accessToken: string | null = null;

export const authService = {
  async login(email: string, password: string): Promise<LoginResponse> {
    const response = await api.post<LoginResponse>('/auth/login', {
      email,
      password,
    });
    // Refresh token automatically stored in HTTP-only cookie by backend
    return response.data;
  },

  async register(email: string, password: string, name: string): Promise<void> {
    await api.post('/auth/register', {
      email,
      password,
      name,
    });
  },

  async logout(): Promise<void> {
    try {
      await api.post('/auth/logout');
    } finally {
      this.clearTokens();
    }
  },

  async refreshToken(): Promise<{ accessToken: string }> {
    // Refresh token sent automatically in cookie
    const response = await api.post<{ accessToken: string }>('/auth/refresh');
    return response.data;
  },

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
- Access token in memory (NOT localStorage) - prevents XSS theft
- Refresh token in HTTP-only cookie - prevents XSS access
- Automatic token refresh with request queuing
- Token cleared on logout or refresh failure

---

### 5.3 React Query Hook Pattern

```typescript
// hooks/useFolder.ts
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
      queryClient.setQueryData(folderKeys.tree(), (old: Folder[] | undefined) => [
        ...(old || []),
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
```

**Pattern Benefits**:
- Centralized query keys
- Type-safe hooks
- Automatic caching
- Optimistic updates
- Easy invalidation

---

### 5.4 Mutation Pattern with Async Jobs

```typescript
// hooks/useFolder.ts (continued)

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

// Hook for checking copy status (for progress UI)
export function useCopyStatus(jobId: string | null) {
  return useQuery({
    queryKey: ['folder', 'copy-status', jobId],
    queryFn: () => folderService.getCopyStatus(jobId!),
    enabled: !!jobId,
    refetchInterval: 2000, // Poll every 2 seconds
    refetchIntervalInBackground: false,
  });
}
```

**Async Job Pattern**:
- Mutation returns immediately with jobId
- Polling hook refetches status every 2s
- Stop polling when status = COMPLETED | FAILED
- Show progress bar in UI

---

## 6. Form Validation Specifications

### 6.1 Validation Library Setup

```typescript
// Using React Hook Form + Zod
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';

// Example: CreateFolderForm validation schema
const createFolderSchema = z.object({
  name: z.string()
    .min(1, "Folder name is required")
    .max(100, "Folder name must not exceed 100 characters"),
  description: z.string()
    .max(500, "Description must not exceed 500 characters")
    .optional(),
  parentFolderId: z.string().uuid().nullable(),
});

type CreateFolderFormData = z.infer<typeof createFolderSchema>;

// Usage in component
function CreateFolderDialog() {
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<CreateFolderFormData>({
    resolver: zodResolver(createFolderSchema),
  });

  const onSubmit = async (data: CreateFolderFormData) => {
    // Call API
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <Input {...register("name")} />
      {errors.name && <p className="text-red-500">{errors.name.message}</p>}
    </form>
  );
}
```

### 6.2 Common Validation Schemas

```typescript
// types/schemas.ts

// Folder schemas
export const createFolderSchema = z.object({
  name: z.string().min(1).max(100),
  description: z.string().max(500).optional(),
  parentFolderId: z.string().uuid().nullable(),
});

export const updateFolderSchema = z.object({
  name: z.string().min(1).max(100),
  description: z.string().max(500).optional(),
});

// Deck schemas
export const createDeckSchema = z.object({
  name: z.string().min(1).max(100),
  description: z.string().max(500).optional(),
  folderId: z.string().uuid().nullable(),
});

// Card schemas
export const cardSchema = z.object({
  front: z.string().min(1).max(5000),
  back: z.string().min(1).max(5000),
});

// SRS Settings schema
export const srsSettingsSchema = z.object({
  reviewOrder: z.enum(['ASCENDING', 'DESCENDING', 'RANDOM']),
  notificationEnabled: z.boolean(),
  notificationTime: z.string().regex(/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/),
  forgottenCardAction: z.enum(['MOVE_TO_BOX_1', 'MOVE_DOWN_N_BOXES', 'STAY_IN_BOX']),
  moveDownBoxes: z.number().int().min(1).max(3),
  newCardsPerDay: z.number().int().min(1).max(100),
  maxReviewsPerDay: z.number().int().min(1).max(500),
});
```

### 6.3 Validation Timing

- **Client-side**: On blur, on submit
- **Server-side**: Backend validates, returns 400 with errors
- **Error display**: Below field, red text
- **Error format from backend**:

```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/folders",
  "fieldErrors": {
    "name": "Folder name is required",
    "depth": "Maximum folder depth (10 levels) exceeded"
  }
}
```

---

## 7. Theme & Styling Specifications

### 7.1 Tailwind Configuration

```javascript
// tailwind.config.js
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
        secondary: {
          DEFAULT: 'var(--secondary)',
          foreground: 'var(--secondary-foreground)',
        },
        accent: {
          DEFAULT: 'var(--accent)',
          foreground: 'var(--accent-foreground)',
        },
        destructive: {
          DEFAULT: 'var(--destructive)',
          foreground: 'var(--destructive-foreground)',
        },
        muted: {
          DEFAULT: 'var(--muted)',
          foreground: 'var(--muted-foreground)',
        },
        border: 'var(--border)',
        input: 'var(--input)',
        ring: 'var(--ring)',
      },
      borderRadius: {
        lg: 'var(--radius)',
        md: 'calc(var(--radius) - 2px)',
        sm: 'calc(var(--radius) - 4px)',
      },
    },
  },
  plugins: [],
};
```

### 7.2 CSS Variables (Theme)

```css
/* styles/globals.css */
@tailwind base;
@tailwind components;
@tailwind utilities;

@layer base {
  :root {
    --background: 0 0% 100%;
    --foreground: 222.2 84% 4.9%;

    --primary: 221.2 83.2% 53.3%;
    --primary-foreground: 210 40% 98%;

    --secondary: 210 40% 96.1%;
    --secondary-foreground: 222.2 47.4% 11.2%;

    --accent: 210 40% 96.1%;
    --accent-foreground: 222.2 47.4% 11.2%;

    --destructive: 0 84.2% 60.2%;
    --destructive-foreground: 210 40% 98%;

    --muted: 210 40% 96.1%;
    --muted-foreground: 215.4 16.3% 46.9%;

    --border: 214.3 31.8% 91.4%;
    --input: 214.3 31.8% 91.4%;
    --ring: 221.2 83.2% 53.3%;

    --radius: 0.5rem;
  }

  .dark {
    --background: 222.2 84% 4.9%;
    --foreground: 210 40% 98%;

    --primary: 217.2 91.2% 59.8%;
    --primary-foreground: 222.2 47.4% 11.2%;

    --secondary: 217.2 32.6% 17.5%;
    --secondary-foreground: 210 40% 98%;

    --accent: 217.2 32.6% 17.5%;
    --accent-foreground: 210 40% 98%;

    --destructive: 0 62.8% 30.6%;
    --destructive-foreground: 210 40% 98%;

    --muted: 217.2 32.6% 17.5%;
    --muted-foreground: 215 20.2% 65.1%;

    --border: 217.2 32.6% 17.5%;
    --input: 217.2 32.6% 17.5%;
    --ring: 224.3 76.3% 48%;
  }
}

@layer base {
  * {
    @apply border-border;
  }

  body {
    @apply bg-background text-foreground;
    font-feature-settings: "rlig" 1, "calt" 1;
  }
}
```

### 7.3 Dark Mode Implementation

```typescript
// hooks/useTheme.ts
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

// App.tsx - Apply theme on mount
function App() {
  useTheme();

  return <RouterProvider router={router} />;
}
```

### 7.4 Shadcn/ui Components

**Installation**:
```bash
npx shadcn-ui@latest init
npx shadcn-ui@latest add button
npx shadcn-ui@latest add input
npx shadcn-ui@latest add dialog
npx shadcn-ui@latest add card
# ... add other components as needed
```

**Components Used**:
- `Button`, `Input`, `Textarea`, `Label`, `Checkbox`, `Switch`
- `Dialog`, `DropdownMenu`, `Popover`, `Sheet`
- `Card`, `Separator`, `Tabs`, `Avatar`, `Badge`
- `Progress`, `ScrollArea`, `Table`, `Toast`

**Customization**:
- All components are copy-pasted into `src/components/ui/`
- Fully customizable with Tailwind classes
- No npm package dependency (tree-shakeable)

---

## 8. Performance Optimizations

### 8.1 Code Splitting & Lazy Loading

```typescript
// main.tsx
import { lazy, Suspense } from 'react';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

// Lazy load pages
const DashboardPage = lazy(() => import('@/pages/Dashboard/DashboardPage'));
const FolderListPage = lazy(() => import('@/pages/Folder/FolderListPage'));
const FolderDetailPage = lazy(() => import('@/pages/Folder/FolderDetailPage'));
const DeckDetailPage = lazy(() => import('@/pages/Deck/DeckDetailPage'));
const ReviewSessionPage = lazy(() => import('@/pages/Review/ReviewSessionPage'));
const SettingsPage = lazy(() => import('@/pages/Settings/SettingsPage'));
const StatsPage = lazy(() => import('@/pages/Stats/StatsPage'));

// Wrap with Suspense
function App() {
  return (
    <Suspense fallback={<LoadingSpinner />}>
      <RouterProvider router={router} />
    </Suspense>
  );
}
```

### 8.2 React Query Optimizations

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
    <Card onMouseEnter={handleMouseEnter}>
      {/* ... */}
    </Card>
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

### 8.3 Virtual Scrolling

```typescript
// For large lists using react-window
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

### 8.4 Debouncing

```typescript
// hooks/useDebounce.ts
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

// Usage: Search input
function SearchBar() {
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearchTerm = useDebounce(searchTerm, 300); // 300ms delay

  const { data } = useQuery({
    queryKey: ['search', debouncedSearchTerm],
    queryFn: () => api.search(debouncedSearchTerm),
    enabled: debouncedSearchTerm.length > 2,
  });

  return (
    <Input
      value={searchTerm}
      onChange={(e) => setSearchTerm(e.target.value)}
      placeholder="Search..."
    />
  );
}
```

### 8.5 Memoization

```typescript
// Memoize expensive calculations
import { useMemo } from 'react';

function FolderTree({ folders }: { folders: Folder[] }) {
  // Build tree structure (expensive)
  const tree = useMemo(() => {
    return buildTree(folders);
  }, [folders]);

  return <TreeView tree={tree} />;
}

// Memoize list items
import { memo } from 'react';

const FolderCard = memo(({ folder }: { folder: Folder }) => {
  return <Card>...</Card>;
});
```

---

## 9. Error Handling Specifications

### 9.1 Error Boundary

```typescript
// components/common/ErrorBoundary.tsx
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
    // Send to error tracking service (Sentry)
  }

  render() {
    if (this.state.hasError) {
      return this.props.fallback || (
        <div className="flex flex-col items-center justify-center h-screen">
          <h2 className="text-2xl font-bold mb-2">Something went wrong</h2>
          <p className="text-gray-600 mb-4">{this.state.error?.message}</p>
          <Button onClick={() => window.location.reload()}>
            Reload Page
          </Button>
        </div>
      );
    }

    return this.props.children;
  }
}
```

### 9.2 API Error Handling

```typescript
// lib/errorHandler.ts
import { AxiosError } from 'axios';
import { toast } from 'sonner';

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  traceId?: string;
  fieldErrors?: Record<string, string>;
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

// Usage in hooks
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

### 9.3 Error Scenarios

| Status Code | Scenario | Handling |
|-------------|----------|----------|
| 400 | Validation errors | Show field errors below inputs |
| 401 | Unauthorized | Auto-refresh token, retry, or redirect to login |
| 403 | Forbidden | Show error toast: "Access denied" |
| 404 | Not found | Show "Not found" message |
| 422 | Business rule error | Show error toast with backend message |
| 500 | Server error | Show generic error, offer retry |
| Network error | No connection | Show toast: "Network error, please check connection" |

---

## 10. Accessibility (A11y) Specifications

### 10.1 Keyboard Navigation

- **Tab order**: Logical focus flow (top to bottom, left to right)
- **Focus indicators**: Blue ring around focused elements (`ring-2 ring-blue-500`)
- **Keyboard shortcuts**:
  - `Escape`: Close modals/dialogs
  - `Enter`: Submit forms, confirm actions
  - `Arrow keys`: Navigate lists, tree nodes
  - `1-4`: Rate cards in review session (optional)

### 10.2 ARIA Labels

```tsx
// Example: Button with icon only
<Button aria-label="Create new folder">
  <Plus />
</Button>

// Example: Dialog
<Dialog aria-labelledby="dialog-title" aria-describedby="dialog-description">
  <DialogTitle id="dialog-title">Create Folder</DialogTitle>
  <DialogDescription id="dialog-description">
    Enter folder name and description
  </DialogDescription>
</Dialog>

// Example: Input with error
<Input
  aria-label="Folder name"
  aria-invalid={!!errors.name}
  aria-describedby={errors.name ? "name-error" : undefined}
/>
{errors.name && (
  <p id="name-error" role="alert" className="text-red-500">
    {errors.name.message}
  </p>
)}
```

### 10.3 Screen Reader Support

- **Announce page changes**: Use `<title>` updates and ARIA live regions
- **Announce errors**: Use `role="alert"` for error messages
- **Announce loading**: Use `aria-busy="true"` and loading spinners with `aria-label`
- **Descriptive labels**: All form inputs have `<label>` or `aria-label`

### 10.4 Contrast Ratios

- **Text**: WCAG AA minimum 4.5:1 for normal text
- **Large text**: WCAG AA minimum 3:1 for 18px+ or 14px+ bold
- **Interactive elements**: Focus indicators with sufficient contrast
- **Dark mode**: Ensure contrast ratios meet WCAG AA in both themes

### 10.5 Focus Management

- **Modals**: Trap focus inside modal while open
- **Dialogs**: Focus first input on open, restore focus on close
- **Toasts**: Do NOT steal focus (use `aria-live="polite"`)

---

## 11. Build & Deployment Specifications

### 11.1 Vite Configuration

```typescript
// vite.config.ts
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

### 11.2 Environment Variables

```bash
# .env.example
# API Base URL
VITE_API_BASE_URL=http://localhost:8080/api

# Environment
VITE_ENV=development
```

```typescript
// Usage in code
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
```

### 11.3 Build Commands

```json
// package.json
{
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0",
    "type-check": "tsc --noEmit"
  }
}
```

### 11.4 Production Optimizations

- **Bundle size**: Target < 500KB gzipped
- **Code splitting**: Route-based lazy loading
- **Tree shaking**: Remove unused imports
- **Minification**: Terser for JS, CSS minification
- **Compression**: Enable gzip/brotli on server
- **Caching**: Set long cache headers for assets (hash in filename)

---

## 12. Testing Strategy

### 12.1 Unit Testing (Vitest + React Testing Library)

```typescript
// hooks/useFolder.test.ts
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

### 12.2 Component Testing

```typescript
// components/folder/FolderCard.test.tsx
import { render, screen, fireEvent } from '@testing-library/react';
import { FolderCard } from './FolderCard';

describe('FolderCard', () => {
  const mockFolder = {
    id: '1',
    name: 'Test Folder',
    description: 'Test description',
    totalDecks: 5,
    totalCards: 100,
    dueCards: 10,
  };

  it('renders folder info correctly', () => {
    render(<FolderCard folder={mockFolder} />);

    expect(screen.getByText('Test Folder')).toBeInTheDocument();
    expect(screen.getByText('Test description')).toBeInTheDocument();
    expect(screen.getByText('5 decks')).toBeInTheDocument();
    expect(screen.getByText('100 cards')).toBeInTheDocument();
  });

  it('calls onClick when clicked', () => {
    const handleClick = vi.fn();
    render(<FolderCard folder={mockFolder} onClick={handleClick} />);

    fireEvent.click(screen.getByText('Test Folder'));

    expect(handleClick).toHaveBeenCalledTimes(1);
  });
});
```

### 12.3 Test Coverage Goals

- Unit tests: ≥ 70% coverage for core logic (hooks, utils)
- Component tests: Key user interactions
- Critical paths:
  - Folder CRUD operations
  - Deck/Card CRUD operations
  - Review session flow
  - Import/Export
  - Auth flow (login, logout, token refresh)

---

## 13. Conclusion

This specification document provides comprehensive guidelines for building the RepeatWise React web application. Key highlights:

**Architecture**:
- Clean separation: Server state (TanStack Query) + Auth state (Context) + UI state (Zustand)
- No Redux overhead for MVP
- Type-safe with TypeScript throughout

**Component Design**:
- 20+ reusable components specified
- Shadcn/ui for accessible, customizable UI
- Proper state management and data fetching patterns

**API Integration**:
- Secure token management (access token in memory, refresh token in HTTP-only cookie)
- Automatic token refresh with request queuing
- Optimistic updates for better UX

**Performance**:
- Code splitting, lazy loading
- Virtual scrolling for large lists
- Debouncing, memoization
- React Query caching and prefetching

**User Experience**:
- Light/Dark mode with system preference detection
- Responsive design (mobile, tablet, desktop)
- Accessible (keyboard navigation, ARIA labels, screen reader support)
- Error handling with user-friendly messages

**Developer Experience**:
- Fast HMR with Vite
- Type-safe API calls
- Easy component customization
- Clear folder structure

This specification is ready for implementation. All components follow consistent patterns and best practices for maintainability and scalability.

---

**Version**: 1.0
**Last Updated**: January 2025
**Status**: Ready for Implementation
