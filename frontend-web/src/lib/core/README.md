# Theme System

Hệ thống theme cho dự án RepeatWise sử dụng Tailwind CSS và next-themes.

## Cấu trúc

```
src/lib/core/
├── theme.ts          # Định nghĩa màu sắc và tokens
├── useTheme.ts       # Custom hook để quản lý theme
├── ThemeProvider.tsx # Enhanced Theme Provider component
└── index.ts          # Exports

src/index.css         # CSS variables cho light/dark theme
tailwind.config.js    # Cấu hình Tailwind với theme tokens
```

## Sử dụng

### 1. Sử dụng Theme Hook

```tsx
import { useTheme } from '@/lib/core'

function MyComponent() {
  const { theme, setTheme, resolvedTheme } = useTheme()
  
  return (
    <button onClick={() => setTheme(theme === 'dark' ? 'light' : 'dark')}>
      Current theme: {resolvedTheme}
    </button>
  )
}
```

### 2. Sử dụng Theme Tokens

```tsx
import { themeTokens } from '@/lib/core'

const primaryColor = themeTokens.colors.light.primary
```

### 3. Sử dụng Tailwind Classes

```tsx
<div className="bg-background text-foreground">
  <button className="bg-primary text-primary-foreground">
    Primary Button
  </button>
</div>
```

## Theme Colors

### Light Theme
- Background: Trắng
- Primary: Xanh dương (#3b82f6)
- Secondary: Xám nhạt
- Destructive: Đỏ

### Dark Theme
- Background: Xám đen
- Primary: Xanh dương sáng hơn
- Secondary: Xám đậm
- Destructive: Đỏ đậm

## Thêm Colors Mới

1. Thêm CSS variable vào `src/index.css`:
```css
:root {
  --new-color: 200 50% 50%;
}
.dark {
  --new-color: 200 50% 70%;
}
```

2. Thêm vào `tailwind.config.js`:
```js
colors: {
  newColor: 'hsl(var(--new-color))',
}
```

3. Sử dụng:
```tsx
<div className="bg-newColor">...</div>
```

## Theme Modes

- `light`: Chế độ sáng
- `dark`: Chế độ tối
- `system`: Tự động theo hệ thống

## Best Practices

1. Luôn sử dụng semantic colors (primary, secondary, etc.) thay vì hardcode màu
2. Sử dụng foreground colors cho text trên các màu nền
3. Test cả light và dark mode
4. Đảm bảo contrast ratio đủ để accessibility

