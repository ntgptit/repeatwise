# System Specification

## 1. System Scope

### 1.1 In Scope
- **Set Management**: Tạo, chỉnh sửa, xóa, xem danh sách set học tập
- **Cycle Management**: Quản lý chu kỳ học 5 lần ôn tập
- **Score Management**: Nhập và lưu trữ điểm số (0-100%)
- **Reminder System**: Tự động tạo và gửi nhắc nhở ôn tập
- **Overload Prevention**: Giới hạn 3 set/ngày, tự động reschedule
- **Statistics & Analytics**: Thống kê tiến trình học tập
- **User Authentication**: Đăng ký, đăng nhập, quản lý profile
- **Mobile Application**: Flutter app cho iOS và Android
- **Backend API**: Spring Boot REST API
- **Database**: PostgreSQL với schema chuẩn

### 1.2 Out of Scope
- **Flashcard Management**: Không quản lý từng flashcard riêng lẻ
- **Content Creation**: Không tạo nội dung học tập
- **Social Features**: Không có tính năng chia sẻ, thi đua
- **Payment Processing**: Không có tính năng thanh toán
- **Multi-language Support**: Chỉ hỗ trợ tiếng Việt và tiếng Anh
- **Offline Mode**: Yêu cầu kết nối internet
- **Advanced Analytics**: Không có AI/ML phân tích nâng cao

## 2. Actors

### 2.1 Primary Actors

#### Student (Người học)
**Description**: Người dùng chính sử dụng ứng dụng để học tập
**Responsibilities**:
- Tạo và quản lý set học tập
- Thực hiện ôn tập theo lịch
- Nhập điểm số sau mỗi lần ôn
- Xem thống kê tiến trình
- Reschedule reminder khi cần

**Preconditions**:
- Đã đăng ký tài khoản
- Đã cài đặt mobile app
- Có kết nối internet

### 2.2 Secondary Actors

#### System Administrator
**Description**: Quản trị viên hệ thống
**Responsibilities**:
- Quản lý user accounts
- Monitor system performance
- Backup và restore database
- Cấu hình system parameters

#### Notification Service
**Description**: Dịch vụ gửi notification
**Responsibilities**:
- Gửi push notification
- Gửi email reminder
- Xử lý delivery status

## 3. Use Case List

### 3.1 User Management
- **UC-001**: User Registration
- **UC-002**: User Login
- **UC-003**: User Profile Management
- **UC-004**: Password Reset

### 3.2 Set Management
- **UC-005**: Create New Set
- **UC-006**: Edit Set Information
- **UC-007**: Delete Set
- **UC-008**: View Set List
- **UC-009**: View Set Details

### 3.3 Learning Cycle
- **UC-010**: Start Learning Cycle
- **UC-011**: Perform Review Session
- **UC-012**: Input Score
- **UC-013**: Skip Review Session
- **UC-014**: Complete Cycle

### 3.4 Reminder Management
- **UC-015**: Receive Reminder
- **UC-016**: Reschedule Reminder
- **UC-017**: Mark Reminder as Done
- **UC-018**: Skip Reminder

### 3.5 Statistics & Analytics
- **UC-019**: View Learning Statistics
- **UC-020**: View Set Progress
- **UC-021**: View Performance Trends
- **UC-022**: Export Learning Data

### 3.6 System Administration
- **UC-023**: Manage System Configuration
- **UC-024**: Monitor System Health
- **UC-025**: Backup Database

## 4. Detailed Use Cases

### UC-001: User Registration

**Primary Actor**: Student
**Goal**: Tạo tài khoản mới để sử dụng ứng dụng

**Preconditions**:
- User chưa có tài khoản
- Mobile app đã được cài đặt

**Main Flow**:
1. User mở app và chọn "Đăng ký"
2. User nhập email address
3. User nhập password (8-20 ký tự)
4. User nhập confirm password
5. User nhập họ tên
6. User chọn ngôn ngữ ưa thích (VI/EN)
7. User chọn timezone
8. User chọn thời gian nhắc nhở mặc định
9. User nhấn "Đăng ký"
10. System validate thông tin
11. System tạo user account
12. System gửi email xác nhận
13. System chuyển user đến màn hình chính

**Alternative Flows**:
- **A1**: Email đã tồn tại
  - System hiển thị thông báo lỗi
  - User có thể thử email khác hoặc đăng nhập
- **A2**: Password không đủ mạnh
  - System hiển thị yêu cầu password
  - User nhập password mới
- **A3**: Confirm password không khớp
  - System hiển thị thông báo lỗi
  - User nhập lại confirm password

**Post Conditions**:
- User account được tạo thành công
- User có thể đăng nhập vào hệ thống
- Email xác nhận được gửi

**Business Rules**: BR-020, BR-021

### UC-005: Create New Set

**Primary Actor**: Student
**Goal**: Tạo set học tập mới với thông tin cơ bản

**Preconditions**:
- User đã đăng nhập
- User có quyền tạo set

**Main Flow**:
1. User chọn "Tạo set mới"
2. User nhập tên set (≤ 100 ký tự)
3. User nhập mô tả (≤ 500 ký tự, optional)
4. User chọn category (vocabulary, grammar, mixed, other)
5. User nhập số từ vựng (> 0)
6. User nhấn "Tạo set"
7. System validate thông tin
8. System tạo set với UUID
9. System set status = "not_started"
10. System set current_cycle = 1
11. System hiển thị thông báo thành công
12. System chuyển đến màn hình set details

**Alternative Flows**:
- **A1**: Tên set trống hoặc quá dài
  - System hiển thị thông báo lỗi
  - User nhập lại tên set
- **A2**: Số từ vựng ≤ 0
  - System hiển thị thông báo lỗi
  - User nhập lại số từ vựng
- **A3**: Category không hợp lệ
  - System hiển thị thông báo lỗi
  - User chọn lại category

**Post Conditions**:
- Set mới được tạo thành công
- Set có trạng thái "not_started"
- User có thể bắt đầu học set

**Business Rules**: BR-001, BR-015, BR-016

### UC-011: Perform Review Session

**Primary Actor**: Student
**Goal**: Thực hiện lần ôn tập và nhập điểm số

**Preconditions**:
- User đã đăng nhập
- Có set cần ôn tập trong ngày
- Reminder đã được gửi

**Main Flow**:
1. User nhận notification về lần ôn
2. User mở app và chọn set cần ôn
3. System hiển thị thông tin set
4. User xem lại nội dung học tập
5. User tự đánh giá mức độ nhớ
6. User chọn điểm số (0-100%)
7. User nhập ghi chú (optional)
8. User nhấn "Lưu điểm"
9. System validate điểm số
10. System lưu vào review_histories
11. System cập nhật trạng thái reminder = "done"
12. System kiểm tra nếu đã đủ 5 lần ôn
13. System tính avg_score nếu hoàn thành chu kỳ
14. System hiển thị thông báo hoàn thành

**Alternative Flows**:
- **A1**: User chọn "Skip"
  - User chọn lý do: forgot, busy, other
  - System lưu trạng thái = "skipped"
  - System cập nhật reminder status = "skipped"
- **A2**: Điểm số không hợp lệ
  - System hiển thị thông báo lỗi
  - User nhập lại điểm số
- **A3**: Hoàn thành chu kỳ (5 lần ôn)
  - System tính delay chu kỳ mới
  - System tạo chu kỳ mới
  - System cập nhật current_cycle

**Post Conditions**:
- Điểm số được lưu thành công
- Lịch sử ôn tập được cập nhật
- Reminder status được cập nhật
- Chu kỳ mới được tạo nếu cần

**Business Rules**: BR-004, BR-007, BR-008, BR-009

## 5. Data Input/Output

### 5.1 User Registration
**Input**:
- Email (string, required, email format)
- Password (string, required, 8-20 chars)
- Confirm Password (string, required, match password)
- Full Name (string, required, ≤ 100 chars)
- Preferred Language (enum: VI, EN)
- Timezone (string, required)
- Default Reminder Time (time, required)

**Output**:
- User ID (UUID)
- Registration Status (success/error)
- Error Messages (if any)

### 5.2 Create Set
**Input**:
- Set Name (string, required, ≤ 100 chars)
- Description (string, optional, ≤ 500 chars)
- Category (enum: vocabulary, grammar, mixed, other)
- Word Count (integer, required, > 0)

**Output**:
- Set ID (UUID)
- Creation Status (success/error)
- Set Details (name, description, category, word_count, status)

### 5.3 Input Score
**Input**:
- Set ID (UUID, required)
- Cycle Number (integer, required)
- Review Number (integer, required, 1-5)
- Score (integer, required, 0-100)
- Note (string, optional, ≤ 500 chars)
- Skip Reason (enum: forgot, busy, other, optional)

**Output**:
- Review ID (UUID)
- Save Status (success/error)
- Updated Set Status (if cycle completed)
- Next Reminder Date (if applicable)

## 6. Validation Rules

### 6.1 User Input Validation
- **Email**: Phải đúng format email, không trùng lặp
- **Password**: 8-20 ký tự, có ít nhất 1 chữ hoa, 1 chữ thường, 1 số
- **Name**: Không được trống, ≤ 100 ký tự
- **Set Name**: Không được trống, ≤ 100 ký tự
- **Word Count**: Phải là số nguyên > 0
- **Score**: Phải là số nguyên từ 0-100

### 6.2 Business Rule Validation
- **Set Creation**: User chỉ có thể tạo set cho chính mình
- **Score Input**: Chỉ có thể nhập điểm cho set của mình
- **Reminder Access**: Chỉ có thể xem reminder của mình
- **Cycle Management**: Không thể skip quá 2 lần liên tiếp

## 7. Error Handling

### 7.1 Validation Errors
- **400 Bad Request**: Input không hợp lệ
- **401 Unauthorized**: Chưa đăng nhập hoặc token hết hạn
- **403 Forbidden**: Không có quyền truy cập
- **404 Not Found**: Resource không tồn tại
- **409 Conflict**: Dữ liệu xung đột (email trùng lặp)

### 7.2 System Errors
- **500 Internal Server Error**: Lỗi hệ thống
- **503 Service Unavailable**: Dịch vụ không khả dụng
- **Database Connection Error**: Lỗi kết nối database
- **Notification Service Error**: Lỗi gửi notification

## 8. Acceptance Criteria

### 8.1 Functional Requirements

#### FR-001: User Registration
**Given** user chưa có tài khoản
**When** user nhập thông tin đăng ký hợp lệ
**Then** tài khoản được tạo thành công
**And** email xác nhận được gửi
**And** user có thể đăng nhập

#### FR-002: Set Creation
**Given** user đã đăng nhập
**When** user tạo set với thông tin hợp lệ
**Then** set được tạo với status "not_started"
**And** set có UUID duy nhất
**And** user có thể bắt đầu học set

#### FR-003: Score Input
**Given** user có set cần ôn tập
**When** user nhập điểm số hợp lệ
**Then** điểm được lưu vào database
**And** lịch sử ôn tập được cập nhật
**And** reminder status được cập nhật

#### FR-004: Overload Prevention
**Given** user có > 3 set cần ôn trong ngày
**When** system tạo reminder
**Then** chỉ 3 set được ưu tiên
**And** các set còn lại được reschedule
**And** thứ tự ưu tiên được áp dụng

### 8.2 Non-Functional Requirements

#### NFR-001: Performance
**Given** user thực hiện thao tác
**When** system xử lý request
**Then** response time < 2 giây
**And** system hỗ trợ 1000 concurrent users

#### NFR-002: Security
**Given** user truy cập hệ thống
**When** user thực hiện thao tác
**Then** dữ liệu được mã hóa
**And** user chỉ access dữ liệu của mình
**And** authentication được validate

#### NFR-003: Availability
**Given** system đang hoạt động
**When** có lỗi xảy ra
**Then** system có thể recover
**And** dữ liệu không bị mất
**And** uptime > 99.5%

## 9. System Constraints

### 9.1 Technical Constraints
- **Mobile Platform**: iOS 12+, Android 8+
- **Backend**: Java 17, Spring Boot 3
- **Database**: PostgreSQL 14+
- **API**: RESTful với JSON format
- **Authentication**: JWT tokens

### 9.2 Business Constraints
- **Daily Limit**: Tối đa 3 set/ngày/user
- **Score Range**: 0-100%
- **Cycle Structure**: 5 lần ôn/chu kỳ
- **Delay Range**: 7-90 ngày
- **Reschedule Limit**: 2 lần/reminder

### 9.3 Regulatory Constraints
- **Data Privacy**: Tuân thủ GDPR/CCPA
- **Data Retention**: Lưu trữ tối thiểu 2 năm
- **User Consent**: Yêu cầu consent cho data processing
- **Right to be Forgotten**: Hỗ trợ xóa dữ liệu user 
