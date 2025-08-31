# UC-019: View Learning Statistics

## 1. Use Case Information

**Use Case ID**: UC-019
**Use Case Name**: View Learning Statistics
**Primary Actor**: Student (Người học)
**Secondary Actors**: None
**Priority**: Medium
**Complexity**: Medium

## 2. Brief Description

Student xem thống kê học tập chi tiết bao gồm tiến trình, hiệu suất, và phân tích dữ liệu học tập. Hệ thống hiển thị các biểu đồ, báo cáo và insights để giúp user theo dõi và cải thiện quá trình học tập.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Có dữ liệu học tập trong hệ thống
- User có quyền truy cập thống kê

## 4. Main Flow

### Step 1: Access Statistics Dashboard
**Actor Action**: Student chọn "Thống kê học tập" từ menu
**System Response**: Hiển thị dashboard thống kê với overview

### Step 2: View Overview Statistics
**Actor Action**: Student xem thống kê tổng quan
**System Response**: Hiển thị:
- Tổng số set đã tạo
- Số set đang học
- Số set đã mastered
- Tổng thời gian học tập
- Điểm trung bình tổng thể

### Step 3: Select Time Period
**Actor Action**: Student chọn khoảng thời gian (7 ngày, 30 ngày, 3 tháng, 1 năm)
**System Response**: Cập nhật tất cả biểu đồ và số liệu theo thời gian

### Step 4: View Performance Charts
**Actor Action**: Student xem các biểu đồ hiệu suất
**System Response**: Hiển thị:
- Biểu đồ điểm số theo thời gian
- Biểu đồ số lần ôn tập
- Biểu đồ tiến trình set
- Biểu đồ phân bố điểm số

### Step 5: View Set Details
**Actor Action**: Student chọn set cụ thể để xem chi tiết
**System Response**: Hiển thị thống kê chi tiết cho set:
- Lịch sử điểm số
- Chu kỳ học tập
- Thời gian hoàn thành
- So sánh với trung bình

### Step 6: Export Statistics
**Actor Action**: Student chọn xuất báo cáo (optional)
**System Response**: Tạo file PDF/Excel với thống kê chi tiết

## 5. Alternative Flows

### A1: No Learning Data
**Trigger**: User chưa có dữ liệu học tập
**Steps**:
1. System hiển thị thông báo: "Bạn chưa có dữ liệu học tập"
2. System hiển thị hướng dẫn tạo set đầu tiên
3. Student có thể tạo set hoặc quay lại
4. Return to Step 1

### A2: Filter by Set Category
**Trigger**: Student muốn lọc theo loại set
**Steps**:
1. Student chọn filter theo category
2. System cập nhật thống kê theo category
3. Student xem thống kê đã lọc
4. Continue to Step 4

### A3: Compare Performance
**Trigger**: Student muốn so sánh hiệu suất
**Steps**:
1. Student chọn "So sánh hiệu suất"
2. System hiển thị so sánh:
   - Hiện tại vs trước đó
   - Set này vs set khác
   - So với trung bình
3. Student phân tích so sánh
4. Continue to Step 4

### A4: View Learning Insights
**Trigger**: Student muốn xem insights
**Steps**:
1. Student chọn "Insights"
2. System hiển thị:
   - Thời gian học tốt nhất
   - Set khó nhất
   - Cải thiện gần đây
   - Gợi ý học tập
3. Student xem insights
4. Continue to Step 4

### A5: Network Connection Error
**Trigger**: Mất kết nối internet
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System hiển thị dữ liệu cached (nếu có)
3. Student có thể thử lại khi có kết nối
4. Return to Step 1

### A6: Large Dataset
**Trigger**: Dữ liệu quá lớn để load
**Steps**:
1. System hiển thị loading indicator
2. System load dữ liệu theo batch
3. System hiển thị thống kê từng phần
4. Continue to Step 2

## 6. Post Conditions

### Success Post Conditions
- Thống kê được hiển thị đầy đủ
- User có thể xem chi tiết từng set
- User có thể xuất báo cáo
- User hiểu được hiệu suất học tập

### Failure Post Conditions
- Thống kê không được hiển thị
- Error message được hiển thị
- User có thể thử lại hoặc liên hệ support

## 7. Business Rules

### BR-013: Learning Analytics
- Tính toán điểm trung bình theo chu kỳ
- Phân tích xu hướng học tập
- So sánh hiệu suất theo thời gian
- Đưa ra gợi ý cải thiện

### BR-014: Data Privacy
- User chỉ xem được dữ liệu của mình
- Không chia sẻ dữ liệu cá nhân
- Tuân thủ GDPR

### BR-015: Performance Metrics
- Điểm trung bình tổng thể
- Tỷ lệ hoàn thành set
- Thời gian trung bình per set
- Số lần reschedule

## 8. Data Requirements

### Input Data
- **User ID** (UUID, required)
- **Time Period** (enum: 7d, 30d, 3m, 1y, required)
- **Set Category** (string, optional)
- **Export Format** (enum: PDF, Excel, optional)

### Output Data
- **Overview Statistics** (total sets, active sets, mastered sets, avg score)
- **Performance Charts** (score trends, review frequency, set progress)
- **Set Details** (individual set statistics)
- **Insights** (learning patterns, recommendations)
- **Export File** (if requested)

## 9. Non-Functional Requirements

### Performance
- Dashboard load < 3 giây
- Chart rendering < 2 giây
- Export generation < 5 giây
- Real-time data updates

### Security
- User chỉ xem được dữ liệu của mình
- Encrypt sensitive statistics
- Audit log for data access

### Usability
- Responsive design for mobile
- Interactive charts
- Clear data visualization
- Easy navigation

## 10. Acceptance Criteria

### AC-001: Dashboard Display
**Given** user có dữ liệu học tập
**When** user truy cập thống kê
**Then** dashboard hiển thị overview đầy đủ
**And** các biểu đồ được render chính xác

### AC-002: Time Period Filter
**Given** user chọn khoảng thời gian
**When** user xem thống kê
**Then** tất cả dữ liệu được filter theo thời gian
**And** biểu đồ được cập nhật

### AC-003: Set Details
**Given** user chọn set cụ thể
**When** user xem chi tiết
**Then** thống kê chi tiết của set được hiển thị
**And** lịch sử điểm số được hiển thị

### AC-004: Export Functionality
**Given** user yêu cầu xuất báo cáo
**When** user chọn format
**Then** file được tạo và download
**And** báo cáo chứa đầy đủ thống kê

### AC-005: No Data Handling
**Given** user chưa có dữ liệu
**When** user truy cập thống kê
**Then** thông báo hướng dẫn được hiển thị
**And** user được khuyến khích tạo set

## 11. Test Cases

### TC-001: Dashboard Load
**Test Data**: User with learning data
**Expected Result**: Dashboard loads with all statistics and charts

### TC-002: Time Filter
**Test Data**: Different time periods
**Expected Result**: Statistics update correctly for each period

### TC-003: Set Details
**Test Data**: Specific set selection
**Expected Result**: Detailed statistics for selected set displayed

### TC-004: Export Report
**Test Data**: PDF/Excel export request
**Expected Result**: File generated and downloaded successfully

### TC-005: No Data User
**Test Data**: New user without data
**Expected Result**: Guidance message displayed

### TC-006: Large Dataset
**Test Data**: User with many sets
**Expected Result**: Statistics load with pagination/optimization

## 12. Related Use Cases

- **UC-005**: Create New Set
- **UC-011**: Perform Review Session
- **UC-020**: Generate Learning Report
- **UC-021**: Set Learning Goals

## 13. Notes

### Implementation Notes
- Use caching for performance optimization
- Implement data aggregation for large datasets
- Use chart libraries for visualization
- Implement real-time updates for active sessions

### Future Enhancements
- AI-powered learning insights
- Social comparison features
- Gamification elements
- Integration with external learning platforms

### UI/UX Considerations
- Clean, modern dashboard design
- Interactive charts with tooltips
- Mobile-responsive layout
- Color-coded performance indicators
- Easy-to-understand metrics
