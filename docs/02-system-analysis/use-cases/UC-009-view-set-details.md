# UC-009: View Set Details

## 1. Use Case Information

**Use Case ID**: UC-009
**Use Case Name**: View Set Details
**Primary Actor**: Student (Người học)
**Secondary Actors**: None
**Priority**: High
**Complexity**: Low

## 2. Brief Description

Student xem thông tin chi tiết của set học tập, bao gồm thông tin cơ bản, tiến trình học tập, lịch sử ôn tập và các thống kê liên quan. Hệ thống hiển thị đầy đủ thông tin để user hiểu rõ trạng thái và hiệu quả học tập.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Set tồn tại và thuộc về user
- Set chưa bị xóa (deleted_at = null)
- User có quyền xem chi tiết set

## 4. Main Flow

### Step 1: Access Set Details
**Actor Action**: Student chọn set từ danh sách hoặc từ notification
**System Response**: Hiển thị trang chi tiết set với thông tin đầy đủ

### Step 2: View Basic Information
**Actor Action**: Student xem thông tin cơ bản của set
**System Response**: Hiển thị:
- Tên set và mô tả đầy đủ
- Category và số từ vựng
- Trạng thái hiện tại
- Ngày tạo và cập nhật cuối
- Thời gian học tập tổng cộng

### Step 3: View Learning Progress
**Actor Action**: Student xem tiến trình học tập
**System Response**: Hiển thị:
- Chu kỳ hiện tại và số lần ôn
- Trạng thái chu kỳ (đang học, hoàn thành)
- Tiến trình tổng thể (not_started → learning → reviewing → mastered)
- Thời gian dự kiến hoàn thành

### Step 4: View Review History
**Actor Action**: Student xem lịch sử ôn tập
**System Response**: Hiển thị:
- Danh sách các lần ôn tập
- Điểm số từng lần ôn
- Ngày thực hiện ôn tập
- Ghi chú (nếu có)
- Trạng thái ôn tập (completed, skipped)

### Step 5: View Performance Statistics
**Actor Action**: Student xem thống kê hiệu suất
**System Response**: Hiển thị:
- Điểm trung bình tổng thể
- Điểm trung bình theo chu kỳ
- Xu hướng cải thiện
- So sánh với mục tiêu
- Thời gian học tập hiệu quả

### Step 6: View Reminder Schedule
**Actor Action**: Student xem lịch nhắc nhở
**System Response**: Hiển thị:
- Lịch nhắc nhở hiện tại
- Lịch sử nhắc nhở
- Số lần reschedule
- Trạng thái reminder (pending, sent, done, skipped)

### Step 7: Access Set Actions
**Actor Action**: Student chọn action cho set
**System Response**: Hiển thị menu actions:
- Bắt đầu học (nếu chưa học)
- Tiếp tục học (nếu đang học)
- Chỉnh sửa thông tin
- Xóa set
- Xuất dữ liệu

## 5. Alternative Flows

### A1: Set Not Found
**Trigger**: Set không tồn tại hoặc đã bị xóa
**Steps**:
1. System hiển thị thông báo: "Set không tồn tại"
2. System chuyển user về danh sách set
3. Student có thể chọn set khác
4. Return to Step 1

### A2: Permission Denied
**Trigger**: User không có quyền xem set
**Steps**:
1. System hiển thị thông báo: "Bạn không có quyền xem set này"
2. System chuyển user về danh sách set
3. Student có thể chọn set khác
4. Return to Step 1

### A3: No Review History
**Trigger**: Set chưa có lịch sử ôn tập
**Steps**:
1. System hiển thị thông báo: "Chưa có lịch sử ôn tập"
2. System hiển thị nút "Bắt đầu học"
3. Student có thể bắt đầu học set
4. Continue to Step 7

### A4: Set in Active Learning
**Trigger**: Set đang trong chu kỳ học tập
**Steps**:
1. System highlight trạng thái "Đang học"
2. System hiển thị thông tin chu kỳ hiện tại
3. System hiển thị nút "Tiếp tục học"
4. Continue to Step 7

### A5: Set Mastered
**Trigger**: Set đã được mastered
**Steps**:
1. System hiển thị badge "Đã hoàn thành"
2. System hiển thị thông báo chúc mừng
3. System hiển thị tổng kết thành tích
4. Continue to Step 7

### A6: Large Review History
**Trigger**: Set có rất nhiều lịch sử ôn tập
**Steps**:
1. System hiển thị pagination cho review history
2. System load history theo batch
3. System hiển thị loading indicator khi cần
4. Continue to Step 4

### A7: Network Connection Error
**Trigger**: Mất kết nối internet
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System hiển thị dữ liệu cached (nếu có)
3. Student có thể thử lại khi có kết nối
4. Return to Step 1

### A8: Export Set Data
**Trigger**: Student muốn xuất dữ liệu set
**Steps**:
1. Student chọn "Xuất dữ liệu"
2. System hiển thị tùy chọn format (PDF, Excel)
3. Student chọn format và nhấn "Xuất"
4. System tạo file và download

## 6. Post Conditions

### Success Post Conditions
- Thông tin chi tiết set được hiển thị đầy đủ
- User có thể thực hiện các action trên set
- Thống kê và lịch sử được hiển thị chính xác
- Navigation giữa các section hoạt động

### Failure Post Conditions
- Thông tin set không được hiển thị
- Error message được hiển thị
- User có thể thử lại hoặc quay lại danh sách

## 7. Business Rules

### BR-001: Set Management
- User chỉ có thể xem set của mình
- Set phải tồn tại và không bị xóa
- Hiển thị đầy đủ thông tin set

### BR-002: Review History Display
- Hiển thị tất cả lịch sử ôn tập
- Sắp xếp theo thời gian (mới nhất trước)
- Hiển thị điểm số và trạng thái
- Pagination cho large datasets

### BR-003: Performance Statistics
- Tính toán điểm trung bình chính xác
- Hiển thị xu hướng cải thiện
- So sánh với mục tiêu học tập
- Cập nhật real-time

### BR-004: Reminder Schedule
- Hiển thị lịch nhắc nhở hiện tại
- Hiển thị lịch sử reschedule
- Trạng thái reminder rõ ràng
- Thông tin chi tiết về timing

### BR-005: Set Actions
- Hiển thị actions phù hợp với trạng thái set
- Bắt đầu học cho set chưa học
- Tiếp tục học cho set đang học
- Chỉnh sửa và xóa cho tất cả set

## 8. Data Requirements

### Input Data
- **Set ID** (UUID, required)
- **User ID** (UUID, required)

### Output Data
- **Set Details** (complete set information)
- **Review History** (array of review records)
- **Performance Statistics** (calculated metrics)
- **Reminder Schedule** (current and historical)
- **Available Actions** (based on set status)

## 9. Non-Functional Requirements

### Performance
- Set details load < 3 giây
- Review history load < 2 giây
- Statistics calculation < 1 giây
- Export generation < 5 giây

### Security
- User chỉ có thể xem set của mình
- Validate set ownership
- Sanitize displayed data

### Usability
- Responsive design cho mobile
- Clear section navigation
- Loading states và error handling
- Export functionality

## 10. Acceptance Criteria

### AC-001: Display Set Details
**Given** user có set hợp lệ
**When** user truy cập chi tiết set
**Then** thông tin đầy đủ được hiển thị
**And** user có thể thực hiện actions

### AC-002: Review History
**Given** set có lịch sử ôn tập
**When** user xem lịch sử
**Then** tất cả lịch sử được hiển thị
**And** thông tin chi tiết được hiển thị

### AC-003: Performance Statistics
**Given** set có dữ liệu học tập
**When** user xem thống kê
**Then** thống kê được tính toán chính xác
**And** xu hướng được hiển thị

### AC-004: Set Actions
**Given** set ở trạng thái cụ thể
**When** user xem actions
**Then** actions phù hợp được hiển thị
**And** user có thể thực hiện actions

### AC-005: Export Functionality
**Given** user muốn xuất dữ liệu
**When** user chọn xuất
**Then** file được tạo và download
**And** dữ liệu đầy đủ được xuất

## 11. Test Cases

### TC-001: Display Set Details
**Test Data**: Valid set with complete information
**Expected Result**: All details displayed correctly

### TC-002: No Review History
**Test Data**: New set without reviews
**Expected Result**: No history message shown, start learning option

### TC-003: Active Learning Set
**Test Data**: Set with status 'learning'
**Expected Result**: Current cycle info shown, continue learning option

### TC-004: Mastered Set
**Test Data**: Set with status 'mastered'
**Expected Result**: Completion badge shown, performance summary

### TC-005: Large Review History
**Test Data**: Set with 100+ reviews
**Expected Result**: Pagination implemented, performance maintained

### TC-006: Export Data
**Test Data**: Set with complete data
**Expected Result**: Export file generated and downloaded

## 12. Related Use Cases

- **UC-005**: Create New Set
- **UC-006**: Edit Set Information
- **UC-007**: Delete Set
- **UC-008**: View Set List
- **UC-010**: Start Learning Cycle
- **UC-011**: Perform Review Session

## 13. Notes

### Implementation Notes
- Implement lazy loading cho review history
- Cache set details để tăng performance
- Calculate statistics efficiently
- Handle large datasets với pagination

### Future Enhancements
- Advanced analytics và insights
- Social sharing features
- Set comparison tools
- Learning recommendations

### UI/UX Considerations
- Clean, organized layout
- Visual progress indicators
- Interactive charts và graphs
- Mobile-first responsive design
- Accessibility features
