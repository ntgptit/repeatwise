# UC-006: Edit Set Information

## 1. Use Case Information

**Use Case ID**: UC-006
**Use Case Name**: Edit Set Information
**Primary Actor**: Student (Người học)
**Secondary Actors**: None
**Priority**: Medium
**Complexity**: Low

## 2. Brief Description

Student chỉnh sửa thông tin của set học tập đã tạo, bao gồm tên, mô tả, category và số từ vựng. Hệ thống validate thông tin và cập nhật set.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Set tồn tại và thuộc về user
- Set chưa bị xóa (deleted_at = null)
- User có quyền chỉnh sửa set

## 4. Main Flow

### Step 1: Access Set Management
**Actor Action**: Student chọn set cần chỉnh sửa từ danh sách
**System Response**: Hiển thị trang chi tiết set với thông tin hiện tại

### Step 2: View Current Set Information
**Actor Action**: Student xem thông tin set hiện tại
**System Response**: Hiển thị:
- Tên set
- Mô tả
- Category
- Số từ vựng
- Trạng thái set
- Ngày tạo
- Tiến trình học tập

### Step 3: Initiate Edit Mode
**Actor Action**: Student chọn "Chỉnh sửa" set
**System Response**: Hiển thị form chỉnh sửa với dữ liệu hiện tại

### Step 4: Update Set Information
**Actor Action**: Student cập nhật:
- Tên set (≤ 100 ký tự)
- Mô tả (≤ 500 ký tự)
- Category (vocabulary, grammar, mixed, other)
- Số từ vựng (> 0)
**System Response**: Validate real-time input và hiển thị feedback

### Step 5: Validate Changes
**Actor Action**: Student nhấn "Lưu thay đổi"
**System Response**:
- Validate tất cả thông tin
- Kiểm tra quyền chỉnh sửa
- Kiểm tra xung đột với learning cycle

### Step 6: Save Set Changes
**Actor Action**: System tự động
**System Response**:
- Cập nhật set information
- Lưu vào activity_logs
- Cập nhật updated_at timestamp
- Hiển thị thông báo thành công

### Step 7: Complete Edit
**Actor Action**: System tự động
**System Response**:
- Refresh set display với thông tin mới
- Hiển thị thông báo cập nhật thành công
- Cập nhật danh sách set nếu cần

## 5. Alternative Flows

### A1: Invalid Set Name
**Trigger**: Tên set không hợp lệ
**Steps**:
1. System hiển thị thông báo lỗi: "Tên set không được để trống và tối đa 100 ký tự"
2. System highlight trường tên set
3. Student nhập lại tên set
4. Return to Step 4

### A2: Invalid Description
**Trigger**: Mô tả quá dài
**Steps**:
1. System hiển thị thông báo lỗi: "Mô tả tối đa 500 ký tự"
2. System highlight trường mô tả
3. Student rút ngắn mô tả
4. Return to Step 4

### A3: Invalid Word Count
**Trigger**: Số từ vựng không hợp lệ
**Steps**:
1. System hiển thị thông báo lỗi: "Số từ vựng phải lớn hơn 0"
2. System highlight trường số từ vựng
3. Student nhập lại số từ vựng
4. Return to Step 4

### A4: Set in Active Learning Cycle
**Trigger**: Set đang trong chu kỳ học tập active
**Steps**:
1. System hiển thị thông báo: "Không thể chỉnh sửa set đang trong chu kỳ học tập"
2. System hiển thị thông tin chu kỳ hiện tại
3. Student có thể:
   - Hoàn thành chu kỳ trước
   - Hủy chỉnh sửa
4. Return to Step 2

### A5: Set Not Found
**Trigger**: Set không tồn tại hoặc đã bị xóa
**Steps**:
1. System hiển thị thông báo: "Set không tồn tại"
2. System chuyển user về danh sách set
3. Student có thể chọn set khác
4. Return to Step 1

### A6: No Changes Made
**Trigger**: Student không thay đổi thông tin nào
**Steps**:
1. System hiển thị thông báo: "Không có thay đổi nào được thực hiện"
2. Student có thể tiếp tục chỉnh sửa hoặc quay lại
3. Return to Step 2

### A7: Network Connection Error
**Trigger**: Mất kết nối internet
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System lưu draft changes locally
3. Student có thể thử lại khi có kết nối
4. Return to Step 1

### A8: Permission Denied
**Trigger**: User không có quyền chỉnh sửa set
**Steps**:
1. System hiển thị thông báo: "Bạn không có quyền chỉnh sửa set này"
2. System chuyển user về danh sách set
3. Student có thể chọn set khác
4. Return to Step 1

## 6. Post Conditions

### Success Post Conditions
- Set information được cập nhật thành công
- Thông tin mới được lưu vào database
- Activity log được tạo
- Set display được refresh

### Failure Post Conditions
- Set không được thay đổi
- Error message được hiển thị
- User có thể thử lại hoặc hủy thay đổi

## 7. Business Rules

### BR-001: Set Management
- User chỉ có thể chỉnh sửa set của mình
- Set phải tồn tại và không bị xóa
- Tên set: bắt buộc, ≤ 100 ký tự
- Mô tả: optional, ≤ 500 ký tự

### BR-002: Set Category
- Category phải là một trong: vocabulary, grammar, mixed, other
- Category dùng cho thống kê và lọc

### BR-003: Word Count Validation
- Số từ vựng phải > 0
- Số từ vựng ảnh hưởng đến SRS algorithm
- Không thể thay đổi khi set đang trong chu kỳ active

### BR-004: Learning Cycle Protection
- Không thể chỉnh sửa set đang trong chu kỳ học tập
- Phải hoàn thành chu kỳ trước khi chỉnh sửa
- Bảo vệ tính toàn vẹn dữ liệu học tập

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- updated_at = now() khi cập nhật
- Lưu lịch sử thay đổi vào activity_logs

## 8. Data Requirements

### Input Data
- **Set ID** (UUID, required)
- **Set Name** (string, required, ≤ 100 chars)
- **Description** (string, optional, ≤ 500 chars)
- **Category** (enum: vocabulary, grammar, mixed, other, required)
- **Word Count** (integer, required, > 0)

### Output Data
- **Update Status** (success/error)
- **Updated Set Info** (new values)
- **Error Messages** (if any)
- **Activity Log Entry** (who, when, what changed)

## 9. Non-Functional Requirements

### Performance
- Set load < 2 giây
- Set update < 3 giây
- Form validation < 500ms

### Security
- User chỉ có thể chỉnh sửa set của mình
- Validate set ownership
- Log all set changes

### Usability
- Form validation real-time
- Clear error messages
- Auto-save draft changes
- Responsive design

## 10. Acceptance Criteria

### AC-001: Successful Set Edit
**Given** user có set hợp lệ
**When** user chỉnh sửa thông tin set
**Then** set được cập nhật thành công
**And** thông tin mới được lưu
**And** activity log được tạo

### AC-002: Invalid Set Name
**Given** user nhập tên set không hợp lệ
**When** user submit form
**Then** system hiển thị thông báo lỗi
**And** set không được cập nhật

### AC-003: Invalid Word Count
**Given** user nhập số từ vựng ≤ 0
**When** user submit form
**Then** system hiển thị thông báo lỗi
**And** set không được cập nhật

### AC-004: Active Learning Cycle
**Given** set đang trong chu kỳ học tập
**When** user thử chỉnh sửa set
**Then** system hiển thị thông báo không thể chỉnh sửa
**And** user được hướng dẫn hoàn thành chu kỳ

### AC-005: No Changes Made
**Given** user không thay đổi thông tin nào
**When** user submit form
**Then** system hiển thị thông báo không có thay đổi
**And** set không được cập nhật

## 11. Test Cases

### TC-001: Valid Set Edit
**Test Data**: Valid name, description, category, word count
**Expected Result**: Set updated successfully, activity logged

### TC-002: Invalid Name
**Test Data**: Empty name or name > 100 characters
**Expected Result**: Validation error, set not updated

### TC-003: Invalid Word Count
**Test Data**: Word count = 0 or negative
**Expected Result**: Validation error, set not updated

### TC-004: Active Learning Cycle
**Test Data**: Set with status 'learning' or 'reviewing'
**Expected Result**: Cannot edit message, set not updated

### TC-005: No Changes
**Test Data**: Same values as current set
**Expected Result**: No changes message, set not updated

### TC-006: Network Timeout
**Test Data**: Simulate network timeout
**Expected Result**: Network error message, retry option

## 12. Related Use Cases

- **UC-005**: Create New Set
- **UC-007**: Delete Set
- **UC-008**: View Set List
- **UC-009**: View Set Details
- **UC-010**: Start Learning Cycle

## 13. Notes

### Implementation Notes
- Validate set ownership trước khi cho phép edit
- Check learning cycle status trước khi update
- Implement auto-save cho draft changes
- Log all set changes for audit

### Future Enhancements
- Bulk edit multiple sets
- Set templates
- Advanced set properties
- Set sharing capabilities

### UI/UX Considerations
- Clean, intuitive form design
- Real-time validation feedback
- Clear success/error messages
- Responsive design for mobile
- Auto-save indicators
