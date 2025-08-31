# US-016: Reschedule Reminder

## 1. User Story

**As a** Student (Người học)
**I want to** thay đổi lịch nhắc nhở ôn tập cho set học tập
**So that** tôi có thể sắp xếp thời gian học tập phù hợp với lịch trình cá nhân

## 2. Business Value

- **User Value**: Cho phép user linh hoạt trong việc sắp xếp thời gian học tập, tăng khả năng hoàn thành reminder
- **Business Value**: Tăng tỷ lệ completion rate, giảm số lượng reminder bị bỏ qua, cải thiện user experience

## 3. Acceptance Criteria

### AC-001: Access Reminder Management
**Given** tôi có reminder đang pending hoặc sent
**When** tôi chọn set cần reschedule
**Then** thông tin reminder hiện tại được hiển thị
**And** tùy chọn reschedule được hiển thị

### AC-002: View Current Reminder Info
**Given** tôi đang xem thông tin reminder
**When** tôi xem chi tiết reminder
**Then** tôi thấy ngày reminder hiện tại
**And** tôi thấy số lần đã reschedule
**And** tôi thấy trạng thái reminder
**And** tôi thấy lịch sử reschedule (nếu có)

### AC-003: Select Valid New Date
**Given** tôi đang chọn ngày mới cho reminder
**When** tôi chọn ngày trong tương lai (1-7 ngày)
**Then** ngày mới được validate
**And** preview được hiển thị

### AC-004: Provide Reschedule Reason
**Given** tôi đã chọn ngày mới
**When** tôi nhập lý do reschedule (optional)
**Then** lý do được lưu cùng với reschedule
**And** preview thông tin thay đổi được hiển thị

### AC-005: Submit Reschedule Successfully
**Given** tôi đã chọn ngày mới hợp lệ
**When** tôi nhấn "Reschedule"
**Then** reminder được reschedule thành công
**And** ngày reminder được cập nhật
**And** reschedule_count được tăng
**And** lịch sử thay đổi được lưu

### AC-006: Invalid Date Selection
**Given** tôi chọn ngày không hợp lệ (quá khứ hoặc > 7 ngày)
**When** tôi nhấn "Reschedule"
**Then** hệ thống hiển thị thông báo lỗi: "Ngày mới phải trong tương lai và không quá 7 ngày"
**And** trường ngày được highlight
**And** reschedule không được thực hiện

### AC-007: Reschedule Limit Exceeded
**Given** tôi đã reschedule 2 lần cho reminder này
**When** tôi thử reschedule lần nữa
**Then** hệ thống hiển thị thông báo: "Bạn đã đạt giới hạn reschedule cho reminder này"
**And** lịch sử reschedule được hiển thị
**And** reschedule không được cho phép

### AC-008: Reminder Already Completed
**Given** reminder đã được hoàn thành
**When** tôi thử reschedule
**Then** hệ thống hiển thị thông báo: "Reminder này đã được hoàn thành, không thể reschedule"
**And** thông tin hoàn thành được hiển thị
**And** tôi được hướng dẫn xem lịch sử

### AC-009: Date Conflict Detection
**Given** ngày mới trùng với reminder khác
**When** tôi submit reschedule
**Then** hệ thống hiển thị thông báo: "Ngày này đã có reminder khác"
**And** reminder trùng lặp được hiển thị
**And** tôi được yêu cầu chọn ngày khác

### AC-010: Network Error Handling
**Given** mất kết nối internet
**When** tôi thử reschedule
**Then** hệ thống hiển thị thông báo lỗi kết nối
**And** draft reschedule data được lưu locally
**And** tôi có thể thử lại khi có kết nối

### AC-011: Reminder Not Found
**Given** reminder không tồn tại hoặc đã bị xóa
**When** tôi thử reschedule
**Then** hệ thống hiển thị thông báo: "Reminder không tồn tại"
**And** danh sách reminder được refresh
**And** tôi có thể chọn reminder khác

### AC-012: Reschedule History
**Given** tôi đã reschedule reminder
**When** tôi xem lịch sử reschedule
**Then** tôi thấy:
- Ngày reschedule
- Lý do reschedule
- Người thực hiện reschedule
- Trạng thái trước và sau reschedule

## 4. Definition of Ready (DoR)

- [ ] UI/UX design cho reschedule interface đã được approve
- [ ] Reminder management system đã được implement
- [ ] Date validation logic đã được thiết kế
- [ ] Conflict detection algorithm đã được implement
- [ ] Reschedule limit rules đã được xác định
- [ ] Activity logging system đã được thiết kế
- [ ] Test cases đã được viết
- [ ] Error handling scenarios đã được xác định

## 5. Definition of Done (DoD)

- [ ] User có thể truy cập reminder management từ dashboard
- [ ] Reminder information được hiển thị đầy đủ và chính xác
- [ ] Date picker với validation hoạt động
- [ ] Reschedule submission được xử lý thành công
- [ ] Reschedule limit enforcement hoạt động
- [ ] Conflict detection hoạt động chính xác
- [ ] Activity logs được tạo cho mọi reschedule
- [ ] Error handling cho tất cả scenarios
- [ ] Reschedule history được lưu và hiển thị
- [ ] Unit tests đạt coverage > 90%
- [ ] Integration tests pass
- [ ] Performance tests đạt yêu cầu
- [ ] Code review được approve
- [ ] Documentation được cập nhật

## 6. Story Points

**Story Points**: 8

**Lý do**: 
- Medium complexity do cần implement date validation và conflict detection
- Integration với reminder system
- Reschedule limit enforcement
- Activity logging requirements
- Error handling scenarios nhiều

## 7. Dependencies

- **Technical Dependencies**:
  - Reminder management system
  - Date/time handling service
  - Conflict detection service
  - Activity logging system
  - Database schema

- **Business Dependencies**:
  - Reminder scheduling system
  - Set management functionality
  - User permission system

## 8. Risk Assessment

### High Risk
- **Date conflict detection**: Có thể miss conflicts trong edge cases
- **Mitigation**: Extensive testing, code review, clear business rules

### Medium Risk
- **Performance issues**: Database queries, conflict checking
- **Mitigation**: Performance testing, caching, optimization

### Low Risk
- **UI/UX issues**: Date picker interface, error messages
- **Mitigation**: User testing, iterative design

## 9. Test Scenarios

### Happy Path
1. User chọn reminder → Chọn ngày mới → Submit → Reschedule thành công
2. User reschedule với lý do → Lý do được lưu
3. User xem lịch sử reschedule → Thông tin đầy đủ

### Error Scenarios
1. User chọn ngày không hợp lệ → Validation error
2. User reschedule quá giới hạn → Limit exceeded message
3. User reschedule reminder đã hoàn thành → Cannot reschedule message
4. User chọn ngày trùng lặp → Conflict warning

### Edge Cases
1. User reschedule trong timezone khác → Timezone handling
2. User reschedule reminder đã bị xóa → Not found message
3. User reschedule với network issues → Retry functionality
4. User reschedule nhiều reminder cùng lúc → Batch processing

## 10. Success Metrics

### Functional Metrics
- **Reschedule Success Rate**: > 95%
- **Conflict Detection Accuracy**: > 99%
- **Limit Enforcement Rate**: 100%
- **Error Recovery Rate**: > 90%

### Performance Metrics
- **Reschedule Process Time**: < 2 giây
- **Date Validation Time**: < 500ms
- **Conflict Checking Time**: < 1 giây
- **Database Update Time**: < 1 giây

### User Experience Metrics
- **Time to Reschedule**: < 1 phút
- **Reschedule Interface Usability**: User feedback score > 4/5
- **Error Message Clarity**: User feedback score > 4/5
- **Conflict Resolution Time**: < 30 giây

### Business Metrics
- **Reminder Completion Rate**: Tăng sau khi có reschedule
- **User Satisfaction**: Tăng với tính linh hoạt
- **Support Tickets**: Giảm liên quan đến reminder conflicts
- **User Retention**: Tăng do improved experience
