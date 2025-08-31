# UC-024: Restore Learning Data

## 1. Use Case Information

**Use Case ID**: UC-024
**Use Case Name**: Restore Learning Data
**Primary Actor**: Student (Người học)
**Secondary Actors**: Backup Service, Validation Service
**Priority**: Low
**Complexity**: High

## 2. Brief Description

Student khôi phục dữ liệu học tập từ backup để khôi phục dữ liệu đã mất hoặc quay lại trạng thái trước đó.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Student có backup hợp lệ
- Có kết nối internet
- Có đủ storage space

## 4. Main Flow

### Step 1: Access Restore Function
**Actor Action**: Student mở màn hình Settings và chọn "Khôi phục dữ liệu"
**System Response**: Hiển thị danh sách backup có sẵn

### Step 2: Select Backup to Restore
**Actor Action**: Student chọn backup muốn khôi phục:
- Chọn từ backup history
- Upload backup file
- Chọn từ cloud storage
- Chọn từ local device
**System Response**: Hiển thị thông tin backup

### Step 3: Review Backup Information
**Actor Action**: System tự động
**System Response**:
- Hiển thị backup details (ngày tạo, size, scope)
- Hiển thị backup contents preview
- Hiển thị conflicts với dữ liệu hiện tại
- Hiển thị estimated restore time

### Step 4: Select Restore Options
**Actor Action**: Student cấu hình restore options:
- Restore mode (full/selective)
- Conflict resolution (overwrite/skip/merge)
- Backup current data before restore
- Restore specific components
**System Response**: Hiển thị preview restore plan

### Step 5: Confirm Restore
**Actor Action**: Student nhấn "Xác nhận Khôi phục"
**System Response**: 
- Validate restore request
- Hiển thị warning về data loss
- Yêu cầu xác nhận cuối cùng

### Step 6: Backup Current Data (Optional)
**Actor Action**: System tự động
**System Response**:
- Tạo backup dữ liệu hiện tại (nếu được chọn)
- Verify backup thành công
- Continue to restore process

### Step 7: Execute Restore
**Actor Action**: System tự động
**System Response**:
- Download backup file
- Decrypt và decompress backup
- Validate backup integrity
- Restore dữ liệu theo options

### Step 8: Resolve Conflicts
**Actor Action**: System tự động
**System Response**:
- Apply conflict resolution rules
- Log all conflicts và resolutions
- Update dữ liệu theo strategy
- Verify restore integrity

### Step 9: Complete Restore
**Actor Action**: System tự động
**System Response**:
- Update user statistics
- Rebuild indexes nếu cần
- Send confirmation notification
- Hiển thị restore summary

## 5. Alternative Flows

### A1: Cancel Restore
**Trigger**: Student hủy bỏ restore
**Steps**:
1. System đóng form restore
2. System quay lại màn hình settings
3. Student có thể chọn action khác
4. Return to Step 1

### A2: Invalid Backup File
**Trigger**: Backup file không hợp lệ hoặc bị hỏng
**Steps**:
1. System hiển thị thông báo lỗi: "Backup file không hợp lệ"
2. System đề xuất backup khác
3. Student có thể:
   - Chọn backup khác
   - Upload backup file khác
4. Return to Step 2

### A3: Insufficient Storage
**Trigger**: Không đủ storage space cho restore
**Steps**:
1. System hiển thị thông báo: "Không đủ storage"
2. System đề xuất cleanup hoặc selective restore
3. Student có thể:
   - Cleanup storage
   - Chọn selective restore
   - Chọn backup nhỏ hơn
4. Continue to Step 4

### A4: Restore Conflicts
**Trigger**: Có nhiều conflicts cần resolve
**Steps**:
1. System hiển thị danh sách conflicts
2. Student chọn resolution cho từng conflict:
   - Overwrite existing
   - Skip conflict
   - Merge data
3. System apply resolutions
4. Continue to Step 7

### A5: Restore Failure
**Trigger**: Lỗi trong quá trình restore
**Steps**:
1. System hiển thị thông báo lỗi
2. System rollback partial restore
3. Student có thể:
   - Thử lại restore
   - Chọn backup khác
   - Liên hệ support
4. Continue to Step 9

## 6. Post Conditions

### Success Post Conditions
- Dữ liệu được khôi phục thành công
- Conflicts được resolve theo rules
- Current data được backup (nếu được chọn)
- Restore report được tạo
- UI được cập nhật với dữ liệu mới

### Failure Post Conditions
- Dữ liệu không được khôi phục
- Current data không bị ảnh hưởng
- Error report được tạo
- Student có thể thử lại

## 7. Business Rules

### BR-060: Restore Management
- Student có thể restore tối đa 1 lần/giờ
- Restore yêu cầu xác nhận cuối cùng
- Restore được log để audit
- Restore không ảnh hưởng đến backup

### BR-061: Data Validation
- Backup file phải hợp lệ và không bị hỏng
- Restore data phải pass validation
- Data relationships phải được maintain
- UUID conflicts phải được resolve

### BR-062: Conflict Resolution
- Default: skip conflicts
- Student có thể chọn resolution strategy
- Conflicts được log và report
- Restore không làm mất data không được chọn

### BR-015: UUID Usage
- Tất cả ID sử dụng UUID format
- UUID được generate trước khi insert

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- created_at = now() khi tạo mới

## 8. Data Requirements

### Input Data
- **Backup File** (file, required)
- **Restore Options** (object, required)
- **Conflict Resolution** (enum, required)
- **User ID** (UUID, required)

### Output Data
- **Restore ID** (UUID)
- **Restore Status** (success/error)
- **Restore Report** (object)
- **Restored Data Summary** (object)

## 9. Non-Functional Requirements

### Performance
- Restore validation < 2 phút
- Restore execution < 15 phút
- Backup download < 5 phút
- Report generation < 1 phút

### Security
- Validate backup file integrity
- Decrypt backup data
- Log all restore actions
- Prevent unauthorized restore

### Usability
- Clear restore interface
- Progress indicator
- Conflict resolution interface
- Restore preview

## 10. Acceptance Criteria

### AC-001: Successful Restore
**Given** student có backup hợp lệ
**When** student restore dữ liệu
**Then** dữ liệu được khôi phục thành công
**And** restore report được tạo

### AC-002: Restore Conflicts
**Given** có conflicts giữa backup và current data
**When** student chọn resolution strategy
**Then** conflicts được resolve theo rules
**And** restore được hoàn thành

### AC-003: Invalid Backup File
**Given** backup file không hợp lệ
**When** student cố gắng restore
**Then** validation error được hiển thị
**And** restore không được thực hiện

### AC-004: Restore Failure
**Given** có lỗi trong quá trình restore
**When** restore được thực hiện
**Then** error report được tạo
**And** current data không bị ảnh hưởng

### AC-005: Large Data Restore
**Given** backup data > 200MB
**When** student restore
**Then** warning được hiển thị
**And** restore vẫn được thực hiện

## 11. Test Cases

### TC-001: Normal Restore
**Test Data**: Valid backup file, small data
**Expected Result**: Data restored successfully

### TC-002: Restore Conflicts
**Test Data**: Backup with conflicts
**Expected Result**: Conflicts resolved, restore completed

### TC-003: Invalid Backup File
**Test Data**: Corrupted backup file
**Expected Result**: Validation error, restore denied

### TC-004: Restore Failure
**Test Data**: Network timeout during restore
**Expected Result**: Error report generated, rollback performed

### TC-005: Large Data Restore
**Test Data**: 400MB backup data
**Expected Result**: Warning displayed, restore completed

## 12. Related Use Cases

- **UC-023**: Backup Learning Data
- **UC-022**: Import Learning Data
- **UC-025**: Manage Backup History
- **UC-019**: View Learning Statistics

## 13. Notes

### Implementation Notes
- Implement backup file validation
- Add restore progress tracking
- Implement conflict resolution logic
- Add restore rollback mechanism

### Future Enhancements
- Automated restore testing
- Cross-platform restore
- Advanced restore analytics
- Restore templates
