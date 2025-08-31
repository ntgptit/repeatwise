# UC-007: Delete Set

## 1. Use Case Information

**Use Case ID**: UC-007
**Use Case Name**: Delete Set
**Primary Actor**: Student (Người học)
**Secondary Actors**: None
**Priority**: Medium
**Complexity**: Low

## 2. Brief Description

Student xóa set học tập khỏi hệ thống. Hệ thống thực hiện soft delete, lưu trữ dữ liệu để có thể khôi phục và cập nhật các thống kê liên quan.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Set tồn tại và thuộc về user
- Set chưa bị xóa (deleted_at = null)
- User có quyền xóa set

## 4. Main Flow

### Step 1: Access Set Management
**Actor Action**: Student chọn set cần xóa từ danh sách
**System Response**: Hiển thị trang chi tiết set với thông tin hiện tại

### Step 2: View Set Information
**Actor Action**: Student xem thông tin set trước khi xóa
**System Response**: Hiển thị:
- Tên set và mô tả
- Category và số từ vựng
- Trạng thái set
- Tiến trình học tập
- Ngày tạo và cập nhật cuối

### Step 3: Initiate Delete Process
**Actor Action**: Student chọn "Xóa set"
**System Response**: Hiển thị confirmation dialog với thông tin set

### Step 4: Confirm Deletion
**Actor Action**: Student xác nhận xóa set
**System Response**:
- Validate quyền xóa set
- Kiểm tra xung đột với learning cycle
- Hiển thị warning về hậu quả

### Step 5: Execute Soft Delete
**Actor Action**: Student nhấn "Xác nhận xóa"
**System Response**:
- Set deleted_at = now()
- Cập nhật updated_at timestamp
- Lưu vào activity_logs
- Cập nhật thống kê user

### Step 6: Complete Deletion
**Actor Action**: System tự động
**System Response**:
- Hiển thị thông báo xóa thành công
- Chuyển user về danh sách set
- Cập nhật danh sách set (ẩn set đã xóa)

### Step 7: Update Statistics
**Actor Action**: System tự động
**System Response**:
- Cập nhật tổng số set của user
- Cập nhật thống kê category
- Refresh dashboard nếu cần

## 5. Alternative Flows

### A1: Set in Active Learning Cycle
**Trigger**: Set đang trong chu kỳ học tập active
**Steps**:
1. System hiển thị thông báo: "Không thể xóa set đang trong chu kỳ học tập"
2. System hiển thị thông tin chu kỳ hiện tại
3. Student có thể:
   - Hoàn thành chu kỳ trước
   - Hủy xóa
4. Return to Step 2

### A2: Set Has Learning History
**Trigger**: Set có lịch sử học tập
**Steps**:
1. System hiển thị warning: "Set này có lịch sử học tập, xóa sẽ mất dữ liệu"
2. System hiển thị tóm tắt lịch sử
3. Student có thể:
   - Xác nhận xóa (mất dữ liệu)
   - Hủy xóa
4. Continue to Step 5

### A3: Set Not Found
**Trigger**: Set không tồn tại hoặc đã bị xóa
**Steps**:
1. System hiển thị thông báo: "Set không tồn tại"
2. System chuyển user về danh sách set
3. Student có thể chọn set khác
4. Return to Step 1

### A4: Permission Denied
**Trigger**: User không có quyền xóa set
**Steps**:
1. System hiển thị thông báo: "Bạn không có quyền xóa set này"
2. System chuyển user về danh sách set
3. Student có thể chọn set khác
4. Return to Step 1

### A5: User Cancels Deletion
**Trigger**: Student hủy quá trình xóa
**Steps**:
1. Student chọn "Hủy" trong confirmation dialog
2. System đóng dialog
3. Student quay lại trang chi tiết set
4. Return to Step 2

### A6: Network Connection Error
**Trigger**: Mất kết nối internet
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System lưu delete request locally
3. Student có thể thử lại khi có kết nối
4. Return to Step 1

### A7: Last Set Deletion
**Trigger**: User xóa set cuối cùng
**Steps**:
1. System hiển thị warning: "Đây là set cuối cùng, xóa sẽ không còn set nào"
2. System gợi ý tạo set mới
3. Student có thể:
   - Xác nhận xóa
   - Tạo set mới trước
4. Continue to Step 5

## 6. Post Conditions

### Success Post Conditions
- Set được soft delete thành công
- deleted_at timestamp được set
- Activity log được tạo
- Thống kê user được cập nhật
- Set không hiển thị trong danh sách

### Failure Post Conditions
- Set không được xóa
- Error message được hiển thị
- User có thể thử lại hoặc hủy

## 7. Business Rules

### BR-001: Set Management
- User chỉ có thể xóa set của mình
- Set phải tồn tại và không bị xóa
- Soft delete: set deleted_at = now()

### BR-004: Learning Cycle Protection
- Không thể xóa set đang trong chu kỳ học tập
- Phải hoàn thành chu kỳ trước khi xóa
- Bảo vệ tính toàn vẹn dữ liệu học tập

### BR-005: Data Preservation
- Không xóa dữ liệu liên quan trong review_histories
- Không xóa dữ liệu liên quan trong remind_schedules
- Lưu trữ để có thể khôi phục

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at, deleted_at
- deleted_at = now() khi xóa
- Lưu lịch sử xóa vào activity_logs

### BR-017: Statistics Update
- Cập nhật tổng số set của user
- Cập nhật thống kê category
- Cập nhật dashboard metrics

## 8. Data Requirements

### Input Data
- **Set ID** (UUID, required)
- **User ID** (UUID, required)
- **Delete Reason** (string, optional, ≤ 500 chars)

### Output Data
- **Delete Status** (success/error)
- **Deleted Set Info** (set details)
- **Error Messages** (if any)
- **Activity Log Entry** (who, when, reason)

## 9. Non-Functional Requirements

### Performance
- Set load < 2 giây
- Delete operation < 3 giây
- Statistics update < 1 giây

### Security
- User chỉ có thể xóa set của mình
- Validate set ownership
- Log all delete operations

### Usability
- Clear confirmation dialog
- Warning messages for data loss
- Undo option (if applicable)
- Responsive design

## 10. Acceptance Criteria

### AC-001: Successful Set Deletion
**Given** user có set hợp lệ
**When** user xác nhận xóa set
**Then** set được soft delete thành công
**And** deleted_at timestamp được set
**And** activity log được tạo

### AC-002: Active Learning Cycle
**Given** set đang trong chu kỳ học tập
**When** user thử xóa set
**Then** system hiển thị thông báo không thể xóa
**And** user được hướng dẫn hoàn thành chu kỳ

### AC-003: Set with Learning History
**Given** set có lịch sử học tập
**When** user xóa set
**Then** system hiển thị warning về mất dữ liệu
**And** user có thể xác nhận hoặc hủy

### AC-004: Last Set Deletion
**Given** user xóa set cuối cùng
**When** user xác nhận xóa
**Then** system hiển thị warning về không còn set
**And** user được gợi ý tạo set mới

### AC-005: Permission Denied
**Given** user không có quyền xóa set
**When** user thử xóa set
**Then** system hiển thị thông báo không có quyền
**And** set không được xóa

## 11. Test Cases

### TC-001: Valid Set Deletion
**Test Data**: Valid set ID, user has permission
**Expected Result**: Set soft deleted, activity logged

### TC-002: Active Learning Cycle
**Test Data**: Set with status 'learning' or 'reviewing'
**Expected Result**: Cannot delete message, set not deleted

### TC-003: Set with History
**Test Data**: Set with review history
**Expected Result**: Warning shown, user can confirm or cancel

### TC-004: Last Set
**Test Data**: User's only set
**Expected Result**: Warning about no sets remaining

### TC-005: Permission Denied
**Test Data**: Set not owned by user
**Expected Result**: Permission denied message, set not deleted

### TC-006: Network Timeout
**Test Data**: Simulate network timeout
**Expected Result**: Network error message, retry option

## 12. Related Use Cases

- **UC-005**: Create New Set
- **UC-006**: Edit Set Information
- **UC-008**: View Set List
- **UC-009**: View Set Details
- **UC-019**: View Learning Statistics

## 13. Notes

### Implementation Notes
- Validate set ownership trước khi cho phép xóa
- Check learning cycle status trước khi xóa
- Implement soft delete với deleted_at timestamp
- Log all delete operations for audit

### Future Enhancements
- Bulk delete multiple sets
- Set archive functionality
- Delete with reason tracking
- Restore deleted sets

### UI/UX Considerations
- Clear confirmation dialogs
- Warning messages for data loss
- Progress indicators
- Responsive design for mobile
- Undo functionality (if applicable)
