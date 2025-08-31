# UC-023: Backup Learning Data

## 1. Use Case Information

**Use Case ID**: UC-023
**Use Case Name**: Backup Learning Data
**Primary Actor**: Student (Người học)
**Secondary Actors**: Backup Service, Cloud Storage Service
**Priority**: Low
**Complexity**: Medium

## 2. Brief Description

Student tạo backup dữ liệu học tập để đảm bảo an toàn dữ liệu, hệ thống sẽ tạo backup tự động hoặc theo yêu cầu và lưu trữ an toàn.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Student có dữ liệu học tập
- Có kết nối internet
- Có đủ storage space

## 4. Main Flow

### Step 1: Access Backup Function
**Actor Action**: Student mở màn hình Settings và chọn "Backup dữ liệu"
**System Response**: Hiển thị form backup với các tùy chọn

### Step 2: Select Backup Type
**Actor Action**: Student chọn loại backup:
- Manual backup (ngay lập tức)
- Scheduled backup (tự động)
- Incremental backup (chỉ thay đổi)
- Full backup (toàn bộ dữ liệu)
**System Response**: Hiển thị options tương ứng

### Step 3: Configure Backup Scope
**Actor Action**: Student chọn phạm vi backup:
- Tất cả dữ liệu
- Chỉ sets và tiến trình
- Chỉ learning history
- Chỉ user preferences
**System Response**: Hiển thị preview scope

### Step 4: Select Backup Location
**Actor Action**: Student chọn vị trí backup:
- Cloud storage (Google Drive, Dropbox)
- Local device
- Email backup
- External storage
**System Response**: Hiển thị authentication interface

### Step 5: Configure Backup Options
**Actor Action**: Student cấu hình tùy chọn:
- Encryption (bật/tắt)
- Compression (bật/tắt)
- Retention period
- Auto-cleanup old backups
**System Response**: Hiển thị preview options

### Step 6: Schedule Backup (Optional)
**Actor Action**: Student cấu hình lịch backup:
- Frequency (daily/weekly/monthly)
- Time of day
- Timezone
- Notification preferences
**System Response**: Hiển thị schedule preview

### Step 7: Confirm Backup
**Actor Action**: Student nhấn "Tạo Backup"
**System Response**: 
- Validate backup request
- Hiển thị loading indicator

### Step 8: Execute Backup
**Actor Action**: System tự động
**System Response**:
- Collect dữ liệu theo scope
- Compress và encrypt (nếu được chọn)
- Upload đến backup location
- Verify backup integrity

### Step 9: Complete Backup
**Actor Action**: System tự động
**System Response**:
- Tạo backup record
- Send confirmation notification
- Update backup history
- Hiển thị backup summary

## 5. Alternative Flows

### A1: Cancel Backup
**Trigger**: Student hủy bỏ backup
**Steps**:
1. System đóng form backup
2. System quay lại màn hình settings
3. Student có thể chọn action khác
4. Return to Step 1

### A2: Authentication Required
**Trigger**: Backup location yêu cầu authentication
**Steps**:
1. System hiển thị authentication dialog
2. Student nhập credentials
3. System validate credentials
4. Continue to Step 5

### A3: Insufficient Storage
**Trigger**: Không đủ storage space
**Steps**:
1. System hiển thị thông báo: "Không đủ storage"
2. System đề xuất cleanup hoặc chọn location khác
3. Student có thể:
   - Cleanup old backups
   - Chọn location khác
   - Giảm backup scope
4. Continue to Step 7

### A4: Backup Failure
**Trigger**: Lỗi trong quá trình backup
**Steps**:
1. System hiển thị thông báo lỗi
2. System retry backup
3. Student có thể:
   - Thử lại
   - Chọn options khác
   - Liên hệ support
4. Continue to Step 9

### A5: Large Data Backup
**Trigger**: Dữ liệu backup quá lớn
**Steps**:
1. System hiển thị cảnh báo: "Dữ liệu lớn, có thể mất thời gian"
2. System đề xuất incremental backup
3. Student có thể:
   - Tiếp tục với thời gian dài
   - Chọn incremental backup
4. Continue to Step 7

## 6. Post Conditions

### Success Post Conditions
- Backup được tạo thành công
- Backup được lưu an toàn
- Backup record được tạo
- Confirmation được gửi
- Backup history được cập nhật

### Failure Post Conditions
- Backup không được tạo
- Error report được tạo
- Existing data không bị ảnh hưởng
- Student có thể thử lại

## 7. Business Rules

### BR-057: Backup Management
- Student có thể tạo tối đa 10 backup/tháng
- Backup được lưu tối đa 1 năm
- Backup size tối đa 500MB
- Backup yêu cầu authentication cho cloud storage

### BR-058: Backup Security
- Backup được mã hóa (nếu được chọn)
- Backup chỉ accessible bởi user
- Backup credentials được bảo mật
- Backup được log để audit

### BR-059: Backup Scheduling
- Scheduled backup chạy tự động
- Backup được retry nếu thất bại
- Backup notification được gửi
- Backup conflicts được resolve

### BR-015: UUID Usage
- Tất cả ID sử dụng UUID format
- UUID được generate trước khi insert

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- created_at = now() khi tạo mới

## 8. Data Requirements

### Input Data
- **Backup Type** (enum, required)
- **Backup Scope** (object, required)
- **Backup Location** (enum, required)
- **Backup Options** (object, optional)
- **Schedule** (object, optional)
- **User ID** (UUID, required)

### Output Data
- **Backup ID** (UUID)
- **Backup Status** (success/error)
- **Backup Location** (string)
- **Backup Size** (integer)
- **Backup Summary** (object)

## 9. Non-Functional Requirements

### Performance
- Backup creation < 10 phút
- Backup upload < 5 phút
- Backup verification < 2 phút
- Schedule execution < 1 phút

### Security
- Encrypt backup data
- Secure backup credentials
- Validate backup integrity
- Log all backup actions

### Usability
- Clear backup interface
- Progress indicator
- Backup history
- Easy restore option

## 10. Acceptance Criteria

### AC-001: Successful Backup
**Given** student có dữ liệu học tập
**When** student tạo backup
**Then** backup được tạo thành công
**And** backup được lưu an toàn

### AC-002: Scheduled Backup
**Given** student cấu hình scheduled backup
**When** đến thời gian backup
**Then** backup được thực hiện tự động
**And** notification được gửi

### AC-003: Cloud Storage Backup
**Given** student chọn cloud storage
**When** student authenticate
**Then** backup được upload lên cloud
**And** backup accessible từ cloud

### AC-004: Backup Failure
**Given** có lỗi trong quá trình backup
**When** backup được thực hiện
**Then** error report được tạo
**And** retry option được cung cấp

### AC-005: Large Data Backup
**Given** dữ liệu backup > 100MB
**When** student tạo backup
**Then** warning được hiển thị
**And** backup vẫn được thực hiện

## 11. Test Cases

### TC-001: Normal Backup
**Test Data**: Valid backup options, small data
**Expected Result**: Backup created successfully

### TC-002: Scheduled Backup
**Test Data**: Daily backup schedule
**Expected Result**: Backup executed automatically

### TC-003: Cloud Storage Backup
**Test Data**: Google Drive backup
**Expected Result**: Backup uploaded to cloud

### TC-004: Backup Failure
**Test Data**: Network timeout during backup
**Expected Result**: Error report generated, retry option

### TC-005: Large Data Backup
**Test Data**: 300MB backup data
**Expected Result**: Warning displayed, backup completed

## 12. Related Use Cases

- **UC-018**: Export Learning Data
- **UC-022**: Import Learning Data
- **UC-024**: Restore Learning Data
- **UC-025**: Manage Backup History

## 13. Notes

### Implementation Notes
- Implement backup encryption
- Add cloud storage integration
- Implement backup scheduling
- Add backup verification

### Future Enhancements
- Automated backup optimization
- Cross-platform backup sync
- Advanced backup analytics
- Backup sharing features
