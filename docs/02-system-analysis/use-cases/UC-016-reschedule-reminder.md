# UC-016: Reschedule Reminder

## 1. Use Case Information

**Use Case ID**: UC-016
**Use Case Name**: Reschedule Reminder
**Primary Actor**: Student (Người học)
**Secondary Actors**: None
**Priority**: Medium
**Complexity**: Low

## 2. Brief Description

Student thay đổi lịch nhắc nhở ôn tập cho set học tập. Hệ thống validate thông tin, cập nhật reminder và lưu lịch sử thay đổi.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Có reminder đang pending hoặc sent
- Reminder chưa đạt giới hạn reschedule (tối đa 2 lần)
- Reminder chưa được hoàn thành (status ≠ 'done')

## 4. Main Flow

### Step 1: Access Reminder Management
**Actor Action**: Student chọn set cần reschedule reminder
**System Response**: Hiển thị thông tin reminder và tùy chọn reschedule

### Step 2: View Current Reminder
**Actor Action**: Student xem thông tin reminder hiện tại
**System Response**: Hiển thị:
- Ngày reminder hiện tại
- Số lần đã reschedule
- Trạng thái reminder
- Lịch sử reschedule (nếu có)

### Step 3: Select New Date
**Actor Action**: Student chọn ngày mới cho reminder
**System Response**: Validate ngày mới và hiển thị preview

### Step 4: Provide Reason
**Actor Action**: Student nhập lý do reschedule (optional)
**System Response**: Hiển thị preview thông tin thay đổi

### Step 5: Submit Reschedule
**Actor Action**: Student nhấn "Reschedule"
**System Response**: 
- Validate thông tin reschedule
- Kiểm tra giới hạn reschedule
- Cập nhật reminder với ngày mới
- Lưu lịch sử thay đổi

### Step 6: Complete Reschedule
**Actor Action**: System tự động
**System Response**:
- Hiển thị thông báo thành công
- Cập nhật reminder status = 'rescheduled'
- Tăng reschedule_count
- Lưu vào activity_logs

## 5. Alternative Flows

### A1: Invalid Date Selection
**Trigger**: Ngày mới không hợp lệ
**Steps**:
1. System hiển thị thông báo lỗi: "Ngày mới phải trong tương lai và không quá 7 ngày"
2. System highlight trường ngày
3. Student chọn lại ngày
4. Return to Step 3

### A2: Reschedule Limit Exceeded
**Trigger**: Đã đạt giới hạn 2 lần reschedule
**Steps**:
1. System hiển thị thông báo: "Bạn đã đạt giới hạn reschedule cho reminder này"
2. System hiển thị lịch sử reschedule
3. Student có thể:
   - Chọn reminder khác
   - Hoàn thành reminder hiện tại
4. Return to Step 1

### A3: Reminder Already Completed
**Trigger**: Reminder đã được hoàn thành
**Steps**:
1. System hiển thị thông báo: "Reminder này đã được hoàn thành, không thể reschedule"
2. System hiển thị thông tin hoàn thành
3. Student có thể xem lịch sử hoặc chọn reminder khác
4. Return to Step 1

### A4: Date Conflict
**Trigger**: Ngày mới trùng với reminder khác
**Steps**:
1. System hiển thị thông báo: "Ngày này đã có reminder khác"
2. System hiển thị reminder trùng lặp
3. Student chọn ngày khác
4. Return to Step 3

### A5: Network Connection Error
**Trigger**: Mất kết nối internet
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System lưu draft reschedule data locally
3. Student có thể thử lại khi có kết nối
4. Return to Step 1

### A6: Reminder Not Found
**Trigger**: Reminder không tồn tại hoặc đã bị xóa
**Steps**:
1. System hiển thị thông báo: "Reminder không tồn tại"
2. System refresh danh sách reminder
3. Student chọn reminder khác
4. Return to Step 1

## 6. Post Conditions

### Success Post Conditions
- Reminder được reschedule thành công
- Ngày reminder được cập nhật
- Reschedule_count được tăng
- Lịch sử thay đổi được lưu
- Activity log được tạo

### Failure Post Conditions
- Reminder không được thay đổi
- Error message được hiển thị
- User có thể thử lại hoặc hủy

## 7. Business Rules

### BR-012: Reminder Reschedule
- Tối đa 2 lần reschedule cho cùng reminder
- Không được reschedule quá 7 ngày trong tương lai
- Không được reschedule về quá khứ
- Lưu lịch sử: ai đổi, khi nào, lý do

### BR-010: Daily Reminder Limit
- Tối đa 3 set/user/ngày
- Nếu trùng lặp, ưu tiên reminder cũ hơn
- Tự động reschedule nếu vượt quá giới hạn

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- updated_at = now() khi cập nhật
- Lưu lịch sử thay đổi

## 8. Data Requirements

### Input Data
- **Reminder ID** (UUID, required)
- **New Date** (date, required, future date)
- **Reason** (string, optional, ≤ 500 chars)

### Output Data
- **Reschedule Status** (success/error)
- **Updated Reminder Info** (new date, reschedule_count)
- **Error Messages** (if any)
- **Activity Log Entry** (who, when, reason)

## 9. Non-Functional Requirements

### Performance
- Reschedule process < 2 giây
- Date validation < 500ms
- Conflict checking < 1 giây

### Security
- User chỉ có thể reschedule reminder của mình
- Validate reminder permissions
- Log all reschedule activities

### Usability
- Clear date picker interface
- Conflict warnings
- Reschedule history display
- Success feedback

## 10. Acceptance Criteria

### AC-001: Successful Reschedule
**Given** user có reminder hợp lệ
**When** user chọn ngày mới hợp lệ
**Then** reminder được reschedule thành công
**And** ngày reminder được cập nhật
**And** reschedule_count được tăng

### AC-002: Invalid Date
**Given** user chọn ngày không hợp lệ
**When** user submit reschedule
**Then** system hiển thị thông báo lỗi
**And** reminder không được thay đổi

### AC-003: Reschedule Limit
**Given** user đã reschedule 2 lần
**When** user thử reschedule lần nữa
**Then** system hiển thị thông báo giới hạn
**And** reschedule không được thực hiện

### AC-004: Date Conflict
**Given** ngày mới trùng với reminder khác
**When** user submit reschedule
**Then** system hiển thị thông báo xung đột
**And** user được yêu cầu chọn ngày khác

### AC-005: Completed Reminder
**Given** reminder đã được hoàn thành
**When** user thử reschedule
**Then** system hiển thị thông báo không thể reschedule
**And** user được hướng dẫn xem lịch sử

## 11. Test Cases

### TC-001: Valid Reschedule
**Test Data**: Valid new date, optional reason
**Expected Result**: Reminder rescheduled, count increased, history logged

### TC-002: Invalid Date
**Test Data**: Past date or date > 7 days
**Expected Result**: Validation error, reschedule not performed

### TC-003: Reschedule Limit
**Test Data**: 3rd reschedule attempt
**Expected Result**: Limit exceeded message, reschedule not allowed

### TC-004: Date Conflict
**Test Data**: New date conflicts with existing reminder
**Expected Result**: Conflict warning, user must choose different date

### TC-005: Completed Reminder
**Test Data**: Reminder with status 'done'
**Expected Result**: Cannot reschedule message, view history option

### TC-006: Network Error
**Test Data**: Simulate network timeout
**Expected Result**: Network error message, retry option

## 12. Related Use Cases

- **UC-015**: Receive Reminder
- **UC-017**: Mark Reminder as Done
- **UC-018**: Skip Reminder
- **UC-019**: View Learning Statistics

## 13. Notes

### Implementation Notes
- Validate date range (today + 1 to today + 7)
- Check reschedule limit before allowing
- Implement conflict detection algorithm
- Log all reschedule activities
- Handle timezone differences

### Future Enhancements
- Smart date suggestions
- Bulk reschedule for multiple reminders
- Integration with calendar apps
- Automatic conflict resolution

### UI/UX Considerations
- Intuitive date picker
- Clear conflict warnings
- Reschedule history display
- Progress indicators
