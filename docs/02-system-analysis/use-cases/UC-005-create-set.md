# UC-005: Create New Set

## 1. Use Case Information

**Use Case ID**: UC-005
**Use Case Name**: Create New Set
**Primary Actor**: Student (Người học)
**Secondary Actors**: None
**Priority**: High
**Complexity**: Low

## 2. Brief Description

Student tạo set học tập mới với thông tin cơ bản như tên, mô tả, category và số lượng từ vựng. Hệ thống validate thông tin và tạo set với trạng thái ban đầu.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Student có quyền tạo set (chưa đạt giới hạn số set)
- Mobile app hoạt động bình thường
- Có kết nối internet

## 4. Main Flow

### Step 1: Access Create Set Form
**Actor Action**: Student chọn "Tạo set mới" từ menu hoặc dashboard
**System Response**: Hiển thị form tạo set với các trường bắt buộc

### Step 2: Input Set Information
**Actor Action**: Student nhập thông tin set:
- Tên set (≤ 100 ký tự)
- Mô tả (≤ 500 ký tự, optional)
- Category (vocabulary, grammar, mixed, other)
- Số từ vựng (> 0)
**System Response**: Validate real-time input và hiển thị feedback

### Step 3: Preview Set Information
**Actor Action**: Student xem preview thông tin set
**System Response**: Hiển thị summary của set sắp tạo

### Step 4: Submit Set Creation
**Actor Action**: Student nhấn "Tạo set"
**System Response**: 
- Validate tất cả thông tin
- Kiểm tra giới hạn số set của user
- Tạo set với UUID
- Set status = 'not_started'
- Set current_cycle = 1
- Set user_id = current user

### Step 5: Complete Set Creation
**Actor Action**: System tự động
**System Response**:
- Hiển thị thông báo thành công
- Chuyển đến màn hình set details
- Hiển thị hướng dẫn bắt đầu học

## 5. Alternative Flows

### A1: Set Name Validation Failed
**Trigger**: Tên set trống hoặc quá dài
**Steps**:
1. System hiển thị thông báo lỗi: "Tên set không được trống và tối đa 100 ký tự"
2. System highlight trường tên set
3. Student nhập lại tên set
4. Return to Step 2

### A2: Word Count Validation Failed
**Trigger**: Số từ vựng ≤ 0 hoặc quá lớn
**Steps**:
1. System hiển thị thông báo lỗi: "Số từ vựng phải lớn hơn 0 và tối đa 10,000"
2. System highlight trường số từ vựng
3. Student nhập lại số từ vựng
4. Return to Step 2

### A3: Category Validation Failed
**Trigger**: Category không hợp lệ
**Steps**:
1. System hiển thị thông báo lỗi: "Vui lòng chọn category hợp lệ"
2. System highlight trường category
3. Student chọn lại category
4. Return to Step 2

### A4: Set Limit Exceeded
**Trigger**: User đã đạt giới hạn số set (100 sets)
**Steps**:
1. System hiển thị thông báo: "Bạn đã đạt giới hạn số set. Vui lòng xóa set cũ hoặc nâng cấp tài khoản"
2. System hiển thị danh sách set hiện tại
3. Student có thể:
   - Xóa set cũ
   - Chọn set để học thay vì tạo mới
4. Return to Step 1

### A5: Network Connection Error
**Trigger**: Mất kết nối internet trong quá trình tạo set
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System lưu draft form data locally
3. Student có thể:
   - Thử lại khi có kết nối
   - Lưu form để tạo sau
4. Return to Step 1

### A6: Duplicate Set Name
**Trigger**: Tên set đã tồn tại cho user này
**Steps**:
1. System hiển thị thông báo: "Tên set đã tồn tại. Vui lòng chọn tên khác"
2. System highlight trường tên set
3. Student nhập tên set khác
4. Return to Step 2

## 6. Post Conditions

### Success Post Conditions
- Set mới được tạo thành công với UUID
- Set có trạng thái 'not_started'
- Set có current_cycle = 1
- Set được liên kết với user hiện tại
- User được chuyển đến màn hình set details
- Hướng dẫn bắt đầu học được hiển thị

### Failure Post Conditions
- Set không được tạo
- Form data được giữ lại (nếu có lỗi validation)
- Error message được hiển thị
- User có thể thử lại hoặc hủy

## 7. Business Rules

### BR-001: Set Creation
- Tên set bắt buộc và ≤ 100 ký tự
- Mô tả ≤ 500 ký tự (optional)
- Số từ vựng > 0
- Category phải thuộc: vocabulary, grammar, mixed, other
- Trạng thái ban đầu: not_started
- Current cycle = 1

### BR-015: UUID Usage
- Tất cả ID sử dụng UUID format
- UUID được generate trước khi insert

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- created_at = now() khi tạo mới

### BR-017: Soft Delete
- Chỉ soft delete (set deleted_at = now())
- Không xóa dữ liệu liên quan

## 8. Data Requirements

### Input Data
- **Set Name** (string, required, ≤ 100 chars)
- **Description** (string, optional, ≤ 500 chars)
- **Category** (enum: vocabulary, grammar, mixed, other)
- **Word Count** (integer, required, > 0)

### Output Data
- **Set ID** (UUID)
- **Creation Status** (success/error)
- **Set Details** (name, description, category, word_count, status)
- **Error Messages** (if any)

## 9. Non-Functional Requirements

### Performance
- Set creation process < 3 giây
- Form validation < 500ms
- Real-time validation feedback

### Security
- User chỉ có thể tạo set cho chính mình
- Validate user permissions
- Log set creation activities

### Usability
- Form validation real-time
- Clear error messages
- Auto-save draft
- Preview before creation

## 10. Acceptance Criteria

### AC-001: Successful Set Creation
**Given** student đã đăng nhập
**When** student tạo set với thông tin hợp lệ
**Then** set được tạo với status "not_started"
**And** set có UUID duy nhất
**And** user có thể bắt đầu học set

### AC-002: Set Name Validation
**Given** student nhập tên set không hợp lệ
**When** student submit form
**Then** system hiển thị thông báo lỗi
**And** form không được submit

### AC-003: Word Count Validation
**Given** student nhập số từ vựng ≤ 0
**When** student submit form
**Then** system hiển thị thông báo lỗi
**And** form không được submit

### AC-004: Category Validation
**Given** student không chọn category
**When** student submit form
**Then** system hiển thị thông báo lỗi
**And** form không được submit

### AC-005: Set Limit Check
**Given** user đã đạt giới hạn số set
**When** student tạo set mới
**Then** system hiển thị thông báo giới hạn
**And** form không được submit

### AC-006: Duplicate Name Check
**Given** tên set đã tồn tại cho user
**When** student tạo set với tên trùng
**Then** system hiển thị thông báo trùng lặp
**And** form không được submit

## 11. Test Cases

### TC-001: Valid Set Creation
**Test Data**: Valid name, description, category, word count
**Expected Result**: Set created, status = not_started, redirect to set details

### TC-002: Empty Set Name
**Test Data**: Empty set name
**Expected Result**: Validation error, form not submitted

### TC-003: Long Set Name
**Test Data**: Set name > 100 characters
**Expected Result**: Validation error, form not submitted

### TC-004: Invalid Word Count
**Test Data**: Word count = 0
**Expected Result**: Validation error, form not submitted

### TC-005: Invalid Category
**Test Data**: Invalid category selection
**Expected Result**: Validation error, form not submitted

### TC-006: Set Limit Exceeded
**Test Data**: User has 100 sets already
**Expected Result**: Limit exceeded message, form not submitted

### TC-007: Duplicate Set Name
**Test Data**: Set name already exists for user
**Expected Result**: Duplicate name error, form not submitted

### TC-008: Network Error
**Test Data**: Simulate network timeout
**Expected Result**: Network error message, retry option

## 12. Related Use Cases

- **UC-006**: Edit Set Information
- **UC-007**: Delete Set
- **UC-008**: View Set List
- **UC-009**: View Set Details
- **UC-010**: Start Learning Cycle

## 13. Notes

### Implementation Notes
- Validate set name uniqueness per user
- Check user set limit before creation
- Generate UUID for set_id
- Set default values for status and current_cycle
- Log set creation activity

### Future Enhancements
- Template sets for common categories
- Import sets from external sources
- Set sharing between users
- Advanced set configuration options

### UI/UX Considerations
- Show character count for name and description
- Provide category icons and descriptions
- Auto-suggest set names based on category
- Preview set information before creation
