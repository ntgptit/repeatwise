# Web Coding Convention - RepeatWise

## 1. Overview

Document này định nghĩa coding convention cho web frontend của RepeatWise, sử dụng **React 18 + TypeScript + Vite**.

**Tech Stack**:
- Core: React 18, TypeScript 5.x
- Build Tool: Vite
- State Management: TanStack Query v5, Context API, Zustand
- Routing: React Router v6
- HTTP Client: Axios
- Styling: Tailwind CSS
- UI Components: Shadcn/ui
- Forms: React Hook Form + Zod
- i18n: react-i18next

---

## 2. General Principles

### 2.1 Code Style Guide

**Base Style**: [Airbnb JavaScript Style Guide](https://github.com/airbnb/javascript) + [Airbnb React Style Guide](https://github.com/airbnb/javascript/tree/master/react)

**Key Rules**:
- Indentation: 2 spaces (not tabs)
- Line length: 100 characters max
- Quotes: Single quotes for strings
- Semicolons: Required
- File encoding: UTF-8
- Line endings: LF (Unix style)

### 2.2 Code Quality Tools

**Required Tools**:
- **ESLint**: Linting and code quality
- **Prettier**: Code formatting
- **TypeScript**: Type checking
- **Vite**: Build and hot reload

**Configuration Files**:
```json
// .eslintrc.json
{
  "extends": [
    "eslint:recommended",
    "plugin:react/recommended",
    "plugin:react-hooks/recommended",
    "plugin:@typescript-eslint/recommended",
    "prettier"
  ],
  "rules": {
    "react/react-in-jsx-scope": "off",
    "react/prop-types": "off",
    "@typescript-eslint/no-unused-vars": ["error", { "argsIgnorePattern": "^_" }]
  }
}

// .prettierrc
{
  "semi": true,
  "singleQuote": true,
  "tabWidth": 2,
  "trailingComma": "es5",
  "printWidth": 100,
  "arrowParens": "always"
}
```

---

## 3. Naming Conventions

### 3.1 File and Folder Naming

**Format**:

| Type | Convention | Example |
|------|-----------|---------|
| Components | PascalCase | `FolderTree.tsx`, `LoginPage.tsx` |
| Hooks | camelCase with `use` prefix | `useFolder.ts`, `useAuth.ts` |
| Utils | camelCase | `formatDate.ts`, `api.ts` |
| Types | PascalCase | `api.ts`, `entities.ts` |
| Constants | UPPER_SNAKE_CASE or camelCase | `API_ENDPOINTS.ts`, `routes.ts` |
| Styles | Same as component | `FolderTree.module.css` |

**Folder Structure**:
```
src/
├── components/           // PascalCase
│   ├── common/
│   │   ├── Button.tsx
│   │   └── Input.tsx
│   └── folder/
│       ├── FolderTree.tsx
│       └── FolderCard.tsx
├── pages/               // PascalCase
│   ├── Auth/
│   │   ├── LoginPage.tsx
│   │   └── RegisterPage.tsx
├── hooks/               // camelCase
│   ├── useFolder.ts
│   └── useAuth.ts
├── services/            // camelCase
│   ├── api.ts
│   └── folderService.ts
├── types/               // camelCase
│   ├── api.ts
│   └── entities.ts
├── constants/           // camelCase
│   ├── routes.ts
│   └── api.ts
└── lib/                 // camelCase
    ├── utils.ts
    └── queryClient.ts
```

### 3.2 Component Naming

**React Components**: PascalCase

```tsx
✅ Good
export function FolderTree({ folders }: FolderTreeProps) { }
export const LoginPage: React.FC = () => { }
export default function DashboardPage() { }

❌ Bad
export function folderTree() { }  // Should be PascalCase
export const login_page = () => { }  // Should be PascalCase
```

**Component File Structure**:
```tsx
// FolderTree.tsx

// 1. Imports (grouped)
import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { FolderTreeNode } from './FolderTreeNode';
import type { Folder } from '@/types/entities';
import { cn } from '@/lib/utils';

// 2. Types/Interfaces
interface FolderTreeProps {
  folders: Folder[];
  onFolderClick?: (folder: Folder) => void;
  className?: string;
}

// 3. Component
export function FolderTree({ folders, onFolderClick, className }: FolderTreeProps) {
  // Component logic
  return (
    <div className={cn('folder-tree', className)}>
      {/* JSX */}
    </div>
  );
}

// 4. Sub-components (if small and related)
function FolderTreeEmptyState() {
  return <div>No folders</div>;
}

// 5. Default export (optional)
export default FolderTree;
```

### 3.3 Variable and Function Naming

**Format**: camelCase

```tsx
✅ Good
const userId = '123';
const folderName = 'My Folder';
const isDarkMode = true;
const hasPermission = false;

function handleFolderClick(folder: Folder) { }
function calculateDueDate(card: Card): Date { }
const getUserFolders = async (userId: string) => { };

❌ Bad
const user_id = '123';  // Should be camelCase
const FolderName = 'My Folder';  // Should be camelCase (not PascalCase for variables)
function HandleClick() { }  // Should be camelCase (PascalCase only for components)
```

**Boolean Variables**: Use `is`, `has`, `should`, `can` prefix

```tsx
✅ Good
const isLoading = true;
const hasError = false;
const shouldRender = true;
const canEdit = false;

❌ Bad
const loading = true;  // Not clear it's boolean
const error = false;  // Could be error object or boolean
```

**Event Handlers**: Use `handle` prefix

```tsx
✅ Good
const handleFolderClick = (folder: Folder) => { };
const handleSubmit = (e: FormEvent) => { };
const handleChange = (value: string) => { };

❌ Bad
const onFolderClick = () => { };  // Use 'handle' for handlers, 'on' for props
const folderClick = () => { };  // Not clear it's a handler
```

### 3.4 TypeScript Type Naming

**Interfaces and Types**: PascalCase

```tsx
✅ Good
interface FolderTreeProps {
  folders: Folder[];
  onFolderClick?: (folder: Folder) => void;
}

type Folder = {
  id: string;
  name: string;
};

type FolderResponse = ApiResponse<Folder>;

❌ Bad
interface folderTreeProps { }  // Should be PascalCase
type folder = { };  // Should be PascalCase
```

**Generic Type Parameters**: Single uppercase letter or PascalCase

```tsx
✅ Good
function identity<T>(value: T): T { }
function create<TEntity>(data: TEntity): TEntity { }
interface ApiResponse<TData> {
  data: TData;
}

❌ Bad
function identity<t>(value: t): t { }  // Should be uppercase
```

---

## 3.5 Clean Code & Readability (BẮT BUỘC)

### 🔴 3.5.1 Tên biến, function, component phải rõ ràng (BẮT BUỘC)

**Không viết tắt tùy tiện, tên phải tự giải thích.**

❌ **SAI - Tên mơ hồ, viết tắt:**
```tsx
// Component name
export function FldrTr() { }  // Folder Tree?
export function UsrMgr() { }  // User Manager?

// Variable name
const usr = getUserData();
const fldr = folderData;
const cnt = folders.length;
const dt = new Date();

// Function name
function proc(data: any) { }  // Process what?
function get() { }  // Get what?
function hdl() { }  // Handle what?
```

✅ **ĐÚNG - Tên rõ ràng, có ý nghĩa:**
```tsx
// Component name
export function FolderTree() { }
export function UserManager() { }

// Variable name
const currentUser = getUserData();
const selectedFolder = folderData;
const totalFolderCount = folders.length;
const createdDate = new Date();

// Function name
function processFolderData(data: FolderData) { }
function getUserFolders(userId: string) { }
function handleFolderClick(folder: Folder) { }
```

**Các viết tắt được chấp nhận:**
```tsx
// OK - Widely known abbreviations
const userId: string;
const apiUrl: string;
const httpClient: AxiosInstance;
const htmlContent: string;
const jsonData: object;
const csvFile: File;

// NOT OK - Custom abbreviations
const usrId;  // Use userId
const fldrCnt;  // Use folderCount
const dtFmt;  // Use dateFormat
```

### 🔴 3.5.2 Function/Component không dài quá 30 dòng (BẮT BUỘC)

**Component/function phải ngắn gọn, tập trung vào một nhiệm vụ.**

❌ **SAI - Component quá dài (>30 dòng):**
```tsx
export function FolderListPage() {
  // State declarations - 5 lines
  const [folders, setFolders] = useState<Folder[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selectedFolder, setSelectedFolder] = useState<Folder | null>(null);

  // Fetch folders - 15 lines
  useEffect(() => {
    const fetchFolders = async () => {
      try {
        setIsLoading(true);
        const response = await api.get('/folders');
        setFolders(response.data);
      } catch (err) {
        if (err instanceof AxiosError) {
          setError(err.response?.data.message || 'Failed to fetch folders');
        } else {
          setError('An unexpected error occurred');
        }
      } finally {
        setIsLoading(false);
      }
    };
    fetchFolders();
  }, []);

  // Handlers - 15 lines
  const handleDeleteFolder = async (folderId: string) => {
    try {
      await api.delete(`/folders/${folderId}`);
      setFolders(folders.filter(f => f.id !== folderId));
      toast.success('Folder deleted');
    } catch (err) {
      toast.error('Failed to delete folder');
    }
  };

  const handleEditFolder = (folder: Folder) => {
    setSelectedFolder(folder);
  };

  // Render - 20 lines
  if (isLoading) return <LoadingSpinner />;
  if (error) return <ErrorState message={error} />;

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">My Folders</h1>
      <div className="grid grid-cols-3 gap-4">
        {folders.map((folder) => (
          <FolderCard
            key={folder.id}
            folder={folder}
            onEdit={handleEditFolder}
            onDelete={handleDeleteFolder}
          />
        ))}
      </div>
    </div>
  );
}
// Total: ~60 lines - TOO LONG!
```

✅ **ĐÚNG - Tách thành components/hooks nhỏ (<30 dòng):**
```tsx
// Custom hook - useFolderList.ts (~15 lines)
export function useFolderList() {
  const { data: folders, isLoading, error } = useQuery({
    queryKey: folderKeys.lists(),
    queryFn: () => folderService.getFolders(),
  });

  const { mutate: deleteFolder } = useMutation({
    mutationFn: (folderId: string) => folderService.deleteFolder(folderId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: folderKeys.lists() });
      toast.success('Folder deleted');
    },
  });

  return { folders, isLoading, error, deleteFolder };
}

// Main component - FolderListPage.tsx (~20 lines)
export function FolderListPage() {
  const { folders, isLoading, error, deleteFolder } = useFolderList();

  if (isLoading) return <LoadingSpinner />;
  if (error) return <ErrorState message="Failed to load folders" />;

  return (
    <div className="p-6">
      <FolderListHeader />
      <FolderGrid folders={folders || []} onDelete={deleteFolder} />
    </div>
  );
}

// Sub-component - FolderGrid.tsx (~15 lines)
interface FolderGridProps {
  folders: Folder[];
  onDelete: (folderId: string) => void;
}

export function FolderGrid({ folders, onDelete }: FolderGridProps) {
  return (
    <div className="grid grid-cols-3 gap-4">
      {folders.map((folder) => (
        <FolderCard key={folder.id} folder={folder} onDelete={onDelete} />
      ))}
    </div>
  );
}
```

### 🔴 3.5.3 Tránh deep nesting (>2 levels) (BẮT BUỘC)

**Sử dụng Early Return và Guard Clauses.**

❌ **SAI - Deep nesting:**
```tsx
function processFolderData(folder: Folder | null) {
  if (folder) {
    if (folder.isActive) {
      if (folder.hasPermission) {
        if (folder.children && folder.children.length > 0) {
          // Process children - Level 4 nesting!
        }
      }
    }
  }
}
```

✅ **ĐÚNG - Early return, no nesting:**
```tsx
function processFolderData(folder: Folder | null) {
  // Guard clauses
  if (!folder) {
    console.warn('Folder is null');
    return;
  }

  if (!folder.isActive) {
    console.warn('Folder is not active');
    return;
  }

  if (!folder.hasPermission) {
    console.warn('No permission for folder');
    return;
  }

  if (!folder.children || folder.children.length === 0) {
    return;
  }

  // Process children - No nesting!
  processChildren(folder.children);
}
```

**Trong React components:**
```tsx
❌ **SAI:**
function FolderDetail({ folderId }: Props) {
  const { data, isLoading, error } = useFolder(folderId);

  return (
    <div>
      {isLoading ? (
        <LoadingSpinner />
      ) : error ? (
        <ErrorState message={error.message} />
      ) : data ? (
        <div>
          {data.folders.length > 0 ? (
            <FolderList folders={data.folders} />
          ) : (
            <EmptyState />
          )}
        </div>
      ) : null}
    </div>
  );
}

✅ **ĐÚNG:**
function FolderDetail({ folderId }: Props) {
  const { data, isLoading, error } = useFolder(folderId);

  // Early returns
  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return <ErrorState message={error.message} />;
  }

  if (!data) {
    return null;
  }

  if (data.folders.length === 0) {
    return <EmptyState />;
  }

  return <FolderList folders={data.folders} />;
}
```

### 🔴 3.5.4 Dùng const cho biến không thay đổi (BẮT BUỘC)

**Luôn dùng `const`, chỉ dùng `let` khi thực sự cần reassign.**

❌ **SAI:**
```tsx
let userId = '123';  // Never reassigned - should be const
let folderName = 'My Folder';  // Never reassigned - should be const
let maxDepth = 10;  // Constant - should be const

function processFolders() {
  let folders = getFolders();  // Never reassigned - should be const
  let count = folders.length;  // Never reassigned - should be const

  // ... 50 lines of code
  // Accidentally reassigned!
  folders = [];  // BUG!
}
```

✅ **ĐÚNG:**
```tsx
const userId = '123';
const folderName = 'My Folder';
const MAX_FOLDER_DEPTH = 10;  // UPPER_SNAKE_CASE for constants

function processFolders() {
  const folders = getFolders();
  const count = folders.length;

  // folders = [];  // Compile error - GOOD!
}

// Use let only when needed
function processItems(items: Item[]) {
  let processedCount = 0;  // Will be incremented

  for (const item of items) {
    processItem(item);
    processedCount++;  // Reassignment needed
  }

  return processedCount;
}
```

### 🔴 3.5.5 Tối đa 3 parameters trong function (BẮT BUỘC)

**Nếu >3 parameters, dùng object parameter.**

❌ **SAI - Quá nhiều parameters:**
```tsx
// 6 parameters - TOO MANY!
function createFolder(
  name: string,
  description: string,
  parentId: string | null,
  userId: string,
  isPublic: boolean,
  color: string
) {
  // ...
}

// Cách gọi - khó đọc, dễ nhầm lẫn thứ tự
createFolder('My Folder', 'Description', null, '123', false, 'blue');
```

✅ **ĐÚNG - Dùng object parameter:**
```tsx
interface CreateFolderParams {
  name: string;
  description: string;
  parentId: string | null;
  userId: string;
  isPublic: boolean;
  color: string;
}

function createFolder(params: CreateFolderParams) {
  const { name, description, parentId, userId, isPublic, color } = params;
  // ...
}

// Cách gọi - rõ ràng, dễ đọc
createFolder({
  name: 'My Folder',
  description: 'Description',
  parentId: null,
  userId: '123',
  isPublic: false,
  color: 'blue',
});
```

**Nguyên tắc:**
- ✅ 0-3 parameters: OK
- ⚠️ 4-5 parameters: Cân nhắc refactor
- ❌ >5 parameters: **BẮT BUỘC** dùng object parameter

### 🔴 3.5.6 Utility Functions và Libraries (BẮT BUỘC)

**Sử dụng utility libraries thay vì tự implement.**

**Required dependencies:**
```json
{
  "dependencies": {
    "lodash-es": "^4.17.21",
    "date-fns": "^3.0.0",
    "clsx": "^2.0.0",
    "tailwind-merge": "^2.0.0"
  }
}
```

❌ **SAI - Tự implement utilities:**
```tsx
// String operations
const trimmed = str.trim().replace(/\s+/g, ' ');
const isEmpty = !str || str.length === 0;

// Array operations
const uniqueIds = [...new Set(ids)];
const chunked = [];
for (let i = 0; i < array.length; i += size) {
  chunked.push(array.slice(i, i + size));
}

// Date operations
const formattedDate = new Date().toLocaleDateString('en-US', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
});
```

✅ **ĐÚNG - Dùng libraries:**
```tsx
import { isEmpty, uniq, chunk, debounce, throttle } from 'lodash-es';
import { format, formatDistance, parseISO, isValid } from 'date-fns';
import { clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

// ===== Lodash-es =====

// String operations
const trimmedString = isEmpty(str);

// Array operations
const uniqueIds = uniq(ids);
const chunkedArray = chunk(array, size);

// Function utilities
const debouncedSearch = debounce((query: string) => {
  performSearch(query);
}, 300);

const throttledScroll = throttle(() => {
  handleScroll();
}, 100);

// ===== date-fns =====

// Date formatting
const formattedDate = format(new Date(), 'PPP');  // Jan 1, 2024
const relativeTime = formatDistance(date, new Date(), { addSuffix: true });

// Date validation
if (isValid(parseISO(dateString))) {
  // Valid date
}

// ===== clsx + tailwind-merge (cn utility) =====

// Combine class names
import { cn } from '@/lib/utils';

const buttonClasses = cn(
  'px-4 py-2 rounded-md',
  variant === 'primary' && 'bg-blue-600 text-white',
  variant === 'secondary' && 'bg-gray-200 text-gray-900',
  isDisabled && 'opacity-50 cursor-not-allowed',
  className
);
```

**cn utility (src/lib/utils.ts):**
```tsx
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}
```

### 🔴 3.5.7 Quản lý text/message bằng i18n (BẮT BUỘC)

**Tất cả text hiển thị phải externalize vào i18n files.**

**Setup i18next:**
```tsx
// src/i18n/config.ts
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';

import en from './locales/en.json';
import vi from './locales/vi.json';

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources: {
      en: { translation: en },
      vi: { translation: vi },
    },
    fallbackLng: 'en',
    interpolation: {
      escapeValue: false,
    },
  });

export default i18n;
```

**Translation files:**
```json
// src/i18n/locales/en.json
{
  "common": {
    "loading": "Loading...",
    "error": "An error occurred",
    "save": "Save",
    "cancel": "Cancel",
    "delete": "Delete"
  },
  "folder": {
    "title": "Folders",
    "create": "Create Folder",
    "edit": "Edit Folder",
    "delete": "Delete Folder",
    "nameRequired": "Folder name is required",
    "nameTooLong": "Folder name must not exceed {{max}} characters",
    "maxDepth": "Folder cannot exceed {{max}} levels",
    "deleteConfirm": "Are you sure you want to delete this folder?"
  },
  "errors": {
    "notFound": "{{resource}} not found",
    "unauthorized": "You are not authorized to access this resource",
    "serverError": "A server error occurred. Please try again."
  }
}

// src/i18n/locales/vi.json
{
  "common": {
    "loading": "Đang tải...",
    "error": "Có lỗi xảy ra",
    "save": "Lưu",
    "cancel": "Hủy",
    "delete": "Xóa"
  },
  "folder": {
    "title": "Thư mục",
    "create": "Tạo thư mục",
    "edit": "Sửa thư mục",
    "delete": "Xóa thư mục",
    "nameRequired": "Tên thư mục không được để trống",
    "nameTooLong": "Tên thư mục không được vượt quá {{max}} ký tự",
    "maxDepth": "Thư mục không thể vượt quá {{max}} cấp độ",
    "deleteConfirm": "Bạn có chắc chắn muốn xóa thư mục này?"
  }
}
```

❌ **SAI - Hardcode text trong component:**
```tsx
export function FolderCard({ folder }: FolderCardProps) {
  const handleDelete = () => {
    if (confirm('Bạn có chắc chắn muốn xóa thư mục này?')) {
      deleteFolder(folder.id);
      toast.success('Thư mục đã được xóa');
    }
  };

  return (
    <div>
      <h3>{folder.name}</h3>
      <button onClick={handleDelete}>Xóa</button>
      <button>Sửa</button>
    </div>
  );
}
```

✅ **ĐÚNG - Dùng i18next:**
```tsx
import { useTranslation } from 'react-i18next';

export function FolderCard({ folder }: FolderCardProps) {
  const { t } = useTranslation();

  const handleDelete = () => {
    if (confirm(t('folder.deleteConfirm'))) {
      deleteFolder(folder.id);
      toast.success(t('folder.deleteSuccess'));
    }
  };

  return (
    <div>
      <h3>{folder.name}</h3>
      <button onClick={handleDelete}>{t('common.delete')}</button>
      <button>{t('common.edit')}</button>
    </div>
  );
}
```

**Với parameters:**
```tsx
// Validation message with parameter
const maxNameLength = 100;
const errorMessage = t('folder.nameTooLong', { max: maxNameLength });

// Error message with resource name
const errorMessage = t('errors.notFound', { resource: 'Folder' });
```

**Lợi ích:**
- ✅ Support đa ngôn ngữ (i18n)
- ✅ Tập trung quản lý text
- ✅ Dễ dàng thay đổi nội dung
- ✅ Consistent messages
- ✅ Support parameters

---

## 4. TypeScript Best Practices

### 4.1 Type Definitions

**Use `interface` for object shapes, `type` for unions/intersections**:

```tsx
✅ Good
interface User {
  id: string;
  email: string;
  name: string;
}

type UserRole = 'admin' | 'user' | 'guest';
type UserWithRole = User & { role: UserRole };

❌ Bad
type User = {  // Use interface for object shapes
  id: string;
  email: string;
}
```

### 4.2 Props Types

**Define explicit props interface**:

```tsx
✅ Good
interface FolderCardProps {
  folder: Folder;
  onEdit?: (folder: Folder) => void;
  onDelete?: (folderId: string) => void;
  className?: string;
}

export function FolderCard({ folder, onEdit, onDelete, className }: FolderCardProps) {
  // Component logic
}

❌ Bad
export function FolderCard(props: any) {  // Never use 'any'
  const { folder, onEdit } = props;
}

export function FolderCard({ folder, onEdit }: { folder: any, onEdit: any }) {
  // Inline types are hard to reuse
}
```

### 4.3 Avoid `any`

```tsx
✅ Good
const folders: Folder[] = [];
const response: ApiResponse<Folder> = await api.get('/folders');

// Use 'unknown' if type is truly unknown
const data: unknown = JSON.parse(jsonString);
if (isFolder(data)) {
  // Type guard
  const folder: Folder = data;
}

❌ Bad
const folders: any = [];  // Never use 'any'
const response: any = await api.get('/folders');
```

### 4.4 Type Inference

**Let TypeScript infer when obvious**:

```tsx
✅ Good
const userId = '123';  // Type inferred as string
const count = 0;  // Type inferred as number
const folders = useFolderTree();  // Type inferred from hook

// Explicit when needed
const folders: Folder[] = [];  // Empty array needs explicit type

❌ Bad
const userId: string = '123';  // Redundant type annotation
const count: number = 0;  // Redundant
```

### 4.5 Utility Types

**Use built-in utility types**:

```tsx
✅ Good
type PartialFolder = Partial<Folder>;  // All properties optional
type RequiredFolder = Required<Folder>;  // All properties required
type FolderKeys = keyof Folder;  // Union of keys
type FolderName = Pick<Folder, 'name'>;  // Pick specific properties
type FolderWithoutId = Omit<Folder, 'id'>;  // Omit specific properties

❌ Bad
type PartialFolder = {  // Manually making all optional
  id?: string;
  name?: string;
  // ...
};
```

---

## 5. React Component Patterns

### 5.1 Functional Components

**Always use functional components** (not class components):

```tsx
✅ Good - Functional component with hooks
import { useState } from 'react';

interface CounterProps {
  initialCount?: number;
}

export function Counter({ initialCount = 0 }: CounterProps) {
  const [count, setCount] = useState(initialCount);

  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(count + 1)}>Increment</button>
    </div>
  );
}

❌ Bad - Class component (deprecated pattern)
class Counter extends React.Component {
  state = { count: 0 };

  render() {
    return <div>{this.state.count}</div>;
  }
}
```

### 5.2 Component Organization

**Smart (Container) vs Dumb (Presentational) Components**:

```tsx
// Smart Component (Container) - Handles data fetching and logic
export default function FolderListPage() {
  const { data: folders, isLoading, error } = useFolderTree();
  const { mutate: deleteFolder } = useDeleteFolder();

  const handleDeleteFolder = (folderId: string) => {
    deleteFolder(folderId);
  };

  if (isLoading) return <LoadingSpinner />;
  if (error) return <ErrorState message="Failed to load folders" />;

  return (
    <div>
      <h1>My Folders</h1>
      <FolderTree folders={folders || []} onDelete={handleDeleteFolder} />
    </div>
  );
}

// Dumb Component (Presentational) - Pure UI, no logic
interface FolderTreeProps {
  folders: Folder[];
  onDelete: (folderId: string) => void;
}

export function FolderTree({ folders, onDelete }: FolderTreeProps) {
  return (
    <div className="folder-tree">
      {folders.map((folder) => (
        <FolderCard key={folder.id} folder={folder} onDelete={onDelete} />
      ))}
    </div>
  );
}
```

### 5.3 Props Destructuring

**Destructure props in function parameter**:

```tsx
✅ Good
export function FolderCard({ folder, onEdit, onDelete }: FolderCardProps) {
  return <div>{folder.name}</div>;
}

❌ Bad
export function FolderCard(props: FolderCardProps) {
  return <div>{props.folder.name}</div>;  // Repetitive 'props.'
}
```

### 5.4 Default Props

**Use default parameters**:

```tsx
✅ Good
interface ButtonProps {
  variant?: 'primary' | 'secondary';
  size?: 'sm' | 'md' | 'lg';
}

export function Button({ variant = 'primary', size = 'md' }: ButtonProps) {
  return <button className={`btn-${variant} btn-${size}`}>Click</button>;
}

❌ Bad (deprecated)
Button.defaultProps = {
  variant: 'primary',
  size: 'md',
};
```

### 5.5 Conditional Rendering

```tsx
✅ Good
// Short circuit for simple conditions
{isLoading && <LoadingSpinner />}

// Ternary for if-else
{isLoading ? <LoadingSpinner /> : <FolderTree folders={folders} />}

// Early return for complex conditions
if (isLoading) {
  return <LoadingSpinner />;
}

if (error) {
  return <ErrorState message={error.message} />;
}

return <FolderTree folders={folders} />;

❌ Bad
// Don't use long ternary chains
{isLoading ? <LoadingSpinner /> : error ? <ErrorState /> : folders.length === 0 ? <EmptyState /> : <FolderTree folders={folders} />}
```

### 5.6 List Rendering

```tsx
✅ Good
{folders.map((folder) => (
  <FolderCard key={folder.id} folder={folder} />
))}

❌ Bad
{folders.map((folder, index) => (
  <FolderCard key={index} folder={folder} />  // Don't use index as key
))}
```

---

## 6. Hooks Best Practices

### 6.1 Custom Hook Structure

**File**: `src/hooks/useFolder.ts`

```tsx
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { folderService } from '@/services/folderService';
import type { Folder, CreateFolderRequest } from '@/types/api';

// Query keys (centralized)
export const folderKeys = {
  all: ['folders'] as const,
  lists: () => [...folderKeys.all, 'list'] as const,
  list: (filters: string) => [...folderKeys.lists(), { filters }] as const,
  details: () => [...folderKeys.all, 'detail'] as const,
  detail: (id: string) => [...folderKeys.details(), id] as const,
  tree: () => [...folderKeys.all, 'tree'] as const,
};

// Query hook
export function useFolderTree() {
  return useQuery({
    queryKey: folderKeys.tree(),
    queryFn: () => folderService.getFolderTree(),
    staleTime: 5 * 60 * 1000,
  });
}

// Mutation hook
export function useCreateFolder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateFolderRequest) => folderService.createFolder(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: folderKeys.tree() });
    },
  });
}
```

**Custom Hook Naming**: Always start with `use`

```tsx
✅ Good
export function useFolder(folderId: string) { }
export function useDebounce<T>(value: T, delay: number): T { }
export function useLocalStorage(key: string) { }

❌ Bad
export function folder(folderId: string) { }  // Must start with 'use'
export function debounce<T>(value: T): T { }  // Must start with 'use'
```

### 6.2 Hook Dependencies

**Always specify dependencies in useEffect, useMemo, useCallback**:

```tsx
✅ Good
useEffect(() => {
  fetchFolders(userId);
}, [userId]);  // Explicit dependency

const memoizedValue = useMemo(() => {
  return calculateExpensiveValue(a, b);
}, [a, b]);  // Explicit dependencies

const handleClick = useCallback(() => {
  doSomething(userId);
}, [userId]);  // Explicit dependency

❌ Bad
useEffect(() => {
  fetchFolders(userId);
}, []);  // Missing dependency - will use stale userId

// eslint-disable-next-line react-hooks/exhaustive-deps
useEffect(() => {
  fetchFolders(userId);
}, []);  // Don't disable ESLint rule
```

### 6.3 Hook Rules

**Rules of Hooks** (enforced by ESLint):
1. Only call hooks at the top level (not in loops, conditions, nested functions)
2. Only call hooks in React functions (components or custom hooks)

```tsx
✅ Good
export function MyComponent() {
  const [count, setCount] = useState(0);  // Top level
  const data = useQuery({ ... });  // Top level

  return <div>{count}</div>;
}

❌ Bad
export function MyComponent() {
  if (condition) {
    const [count, setCount] = useState(0);  // Inside condition - ERROR
  }

  for (let i = 0; i < 10; i++) {
    useEffect(() => { });  // Inside loop - ERROR
  }
}
```

---

## 7. State Management

### 7.1 React Query (Server State)

**Use TanStack Query for all API data**:

```tsx
// Fetch data
const { data, isLoading, error } = useQuery({
  queryKey: ['folders', userId],
  queryFn: () => folderService.getFolders(userId),
});

// Mutate data
const { mutate, isPending } = useMutation({
  mutationFn: (data: CreateFolderRequest) => folderService.createFolder(data),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['folders'] });
  },
});
```

### 7.2 Context API (Auth State)

**File**: `src/contexts/AuthContext.tsx`

```tsx
import { createContext, useContext, useState, ReactNode } from 'react';

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);

  const login = async (email: string, password: string) => {
    const response = await authService.login(email, password);
    setUser(response.user);
  };

  const logout = async () => {
    await authService.logout();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: !!user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

// Custom hook for convenience
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
```

### 7.3 Zustand (UI State)

**File**: `src/store/uiStore.ts`

```tsx
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface UIState {
  sidebarOpen: boolean;
  toggleSidebar: () => void;
  theme: 'light' | 'dark' | 'system';
  setTheme: (theme: 'light' | 'dark' | 'system') => void;
}

export const useUIStore = create<UIState>()(
  persist(
    (set) => ({
      sidebarOpen: true,
      toggleSidebar: () => set((state) => ({ sidebarOpen: !state.sidebarOpen })),
      theme: 'system',
      setTheme: (theme) => set({ theme }),
    }),
    {
      name: 'repeatwise-ui',
    }
  )
);
```

---

## 8. Styling Conventions

### 8.1 Tailwind CSS

**Use Tailwind utility classes**:

```tsx
✅ Good
<div className="flex items-center gap-4 p-4 bg-white dark:bg-gray-800 rounded-lg shadow-md">
  <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Title</h1>
</div>

// Use cn() utility for conditional classes
import { cn } from '@/lib/utils';

<button
  className={cn(
    'px-4 py-2 rounded-md font-medium',
    variant === 'primary' && 'bg-blue-600 text-white',
    variant === 'secondary' && 'bg-gray-200 text-gray-900',
    isDisabled && 'opacity-50 cursor-not-allowed'
  )}
>
  Click me
</button>
```

### 8.2 Component-Specific Styles

**Extract complex class strings**:

```tsx
✅ Good
const buttonVariants = {
  primary: 'bg-blue-600 text-white hover:bg-blue-700',
  secondary: 'bg-gray-200 text-gray-900 hover:bg-gray-300',
  danger: 'bg-red-600 text-white hover:bg-red-700',
};

<button className={cn('px-4 py-2 rounded-md', buttonVariants[variant])}>
  Click me
</button>

❌ Bad - Long class strings inline
<button className="bg-blue-600 text-white hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 px-4 py-2 rounded-md font-medium">
  Click me
</button>
```

---

## 9. API Integration

### 9.1 Axios Service

**File**: `src/services/api.ts`

```tsx
import axios from 'axios';
import { authService } from './authService';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

export const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const token = authService.getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const response = await authService.refreshToken();
        authService.setAccessToken(response.accessToken);
        originalRequest.headers.Authorization = `Bearer ${response.accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        authService.clearTokens();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
```

### 9.2 Service Layer

**File**: `src/services/folderService.ts`

```tsx
import api from './api';
import type { Folder, FolderResponse, CreateFolderRequest } from '@/types/api';

export const folderService = {
  async getFolderTree(): Promise<Folder[]> {
    const response = await api.get<Folder[]>('/folders/tree');
    return response.data;
  },

  async getFolder(folderId: string): Promise<FolderResponse> {
    const response = await api.get<FolderResponse>(`/folders/${folderId}`);
    return response.data;
  },

  async createFolder(data: CreateFolderRequest): Promise<FolderResponse> {
    const response = await api.post<FolderResponse>('/folders', data);
    return response.data;
  },

  async deleteFolder(folderId: string): Promise<void> {
    await api.delete(`/folders/${folderId}`);
  },
};
```

---

## 10. Error Handling

### 10.1 Error Boundary

```tsx
import { Component, ErrorInfo, ReactNode } from 'react';

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
    console.error('Error caught:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return this.props.fallback || (
        <div className="error-boundary">
          <h2>Something went wrong</h2>
          <button onClick={() => window.location.reload()}>Reload</button>
        </div>
      );
    }

    return this.props.children;
  }
}
```

### 10.2 API Error Handling

```tsx
import { AxiosError } from 'axios';
import { toast } from 'sonner';

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
}

export function handleApiError(error: unknown): ApiError | null {
  if (error instanceof AxiosError) {
    const apiError = error.response?.data as ApiError;

    if (apiError?.message) {
      toast.error(apiError.message);
    } else {
      toast.error('An unexpected error occurred');
    }

    return apiError;
  }

  toast.error('An unexpected error occurred');
  return null;
}
```

---

## 11. Testing

### 11.1 Component Testing

```tsx
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { FolderCard } from './FolderCard';

describe('FolderCard', () => {
  const mockFolder = {
    id: '1',
    name: 'Test Folder',
    description: 'Test description',
  };

  it('should render folder name', () => {
    render(<FolderCard folder={mockFolder} />);
    expect(screen.getByText('Test Folder')).toBeInTheDocument();
  });

  it('should call onEdit when edit button clicked', async () => {
    const handleEdit = vi.fn();
    render(<FolderCard folder={mockFolder} onEdit={handleEdit} />);

    const editButton = screen.getByRole('button', { name: /edit/i });
    await userEvent.click(editButton);

    expect(handleEdit).toHaveBeenCalledWith(mockFolder);
  });
});
```

### 11.2 Hook Testing

```tsx
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useFolderTree } from './useFolder';
import { folderService } from '@/services/folderService';

vi.mock('@/services/folderService');

function createWrapper() {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });

  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
}

describe('useFolderTree', () => {
  it('should fetch folder tree successfully', async () => {
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

## 12. Performance Optimization

### 12.1 Code Splitting

```tsx
import { lazy, Suspense } from 'react';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';

// Lazy load pages
const DashboardPage = lazy(() => import('@/pages/Dashboard/DashboardPage'));
const FolderListPage = lazy(() => import('@/pages/Folder/FolderListPage'));

function App() {
  return (
    <Suspense fallback={<LoadingSpinner />}>
      <Routes>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/folders" element={<FolderListPage />} />
      </Routes>
    </Suspense>
  );
}
```

### 12.2 Memoization

```tsx
import { useMemo, useCallback } from 'react';

export function FolderList({ folders }: FolderListProps) {
  // Memoize expensive calculation
  const sortedFolders = useMemo(() => {
    return folders.sort((a, b) => a.name.localeCompare(b.name));
  }, [folders]);

  // Memoize callback to prevent child re-renders
  const handleFolderClick = useCallback(
    (folder: Folder) => {
      console.log('Clicked:', folder.name);
    },
    [] // No dependencies
  );

  return (
    <div>
      {sortedFolders.map((folder) => (
        <FolderCard key={folder.id} folder={folder} onClick={handleFolderClick} />
      ))}
    </div>
  );
}
```

---

## 13. Code Review Checklist

Before submitting PR, check:

- [ ] Code follows Airbnb style guide
- [ ] All TypeScript types are defined (no `any`)
- [ ] Components are properly typed with interfaces
- [ ] Props are destructured
- [ ] Hooks follow rules of hooks
- [ ] useEffect dependencies are correct
- [ ] No console.log statements (use proper logging)
- [ ] Error handling implemented
- [ ] Loading and error states handled
- [ ] Tests written and passing
- [ ] No ESLint warnings
- [ ] Code formatted with Prettier
- [ ] Responsive design tested (mobile, tablet, desktop)
- [ ] Accessibility considered (keyboard navigation, ARIA labels)

---

## 14. References

- [React Documentation](https://react.dev/)
- [TypeScript Documentation](https://www.typescriptlang.org/docs/)
- [Airbnb JavaScript Style Guide](https://github.com/airbnb/javascript)
- [TanStack Query Documentation](https://tanstack.com/query/latest)
- [React Router Documentation](https://reactrouter.com/)
- [Tailwind CSS Documentation](https://tailwindcss.com/docs)
