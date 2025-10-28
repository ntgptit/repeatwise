# Overview - RepeatWise

## Giới thiệu dự án

**RepeatWise** là một ứng dụng học tập cá nhân sử dụng phương pháp Spaced Repetition System (SRS) dạng Box System. Ứng dụng giúp người dùng tạo flashcards, tổ chức theo cấu trúc folders phân cấp, và tự động lên lịch ôn tập dựa trên thuật toán khoa học.

## Phạm vi dự án

**Phiên bản**: MVP (Minimum Viable Product) - Personal Use

**Mục tiêu**: Xây dựng các chức năng cốt lõi, dễ maintain và mở rộng trong tương lai.

## Công nghệ sử dụng

### Backend
- Java 17
- Spring Boot 3
- PostgreSQL
- Spring Data JPA
- JWT Authentication với Refresh Token

### Frontend Web
- React TypeScript
- Tailwind CSS + Shadcn/ui
- TanStack Query (React Query)
- Zustand

### Frontend Mobile
- React Native
- React Native Paper
- TanStack Query

## Kiến trúc hệ thống

- **Pattern**: Layered Architecture (Controller → Service → Repository)
- **API**: RESTful
- **Design Patterns**: Composite, Strategy, Repository, DTO, Visitor

## Đặc điểm nổi bật

1. **Cấu trúc Folder phân cấp không giới hạn** (tối đa 10 cấp)
2. **Thuật toán SRS 7-box system** với interval cố định
3. **Import/Export hàng loạt** từ CSV/Excel
4. **Copy/Move operations** với async processing cho large datasets
5. **Multi-mode study**: Standard SRS, Cram, Random
6. **Theme support**: Light/Dark mode
7. **Multilingual**: Vietnamese, English

## Tài liệu tham khảo

- [Vision Document](./01-vision.md)
- [Glossary](./02-glossary.md)
- [System Context Diagram](./03-system-context-diagram.md)
- [Stakeholders](./04-stakeholders.md)
