# US-011: Perform Review Session

## 1. User Story

**As a** Student (Người học)
**I want to** thực hiện lần ôn tập cho set học tập và tự đánh giá mức độ nhớ
**So that** tôi có thể theo dõi tiến trình học tập và cải thiện khả năng ghi nhớ

## 2. Business Value

- **User Value**: Giúp user tự đánh giá và theo dõi hiệu quả học tập, tạo động lực học tập
- **Business Value**: Thu thập dữ liệu học tập để cải thiện thuật toán SRS, tăng user engagement

## 3. Acceptance Criteria

### AC-001: Access Review Session
**Given** tôi có set cần ôn tập trong ngày
**When** tôi nhận notification hoặc chọn set cần ôn
**Then** màn hình review được hiển thị
**And** thông tin set được hiển thị đầy đủ

### AC-002: View Set Information
**Given** tôi đang ở màn hình review
**When** tôi xem thông tin set
**Then** tôi thấy tên set và mô tả
**And** tôi thấy số từ vựng
**And** tôi thấy chu kỳ hiện tại và lần ôn
**And** tôi thấy lịch sử điểm số (nếu có)

### AC-003: Perform Review
**Given** tôi đang xem thông tin set
**When** tôi xem lại nội dung học tập
**Then** nội dung cần ôn được hiển thị rõ ràng
**And** tôi có thể tự đánh giá mức độ nhớ

### AC-004: Input Valid Score
**Given** tôi đã xem lại nội dung
**When** tôi chọn điểm số từ 0-100%
**And** tôi nhập ghi chú (optional)
**Then** điểm số được validate
**And** preview được hiển thị

### AC-005: Submit Review Successfully
**Given** tôi đã nhập điểm số hợp lệ
**When** tôi nhấn "Lưu điểm"
**Then** review được lưu thành công
**And** trạng thái reminder được cập nhật thành 'done'
**And** tiến trình set được cập nhật

### AC-006: Skip Review Session
**Given** tôi không thể ôn tập ngay
**When** tôi chọn "Skip"
**And** tôi chọn lý do: forgot, busy, other
**Then** review được lưu với status 'skipped'
**And** reminder status được cập nhật thành 'skipped'

### AC-007: Invalid Score Input
**Given** tôi nhập điểm số không hợp lệ
**When** tôi nhấn "Lưu điểm"
**Then** hệ thống hiển thị thông báo lỗi: "Điểm số phải từ 0-100"
**And** trường điểm số được highlight
**And** review không được lưu

### AC-008: Cycle Completion
**Given** tôi đã hoàn thành 5 lần ôn trong chu kỳ
**When** tôi nhập điểm lần cuối
**Then** chu kỳ được hoàn thành
**And** avg_score được tính
**And** chu kỳ mới được tạo với delay mới

### AC-009: Set Mastered
**Given** set đạt điều kiện mastered (avg_score ≥ 80% trong 3 chu kỳ)
**When** tôi hoàn thành chu kỳ
**Then** set status được cập nhật thành 'mastered'
**And** thông báo chúc mừng được hiển thị
**And** reminder cho review định kỳ được tạo

### AC-010: Network Error Handling
**Given** mất kết nối internet trong quá trình ôn tập
**When** tôi thử lưu review
**Then** hệ thống hiển thị thông báo lỗi kết nối
**And** draft review data được lưu locally
**And** tôi có thể thử lại khi có kết nối

### AC-011: Review Already Completed
**Given** lần ôn này đã được hoàn thành
**When** tôi truy cập review
**Then** hệ thống hiển thị thông báo: "Lần ôn này đã được hoàn thành"
**And** điểm số đã nhập được hiển thị
**And** tôi có thể xem lịch sử hoặc chuyển sang set khác

### AC-012: Score Guidance
**Given** tôi đang nhập điểm số
**When** tôi xem hướng dẫn đánh giá
**Then** tôi thấy:
- Điểm 0-40: Kém, cần ôn lại sớm
- Điểm 41-70: Trung bình, ôn lại bình thường
- Điểm 71-100: Tốt, có thể ôn lại muộn hơn

## 4. Definition of Ready (DoR)

- [ ] UI/UX design cho review interface đã được approve
- [ ] SRS algorithm đã được implement
- [ ] Database schema cho review_histories đã được thiết kế
- [ ] Reminder system đã được implement
- [ ] Score validation rules đã được xác định
- [ ] Cycle completion logic đã được thiết kế
- [ ] Test cases đã được viết
- [ ] Error handling scenarios đã được xác định

## 5. Definition of Done (DoD)

- [ ] User có thể truy cập review session từ notification hoặc dashboard
- [ ] Set information được hiển thị đầy đủ và chính xác
- [ ] Score input với validation hoạt động
- [ ] Review submission được lưu thành công
- [ ] Skip functionality hoạt động với lý do
- [ ] Cycle completion logic hoạt động chính xác
- [ ] Set mastered logic hoạt động
- [ ] SRS algorithm tính toán delay chính xác
- [ ] Reminder status được cập nhật
- [ ] Activity logs được tạo
- [ ] Unit tests đạt coverage > 90%
- [ ] Integration tests pass
- [ ] Performance tests đạt yêu cầu
- [ ] Code review được approve
- [ ] Documentation được cập nhật

## 6. Story Points

**Story Points**: 13

**Lý do**: 
- High complexity do cần implement SRS algorithm
- Integration với reminder system
- Cycle completion logic phức tạp
- Score validation và business rules
- Error handling scenarios nhiều
- Performance requirements cao

## 7. Dependencies

- **Technical Dependencies**:
  - SRS algorithm service
  - Reminder management system
  - Set management system
  - Database schema
  - Notification system

- **Business Dependencies**:
  - Set creation functionality
  - Reminder scheduling
  - Learning cycle management

## 8. Risk Assessment

### High Risk
- **SRS algorithm complexity**: Tính toán delay có thể có bugs
- **Mitigation**: Extensive testing, code review, mathematical validation

### Medium Risk
- **Performance issues**: Database queries, algorithm calculation
- **Mitigation**: Performance testing, caching, optimization

### Low Risk
- **UI/UX issues**: Score input interface, progress indicators
- **Mitigation**: User testing, iterative design

## 9. Test Scenarios

### Happy Path
1. User nhận notification → Truy cập review → Nhập điểm → Lưu thành công
2. User skip review → Chọn lý do → Lưu thành công
3. User hoàn thành chu kỳ → Cycle completion → Chu kỳ mới được tạo

### Error Scenarios
1. User nhập điểm không hợp lệ → Validation error
2. User mất kết nối → Network error → Retry functionality
3. User truy cập review đã hoàn thành → Already completed message

### Edge Cases
1. User nhập điểm 0% → Valid score, short delay
2. User nhập điểm 100% → Valid score, long delay
3. User hoàn thành set → Set mastered → Congratulations
4. User có nhiều set cần ôn → Priority system

## 10. Success Metrics

### Functional Metrics
- **Review Completion Rate**: > 85%
- **Score Input Accuracy**: > 95%
- **Cycle Completion Rate**: > 90%
- **Set Mastered Rate**: > 70%

### Performance Metrics
- **Review Submission Time**: < 2 giây
- **Score Validation Time**: < 500ms
- **Cycle Calculation Time**: < 1 giây
- **Database Query Time**: < 1 giây

### User Experience Metrics
- **Time to Complete Review**: < 5 phút
- **Score Input Satisfaction**: User feedback score > 4/5
- **Review Interface Usability**: User feedback score > 4/5
- **Error Recovery Rate**: > 95%

### Business Metrics
- **User Engagement**: Tăng thời gian sử dụng app
- **Learning Effectiveness**: Cải thiện retention rate
- **Data Quality**: Độ chính xác của score data
