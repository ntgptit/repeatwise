# UC-012: Complete Review Session

## 1. Use Case Information

**Use Case ID**: UC-012
**Use Case Name**: Complete Review Session
**Primary Actor**: Student (Người học)
**Secondary Actors**: Notification Service, Statistics Service
**Priority**: High
**Complexity**: Medium

## 2. Brief Description

Student hoàn thành một phiên ôn tập bằng cách nhập điểm số và hệ thống cập nhật tiến trình học tập, tính toán lịch ôn tiếp theo.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Student đang trong phiên ôn tập (UC-011)
- Set đang ở trạng thái learning hoặc reviewing
- Có ít nhất 1 lần ôn trong chu kỳ hiện tại

## 4. Main Flow

### Step 1: Submit Review Score
**Actor Action**: Student nhập điểm số (0-100%) và nhấn "Hoàn thành"
**System Response**: Validate điểm số và hiển thị xác nhận

### Step 2: Validate Score
**Actor Action**: System tự động
**System Response**: 
- Kiểm tra điểm số trong khoảng 0-100
- Validate format và logic
- Hiển thị preview kết quả

### Step 3: Update Learning Progress
**Actor Action**: System tự động
**System Response**:
- Lưu điểm số vào learning_history
- Cập nhật review_count trong current_cycle
- Tính toán trung bình điểm của chu kỳ hiện tại

### Step 4: Check Cycle Completion
**Actor Action**: System tự động
**System Response**:
- Kiểm tra nếu đã đủ 5 lần ôn trong chu kỳ
- Nếu đủ: chuyển sang Step 5
- Nếu chưa đủ: chuyển sang Step 6

### Step 5: Complete Current Cycle
**Actor Action**: System tự động
**System Response**:
- Cập nhật cycle_status = 'completed'
- Tính toán delay cho chu kỳ tiếp theo theo thuật toán SRS
- Tạo chu kỳ mới với next_review_date
- Cập nhật set status nếu cần

### Step 6: Schedule Next Review
**Actor Action**: System tự động
**System Response**:
- Tạo reminder cho lần ôn tiếp theo
- Cập nhật next_review_date
- Gửi notification xác nhận

### Step 7: Update Statistics
**Actor Action**: System tự động
**System Response**:
- Cập nhật learning statistics
- Tính toán progress percentage
- Log completion event

### Step 8: Display Completion
**Actor Action**: System tự động
**System Response**:
- Hiển thị màn hình hoàn thành
- Hiển thị điểm số và feedback
- Hiển thị lịch ôn tiếp theo
- Đề xuất set khác nếu có

## 5. Alternative Flows

### A1: Invalid Score Input
**Trigger**: Điểm số không hợp lệ
**Steps**:
1. System hiển thị thông báo lỗi: "Điểm số phải từ 0-100"
2. System highlight trường điểm số
3. Student nhập lại điểm số
4. Return to Step 1

### A2: Network Connection Error
**Trigger**: Mất kết nối khi submit điểm số
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System lưu điểm số locally
3. Student có thể:
   - Thử lại khi có kết nối
   - Lưu để submit sau
4. Return to Step 1

### A3: Cycle Completion with High Score
**Trigger**: Hoàn thành chu kỳ với điểm trung bình > 80%
**Steps**:
1. System hiển thị thông báo chúc mừng
2. System kiểm tra điều kiện mastered (BR-033)
3. Nếu đạt mastered:
   - Cập nhật status = 'mastered'
   - Tạo lịch ôn định kỳ 90 ngày
   - Hiển thị thông báo chúc mừng đặc biệt
4. Nếu chưa đạt mastered:
   - Đề xuất tăng độ khó hoặc chuyển set
   - Tính delay dài hơn cho chu kỳ tiếp theo
5. Continue to Step 5

### A4: Cycle Completion with Low Score
**Trigger**: Hoàn thành chu kỳ với điểm trung bình < 50%
**Steps**:
1. System hiển thị thông báo khuyến khích
2. System đề xuất ôn lại sớm hơn
3. System tính delay ngắn hơn cho chu kỳ tiếp theo
4. Continue to Step 5

### A5: Maximum Daily Reviews Reached
**Trigger**: Đã đạt giới hạn 3 set/ngày
**Steps**:
1. System thông báo giới hạn daily review
2. System tự động reschedule reminder
3. System đề xuất set khác cho ngày mai
4. Continue to Step 6

## 6. Post Conditions

### Success Post Conditions
- Điểm số được lưu vào learning_history
- Tiến trình chu kỳ được cập nhật
- Lịch ôn tiếp theo được tính toán
- Statistics được cập nhật
- Reminder được tạo cho lần ôn tiếp theo

### Failure Post Conditions
- Điểm số không được lưu
- Tiến trình không thay đổi
- Error message được hiển thị
- Student có thể thử lại

## 7. Business Rules

### BR-030: Review Scoring
- Điểm số phải từ 0-100%
- Điểm số bắt buộc phải nhập
- Không thể sửa điểm số sau khi submit

### BR-031: Cycle Management
- Mỗi chu kỳ có 5 lần ôn
- Chu kỳ mới chỉ bắt đầu sau khi hoàn thành chu kỳ cũ
- Delay chu kỳ mới tính theo thuật toán SRS

### BR-032: Daily Review Limit
- Tối đa 3 set được nhắc ôn mỗi ngày
- Set vượt quá giới hạn tự động reschedule
- Ưu tiên set quá hạn và điểm thấp

### BR-015: UUID Usage
- Tất cả ID sử dụng UUID format
- UUID được generate trước khi insert

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- created_at = now() khi tạo mới

## 8. Data Requirements

### Input Data
- **Review Score** (integer, required, 0-100)
- **Review Date** (datetime, required)
- **Review Duration** (integer, optional, seconds)
- **Notes** (string, optional, ≤ 500 chars)

### Output Data
- **Review ID** (UUID)
- **Completion Status** (success/error)
- **Next Review Date** (datetime)
- **Cycle Status** (in_progress/completed)
- **Updated Statistics** (object)

## 9. Non-Functional Requirements

### Performance
- Score submission < 3 giây
- Statistics update < 2 giây
- Reminder creation < 1 giây

### Security
- Validate user ownership của set
- Log all score submissions
- Prevent score manipulation

### Usability
- Clear score input interface
- Immediate feedback
- Progress visualization
- Completion celebration

## 10. Acceptance Criteria

### AC-001: Valid Score Submission
**Given** student đang trong phiên ôn tập
**When** student nhập điểm số hợp lệ
**Then** điểm số được lưu thành công
**And** tiến trình được cập nhật

### AC-002: Cycle Completion
**Given** student đã ôn đủ 5 lần trong chu kỳ
**When** student submit lần ôn cuối
**Then** chu kỳ được hoàn thành
**And** chu kỳ mới được tạo

### AC-003: Invalid Score Handling
**Given** student nhập điểm số không hợp lệ
**When** student submit
**Then** system hiển thị thông báo lỗi
**And** form không được submit

### AC-004: Daily Limit Handling
**Given** đã đạt giới hạn 3 set/ngày
**When** student hoàn thành ôn tập
**Then** reminder được reschedule
**And** thông báo giới hạn hiển thị

### AC-005: Statistics Update
**Given** student hoàn thành ôn tập
**When** điểm số được lưu
**Then** statistics được cập nhật
**And** progress được hiển thị

## 11. Test Cases

### TC-001: Normal Score Submission
**Test Data**: Score = 85, valid session
**Expected Result**: Score saved, progress updated, next review scheduled

### TC-002: Cycle Completion
**Test Data**: 5th review in cycle, score = 90
**Expected Result**: Cycle completed, new cycle created, delay calculated

### TC-003: Invalid Score
**Test Data**: Score = 150
**Expected Result**: Validation error, form not submitted

### TC-004: Network Error
**Test Data**: Simulate network timeout
**Expected Result**: Error message, retry option

### TC-005: Daily Limit Reached
**Test Data**: 4th set review of the day
**Expected Result**: Reminder rescheduled, limit notification

## 12. Related Use Cases

- **UC-011**: Perform Review Session
- **UC-010**: Start Learning Cycle
- **UC-016**: Reschedule Reminder
- **UC-019**: View Learning Statistics

## 13. Notes

### Implementation Notes
- Implement SRS algorithm cho delay calculation
- Add score validation và sanitization
- Log all review completions
- Implement retry mechanism cho network errors

### Future Enhancements
- Adaptive difficulty adjustment
- Peer comparison features
- Gamification elements
- AI-powered study recommendations
