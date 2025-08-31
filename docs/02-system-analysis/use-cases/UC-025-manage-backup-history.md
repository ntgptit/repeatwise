# UC-025: Manage Backup History

## 1. Use Case Information

**Use Case ID**: UC-025
**Use Case Name**: Manage Backup History
**Primary Actor**: Student (Người học)
**Secondary Actors**: Backup Service
**Priority**: Low
**Complexity**: Low

## 2. Brief Description

Student quản lý lịch sử backup bao gồm xem danh sách backup, tải xuống, xóa và quản lý các backup đã tạo.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Student có ít nhất 1 backup đã tạo
- Có kết nối internet

## 4. Main Flow

### Step 1: Access Backup History
**Actor Action**: Student mở màn hình Settings và chọn "Lịch sử Backup"
**System Response**: Hiển thị danh sách backup với thông tin cơ bản

### Step 2: View Backup List
**Actor Action**: System tự động
**System Response**: Hiển thị danh sách backup:
- Backup ID và tên
- Ngày tạo
- Kích thước file
- Loại backup
- Trạng thái
- Vị trí lưu trữ

### Step 3: Filter and Search
**Actor Action**: Student sử dụng filter và search:
- Filter theo ngày tạo
- Filter theo loại backup
- Filter theo trạng thái
- Search theo tên backup
**System Response**: Cập nhật danh sách theo filter

### Step 4: View Backup Details
**Actor Action**: Student chọn backup để xem chi tiết
**System Response**: Hiển thị thông tin chi tiết:
- Thông tin backup đầy đủ
- Nội dung backup
- Metadata
- Access logs

### Step 5: Select Backup Action
**Actor Action**: Student chọn action cho backup:
- Download backup
- Restore từ backup
- Delete backup
- Share backup
- View backup report
**System Response**: Hiển thị options tương ứng

### Step 6: Execute Selected Action
**Actor Action**: Student thực hiện action đã chọn
**System Response**: 
- Validate action request
- Hiển thị loading indicator
- Thực hiện action

### Step 7: Confirm Action Results
**Actor Action**: System tự động
**System Response**:
- Hiển thị kết quả action
- Cập nhật backup list
- Gửi confirmation notification

## 5. Alternative Flows

### A1: No Backups Available
**Trigger**: Student chưa có backup nào
**Steps**:
1. System hiển thị thông báo: "Chưa có backup nào"
2. System đề xuất tạo backup đầu tiên
3. Student có thể tạo backup hoặc quay lại
4. Return to Step 1

### A2: Backup Access Error
**Trigger**: Không thể truy cập backup
**Steps**:
1. System hiển thị thông báo lỗi: "Không thể truy cập backup"
2. System đề xuất backup khác
3. Student có thể:
   - Thử lại
   - Chọn backup khác
   - Liên hệ support
4. Continue to Step 5

### A3: Delete Backup Confirmation
**Trigger**: Student chọn xóa backup
**Steps**:
1. System hiển thị dialog xác nhận xóa
2. System cảnh báo về việc mất dữ liệu
3. Student xác nhận xóa
4. Continue to Step 6

### A4: Large Backup Download
**Trigger**: Backup file quá lớn
**Steps**:
1. System hiển thị cảnh báo: "File lớn, có thể mất thời gian"
2. System đề xuất download sau hoặc chia nhỏ
3. Student có thể:
   - Tiếp tục download
   - Chọn download sau
4. Continue to Step 6

### A5: Backup Expired
**Trigger**: Backup đã hết hạn
**Steps**:
1. System hiển thị thông báo: "Backup đã hết hạn"
2. System đề xuất tạo backup mới
3. Student có thể:
   - Tạo backup mới
   - Xóa backup cũ
4. Continue to Step 5

## 6. Post Conditions

### Success Post Conditions
- Backup list được hiển thị
- Selected action được thực hiện
- Backup history được cập nhật
- Confirmation được gửi
- UI được cập nhật

### Failure Post Conditions
- Action không được thực hiện
- Error message được hiển thị
- Backup list không thay đổi
- Student có thể thử lại

## 7. Business Rules

### BR-063: Backup History Management
- Student có thể xem tất cả backup của mình
- Backup được sắp xếp theo ngày tạo (mới nhất trước)
- Backup quá hạn tự động bị xóa
- Backup access được log để audit

### BR-064: Backup Actions
- Student có thể download backup
- Student có thể xóa backup
- Student có thể restore từ backup
- Student không thể xóa backup đang được sử dụng

### BR-065: Backup Retention
- Backup được lưu tối đa 1 năm
- Backup tự động bị xóa khi hết hạn
- Student được thông báo trước khi xóa
- Backup quan trọng có thể được extend

### BR-015: UUID Usage
- Tất cả ID sử dụng UUID format
- UUID được generate trước khi insert

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- created_at = now() khi tạo mới

## 8. Data Requirements

### Input Data
- **User ID** (UUID, required)
- **Filter Criteria** (object, optional)
- **Search Term** (string, optional)
- **Action Type** (enum, required)
- **Backup ID** (UUID, required)

### Output Data
- **Backup List** (array)
- **Backup Details** (object)
- **Action Status** (success/error)
- **Action Results** (object)

## 9. Non-Functional Requirements

### Performance
- Backup list load < 2 giây
- Backup details load < 1 giây
- Action execution < 5 giây
- Search/filter < 500ms

### Security
- Validate user ownership
- Log all backup actions
- Prevent unauthorized access
- Secure backup download

### Usability
- Intuitive backup list
- Easy filtering và search
- Clear action buttons
- Responsive design

## 10. Acceptance Criteria

### AC-001: View Backup History
**Given** student có backup history
**When** student mở backup history
**Then** danh sách backup được hiển thị
**And** thông tin backup đầy đủ

### AC-002: Filter Backups
**Given** student sử dụng filter
**When** student chọn filter criteria
**Then** backup list được filter
**And** kết quả chính xác

### AC-003: Download Backup
**Given** student chọn download backup
**When** student thực hiện download
**Then** backup file được download
**And** download log được ghi

### AC-004: Delete Backup
**Given** student chọn xóa backup
**When** student xác nhận xóa
**Then** backup được xóa
**And** backup list được cập nhật

### AC-005: Backup Expired
**Given** backup đã hết hạn
**When** student truy cập backup
**Then** thông báo hết hạn hiển thị
**And** option tạo backup mới được cung cấp

## 11. Test Cases

### TC-001: View Backup List
**Test Data**: User with 5 backups
**Expected Result**: All backups displayed correctly

### TC-002: Filter Backups
**Test Data**: Filter by date range
**Expected Result**: Filtered results displayed

### TC-003: Download Backup
**Test Data**: Valid backup file
**Expected Result**: Backup downloaded successfully

### TC-004: Delete Backup
**Test Data**: Backup not in use
**Expected Result**: Backup deleted, list updated

### TC-005: Expired Backup
**Test Data**: Backup older than 1 year
**Expected Result**: Expiration notice displayed

## 12. Related Use Cases

- **UC-023**: Backup Learning Data
- **UC-024**: Restore Learning Data
- **UC-018**: Export Learning Data
- **UC-021**: View System Settings

## 13. Notes

### Implementation Notes
- Implement backup list pagination
- Add backup search functionality
- Implement backup action logging
- Add backup expiration handling

### Future Enhancements
- Backup analytics dashboard
- Automated backup cleanup
- Backup sharing features
- Backup comparison tools
