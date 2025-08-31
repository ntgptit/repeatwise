# UC-018: Export Learning Data

## 1. Use Case Information

**Use Case ID**: UC-018
**Use Case Name**: Export Learning Data
**Primary Actor**: Student (Người học)
**Secondary Actors**: File Service, Email Service
**Priority**: Low
**Complexity**: Medium

## 2. Brief Description

Student xuất dữ liệu học tập của mình để backup, phân tích hoặc chuyển sang hệ thống khác, hệ thống sẽ tạo file export và gửi qua email.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Student có ít nhất 1 set đã tạo
- Email đã được verified
- Có kết nối internet

## 4. Main Flow

### Step 1: Access Data Export
**Actor Action**: Student mở màn hình Settings và chọn "Xuất dữ liệu"
**System Response**: Hiển thị form export với các tùy chọn

### Step 2: Select Export Type
**Actor Action**: Student chọn loại dữ liệu muốn export:
- Tất cả dữ liệu
- Chỉ set và tiến trình
- Chỉ thống kê học tập
- Chỉ lịch sử ôn tập
**System Response**: Hiển thị preview dữ liệu sẽ export

### Step 3: Select Export Format
**Actor Action**: Student chọn format export:
- JSON (đầy đủ, có thể import lại)
- CSV (dễ đọc, phân tích)
- Excel (bảng tính, biểu đồ)
- PDF (báo cáo, in ấn)
**System Response**: Hiển thị preview format

### Step 4: Select Date Range
**Actor Action**: Student chọn khoảng thời gian:
- Tất cả dữ liệu
- 30 ngày gần nhất
- 3 tháng gần nhất
- 1 năm gần nhất
- Tùy chỉnh ngày
**System Response**: Hiển thị preview data range

### Step 5: Configure Export Options
**Actor Action**: Student cấu hình tùy chọn:
- Bao gồm metadata
- Nén file
- Mã hóa file
- Tự động xóa sau 7 ngày
**System Response**: Hiển thị preview options

### Step 6: Confirm Export
**Actor Action**: Student nhấn "Xuất dữ liệu"
**System Response**: 
- Validate export request
- Hiển thị loading indicator

### Step 7: Generate Export File
**Actor Action**: System tự động
**System Response**:
- Query dữ liệu theo criteria
- Format dữ liệu theo format chọn
- Tạo file export
- Upload file lên storage

### Step 8: Send Export Email
**Actor Action**: System tự động
**System Response**:
- Tạo email với download link
- Gửi email qua Email Service
- Log export action

### Step 9: Display Confirmation
**Actor Action**: System tự động
**System Response**:
- Hiển thị thông báo thành công
- Hiển thị download link
- Cập nhật export history

## 5. Alternative Flows

### A1: Cancel Export
**Trigger**: Student hủy bỏ export
**Steps**:
1. System đóng form export
2. System quay lại màn hình settings
3. Student có thể chọn action khác
4. Return to Step 1

### A2: Network Connection Error
**Trigger**: Mất kết nối khi generate export
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System lưu export request locally
3. Student có thể:
   - Thử lại khi có kết nối
   - Hủy bỏ export
4. Return to Step 1

### A3: Large Data Export
**Trigger**: Dữ liệu quá lớn (> 10MB)
**Steps**:
1. System hiển thị cảnh báo: "Dữ liệu lớn, có thể mất thời gian"
2. System đề xuất chia nhỏ hoặc chọn format khác
3. Student có thể:
   - Tiếp tục với thời gian dài
   - Chọn format nhỏ hơn
   - Chia nhỏ data range
4. Continue to Step 7

### A4: Email Not Verified
**Trigger**: Email chưa được verified
**Steps**:
1. System hiển thị thông báo: "Email chưa verified"
2. System yêu cầu verify email trước
3. Student có thể:
   - Verify email
   - Chọn download trực tiếp
4. Continue to Step 7

### A5: Export Limit Reached
**Trigger**: Đã đạt giới hạn export (5 lần/tháng)
**Steps**:
1. System hiển thị thông báo: "Đã đạt giới hạn export"
2. System đề xuất upgrade hoặc chờ tháng sau
3. Student có thể:
   - Upgrade account
   - Chờ tháng sau
4. Return to Step 1

## 6. Post Conditions

### Success Post Conditions
- Export file được tạo thành công
- Email với download link được gửi
- Export history được cập nhật
- File được lưu trong storage
- Confirmation được hiển thị

### Failure Post Conditions
- Export file không được tạo
- Error message được hiển thị
- Student có thể thử lại hoặc liên hệ support

## 7. Business Rules

### BR-045: Export Management
- Student có thể export tối đa 5 lần/tháng
- Export file được lưu tối đa 30 ngày
- File size tối đa 100MB
- Export yêu cầu email verified

### BR-046: Data Privacy
- Export chỉ bao gồm dữ liệu của user
- Sensitive data được mã hóa
- Download link có thời hạn
- Export được log để audit

### BR-047: Export Formats
- JSON: đầy đủ, có thể import lại
- CSV: dễ đọc, phân tích
- Excel: bảng tính, biểu đồ
- PDF: báo cáo, in ấn

### BR-015: UUID Usage
- Tất cả ID sử dụng UUID format
- UUID được generate trước khi insert

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- created_at = now() khi tạo mới

## 8. Data Requirements

### Input Data
- **Export Type** (enum, required)
- **Export Format** (enum, required)
- **Date Range** (object, required)
- **Export Options** (object, optional)
- **User ID** (UUID, required)

### Output Data
- **Export ID** (UUID)
- **File URL** (string)
- **File Size** (integer)
- **Export Status** (success/error)
- **Download Link** (string)

## 9. Non-Functional Requirements

### Performance
- Export generation < 5 phút
- File download < 2 phút
- Email delivery < 30 giây

### Security
- Validate user ownership
- Encrypt sensitive data
- Secure download links
- Log all export actions

### Usability
- Clear export options
- Progress indicator
- Download confirmation
- Export history

## 10. Acceptance Criteria

### AC-001: Successful Export
**Given** student có dữ liệu học tập
**When** student export với options hợp lệ
**Then** export file được tạo thành công
**And** email với download link được gửi

### AC-002: Large Data Export
**Given** dữ liệu > 10MB
**When** student export
**Then** warning được hiển thị
**And** export vẫn được thực hiện

### AC-003: Export Limit Reached
**Given** đã export 5 lần/tháng
**When** student cố gắng export lần thứ 6
**Then** limit error được hiển thị
**And** export không được thực hiện

### AC-004: Email Not Verified
**Given** email chưa verified
**When** student export
**Then** verification requirement được hiển thị
**And** alternative download được cung cấp

### AC-005: Network Error
**Given** mất kết nối internet
**When** student export
**Then** error message được hiển thị
**And** retry option được cung cấp

## 11. Test Cases

### TC-001: Normal Export
**Test Data**: Valid export options, small data
**Expected Result**: File created, email sent

### TC-002: Large Data Export
**Test Data**: 50MB of data
**Expected Result**: Warning displayed, export completed

### TC-003: Export Limit Reached
**Test Data**: 6th export of the month
**Expected Result**: Limit error, export denied

### TC-004: Email Not Verified
**Test Data**: Unverified email
**Expected Result**: Verification requirement, alternative offered

### TC-005: Network Error
**Test Data**: Simulate network timeout
**Expected Result**: Error message, retry option

## 12. Related Use Cases

- **UC-003**: User Profile Management
- **UC-019**: View Learning Statistics
- **UC-021**: View System Settings
- **UC-022**: Import Learning Data

## 13. Notes

### Implementation Notes
- Implement file generation service
- Add export scheduling for large data
- Implement secure file storage
- Add export analytics tracking

### Future Enhancements
- Automated backup exports
- Cloud storage integration
- Advanced data visualization
- Export templates
