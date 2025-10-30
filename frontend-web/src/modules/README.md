/**
 * Frontend Module Structure Documentation
 * 
 * This document describes the module-based architecture for the RepeatWise frontend application.
 * 
 * ## Architecture Overview
 * 
 * The frontend is organized into feature modules, each containing:
 * - `components/`: React components specific to the module
 * - `hooks/`: Custom React hooks for module-specific logic
 * - `types/`: TypeScript type definitions for the module
 * 
 * ## Module Structure
 * 
 * ```
 * src/
 * ├── modules/
 * │   ├── auth/           # Authentication & authorization
 * │   ├── folders/        # Folder hierarchy management
 * │   ├── decks/          # Deck management
 * │   ├── cards/          # Flashcard management
 * │   ├── review/         # SRS review session
 * │   ├── importExport/   # Bulk import/export
 * │   ├── settings/       # User settings
 * │   └── stats/          # Statistics & analytics
 * ├── lib/                # Shared utilities
 * │   ├── hooks/          # Shared hooks
 * │   ├── utils/          # Utility functions
 * │   ├── stores/         # State management stores
 * │   └── validators/     # Validation utilities
 * ├── api/                # API client & types
 * ├── components/         # Shared UI components
 * ├── pages/              # Page components
 * └── constants/          # Constants & configuration
 * ```
 * 
 * ## Module Details
 * 
 * ### auth Module
 * Components: RegisterForm, LoginForm, ProtectedRoute, AuthContext
 * 
 * ### folders Module
 * Components: FolderTree, Breadcrumb, FolderModal, MoveFolderModal, CopyFolderModal, FolderStatistics
 * 
 * ### decks Module
 * Components: DeckList, DeckCard, DeckDetails, DeckActions
 * 
 * ### cards Module
 * Components: CardList, CardEditor, CardForm, CardValidator
 * 
 * ### review Module
 * Components: ReviewSession, CardDisplay, RatingButtons, ProgressBar, UndoButton, SkipButton
 * 
 * ### importExport Module
 * Components: ImportWizard, FileUpload, ColumnMapping, ValidationPreview, ProgressIndicator, ExportDialog
 * 
 * ### settings Module
 * Components: ProfileForm, PasswordForm, SrsSettingsForm, ThemeSelector, LanguageSelector
 * 
 * ### stats Module
 * Components: Dashboard, StreakCounter, BoxDistributionChart, ReviewHistory
 * 
 * ## Import Conventions
 * 
 * ```typescript
 * // Import from a specific module
 * import { RegisterForm, useAuth } from '@/modules/auth'
 * 
 * // Import from shared lib
 * import { formatDate, validateEmail } from '@/lib/utils'
 * 
 * // Import from API
 * import { authApi, userApi } from '@/api/modules'
 * ```
 */
