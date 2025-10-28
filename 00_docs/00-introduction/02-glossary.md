# Glossary - RepeatWise

## Thuật ngữ chung

### A

**Access Token**
- JWT token ngắn hạn (15 phút) để xác thực API requests
- Được lưu trong memory (không persistent)

**Async Operation**
- Thao tác chạy background không block UI
- Ví dụ: Copy folder với > 50 items

### B

**Box System**
- Hệ thống 7 ô để quản lý SRS
- Mỗi ô có interval khác nhau (1, 3, 7, 14, 30, 60, 120 ngày)

**Breadcrumb**
- Navigation path hiển thị vị trí hiện tại trong folder tree
- Ví dụ: Home > English > IELTS > Vocabulary

### C

**Card (Flashcard)**
- Thẻ học với 2 mặt: Front (câu hỏi) và Back (câu trả lời)
- Đơn vị cơ bản trong hệ thống học tập

**Composite Pattern**
- Design pattern cho cấu trúc cây folders
- Folder có thể chứa sub-folders và decks

**Cram Mode**
- Chế độ học nhanh không theo SRS schedule
- Không ảnh hưởng đến due date

### D

**Deck**
- Bộ thẻ học, chứa nhiều flashcards cùng chủ đề
- Có thể thuộc folder hoặc standalone

**Depth**
- Cấp độ của folder trong tree (root = 0)
- Giới hạn tối đa: 10 levels

**Due Date**
- Ngày cần ôn tập card theo SRS schedule
- Được tính tự động dựa trên box position

### E

**Ease Factor**
- Hệ số độ dễ (default 2.5)
- Được điều chỉnh dựa trên rating của user

### F

**Folder**
- Thư mục tổ chức decks và sub-folders
- Có thể lồng nhau không giới hạn (max 10 levels)

**Forgotten Card Action**
- Hành động khi user rate "Again" (quên card)
- Options: Move to Box 1, Move down N boxes, Stay in box

### I

**Interval**
- Khoảng thời gian giữa 2 lần review
- Được tính dựa trên box position và rating

### J

**JWT (JSON Web Token)**
- Token format để authentication
- Gồm header, payload, signature

### L

**Lapse**
- Khi user quên card (rate "Again")
- Lapse count được tracking để phân tích

### M

**Materialized Path**
- Đường dẫn đầy đủ của folder được lưu trong database
- Format: /parent_id/child_id/grandchild_id
- Giúp query nhanh descendants

**MVP (Minimum Viable Product)**
- Phiên bản sản phẩm với tính năng cốt lõi
- Focus: Simple, maintainable, extensible

### R

**Rating**
- Đánh giá của user sau khi review card
- 4 options: Again (<1min), Hard (<6min), Good (next interval), Easy (4x interval)

**Refresh Token**
- Token dài hạn (7 ngày) để refresh access token
- Được lưu trong HTTP-only cookie
- One-time use, rotate sau mỗi lần refresh

**Review**
- Quá trình ôn tập flashcard
- Gồm: xem front, suy nghĩ, lật xem back, đánh giá

**Review Order**
- Thứ tự review cards: Ascending, Descending, Random
- User có thể cấu hình trong settings

### S

**Soft Delete**
- Xóa logic bằng cách set deleted_at timestamp
- Cho phép restore data
- Hard delete sau 30 ngày

**SRS (Spaced Repetition System)**
- Hệ thống lặp lại giãn cách dựa trên khoa học
- Giúp ghi nhớ lâu dài hiệu quả

**Strategy Pattern**
- Design pattern cho review order và forgotten card action
- Dễ mở rộng thêm strategies mới

**Streak**
- Số ngày học liên tục không gián đoạn
- Reset về 0 nếu bỏ qua 1 ngày

### T

**Tree View**
- Giao diện hiển thị folder structure dạng cây
- Có expand/collapse nodes

### V

**Visitor Pattern**
- Design pattern để traverse folder tree
- Dùng để tính toán statistics recursive

## Thuật ngữ kỹ thuật

### Backend

**Controller**
- Layer xử lý HTTP requests
- Validate input, gọi service, return response

**Service**
- Business logic layer
- Interface + Implementation pattern

**Repository**
- Data access layer
- Spring Data JPA repositories

**Entity**
- JPA entity mapping với database table
- Annotations: @Entity, @Table, @Column

**DTO (Data Transfer Object)**
- Object truyền data giữa layers
- Request DTO và Response DTO

**Mapper**
- Convert giữa Entity và DTO
- Sử dụng MapStruct

### Frontend

**Context API**
- React context cho global state
- Dùng cho Auth và Settings

**TanStack Query (React Query)**
- Library quản lý server state
- Auto caching, refetch, sync

**Zustand**
- Lightweight state management
- Dùng cho UI state (sidebar, theme)

**Shadcn/ui**
- Component library cho React
- Built với Tailwind CSS

### Database

**Index**
- Cấu trúc giúp tăng tốc query
- Quan trọng: composite index cho review queries

**Foreign Key (FK)**
- Ràng buộc tham chiếu giữa tables
- Đảm bảo data integrity

**Composite Primary Key**
- Primary key gồm nhiều columns
- Ví dụ: (folder_id, user_id) trong folder_stats

**Denormalization**
- Lưu duplicate data để optimize read
- Ví dụ: folder_stats table cache

## Abbreviations

- **API**: Application Programming Interface
- **CRUD**: Create, Read, Update, Delete
- **CSV**: Comma-Separated Values
- **FK**: Foreign Key
- **HTTP**: Hypertext Transfer Protocol
- **HTTPS**: HTTP Secure
- **JPA**: Java Persistence API
- **JSON**: JavaScript Object Notation
- **JWT**: JSON Web Token
- **ORM**: Object-Relational Mapping
- **PK**: Primary Key
- **REST**: Representational State Transfer
- **SPA**: Single Page Application
- **SQL**: Structured Query Language
- **UI**: User Interface
- **UUID**: Universally Unique Identifier
- **UX**: User Experience
