# UC-015: Resume Learning Set

## 1. Use Case Information

**Use Case ID**: UC-015
**Use Case Name**: Resume Learning Set
**Primary Actor**: Student (Người học)
**Secondary Actors**: Notification Service, Scheduling Service
**Priority**: Medium
**Complexity**: Low

## 2. Brief Description

Student tiếp tục học tập một set đã bị tạm dừng, hệ thống sẽ khôi phục trạng thái học tập và tạo lịch ôn mới.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Student có ít nhất 1 set đang ở trạng thái paused
- Có kết nối internet

## 4. Main Flow

### Step 1: Access Set Management
**Actor Action**: Student mở màn hình quản lý set
**System Response**: Hiển thị danh sách set với trạng thái hiện tại

### Step 2: Select Paused Set
**Actor Action**: Student chọn set đang paused muốn tiếp tục
**System Response**: Hiển thị thông tin chi tiết set và trạng thái paused

### Step 3: Choose Resume Action
**Actor Action**: Student chọn "Tiếp tục học" từ menu action
**System Response**: Hiển thị dialog xác nhận resume với thông tin set

### Step 4: Review Set Information
**Actor Action**: System tự động
**System Response**:
- Hiển thị thông tin set (tên, mô tả, số từ)
- Hiển thị tiến trình trước khi pause
- Hiển thị thời gian đã pause

### Step 5: Confirm Resume Action
**Actor Action**: Student nhấn "Xác nhận tiếp tục"
**System Response**: 
- Validate resume request
- Hiển thị loading indicator

### Step 6: Restore Set Status
**Actor Action**: System tự động
**System Response**:
- Cập nhật set status = 'learning' hoặc 'reviewing'
- Khôi phục trạng thái chu kỳ học
- Tính toán next_review_date mới

### Step 7: Create New Schedule
**Actor Action**: System tự động
**System Response**:
- Tạo reminder mới cho lần ôn tiếp theo
- Cập nhật daily schedule
- Log resume action

### Step 8: Send Confirmation
**Actor Action**: System tự động
**System Response**:
- Gửi notification xác nhận resume
- Hiển thị thông báo thành công
- Cập nhật UI

## 5. Alternative Flows

### A1: Cancel Resume Action
**Trigger**: Student hủy bỏ resume action
**Steps**:
1. System đóng dialog resume
2. System quay lại màn hình set details
3. Student có thể chọn action khác
4. Return to Step 1

### A2: Network Connection Error
**Trigger**: Mất kết nối khi submit resume
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System lưu resume request locally
3. Student có thể:
   - Thử lại khi có kết nối
   - Hủy bỏ resume action
4. Return to Step 1

### A3: Resume Multiple Sets
**Trigger**: Student chọn resume nhiều set cùng lúc
**Steps**:
1. System hiển thị danh sách set có thể resume
2. Student chọn các set muốn resume
3. System resume tất cả set được chọn
4. Continue to Step 6

### A4: Resume with Overdue Sets
**Trigger**: Resume set đã quá hạn ôn tập
**Steps**:
1. System hiển thị cảnh báo: "Set đã quá hạn ôn tập"
2. System đề xuất ôn tập ngay lập tức
3. Student có thể:
   - Ôn tập ngay
   - Resume bình thường
4. Continue to Step 6

### A5: Resume During Peak Hours
**Trigger**: Resume vào giờ cao điểm (tối đa 3 set/ngày)
**Steps**:
1. System thông báo có thể ảnh hưởng lịch học
2. System đề xuất thời gian thay thế
3. Student có thể chọn thời gian khác
4. Continue to Step 6

## 6. Post Conditions

### Success Post Conditions
- Set status được cập nhật thành 'learning' hoặc 'reviewing'
- Trạng thái chu kỳ học được khôi phục
- Reminder mới được tạo
- Next review date được cập nhật
- Notification xác nhận được gửi
- UI được cập nhật

### Failure Post Conditions
- Set status không thay đổi
- Trạng thái pause được giữ nguyên
- Error message được hiển thị
- Student có thể thử lại

## 7. Business Rules

### BR-039: Resume Management
- Student có thể resume bất cứ set nào đang paused
- Resume khôi phục đầy đủ trạng thái trước khi pause
- Resume không ảnh hưởng đến set khác
- Set có thể resume ngay lập tức

### BR-040: Resume Scheduling
- Next review date = ngày hiện tại + 1 ngày
- Resume tạo reminder mới cho lần ôn tiếp theo
- Daily limit vẫn được áp dụng
- Ưu tiên set quá hạn khi resume

### BR-041: Resume Analytics
- Log tất cả resume actions
- Track resume patterns để cải thiện UX
- Alert khi resume quá nhiều set cùng lúc
- Đề xuất cải thiện dựa trên resume data

### BR-015: UUID Usage
- Tất cả ID sử dụng UUID format
- UUID được generate trước khi insert

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- created_at = now() khi tạo mới

## 8. Data Requirements

### Input Data
- **Resume Date** (datetime, required)
- **Set ID** (UUID, required)
- **Resume Reason** (string, optional, ≤ 200 chars)

### Output Data
- **Resume ID** (UUID)
- **Next Review Date** (datetime)
- **Resume Status** (success/error)
- **Updated Set Status** (object)

## 9. Non-Functional Requirements

### Performance
- Resume action < 3 giây
- Schedule creation < 2 giây
- Notification delivery < 5 giây

### Security
- Validate user ownership của set
- Log all resume actions
- Prevent resume manipulation

### Usability
- Clear resume confirmation dialog
- Set information preview
- Immediate feedback
- Resume preview

## 10. Acceptance Criteria

### AC-001: Successful Resume
**Given** student có set đang paused
**When** student chọn resume
**Then** set được resume thành công
**And** reminder mới được tạo

### AC-002: Resume Multiple Sets
**Given** student có nhiều set paused
**When** student chọn resume nhiều set
**Then** tất cả set được resume
**And** schedule được cập nhật

### AC-003: Resume Overdue Set
**Given** set đã quá hạn ôn tập
**When** student resume set
**Then** warning được hiển thị
**And** resume vẫn được thực hiện

### AC-004: Resume During Peak Hours
**Given** đã đạt giới hạn 3 set/ngày
**When** student resume set
**Then** thông báo conflict được hiển thị
**And** alternative time được đề xuất

### AC-005: Resume with Custom Reason
**Given** student nhập lý do resume
**When** student submit resume
**Then** custom reason được lưu
**And** resume được thực hiện

## 11. Test Cases

### TC-001: Normal Resume
**Test Data**: Valid paused set, normal conditions
**Expected Result**: Set resumed, new reminder created

### TC-002: Resume Multiple Sets
**Test Data**: 3 paused sets
**Expected Result**: All sets resumed, schedules updated

### TC-003: Resume Overdue Set
**Test Data**: Set overdue by 5 days
**Expected Result**: Warning displayed, resume completed

### TC-004: Resume During Peak Hours
**Test Data**: 4th set resume of the day
**Expected Result**: Conflict notification, alternative suggested

### TC-005: Network Error
**Test Data**: Simulate network timeout
**Expected Result**: Error message, retry option

## 12. Related Use Cases

- **UC-014**: Pause Learning Set
- **UC-016**: Reschedule Reminder
- **UC-019**: View Learning Statistics
- **UC-020**: Manage Learning Preferences

## 13. Notes

### Implementation Notes
- Implement resume validation logic
- Add resume analytics tracking
- Implement schedule conflict resolution
- Add resume pattern analysis

### Future Enhancements
- Smart resume suggestions
- Resume impact analysis
- Adaptive resume timing
- Resume recovery recommendations
