# UC-008: View Set List

## 1. Use Case Information

**Use Case ID**: UC-008
**Use Case Name**: View Set List
**Primary Actor**: Student (Người học)
**Secondary Actors**: None
**Priority**: High
**Complexity**: Low

## 2. Brief Description

Student xem danh sách tất cả set học tập của mình với thông tin tổng quan, có thể lọc, sắp xếp và tìm kiếm. Hệ thống hiển thị thông tin cơ bản và trạng thái của từng set.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- User có quyền truy cập danh sách set
- Có ít nhất một set trong hệ thống (hoặc hiển thị empty state)

## 4. Main Flow

### Step 1: Access Set List
**Actor Action**: Student chọn "Danh sách set" từ menu chính
**System Response**: Hiển thị trang danh sách set với tất cả set của user

### Step 2: View Set Overview
**Actor Action**: Student xem danh sách set
**System Response**: Hiển thị cho mỗi set:
- Tên set và mô tả ngắn
- Category (vocabulary, grammar, mixed, other)
- Số từ vựng
- Trạng thái (not_started, learning, reviewing, mastered)
- Tiến trình học tập (chu kỳ hiện tại)
- Ngày tạo và cập nhật cuối

### Step 3: Navigate Set List
**Actor Action**: Student cuộn và xem các set
**System Response**: 
- Hiển thị pagination nếu có nhiều set
- Load more sets khi cuộn xuống
- Hiển thị loading indicator khi cần

### Step 4: Select Set Actions
**Actor Action**: Student chọn action cho set cụ thể
**System Response**: Hiển thị menu actions:
- Xem chi tiết
- Chỉnh sửa
- Xóa
- Bắt đầu học (nếu chưa học)

### Step 5: Apply Filters (Optional)
**Actor Action**: Student chọn filter theo category hoặc trạng thái
**System Response**: Cập nhật danh sách theo filter đã chọn

### Step 6: Search Sets (Optional)
**Actor Action**: Student nhập từ khóa tìm kiếm
**System Response**: Hiển thị kết quả tìm kiếm real-time

## 5. Alternative Flows

### A1: No Sets Available
**Trigger**: User chưa có set nào
**Steps**:
1. System hiển thị empty state: "Bạn chưa có set nào"
2. System hiển thị nút "Tạo set đầu tiên"
3. Student có thể tạo set mới
4. Return to Step 1

### A2: Filter by Category
**Trigger**: Student chọn filter theo category
**Steps**:
1. Student chọn category: vocabulary, grammar, mixed, other
2. System cập nhật danh sách chỉ hiển thị set thuộc category
3. System hiển thị số lượng kết quả
4. Continue to Step 2

### A3: Filter by Status
**Trigger**: Student chọn filter theo trạng thái
**Steps**:
1. Student chọn status: not_started, learning, reviewing, mastered
2. System cập nhật danh sách chỉ hiển thị set có status tương ứng
3. System hiển thị số lượng kết quả
4. Continue to Step 2

### A4: Search by Name
**Trigger**: Student tìm kiếm theo tên set
**Steps**:
1. Student nhập từ khóa vào ô tìm kiếm
2. System tìm kiếm real-time trong tên và mô tả set
3. System hiển thị kết quả tìm kiếm
4. Continue to Step 2

### A5: Sort Sets
**Trigger**: Student chọn sắp xếp danh sách
**Steps**:
1. Student chọn sort option: tên, ngày tạo, trạng thái, tiến trình
2. System sắp xếp danh sách theo option đã chọn
3. System hiển thị danh sách đã sắp xếp
4. Continue to Step 2

### A6: Large Dataset
**Trigger**: User có rất nhiều set (> 50)
**Steps**:
1. System hiển thị pagination hoặc infinite scroll
2. System load sets theo batch (20 set/lần)
3. System hiển thị loading indicator khi load thêm
4. Continue to Step 2

### A7: Network Connection Error
**Trigger**: Mất kết nối internet
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System hiển thị dữ liệu cached (nếu có)
3. Student có thể thử lại khi có kết nối
4. Return to Step 1

### A8: Permission Denied
**Trigger**: User không có quyền xem danh sách set
**Steps**:
1. System hiển thị thông báo: "Bạn không có quyền truy cập"
2. System chuyển user về trang chính
3. Student có thể liên hệ admin
4. Return to Step 1

## 6. Post Conditions

### Success Post Conditions
- Danh sách set được hiển thị đầy đủ
- User có thể thực hiện các action trên set
- Filter và search hoạt động chính xác
- Pagination hoạt động nếu cần

### Failure Post Conditions
- Danh sách không được hiển thị
- Error message được hiển thị
- User có thể thử lại hoặc liên hệ support

## 7. Business Rules

### BR-001: Set Management
- User chỉ có thể xem set của mình
- Set bị xóa (deleted_at ≠ null) không hiển thị
- Hiển thị thông tin cơ bản của mỗi set

### BR-002: Set Display
- Tên set: hiển thị đầy đủ hoặc truncate với "..."
- Mô tả: hiển thị tối đa 100 ký tự
- Category: hiển thị với icon và màu sắc
- Trạng thái: hiển thị với badge màu

### BR-003: Set Filtering
- Filter theo category: vocabulary, grammar, mixed, other
- Filter theo status: not_started, learning, reviewing, mastered
- Kết hợp nhiều filter cùng lúc
- Clear all filters option

### BR-004: Set Search
- Tìm kiếm trong tên set và mô tả
- Tìm kiếm không phân biệt hoa thường
- Tìm kiếm real-time với debounce
- Highlight từ khóa tìm kiếm

### BR-005: Set Sorting
- Sắp xếp theo tên (A-Z, Z-A)
- Sắp xếp theo ngày tạo (mới nhất, cũ nhất)
- Sắp xếp theo trạng thái
- Sắp xếp theo tiến trình học tập

## 8. Data Requirements

### Input Data
- **User ID** (UUID, required)
- **Category Filter** (enum, optional)
- **Status Filter** (enum, optional)
- **Search Keyword** (string, optional)
- **Sort Option** (enum, optional)
- **Page Number** (integer, optional)

### Output Data
- **Set List** (array of set objects)
- **Total Count** (integer)
- **Filter Options** (available categories, statuses)
- **Pagination Info** (current page, total pages)

## 9. Non-Functional Requirements

### Performance
- Set list load < 2 giây
- Filter response < 1 giây
- Search response < 500ms
- Pagination load < 1 giây

### Security
- User chỉ có thể xem set của mình
- Validate user permissions
- Sanitize search input

### Usability
- Responsive design cho mobile
- Infinite scroll hoặc pagination
- Clear visual hierarchy
- Loading states và error handling

## 10. Acceptance Criteria

### AC-001: Display Set List
**Given** user có set trong hệ thống
**When** user truy cập danh sách set
**Then** tất cả set được hiển thị với thông tin cơ bản
**And** user có thể thực hiện actions trên set

### AC-002: Empty State
**Given** user chưa có set nào
**When** user truy cập danh sách set
**Then** empty state được hiển thị
**And** user được hướng dẫn tạo set đầu tiên

### AC-003: Filter by Category
**Given** user chọn filter theo category
**When** user xem danh sách set
**Then** chỉ set thuộc category đó được hiển thị
**And** số lượng kết quả được hiển thị

### AC-004: Search Sets
**Given** user nhập từ khóa tìm kiếm
**When** user tìm kiếm set
**Then** set có tên/mô tả chứa từ khóa được hiển thị
**And** từ khóa được highlight

### AC-005: Sort Sets
**Given** user chọn sắp xếp
**When** user xem danh sách set
**Then** set được sắp xếp theo option đã chọn
**And** thứ tự sắp xếp được duy trì

## 11. Test Cases

### TC-001: Display Set List
**Test Data**: User with multiple sets
**Expected Result**: All sets displayed with basic information

### TC-002: Empty State
**Test Data**: User with no sets
**Expected Result**: Empty state shown with create set option

### TC-003: Category Filter
**Test Data**: Filter by vocabulary category
**Expected Result**: Only vocabulary sets displayed

### TC-004: Status Filter
**Test Data**: Filter by learning status
**Expected Result**: Only sets with learning status displayed

### TC-005: Search Functionality
**Test Data**: Search keyword "grammar"
**Expected Result**: Sets with "grammar" in name/description displayed

### TC-006: Large Dataset
**Test Data**: User with 100+ sets
**Expected Result**: Pagination or infinite scroll implemented

## 12. Related Use Cases

- **UC-005**: Create New Set
- **UC-006**: Edit Set Information
- **UC-007**: Delete Set
- **UC-009**: View Set Details
- **UC-010**: Start Learning Cycle

## 13. Notes

### Implementation Notes
- Implement pagination hoặc infinite scroll cho large datasets
- Use debounce cho search functionality
- Cache set list data để tăng performance
- Implement lazy loading cho set images/icons

### Future Enhancements
- Advanced filtering options
- Set grouping by category/status
- Bulk actions on sets
- Set templates và sharing

### UI/UX Considerations
- Clean, card-based layout
- Visual indicators cho status và progress
- Smooth animations và transitions
- Mobile-first responsive design
- Accessibility features
