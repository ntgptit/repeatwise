# UC-014: Pause Learning Set

## 1. Use Case Information

**Use Case ID**: UC-014
**Use Case Name**: Pause Learning Set
**Primary Actor**: Student (Người học)
**Secondary Actors**: Notification Service, Scheduling Service
**Priority**: Medium
**Complexity**: Low

## 2. Brief Description

Student tạm dừng việc học một set để tập trung vào set khác hoặc nghỉ ngơi, hệ thống sẽ dừng tất cả reminder và lưu trạng thái hiện tại.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Student có ít nhất 1 set đang active
- Set đang ở trạng thái learning hoặc reviewing
- Có kết nối internet

## 4. Main Flow

### Step 1: Access Set Management
**Actor Action**: Student mở màn hình quản lý set
**System Response**: Hiển thị danh sách set với trạng thái hiện tại

### Step 2: Select Set to Pause
**Actor Action**: Student chọn set muốn tạm dừng
**System Response**: Hiển thị thông tin chi tiết set và các action có thể thực hiện

### Step 3: Choose Pause Action
**Actor Action**: Student chọn "Tạm dừng" từ menu action
**System Response**: Hiển thị dialog xác nhận pause với các tùy chọn

### Step 4: Select Pause Duration
**Actor Action**: Student chọn thời gian tạm dừng:
- 1 ngày
- 3 ngày
- 1 tuần
- 2 tuần
- Vô thời hạn
**System Response**: Hiển thị preview pause period

### Step 5: Enter Pause Reason
**Actor Action**: Student nhập lý do tạm dừng (optional):
- Tập trung set khác
- Cần nghỉ ngơi
- Quá khó
- Lý do khác
**System Response**: Validate và hiển thị preview

### Step 6: Confirm Pause Action
**Actor Action**: Student nhấn "Xác nhận tạm dừng"
**System Response**: 
- Validate pause request
- Hiển thị loading indicator

### Step 7: Update Set Status
**Actor Action**: System tự động
**System Response**:
- Cập nhật set status = 'paused'
- Lưu pause reason và duration
- Dừng tất cả reminder pending
- Lưu trạng thái hiện tại

### Step 8: Update Scheduling
**Actor Action**: System tự động
**System Response**:
- Cập nhật next_review_date = pause_end_date
- Log pause action
- Cập nhật daily schedule

### Step 9: Send Confirmation
**Actor Action**: System tự động
**System Response**:
- Gửi notification xác nhận pause
- Hiển thị thông báo thành công
- Cập nhật UI

## 5. Alternative Flows

### A1: Cancel Pause Action
**Trigger**: Student hủy bỏ pause action
**Steps**:
1. System đóng dialog pause
2. System quay lại màn hình set details
3. Student có thể chọn action khác
4. Return to Step 1

### A2: Network Connection Error
**Trigger**: Mất kết nối khi submit pause
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System lưu pause request locally
3. Student có thể:
   - Thử lại khi có kết nối
   - Hủy bỏ pause action
4. Return to Step 1

### A3: Pause All Sets
**Trigger**: Student chọn pause tất cả set
**Steps**:
1. System hiển thị cảnh báo: "Bạn sẽ tạm dừng tất cả set"
2. System yêu cầu xác nhận đặc biệt
3. System pause tất cả set cùng lúc
4. Continue to Step 7

### A4: Pause During Active Review
**Trigger**: Student pause set đang trong phiên ôn tập
**Steps**:
1. System hiển thị cảnh báo: "Set đang trong phiên ôn tập"
2. System đề xuất hoàn thành ôn tập trước
3. Student có thể:
   - Hoàn thành ôn tập trước
   - Pause ngay lập tức
4. Continue to Step 7

### A5: Pause with Custom Duration
**Trigger**: Student chọn "Vô thời hạn"
**Steps**:
1. System hiển thị text input cho custom duration
2. Student nhập số ngày (tối đa 90 ngày)
3. System validate và lưu custom duration
4. Continue to Step 7

## 6. Post Conditions

### Success Post Conditions
- Set status được cập nhật thành 'paused'
- Tất cả reminder pending được dừng
- Pause reason và duration được lưu
- Next review date được cập nhật
- Notification xác nhận được gửi
- UI được cập nhật

### Failure Post Conditions
- Set status không thay đổi
- Reminder không bị ảnh hưởng
- Error message được hiển thị
- Student có thể thử lại

## 7. Business Rules

### BR-036: Pause Management
- Student có thể pause tối đa 5 set cùng lúc
- Pause duration tối đa 90 ngày
- Pause không làm mất tiến trình học
- Set có thể resume bất cứ lúc nào

### BR-037: Pause Scheduling
- Pause dừng tất cả reminder pending
- Next review date = pause_end_date + 1 ngày
- Pause không ảnh hưởng đến set khác
- Daily limit vẫn được áp dụng

### BR-038: Pause Analytics
- Log tất cả pause actions
- Track pause patterns để cải thiện UX
- Alert khi pause quá nhiều set
- Đề xuất cải thiện dựa trên pause data

### BR-015: UUID Usage
- Tất cả ID sử dụng UUID format
- UUID được generate trước khi insert

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- created_at = now() khi tạo mới

## 8. Data Requirements

### Input Data
- **Pause Duration** (integer, required, 1-90 days)
- **Pause Reason** (enum, optional)
- **Custom Reason** (string, optional, ≤ 200 chars)
- **Pause Date** (datetime, required)
- **Set ID** (UUID, required)

### Output Data
- **Pause ID** (UUID)
- **Pause End Date** (datetime)
- **Pause Status** (success/error)
- **Updated Set Status** (object)

## 9. Non-Functional Requirements

### Performance
- Pause action < 3 giây
- Reminder update < 2 giây
- Notification delivery < 5 giây

### Security
- Validate user ownership của set
- Log all pause actions
- Prevent pause manipulation

### Usability
- Clear pause confirmation dialog
- Easy duration selection
- Immediate feedback
- Pause preview

## 10. Acceptance Criteria

### AC-001: Successful Pause
**Given** student có set đang active
**When** student chọn pause với duration hợp lệ
**Then** set được pause thành công
**And** reminder được dừng

### AC-002: Pause with Custom Reason
**Given** student chọn "Lý do khác"
**When** student nhập lý do custom
**Then** custom reason được lưu
**And** pause được thực hiện

### AC-003: Pause All Sets
**Given** student có nhiều set active
**When** student chọn pause tất cả
**Then** tất cả set được pause
**And** confirmation được yêu cầu

### AC-004: Pause During Review
**Given** set đang trong phiên ôn tập
**When** student chọn pause
**Then** warning được hiển thị
**And** pause vẫn được cho phép

### AC-005: Pause Limit Reached
**Given** đã pause 5 set
**When** student cố gắng pause set thứ 6
**Then** error message được hiển thị
**And** pause không được thực hiện

## 11. Test Cases

### TC-001: Normal Pause
**Test Data**: Valid duration, normal conditions
**Expected Result**: Set paused, reminders stopped

### TC-002: Pause with Custom Reason
**Test Data**: Custom reason = "Quá khó"
**Expected Result**: Custom reason saved, pause completed

### TC-003: Pause All Sets
**Test Data**: 3 active sets
**Expected Result**: All sets paused, confirmation required

### TC-004: Pause During Review
**Test Data**: Set in active review session
**Expected Result**: Warning displayed, pause allowed

### TC-005: Pause Limit Reached
**Test Data**: 6th set pause attempt
**Expected Result**: Error message, pause denied

## 12. Related Use Cases

- **UC-015**: Resume Learning Set
- **UC-016**: Reschedule Reminder
- **UC-019**: View Learning Statistics
- **UC-020**: Manage Learning Preferences

## 13. Notes

### Implementation Notes
- Implement pause duration enum
- Add pause analytics tracking
- Implement reminder cancellation
- Add pause pattern analysis

### Future Enhancements
- Smart pause suggestions
- Pause impact analysis
- Adaptive pause limits
- Pause recovery recommendations
