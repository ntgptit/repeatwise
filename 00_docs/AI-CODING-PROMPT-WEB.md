# AI Coding Prompt - Frontend Web (React + TypeScript)

## 🎯 Mục tiêu

Implement **RepeatWise Web App** - React application cho flashcard learning với SRS.

**Tech Stack**: React 18, TypeScript 5.x, Vite, TanStack Query v5, React Router v6, Tailwind CSS + Shadcn/ui

---

## 📚 Tài liệu bắt buộc đọc (Thứ tự ưu tiên)

### 1️⃣ Coding Convention ⭐ ĐỌC ĐẦU TIÊN
[Web Coding Convention](../docs/05-quality/coding-convention-web.md) - **BẮT BUỘC TUÂN THỦ 100%**

**Key Rules**: Component ≤ 30 lines, NO viết tắt, Early Return, dùng Lodash-es + date-fns, TypeScript strict (NO `any`), Functional components only

### 2️⃣ API Integration ⭐ HIỂU API
[API Endpoints Summary](../docs/03-design/api/api-endpoints-summary.md) - Endpoints, Request/Response, JWT auth

### 3️⃣ Frontend Architecture ⭐ HIỂU KIẾN TRÚC
[Frontend Architecture](../docs/03-design/architecture/frontend-architecture.md) - Component structure, TanStack Query, Context API, Zustand, token refresh

### 4️⃣ Frontend Specs ⭐ DETAIL DESIGN
[Frontend Web Specs](../docs/04-detail-design/07-frontend-web-specs.md) - 20+ components, state management, form validation, API integration

### 5️⃣ UI/UX Design
[Web Wireframes](../docs/04-detail-design/09-wireframes-web.md) - 25+ screens layouts, responsive design, dark mode

---

## ✅ Coding Checklist

### Trước khi code
- [ ] Đọc Web Coding Convention
- [ ] Đọc API spec và Frontend Web Specs
- [ ] Xem wireframes cho UI layout

### Khi viết code
- [ ] Component ≤ 30 lines
- [ ] TypeScript strict mode, NO `any`
- [ ] Props interface rõ ràng
- [ ] Dùng TanStack Query cho API calls
- [ ] Error handling với try-catch
- [ ] Loading & error states
- [ ] Dùng i18next cho text
- [ ] Tailwind CSS classes, dùng `cn()` utility
- [ ] Responsive design (mobile-first)
- [ ] Accessibility (keyboard navigation, ARIA labels)

### Testing
- [ ] Component tests với React Testing Library
- [ ] Hook tests, Integration tests

---

**Version**: 1.0 | **Last Updated**: 2025-01-10
