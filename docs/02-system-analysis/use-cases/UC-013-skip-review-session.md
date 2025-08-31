# UC-013: Skip Review Session

## 1. Use Case Information

**Use Case ID**: UC-013
**Use Case Name**: Skip Review Session
**Primary Actor**: Student (Người học)
**Secondary Actors**: Notification Service, Scheduling Service
**Priority**: Medium
**Complexity**: Low

## 2. Brief Description

Student bỏ qua một phiên ôn tập đã được lên lịch và hệ thống reschedule lịch ôn cho ngày khác với lý do skip.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Student có reminder pending cho set
- Set đang ở trạng thái learning hoặc reviewing
- Có kết nối internet

## 4. Main Flow

### Step 1: Access Review Reminder
**Actor Action**: Student mở app và thấy reminder cho phiên ôn tập
**System Response**: Hiển thị thông tin set và lịch ôn

### Step 2: Choose Skip Option
**Actor Action**: Student chọn "Bỏ qua" thay vì "Bắt đầu ôn"
**System Response**: Hiển thị dialog xác nhận skip với các tùy chọn

### Step 3: Select Skip Reason
**Actor Action**: Student chọn lý do skip:
- Bận việc khác
- Không có thời gian
- Cần nghỉ ngơi
- Lý do khác
**System Response**: Hiển thị preview reschedule date

### Step 4: Confirm Skip Action
**Actor Action**: Student nhấn "Xác nhận bỏ qua"
**System Response**: 
- Validate skip request
- Hiển thị loading indicator

### Step 5: Update Reminder Status
**Actor Action**: System tự động
**System Response**:
- Cập nhật reminder status = 'skipped'
- Lưu skip reason và timestamp
- Tính toán ngày reschedule mới

### Step 6: Reschedule Review
**Actor Action**: System tự động
**System Response**:
- Tạo reminder mới cho ngày tiếp theo
- Cập nhật next_review_date
- Log skip action

### Step 7: Send Confirmation
**Actor Action**: System tự động
**System Response**:
- Gửi notification xác nhận skip
- Hiển thị thông báo thành công
- Cập nhật UI

## 5. Alternative Flows

### A1: Cancel Skip Action
**Trigger**: Student hủy bỏ skip action
**Steps**:
1. System đóng dialog skip
2. System quay lại màn hình reminder
3. Student có thể chọn action khác
4. Return to Step 1

### A2: Network Connection Error
**Trigger**: Mất kết nối khi submit skip
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System lưu skip request locally
3. Student có thể:
   - Thử lại khi có kết nối
   - Hủy bỏ skip action
4. Return to Step 1

### A3: Maximum Skip Limit Reached
**Trigger**: Đã skip quá 3 lần liên tiếp
**Steps**:
1. System hiển thị cảnh báo: "Bạn đã bỏ qua nhiều lần"
2. System đề xuất giảm độ khó hoặc tạm dừng set
3. System vẫn cho phép skip nhưng ghi nhận warning
4. Continue to Step 5

### A4: Skip During Peak Hours
**Trigger**: Skip vào giờ cao điểm (tối đa 3 set/ngày)
**Steps**:
1. System thông báo có thể ảnh hưởng lịch học
2. System đề xuất thời gian thay thế
3. Student có thể chọn thời gian khác
4. Continue to Step 5

### A5: Skip with Custom Reason
**Trigger**: Student chọn "Lý do khác"
**Steps**:
1. System hiển thị text input cho custom reason
2. Student nhập lý do (tối đa 200 ký tự)
3. System validate và lưu custom reason
4. Continue to Step 5

## 6. Post Conditions

### Success Post Conditions
- Reminder status được cập nhật thành 'skipped'
- Lịch ôn mới được tạo cho ngày tiếp theo
- Skip reason được lưu vào database
- Notification xác nhận được gửi
- UI được cập nhật

### Failure Post Conditions
- Reminder status không thay đổi
- Lịch ôn không được reschedule
- Error message được hiển thị
- Student có thể thử lại

## 7. Business Rules

### BR-033: Skip Management
- Student có thể skip tối đa 3 lần liên tiếp
- Skip không ảnh hưởng đến cycle progress
- Reschedule date = ngày hiện tại + 1 ngày
- Skip reason bắt buộc phải chọn

### BR-034: Reschedule Logic
- Skip không làm mất chu kỳ học
- Next review date không vượt quá 7 ngày
- Ưu tiên reschedule vào ngày ít set nhất
- Tránh conflict với set khác

### BR-035: Skip Analytics
- Log tất cả skip actions
- Track skip patterns để cải thiện UX
- Alert khi skip quá nhiều
- Đề xuất cải thiện dựa trên skip data

### BR-015: UUID Usage
- Tất cả ID sử dụng UUID format
- UUID được generate trước khi insert

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- created_at = now() khi tạo mới

## 8. Data Requirements

### Input Data
- **Skip Reason** (enum, required)
- **Custom Reason** (string, optional, ≤ 200 chars)
- **Skip Date** (datetime, required)
- **Set ID** (UUID, required)

### Output Data
- **Skip ID** (UUID)
- **Reschedule Date** (datetime)
- **Skip Status** (success/error)
- **Updated Reminder** (object)

## 9. Non-Functional Requirements

### Performance
- Skip action < 2 giây
- Reschedule calculation < 1 giây
- Notification delivery < 5 giây

### Security
- Validate user ownership của set
- Log all skip actions
- Prevent skip manipulation

### Usability
- Clear skip confirmation dialog
- Easy reason selection
- Immediate feedback
- Reschedule preview

## 10. Acceptance Criteria

### AC-001: Successful Skip
**Given** student có reminder pending
**When** student chọn skip với lý do hợp lệ
**Then** reminder được skip thành công
**And** lịch ôn mới được tạo

### AC-002: Skip with Custom Reason
**Given** student chọn "Lý do khác"
**When** student nhập lý do custom
**Then** custom reason được lưu
**And** skip được thực hiện

### AC-003: Skip Limit Warning
**Given** student đã skip 3 lần liên tiếp
**When** student skip lần thứ 4
**Then** warning được hiển thị
**And** skip vẫn được thực hiện

### AC-004: Network Error Handling
**Given** mất kết nối internet
**When** student submit skip
**Then** error message được hiển thị
**And** retry option được cung cấp

### AC-005: Reschedule Conflict
**Given** ngày reschedule có quá nhiều set
**When** system tính toán lịch mới
**Then** system chọn ngày thay thế
**And** conflict được tránh

## 11. Test Cases

### TC-001: Normal Skip
**Test Data**: Valid skip reason, normal conditions
**Expected Result**: Skip successful, reschedule created

### TC-002: Skip with Custom Reason
**Test Data**: Custom reason = "Bận họp"
**Expected Result**: Custom reason saved, skip completed

### TC-003: Skip Limit Reached
**Test Data**: 4th consecutive skip
**Expected Result**: Warning displayed, skip still allowed

### TC-004: Network Error
**Test Data**: Simulate network timeout
**Expected Result**: Error message, retry option

### TC-005: Reschedule Conflict
**Test Data**: Target date has 3 sets already
**Expected Result**: Alternative date selected

## 12. Related Use Cases

- **UC-011**: Perform Review Session
- **UC-016**: Reschedule Reminder
- **UC-019**: View Learning Statistics
- **UC-020**: Manage Learning Preferences

## 13. Notes

### Implementation Notes
- Implement skip reason enum
- Add skip analytics tracking
- Implement reschedule conflict resolution
- Add skip pattern analysis

### Future Enhancements
- Smart reschedule suggestions
- Skip pattern learning
- Adaptive skip limits
- Skip impact analysis
