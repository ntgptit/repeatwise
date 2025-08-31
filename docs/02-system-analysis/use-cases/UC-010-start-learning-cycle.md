# UC-010: Start Learning Cycle

## 1. Use Case Information

**Use Case ID**: UC-010
**Use Case Name**: Start Learning Cycle
**Primary Actor**: Student (Người học)
**Secondary Actors**: None
**Priority**: High
**Complexity**: Medium

## 2. Brief Description

Student bắt đầu chu kỳ học tập cho set, hệ thống tạo chu kỳ mới với 5 lần ôn tập và lên lịch reminder theo thuật toán SRS. Hệ thống cập nhật trạng thái set và tạo lịch nhắc nhở.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Set tồn tại và thuộc về user
- Set có trạng thái 'not_started' hoặc đã hoàn thành chu kỳ trước
- User có quyền bắt đầu học set

## 4. Main Flow

### Step 1: Access Set Management
**Actor Action**: Student chọn set cần bắt đầu học từ danh sách
**System Response**: Hiển thị trang chi tiết set với thông tin hiện tại

### Step 2: View Set Information
**Actor Action**: Student xem thông tin set trước khi bắt đầu
**System Response**: Hiển thị:
- Tên set và mô tả
- Category và số từ vựng
- Trạng thái hiện tại
- Lịch sử học tập (nếu có)
- Thông tin chu kỳ trước (nếu có)

### Step 3: Initiate Learning Cycle
**Actor Action**: Student chọn "Bắt đầu học" hoặc "Bắt đầu chu kỳ mới"
**System Response**:
- Validate set có thể bắt đầu học
- Kiểm tra quyền của user
- Hiển thị confirmation dialog

### Step 4: Confirm Start Learning
**Actor Action**: Student xác nhận bắt đầu chu kỳ học tập
**System Response**:
- Tạo chu kỳ mới với current_cycle + 1
- Set status = 'learning'
- Tạo 5 reminder schedules cho chu kỳ

### Step 5: Create Reminder Schedule
**Actor Action**: System tự động
**System Response**:
- Tính toán thời gian cho 5 lần ôn tập
- Tạo reminder schedules với status 'pending'
- Áp dụng thuật toán SRS cho timing
- Kiểm tra overload prevention

### Step 6: Update Set Status
**Actor Action**: System tự động
**System Response**:
- Cập nhật set status = 'learning'
- Cập nhật current_cycle
- Cập nhật updated_at timestamp
- Lưu vào activity_logs

### Step 7: Complete Cycle Start
**Actor Action**: System tự động
**System Response**:
- Hiển thị thông báo thành công
- Hiển thị thông tin chu kỳ mới
- Hiển thị lịch nhắc nhở
- Chuyển user đến review session đầu tiên

## 5. Alternative Flows

### A1: Set Already in Learning
**Trigger**: Set đang trong chu kỳ học tập
**Steps**:
1. System hiển thị thông báo: "Set đang trong chu kỳ học tập"
2. System hiển thị thông tin chu kỳ hiện tại
3. Student có thể:
   - Tiếp tục chu kỳ hiện tại
   - Hoàn thành chu kỳ trước
4. Return to Step 2

### A2: Set Not Found
**Trigger**: Set không tồn tại hoặc đã bị xóa
**Steps**:
1. System hiển thị thông báo: "Set không tồn tại"
2. System chuyển user về danh sách set
3. Student có thể chọn set khác
4. Return to Step 1

### A3: Permission Denied
**Trigger**: User không có quyền bắt đầu học set
**Steps**:
1. System hiển thị thông báo: "Bạn không có quyền bắt đầu học set này"
2. System chuyển user về danh sách set
3. Student có thể chọn set khác
4. Return to Step 1

### A4: Overload Prevention
**Trigger**: User đã có quá nhiều set đang học (> 10)
**Steps**:
1. System hiển thị warning: "Bạn đã có nhiều set đang học"
2. System hiển thị danh sách set đang học
3. Student có thể:
   - Xác nhận thêm set mới
   - Hoàn thành set cũ trước
4. Continue to Step 4

### A5: Reminder Schedule Conflict
**Trigger**: Có xung đột lịch nhắc nhở
**Steps**:
1. System hiển thị thông báo: "Có xung đột lịch nhắc nhở"
2. System hiển thị các reminder trùng lặp
3. System tự động reschedule theo priority
4. Continue to Step 5

### A6: Network Connection Error
**Trigger**: Mất kết nối internet
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System lưu start request locally
3. Student có thể thử lại khi có kết nối
4. Return to Step 1

### A7: User Cancels Start
**Trigger**: Student hủy quá trình bắt đầu
**Steps**:
1. Student chọn "Hủy" trong confirmation dialog
2. System đóng dialog
3. Student quay lại trang chi tiết set
4. Return to Step 2

### A8: First Time Learning
**Trigger**: Set chưa từng được học
**Steps**:
1. System hiển thị welcome message
2. System hiển thị hướng dẫn học tập
3. System tạo chu kỳ đầu tiên (cycle = 1)
4. Continue to Step 4

## 6. Post Conditions

### Success Post Conditions
- Chu kỳ học tập mới được tạo thành công
- Set status được cập nhật thành 'learning'
- 5 reminder schedules được tạo
- User có thể bắt đầu ôn tập

### Failure Post Conditions
- Chu kỳ không được tạo
- Set status không thay đổi
- Error message được hiển thị
- User có thể thử lại hoặc hủy

## 7. Business Rules

### BR-001: Set Management
- User chỉ có thể bắt đầu học set của mình
- Set phải tồn tại và không bị xóa
- Set phải có trạng thái phù hợp để bắt đầu

### BR-004: Learning Cycle Structure
- Mỗi chu kỳ có đúng 5 lần ôn tập
- Thời gian giữa các lần ôn cố định cho chu kỳ đầu
- Chu kỳ mới bắt đầu sau khi hoàn thành chu kỳ trước

### BR-006: Reminder Scheduling
- Tạo 5 reminder cho mỗi chu kỳ
- Áp dụng thuật toán SRS cho timing
- Kiểm tra overload prevention (max 3 set/ngày)
- Priority: oldest overdue > lower avg_score > lower word_count

### BR-007: Cycle Progression
- Chu kỳ đầu tiên: cycle = 1
- Chu kỳ tiếp theo: cycle = previous_cycle + 1
- Set status: not_started → learning → reviewing → mastered

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- updated_at = now() khi bắt đầu chu kỳ
- Lưu lịch sử bắt đầu chu kỳ vào activity_logs

## 8. Data Requirements

### Input Data
- **Set ID** (UUID, required)
- **User ID** (UUID, required)

### Output Data
- **Cycle Status** (success/error)
- **New Cycle Info** (cycle number, start date)
- **Reminder Schedules** (5 reminders created)
- **Error Messages** (if any)
- **Activity Log Entry** (who, when, what)

## 9. Non-Functional Requirements

### Performance
- Cycle start < 3 giây
- Reminder creation < 2 giây
- Status update < 1 giây

### Security
- User chỉ có thể bắt đầu học set của mình
- Validate set ownership
- Log all cycle start activities

### Usability
- Clear confirmation dialog
- Progress indicators
- Success feedback
- Error handling

## 10. Acceptance Criteria

### AC-001: Successful Cycle Start
**Given** user có set hợp lệ
**When** user bắt đầu chu kỳ học tập
**Then** chu kỳ mới được tạo thành công
**And** set status được cập nhật thành 'learning'
**And** 5 reminder schedules được tạo

### AC-002: Set Already in Learning
**Given** set đang trong chu kỳ học tập
**When** user thử bắt đầu chu kỳ mới
**Then** system hiển thị thông báo set đang học
**And** user được hướng dẫn tiếp tục chu kỳ hiện tại

### AC-003: First Time Learning
**Given** set chưa từng được học
**When** user bắt đầu học lần đầu
**Then** chu kỳ đầu tiên được tạo (cycle = 1)
**And** welcome message được hiển thị

### AC-004: Overload Prevention
**Given** user đã có nhiều set đang học
**When** user thử bắt đầu set mới
**Then** system hiển thị warning về overload
**And** user có thể xác nhận hoặc hủy

### AC-005: Reminder Schedule Creation
**Given** chu kỳ được tạo thành công
**When** system tạo reminder schedules
**Then** 5 reminder được tạo với timing chính xác
**And** overload prevention được áp dụng

## 11. Test Cases

### TC-001: Valid Cycle Start
**Test Data**: Valid set with status 'not_started'
**Expected Result**: Cycle created, status updated, reminders scheduled

### TC-002: Set Already Learning
**Test Data**: Set with status 'learning'
**Expected Result**: Cannot start message, continue current cycle

### TC-003: First Time Learning
**Test Data**: New set without learning history
**Expected Result**: First cycle created, welcome message shown

### TC-004: Overload Prevention
**Test Data**: User with 10+ active sets
**Expected Result**: Warning shown, user can confirm or cancel

### TC-005: Reminder Conflict
**Test Data**: Set with conflicting reminder schedules
**Expected Result**: Conflict resolved, reminders rescheduled

### TC-006: Network Error
**Test Data**: Simulate network timeout
**Expected Result**: Network error message, retry option

## 12. Related Use Cases

- **UC-005**: Create New Set
- **UC-009**: View Set Details
- **UC-011**: Perform Review Session
- **UC-014**: Complete Cycle
- **UC-015**: Receive Reminder

## 13. Notes

### Implementation Notes
- Validate set status trước khi cho phép bắt đầu
- Implement SRS algorithm cho reminder timing
- Check overload prevention rules
- Handle reminder conflicts automatically

### Future Enhancements
- Adaptive learning paths
- Personalized reminder timing
- Learning difficulty adjustment
- Social learning features

### UI/UX Considerations
- Clear progress indicators
- Motivational messages
- Easy navigation to first review
- Success celebrations
