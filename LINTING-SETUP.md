# 🎨 Linting & Formatting Setup

Complete linting and formatting configuration for RepeatWise project.

## 📋 Overview

**Status**: ✅ Complete
**Tools**: ESLint + Prettier + EditorConfig
**Coverage**: Web + Mobile + Universal

---

## 🌐 Frontend Web Configuration

### Files Created

1. **[frontend-web/.prettierrc](frontend-web/.prettierrc)** - Prettier config
2. **[frontend-web/.prettierignore](frontend-web/.prettierignore)** - Prettier ignore patterns
3. **[frontend-web/eslint.config.js](frontend-web/eslint.config.js)** - ESLint flat config (updated)

### ESLint Rules

**Base Configs**:
- `@eslint/js` - JavaScript recommended
- `typescript-eslint` - TypeScript recommended
- `eslint-plugin-react-hooks` - React Hooks rules
- `eslint-plugin-react-refresh` - Vite HMR rules

**Custom Rules**:
```js
// TypeScript
'@typescript-eslint/no-explicit-any': 'warn'
'@typescript-eslint/no-unused-vars': 'warn' // Ignore vars starting with _
'@typescript-eslint/no-non-null-assertion': 'warn'

// React
'react-hooks/rules-of-hooks': 'error'
'react-hooks/exhaustive-deps': 'warn'

// General
'no-console': 'warn' // Allow console.warn and console.error
'no-debugger': 'warn'
'prefer-const': 'warn'
'no-var': 'error'
```

### Prettier Config

```json
{
  "semi": false,
  "trailingComma": "es5",
  "singleQuote": true,
  "printWidth": 100,
  "tabWidth": 2,
  "arrowParens": "avoid"
}
```

### Scripts

```bash
# Linting
npm run lint          # Check for errors
npm run lint:fix      # Auto-fix errors

# Formatting
npm run format        # Format all files
npm run format:check  # Check formatting
```

---

## 📱 Frontend Mobile Configuration

### Files Created

1. **[frontend-mobile/.eslintrc.js](frontend-mobile/.eslintrc.js)** - ESLint config (updated)
2. **[frontend-mobile/.prettierrc.js](frontend-mobile/.prettierrc.js)** - Prettier config (updated)
3. **[frontend-mobile/.prettierignore](frontend-mobile/.prettierignore)** - Prettier ignore patterns

### ESLint Rules

**Base Configs**:
- `@react-native` - React Native recommended
- `@typescript-eslint/recommended` - TypeScript rules

**Custom Rules**:
```js
// TypeScript
'@typescript-eslint/no-explicit-any': 'warn'
'@typescript-eslint/no-unused-vars': 'warn'

// React Native
'react/react-in-jsx-scope': 'off' // Not needed in RN
'react-native/no-unused-styles': 'warn'
'react-native/no-inline-styles': 'warn'

// React Hooks
'react-hooks/rules-of-hooks': 'error'
'react-hooks/exhaustive-deps': 'warn'

// Code Style
'semi': ['error', 'never']
'quotes': ['error', 'single']
```

### Prettier Config

```js
{
  semi: false,
  trailingComma: 'es5',
  singleQuote: true,
  printWidth: 100,
  tabWidth: 2,
  arrowParens: 'avoid'
}
```

### Scripts

```bash
# Linting
npm run lint          # Check for errors
npm run lint:fix      # Auto-fix errors

# Formatting
npm run format        # Format all files
npm run format:check  # Check formatting
```

---

## 🔧 EditorConfig (Universal)

### File Created

**[.editorconfig](.editorconfig)** - Universal editor settings

### Configuration

```ini
# Universal
[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true

# JavaScript/TypeScript
[*.{ts,tsx,js,jsx,json}]
indent_style = space
indent_size = 2
max_line_length = 100

# Java
[*.{java,kt}]
indent_style = space
indent_size = 4
max_line_length = 120

# YAML
[*.{yml,yaml}]
indent_style = space
indent_size = 2
```

**Supported Files**:
- TypeScript/JavaScript/JSON: 2 spaces
- Java/Kotlin: 4 spaces
- XML/YAML: 2 spaces
- Shell scripts: LF line endings
- Batch scripts: CRLF line endings

---

## 📊 Configuration Summary

### Frontend Web

| Tool | Config File | Status | Rules |
|------|------------|--------|-------|
| ESLint | `eslint.config.js` | ✅ Updated | TypeScript + React + Custom |
| Prettier | `.prettierrc` | ✅ Created | Standard + Custom |
| EditorConfig | `../.editorconfig` | ✅ Shared | Universal |

### Frontend Mobile

| Tool | Config File | Status | Rules |
|------|------------|--------|-------|
| ESLint | `.eslintrc.js` | ✅ Updated | TypeScript + React Native + Custom |
| Prettier | `.prettierrc.js` | ✅ Updated | Standard + Custom |
| EditorConfig | `../.editorconfig` | ✅ Shared | Universal |

---

## 🚀 Usage

### Check Code Quality

```bash
# Frontend Web
cd frontend-web
npm run lint          # Check linting
npm run format:check  # Check formatting

# Frontend Mobile
cd frontend-mobile
npm run lint          # Check linting
npm run format:check  # Check formatting
```

### Auto-Fix Issues

```bash
# Frontend Web
cd frontend-web
npm run lint:fix      # Fix linting issues
npm run format        # Format all files

# Frontend Mobile
cd frontend-mobile
npm run lint:fix      # Fix linting issues
npm run format        # Format all files
```

### Pre-Commit (Recommended)

Install husky for automatic linting before commit:

```bash
# Install husky
npm install -D husky lint-staged

# Setup pre-commit hook
npx husky install
npx husky add .husky/pre-commit "npx lint-staged"
```

Create `.lintstagedrc.json`:
```json
{
  "*.{ts,tsx,js,jsx}": [
    "eslint --fix",
    "prettier --write"
  ],
  "*.{json,css,md}": [
    "prettier --write"
  ]
}
```

---

## 🎯 IDE Integration

### VS Code

**Extensions**:
- ESLint (`dbaeumer.vscode-eslint`)
- Prettier (`esbenp.prettier-vscode`)
- EditorConfig (`editorconfig.editorconfig`)

**Settings** (`.vscode/settings.json`):
```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "[typescript]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  },
  "[typescriptreact]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  }
}
```

### WebStorm / IntelliJ IDEA

1. **ESLint**: Settings → Languages & Frameworks → JavaScript → Code Quality Tools → ESLint
   - ✅ Automatic ESLint configuration
   - ✅ Run eslint --fix on save

2. **Prettier**: Settings → Languages & Frameworks → JavaScript → Prettier
   - ✅ On save
   - ✅ On reformat code

3. **EditorConfig**: Automatically detected

---

## 🧪 Test Results

### Frontend Web Lint Test

```bash
$ cd frontend-web && npm run lint
> eslint .

D:\workspace\repeatwise\frontend-web\src\main.tsx
  6:12  warning  Forbidden non-null assertion  @typescript-eslint/no-non-null-assertion

✖ 1 problem (0 errors, 1 warning)
```

**Status**: ✅ Working (1 expected warning in generated code)

### Frontend Mobile Lint Test

```bash
$ cd frontend-mobile && npm run lint
> eslint .

D:\workspace\repeatwise\frontend-mobile\App.tsx
   8:60  error  Extra semicolon  semi
   9:75  error  Extra semicolon  semi
  ...

✖ 10 problems (10 errors, 0 warnings)
  10 errors and 0 warnings potentially fixable with the `--fix` option.
```

**Status**: ✅ Working (detecting style violations in generated code)

---

## 📝 Style Guide

### Naming Conventions

**TypeScript/JavaScript**:
- Components: `PascalCase` (e.g., `UserProfile.tsx`)
- Functions: `camelCase` (e.g., `getUserData`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `API_BASE_URL`)
- Files: `kebab-case` or `PascalCase` for components

**React Native**:
- Components: `PascalCase` (e.g., `HomeScreen.tsx`)
- Styles: `camelCase` (e.g., `containerStyle`)

### Code Style

**Preferred**:
```typescript
// ✅ Good
const user = await fetchUser(id)
const items = data?.items ?? []
const handleClick = () => console.log('clicked')

// ❌ Avoid
var user = await fetchUser(id);
const items = data && data.items ? data.items : [];
function handleClick() { console.log('clicked'); }
```

**React Hooks**:
```typescript
// ✅ Good - Dependencies listed
useEffect(() => {
  fetchData(id)
}, [id])

// ❌ Avoid - Missing dependencies
useEffect(() => {
  fetchData(id)
}, [])
```

---

## 🔍 Common Issues

### Issue: ESLint not detecting TypeScript errors

**Solution**: Make sure `typescript-eslint` is installed:
```bash
npm install -D @typescript-eslint/parser @typescript-eslint/eslint-plugin
```

### Issue: Prettier conflicts with ESLint

**Solution**: Rules are configured to work together. If conflicts occur:
```bash
npm install -D eslint-config-prettier
```

### Issue: EditorConfig not working

**Solution**: Install EditorConfig plugin for your IDE:
- VS Code: `editorconfig.editorconfig`
- WebStorm: Built-in support

### Issue: Lint on commit not working

**Solution**: Ensure husky hooks are executable:
```bash
chmod +x .husky/pre-commit  # Linux/Mac
```

---

## 📊 Files Summary

### Created/Updated

| File | Location | Purpose | Status |
|------|----------|---------|--------|
| `.prettierrc` | frontend-web/ | Prettier config | ✅ Created |
| `.prettierignore` | frontend-web/ | Prettier ignore | ✅ Created |
| `eslint.config.js` | frontend-web/ | ESLint config | ✅ Updated |
| `.eslintrc.js` | frontend-mobile/ | ESLint config | ✅ Updated |
| `.prettierrc.js` | frontend-mobile/ | Prettier config | ✅ Updated |
| `.prettierignore` | frontend-mobile/ | Prettier ignore | ✅ Created |
| `.editorconfig` | root | Universal config | ✅ Created |

**Total**: 7 files configured

---

## ✅ Benefits

### Code Quality
- ✅ Consistent code style across team
- ✅ Catch errors before commit
- ✅ TypeScript best practices enforced
- ✅ React/React Native best practices

### Developer Experience
- ✅ Auto-format on save
- ✅ Auto-fix common issues
- ✅ IDE integration
- ✅ Fast feedback

### Team Collaboration
- ✅ No style debates
- ✅ Clean diffs in PRs
- ✅ Easy onboarding
- ✅ Consistent codebase

---

## 🎯 Next Steps

### Recommended

1. **Install IDE Extensions**:
   - ESLint
   - Prettier
   - EditorConfig

2. **Configure Pre-Commit Hooks**:
   ```bash
   npm install -D husky lint-staged
   npx husky install
   ```

3. **Run Initial Format**:
   ```bash
   cd frontend-web && npm run format
   cd frontend-mobile && npm run format
   ```

4. **Enable Format on Save** in IDE settings

### Optional

- Add custom ESLint rules for your team
- Configure import order sorting
- Add spell checking
- Set up CI/CD lint checks

---

**Status**: ✅ **Linting & Formatting Complete**
**Date**: October 14, 2025
**Tested**: ✅ Both web and mobile

**Code quality tools are ready to use!** 🎨
