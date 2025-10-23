# Frontend Component Hierarchy

**Source**: `04-detail-design/07-frontend-web-specs.md` & `08-frontend-mobile-specs.md`.

- **App shell**: `AppLayout` → `Sidebar` (web), `TabNavigator` (mobile).
- **Library**: `FolderTree` → `FolderList` → `DeckList` → `DeckCardGrid`.
- **Review**: `ReviewWorkspace` (web) / `ReviewScreen` (mobile) using shared `ReviewCard` component.
- **Settings**: `SettingsLayout` → `SrsSettingsForm`, `NotificationSettingsForm`.

Use to orient Claude when building new UI features.
