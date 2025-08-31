# UC-011: Perform Review Session

## 1. Use Case Information

**Use Case ID**: UC-011
**Use Case Name**: Perform Review Session
**Primary Actor**: Student (Người học)
**Secondary Actors**: None
**Priority**: High
**Complexity**: Medium

## 2. Brief Description

Student thực hiện lần ôn tập cho set học tập, tự đánh giá mức độ nhớ và nhập điểm số. Hệ thống lưu kết quả và cập nhật tiến trình học tập.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Có set cần ôn tập trong ngày
- Reminder đã được gửi hoặc user chủ động ôn tập
- Set có trạng thái 'learning' hoặc 'reviewing'

## 4. Main Flow

### Step 1: Access Review Session
**Actor Action**: Student nhận notification hoặc chọn set cần ôn
**System Response**: Hiển thị màn hình review với thông tin set

### Step 2: View Set Information
**Actor Action**: Student xem thông tin set cần ôn
**System Response**: Hiển thị:
- Tên set và mô tả
- Số từ vựng
- Chu kỳ hiện tại và lần ôn
- Lịch sử điểm số (nếu có)

### Step 3: Perform Review
**Actor Action**: Student xem lại nội dung học tập và tự đánh giá
**System Response**: Hiển thị nội dung cần ôn và hướng dẫn đánh giá

### Step 4: Input Score
**Actor Action**: Student chọn điểm số (0-100%) và nhập ghi chú (optional)
**System Response**: Validate điểm số và hiển thị preview

### Step 5: Submit Review
**Actor Action**: Student nhấn "Lưu điểm"
**System Response**: 
- Validate điểm số
- Lưu vào review_histories
- Cập nhật trạng thái reminder = 'done'
- Kiểm tra nếu đã đủ 5 lần ôn

### Step 6: Complete Review
**Actor Action**: System tự động
**System Response**:
- Hiển thị thông báo hoàn thành
- Cập nhật tiến trình set
- Tính toán delay chu kỳ mới nếu hoàn thành chu kỳ
- Tạo reminder cho lần ôn tiếp theo

## 5. Alternative Flows

### A1: Skip Review Session
**Trigger**: Student chọn skip lần ôn
**Steps**:
1. Student chọn "Skip" thay vì nhập điểm
2. Student chọn lý do: forgot, busy, other
3. System lưu trạng thái = 'skipped'
4. System cập nhật reminder status = 'skipped'
5. Continue to Step 6

### A2: Invalid Score Input
**Trigger**: Điểm số không hợp lệ
**Steps**:
1. System hiển thị thông báo lỗi: "Điểm số phải từ 0-100"
2. System highlight trường điểm số
3. Student nhập lại điểm số
4. Return to Step 4

### A3: Cycle Completion
**Trigger**: Hoàn thành 5 lần ôn trong chu kỳ
**Steps**:
1. System tính avg_score của chu kỳ
2. System tính delay chu kỳ mới theo SRS algorithm
3. System tạo chu kỳ mới với current_cycle + 1
4. System cập nhật trạng thái set nếu cần
5. Continue to Step 6

### A4: Set Mastered
**Trigger**: Set đạt điều kiện mastered
**Steps**:
1. System kiểm tra điều kiện mastered theo BR-033:
   - avg_score ≥ 85% trong 3 chu kỳ liên tiếp
   - Không có skip trong 3 chu kỳ cuối
   - Tổng thời gian học ≥ 30 ngày
2. System cập nhật status = 'mastered'
3. System hiển thị thông báo chúc mừng
4. System tạo lịch ôn định kỳ mỗi 90 ngày
5. System ghi nhận achievement vào activity_logs
6. Continue to Step 6

### A5: Network Connection Error
**Trigger**: Mất kết nối internet trong quá trình ôn tập
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System lưu draft review data locally
3. Student có thể thử lại khi có kết nối
4. Return to Step 1

### A6: Review Already Completed
**Trigger**: Lần ôn này đã được hoàn thành
**Steps**:
1. System hiển thị thông báo: "Lần ôn này đã được hoàn thành"
2. System hiển thị điểm số đã nhập
3. Student có thể xem lịch sử hoặc chuyển sang set khác
4. Return to Step 1

## 6. Post Conditions

### Success Post Conditions
- Review được lưu thành công vào database
- Điểm số được ghi nhận
- Reminder status được cập nhật
- Tiến trình set được cập nhật
- Chu kỳ mới được tạo nếu hoàn thành

### Failure Post Conditions
- Review không được lưu
- Error message được hiển thị
- User có thể thử lại
- Draft data được giữ lại

## 7. Business Rules

### BR-004: Cycle Structure
- Mỗi chu kỳ có đúng 5 lần ôn tập
- Thời gian giữa các lần ôn cố định
- Sau 5 lần ôn, tính avg_score và delay chu kỳ mới

### BR-007: Score Input
- Điểm bắt buộc nhập (0-100%)
- Nếu skip, phải chọn lý do: forgot, busy, other
- Trạng thái lần ôn = skipped nếu không nhập điểm

### BR-008: Score History
- Lưu đầy đủ: set_id, cycle_no, review_no, score, status, note, created_at
- Mọi thay đổi điểm lưu vào activity_logs
- Không xóa lịch sử điểm

### BR-009: Score Validation
- Điểm phải là số nguyên từ 0-100
- Không cho phép điểm âm hoặc > 100
- Điểm 0-40: Kém, cần ôn lại sớm
- Điểm 41-70: Trung bình, ôn lại bình thường
- Điểm 71-100: Tốt, có thể ôn lại muộn hơn

## 8. Data Requirements

### Input Data
- **Set ID** (UUID, required)
- **Cycle Number** (integer, required)
- **Review Number** (integer, required, 1-5)
- **Score** (integer, required, 0-100)
- **Note** (string, optional, ≤ 500 chars)
- **Skip Reason** (enum: forgot, busy, other, optional)

### Output Data
- **Review ID** (UUID)
- **Save Status** (success/error)
- **Updated Set Status** (if cycle completed)
- **Next Reminder Date** (if applicable)
- **Cycle Completion Info** (if applicable)

## 9. Non-Functional Requirements

### Performance
- Review submission < 2 giây
- Score validation < 500ms
- Cycle calculation < 1 giây

### Security
- User chỉ có thể nhập điểm cho set của mình
- Validate review permissions
- Log all score changes

### Usability
- Clear score input interface
- Helpful guidance for self-assessment
- Progress indicators
- Success feedback

## 10. Acceptance Criteria

### AC-001: Successful Review
**Given** user có set cần ôn tập
**When** user nhập điểm số hợp lệ
**Then** review được lưu thành công
**And** tiến trình set được cập nhật
**And** reminder status được cập nhật

### AC-002: Skip Review
**Given** user không thể ôn tập
**When** user chọn skip với lý do
**Then** review được lưu với status 'skipped'
**And** reminder status được cập nhật
**And** lần ôn tiếp theo được lên lịch

### AC-003: Cycle Completion
**Given** user hoàn thành 5 lần ôn
**When** user nhập điểm lần cuối
**Then** chu kỳ được hoàn thành
**And** avg_score được tính
**And** chu kỳ mới được tạo với delay mới

### AC-004: Invalid Score
**Given** user nhập điểm không hợp lệ
**When** user submit review
**Then** system hiển thị thông báo lỗi
**And** review không được lưu

### AC-005: Set Mastered
**Given** set đạt điều kiện mastered (BR-033)
**When** user hoàn thành chu kỳ
**Then** set status được cập nhật thành 'mastered'
**And** thông báo chúc mừng được hiển thị
**And** lịch ôn định kỳ 90 ngày được tạo
**And** achievement được ghi nhận

## 11. Test Cases

### TC-001: Valid Review Submission
**Test Data**: Valid score (75), optional note
**Expected Result**: Review saved, progress updated, reminder status updated

### TC-002: Skip Review
**Test Data**: Skip with reason 'busy'
**Expected Result**: Review saved with 'skipped' status, next reminder scheduled

### TC-003: Cycle Completion
**Test Data**: 5th review in cycle, score 80
**Expected Result**: Cycle completed, new cycle created, delay calculated

### TC-004: Invalid Score
**Test Data**: Score = 150
**Expected Result**: Validation error, review not saved

### TC-005: Set Mastered
**Test Data**: 3rd consecutive cycle with avg_score ≥ 85%, no skips, ≥ 30 days total
**Expected Result**: Set status updated to 'mastered', congratulations shown, 90-day periodic review scheduled

### TC-006: Network Error
**Test Data**: Simulate network timeout
**Expected Result**: Network error message, draft saved locally

## 12. Related Use Cases

- **UC-010**: Start Learning Cycle
- **UC-012**: Input Score
- **UC-013**: Skip Review Session
- **UC-014**: Complete Cycle
- **UC-015**: Receive Reminder

## 13. Notes

### Implementation Notes
- Validate review number within cycle (1-5)
- Calculate avg_score only when cycle completes
- Implement SRS algorithm for delay calculation
- Log all score changes for audit
- Handle concurrent review submissions

### Future Enhancements
- Adaptive difficulty based on performance
- Detailed performance analytics
- Review reminders with smart timing
- Social features for motivation

### UI/UX Considerations
- Clear score input with visual feedback
- Progress indicators for cycle completion
- Motivational messages based on performance
- Easy navigation between reviews
