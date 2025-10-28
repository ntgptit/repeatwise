# AI Coding Prompt - Frontend Mobile (React Native)

## 🎯 Mục tiêu

Implement **RepeatWise Mobile App** - React Native application cho flashcard learning với SRS.

**Tech Stack**: React Native 0.73+, TypeScript 5.x, React Navigation v6, TanStack Query v5, React Native Paper

---

## 📚 Tài liệu bắt buộc đọc (Thứ tự ưu tiên)

### 1️⃣ Coding Convention ⭐ ĐỌC ĐẦU TIÊN
[Mobile Coding Convention](../docs/05-quality/coding-convention-mobile.md) - **BẮT BUỘC TUÂN THỦ 100%**

**Key Rules**: Component ≤ 30 lines, NO viết tắt, NO inline styles, FlatList (NOT ScrollView + map), TypeScript strict (NO `any`)

### 2️⃣ API Integration ⭐ HIỂU API
[API Endpoints Summary](../docs/03-design/api/api-endpoints-summary.md) - Endpoints, Request/Response, JWT auth

### 3️⃣ Frontend Architecture ⭐ HIỂU KIẾN TRÚC
[Frontend Architecture](../docs/03-design/architecture/frontend-architecture.md) - Component structure, TanStack Query, Context API, token refresh

### 4️⃣ Frontend Mobile Specs ⭐ DETAIL DESIGN
[Frontend Mobile Specs](../docs/04-detail-design/08-frontend-mobile-specs.md) - 15 screens, navigation, platform-specific features, gestures

### 5️⃣ UI/UX Design
[Mobile Wireframes](../docs/04-detail-design/10-wireframes-mobile.md) - 40+ screens layouts, gestures, animations

---

## ✅ Coding Checklist

### Trước khi code
- [ ] Đọc Mobile Coding Convention
- [ ] Đọc API spec và Frontend Mobile Specs
- [ ] Xem wireframes cho UI layout

### Khi viết code
- [ ] Screen ≤ 30 lines
- [ ] TypeScript strict mode, NO `any`
- [ ] StyleSheet.create (NO inline styles)
- [ ] FlatList cho lists (NOT ScrollView + map)
- [ ] Platform-specific code (`Platform.select()`)
- [ ] SafeAreaView cho screens
- [ ] Type-safe navigation
- [ ] i18n-js cho text
- [ ] Performance optimizations (memo, useMemo, useCallback)

### Testing
- [ ] Component tests với React Native Testing Library
- [ ] Tested on both iOS and Android

---

**Version**: 1.0 | **Last Updated**: 2025-01-10
