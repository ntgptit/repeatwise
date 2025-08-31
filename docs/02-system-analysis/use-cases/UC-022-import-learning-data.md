# UC-022: Import Learning Data

## 1. Use Case Information

**Use Case ID**: UC-022
**Use Case Name**: Import Learning Data
**Primary Actor**: Student (Người học)
**Secondary Actors**: File Service, Validation Service
**Priority**: Low
**Complexity**: High

## 2. Brief Description

Student import dữ liệu học tập từ file export hoặc từ hệ thống khác để khôi phục dữ liệu hoặc chuyển đổi từ platform khác.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Student có file import hợp lệ
- Có kết nối internet
- Có đủ storage space

## 4. Main Flow

### Step 1: Access Import Function
**Actor Action**: Student mở màn hình Settings và chọn "Import dữ liệu"
**System Response**: Hiển thị form import với các tùy chọn

### Step 2: Select Import Source
**Actor Action**: Student chọn nguồn import:
- File từ thiết bị
- File từ cloud storage
- URL download
- Paste JSON data
**System Response**: Hiển thị upload interface tương ứng

### Step 3: Upload Import File
**Actor Action**: Student upload file import:
- Chọn file từ thiết bị
- Hoặc paste JSON data
- Hoặc nhập URL
**System Response**: Validate file format và hiển thị preview

### Step 4: Select Import Options
**Actor Action**: Student cấu hình import options:
- Import mode (merge/replace)
- Conflict resolution (skip/overwrite/rename)
- Data validation level
- Backup existing data
**System Response**: Hiển thị preview import plan

### Step 5: Review Import Data
**Actor Action**: System tự động
**System Response**:
- Parse và validate file
- Hiển thị summary dữ liệu
- Hiển thị conflicts nếu có
- Hiển thị estimated time

### Step 6: Confirm Import
**Actor Action**: Student nhấn "Xác nhận Import"
**System Response**: 
- Validate import request
- Hiển thị loading indicator

### Step 7: Execute Import
**Actor Action**: System tự động
**System Response**:
- Backup existing data (nếu được chọn)
- Import sets và learning data
- Resolve conflicts theo rules
- Update user statistics

### Step 8: Validate Import Results
**Actor Action**: System tự động
**System Response**:
- Validate imported data
- Check data integrity
- Generate import report
- Log import actions

### Step 9: Display Results
**Actor Action**: System tự động
**System Response**:
- Hiển thị import summary
- Hiển thị success/error details
- Cung cấp download import report
- Cập nhật UI

## 5. Alternative Flows

### A1: Cancel Import
**Trigger**: Student hủy bỏ import
**Steps**:
1. System đóng form import
2. System quay lại màn hình settings
3. Student có thể chọn action khác
4. Return to Step 1

### A2: Invalid File Format
**Trigger**: File import không đúng format
**Steps**:
1. System hiển thị thông báo lỗi: "File không đúng format"
2. System hiển thị format requirements
3. Student có thể:
   - Chọn file khác
   - Convert file format
4. Return to Step 3

### A3: Data Conflicts
**Trigger**: Có conflicts giữa dữ liệu import và existing
**Steps**:
1. System hiển thị danh sách conflicts
2. Student chọn resolution strategy:
   - Skip conflicts
   - Overwrite existing
   - Rename imported items
3. System apply resolution strategy
4. Continue to Step 7

### A4: Large Data Import
**Trigger**: Dữ liệu import quá lớn (> 50MB)
**Steps**:
1. System hiển thị cảnh báo: "Dữ liệu lớn, có thể mất thời gian"
2. System đề xuất chia nhỏ hoặc import từng phần
3. Student có thể:
   - Tiếp tục với thời gian dài
   - Chia nhỏ data
4. Continue to Step 7

### A5: Import Errors
**Trigger**: Lỗi trong quá trình import
**Steps**:
1. System hiển thị danh sách errors
2. System rollback failed imports
3. Student có thể:
   - Retry import
   - Fix errors và retry
   - Cancel import
4. Continue to Step 9

## 6. Post Conditions

### Success Post Conditions
- Dữ liệu được import thành công
- Conflicts được resolve theo rules
- Import report được tạo
- UI được cập nhật với dữ liệu mới
- Backup được tạo (nếu được chọn)

### Failure Post Conditions
- Dữ liệu không được import
- Error report được tạo
- Existing data không bị ảnh hưởng
- Student có thể thử lại hoặc liên hệ support

## 7. Business Rules

### BR-054: Import Management
- Student có thể import tối đa 1 lần/giờ
- Import file size tối đa 100MB
- Import yêu cầu validation đầy đủ
- Import được log để audit

### BR-055: Data Validation
- Import data phải đúng format
- Required fields phải có giá trị
- Data relationships phải hợp lệ
- UUID conflicts phải được resolve

### BR-056: Conflict Resolution
- Default: skip conflicts
- Student có thể chọn resolution strategy
- Conflicts được log và report
- Import không làm mất existing data

### BR-015: UUID Usage
- Tất cả ID sử dụng UUID format
- UUID được generate trước khi insert

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- created_at = now() khi tạo mới

## 8. Data Requirements

### Input Data
- **Import File** (file, required)
- **Import Options** (object, required)
- **Conflict Resolution** (enum, required)
- **User ID** (UUID, required)

### Output Data
- **Import ID** (UUID)
- **Import Status** (success/error)
- **Import Report** (object)
- **Imported Data Summary** (object)

## 9. Non-Functional Requirements

### Performance
- Import validation < 30 giây
- Import execution < 10 phút
- File upload < 5 phút
- Report generation < 1 phút

### Security
- Validate file format và content
- Sanitize imported data
- Log all import actions
- Prevent data injection

### Usability
- Clear import interface
- Progress indicator
- Detailed error messages
- Import preview

## 10. Acceptance Criteria

### AC-001: Successful Import
**Given** student có file import hợp lệ
**When** student import dữ liệu
**Then** dữ liệu được import thành công
**And** import report được tạo

### AC-002: Data Conflicts
**Given** có conflicts giữa dữ liệu import và existing
**When** student chọn resolution strategy
**Then** conflicts được resolve theo rules
**And** import được hoàn thành

### AC-003: Invalid File Format
**Given** file import không đúng format
**When** student upload file
**Then** validation error được hiển thị
**And** format requirements được cung cấp

### AC-004: Large Data Import
**Given** dữ liệu import > 50MB
**When** student import
**Then** warning được hiển thị
**And** import vẫn được thực hiện

### AC-005: Import Errors
**Given** có lỗi trong quá trình import
**When** import được thực hiện
**Then** error report được tạo
**And** existing data không bị ảnh hưởng

## 11. Test Cases

### TC-001: Normal Import
**Test Data**: Valid import file, small data
**Expected Result**: Data imported successfully

### TC-002: Data Conflicts
**Test Data**: Import file with conflicts
**Expected Result**: Conflicts resolved, import completed

### TC-003: Invalid File Format
**Test Data**: Invalid file format
**Expected Result**: Validation error, format requirements shown

### TC-004: Large Data Import
**Test Data**: 80MB import file
**Expected Result**: Warning displayed, import completed

### TC-005: Import Errors
**Test Data**: File with validation errors
**Expected Result**: Error report generated, rollback performed

## 12. Related Use Cases

- **UC-018**: Export Learning Data
- **UC-019**: View Learning Statistics
- **UC-021**: View System Settings
- **UC-023**: Backup Learning Data

## 13. Notes

### Implementation Notes
- Implement file validation service
- Add import progress tracking
- Implement conflict resolution logic
- Add import rollback mechanism

### Future Enhancements
- Automated import scheduling
- Cloud storage integration
- Advanced data mapping
- Import templates
