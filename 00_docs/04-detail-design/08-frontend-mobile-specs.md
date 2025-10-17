# Frontend Mobile Specifications - RepeatWise MVP

## 1. Overview

### 1.1 Platform & Technology Stack
- **Platform**: React Native 0.73+ (iOS + Android)
- **Language**: TypeScript 5.x
- **State Management**:
  - Server State: TanStack Query v5 (shared with web)
  - Auth State: Context API
  - UI State: Zustand (optional)
- **Navigation**: React Navigation v6
- **UI Library**: React Native Paper (Material Design 3)
- **HTTP Client**: Axios (shared with web)
- **Forms**: React Hook Form + Zod validation
- **Internationalization**: i18n-js (Vietnamese/English)
- **Notifications**: React Native Firebase (Push Notifications)

### 1.2 Design Principles
- **Component Reusability**: Maximize code sharing with web app (services, hooks, types)
- **Native Feel**: Platform-specific UX patterns (iOS vs Android)
- **Performance**: Optimized FlatLists, lazy loading, minimal re-renders
- **Accessibility**: VoiceOver/TalkBack support, proper labeling
- **Offline-Ready**: Architecture prepared for future offline mode (not MVP)

---

## 2. Application Architecture

### 2.1 Folder Structure
```
frontend-mobile/
├── src/
│   ├── components/              # Reusable UI components
│   │   ├── common/             # Button, Input, Card, LoadingSpinner
│   │   ├── folder/             # FolderTreeView, FolderItem, FolderActionSheet
│   │   ├── deck/               # DeckList, DeckCard, DeckStatsCard
│   │   ├── card/               # CardItem, CardEditor, CardPreview
│   │   └── review/             # FlashcardView, RatingButtons, ReviewProgressBar
│   │
│   ├── screens/                # Screen components (navigation destinations)
│   │   ├── Auth/
│   │   │   ├── LoginScreen.tsx
│   │   │   └── RegisterScreen.tsx
│   │   ├── Folder/
│   │   │   ├── FoldersScreen.tsx         # Main folders view (tab)
│   │   │   ├── FolderDetailScreen.tsx     # Folder contents
│   │   │   ├── CreateFolderScreen.tsx     # Modal screen
│   │   │   └── FolderStatsScreen.tsx      # Folder statistics
│   │   ├── Deck/
│   │   │   ├── DeckDetailScreen.tsx       # Deck contents + cards
│   │   │   ├── CreateDeckScreen.tsx       # Modal screen
│   │   │   └── ImportCardsScreen.tsx      # Import CSV/Excel
│   │   ├── Review/
│   │   │   ├── ReviewScreen.tsx           # Due cards overview (tab)
│   │   │   ├── ReviewSessionScreen.tsx    # Fullscreen flashcards
│   │   │   └── CramSessionScreen.tsx      # Cram mode
│   │   ├── Stats/
│   │   │   ├── StatisticsScreen.tsx       # Stats overview (tab)
│   │   │   └── BoxDistributionScreen.tsx  # Box chart details
│   │   └── Settings/
│   │       └── SettingsScreen.tsx         # SRS settings + profile (tab)
│   │
│   ├── navigation/             # React Navigation setup
│   │   ├── AppNavigator.tsx    # Root navigator (auth check)
│   │   ├── AuthStack.tsx       # Auth screens (login, register)
│   │   ├── MainTabs.tsx        # Bottom tab navigator
│   │   └── types.ts            # Navigation param types
│   │
│   ├── services/               # API calls (SHARED with web)
│   │   ├── api.ts              # Axios instance + interceptors
│   │   ├── authService.ts
│   │   ├── folderService.ts
│   │   ├── deckService.ts
│   │   ├── cardService.ts
│   │   ├── reviewService.ts
│   │   └── statsService.ts
│   │
│   ├── hooks/                  # Custom React hooks (SHARED with web)
│   │   ├── useAuth.ts          # Auth context hook
│   │   ├── useFolder.ts        # Folder queries & mutations (React Query)
│   │   ├── useDeck.ts          # Deck queries & mutations
│   │   ├── useCard.ts          # Card queries & mutations
│   │   ├── useReview.ts        # Review queries & mutations
│   │   └── useStats.ts         # Statistics queries
│   │
│   ├── contexts/               # React Context providers
│   │   ├── AuthContext.tsx     # Auth state (user, login, logout)
│   │   └── SettingsContext.tsx # User settings
│   │
│   ├── store/                  # Zustand stores (optional UI state)
│   │   └── uiStore.ts          # Sidebar open/closed, theme, temp UI state
│   │
│   ├── types/                  # TypeScript types (SHARED with web)
│   │   ├── api.ts              # API request/response types
│   │   ├── entities.ts         # Domain entities (User, Folder, Deck, Card)
│   │   └── common.ts           # Common types
│   │
│   ├── utils/                  # Utility functions
│   │   ├── formatters.ts       # Date, number formatters
│   │   ├── validators.ts       # Form validators
│   │   └── storage.ts          # AsyncStorage wrappers
│   │
│   ├── notifications/          # Push notification service
│   │   └── notificationService.ts
│   │
│   ├── constants/              # App constants
│   │   ├── routes.ts           # Route names
│   │   ├── api.ts              # API endpoints
│   │   └── config.ts           # App configuration
│   │
│   └── i18n/                   # Internationalization
│       ├── index.ts            # i18n setup
│       ├── en.json             # English translations
│       └── vi.json             # Vietnamese translations
│
├── android/                    # Android native code
├── ios/                        # iOS native code
├── tsconfig.json
└── package.json
```

### 2.2 Navigation Structure

```
AppNavigator (Root)
├── isAuthenticated = false → AuthStack
│   ├── LoginScreen
│   └── RegisterScreen
│
└── isAuthenticated = true → MainStack
    ├── MainTabs (Bottom Tabs)
    │   ├── FoldersTab → FoldersScreen
    │   ├── ReviewTab → ReviewScreen
    │   ├── StatsTab → StatisticsScreen
    │   └── SettingsTab → SettingsScreen
    │
    └── Modal Screens (Stack)
        ├── FolderDetailScreen (push)
        ├── DeckDetailScreen (push)
        ├── CreateFolderScreen (modal)
        ├── CreateDeckScreen (modal)
        ├── ImportCardsScreen (modal)
        ├── ReviewSessionScreen (fullscreen modal)
        ├── CramSessionScreen (fullscreen modal)
        ├── FolderStatsScreen (push)
        └── BoxDistributionScreen (push)
```

**Navigation Types**:
- **Stack Navigation**: For drill-down navigation (Folders → Folder Detail → Deck Detail)
- **Bottom Tabs**: For main app sections (Folders, Review, Stats, Settings)
- **Modal Screens**: For create/edit forms, fullscreen review
- **Nested Navigators**: Tabs inside main stack

### 2.3 State Management (Same as Web)

| State Type | Technology | Use Cases |
|------------|-----------|-----------|
| **Server State** | TanStack Query | Folders, decks, cards, reviews, stats (auto caching, refetch) |
| **Auth State** | Context API | User, login status, token management |
| **UI State** | Zustand (optional) | Theme, modal visibility, temp UI state |
| **Form State** | React Hook Form | Form inputs, validation |

**Benefits**:
- **Shared logic with web**: Same hooks, services, types
- **Automatic caching**: React Query handles cache, stale data
- **Less boilerplate**: No Redux overhead (~300 lines saved)
- **Better DX**: Simpler code, easier to maintain

---

## 3. Screen Specifications

### 3.1 Auth Screens

#### Screen: LoginScreen

**Purpose**: User authentication with email/password

**Navigation**:
- Route: `AuthStack → Login`
- Header: Hidden (custom header in screen)
- Actions: Navigate to RegisterScreen, forgot password (future)

**Layout**:
```tsx
<SafeAreaView style={styles.container}>
  <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'}>
    {/* Logo/Brand */}
    <Image source={require('@/assets/logo.png')} style={styles.logo} />
    <Text variant="headlineMedium">Welcome back</Text>

    {/* Login Form */}
    <Controller
      control={control}
      name="email"
      render={({ field }) => (
        <TextInput
          label="Email"
          value={field.value}
          onChangeText={field.onChange}
          autoCapitalize="none"
          keyboardType="email-address"
          error={!!errors.email}
        />
      )}
    />
    <Controller
      control={control}
      name="password"
      render={({ field }) => (
        <TextInput
          label="Password"
          value={field.value}
          onChangeText={field.onChange}
          secureTextEntry
          error={!!errors.password}
        />
      )}
    />

    {/* Actions */}
    <Button mode="contained" onPress={handleSubmit(onSubmit)} loading={isLoading}>
      Login
    </Button>
    <Button mode="text" onPress={() => navigation.navigate('Register')}>
      Don't have an account? Sign up
    </Button>
  </KeyboardAvoidingView>
</SafeAreaView>
```

**Data Fetching**:
- Hook: `useAuth()` from AuthContext
- Action: `login(email, password)` mutation
- Loading state: Button shows loading spinner
- Error handling: Show toast on error (invalid credentials, network error)

**Validation**:
- Email: Required, valid email format
- Password: Required, min 8 characters
- Zod schema validation

**Platform-Specific**:
- iOS: KeyboardAvoidingView with padding
- Android: KeyboardAvoidingView with height
- Auto-focus email input on mount
- Auto-submit on Enter key (iOS keyboard)

---

#### Screen: RegisterScreen

**Purpose**: User registration with email/password/name

**Navigation**:
- Route: `AuthStack → Register`
- Header: Back button (navigate to Login)

**Layout**:
```tsx
<SafeAreaView>
  <ScrollView keyboardShouldPersistTaps="handled">
    {/* Registration Form */}
    <TextInput label="Name" {...} />
    <TextInput label="Email" {...} />
    <TextInput label="Password" secureTextEntry {...} />
    <TextInput label="Confirm Password" secureTextEntry {...} />

    {/* Submit */}
    <Button mode="contained" onPress={handleSubmit} loading={isLoading}>
      Create Account
    </Button>
    <Button mode="text" onPress={() => navigation.goBack()}>
      Already have an account? Login
    </Button>
  </ScrollView>
</SafeAreaView>
```

**Data Fetching**:
- Hook: `useAuth()` from AuthContext
- Action: `register(email, password, name)` mutation
- On success: Navigate to LoginScreen with success message

**Validation**:
- Name: Required, max 100 chars
- Email: Required, valid format, unique (checked by backend)
- Password: Required, min 8 chars, contains uppercase/lowercase/number
- Confirm password: Must match password

---

### 3.2 Main Tab Screens

#### Screen: FoldersScreen (Home Tab)

**Purpose**: Display user's folder tree + decks (main home screen)

**Navigation**:
- Route: `MainTabs → Folders`
- Header: Custom header with title "My Folders" + Add button (FAB)
- Tab bar: Active tab indicator (Folders icon highlighted)

**Layout**:
```tsx
<SafeAreaView>
  {/* Header */}
  <Appbar.Header>
    <Appbar.Content title="My Folders" />
    <Appbar.Action icon="plus" onPress={() => setCreateModalVisible(true)} />
  </Appbar.Header>

  {/* Folder Tree */}
  <FolderTreeView
    folders={folders}
    onFolderPress={handleFolderPress}
    onFolderLongPress={handleFolderLongPress}
  />

  {/* Selected Folder's Decks */}
  {selectedFolder && (
    <View style={styles.deckSection}>
      <Text variant="titleMedium">Decks in {selectedFolder.name}</Text>
      <DeckList deckIds={selectedFolder.deckIds} onDeckPress={handleDeckPress} />
    </View>
  )}

  {/* FAB for quick create */}
  <FAB
    icon="plus"
    style={styles.fab}
    onPress={() => setCreateModalVisible(true)}
  />
</SafeAreaView>
```

**Data Fetching**:
- Hook: `useFolderTree()` from useFolder hook
- Endpoint: `GET /api/folders`
- Pull-to-refresh: Enabled (RefreshControl)
- Loading state: ActivityIndicator centered
- Empty state: "No folders yet. Create one to get started!" with illustration

**Actions**:
- **Tap folder**: Expand/collapse children (toggle expanded state)
- **Long press folder**: Show action sheet (Rename, Move, Copy, Delete)
- **Tap deck**: Navigate to DeckDetailScreen
- **Pull down**: Refresh folder tree
- **FAB (bottom right)**: Open CreateFolderScreen modal

**Platform-Specific**:
- **iOS**:
  - Swipe left on folder → Delete action (red background)
  - Native action sheet (iOS ActionSheet)
  - Haptic feedback on long press (Light impact)
- **Android**:
  - Long press → Context menu (bottom sheet)
  - Ripple effect on tap
  - Material bottom sheet animation

**Performance**:
- Lazy load folder children (expand on demand)
- FlatList for decks (virtualized)
- Memoize FolderItem components (React.memo)

---

#### Screen: ReviewScreen (Review Tab)

**Purpose**: Overview of due cards, start review session

**Navigation**:
- Route: `MainTabs → Review`
- Header: "Review" + Settings button (navigate to SRS settings)
- Tab bar: Active tab indicator

**Layout**:
```tsx
<SafeAreaView>
  <ScrollView refreshControl={<RefreshControl refreshing={isRefreshing} />}>
    {/* Due Cards Summary */}
    <Card style={styles.summaryCard}>
      <Card.Content>
        <Text variant="displaySmall">{dueCardsCount}</Text>
        <Text variant="bodyMedium">cards due today</Text>
      </Card.Content>
    </Card>

    {/* Review Options */}
    <List.Section>
      <List.Subheader>Review Modes</List.Subheader>
      <List.Item
        title="Spaced Repetition"
        description={`${dueCardsCount} cards due`}
        left={(props) => <List.Icon {...props} icon="refresh-circle" />}
        right={(props) => <List.Icon {...props} icon="chevron-right" />}
        onPress={() => startReview('SPACED_REPETITION')}
      />
      <List.Item
        title="Cram Mode"
        description="Review all cards quickly"
        left={(props) => <List.Icon {...props} icon="lightning-bolt" />}
        onPress={() => startCramMode()}
      />
      <List.Item
        title="Random Mode"
        description="Review random cards"
        left={(props) => <List.Icon {...props} icon="shuffle" />}
        onPress={() => startRandomMode()}
      />
    </List.Section>

    {/* Today's Progress */}
    <Card>
      <Card.Content>
        <Text variant="titleMedium">Today's Progress</Text>
        <ProgressBar progress={reviewProgress} />
        <Text variant="bodySmall">{reviewedCount} / {dailyLimit} cards</Text>
      </Card.Content>
    </Card>
  </ScrollView>
</SafeAreaView>
```

**Data Fetching**:
- Hook: `useDueCards()` from useReview hook
- Endpoint: `GET /api/review/due?mode=SPACED_REPETITION`
- Auto-refetch: Every 5 minutes (staleTime: 5 * 60 * 1000)
- Pull-to-refresh: Enabled

**Actions**:
- **Tap "Spaced Repetition"**: Navigate to ReviewSessionScreen with due cards
- **Tap "Cram Mode"**: Show folder/deck picker → Navigate to CramSessionScreen
- **Tap "Random Mode"**: Show card count picker → Navigate to ReviewSessionScreen
- **Settings button (header)**: Navigate to SettingsScreen (SRS settings tab)

**Empty State**:
- No due cards: "Great job! No cards due today. Come back tomorrow!" with illustration

---

#### Screen: StatisticsScreen (Stats Tab)

**Purpose**: Display user statistics, streak, box distribution

**Navigation**:
- Route: `MainTabs → Stats`
- Header: "Statistics"
- Tab bar: Active tab indicator

**Layout**:
```tsx
<SafeAreaView>
  <ScrollView>
    {/* Streak Card */}
    <Card style={styles.streakCard}>
      <Card.Content style={styles.streakContent}>
        <Icon source="fire" size={48} color={theme.colors.primary} />
        <Text variant="displayMedium">{streakDays}</Text>
        <Text variant="bodyMedium">day streak</Text>
      </Card.Content>
    </Card>

    {/* Today's Stats */}
    <Card>
      <Card.Content>
        <Text variant="titleMedium">Today</Text>
        <View style={styles.statsRow}>
          <StatItem label="Reviewed" value={todayReviewedCount} />
          <StatItem label="New Cards" value={todayNewCardsCount} />
          <StatItem label="Accuracy" value={`${todayAccuracy}%`} />
        </View>
      </Card.Content>
    </Card>

    {/* Box Distribution Chart */}
    <Card>
      <Card.Content>
        <Text variant="titleMedium">Box Distribution</Text>
        <BoxDistributionChart data={boxDistribution} />
        <Button mode="text" onPress={() => navigation.navigate('BoxDistribution')}>
          View Details
        </Button>
      </Card.Content>
    </Card>

    {/* All-Time Stats */}
    <List.Section>
      <List.Subheader>All-Time Statistics</List.Subheader>
      <List.Item
        title="Total Cards Learned"
        description={totalCardsLearned.toString()}
        left={(props) => <List.Icon {...props} icon="cards" />}
      />
      <List.Item
        title="Total Study Time"
        description={`${totalStudyMinutes} minutes`}
        left={(props) => <List.Icon {...props} icon="clock" />}
      />
    </List.Section>
  </ScrollView>
</SafeAreaView>
```

**Data Fetching**:
- Hook: `useUserStats()` from useStats hook
- Endpoints:
  - `GET /api/stats/user` (streak, total stats)
  - `GET /api/stats/box-distribution` (chart data)
- Auto-refetch: On screen focus (refetchOnMount: true)
- Pull-to-refresh: Enabled

**Actions**:
- **Tap "View Details" (box chart)**: Navigate to BoxDistributionScreen with full chart

**Components**:
- **StatItem**: Reusable component for label + value pair
- **BoxDistributionChart**: Simple bar chart (react-native-chart-kit or custom)

---

#### Screen: SettingsScreen (Settings Tab)

**Purpose**: SRS settings, user profile, app preferences

**Navigation**:
- Route: `MainTabs → Settings`
- Header: "Settings"
- Tab bar: Active tab indicator

**Layout**:
```tsx
<SafeAreaView>
  <ScrollView>
    {/* Profile Section */}
    <List.Section>
      <List.Subheader>Profile</List.Subheader>
      <List.Item
        title={user.name}
        description={user.email}
        left={(props) => <Avatar.Text {...props} label={user.name[0]} />}
        right={(props) => <List.Icon {...props} icon="chevron-right" />}
        onPress={() => navigation.navigate('ProfileSettings')}
      />
    </List.Section>

    {/* SRS Settings */}
    <List.Section>
      <List.Subheader>Spaced Repetition</List.Subheader>
      <List.Item
        title="Review Order"
        description={srsSettings.reviewOrder}
        left={(props) => <List.Icon {...props} icon="sort" />}
        right={(props) => <List.Icon {...props} icon="chevron-right" />}
        onPress={() => setReviewOrderPickerVisible(true)}
      />
      <List.Item
        title="Forgotten Card Action"
        description={srsSettings.forgottenCardAction}
        left={(props) => <List.Icon {...props} icon="undo" />}
        onPress={() => setForgottenActionPickerVisible(true)}
      />
      <List.Item
        title="New Cards Per Day"
        description={srsSettings.newCardsPerDay.toString()}
        left={(props) => <List.Icon {...props} icon="plus-circle" />}
        onPress={() => setNewCardsLimitPickerVisible(true)}
      />
      <List.Item
        title="Max Reviews Per Day"
        description={srsSettings.maxReviewsPerDay.toString()}
        left={(props) => <List.Icon {...props} icon="counter" />}
        onPress={() => setMaxReviewsPickerVisible(true)}
      />
    </List.Section>

    {/* Notifications */}
    <List.Section>
      <List.Subheader>Notifications</List.Subheader>
      <List.Item
        title="Daily Reminder"
        description={notificationEnabled ? 'Enabled' : 'Disabled'}
        left={(props) => <List.Icon {...props} icon="bell" />}
        right={() => (
          <Switch
            value={notificationEnabled}
            onValueChange={handleToggleNotification}
          />
        )}
      />
      {notificationEnabled && (
        <List.Item
          title="Reminder Time"
          description={notificationTime}
          left={(props) => <List.Icon {...props} icon="clock" />}
          onPress={() => setTimePickerVisible(true)}
        />
      )}
    </List.Section>

    {/* Appearance */}
    <List.Section>
      <List.Subheader>Appearance</List.Subheader>
      <List.Item
        title="Theme"
        description={theme === 'system' ? 'System Default' : theme}
        left={(props) => <List.Icon {...props} icon="palette" />}
        onPress={() => setThemePickerVisible(true)}
      />
      <List.Item
        title="Language"
        description={language === 'vi' ? 'Vietnamese' : 'English'}
        left={(props) => <List.Icon {...props} icon="translate" />}
        onPress={() => setLanguagePickerVisible(true)}
      />
    </List.Section>

    {/* Account Actions */}
    <List.Section>
      <List.Item
        title="Logout"
        titleStyle={{ color: theme.colors.error }}
        left={(props) => <List.Icon {...props} icon="logout" color={theme.colors.error} />}
        onPress={handleLogout}
      />
    </List.Section>
  </ScrollView>
</SafeAreaView>
```

**Data Fetching**:
- Hook: `useAuth()` for user data
- Hook: `useSRSSettings()` for SRS settings
- Endpoint: `GET /api/srs/settings`
- Auto-refetch: On screen focus

**Actions**:
- **Tap "Review Order"**: Show picker (Ascending, Descending, Random)
- **Tap "Forgotten Card Action"**: Show picker (Move to Box 1, Move Down N Boxes, Stay in Box)
- **Tap daily limits**: Show number picker (slider or stepper)
- **Toggle notification**: Update `notification_enabled` setting
- **Tap "Reminder Time"**: Show time picker (native iOS/Android)
- **Tap "Theme"**: Show picker (Light, Dark, System)
- **Tap "Language"**: Show picker (Vietnamese, English) → Update i18n locale
- **Tap "Logout"**: Confirm dialog → Call logout() → Navigate to Login

**Platform-Specific**:
- **iOS**:
  - Native action sheet for pickers
  - Native DateTimePicker for time selection
  - Haptic feedback on toggle/switch
- **Android**:
  - Bottom sheet for pickers
  - Native TimePickerAndroid
  - Material design switches

---

### 3.3 Detail Screens

#### Screen: FolderDetailScreen

**Purpose**: Display folder info, sub-folders, and decks

**Navigation**:
- Route: `MainStack → FolderDetail`
- Params: `{ folderId: string }`
- Header: Folder name + Edit button + More button (action sheet)

**Layout**:
```tsx
<SafeAreaView>
  {/* Breadcrumb */}
  <Breadcrumb path={folderPath} onNavigate={handleBreadcrumbPress} />

  <ScrollView>
    {/* Folder Info Card */}
    <Card>
      <Card.Content>
        <Text variant="headlineSmall">{folder.name}</Text>
        <Text variant="bodyMedium" style={styles.description}>
          {folder.description}
        </Text>
        <View style={styles.statsRow}>
          <Chip icon="folder">{folder.totalDecksCount} decks</Chip>
          <Chip icon="cards">{folder.totalCardsCount} cards</Chip>
          <Chip icon="clock">{folder.dueCardsCount} due</Chip>
        </View>
      </Card.Content>
    </Card>

    {/* Sub-folders */}
    {subFolders.length > 0 && (
      <View>
        <Text variant="titleMedium">Sub-folders</Text>
        <FlatList
          data={subFolders}
          renderItem={({ item }) => (
            <FolderItem folder={item} onPress={handleSubFolderPress} />
          )}
          keyExtractor={(item) => item.id}
        />
      </View>
    )}

    {/* Decks */}
    {decks.length > 0 && (
      <View>
        <Text variant="titleMedium">Decks</Text>
        <DeckList decks={decks} onDeckPress={handleDeckPress} />
      </View>
    )}
  </ScrollView>

  {/* Action Buttons */}
  <FAB.Group
    open={fabOpen}
    icon={fabOpen ? 'close' : 'plus'}
    actions={[
      { icon: 'folder-plus', label: 'New Folder', onPress: handleCreateFolder },
      { icon: 'cards', label: 'New Deck', onPress: handleCreateDeck },
    ]}
    onStateChange={({ open }) => setFabOpen(open)}
  />
</SafeAreaView>
```

**Data Fetching**:
- Hook: `useFolder(folderId)` from useFolder hook
- Endpoints:
  - `GET /api/folders/{id}` (folder details)
  - `GET /api/folders/{id}/children` (sub-folders + decks)
  - `GET /api/folders/{id}/stats` (recursive stats)
- Loading state: Skeleton loader
- Error state: "Failed to load folder. Tap to retry"

**Actions**:
- **Edit button (header)**: Navigate to EditFolderScreen (modal)
- **More button (header)**: Show action sheet (Move, Copy, Delete, View Stats)
- **Tap sub-folder**: Navigate to FolderDetailScreen with new folderId
- **Tap deck**: Navigate to DeckDetailScreen
- **FAB actions**: Create new folder or deck inside this folder
- **Tap breadcrumb**: Navigate to parent folder

**Action Sheet Options** (More button):
- **Rename**: Show inline rename dialog
- **Move**: Show folder picker → Move folder to selected destination
- **Copy**: Show folder picker → Copy folder to destination (async if large)
- **Delete**: Confirm dialog → Soft delete folder
- **View Statistics**: Navigate to FolderStatsScreen

---

#### Screen: DeckDetailScreen

**Purpose**: Display deck info and cards list

**Navigation**:
- Route: `MainStack → DeckDetail`
- Params: `{ deckId: string }`
- Header: Deck name + Edit button + More button

**Layout**:
```tsx
<SafeAreaView>
  <ScrollView>
    {/* Deck Info */}
    <Card>
      <Card.Content>
        <Text variant="headlineSmall">{deck.name}</Text>
        <Text variant="bodyMedium">{deck.description}</Text>
        <View style={styles.statsRow}>
          <Chip icon="cards">{deck.cardsCount} cards</Chip>
          <Chip icon="clock">{deck.dueCardsCount} due</Chip>
          <Chip icon="new-box">{deck.newCardsCount} new</Chip>
        </View>
      </Card.Content>
      <Card.Actions>
        <Button mode="contained" onPress={() => startReview(deckId)}>
          Start Review
        </Button>
        <Button mode="outlined" onPress={() => startCram(deckId)}>
          Cram Mode
        </Button>
      </Card.Actions>
    </Card>

    {/* Cards List */}
    <Text variant="titleMedium">Cards ({cards.length})</Text>
    <FlatList
      data={cards}
      renderItem={({ item }) => (
        <CardItem card={item} onPress={() => handleEditCard(item)} />
      )}
      keyExtractor={(item) => item.id}
      ListEmptyComponent={
        <EmptyState
          title="No cards yet"
          description="Add cards manually or import from CSV/Excel"
          action={<Button onPress={handleImport}>Import Cards</Button>}
        />
      }
    />
  </ScrollView>

  {/* FAB for actions */}
  <FAB.Group
    open={fabOpen}
    icon={fabOpen ? 'close' : 'plus'}
    actions={[
      { icon: 'card-plus', label: 'Add Card', onPress: handleAddCard },
      { icon: 'file-import', label: 'Import Cards', onPress: handleImport },
    ]}
    onStateChange={({ open }) => setFabOpen(open)}
  />
</SafeAreaView>
```

**Data Fetching**:
- Hook: `useDeck(deckId)` from useDeck hook
- Endpoints:
  - `GET /api/decks/{id}` (deck details)
  - `GET /api/decks/{id}/cards` (cards list)
- Pagination: Load 100 cards initially, load more on scroll
- Pull-to-refresh: Enabled

**Actions**:
- **Start Review**: Navigate to ReviewSessionScreen with deck cards
- **Cram Mode**: Navigate to CramSessionScreen
- **Edit button (header)**: Inline edit deck name/description
- **More button (header)**: Action sheet (Move, Copy, Delete, Export)
- **Tap card**: Navigate to EditCardScreen (inline edit modal)
- **Add Card (FAB)**: Navigate to CreateCardScreen (modal)
- **Import Cards (FAB)**: Navigate to ImportCardsScreen (modal)

**Action Sheet Options** (More button):
- **Move**: Show folder picker → Move deck
- **Copy**: Show folder picker → Copy deck (async if > 1000 cards)
- **Delete**: Confirm dialog → Soft delete deck
- **Export**: Show format picker (CSV/Excel) → Download file

---

### 3.4 Modal Screens

#### Screen: CreateFolderScreen

**Purpose**: Create new folder (modal form)

**Navigation**:
- Route: `MainStack → CreateFolder` (modal presentation)
- Params: `{ parentFolderId?: string }`
- Header: "Create Folder" + Cancel button + Save button

**Layout**:
```tsx
<SafeAreaView>
  <Appbar.Header>
    <Appbar.BackAction onPress={() => navigation.goBack()} />
    <Appbar.Content title="Create Folder" />
    <Appbar.Action icon="check" onPress={handleSubmit} disabled={!isValid} />
  </Appbar.Header>

  <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'}>
    <View style={styles.form}>
      <Controller
        control={control}
        name="name"
        render={({ field }) => (
          <TextInput
            label="Folder Name *"
            value={field.value}
            onChangeText={field.onChange}
            error={!!errors.name}
            helperText={errors.name?.message}
          />
        )}
      />
      <Controller
        control={control}
        name="description"
        render={({ field }) => (
          <TextInput
            label="Description (optional)"
            value={field.value}
            onChangeText={field.onChange}
            multiline
            numberOfLines={3}
          />
        )}
      />
      {parentFolderId && (
        <HelperText>
          This folder will be created inside: {parentFolderName}
        </HelperText>
      )}
    </View>
  </KeyboardAvoidingView>
</SafeAreaView>
```

**Data Fetching**:
- Hook: `useCreateFolder()` mutation from useFolder hook
- Endpoint: `POST /api/folders`
- Request: `{ name, description, parent_folder_id }`
- On success: Close modal, invalidate folder tree query, show toast

**Validation**:
- Name: Required, max 100 chars
- Description: Optional, max 500 chars
- Depth check: Ensure parent depth + 1 <= 10

**Platform-Specific**:
- **iOS**: Modal slides from bottom, swipe down to dismiss
- **Android**: Modal slides from bottom, back button dismisses

---

#### Screen: CreateDeckScreen

**Purpose**: Create new deck (modal form)

**Navigation**:
- Route: `MainStack → CreateDeck` (modal)
- Params: `{ folderId?: string }`
- Header: "Create Deck" + Cancel + Save

**Layout**: Similar to CreateFolderScreen

**Data Fetching**:
- Hook: `useCreateDeck()` mutation
- Endpoint: `POST /api/decks`
- Request: `{ name, description, folder_id }`

---

#### Screen: ImportCardsScreen

**Purpose**: Import cards from CSV/Excel file

**Navigation**:
- Route: `MainStack → ImportCards` (modal)
- Params: `{ deckId: string }`
- Header: "Import Cards" + Cancel + Import button

**Layout**:
```tsx
<SafeAreaView>
  <ScrollView>
    {/* Step 1: File Selection */}
    <Card>
      <Card.Content>
        <Text variant="titleMedium">1. Select File</Text>
        <Button
          mode="outlined"
          icon="file-upload"
          onPress={handleFilePicker}
          style={styles.uploadButton}
        >
          {file ? file.name : 'Choose CSV or Excel file'}
        </Button>
        {file && (
          <Chip icon="file" onClose={handleRemoveFile}>
            {file.name} ({formatFileSize(file.size)})
          </Chip>
        )}
      </Card.Content>
    </Card>

    {/* Step 2: Preview */}
    {previewData && (
      <Card>
        <Card.Content>
          <Text variant="titleMedium">2. Preview</Text>
          <Text variant="bodySmall">
            Found {previewData.totalRows} rows ({previewData.validRows} valid, {previewData.errorRows} errors)
          </Text>
          <DataTable>
            <DataTable.Header>
              <DataTable.Title>Front</DataTable.Title>
              <DataTable.Title>Back</DataTable.Title>
            </DataTable.Header>
            {previewData.rows.slice(0, 5).map((row, index) => (
              <DataTable.Row key={index}>
                <DataTable.Cell>{row.front}</DataTable.Cell>
                <DataTable.Cell>{row.back}</DataTable.Cell>
              </DataTable.Row>
            ))}
          </DataTable>
          <HelperText>Showing first 5 rows</HelperText>
        </Card.Content>
      </Card>
    )}

    {/* Step 3: Import Options */}
    {previewData && (
      <Card>
        <Card.Content>
          <Text variant="titleMedium">3. Import Options</Text>
          <Checkbox.Item
            label="Skip duplicate cards"
            status={skipDuplicates ? 'checked' : 'unchecked'}
            onPress={() => setSkipDuplicates(!skipDuplicates)}
          />
        </Card.Content>
      </Card>
    )}

    {/* Errors */}
    {previewData?.errors.length > 0 && (
      <Card style={styles.errorCard}>
        <Card.Content>
          <Text variant="titleMedium" style={{ color: theme.colors.error }}>
            Errors ({previewData.errors.length})
          </Text>
          <List.Section>
            {previewData.errors.slice(0, 3).map((error, index) => (
              <List.Item
                key={index}
                title={`Row ${error.row}: ${error.message}`}
                left={(props) => <List.Icon {...props} icon="alert-circle" />}
              />
            ))}
          </List.Section>
          <Button mode="text" onPress={handleDownloadErrorReport}>
            Download Error Report
          </Button>
        </Card.Content>
      </Card>
    )}
  </ScrollView>

  {/* Import Button */}
  <View style={styles.footer}>
    <Button
      mode="contained"
      onPress={handleImport}
      loading={isImporting}
      disabled={!previewData || previewData.validRows === 0}
    >
      Import {previewData?.validRows || 0} Cards
    </Button>
  </View>
</SafeAreaView>
```

**Data Fetching**:
- Hook: `useImportCards()` mutation
- Endpoint: `POST /api/decks/{deckId}/cards/import` (multipart/form-data)
- Request: File upload with FormData
- Progress: Show progress bar during upload
- Response: Import summary (success count, error details)

**Actions**:
- **Step 1 - Choose File**: Open native file picker (react-native-document-picker)
  - Filter: `.csv`, `.xlsx` files only
  - Max size: 50MB
- **Step 2 - Preview**: Parse first 10 rows, show preview table
  - Validate format (Front, Back columns)
  - Show error count
- **Step 3 - Import**: Upload file to backend
  - Show progress indicator
  - On success: Show toast "Imported X cards", close modal, refresh deck
  - On error: Show error details, allow download error report CSV

**Platform-Specific**:
- **iOS**: Native document picker (UIDocumentPickerViewController)
- **Android**: Native document picker (Intent.ACTION_OPEN_DOCUMENT)

---

#### Screen: ReviewSessionScreen

**Purpose**: Fullscreen flashcard review session

**Navigation**:
- Route: `MainStack → ReviewSession` (fullscreen modal)
- Params: `{ mode: 'SPACED_REPETITION' | 'CRAM' | 'RANDOM', scopeId?: string }`
- Header: Hidden (custom in-screen header)

**Layout**:
```tsx
<SafeAreaView style={styles.fullscreen}>
  {/* Custom Header */}
  <View style={styles.header}>
    <IconButton icon="close" onPress={handleExitSession} />
    <ReviewProgressBar current={currentIndex} total={totalCards} />
    <IconButton icon="dots-vertical" onPress={handleShowMenu} />
  </View>

  {/* Flashcard */}
  <View style={styles.cardContainer}>
    <FlashcardView
      card={currentCard}
      isFlipped={isFlipped}
      onFlip={() => setIsFlipped(!isFlipped)}
    />
  </View>

  {/* Rating Buttons (shown after flip) */}
  {isFlipped && (
    <RatingButtons
      onRate={handleRate}
      disabled={isSubmitting}
      ratings={['AGAIN', 'HARD', 'GOOD', 'EASY']}
    />
  )}

  {/* Action Buttons */}
  <View style={styles.actions}>
    <Button mode="text" icon="undo" onPress={handleUndo} disabled={!canUndo}>
      Undo
    </Button>
    <Button mode="text" icon="skip-next" onPress={handleSkip}>
      Skip
    </Button>
    <Button mode="text" icon="pencil" onPress={handleEditCard}>
      Edit
    </Button>
  </View>
</SafeAreaView>
```

**Data Fetching**:
- Hook: `useDueCards(mode, scopeId)` from useReview hook
- Endpoint: `GET /api/review/due?mode={mode}&scope_id={scopeId}`
- Mutation: `useSubmitReview()` for rating submission
- Endpoint: `POST /api/review/submit` (body: `{ card_id, rating }`)

**Actions**:
- **Tap card**: Flip to show answer (animated 3D flip)
- **Tap rating button**: Submit rating, move to next card
  - "Again" (< 1 min): Card moves to box 1 or stays (based on settings)
  - "Hard" (< 6 min): Card stays in same box, short interval
  - "Good" (next interval): Card moves to next box
  - "Easy" (4x interval): Card skips 1-2 boxes
- **Undo**: Go back to previous card, undo last rating
- **Skip**: Postpone card to end of session
- **Edit**: Open inline edit dialog, update card text
- **Exit (close button)**: Confirm dialog → Exit session

**Platform-Specific**:
- **iOS**:
  - 3D flip animation (CATransform3D)
  - Haptic feedback on rating (Medium impact)
  - Swipe left/right for rating shortcuts (Again/Good)
- **Android**:
  - 3D flip animation (RotateY)
  - Ripple effect on buttons

**Gestures**:
- **Tap card**: Flip front/back
- **Swipe left**: Rate as "Again" (quick reject)
- **Swipe right**: Rate as "Good" (quick accept)
- **Swipe down**: Skip card

**Session End**:
- When all cards reviewed: Show ReviewSummaryCard
  - Cards reviewed, accuracy, time spent
  - "Continue" button → Fetch more due cards
  - "Finish" button → Navigate back to home

---

#### Screen: CramSessionScreen

**Purpose**: Cram mode - review all cards quickly without SRS

**Layout**: Same as ReviewSessionScreen, but:
- No rating buttons (only "Next" button)
- No SRS algorithm applied
- Cards shuffled randomly
- No undo (simpler UX)
- Progress bar shows cards remaining

**Data Fetching**:
- Hook: `useCramCards(deckId)` or `useCramCards(folderId)` (folder scope)
- Endpoint: `GET /api/review/due?mode=CRAM&scope_id={id}`
- No mutations (no rating submission)

---

### 3.5 Other Screens

#### Screen: FolderStatsScreen

**Purpose**: Detailed folder statistics (recursive)

**Navigation**:
- Route: `MainStack → FolderStats`
- Params: `{ folderId: string }`
- Header: "Folder Statistics" + folder name

**Layout**:
```tsx
<SafeAreaView>
  <ScrollView>
    {/* Overview Card */}
    <Card>
      <Card.Content>
        <Text variant="headlineSmall">{folder.name} Statistics</Text>
        <View style={styles.statsGrid}>
          <StatTile label="Total Decks" value={stats.totalDecks} icon="folder" />
          <StatTile label="Total Cards" value={stats.totalCards} icon="cards" />
          <StatTile label="Due Cards" value={stats.dueCards} icon="clock" />
          <StatTile label="New Cards" value={stats.newCards} icon="new-box" />
        </View>
      </Card.Content>
    </Card>

    {/* Box Distribution (Recursive) */}
    <Card>
      <Card.Content>
        <Text variant="titleMedium">Box Distribution (All Cards)</Text>
        <BoxDistributionChart data={stats.boxDistribution} />
      </Card.Content>
    </Card>

    {/* Sub-folder Stats */}
    <List.Section>
      <List.Subheader>Sub-folder Statistics</List.Subheader>
      {stats.subFolderStats.map((subfolder) => (
        <List.Item
          key={subfolder.id}
          title={subfolder.name}
          description={`${subfolder.totalCards} cards, ${subfolder.dueCards} due`}
          left={(props) => <List.Icon {...props} icon="folder" />}
          right={(props) => <List.Icon {...props} icon="chevron-right" />}
          onPress={() => navigation.push('FolderStats', { folderId: subfolder.id })}
        />
      ))}
    </List.Section>
  </ScrollView>
</SafeAreaView>
```

**Data Fetching**:
- Hook: `useFolderStats(folderId)` from useStats hook
- Endpoint: `GET /api/stats/folder/{id}` (recursive stats)
- Response: Total cards, due cards, box distribution (all descendants)

---

#### Screen: BoxDistributionScreen

**Purpose**: Full-screen box distribution chart with details

**Navigation**:
- Route: `MainStack → BoxDistribution`
- Params: `{ scope: 'all' | 'folder' | 'deck', scopeId?: string }`
- Header: "Box Distribution"

**Layout**:
```tsx
<SafeAreaView>
  <ScrollView>
    {/* Chart */}
    <Card>
      <Card.Content>
        <Text variant="titleMedium">Cards per Box</Text>
        <BarChart
          data={{
            labels: ['Box 1', 'Box 2', 'Box 3', 'Box 4', 'Box 5', 'Box 6', 'Box 7'],
            datasets: [{ data: boxDistribution }],
          }}
          width={Dimensions.get('window').width - 40}
          height={220}
          chartConfig={chartConfig}
        />
      </Card.Content>
    </Card>

    {/* Details Table */}
    <DataTable>
      <DataTable.Header>
        <DataTable.Title>Box</DataTable.Title>
        <DataTable.Title numeric>Cards</DataTable.Title>
        <DataTable.Title numeric>Interval</DataTable.Title>
      </DataTable.Header>
      {boxData.map((box) => (
        <DataTable.Row key={box.boxNumber}>
          <DataTable.Cell>Box {box.boxNumber}</DataTable.Cell>
          <DataTable.Cell numeric>{box.cardCount}</DataTable.Cell>
          <DataTable.Cell numeric>{box.interval} days</DataTable.Cell>
        </DataTable.Row>
      ))}
    </DataTable>
  </ScrollView>
</SafeAreaView>
```

**Data Fetching**:
- Hook: `useBoxDistribution(scope, scopeId)` from useStats hook
- Endpoint: `GET /api/stats/box-distribution?scope={scope}&scope_id={scopeId}`
- Response: `[{ boxNumber, cardCount, interval }, ...]`

**Chart Library**: react-native-chart-kit (simple bar chart)

---

## 4. Component Specifications

### 4.1 Layout Components

#### Component: AppContainer

**Purpose**: Root app setup with navigation, providers

**Implementation**:
```tsx
import { NavigationContainer } from '@react-navigation/native';
import { QueryClientProvider } from '@tanstack/react-query';
import { PaperProvider } from 'react-native-paper';
import { AuthProvider } from '@/contexts/AuthContext';
import AppNavigator from '@/navigation/AppNavigator';

export default function AppContainer() {
  return (
    <QueryClientProvider client={queryClient}>
      <PaperProvider theme={theme}>
        <AuthProvider>
          <NavigationContainer>
            <AppNavigator />
          </NavigationContainer>
        </AuthProvider>
      </PaperProvider>
    </QueryClientProvider>
  );
}
```

---

#### Component: ScreenWrapper

**Purpose**: Consistent screen layout with safe area, status bar

**Props**:
```tsx
interface ScreenWrapperProps {
  children: React.ReactNode;
  withScrollView?: boolean;
  refreshControl?: React.ReactElement;
  backgroundColor?: string;
}
```

**Implementation**:
```tsx
import { SafeAreaView, ScrollView, StatusBar } from 'react-native';
import { useTheme } from 'react-native-paper';

export function ScreenWrapper({
  children,
  withScrollView = false,
  refreshControl,
  backgroundColor,
}: ScreenWrapperProps) {
  const theme = useTheme();
  const bgColor = backgroundColor || theme.colors.background;

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: bgColor }}>
      <StatusBar
        barStyle={theme.dark ? 'light-content' : 'dark-content'}
        backgroundColor={bgColor}
      />
      {withScrollView ? (
        <ScrollView refreshControl={refreshControl}>
          {children}
        </ScrollView>
      ) : (
        children
      )}
    </SafeAreaView>
  );
}
```

---

#### Component: Header

**Purpose**: Custom header with back button, title, actions

**Props**:
```tsx
interface HeaderProps {
  title: string;
  leftButton?: 'back' | 'close' | 'menu';
  onLeftPress?: () => void;
  rightButtons?: Array<{
    icon: string;
    onPress: () => void;
  }>;
}
```

**Implementation**:
```tsx
import { Appbar } from 'react-native-paper';

export function Header({ title, leftButton, onLeftPress, rightButtons }: HeaderProps) {
  return (
    <Appbar.Header>
      {leftButton === 'back' && <Appbar.BackAction onPress={onLeftPress} />}
      {leftButton === 'close' && <Appbar.Action icon="close" onPress={onLeftPress} />}
      {leftButton === 'menu' && <Appbar.Action icon="menu" onPress={onLeftPress} />}

      <Appbar.Content title={title} />

      {rightButtons?.map((button, index) => (
        <Appbar.Action key={index} icon={button.icon} onPress={button.onPress} />
      ))}
    </Appbar.Header>
  );
}
```

---

### 4.2 Folder Components

#### Component: FolderTreeView

**Purpose**: Collapsible folder tree with expand/collapse

**Props**:
```tsx
interface FolderTreeViewProps {
  folders: Folder[];
  onFolderPress: (folder: Folder) => void;
  onFolderLongPress: (folder: Folder) => void;
}
```

**Implementation**:
```tsx
import { FlatList } from 'react-native';
import { FolderItem } from './FolderItem';

export function FolderTreeView({ folders, onFolderPress, onFolderLongPress }: Props) {
  const [expandedIds, setExpandedIds] = useState<Set<string>>(new Set());

  const toggleExpand = (folderId: string) => {
    const newExpanded = new Set(expandedIds);
    if (newExpanded.has(folderId)) {
      newExpanded.delete(folderId);
    } else {
      newExpanded.add(folderId);
    }
    setExpandedIds(newExpanded);
  };

  const renderFolder = ({ item }: { item: Folder }) => {
    const isExpanded = expandedIds.has(item.id);
    return (
      <View>
        <FolderItem
          folder={item}
          isExpanded={isExpanded}
          onPress={() => onFolderPress(item)}
          onLongPress={() => onFolderLongPress(item)}
          onToggleExpand={() => toggleExpand(item.id)}
        />
        {isExpanded && item.children && (
          <View style={{ paddingLeft: 20 }}>
            <FolderTreeView
              folders={item.children}
              onFolderPress={onFolderPress}
              onFolderLongPress={onFolderLongPress}
            />
          </View>
        )}
      </View>
    );
  };

  return (
    <FlatList
      data={folders}
      renderItem={renderFolder}
      keyExtractor={(item) => item.id}
    />
  );
}
```

---

#### Component: FolderItem

**Purpose**: Single folder row with icon, name, expand button

**Props**:
```tsx
interface FolderItemProps {
  folder: Folder;
  isExpanded: boolean;
  onPress: () => void;
  onLongPress: () => void;
  onToggleExpand: () => void;
}
```

**Implementation**:
```tsx
import { List, IconButton } from 'react-native-paper';

export const FolderItem = React.memo(({
  folder,
  isExpanded,
  onPress,
  onLongPress,
  onToggleExpand,
}: FolderItemProps) => {
  return (
    <List.Item
      title={folder.name}
      description={`${folder.deckCount} decks, ${folder.cardCount} cards`}
      left={(props) => <List.Icon {...props} icon="folder" />}
      right={(props) => (
        folder.childrenCount > 0 ? (
          <IconButton
            icon={isExpanded ? 'chevron-down' : 'chevron-right'}
            onPress={onToggleExpand}
          />
        ) : null
      )}
      onPress={onPress}
      onLongPress={onLongPress}
    />
  );
});
```

---

#### Component: FolderActionSheet

**Purpose**: Bottom sheet with folder actions (rename, move, copy, delete)

**Props**:
```tsx
interface FolderActionSheetProps {
  folder: Folder;
  visible: boolean;
  onDismiss: () => void;
  onRename: () => void;
  onMove: () => void;
  onCopy: () => void;
  onDelete: () => void;
}
```

**Implementation**:
```tsx
import { Portal, Modal, List } from 'react-native-paper';

export function FolderActionSheet({
  folder,
  visible,
  onDismiss,
  onRename,
  onMove,
  onCopy,
  onDelete,
}: Props) {
  return (
    <Portal>
      <Modal
        visible={visible}
        onDismiss={onDismiss}
        contentContainerStyle={styles.bottomSheet}
      >
        <List.Item
          title="Rename"
          left={(props) => <List.Icon {...props} icon="pencil" />}
          onPress={() => { onRename(); onDismiss(); }}
        />
        <List.Item
          title="Move"
          left={(props) => <List.Icon {...props} icon="folder-move" />}
          onPress={() => { onMove(); onDismiss(); }}
        />
        <List.Item
          title="Copy"
          left={(props) => <List.Icon {...props} icon="content-copy" />}
          onPress={() => { onCopy(); onDismiss(); }}
        />
        <List.Item
          title="Delete"
          titleStyle={{ color: theme.colors.error }}
          left={(props) => <List.Icon {...props} icon="delete" color={theme.colors.error} />}
          onPress={() => { onDelete(); onDismiss(); }}
        />
      </Modal>
    </Portal>
  );
}
```

---

### 4.3 Deck Components

#### Component: DeckList

**Purpose**: Optimized FlatList for decks

**Props**:
```tsx
interface DeckListProps {
  decks: Deck[];
  onDeckPress: (deck: Deck) => void;
}
```

**Implementation**:
```tsx
import { FlatList } from 'react-native';
import { DeckCard } from './DeckCard';

export function DeckList({ decks, onDeckPress }: DeckListProps) {
  const renderDeck = ({ item }: { item: Deck }) => (
    <DeckCard deck={item} onPress={() => onDeckPress(item)} />
  );

  return (
    <FlatList
      data={decks}
      renderItem={renderDeck}
      keyExtractor={(item) => item.id}
      windowSize={10}
      maxToRenderPerBatch={10}
      removeClippedSubviews={true}
      getItemLayout={(data, index) => ({
        length: 100, // Fixed item height
        offset: 100 * index,
        index,
      })}
    />
  );
}
```

---

#### Component: DeckCard

**Purpose**: Deck card with name, stats, action button

**Props**:
```tsx
interface DeckCardProps {
  deck: Deck;
  onPress: () => void;
}
```

**Implementation**:
```tsx
import { Card, Text, Chip } from 'react-native-paper';

export const DeckCard = React.memo(({ deck, onPress }: DeckCardProps) => {
  return (
    <Card style={styles.card} onPress={onPress}>
      <Card.Content>
        <Text variant="titleMedium">{deck.name}</Text>
        <Text variant="bodySmall" numberOfLines={2}>
          {deck.description}
        </Text>
        <View style={styles.chipsRow}>
          <Chip icon="cards" compact>{deck.cardsCount} cards</Chip>
          <Chip icon="clock" compact>{deck.dueCardsCount} due</Chip>
        </View>
      </Card.Content>
    </Card>
  );
});
```

---

#### Component: DeckStatsCard

**Purpose**: Deck statistics display

**Props**:
```tsx
interface DeckStatsCardProps {
  stats: DeckStats;
}
```

**Implementation**:
```tsx
import { Card, DataTable } from 'react-native-paper';

export function DeckStatsCard({ stats }: DeckStatsCardProps) {
  return (
    <Card>
      <Card.Content>
        <Text variant="titleMedium">Statistics</Text>
        <DataTable>
          <DataTable.Row>
            <DataTable.Cell>Total Cards</DataTable.Cell>
            <DataTable.Cell numeric>{stats.totalCards}</DataTable.Cell>
          </DataTable.Row>
          <DataTable.Row>
            <DataTable.Cell>Due Cards</DataTable.Cell>
            <DataTable.Cell numeric>{stats.dueCards}</DataTable.Cell>
          </DataTable.Row>
          <DataTable.Row>
            <DataTable.Cell>New Cards</DataTable.Cell>
            <DataTable.Cell numeric>{stats.newCards}</DataTable.Cell>
          </DataTable.Row>
          <DataTable.Row>
            <DataTable.Cell>Mature Cards</DataTable.Cell>
            <DataTable.Cell numeric>{stats.matureCards}</DataTable.Cell>
          </DataTable.Row>
        </DataTable>
      </Card.Content>
    </Card>
  );
}
```

---

### 4.4 Review Components

#### Component: FlashcardView

**Purpose**: Animated flip card for review session

**Props**:
```tsx
interface FlashcardViewProps {
  card: Card;
  isFlipped: boolean;
  onFlip: () => void;
}
```

**Implementation**:
```tsx
import { Animated, TouchableWithoutFeedback } from 'react-native';
import { Card, Text } from 'react-native-paper';

export function FlashcardView({ card, isFlipped, onFlip }: Props) {
  const flipAnimation = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    Animated.timing(flipAnimation, {
      toValue: isFlipped ? 180 : 0,
      duration: 300,
      useNativeDriver: true,
    }).start();
  }, [isFlipped]);

  const frontInterpolate = flipAnimation.interpolate({
    inputRange: [0, 180],
    outputRange: ['0deg', '180deg'],
  });

  const backInterpolate = flipAnimation.interpolate({
    inputRange: [0, 180],
    outputRange: ['180deg', '360deg'],
  });

  return (
    <TouchableWithoutFeedback onPress={onFlip}>
      <View style={styles.container}>
        {/* Front side */}
        <Animated.View
          style={[
            styles.card,
            { transform: [{ rotateY: frontInterpolate }] },
            isFlipped && styles.hiddenCard,
          ]}
        >
          <Card>
            <Card.Content>
              <Text variant="headlineSmall" style={styles.centered}>
                {card.front}
              </Text>
            </Card.Content>
          </Card>
        </Animated.View>

        {/* Back side */}
        <Animated.View
          style={[
            styles.card,
            styles.backCard,
            { transform: [{ rotateY: backInterpolate }] },
            !isFlipped && styles.hiddenCard,
          ]}
        >
          <Card>
            <Card.Content>
              <Text variant="headlineSmall" style={styles.centered}>
                {card.back}
              </Text>
            </Card.Content>
          </Card>
        </Animated.View>
      </View>
    </TouchableWithoutFeedback>
  );
}
```

---

#### Component: RatingButtons

**Purpose**: 4 rating buttons (Again, Hard, Good, Easy)

**Props**:
```tsx
interface RatingButtonsProps {
  onRate: (rating: 'AGAIN' | 'HARD' | 'GOOD' | 'EASY') => void;
  disabled?: boolean;
}
```

**Implementation**:
```tsx
import { View } from 'react-native';
import { Button } from 'react-native-paper';

export function RatingButtons({ onRate, disabled }: Props) {
  return (
    <View style={styles.container}>
      <Button
        mode="contained"
        buttonColor={theme.colors.error}
        onPress={() => onRate('AGAIN')}
        disabled={disabled}
        style={styles.button}
      >
        Again
        <Text variant="bodySmall">{'<1m'}</Text>
      </Button>
      <Button
        mode="contained"
        buttonColor={theme.colors.warning}
        onPress={() => onRate('HARD')}
        disabled={disabled}
        style={styles.button}
      >
        Hard
        <Text variant="bodySmall">{'<6m'}</Text>
      </Button>
      <Button
        mode="contained"
        buttonColor={theme.colors.primary}
        onPress={() => onRate('GOOD')}
        disabled={disabled}
        style={styles.button}
      >
        Good
        <Text variant="bodySmall">{nextInterval}</Text>
      </Button>
      <Button
        mode="contained"
        buttonColor={theme.colors.success}
        onPress={() => onRate('EASY')}
        disabled={disabled}
        style={styles.button}
      >
        Easy
        <Text variant="bodySmall">{'4x'}</Text>
      </Button>
    </View>
  );
}
```

---

#### Component: ReviewProgressBar

**Purpose**: Show review session progress

**Props**:
```tsx
interface ReviewProgressBarProps {
  current: number;
  total: number;
}
```

**Implementation**:
```tsx
import { ProgressBar, Text } from 'react-native-paper';

export function ReviewProgressBar({ current, total }: Props) {
  const progress = current / total;

  return (
    <View style={styles.container}>
      <ProgressBar progress={progress} style={styles.progressBar} />
      <Text variant="bodySmall">
        {current} / {total} cards
      </Text>
    </View>
  );
}
```

---

#### Component: ReviewSummaryCard

**Purpose**: Session results summary

**Props**:
```tsx
interface ReviewSummaryCardProps {
  cardsReviewed: number;
  accuracy: number;
  timeSpent: number; // seconds
  onContinue: () => void;
  onFinish: () => void;
}
```

**Implementation**:
```tsx
import { Card, Button, Text } from 'react-native-paper';

export function ReviewSummaryCard({
  cardsReviewed,
  accuracy,
  timeSpent,
  onContinue,
  onFinish,
}: Props) {
  return (
    <Card>
      <Card.Content>
        <Text variant="headlineMedium" style={styles.title}>
          Session Complete!
        </Text>
        <View style={styles.statsRow}>
          <StatItem label="Cards Reviewed" value={cardsReviewed} />
          <StatItem label="Accuracy" value={`${accuracy}%`} />
          <StatItem label="Time Spent" value={formatTime(timeSpent)} />
        </View>
      </Card.Content>
      <Card.Actions>
        <Button mode="outlined" onPress={onContinue}>
          Continue Reviewing
        </Button>
        <Button mode="contained" onPress={onFinish}>
          Finish
        </Button>
      </Card.Actions>
    </Card>
  );
}
```

---

### 4.5 Form Components

#### Component: TextInput

**Purpose**: Custom styled text input (wrapper around Paper TextInput)

**Props**: Same as React Native Paper TextInput

**Implementation**:
```tsx
import { TextInput as PaperTextInput } from 'react-native-paper';

export function TextInput(props: TextInputProps) {
  return (
    <PaperTextInput
      {...props}
      mode="outlined"
      style={[styles.input, props.style]}
    />
  );
}
```

---

#### Component: FormField

**Purpose**: Label + Input + Error message

**Props**:
```tsx
interface FormFieldProps {
  label: string;
  value: string;
  onChangeText: (text: string) => void;
  error?: string;
  multiline?: boolean;
  secureTextEntry?: boolean;
}
```

**Implementation**:
```tsx
import { View } from 'react-native';
import { Text, HelperText } from 'react-native-paper';
import { TextInput } from './TextInput';

export function FormField({
  label,
  value,
  onChangeText,
  error,
  ...inputProps
}: FormFieldProps) {
  return (
    <View style={styles.container}>
      <TextInput
        label={label}
        value={value}
        onChangeText={onChangeText}
        error={!!error}
        {...inputProps}
      />
      {error && (
        <HelperText type="error" visible={!!error}>
          {error}
        </HelperText>
      )}
    </View>
  );
}
```

---

#### Component: PickerField

**Purpose**: Dropdown select field

**Props**:
```tsx
interface PickerFieldProps {
  label: string;
  value: string;
  options: Array<{ label: string; value: string }>;
  onValueChange: (value: string) => void;
}
```

**Implementation**:
```tsx
import { Platform } from 'react-native';
import { Menu, Button } from 'react-native-paper';

export function PickerField({ label, value, options, onValueChange }: Props) {
  const [visible, setVisible] = useState(false);

  const selectedOption = options.find((opt) => opt.value === value);

  return (
    <Menu
      visible={visible}
      onDismiss={() => setVisible(false)}
      anchor={
        <Button
          mode="outlined"
          onPress={() => setVisible(true)}
          icon="chevron-down"
        >
          {selectedOption?.label || label}
        </Button>
      }
    >
      {options.map((option) => (
        <Menu.Item
          key={option.value}
          title={option.label}
          onPress={() => {
            onValueChange(option.value);
            setVisible(false);
          }}
        />
      ))}
    </Menu>
  );
}
```

---

#### Component: SwitchField

**Purpose**: Toggle switch with label

**Props**:
```tsx
interface SwitchFieldProps {
  label: string;
  value: boolean;
  onValueChange: (value: boolean) => void;
}
```

**Implementation**:
```tsx
import { View } from 'react-native';
import { Text, Switch } from 'react-native-paper';

export function SwitchField({ label, value, onValueChange }: Props) {
  return (
    <View style={styles.container}>
      <Text variant="bodyLarge">{label}</Text>
      <Switch value={value} onValueChange={onValueChange} />
    </View>
  );
}
```

---

## 5. Platform-Specific Features

### 5.1 iOS Specifications

```yaml
Navigation:
  - Stack navigation with native swipe back gesture
  - Large title header (iOS 11+) on main screens
  - Modal presentation style: formSheet (centered modal on iPad)

Action Sheet:
  - Native iOS Action Sheet (ActionSheetIOS)
  - Options: Cancel button at bottom, destructive option in red
  - Blur background effect

Haptic Feedback:
  - Light impact: On button press, toggle switch
  - Medium impact: On review rating submission
  - Success notification: On completed review session
  - Error notification: On failed action
  - Selection: On picker value change

Safe Area:
  - Respect iPhone notch/Dynamic Island (SafeAreaView)
  - Bottom tab bar padding for home indicator
  - Keyboard avoidance with proper padding

Dark Mode:
  - Follow system settings (Appearance.getColorScheme())
  - Smooth transition animation
  - Adaptive colors for text, backgrounds, borders

Gestures:
  - Swipe left on list item → Delete action (iOS Mail style)
  - Swipe back from edge → Navigate back
  - Pull down on modal → Dismiss modal
```

### 5.2 Android Specifications

```yaml
Navigation:
  - Stack navigation with hardware back button support
  - Material Design header with shadow
  - Modal presentation: Slide from bottom, full screen

Action Sheet:
  - Bottom sheet modal (react-native-bottom-sheet)
  - Drag handle at top
  - Backdrop with press to dismiss
  - Smooth spring animation

Ripple Effect:
  - All touchable elements show ripple on press
  - Ripple color: theme.colors.primary with 20% opacity
  - Bounded ripple for cards, unbounded for icons

Status Bar:
  - Translucent status bar (StatusBar.setTranslucent)
  - Background color matches header
  - Light/dark icons based on theme

Dark Mode:
  - Follow system settings (Appearance.getColorScheme())
  - Material Design 3 dark theme colors
  - Elevated surfaces with correct opacity

Material Design:
  - FAB with elevation and animation
  - Snackbar for temporary messages
  - Chips for tags/filters
  - Cards with elevation and rounded corners
```

---

## 6. Gestures & Animations

### 6.1 Flashcard Flip Animation

**Trigger**: Tap card to reveal answer

**Animation**:
```tsx
// 1. Rotate card 180° on Y-axis
const flipAnimation = Animated.timing(flipValue, {
  toValue: 180,
  duration: 300,
  easing: Easing.bezier(0.4, 0.0, 0.2, 1), // Material easing
  useNativeDriver: true,
});

// 2. Interpolate rotation
const frontRotation = flipValue.interpolate({
  inputRange: [0, 180],
  outputRange: ['0deg', '180deg'],
});

const backRotation = flipValue.interpolate({
  inputRange: [0, 180],
  outputRange: ['180deg', '360deg'],
});

// 3. Hide back face during rotation
const frontOpacity = flipValue.interpolate({
  inputRange: [0, 90, 90.01, 180],
  outputRange: [1, 1, 0, 0],
});

const backOpacity = flipValue.interpolate({
  inputRange: [0, 90, 90.01, 180],
  outputRange: [0, 0, 1, 1],
});
```

**Result**: Smooth 3D card flip with correct z-index handling

---

### 6.2 Swipe to Delete (iOS)

**Trigger**: Swipe left on folder/deck item

**Animation**:
```tsx
// 1. Detect swipe gesture
const panGesture = Gesture.Pan()
  .onUpdate((event) => {
    if (event.translationX < -50) {
      // Show delete button
      translateX.value = withSpring(event.translationX, {
        damping: 20,
        stiffness: 90,
      });
    }
  })
  .onEnd((event) => {
    if (event.translationX < -100) {
      // Confirm delete
      translateX.value = withTiming(-screenWidth, {
        duration: 200,
      }, () => {
        runOnJS(handleDelete)();
      });
    } else {
      // Reset position
      translateX.value = withSpring(0);
    }
  });

// 2. Animate item removal (fade out + slide up)
const removeAnimation = Animated.sequence([
  Animated.timing(opacity, {
    toValue: 0,
    duration: 200,
    useNativeDriver: true,
  }),
  Animated.timing(height, {
    toValue: 0,
    duration: 200,
    useNativeDriver: false,
  }),
]);
```

**Result**: iOS Mail-style swipe to delete with smooth animations

---

### 6.3 Pull to Refresh

**Trigger**: Pull down on ScrollView

**Animation**:
```tsx
import { RefreshControl } from 'react-native';

<ScrollView
  refreshControl={
    <RefreshControl
      refreshing={isRefreshing}
      onRefresh={handleRefresh}
      tintColor={theme.colors.primary} // iOS
      colors={[theme.colors.primary]} // Android
      progressBackgroundColor={theme.colors.surface} // Android
    />
  }
>
  {/* Content */}
</ScrollView>

// Haptic feedback on refresh
const handleRefresh = async () => {
  if (Platform.OS === 'ios') {
    Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
  }

  await refetch();

  if (Platform.OS === 'ios') {
    Haptics.notificationAsync(
      Haptics.NotificationFeedbackType.Success
    );
  }
};
```

**Result**: Native pull-to-refresh with platform-specific styling and haptics

---

### 6.4 Folder Expand/Collapse

**Trigger**: Tap folder item

**Animation**:
```tsx
// 1. Rotate chevron icon (right → down)
const rotation = useSharedValue(0);

const animatedStyle = useAnimatedStyle(() => ({
  transform: [{ rotate: `${rotation.value}deg` }],
}));

// 2. Toggle expansion
const toggleExpand = () => {
  rotation.value = withTiming(isExpanded ? 0 : 90, {
    duration: 200,
    easing: Easing.out(Easing.ease),
  });

  // 3. Slide children in/out
  childrenHeight.value = withTiming(isExpanded ? 0 : measuredHeight, {
    duration: 200,
    easing: Easing.out(Easing.ease),
  });
};
```

**Result**: Smooth folder expansion with rotating chevron and sliding content

---

## 7. Push Notifications

### 7.1 Setup

**Library**: Firebase Cloud Messaging (react-native-firebase)

**Permissions**:
- **iOS**: Request permission on app launch
  ```tsx
  import messaging from '@react-native-firebase/messaging';

  const requestPermission = async () => {
    const authStatus = await messaging().requestPermission();
    const enabled =
      authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
      authStatus === messaging.AuthorizationStatus.PROVISIONAL;

    if (enabled) {
      console.log('Permission granted:', authStatus);
      const token = await messaging().getToken();
      await sendTokenToBackend(token);
    }
  };
  ```

- **Android**: Auto-granted (no explicit permission needed)

**Token Storage**:
- Get FCM token on app launch
- Send to backend: `POST /api/users/fcm-token` with `{ token }`
- Update token on refresh: `messaging().onTokenRefresh()`

### 7.2 Notification Types

#### 1. Daily Reminder

**Trigger**: Scheduled at user-configured time (default 9:00 AM)

**Payload**:
```json
{
  "notification": {
    "title": "Time to review!",
    "body": "You have 15 cards due today"
  },
  "data": {
    "type": "DAILY_REMINDER",
    "action": "OPEN_REVIEW_SCREEN"
  }
}
```

**Action**: Tap notification → Open ReviewScreen

**Schedule**: Backend sends at user's `notification_time` setting

---

#### 2. Review Completed

**Trigger**: After completing review session

**Payload**:
```json
{
  "notification": {
    "title": "Great job!",
    "body": "You completed 15 cards today"
  },
  "data": {
    "type": "REVIEW_COMPLETED",
    "action": "OPEN_STATS_SCREEN"
  }
}
```

**Action**: Tap notification → Open StatisticsScreen

---

#### 3. Copy Job Complete

**Trigger**: Async folder/deck copy job completes

**Payload**:
```json
{
  "notification": {
    "title": "Copy complete",
    "body": "Folder 'English Learning' copied successfully"
  },
  "data": {
    "type": "COPY_JOB_COMPLETE",
    "action": "OPEN_FOLDER_DETAIL",
    "folderId": "abc-123"
  }
}
```

**Action**: Tap notification → Open FolderDetailScreen with new folder

---

### 7.3 Notification Handling

**App in Foreground**:
```tsx
import { showToast } from '@/utils/toast';

messaging().onMessage(async (remoteMessage) => {
  // Show in-app toast/banner (not system notification)
  showToast({
    title: remoteMessage.notification?.title,
    message: remoteMessage.notification?.body,
    type: 'info',
    onPress: () => handleNotificationPress(remoteMessage.data),
  });
});
```

**App in Background/Quit**:
```tsx
// Open app from notification
messaging().onNotificationOpenedApp((remoteMessage) => {
  console.log('Notification opened app from background:', remoteMessage);
  handleNotificationPress(remoteMessage.data);
});

// App opened from quit state
messaging()
  .getInitialNotification()
  .then((remoteMessage) => {
    if (remoteMessage) {
      console.log('Notification opened app from quit state:', remoteMessage);
      handleNotificationPress(remoteMessage.data);
    }
  });
```

**Deep Linking**:
```tsx
const handleNotificationPress = (data: any) => {
  switch (data.action) {
    case 'OPEN_REVIEW_SCREEN':
      navigationRef.navigate('Review');
      break;
    case 'OPEN_STATS_SCREEN':
      navigationRef.navigate('Stats');
      break;
    case 'OPEN_FOLDER_DETAIL':
      navigationRef.navigate('FolderDetail', { folderId: data.folderId });
      break;
  }
};
```

---

## 8. Offline Support (Future - Not MVP)

**PLANNED for Post-MVP**:

```yaml
Local Storage:
  - AsyncStorage: User settings, auth tokens
  - SQLite (react-native-sqlite-storage): Cached cards, decks, folders
  - WatermelonDB (optional): Reactive database with sync

Sync Strategy:
  - Trigger: On app open, on network reconnect
  - Upload pending changes: Queue mutations in local DB
  - Download server updates: Fetch latest data since last sync
  - Conflict resolution: Last-write-wins (simple) or manual merge (complex)

Offline Indicators:
  - Banner: "You are offline. Changes will sync when online."
  - Icon: Offline icon in header
  - Badge: Show pending upload count

Queue Mutations:
  - Store mutations in local queue (AsyncStorage)
  - Retry on network reconnect
  - Show upload progress in sync screen
  - Handle conflicts: Show conflict resolution UI

Offline Review:
  - Cache due cards for today
  - Store review results locally
  - Upload review logs when online
  - Merge SRS state on sync
```

**Note**: Offline mode adds significant complexity (~2-3 weeks dev time). Not included in MVP.

---

## 9. Performance Optimizations

### 9.1 FlatList Optimization

**Configuration**:
```tsx
<FlatList
  data={items}
  renderItem={renderItem}
  keyExtractor={(item) => item.id}

  // Performance props
  windowSize={10}              // Render 10 screens worth of items
  maxToRenderPerBatch={10}     // Batch render 10 items at a time
  removeClippedSubviews={true} // Unmount off-screen items (Android)
  initialNumToRender={20}      // Render 20 items on mount

  // Fixed height optimization
  getItemLayout={(data, index) => ({
    length: ITEM_HEIGHT,
    offset: ITEM_HEIGHT * index,
    index,
  })}

  // Memoize renderItem
  renderItem={useCallback(({ item }) => (
    <MemoizedItem item={item} />
  ), [])}
/>
```

**Benefits**:
- Fast scrolling (no layout recalculation)
- Reduced memory usage (unmount off-screen items)
- Smooth animations (60 FPS)

---

### 9.2 Image Optimization (Future)

**Library**: react-native-fast-image (when images added)

**Configuration**:
```tsx
import FastImage from 'react-native-fast-image';

<FastImage
  source={{
    uri: imageUrl,
    priority: FastImage.priority.high,
  }}
  resizeMode={FastImage.resizeMode.contain}
  style={{ width: 200, height: 200 }}

  // Cache images locally
  cacheControl={FastImage.cacheControl.immutable}
/>
```

**Image Resizing**:
- Server-side: Resize images to max 1024x1024 before upload
- Client-side: Use `react-native-image-resizer` before upload
- Cache: Store resized images in device cache

---

### 9.3 Navigation Optimization

**Lazy Load Screens**:
```tsx
import { lazy, Suspense } from 'react';

// Lazy load heavy screens
const DeckDetailScreen = lazy(() => import('./screens/Deck/DeckDetailScreen'));
const ReviewSessionScreen = lazy(() => import('./screens/Review/ReviewSessionScreen'));

// Wrap with Suspense
<Stack.Screen name="DeckDetail">
  {(props) => (
    <Suspense fallback={<LoadingSpinner />}>
      <DeckDetailScreen {...props} />
    </Suspense>
  )}
</Stack.Screen>
```

**Detach Inactive Screens** (React Navigation v6):
```tsx
<Stack.Navigator
  screenOptions={{
    detachInactiveScreens: true, // Unmount inactive screens
  }}
>
  {/* Screens */}
</Stack.Navigator>
```

**Benefits**:
- Faster navigation (lazy load reduces initial bundle size)
- Lower memory usage (detach inactive screens)

---

### 9.4 State Optimization

**useMemo for Expensive Calculations**:
```tsx
const sortedFolders = useMemo(() => {
  return folders.sort((a, b) => a.name.localeCompare(b.name));
}, [folders]);
```

**React.memo for List Items**:
```tsx
export const FolderItem = React.memo(({ folder, onPress }: Props) => {
  return (
    <List.Item title={folder.name} onPress={onPress} />
  );
}, (prevProps, nextProps) => {
  // Only re-render if folder changed
  return prevProps.folder.id === nextProps.folder.id;
});
```

**Avoid Inline Functions in Render**:
```tsx
// Bad: Creates new function on every render
<Button onPress={() => handlePress(item.id)} />

// Good: Use useCallback
const handlePress = useCallback((id: string) => {
  // ...
}, []);

<Button onPress={() => handlePress(item.id)} />
```

---

### 9.5 Bundle Optimization

**Code Splitting**:
```typescript
// metro.config.js
module.exports = {
  transformer: {
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: true, // Inline require() calls
      },
    }),
  },
};
```

**Hermes Engine** (Android):
```gradle
// android/app/build.gradle
project.ext.react = [
  enableHermes: true, // Enable Hermes for faster startup
]
```

**Benefits**:
- Faster app startup (50% improvement on Android)
- Smaller bundle size (~30% reduction)
- Lower memory usage

**Remove Unused Libraries**:
- Audit package.json: Remove unused dependencies
- Tree-shaking: Ensure unused code is eliminated
- Use `react-native-bundle-visualizer` to analyze bundle

---

## 10. Native Modules (if needed)

### 10.1 File Picker (Import Cards)

**Library**: react-native-document-picker

**Usage**:
```tsx
import DocumentPicker from 'react-native-document-picker';

const handleFilePicker = async () => {
  try {
    const result = await DocumentPicker.pick({
      type: [
        DocumentPicker.types.csv,
        DocumentPicker.types.xlsx,
      ],
      copyTo: 'cachesDirectory', // Copy to app cache
    });

    console.log('Selected file:', result);

    // Upload file
    const formData = new FormData();
    formData.append('file', {
      uri: result.fileCopyUri, // Use copied file URI
      name: result.name,
      type: result.type,
    });

    await uploadFile(formData);
  } catch (err) {
    if (DocumentPicker.isCancel(err)) {
      console.log('User cancelled');
    } else {
      console.error('Error:', err);
    }
  }
};
```

**Supports**:
- CSV files (`.csv`)
- Excel files (`.xlsx`, `.xls`)
- Platform: iOS, Android

---

### 10.2 Biometric Authentication (Future)

**Library**: react-native-biometrics

**Usage**:
```tsx
import ReactNativeBiometrics from 'react-native-biometrics';

const rnBiometrics = new ReactNativeBiometrics();

// Check if biometrics available
const { available, biometryType } = await rnBiometrics.isSensorAvailable();

if (available) {
  console.log('Biometry type:', biometryType); // FaceID, TouchID, Biometrics

  // Authenticate
  const { success } = await rnBiometrics.simplePrompt({
    promptMessage: 'Confirm fingerprint',
  });

  if (success) {
    // Login user
  }
}
```

**Supports**:
- **iOS**: Face ID, Touch ID
- **Android**: Fingerprint, Face unlock

**Use Case**:
- Enable in settings
- Prompt biometric on app launch
- Fall back to password on failure

---

## 11. Testing Strategy

### 11.1 Unit Tests (Jest)

**Test Hooks**:
```tsx
// __tests__/hooks/useFolders.test.ts
import { renderHook, waitFor } from '@testing-library/react-native';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useFolderTree } from '@/hooks/useFolder';

const queryClient = new QueryClient();
const wrapper = ({ children }) => (
  <QueryClientProvider client={queryClient}>
    {children}
  </QueryClientProvider>
);

test('useFolderTree fetches folders', async () => {
  const { result } = renderHook(() => useFolderTree(), { wrapper });

  expect(result.current.isLoading).toBe(true);

  await waitFor(() => {
    expect(result.current.isSuccess).toBe(true);
  });

  expect(result.current.data).toHaveLength(3);
});
```

**Test Utils**:
```tsx
// __tests__/utils/formatters.test.ts
import { formatDate, formatFileSize } from '@/utils/formatters';

test('formatDate formats ISO string', () => {
  const date = '2025-01-15T10:30:00Z';
  expect(formatDate(date)).toBe('Jan 15, 2025');
});

test('formatFileSize converts bytes to readable format', () => {
  expect(formatFileSize(1024)).toBe('1.0 KB');
  expect(formatFileSize(1048576)).toBe('1.0 MB');
});
```

---

### 11.2 Component Tests (React Native Testing Library)

**Test FolderItem**:
```tsx
// __tests__/components/FolderItem.test.tsx
import { render, fireEvent } from '@testing-library/react-native';
import { FolderItem } from '@/components/folder/FolderItem';

test('FolderItem renders correctly', () => {
  const folder = {
    id: '1',
    name: 'Test Folder',
    deckCount: 5,
    cardCount: 100,
  };

  const { getByText } = render(
    <FolderItem folder={folder} onPress={jest.fn()} />
  );

  expect(getByText('Test Folder')).toBeTruthy();
  expect(getByText('5 decks, 100 cards')).toBeTruthy();
});

test('FolderItem calls onPress when tapped', () => {
  const onPress = jest.fn();
  const folder = { id: '1', name: 'Test' };

  const { getByText } = render(
    <FolderItem folder={folder} onPress={onPress} />
  );

  fireEvent.press(getByText('Test'));
  expect(onPress).toHaveBeenCalledWith(folder);
});
```

**Test DeckCard**:
```tsx
// __tests__/components/DeckCard.test.tsx
import { render } from '@testing-library/react-native';
import { DeckCard } from '@/components/deck/DeckCard';

test('DeckCard displays stats correctly', () => {
  const deck = {
    id: '1',
    name: 'IELTS Vocabulary',
    cardsCount: 500,
    dueCardsCount: 50,
  };

  const { getByText } = render(
    <DeckCard deck={deck} onPress={jest.fn()} />
  );

  expect(getByText('IELTS Vocabulary')).toBeTruthy();
  expect(getByText('500 cards')).toBeTruthy();
  expect(getByText('50 due')).toBeTruthy();
});
```

**Test ReviewCard (Flip Animation)**:
```tsx
// __tests__/components/FlashcardView.test.tsx
import { render, fireEvent, waitFor } from '@testing-library/react-native';
import { FlashcardView } from '@/components/review/FlashcardView';

test('FlashcardView flips when tapped', async () => {
  const card = { id: '1', front: 'Hello', back: 'Xin chào' };

  const { getByText } = render(
    <FlashcardView card={card} isFlipped={false} onFlip={jest.fn()} />
  );

  // Initially shows front
  expect(getByText('Hello')).toBeTruthy();

  // Tap to flip
  fireEvent.press(getByText('Hello'));

  // After flip, shows back
  await waitFor(() => {
    expect(getByText('Xin chào')).toBeTruthy();
  });
});
```

---

### 11.3 E2E Tests (Detox)

**Setup**: Detox for React Native E2E testing

**Test Login Flow**:
```tsx
// e2e/auth.e2e.ts
describe('Authentication', () => {
  beforeAll(async () => {
    await device.launchApp();
  });

  it('should login successfully', async () => {
    await element(by.id('email-input')).typeText('user@example.com');
    await element(by.id('password-input')).typeText('password123');
    await element(by.id('login-button')).tap();

    // Should navigate to home screen
    await expect(element(by.text('My Folders'))).toBeVisible();
  });
});
```

**Test Create Folder Flow**:
```tsx
// e2e/folder.e2e.ts
describe('Folder Management', () => {
  it('should create new folder', async () => {
    await element(by.id('create-folder-fab')).tap();
    await element(by.id('folder-name-input')).typeText('English Learning');
    await element(by.id('save-button')).tap();

    // Should show in folder list
    await expect(element(by.text('English Learning'))).toBeVisible();
  });
});
```

**Test Review Session**:
```tsx
// e2e/review.e2e.ts
describe('Review Session', () => {
  it('should complete review session', async () => {
    await element(by.text('Spaced Repetition')).tap();

    // Tap card to flip
    await element(by.id('flashcard')).tap();

    // Rate card
    await element(by.id('rating-good')).tap();

    // Should show next card
    await expect(element(by.id('progress-bar'))).toBeVisible();
  });
});
```

**Test Import Cards**:
```tsx
// e2e/import.e2e.ts
describe('Import Cards', () => {
  it('should import cards from CSV', async () => {
    await element(by.id('import-cards-button')).tap();

    // Select file (mocked in test environment)
    await element(by.id('file-picker')).tap();
    await element(by.text('test-cards.csv')).tap();

    // Preview and import
    await expect(element(by.text('Found 100 rows'))).toBeVisible();
    await element(by.id('import-button')).tap();

    // Should show success
    await expect(element(by.text('Imported 100 cards'))).toBeVisible();
  });
});
```

---

## 12. Accessibility (A11y)

### 12.1 Screen Reader Support

**VoiceOver (iOS) / TalkBack (Android)**:

```tsx
import { AccessibilityInfo } from 'react-native';

// Check if screen reader enabled
const [screenReaderEnabled, setScreenReaderEnabled] = useState(false);

useEffect(() => {
  AccessibilityInfo.isScreenReaderEnabled().then(setScreenReaderEnabled);

  const subscription = AccessibilityInfo.addEventListener(
    'screenReaderChanged',
    setScreenReaderEnabled
  );

  return () => subscription.remove();
}, []);
```

**Accessible Labels**:
```tsx
<TouchableOpacity
  accessibilityLabel="Delete folder"
  accessibilityHint="Double tap to delete this folder"
  onPress={handleDelete}
>
  <Icon name="delete" />
</TouchableOpacity>
```

**Accessible Roles**:
```tsx
<View accessibilityRole="button">
  <Text>Create Folder</Text>
</View>

<FlatList
  accessibilityRole="list"
  data={folders}
  renderItem={({ item }) => (
    <View accessibilityRole="listitem">
      <Text>{item.name}</Text>
    </View>
  )}
/>
```

---

### 12.2 Contrast Ratios (WCAG AA)

**Color Palette** (Material Design 3):
```tsx
const lightTheme = {
  colors: {
    primary: '#1976D2',        // Blue 700
    onPrimary: '#FFFFFF',      // White (contrast 4.5:1)
    background: '#FFFFFF',     // White
    onBackground: '#000000',   // Black (contrast 21:1)
    surface: '#F5F5F5',        // Gray 100
    onSurface: '#212121',      // Gray 900 (contrast 16:1)
    error: '#D32F2F',          // Red 700
    onError: '#FFFFFF',        // White (contrast 4.5:1)
  },
};

const darkTheme = {
  colors: {
    primary: '#90CAF9',        // Blue 200
    onPrimary: '#000000',      // Black (contrast 8:1)
    background: '#121212',     // Dark background
    onBackground: '#FFFFFF',   // White (contrast 15:1)
    surface: '#1E1E1E',        // Dark surface
    onSurface: '#E0E0E0',      // Light gray (contrast 12:1)
    error: '#EF5350',          // Red 400
    onError: '#000000',        // Black (contrast 5:1)
  },
};
```

**Validation**: Use [WebAIM Contrast Checker](https://webaim.org/resources/contrastchecker/) to verify all color combinations meet WCAG AA (4.5:1 for normal text, 3:1 for large text).

---

### 12.3 Font Scaling

**Respect System Font Size**:
```tsx
import { Text } from 'react-native-paper';

// Paper Text components auto-scale with system settings

<Text variant="displayLarge">Title</Text>  // Auto-scales
<Text variant="bodyMedium">Body text</Text>  // Auto-scales
```

**Manual Scaling**:
```tsx
import { PixelRatio } from 'react-native';

const getFontSize = (size: number) => {
  const fontScale = PixelRatio.getFontScale();
  return size * fontScale;
};

<Text style={{ fontSize: getFontSize(16) }}>Scaled text</Text>
```

**Test with Large Text**:
- iOS: Settings → Accessibility → Display & Text Size → Larger Text
- Android: Settings → Display → Font size → Largest

---

### 12.4 Gesture Alternatives

**Swipe Actions → Button Alternative**:
```tsx
// Swipe left to delete (iOS)
<Swipeable
  renderRightActions={() => (
    <TouchableOpacity onPress={handleDelete}>
      <Text>Delete</Text>
    </TouchableOpacity>
  )}
>
  <FolderItem folder={folder} />
</Swipeable>

// Alternative: Long press → Action sheet (accessible)
<TouchableOpacity
  onLongPress={() => setActionSheetVisible(true)}
  accessibilityActions={[
    { name: 'delete', label: 'Delete folder' },
    { name: 'rename', label: 'Rename folder' },
  ]}
  onAccessibilityAction={(event) => {
    switch (event.nativeEvent.actionName) {
      case 'delete':
        handleDelete();
        break;
      case 'rename':
        handleRename();
        break;
    }
  }}
>
  <FolderItem folder={folder} />
</TouchableOpacity>
```

---

## 13. Internationalization (i18n)

### 13.1 Setup (i18n-js)

**Configuration**:
```tsx
// src/i18n/index.ts
import { I18n } from 'i18n-js';
import * as Localization from 'expo-localization';
import en from './en.json';
import vi from './vi.json';

const i18n = new I18n({
  en,
  vi,
});

// Set default locale
i18n.locale = Localization.locale;
i18n.enableFallback = true;
i18n.defaultLocale = 'vi';

export default i18n;
```

**Translation Files**:
```json
// src/i18n/en.json
{
  "common": {
    "save": "Save",
    "cancel": "Cancel",
    "delete": "Delete"
  },
  "folders": {
    "title": "My Folders",
    "createFolder": "Create Folder",
    "emptyState": "No folders yet. Create one to get started!"
  },
  "review": {
    "dueCards": "{{count}} cards due today",
    "sessionComplete": "Session Complete!",
    "cardsReviewed": "Cards Reviewed"
  }
}

// src/i18n/vi.json
{
  "common": {
    "save": "Lưu",
    "cancel": "Hủy",
    "delete": "Xóa"
  },
  "folders": {
    "title": "Thư mục của tôi",
    "createFolder": "Tạo thư mục",
    "emptyState": "Chưa có thư mục. Tạo thư mục mới để bắt đầu!"
  },
  "review": {
    "dueCards": "{{count}} thẻ cần ôn hôm nay",
    "sessionComplete": "Hoàn thành!",
    "cardsReviewed": "Số thẻ đã ôn"
  }
}
```

### 13.2 Usage in Components

```tsx
import i18n from '@/i18n';

export function FoldersScreen() {
  return (
    <View>
      <Text>{i18n.t('folders.title')}</Text>
      <Button>{i18n.t('folders.createFolder')}</Button>
      <Text>{i18n.t('review.dueCards', { count: 15 })}</Text>
    </View>
  );
}
```

### 13.3 Language Switcher

```tsx
// In SettingsScreen
import i18n from '@/i18n';
import AsyncStorage from '@react-native-async-storage/async-storage';

const changeLanguage = async (locale: 'en' | 'vi') => {
  i18n.locale = locale;
  await AsyncStorage.setItem('userLocale', locale);

  // Force re-render
  forceUpdate();
};

<PickerField
  label="Language"
  value={i18n.locale}
  options={[
    { label: 'English', value: 'en' },
    { label: 'Tiếng Việt', value: 'vi' },
  ]}
  onValueChange={changeLanguage}
/>
```

---

## 14. Conclusion

This mobile app specification provides:

1. **Complete Screen Specs**: 15+ screens with layouts, data fetching, actions
2. **Reusable Components**: 12+ components (folders, decks, cards, review)
3. **Platform-Specific UX**: iOS vs Android differences (gestures, animations, UI)
4. **Performance Optimized**: FlatList optimization, lazy loading, memoization
5. **Accessibility**: Screen reader support, contrast ratios, font scaling
6. **Push Notifications**: Daily reminders, job completion, deep linking
7. **Testing Strategy**: Unit tests (Jest), component tests (RNTL), E2E tests (Detox)
8. **Internationalization**: Vietnamese/English support with i18n-js

**Key Design Decisions**:
- **State Management**: TanStack Query + Context API (same as web, no Redux overhead)
- **UI Library**: React Native Paper (Material Design 3, accessible)
- **Navigation**: React Navigation v6 (native feel, deep linking)
- **Shared Code**: Services, hooks, types shared with web app (50%+ code reuse)
- **Native Modules**: File picker for import, FCM for notifications, biometrics (future)

**Implementation Timeline**:
- **Week 1-2**: Navigation setup, auth screens, folder tree view
- **Week 3-4**: Deck/card management, import/export screens
- **Week 5-6**: Review session (flashcards, animations), notifications
- **Week 7-8**: Statistics, settings, polish (gestures, haptics, i18n)
- **Week 9**: Testing (unit, component, E2E), bug fixes
- **Week 10**: Platform-specific polish (iOS/Android differences)

**Total**: 10-12 weeks for full mobile app (iOS + Android)

---

**Version**: 1.0 MVP
**Last Updated**: January 2025
**Focus**: Native-feel mobile app with shared business logic with web
