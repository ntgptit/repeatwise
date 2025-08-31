# Business Rules

## 1. Set Management Rules

### BR-001: Set Creation
**Condition**: User tạo set mới
**Rules**:
- Tên set bắt buộc và ≤ 100 ký tự
- Mô tả ≤ 500 ký tự (optional)
- Số từ vựng > 0
- Category phải thuộc: vocabulary, grammar, mixed, other
- Trạng thái ban đầu: not_started
- Current cycle = 1
**Result**: Set được tạo với UUID, created_at = now()

### BR-002: Set Deletion
**Condition**: User xóa set
**Rules**:
- Chỉ soft delete (set deleted_at = now())
- Không xóa dữ liệu liên quan trong review_histories
- Không xóa dữ liệu liên quan trong remind_schedules
**Result**: Set được đánh dấu deleted, dữ liệu được bảo toàn

### BR-003: Set Status Transition
**Condition**: Set thay đổi trạng thái
**Rules**:
- not_started → learning: Khi bắt đầu chu kỳ đầu tiên
- learning → reviewing: Khi hoàn thành 5 lần ôn đầu tiên
- reviewing → mastered: Khi đạt điều kiện mastered (xem BR-033)
- mastered → reviewing: Khi avg_score < 70% trong chu kỳ tiếp theo
- mastered → learning: Khi user chọn reset set (tùy chọn)

### BR-034: Mastered Status Maintenance
**Condition**: Set đã mastered cần duy trì trạng thái
**Rules**:
- **Ôn định kỳ**: Tự động tạo reminder mỗi 90 ngày
- **Điều kiện duy trì**: avg_score ≥ 70% trong lần ôn định kỳ
- **Chuyển về reviewing**: Nếu avg_score < 70% trong lần ôn định kỳ
- **Reset về learning**: Nếu avg_score < 50% trong lần ôn định kỳ
- **Thông báo**: Cảnh báo khi performance giảm sút
- **Lịch sử**: Lưu tất cả lần ôn định kỳ vào review_histories
**Result**: Set mastered được duy trì hoặc chuyển về trạng thái phù hợp
**Result**: Status được cập nhật, updated_at = now()

### BR-033: Mastered Status Definition
**Condition**: Set đạt trạng thái mastered
**Rules**:
- **Điều kiện chính**: avg_score ≥ 85% trong 3 chu kỳ liên tiếp
- **Điều kiện phụ**: 
  - Không có lần ôn nào bị skip trong 3 chu kỳ cuối
  - Tổng thời gian học ≥ 30 ngày (từ lần ôn đầu tiên)
- **Chu kỳ được tính**: Chỉ tính từ chu kỳ thứ 2 trở đi (bỏ qua chu kỳ đầu tiên)
- **Xử lý skip**: Nếu có skip trong 3 chu kỳ cuối, reset lại đếm chu kỳ
- **Thông báo**: Hiển thị thông báo chúc mừng khi đạt mastered
- **Lịch ôn định kỳ**: Tự động tạo lịch ôn định kỳ mỗi 90 ngày
**Result**: Set được đánh dấu mastered và chuyển sang chế độ ôn định kỳ

### BR-024: Set Word Count Modification
**Condition**: User thay đổi word_count của set
**Rules**:
- Nếu set đang trong chu kỳ học (learning/reviewing):
  - Công thức delay chu kỳ tiếp theo sẽ sử dụng word_count mới
  - Không ảnh hưởng đến chu kỳ hiện tại
  - Lưu lịch sử thay đổi vào activity_logs
- Nếu set ở trạng thái mastered:
  - Cho phép thay đổi tự do
  - Không ảnh hưởng đến lịch sử học
**Result**: Word count được cập nhật với tracking lịch sử

### BR-025: Empty Set Handling
**Condition**: Set có word_count = 0
**Rules**:
- Không cho phép tạo set với word_count = 0
- Nếu word_count bị set = 0 do lỗi:
  - Tạm thời sử dụng word_count = 1 cho tính toán delay
  - Hiển thị cảnh báo cho user
  - Yêu cầu user cập nhật word_count > 0
**Result**: Tránh lỗi division by zero trong tính toán

## 2. Cycle Management Rules

### BR-004: Cycle Structure
**Condition**: Mỗi chu kỳ học
**Rules**:
- Mỗi chu kỳ có đúng 5 lần ôn tập
- Thời gian giữa các lần ôn cố định: 1, 3, 7, 14, 30 ngày (chu kỳ đầu)
- Sau 5 lần ôn, tính avg_score và delay chu kỳ mới
**Result**: Chu kỳ mới bắt đầu sau delay được tính

### BR-020: Intra-Cycle Review Intervals
**Condition**: Khoảng thời gian giữa các lần ôn tập trong chu kỳ
**Rules**:
- **Chu kỳ đầu tiên (learning)**: 
  - Lần 1: Ngay sau khi bắt đầu học
  - Lần 2: 1 ngày sau lần 1
  - Lần 3: 3 ngày sau lần 2
  - Lần 4: 7 ngày sau lần 3
  - Lần 5: 14 ngày sau lần 4
- **Chu kỳ tiếp theo (reviewing)**: 
  - Lần 1: 1 ngày sau khi hoàn thành chu kỳ trước
  - Lần 2: 3 ngày sau lần 1
  - Lần 3: 7 ngày sau lần 2
  - Lần 4: 14 ngày sau lần 3
  - Lần 5: 30 ngày sau lần 4
- **Tùy chỉnh khoảng thời gian**:
  - Conservative: 1, 3, 7, 14, 30 ngày (mặc định)
  - Aggressive: 1, 2, 4, 8, 16 ngày
  - Balanced: 1, 3, 6, 12, 24 ngày
- **Xử lý skip**: Nếu user skip một lần ôn, khoảng thời gian tiếp theo vẫn tính từ lần ôn trước đó
- **Giới hạn tối thiểu**: Không được ít hơn 1 ngày giữa các lần ôn
**Result**: Lịch ôn tập được lên kế hoạch theo thuật toán Spaced Repetition với khoảng cách tối ưu

### BR-005: Cycle Delay Calculation
**Condition**: Tính delay chu kỳ mới
**Formula**: `next_cycle_delay_days = base_delay - penalty * (100 - avg_score) + scaling * word_count`
**Rules**:
- base_delay = 30 ngày (có thể config)
- penalty = 0.2 (có thể config)
- scaling = 0.02 (có thể config)
- Giới hạn: 7 ≤ delay ≤ 90 ngày
- Nếu avg_score < 40% → delay = 7 ngày
**Result**: Delay được tính và áp dụng cho chu kỳ mới

### BR-027: Personalized SRS Algorithm
**Condition**: User tùy chỉnh thuật toán SRS
**Rules**:
- **Basic Level**: Sử dụng hệ số mặc định (base_delay=30, penalty=0.2, scaling=0.02)
- **Advanced Level**: Cho phép user tùy chỉnh:
  - base_delay: 15-60 ngày
  - penalty: 0.1-0.5
  - scaling: 0.01-0.05
- **Custom Intervals**: Cho phép chọn chuỗi ngày khác:
  - Conservative: 1, 3, 7, 14, 30 ngày
  - Aggressive: 1, 2, 4, 8, 16 ngày
  - Balanced: 1, 3, 6, 12, 24 ngày
- Lưu lịch sử thay đổi vào user_preferences
- Áp dụng ngay cho chu kỳ tiếp theo
**Result**: Thuật toán được cá nhân hóa theo khả năng ghi nhớ của user

### BR-006: Cycle Continuation
**Condition**: Chu kỳ lặp lại
**Rules**:
- Chu kỳ lặp vô hạn cho đến khi set đạt trạng thái mastered
- Không giới hạn số chu kỳ
- Mỗi chu kỳ độc lập với nhau
- Set mastered vẫn có thể được reset để học lại
**Result**: Set có thể học nhiều vòng tùy tiến độ

## 3. Score Management Rules

### BR-007: Score Input
**Condition**: User nhập điểm sau lần ôn
**Rules**:
- Điểm bắt buộc nhập (0-100%)
- Nếu skip, phải chọn lý do: forgot, busy, other
- Trạng thái lần ôn = skipped nếu không nhập điểm
- Chỉnh sửa điểm trong vòng 24h sau khi nhập
**Result**: Điểm được lưu vào review_histories

### BR-028: Contextual Score Input
**Condition**: User chọn mức độ tự tin thay vì điểm số
**Rules**:
- **Confidence Levels**:
  - "Dễ" (Easy): Tự động quy đổi thành 85-100%
  - "Bình thường" (Normal): Tự động quy đổi thành 60-84%
  - "Khó" (Hard): Tự động quy đổi thành 0-59%
- **Score Mapping**:
  - Dễ: Random trong khoảng 85-100%
  - Bình thường: Random trong khoảng 60-84%
  - Khó: Random trong khoảng 0-59%
- User có thể chuyển đổi giữa chế độ "Điểm số" và "Mức độ tự tin"
- Lưu preference vào user_settings
- Hiển thị cả điểm số và mức độ tự tin trong lịch sử
**Result**: Giảm gánh nặng đánh giá cho user

### BR-026: Skip Review Handling
**Condition**: User skip một lần ôn tập
**Rules**:
- Lần ôn bị skipped được tính điểm 0 vào avg_score của chu kỳ
- Hệ thống tự động reschedule lần ôn đó sang ngày hôm sau
- Nếu user skip 3 lần liên tiếp cho cùng một lần ôn:
  - Tự động chuyển sang lần ôn tiếp theo
  - Ghi nhận vào activity_logs
  - Có thể ảnh hưởng đến trạng thái mastered
- Lịch sử skip được lưu với lý do và timestamp
**Result**: Skip được xử lý mà không làm gián đoạn chu kỳ học

### BR-008: Score History
**Condition**: Lưu lịch sử điểm
**Rules**:
- Lưu đầy đủ: set_id, cycle_no, review_no, score, status, note, created_at
- Mọi thay đổi điểm lưu vào activity_logs
- Không xóa lịch sử điểm
**Result**: Dữ liệu được bảo toàn cho phân tích

### BR-009: Score Validation
**Condition**: Validate điểm số
**Rules**:
- Điểm phải là số nguyên từ 0-100
- Không cho phép điểm âm hoặc > 100
- Điểm 0-40: Kém, cần ôn lại sớm
- Điểm 41-70: Trung bình, ôn lại bình thường
- Điểm 71-100: Tốt, có thể ôn lại muộn hơn
**Result**: Điểm hợp lệ được chấp nhận

## 4. Reminder Management Rules

### BR-010: Daily Reminder Limit
**Condition**: Tạo reminder hàng ngày
**Rules**:
- Tối đa 3 set/user/ngày
- Nếu > 3 set tới hạn:
  1. Ưu tiên set quá hạn lâu nhất
  2. Ưu tiên set có avg_score thấp hơn
  3. Ưu tiên set có word_count thấp hơn
- Các set còn lại reschedule sang ngày tiếp theo
**Result**: Đảm bảo không quá tải user

### BR-011: Reminder Status
**Condition**: Quản lý trạng thái reminder
**Rules**:
- pending: Chờ gửi
- sent: Đã gửi notification
- done: User đã hoàn thành ôn
- skipped: User bỏ qua
- rescheduled: Đã dời ngày
- cancelled: Đã hủy
**Result**: Trạng thái được cập nhật theo hành động

### BR-012: Reminder Reschedule
**Condition**: User reschedule reminder
**Rules**:
- Tối đa 2 lần reschedule cho cùng reminder
- Lưu lịch sử: ai đổi, khi nào, lý do
- Không được reschedule quá 7 ngày trong tương lai
- Không được reschedule về quá khứ
**Result**: Reminder được dời ngày với lịch sử

### BR-029: Reschedule Reason Tracking
**Condition**: Lưu trữ lý do reschedule
**Rules**:
- **System Reasons**:
  - system_overload: Quá tải hệ thống (>3 set/ngày)
  - user_preference: Theo preference của user
  - algorithm_adjustment: Điều chỉnh theo thuật toán
- **User Reasons**:
  - user_manual_reschedule: User tự dời lịch
  - user_busy: User bận rộn
  - user_sick: User ốm/bệnh
  - user_travel: User đi du lịch
- **Automatic Reasons**:
  - skip_overflow: Skip quá nhiều lần
  - performance_adjustment: Điều chỉnh theo hiệu suất
- Lưu reason vào remind_schedules.reason field
- Phân tích reason để cải thiện thuật toán
- Hiển thị reason trong lịch sử reschedule
**Result**: Dữ liệu quý giá cho phân tích hành vi user

## 5. Overload Prevention Rules

### BR-013: Daily Set Distribution
**Condition**: Phân bố set theo ngày
**Rules**:
- Giãn tuyến tính theo word_count
- Set nhiều từ → delay dài hơn
- Đảm bảo không dồn set lớn vào cùng ngày
- Reschedule tự động nếu vượt quá 3 set/ngày
**Result**: Lịch học cân bằng và hợp lý

### BR-014: Priority Calculation
**Condition**: Tính ưu tiên khi quá tải
**Rules**:
- Ưu tiên 1: Set quá hạn lâu nhất (số ngày quá hạn)
- Ưu tiên 2: Set có avg_score thấp hơn
- Ưu tiên 3: Set có word_count thấp hơn
- FIFO theo ngày tới hạn ban đầu
**Result**: Set quan trọng nhất được ưu tiên

## 6. Data Integrity Rules

### BR-015: UUID Usage
**Condition**: Tất cả entities
**Rules**:
- Tất cả ID sử dụng UUID format
- Không sử dụng auto-increment
- UUID được generate trước khi insert
**Result**: ID duy nhất toàn cầu

### BR-016: Audit Fields
**Condition**: Tất cả entities
**Rules**:
- Bắt buộc có created_at, updated_at
- created_at = now() khi tạo mới
- updated_at = now() khi cập nhật
- deleted_at = now() khi soft delete
**Result**: Dữ liệu có thể audit và rollback

### BR-017: Soft Delete
**Condition**: Xóa dữ liệu
**Rules**:
- Không hard delete bất kỳ dữ liệu nào
- Chỉ đánh dấu deleted_at
- Dữ liệu liên quan được bảo toàn
- Có thể rollback nếu cần
**Result**: Dữ liệu an toàn và có thể khôi phục

## 7. Performance Rules

### BR-018: Database Indexing
**Condition**: Truy vấn dữ liệu
**Rules**:
- Index trên user_id, set_id, remind_date
- Index trên status, created_at
- Composite index cho queries phức tạp
- Regular maintenance và optimization
**Result**: Truy vấn nhanh và hiệu quả

### BR-019: Caching Strategy
**Condition**: Dữ liệu thường xuyên truy cập
**Rules**:
- Cache user preferences và settings
- Cache set information và statistics
- Cache reminder schedules
- TTL = 1 giờ cho cache data
**Result**: Giảm tải database và tăng performance

## 8. Security Rules

### BR-020: Authentication
**Condition**: User access
**Rules**:
- Sử dụng JWT + refresh token
- Token expiry = 1 giờ
- Refresh token expiry = 7 ngày
- Secure token storage
**Result**: Access được bảo mật

### BR-021: Authorization
**Condition**: Data access
**Rules**:
- User chỉ access dữ liệu của mình
- Role-based access control
- API rate limiting
- Input validation và sanitization
**Result**: Dữ liệu được bảo vệ

## 9. Compliance Rules

### BR-022: Data Privacy
**Condition**: Xử lý dữ liệu cá nhân
**Rules**:
- Tuân thủ GDPR/CCPA
- Data minimization
- User consent cho data processing
- Right to be forgotten
**Result**: Compliance với quy định bảo mật

### BR-023: Data Retention
**Condition**: Lưu trữ dữ liệu
**Rules**:
- Lưu trữ tối thiểu 2 năm
- Archive sau 5 năm
- Delete sau 10 năm
- Backup định kỳ
**Result**: Dữ liệu được bảo toàn theo quy định 
