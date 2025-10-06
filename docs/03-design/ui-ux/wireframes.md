# Wireframes Specification

## 1. Overview

Wireframes này định nghĩa giao diện người dùng cho ứng dụng RepeatWise - hệ thống học tập theo phương pháp Spaced Repetition. Ứng dụng được thiết kế cho mobile (iOS/Android) với Flutter framework.

## 2. Design Principles

### 2.1 Core Principles
- **Simplicity**: Giao diện đơn giản, dễ sử dụng
- **Accessibility**: Hỗ trợ người dùng khuyết tật
- **Responsive**: Tương thích với nhiều kích thước màn hình
- **Consistency**: Nhất quán về màu sắc, typography, spacing
- **Performance**: Tối ưu tốc độ load và response

### 2.2 Color Scheme
- **Primary**: #2196F3 (Blue)
- **Secondary**: #FF9800 (Orange)
- **Success**: #4CAF50 (Green)
- **Warning**: #FFC107 (Yellow)
- **Error**: #F44336 (Red)
- **Background**: #FAFAFA (Light Gray)
- **Surface**: #FFFFFF (White)
- **Text Primary**: #212121 (Dark Gray)
- **Text Secondary**: #757575 (Medium Gray)

### 2.3 Typography
- **Heading 1**: Roboto Bold, 24sp
- **Heading 2**: Roboto Medium, 20sp
- **Body**: Roboto Regular, 16sp
- **Caption**: Roboto Regular, 14sp
- **Button**: Roboto Medium, 16sp

## 3. Screen Specifications

### 3.1 Authentication Screens

#### 3.1.1 Welcome Screen
```
┌─────────────────────────────────────┐
│                                     │
│            [Logo]                   │
│         RepeatWise                  │
│                                     │
│    Học tập hiệu quả với             │
│    Spaced Repetition                │
│                                     │
│    [Đăng nhập]                      │
│    [Đăng ký]                        │
│                                     │
│    [Tìm hiểu thêm]                  │
│                                     │
└─────────────────────────────────────┘
```

**Elements**:
- App logo và tên
- Tagline giới thiệu
- Button "Đăng nhập" (Primary)
- Button "Đăng ký" (Secondary)
- Link "Tìm hiểu thêm"

#### 3.1.2 Login Screen
```
┌─────────────────────────────────────┐
│  ← Đăng nhập                        │
│                                     │
│    [Logo]                           │
│                                     │
│  Email                              │
│  ┌─────────────────────────────────┐ │
│  │ user@example.com               │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Mật khẩu                           │
│  ┌─────────────────────────────────┐ │
│  │ •••••••••••••••••••••••••••••• │ │
│  └─────────────────────────────────┘ │
│                                     │
│  [Quên mật khẩu?]                   │
│                                     │
│  [Đăng nhập]                        │
│                                     │
│  Hoặc                                │
│  [Đăng nhập với Google]             │
│                                     │
│  Chưa có tài khoản? [Đăng ký ngay]  │
│                                     │
└─────────────────────────────────────┘
```

**Elements**:
- Header với back button
- Email input field
- Password input field với toggle visibility
- "Quên mật khẩu?" link
- Login button
- Social login option
- Sign up link

#### 3.1.3 Registration Screen
```
┌─────────────────────────────────────┐
│  ← Đăng ký                          │
│                                     │
│    [Logo]                           │
│                                     │
│  Họ và tên                          │
│  ┌─────────────────────────────────┐ │
│  │ Nguyễn Văn A                   │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Email                              │
│  ┌─────────────────────────────────┐ │
│  │ user@example.com               │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Mật khẩu                           │
│  ┌─────────────────────────────────┐ │
│  │ •••••••••••••••••••••••••••••• │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Xác nhận mật khẩu                   │
│  ┌─────────────────────────────────┐ │
│  │ •••••••••••••••••••••••••••••• │ │
│  └─────────────────────────────────┘ │
│                                     │
│  [Tôi đồng ý với Điều khoản sử dụng] │
│                                     │
│  [Đăng ký]                          │
│                                     │
│  Đã có tài khoản? [Đăng nhập]       │
│                                     │
└─────────────────────────────────────┘
```

### 3.2 Main Application Screens

#### 3.2.1 Dashboard (Home Screen)
```
┌─────────────────────────────────────┐
│  [Avatar] RepeatWise    [Settings]  │
│                                     │
│  Xin chào, Nguyễn Văn A!            │
│                                     │
│  ┌─────────────────────────────────┐ │
│  │ Hôm nay cần ôn tập              │ │
│  │                                 │ │
│  │ [Set 1] [Set 2] [Set 3]         │ │
│  │                                 │ │
│  │ [Bắt đầu ôn tập]                │ │
│  └─────────────────────────────────┘ │
│                                     │
│  ┌─────────────────────────────────┐ │
│  │ Thống kê tuần này               │ │
│  │                                 │ │
│  │ Đã hoàn thành: 15/20 set        │ │
│  │ Điểm trung bình: 85%            │ │
│  │                                 │ │
│  │ [Xem chi tiết]                  │ │
│  └─────────────────────────────────┘ │
│                                     │
│  ┌─────────────────────────────────┐ │
│  │ Hành động nhanh                 │ │
│  │                                 │ │
│  │ [Tạo set mới] [Xem tất cả set]  │ │
│  │ [Lịch ôn tập] [Thống kê]        │ │
│  └─────────────────────────────────┘ │
│                                     │
│  [Home] [Sets] [Calendar] [Stats]   │
│                                     │
└─────────────────────────────────────┘
```

**Elements**:
- Header với avatar và settings
- Greeting message
- Today's review section
- Weekly statistics card
- Quick actions
- Bottom navigation

#### 3.2.2 Set List Screen
```
┌─────────────────────────────────────┐
│  Bộ học tập              [+ Tạo mới] │
│                                     │
│  ┌─────────────────────────────────┐ │
│  │ [Search] Tìm kiếm set...        │ │
│  └─────────────────────────────────┘ │
│                                     │
│  ┌─────────────────────────────────┐ │
│  │ 📚 Toán học lớp 10              │ │
│  │ 50 từ • 3/5 chu kỳ • 85%       │ │
│  │ [Edit] [Delete] [Start]         │ │
│  └─────────────────────────────────┘ │
│                                     │
│  ┌─────────────────────────────────┐ │
│  │ 📚 Tiếng Anh cơ bản             │ │
│  │ 100 từ • 1/5 chu kỳ • 92%      │ │
│  │ [Edit] [Delete] [Start]         │ │
│  └─────────────────────────────────┘ │
│                                     │
│  ┌─────────────────────────────────┐ │
│  │ 📚 Lịch sử Việt Nam             │ │
│  │ 30 từ • 5/5 chu kỳ • 78%       │ │
│  │ [Edit] [Delete] [Start]         │ │
│  └─────────────────────────────────┘ │
│                                     │
│  [Home] [Sets] [Calendar] [Stats]   │
│                                     │
└─────────────────────────────────────┘
```

**Elements**:
- Header với search và create button
- Search bar
- Set cards với thông tin:
  - Tên set
  - Số lượng từ
  - Tiến độ chu kỳ
  - Điểm trung bình
  - Action buttons

#### 3.2.3 Create/Edit Set Screen
```
┌─────────────────────────────────────┐
│  ← Tạo set mới                      │
│                                     │
│  Tên set *                          │
│  ┌─────────────────────────────────┐ │
│  │ Toán học lớp 10                 │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Mô tả                              │
│  ┌─────────────────────────────────┐ │
│  │ Các công thức toán học cơ bản   │ │
│  │ cho học sinh lớp 10             │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Số lượng từ *                      │
│  ┌─────────────────────────────────┐ │
│  │ 50                              │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Thời gian nhắc nhở mặc định        │
│  ┌─────────────────────────────────┐ │
│  │ 09:00                           │ │
│  └─────────────────────────────────┘ │
│                                     │
│  [Lưu] [Hủy]                        │
│                                     │
└─────────────────────────────────────┘
```

**Elements**:
- Form fields cho set information
- Required field indicators
- Save/Cancel buttons

#### 3.2.4 Review Session Screen
```
┌─────────────────────────────────────┐
│  Toán học lớp 10        [X]         │
│                                     │
│  ┌─────────────────────────────────┐ │
│  │                                 │ │
│  │        Từ thứ 15/50             │ │
│  │                                 │ │
│  │      Công thức bậc 2            │ │
│  │                                 │ │
│  │    ax² + bx + c = 0             │ │
│  │                                 │ │
│  │    x = (-b ± √b²-4ac)/2a       │ │
│  │                                 │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Bạn nhớ được bao nhiêu?            │
│                                     │
│  [0-20%] [21-40%] [41-60%]         │
│  [61-80%] [81-100%]                │
│                                     │
│  [Bỏ qua] [Tiếp theo]              │
│                                     │
└─────────────────────────────────────┘
```

**Elements**:
- Header với set name và close button
- Progress indicator
- Content display area
- Score selection buttons
- Navigation buttons

#### 3.2.5 Statistics Screen
```
┌─────────────────────────────────────┐
│  Thống kê học tập                   │
│                                     │
│  ┌─────────────────────────────────┐ │
│  │ Tuần này                        │ │
│  │                                 │ │
│  │ Đã hoàn thành: 15/20 set        │ │
│  │ Điểm trung bình: 85%            │ │
│  │ Thời gian học: 2h 30m           │ │
│  └─────────────────────────────────┘ │
│                                     │
│  ┌─────────────────────────────────┐ │
│  │ Biểu đồ tiến độ                 │ │
│  │                                 │ │
│  │ [Chart Area]                    │ │
│  │                                 │ │
│  │ Thứ 2  3  4  5  6  7  CN       │ │
│  └─────────────────────────────────┘ │
│                                     │
│  ┌─────────────────────────────────┐ │
│  │ Set xuất sắc nhất               │ │
│  │                                 │ │
│  │ 📚 Tiếng Anh cơ bản - 92%       │ │
│  │ 📚 Toán học lớp 10 - 85%        │ │
│  │ 📚 Lịch sử Việt Nam - 78%       │ │
│  └─────────────────────────────────┘ │
│                                     │
│  [Home] [Sets] [Calendar] [Stats]   │
│                                     │
└─────────────────────────────────────┘
```

### 3.3 Settings & Profile Screens

#### 3.3.1 Profile Screen
```
┌─────────────────────────────────────┐
│  ← Hồ sơ cá nhân                    │
│                                     │
│        [Avatar]                     │
│        [Thay đổi ảnh]               │
│                                     │
│  Họ và tên                          │
│  ┌─────────────────────────────────┐ │
│  │ Nguyễn Văn A                   │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Email                              │
│  ┌─────────────────────────────────┐ │
│  │ user@example.com               │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Ngôn ngữ                           │
│  ┌─────────────────────────────────┐ │
│  │ Tiếng Việt ▼                   │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Múi giờ                            │
│  ┌─────────────────────────────────┐ │
│  │ GMT+7 (Hà Nội) ▼               │ │
│  └─────────────────────────────────┘ │
│                                     │
│  [Lưu thay đổi]                     │
│                                     │
└─────────────────────────────────────┘
```

#### 3.3.2 Settings Screen
```
┌─────────────────────────────────────┐
│  ← Cài đặt                          │
│                                     │
│  Thông báo                          │
│  ┌─────────────────────────────────┐ │
│  │ Push notification    [ON]       │ │
│  │ Email reminder       [OFF]      │ │
│  │ Sound alert          [ON]       │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Học tập                            │
│  ┌─────────────────────────────────┐ │
│  │ Tự động bắt đầu chu kỳ [ON]     │ │
│  │ Hiển thị gợi ý        [ON]      │ │
│  │ Chế độ tối          [OFF]       │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Dữ liệu                            │
│  ┌─────────────────────────────────┐ │
│  │ [Xuất dữ liệu]                  │ │
│  │ [Sao lưu]                       │ │
│  │ [Khôi phục]                     │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Tài khoản                          │
│  ┌─────────────────────────────────┐ │
│  │ [Đổi mật khẩu]                  │ │
│  │ [Đăng xuất]                     │ │
│  │ [Xóa tài khoản]                 │ │
│  └─────────────────────────────────┘ │
│                                     │
└─────────────────────────────────────┘
```

### 3.4 Notification Management Screens

#### 3.4.1 Reschedule Reminder Modal
```
┌─────────────────────────────────────┐
│  Đổi lịch nhắc nhở        [X]      │
│                                     │
│  Set: Toán học lớp 10               │
│  Lần ôn: 3/5                        │
│                                     │
│  Thời gian mới:                     │
│  ┌─────────────────────────────────┐ │
│  │ 15:30                          │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Ngày:                              │
│  ┌─────────────────────────────────┐ │
│  │ 25/12/2024                     │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Lý do thay đổi:                    │
│  ┌─────────────────────────────────┐ │
│  │ Bận việc                        │ │
│  └─────────────────────────────────┘ │
│                                     │
│  [Hủy] [Lưu thay đổi]              │
└─────────────────────────────────────┘
```

**Elements**:
- Modal header với close button
- Set information display
- Time picker cho thời gian mới
- Date picker cho ngày mới
- Reason dropdown
- Action buttons

#### 3.4.2 Notification Preferences Screen
```
┌─────────────────────────────────────┐
│  ← Thông báo                        │
│                                     │
│  Push Notifications                 │
│  ┌─────────────────────────────────┐ │
│  │ Nhắc nhở ôn tập    [ON]        │ │
│  │ Hoàn thành set     [ON]        │ │
│  │ Thành tích mới     [OFF]       │ │
│  │ Cập nhật hệ thống  [ON]        │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Email Notifications                │
│  ┌─────────────────────────────────┐ │
│  │ Báo cáo tuần      [OFF]        │ │
│  │ Nhắc nhở dài hạn  [ON]         │ │
│  │ Thông báo bảo mật [ON]         │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Thời gian nhắc nhở                 │
│  ┌─────────────────────────────────┐ │
│  │ Giờ mặc định: 09:00             │ │
│  │ Tần suất: Hàng ngày             │ │
│  │ Thời gian tĩnh: 22:00 - 08:00   │ │
│  └─────────────────────────────────┘ │
│                                     │
│  [Lưu cài đặt]                      │
└─────────────────────────────────────┘
```

**Elements**:
- Toggle switches cho từng loại notification
- Time settings
- Frequency settings
- Quiet hours settings
- Save button

### 3.5 Data Management Screens

#### 3.5.1 Export Data Screen
```
┌─────────────────────────────────────┐
│  ← Xuất dữ liệu                     │
│                                     │
│  Loại dữ liệu:                      │
│  ○ Tất cả dữ liệu                   │
│  ● Chỉ set và tiến trình            │
│  ○ Chỉ thống kê học tập             │
│  ○ Chỉ lịch sử ôn tập               │
│                                     │
│  Định dạng:                         │
│  ○ JSON (đầy đủ)                   │
│  ● CSV (dễ đọc)                    │
│  ○ Excel (bảng tính)               │
│  ○ PDF (báo cáo)                   │
│                                     │
│  Thời gian:                         │
│  ○ Tất cả dữ liệu                   │
│  ● 30 ngày gần nhất                 │
│  ○ 3 tháng gần nhất                 │
│  ○ Tùy chỉnh                       │
│                                     │
│  Tùy chọn:                          │
│  ┌─────────────────────────────────┐ │
│  │ ☑ Bao gồm metadata             │ │
│  │ ☑ Nén file                     │ │
│  │ ☐ Mã hóa file                  │ │
│  │ ☑ Tự động xóa sau 7 ngày       │ │
│  └─────────────────────────────────┘ │
│                                     │
│  [Xuất dữ liệu]                     │
└─────────────────────────────────────┘
```

**Elements**:
- Radio buttons cho data type
- Format selection
- Date range selection
- Checkboxes cho options
- Export button

#### 3.5.2 Import Data Screen
```
┌─────────────────────────────────────┐
│  ← Nhập dữ liệu                     │
│                                     │
│  Chọn file:                         │
│  ┌─────────────────────────────────┐ │
│  │                                 │ │
│  │     📁 Chọn file để nhập        │ │
│  │                                 │ │
│  │     Hỗ trợ: JSON, CSV, Excel    │ │
│  │                                 │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Tùy chọn nhập:                     │
│  ┌─────────────────────────────────┐ │
│  │ ☑ Ghi đè dữ liệu cũ            │ │
│  │ ☐ Chỉ thêm dữ liệu mới         │ │
│  │ ☑ Validate dữ liệu trước nhập   │ │
│  │ ☐ Backup trước khi nhập         │ │
│  └─────────────────────────────────┘ │
│                                     │
│  [Nhập dữ liệu]                     │
└─────────────────────────────────────┘
```

**Elements**:
- File upload area
- Import options checkboxes
- Import button

#### 3.5.3 Backup Configuration Screen
```
┌─────────────────────────────────────┐
│  ← Sao lưu dữ liệu                  │
│                                     │
│  Loại backup:                       │
│  ● Manual backup (ngay lập tức)     │
│  ○ Scheduled backup (tự động)       │
│  ○ Incremental backup (chỉ thay đổi)│
│  ○ Full backup (toàn bộ dữ liệu)    │
│                                     │
│  Phạm vi backup:                    │
│  ● Tất cả dữ liệu                   │
│  ○ Chỉ sets và tiến trình           │
│  ○ Chỉ learning history             │
│  ○ Chỉ user preferences             │
│                                     │
│  Vị trí lưu trữ:                    │
│  ● Cloud storage (Google Drive)     │
│  ○ Local device                     │
│  ○ Email backup                     │
│  ○ External storage                 │
│                                     │
│  Tùy chọn:                          │
│  ┌─────────────────────────────────┐ │
│  │ ☑ Mã hóa backup                │ │
│  │ ☑ Nén dữ liệu                  │ │
│  │ ☐ Tự động xóa backup cũ        │ │
│  │ ☑ Thông báo khi hoàn thành     │ │
│  └─────────────────────────────────┘ │
│                                     │
│  [Tạo backup]                       │
└─────────────────────────────────────┘
```

**Elements**:
- Backup type selection
- Scope selection
- Storage location selection
- Options checkboxes
- Create backup button

#### 3.5.4 Restore Data Screen
```
┌─────────────────────────────────────┐
│  ← Khôi phục dữ liệu                │
│                                     │
│  Chọn backup:                       │
│  ┌─────────────────────────────────┐ │
│  │ 📁 backup_2024_12_25.zip       │ │
│  │ 25/12/2024 • 15:30 • 2.5MB     │ │
│  │ ☑ Chọn                          │ │
│  └─────────────────────────────────┘ │
│                                     │
│  ┌─────────────────────────────────┐ │
│  │ 📁 backup_2024_12_20.zip       │ │
│  │ 20/12/2024 • 09:15 • 2.3MB     │ │
│  │ ○ Chọn                          │ │
│  └─────────────────────────────────┘ │
│                                     │
│  Tùy chọn khôi phục:                │
│  ┌─────────────────────────────────┐ │
│  │ ☑ Backup dữ liệu hiện tại      │ │
│  │ ☑ Validate backup trước khôi phục│ │
│  │ ☐ Chỉ khôi phục sets           │ │
│  │ ☐ Chỉ khôi phục preferences    │ │
│  └─────────────────────────────────┘ │
│                                     │
│  [Khôi phục dữ liệu]                │
└─────────────────────────────────────┘
```

**Elements**:
- Backup file list
- File selection radio buttons
- Restore options checkboxes
- Restore button

#### 3.5.5 Backup History Screen
```
┌─────────────────────────────────────┐
│  ← Lịch sử sao lưu                  │
│                                     │
│  ┌─────────────────────────────────┐ │
│  │ 📁 backup_2024_12_25.zip       │ │
│  │ 25/12/2024 • 15:30 • 2.5MB     │ │
│  │ ✅ Thành công                   │ │
│  │ [Khôi phục] [Xóa] [Tải xuống]   │ │
│  └─────────────────────────────────┘ │
│                                     │
│  ┌─────────────────────────────────┐ │
│  │ 📁 backup_2024_12_20.zip       │ │
│  │ 20/12/2024 • 09:15 • 2.3MB     │ │
│  │ ✅ Thành công                   │ │
│  │ [Khôi phục] [Xóa] [Tải xuống]   │ │
│  └─────────────────────────────────┘ │
│                                     │
│  ┌─────────────────────────────────┐ │
│  │ 📁 backup_2024_12_15.zip       │ │
│  │ 15/12/2024 • 14:20 • 2.1MB     │ │
│  │ ❌ Thất bại                     │ │
│  │ [Xóa] [Thử lại]                 │ │
│  └─────────────────────────────────┘ │
│                                     │
│  [Tạo backup mới]                   │
└─────────────────────────────────────┘
```

**Elements**:
- Backup history list
- Status indicators
- Action buttons for each backup
- Create new backup button

### 3.6 Loading & Error States

#### 3.6.1 Loading Screen
```
┌─────────────────────────────────────┐
│                                     │
│                                     │
│            ⏳                        │
│                                     │
│        Đang tải dữ liệu...          │
│                                     │
│                                     │
└─────────────────────────────────────┘
```

**Elements**:
- Loading spinner
- Loading message
- Progress indicator (optional)

#### 3.6.2 Error Screen
```
┌─────────────────────────────────────┐
│                                     │
│            ❌                        │
│                                     │
│        Đã xảy ra lỗi                │
│                                     │
│  Không thể kết nối đến máy chủ.     │
│  Vui lòng kiểm tra kết nối mạng     │
│  và thử lại.                        │
│                                     │
│  [Thử lại] [Báo cáo lỗi]            │
│                                     │
└─────────────────────────────────────┘
```

**Elements**:
- Error icon
- Error title
- Error description
- Action buttons

#### 3.6.3 Empty State Screen
```
┌─────────────────────────────────────┐
│                                     │
│            📚                        │
│                                     │
│        Chưa có set nào              │
│                                     │
│  Bắt đầu tạo set đầu tiên để        │
│  bắt đầu học tập.                   │
│                                     │
│  [Tạo set mới]                      │
│                                     │
└─────────────────────────────────────┘
```

**Elements**:
- Empty state icon
- Empty state message
- Call-to-action button

### 3.7 Modal & Dialog Components

#### 3.7.1 Confirmation Dialog
```
┌─────────────────────────────────────┐
│  Xác nhận xóa set        [X]        │
│                                     │
│  Bạn có chắc chắn muốn xóa set      │
│  "Toán học lớp 10"?                 │
│                                     │
│  Hành động này không thể hoàn tác.  │
│                                     │
│  [Hủy] [Xóa set]                    │
└─────────────────────────────────────┘
```

**Elements**:
- Modal header với title và close button
- Confirmation message
- Warning text
- Action buttons

#### 3.7.2 Success Dialog
```
┌─────────────────────────────────────┐
│  Thành công!            [X]         │
│                                     │
│            ✅                        │
│                                     │
│  Set đã được tạo thành công.        │
│                                     │
│  [Tiếp tục]                         │
└─────────────────────────────────────┘
```

**Elements**:
- Success icon
- Success message
- Continue button

#### 3.7.3 Input Dialog
```
┌─────────────────────────────────────┐
│  Đổi tên set            [X]         │
│                                     │
│  Tên mới:                           │
│  ┌─────────────────────────────────┐ │
│  │ Toán học lớp 10 nâng cao       │ │
│  └─────────────────────────────────┘ │
│                                     │
│  [Hủy] [Lưu]                        │
└─────────────────────────────────────┘
```

**Elements**:
- Input field
- Action buttons

#### 3.7.4 Bottom Sheet
```
┌─────────────────────────────────────┐
│  ────────────────────────────────── │
│                                     │
│  Tùy chọn set                       │
│                                     │
│  [✏️ Chỉnh sửa]                     │
│  [📊 Xem thống kê]                  │
│  [⏸️ Tạm dừng]                      │
│  [🗑️ Xóa]                           │
│  [📤 Xuất]                          │
│                                     │
│  [Hủy]                              │
└─────────────────────────────────────┘
```

**Elements**:
- Drag handle
- Option list
- Cancel button

## 4. User Flows

### 4.1 Onboarding Flow
1. Welcome Screen → Login/Register
2. Registration → Email Verification
3. Login → Dashboard
4. First-time user → Tutorial screens

### 4.2 Learning Flow
1. Dashboard → Select Set
2. Set Details → Start Review
3. Review Session → Score Input
4. Score Input → Next Question/Complete
5. Complete → Dashboard

### 4.3 Set Management Flow
1. Set List → Create New Set
2. Create Set → Set Details
3. Set Details → Edit/Delete
4. Edit → Save Changes

## 5. Responsive Design

### 5.1 Breakpoints
- **Mobile**: 320px - 768px
- **Tablet**: 768px - 1024px
- **Desktop**: 1024px+

### 5.2 Adaptive Elements
- Flexible grid layouts
- Scalable typography
- Touch-friendly buttons (min 44px)
- Swipe gestures for navigation

## 6. Accessibility

### 6.1 WCAG 2.1 Compliance
- **Color Contrast**: Minimum 4.5:1 ratio
- **Touch Targets**: Minimum 44x44px
- **Screen Reader**: Proper labels and descriptions
- **Keyboard Navigation**: Full keyboard support

### 6.2 Features
- VoiceOver/TalkBack support
- High contrast mode
- Large text support
- Reduced motion preferences

## 7. Animation & Transitions

### 7.1 Micro-interactions
- Button press feedback
- Loading states
- Success/error animations
- Smooth page transitions

### 7.2 Performance
- 60fps animations
- Hardware acceleration
- Optimized asset loading
- Minimal reflows

## 8. Implementation Guidelines

### 8.1 Flutter Widgets
- Use Material Design 3 components
- Implement custom widgets for consistency
- Follow Flutter best practices
- Optimize for performance

### 8.2 State Management
- Use Provider/Riverpod for state
- Implement proper error handling
- Cache frequently accessed data
- Optimize rebuilds

### 8.3 Testing
- Widget tests for UI components
- Integration tests for user flows
- Accessibility testing
- Performance testing

## 9. Future Enhancements

### 9.1 Planned Features
- Dark mode support
- Custom themes
- Advanced animations
- Offline mode
- Social features

### 9.2 Scalability
- Modular component architecture
- Theme system
- Internationalization support
- Plugin architecture 
