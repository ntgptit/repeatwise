# Mobile Coding Convention - RepeatWise

## 1. Overview

Document này định nghĩa coding convention cho mobile app của RepeatWise, sử dụng **React Native + TypeScript**.

**Tech Stack**:
- Core: React Native 0.73+
- Language: TypeScript 5.x
- Navigation: React Navigation v6
- State Management: TanStack Query v5, Context API, Zustand
- HTTP Client: Axios
- UI Library: React Native Paper (Material Design)
- Forms: React Hook Form
- i18n: i18n-js
- Notifications: React Native Firebase

**Note**: RepeatWise mobile app chia sẻ nhiều code conventions với web app do cùng sử dụng React + TypeScript ecosystem.

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
- **Metro Bundler**: React Native bundler

**Configuration Files**:
```json
// .eslintrc.json
{
  "extends": [
    "eslint:recommended",
    "plugin:react/recommended",
    "plugin:react-hooks/recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:react-native/all",
    "prettier"
  ],
  "rules": {
    "react/react-in-jsx-scope": "off",
    "react/prop-types": "off",
    "react-native/no-inline-styles": "warn",
    "react-native/no-color-literals": "warn",
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

## 3. Project Structure

### 3.1 Folder Structure

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
│   │   └── card/                # Card components
│   │       ├── CardList.tsx
│   │       └── CardItem.tsx
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
│   │   └── Review/
│   │       └── ReviewSessionScreen.tsx
│   │
│   ├── navigation/              # React Navigation setup
│   │   ├── AppNavigator.tsx     # Root navigator
│   │   ├── AuthNavigator.tsx    # Auth stack
│   │   └── MainNavigator.tsx    # Main tab navigator
│   │
│   ├── services/                # API service layer (same as web)
│   │   ├── api.ts
│   │   ├── authService.ts
│   │   └── folderService.ts
│   │
│   ├── hooks/                   # Custom hooks (same as web)
│   │   ├── useFolder.ts
│   │   ├── useAuth.ts
│   │   └── useDebounce.ts
│   │
│   ├── contexts/                # Context providers (same as web)
│   │   ├── AuthContext.tsx
│   │   └── ThemeContext.tsx
│   │
│   ├── store/                   # Zustand stores (optional)
│   │   └── uiStore.ts
│   │
│   ├── types/                   # TypeScript types (shared with web)
│   │   ├── api.ts
│   │   ├── entities.ts
│   │   └── navigation.ts
│   │
│   ├── constants/               # Constants
│   │   ├── routes.ts
│   │   ├── colors.ts
│   │   └── api.ts
│   │
│   ├── utils/                   # Utility functions
│   │   ├── formatDate.ts
│   │   └── validators.ts
│   │
│   ├── notifications/           # Push notification service
│   │   └── notificationService.ts
│   │
│   └── styles/                  # Global styles
│       ├── colors.ts
│       ├── spacing.ts
│       └── typography.ts
│
├── android/                     # Android native code
├── ios/                         # iOS native code
├── .eslintrc.json
├── .prettierrc
├── tsconfig.json
├── metro.config.js
├── package.json
└── README.md
```

### 3.2 File Naming Conventions

**Format**:

| Type | Convention | Example |
|------|-----------|---------|
| Screens | PascalCase + `Screen` suffix | `LoginScreen.tsx`, `FolderListScreen.tsx` |
| Components | PascalCase | `FolderCard.tsx`, `Button.tsx` |
| Hooks | camelCase with `use` prefix | `useFolder.ts`, `useAuth.ts` |
| Services | camelCase + `Service` suffix | `authService.ts`, `folderService.ts` |
| Utils | camelCase | `formatDate.ts`, `validators.ts` |
| Types | camelCase | `api.ts`, `entities.ts`, `navigation.ts` |
| Constants | camelCase | `colors.ts`, `spacing.ts` |

---

## 4. Naming Conventions

### 4.1 Component Naming

**Format**: PascalCase

```tsx
✅ Good
export function FolderCard({ folder }: FolderCardProps) { }
export const LoginScreen: React.FC = () => { }
export default function HomeScreen() { }

❌ Bad
export function folderCard() { }  // Should be PascalCase
export const login_screen = () => { }  // Should be PascalCase
```

### 4.2 Variable and Function Naming

**Format**: camelCase

```tsx
✅ Good
const userId = '123';
const folderName = 'My Folder';
const isLoading = true;

function handleFolderPress(folder: Folder) { }
const getUserFolders = async (userId: string) => { };

❌ Bad
const user_id = '123';  // Should be camelCase
const FolderName = 'My Folder';  // Should be camelCase
function HandlePress() { }  // Should be camelCase
```

### 4.3 Event Handlers (React Native Specific)

**Use `handle` prefix for handlers, `on` for props**:

```tsx
✅ Good
// In component
const handleFolderPress = (folder: Folder) => { };

// In props
interface FolderCardProps {
  onPress?: (folder: Folder) => void;
  onLongPress?: (folder: Folder) => void;
}

<FolderCard
  folder={folder}
  onPress={handleFolderPress}
/>

❌ Bad
const onFolderPress = () => { };  // Use 'handle' for handlers
const folderPress = () => { };  // Not clear it's a handler
```

---

## 4.4 Clean Code & Readability (BẮT BUỘC)

### 🔴 4.4.1 Tên biến, function, component phải rõ ràng (BẮT BUỘC)

**Không viết tắt tùy tiện, tên phải tự giải thích.**

❌ **SAI - Tên mơ hồ, viết tắt:**
```tsx
// Component/Screen name
export function FldrLst() { }  // Folder List?
export function RvwScr() { }  // Review Screen?

// Variable name
const usr = currentUser;
const fldr = selectedFolder;
const cnt = folders.length;

// Function name
function proc() { }  // Process what?
function hdl() { }  // Handle what?
```

✅ **ĐÚNG - Tên rõ ràng, có ý nghĩa:**
```tsx
// Component/Screen name
export function FolderListScreen() { }
export function ReviewSessionScreen() { }

// Variable name
const currentUser = getUserData();
const selectedFolder = folderData;
const totalFolderCount = folders.length;

// Function name
function processFolderData(data: FolderData) { }
function handleFolderPress(folder: Folder) { }
```

### 🔴 4.4.2 Component/Function không dài quá 30 dòng (BẮT BUỘC)

**Component/function phải ngắn gọn, tập trung vào một nhiệm vụ.**

❌ **SAI - Screen component quá dài:**
```tsx
export function FolderListScreen() {
  // State - 5 lines
  const [folders, setFolders] = useState<Folder[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [refreshing, setRefreshing] = useState(false);

  // Fetch data - 15 lines
  const fetchFolders = async () => {
    try {
      setIsLoading(true);
      const response = await folderService.getFolders();
      setFolders(response.data);
    } catch (error) {
      Alert.alert('Error', 'Failed to fetch folders');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchFolders();
  }, []);

  // Handlers - 10 lines
  const handleRefresh = async () => {
    setRefreshing(true);
    await fetchFolders();
    setRefreshing(false);
  };

  const handleFolderPress = (folder: Folder) => {
    navigation.navigate('FolderDetail', { folderId: folder.id });
  };

  // Render - 20 lines
  if (isLoading) return <LoadingSpinner />;

  return (
    <View style={styles.container}>
      <FlatList
        data={folders}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <FolderCard folder={item} onPress={handleFolderPress} />
        )}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={handleRefresh} />
        }
      />
    </View>
  );
}
// Total: ~60 lines - TOO LONG!
```

✅ **ĐÚNG - Tách thành hooks và components nhỏ:**
```tsx
// Custom hook - useFolderList.ts (~15 lines)
export function useFolderList() {
  const { data: folders, isLoading, error, refetch } = useQuery({
    queryKey: folderKeys.lists(),
    queryFn: () => folderService.getFolders(),
  });

  const [refreshing, setRefreshing] = useState(false);

  const handleRefresh = async () => {
    setRefreshing(true);
    await refetch();
    setRefreshing(false);
  };

  return { folders, isLoading, error, refreshing, handleRefresh };
}

// Screen component - FolderListScreen.tsx (~20 lines)
export function FolderListScreen() {
  const navigation = useNavigation();
  const { folders, isLoading, refreshing, handleRefresh } = useFolderList();

  const handleFolderPress = (folder: Folder) => {
    navigation.navigate('FolderDetail', { folderId: folder.id });
  };

  if (isLoading) return <LoadingSpinner />;

  return (
    <View style={styles.container}>
      <FolderList
        folders={folders || []}
        refreshing={refreshing}
        onRefresh={handleRefresh}
        onFolderPress={handleFolderPress}
      />
    </View>
  );
}
```

### 🔴 4.4.3 Tránh deep nesting (>2 levels) (BẮT BUỘC)

**Sử dụng Early Return.**

❌ **SAI - Deep nesting:**
```tsx
function FolderDetailScreen({ route }: Props) {
  const { data, isLoading, error } = useFolder(route.params.folderId);

  return (
    <View style={styles.container}>
      {isLoading ? (
        <LoadingSpinner />
      ) : error ? (
        <ErrorState message={error.message} />
      ) : data ? (
        data.folders.length > 0 ? (
          <FlatList data={data.folders} renderItem={...} />
        ) : (
          <EmptyState />
        )
      ) : null}
    </View>
  );
}
```

✅ **ĐÚNG - Early return:**
```tsx
function FolderDetailScreen({ route }: Props) {
  const { data, isLoading, error } = useFolder(route.params.folderId);

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return <ErrorState message={error.message} />;
  }

  if (!data || data.folders.length === 0) {
    return <EmptyState />;
  }

  return (
    <View style={styles.container}>
      <FlatList data={data.folders} renderItem={...} />
    </View>
  );
}
```

### 🔴 4.4.4 Dùng const cho biến không thay đổi (BẮT BUỘC)

```tsx
❌ **SAI:**
let userId = '123';  // Never reassigned
let maxRetries = 3;  // Constant

✅ **ĐÚNG:**
const userId = '123';
const MAX_RETRIES = 3;  // UPPER_SNAKE_CASE for constants
```

### 🔴 4.4.5 Tối đa 3 parameters trong function (BẮT BUỘC)

```tsx
❌ **SAI:**
function navigateToDetail(
  folderId: string,
  folderName: string,
  parentId: string | null,
  userId: string,
  isShared: boolean
) { }

✅ **ĐÚNG:**
interface NavigateToDetailParams {
  folderId: string;
  folderName: string;
  parentId: string | null;
  userId: string;
  isShared: boolean;
}

function navigateToDetail(params: NavigateToDetailParams) {
  const { folderId, folderName, parentId, userId, isShared } = params;
  // ...
}
```

### 🔴 4.4.6 Utility Libraries (BẮT BUỘC)

**Required dependencies:**
```json
{
  "dependencies": {
    "lodash": "^4.17.21",
    "date-fns": "^3.0.0"
  }
}
```

```tsx
import { isEmpty, uniq, debounce } from 'lodash';
import { format, formatDistance } from 'date-fns';

// String/Array operations
if (isEmpty(folders)) { }
const uniqueIds = uniq(folderIds);

// Date operations
const formattedDate = format(new Date(), 'PPP');
const relativeTime = formatDistance(date, new Date(), { addSuffix: true });

// Debounce search
const debouncedSearch = debounce((query: string) => {
  performSearch(query);
}, 300);
```

### 🔴 4.4.7 Quản lý text bằng i18n (BẮT BUỘC)

**Setup i18n-js:**
```tsx
// src/i18n/config.ts
import { I18n } from 'i18n-js';
import en from './locales/en.json';
import vi from './locales/vi.json';

const i18n = new I18n({
  en,
  vi,
});

i18n.enableFallback = true;
i18n.defaultLocale = 'en';

export default i18n;
```

**Translation files:**
```json
// src/i18n/locales/en.json
{
  "common": {
    "loading": "Loading...",
    "save": "Save",
    "cancel": "Cancel",
    "delete": "Delete"
  },
  "folder": {
    "title": "Folders",
    "createTitle": "Create Folder",
    "nameRequired": "Folder name is required",
    "deleteConfirm": "Are you sure you want to delete this folder?"
  }
}

// src/i18n/locales/vi.json
{
  "common": {
    "loading": "Đang tải...",
    "save": "Lưu",
    "cancel": "Hủy",
    "delete": "Xóa"
  },
  "folder": {
    "title": "Thư mục",
    "createTitle": "Tạo thư mục",
    "nameRequired": "Tên thư mục không được để trống",
    "deleteConfirm": "Bạn có chắc chắn muốn xóa thư mục này?"
  }
}
```

❌ **SAI - Hardcode text:**
```tsx
export function FolderCard({ folder }: FolderCardProps) {
  const handleDelete = () => {
    Alert.alert(
      'Xóa thư mục',
      'Bạn có chắc chắn muốn xóá thư mục này?',
      [
        { text: 'Hủy', style: 'cancel' },
        { text: 'Xóa', onPress: () => deleteFolder(folder.id) },
      ]
    );
  };

  return (
    <View>
      <Text>{folder.name}</Text>
      <Button title="Xóa" onPress={handleDelete} />
    </View>
  );
}
```

✅ **ĐÚNG - Dùng i18n:**
```tsx
import i18n from '@/i18n/config';

export function FolderCard({ folder }: FolderCardProps) {
  const handleDelete = () => {
    Alert.alert(
      i18n.t('common.delete'),
      i18n.t('folder.deleteConfirm'),
      [
        { text: i18n.t('common.cancel'), style: 'cancel' },
        { text: i18n.t('common.delete'), onPress: () => deleteFolder(folder.id) },
      ]
    );
  };

  return (
    <View>
      <Text>{folder.name}</Text>
      <Button title={i18n.t('common.delete')} onPress={handleDelete} />
    </View>
  );
}
```

---

## 5. React Native Component Best Practices

### 5.1 Component Structure

```tsx
import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import { Card } from 'react-native-paper';
import type { Folder } from '@/types/entities';

interface FolderCardProps {
  folder: Folder;
  onPress?: (folder: Folder) => void;
  onLongPress?: (folder: Folder) => void;
  testID?: string;
}

export function FolderCard({
  folder,
  onPress,
  onLongPress,
  testID = 'folder-card',
}: FolderCardProps) {
  const handlePress = () => {
    onPress?.(folder);
  };

  const handleLongPress = () => {
    onLongPress?.(folder);
  };

  return (
    <Card
      style={styles.card}
      onPress={handlePress}
      onLongPress={handleLongPress}
      testID={testID}
    >
      <Card.Content>
        <Text style={styles.title}>{folder.name}</Text>
        {folder.description && (
          <Text style={styles.description}>{folder.description}</Text>
        )}
      </Card.Content>
    </Card>
  );
}

const styles = StyleSheet.create({
  card: {
    marginVertical: 8,
    marginHorizontal: 16,
  },
  title: {
    fontSize: 18,
    fontWeight: '600',
  },
  description: {
    fontSize: 14,
    color: '#666',
    marginTop: 4,
  },
});
```

### 5.2 StyleSheet vs Inline Styles

**Always use StyleSheet.create, avoid inline styles**:

```tsx
✅ Good
const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
  },
});

<View style={styles.container}>
  <Text style={styles.title}>Title</Text>
</View>

// Conditional styles - use array
<View style={[styles.button, isPressed && styles.buttonPressed]} />

❌ Bad
<View style={{ flex: 1, padding: 16, backgroundColor: '#fff' }}>
  <Text style={{ fontSize: 24, fontWeight: 'bold' }}>Title</Text>
</View>
```

**Why StyleSheet.create?**
- Performance: Styles are optimized and sent to native only once
- Validation: Catches typos and invalid style properties
- Code organization: Centralized styles
- IntelliSense: Better autocomplete

### 5.3 Platform-Specific Code

**Use Platform module for platform-specific logic**:

```tsx
import { Platform, StyleSheet } from 'react-native';

✅ Good
const styles = StyleSheet.create({
  container: {
    padding: 16,
    ...Platform.select({
      ios: {
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 4,
      },
      android: {
        elevation: 4,
      },
    }),
  },
  text: {
    fontSize: Platform.OS === 'ios' ? 17 : 16,
  },
});

// Platform-specific components
import { Button } from './Button.ios';  // or Button.android
```

### 5.4 FlatList Best Practices

**Use FlatList for lists, not ScrollView with map**:

```tsx
✅ Good
import { FlatList } from 'react-native';

<FlatList
  data={folders}
  keyExtractor={(item) => item.id}
  renderItem={({ item }) => (
    <FolderCard
      folder={item}
      onPress={handleFolderPress}
    />
  )}
  ItemSeparatorComponent={() => <View style={styles.separator} />}
  ListEmptyComponent={<EmptyState message="No folders" />}
  onEndReached={handleLoadMore}
  onEndReachedThreshold={0.5}
  refreshing={isRefreshing}
  onRefresh={handleRefresh}
/>

❌ Bad - ScrollView with map (bad performance for large lists)
<ScrollView>
  {folders.map((folder) => (
    <FolderCard key={folder.id} folder={folder} />
  ))}
</ScrollView>
```

### 5.5 Safe Area Handling

**Use SafeAreaView for screens**:

```tsx
import { SafeAreaView } from 'react-native-safe-area-context';

✅ Good
export function HomeScreen() {
  return (
    <SafeAreaView style={styles.container} edges={['top', 'bottom']}>
      {/* Screen content */}
    </SafeAreaView>
  );
}

// Or use SafeAreaProvider at root
import { SafeAreaProvider } from 'react-native-safe-area-context';

export default function App() {
  return (
    <SafeAreaProvider>
      {/* App content */}
    </SafeAreaProvider>
  );
}
```

---

## 6. React Navigation

### 6.1 Type-Safe Navigation

**Define navigation types**:

```tsx
// types/navigation.ts
import type { NavigatorScreenParams } from '@react-navigation/native';

// Root Stack
export type RootStackParamList = {
  Auth: NavigatorScreenParams<AuthStackParamList>;
  Main: NavigatorScreenParams<MainTabParamList>;
};

// Auth Stack
export type AuthStackParamList = {
  Login: undefined;
  Register: undefined;
};

// Main Tab
export type MainTabParamList = {
  Home: undefined;
  Folders: undefined;
  Review: undefined;
  Settings: undefined;
};

// Folder Stack
export type FolderStackParamList = {
  FolderList: undefined;
  FolderDetail: { folderId: string };
};

// Navigation Props
import type { StackScreenProps } from '@react-navigation/stack';

export type FolderDetailScreenProps = StackScreenProps<
  FolderStackParamList,
  'FolderDetail'
>;
```

### 6.2 Navigation Setup

**File**: `src/navigation/AppNavigator.tsx`

```tsx
import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { useAuth } from '@/contexts/AuthContext';
import AuthNavigator from './AuthNavigator';
import MainNavigator from './MainNavigator';
import type { RootStackParamList } from '@/types/navigation';

const Stack = createNativeStackNavigator<RootStackParamList>();

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

**File**: `src/navigation/MainNavigator.tsx`

```tsx
import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import HomeScreen from '@/screens/Home/HomeScreen';
import FolderListScreen from '@/screens/Folder/FolderListScreen';
import ReviewSessionScreen from '@/screens/Review/ReviewSessionScreen';
import SettingsScreen from '@/screens/Settings/SettingsScreen';
import type { MainTabParamList } from '@/types/navigation';

const Tab = createBottomTabNavigator<MainTabParamList>();

export default function MainNavigator() {
  return (
    <Tab.Navigator
      screenOptions={{
        tabBarActiveTintColor: '#2196F3',
        tabBarInactiveTintColor: '#666',
      }}
    >
      <Tab.Screen
        name="Home"
        component={HomeScreen}
        options={{
          tabBarLabel: 'Home',
          tabBarIcon: ({ color, size }) => (
            <MaterialCommunityIcons name="home" color={color} size={size} />
          ),
        }}
      />
      <Tab.Screen
        name="Folders"
        component={FolderListScreen}
        options={{
          tabBarLabel: 'Folders',
          tabBarIcon: ({ color, size }) => (
            <MaterialCommunityIcons name="folder" color={color} size={size} />
          ),
        }}
      />
      <Tab.Screen
        name="Review"
        component={ReviewSessionScreen}
        options={{
          tabBarLabel: 'Review',
          tabBarIcon: ({ color, size }) => (
            <MaterialCommunityIcons name="cards" color={color} size={size} />
          ),
        }}
      />
      <Tab.Screen
        name="Settings"
        component={SettingsScreen}
        options={{
          tabBarLabel: 'Settings',
          tabBarIcon: ({ color, size }) => (
            <MaterialCommunityIcons name="cog" color={color} size={size} />
          ),
        }}
      />
    </Tab.Navigator>
  );
}
```

### 6.3 Navigation Usage in Screens

```tsx
import { useNavigation } from '@react-navigation/native';
import type { StackNavigationProp } from '@react-navigation/stack';
import type { FolderStackParamList } from '@/types/navigation';

type NavigationProp = StackNavigationProp<FolderStackParamList, 'FolderList'>;

export function FolderListScreen() {
  const navigation = useNavigation<NavigationProp>();

  const handleFolderPress = (folderId: string) => {
    navigation.navigate('FolderDetail', { folderId });
  };

  return (
    <View>
      {/* Screen content */}
    </View>
  );
}

// Using route params
export function FolderDetailScreen({ route }: FolderDetailScreenProps) {
  const { folderId } = route.params;

  const { data: folder } = useFolder(folderId);

  return (
    <View>
      <Text>{folder?.name}</Text>
    </View>
  );
}
```

---

## 7. State Management

### 7.1 TanStack Query (Server State)

**Same as web - see web coding convention**

```tsx
// hooks/useFolder.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { folderService } from '@/services/folderService';
import type { Folder } from '@/types/entities';

export const folderKeys = {
  all: ['folders'] as const,
  lists: () => [...folderKeys.all, 'list'] as const,
  detail: (id: string) => [...folderKeys.all, 'detail', id] as const,
};

export function useFolderList() {
  return useQuery({
    queryKey: folderKeys.lists(),
    queryFn: () => folderService.getFolders(),
  });
}

export function useCreateFolder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateFolderRequest) => folderService.createFolder(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: folderKeys.lists() });
    },
  });
}
```

### 7.2 Context API (Auth State)

**Same as web - see web coding convention**

### 7.3 Zustand (UI State)

**File**: `src/store/uiStore.ts`

```tsx
import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import AsyncStorage from '@react-native-async-storage/async-storage';

interface UIState {
  theme: 'light' | 'dark' | 'system';
  setTheme: (theme: 'light' | 'dark' | 'system') => void;
  language: 'en' | 'vi';
  setLanguage: (language: 'en' | 'vi') => void;
}

export const useUIStore = create<UIState>()(
  persist(
    (set) => ({
      theme: 'system',
      setTheme: (theme) => set({ theme }),
      language: 'vi',
      setLanguage: (language) => set({ language }),
    }),
    {
      name: 'repeatwise-ui',
      storage: createJSONStorage(() => AsyncStorage),
    }
  )
);
```

---

## 8. React Native Paper (UI Components)

### 8.1 Theme Setup

**File**: `src/App.tsx`

```tsx
import React from 'react';
import { Provider as PaperProvider, DefaultTheme, DarkTheme } from 'react-native-paper';
import { useColorScheme } from 'react-native';
import AppNavigator from './navigation/AppNavigator';

const lightTheme = {
  ...DefaultTheme,
  colors: {
    ...DefaultTheme.colors,
    primary: '#2196F3',
    accent: '#FFC107',
  },
};

const darkTheme = {
  ...DarkTheme,
  colors: {
    ...DarkTheme.colors,
    primary: '#64B5F6',
    accent: '#FFD54F',
  },
};

export default function App() {
  const colorScheme = useColorScheme();
  const theme = colorScheme === 'dark' ? darkTheme : lightTheme;

  return (
    <PaperProvider theme={theme}>
      <AppNavigator />
    </PaperProvider>
  );
}
```

### 8.2 Using Paper Components

```tsx
import { Button, Card, TextInput, FAB } from 'react-native-paper';

export function CreateFolderScreen() {
  const [folderName, setFolderName] = useState('');

  return (
    <View style={styles.container}>
      <TextInput
        label="Folder Name"
        value={folderName}
        onChangeText={setFolderName}
        mode="outlined"
        style={styles.input}
      />

      <Button
        mode="contained"
        onPress={handleCreate}
        loading={isLoading}
        disabled={!folderName}
      >
        Create Folder
      </Button>

      <FAB
        icon="plus"
        style={styles.fab}
        onPress={handleCreate}
      />
    </View>
  );
}
```

---

## 9. Async Storage

### 9.1 Storage Service

**File**: `src/services/storageService.ts`

```tsx
import AsyncStorage from '@react-native-async-storage/async-storage';

export const storageService = {
  async setItem(key: string, value: string): Promise<void> {
    try {
      await AsyncStorage.setItem(key, value);
    } catch (error) {
      console.error('Error saving to storage:', error);
      throw error;
    }
  },

  async getItem(key: string): Promise<string | null> {
    try {
      return await AsyncStorage.getItem(key);
    } catch (error) {
      console.error('Error reading from storage:', error);
      throw error;
    }
  },

  async removeItem(key: string): Promise<void> {
    try {
      await AsyncStorage.removeItem(key);
    } catch (error) {
      console.error('Error removing from storage:', error);
      throw error;
    }
  },

  async setObject<T>(key: string, value: T): Promise<void> {
    try {
      const jsonValue = JSON.stringify(value);
      await AsyncStorage.setItem(key, jsonValue);
    } catch (error) {
      console.error('Error saving object to storage:', error);
      throw error;
    }
  },

  async getObject<T>(key: string): Promise<T | null> {
    try {
      const jsonValue = await AsyncStorage.getItem(key);
      return jsonValue != null ? JSON.parse(jsonValue) : null;
    } catch (error) {
      console.error('Error reading object from storage:', error);
      throw error;
    }
  },

  async clear(): Promise<void> {
    try {
      await AsyncStorage.clear();
    } catch (error) {
      console.error('Error clearing storage:', error);
      throw error;
    }
  },
};
```

---

## 10. Push Notifications

### 10.1 Notification Service

**File**: `src/notifications/notificationService.ts`

```tsx
import messaging from '@react-native-firebase/messaging';
import { Platform } from 'react-native';

export const notificationService = {
  async requestPermission(): Promise<boolean> {
    const authStatus = await messaging().requestPermission();
    const enabled =
      authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
      authStatus === messaging.AuthorizationStatus.PROVISIONAL;

    return enabled;
  },

  async getFCMToken(): Promise<string | null> {
    try {
      const token = await messaging().getToken();
      return token;
    } catch (error) {
      console.error('Error getting FCM token:', error);
      return null;
    }
  },

  async checkPermission(): Promise<boolean> {
    const authStatus = await messaging().hasPermission();
    return authStatus === messaging.AuthorizationStatus.AUTHORIZED;
  },

  onNotificationReceived(handler: (message: any) => void) {
    return messaging().onMessage(handler);
  },

  onNotificationOpened(handler: (message: any) => void) {
    return messaging().onNotificationOpenedApp(handler);
  },

  getInitialNotification() {
    return messaging().getInitialNotification();
  },
};
```

---

## 11. Internationalization (i18n)

### 11.1 i18n Setup

**File**: `src/lib/i18n.ts`

```tsx
import { I18n } from 'i18n-js';
import { getLocales } from 'react-native-localize';
import en from '../locales/en.json';
import vi from '../locales/vi.json';

const i18n = new I18n({
  en,
  vi,
});

i18n.locale = getLocales()[0].languageCode;
i18n.enableFallback = true;
i18n.defaultLocale = 'en';

export default i18n;
```

**Translation Files**:

```json
// src/locales/en.json
{
  "common": {
    "cancel": "Cancel",
    "save": "Save",
    "delete": "Delete",
    "edit": "Edit"
  },
  "folders": {
    "title": "Folders",
    "create": "Create Folder",
    "empty": "No folders yet"
  }
}

// src/locales/vi.json
{
  "common": {
    "cancel": "Hủy",
    "save": "Lưu",
    "delete": "Xóa",
    "edit": "Sửa"
  },
  "folders": {
    "title": "Thư mục",
    "create": "Tạo thư mục",
    "empty": "Chưa có thư mục"
  }
}
```

### 11.2 Using Translations

```tsx
import i18n from '@/lib/i18n';

export function FolderListScreen() {
  return (
    <View>
      <Text>{i18n.t('folders.title')}</Text>
      <Button>{i18n.t('folders.create')}</Button>
    </View>
  );
}

// With parameters
i18n.t('common.itemCount', { count: 5 });
// Translation: "5 items"
```

---

## 12. Testing

### 12.1 Component Testing

```tsx
import React from 'react';
import { render, fireEvent } from '@testing-library/react-native';
import { FolderCard } from './FolderCard';

describe('FolderCard', () => {
  const mockFolder = {
    id: '1',
    name: 'Test Folder',
    description: 'Test description',
  };

  it('should render folder name', () => {
    const { getByText } = render(<FolderCard folder={mockFolder} />);
    expect(getByText('Test Folder')).toBeTruthy();
  });

  it('should call onPress when pressed', () => {
    const handlePress = jest.fn();
    const { getByTestId } = render(
      <FolderCard folder={mockFolder} onPress={handlePress} />
    );

    fireEvent.press(getByTestId('folder-card'));
    expect(handlePress).toHaveBeenCalledWith(mockFolder);
  });
});
```

### 12.2 Hook Testing

**Same as web - see web coding convention**

---

## 13. Performance Optimization

### 13.1 Memoization

```tsx
import { useMemo, useCallback, memo } from 'react';

// Memoize component
export const FolderCard = memo(({ folder, onPress }: FolderCardProps) => {
  return <Card />;
});

// Memoize callback
const handlePress = useCallback(
  (folder: Folder) => {
    console.log('Pressed:', folder.name);
  },
  []
);

// Memoize expensive calculation
const sortedFolders = useMemo(() => {
  return folders.sort((a, b) => a.name.localeCompare(b.name));
}, [folders]);
```

### 13.2 Image Optimization

```tsx
import FastImage from 'react-native-fast-image';

✅ Good - Use FastImage for remote images
<FastImage
  style={styles.image}
  source={{
    uri: imageUrl,
    priority: FastImage.priority.normal,
  }}
  resizeMode={FastImage.resizeMode.cover}
/>

// Local images
<Image source={require('@/assets/logo.png')} style={styles.logo} />
```

### 13.3 FlatList Optimization

```tsx
<FlatList
  data={folders}
  keyExtractor={(item) => item.id}
  renderItem={({ item }) => <FolderCard folder={item} />}

  // Performance optimizations
  removeClippedSubviews={true}
  maxToRenderPerBatch={10}
  updateCellsBatchingPeriod={50}
  initialNumToRender={10}
  windowSize={10}

  // Use getItemLayout for fixed height items
  getItemLayout={(data, index) => ({
    length: ITEM_HEIGHT,
    offset: ITEM_HEIGHT * index,
    index,
  })}
/>
```

---

## 14. React Native Specific Best Practices

### 14.1 Avoid Console Logs in Production

```tsx
// config/logger.ts
export const logger = {
  log: (__DEV__ ? console.log : () => {}),
  warn: (__DEV__ ? console.warn : () => {}),
  error: (__DEV__ ? console.error : () => {}),
};

// Usage
import { logger } from '@/config/logger';
logger.log('Debug message');
```

### 14.2 Keyboard Handling

```tsx
import { KeyboardAvoidingView, Platform, TouchableWithoutFeedback, Keyboard } from 'react-native';

export function LoginScreen() {
  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      style={styles.container}
    >
      <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
        <View>
          <TextInput placeholder="Email" />
          <TextInput placeholder="Password" secureTextEntry />
        </View>
      </TouchableWithoutFeedback>
    </KeyboardAvoidingView>
  );
}
```

### 14.3 Dimensions and Responsive Design

```tsx
import { Dimensions, useWindowDimensions } from 'react-native';

// Hook (preferred - updates on orientation change)
export function MyComponent() {
  const { width, height } = useWindowDimensions();

  return (
    <View style={{ width: width * 0.9 }}>
      {/* Content */}
    </View>
  );
}

// Static dimensions (only at mount)
const { width, height } = Dimensions.get('window');
```

### 14.4 Touch Feedback

```tsx
import { TouchableOpacity, TouchableHighlight, Pressable } from 'react-native';

✅ Good - Use Pressable (modern API)
<Pressable
  onPress={handlePress}
  style={({ pressed }) => [
    styles.button,
    pressed && styles.buttonPressed,
  ]}
>
  {({ pressed }) => (
    <Text style={[styles.text, pressed && styles.textPressed]}>
      Press me
    </Text>
  )}
</Pressable>

// Or TouchableOpacity for simple cases
<TouchableOpacity onPress={handlePress} activeOpacity={0.7}>
  <View style={styles.button}>
    <Text>Press me</Text>
  </View>
</TouchableOpacity>
```

---

## 15. Code Review Checklist

Before submitting PR, check:

- [ ] Code follows Airbnb style guide
- [ ] All TypeScript types are defined (no `any`)
- [ ] Components use StyleSheet.create (no inline styles)
- [ ] FlatList used for lists (not ScrollView with map)
- [ ] Platform-specific code properly handled
- [ ] SafeAreaView used for screens
- [ ] Navigation is type-safe
- [ ] Images optimized (FastImage for remote)
- [ ] No console.log in production code
- [ ] Keyboard handling implemented for forms
- [ ] Touch feedback on interactive elements
- [ ] Error handling implemented
- [ ] Tests written and passing
- [ ] No ESLint warnings
- [ ] Code formatted with Prettier
- [ ] Tested on both iOS and Android
- [ ] Performance optimizations applied (memo, useMemo, useCallback)

---

## 16. Common Pitfalls to Avoid

### 16.1 Don't Use Index as Key

```tsx
❌ Bad
{items.map((item, index) => (
  <Item key={index} data={item} />
))}

✅ Good
{items.map((item) => (
  <Item key={item.id} data={item} />
))}
```

### 16.2 Don't Mutate State

```tsx
❌ Bad
const handleAdd = () => {
  folders.push(newFolder);  // Mutating array
  setFolders(folders);
};

✅ Good
const handleAdd = () => {
  setFolders([...folders, newFolder]);  // New array
};
```

### 16.3 Don't Forget to Unsubscribe

```tsx
✅ Good
useEffect(() => {
  const subscription = messaging().onMessage(handleMessage);

  return () => {
    subscription();  // Cleanup
  };
}, []);
```

### 16.4 Don't Use Navigation Outside Component

```tsx
❌ Bad
// In service file
import { navigation } from './navigation';
navigation.navigate('Home');

✅ Good
// Pass navigation as parameter or use hook inside component
export function MyScreen() {
  const navigation = useNavigation();

  const handleLogin = async () => {
    await login();
    navigation.navigate('Home');
  };
}
```

---

## 17. References

- [React Native Documentation](https://reactnative.dev/docs/getting-started)
- [React Navigation Documentation](https://reactnavigation.org/)
- [React Native Paper Documentation](https://callstack.github.io/react-native-paper/)
- [TanStack Query Documentation](https://tanstack.com/query/latest)
- [Airbnb JavaScript Style Guide](https://github.com/airbnb/javascript)
- [TypeScript Documentation](https://www.typescriptlang.org/docs/)
