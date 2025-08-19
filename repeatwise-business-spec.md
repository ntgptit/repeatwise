# RepeatWise – Business Specification (Revised)

---

## 1. Tầm nhìn & Giá trị cốt lõi

- **RepeatWise** là ứng dụng quản lý học tập thông minh, giúp người học ghi nhớ lâu dài thông qua thuật toán **Spaced Repetition (SRS)**, tối ưu lịch ôn dựa trên điểm số và dung lượng nội dung.
- Quản lý học theo **set** (học phần/chủ đề), không đi sâu vào flashcard cá nhân mà tập trung vào tiến trình & kết quả tổng thể.
- Triết lý: **Tối giản – Cá nhân hóa – Tập trung vào hiệu quả** trước khi mở rộng cộng đồng hoặc thương mại hóa.

---

## 2. Đơn vị quản lý: SET

- User được tạo **không giới hạn set**.
- Mỗi set có:
  - `id` (UUID)
  - `name` (tên set, bắt buộc, ≤ 100 ký tự)
  - `description` (mô tả ngắn, ≤ 500 ký tự)
  - `category` (enum: `vocabulary`, `grammar`, `mixed`, `other`) – dùng cho thống kê và lọc.
  - `word_count` (số từ vựng, bắt buộc > 0)
  - `status` (enum: `not_started`, `learning`, `reviewing`, `mastered`)
  - `current_cycle` (số thứ tự chu kỳ hiện tại)
  - `created_at`, `updated_at`, `deleted_at` (soft delete)

---

## 3. Chu kỳ học & Quy trình SRS

- **Mỗi set có 5 lần ôn tập** trong một chu kỳ học.
- Thời gian giữa các lần ôn trong chu kỳ đầu tiên áp dụng khoảng cách cố định (ví dụ: 1, 3, 7, 14, 30 ngày).
- Sau khi hoàn thành 5 lần ôn:
  - Tính **điểm trung bình chu kỳ** (`avg_score`)
  - Tính **delay bắt đầu chu kỳ mới** theo công thức ở mục 5.
  - Chu kỳ mới bắt đầu từ ngày delay được tính.
- Chu kỳ lặp vô hạn → mỗi set có thể ôn nhiều vòng tùy tiến độ.

---

## 4. Ghi nhận & Sử dụng điểm số

- Sau mỗi lần ôn:
  - User **bắt buộc nhập điểm** (0–100%).
  - Nếu bỏ qua, hệ thống yêu cầu chọn lý do (`forgot`, `busy`, `other`) và trạng thái lần ôn = `skipped`.
- Lịch sử điểm:
  - Lưu vào bảng `review_histories` (set_id, cycle_no, review_no, score, status, note, created_at)
  - Cho phép chỉnh sửa điểm trong vòng **24h** sau khi nhập, mọi thay đổi lưu vào `activity_logs`.

---

## 5. Thuật toán tính delay chu kỳ mới

```
next_cycle_delay_days = base_delay - penalty * (100 - avg_score) + scaling * word_count
```

- `base_delay` (default 30 ngày, có thể config trong DB)
- `penalty` (default 0.2)
- `scaling` (default 0.02)
- `avg_score`: trung bình 5 lần ôn của chu kỳ vừa kết thúc
- `word_count`: số từ của set
- Giới hạn: **7 ≤ delay ≤ 90 ngày**
- Nếu `avg_score < 40%` → delay = 7 ngày (ôn lại sớm nhất)

---

## 6. Quản lý lịch ôn & tránh overload

- **Mỗi user chỉ nhận tối đa 3 set cần ôn trong một ngày**.
- Nếu số set tới hạn > 3:
  1. Ưu tiên set quá hạn lâu nhất.
  2. Nếu bằng nhau, ưu tiên set có `avg_score` thấp hơn.
  3. Nếu tiếp tục bằng nhau, ưu tiên set có `word_count` thấp hơn.
- Các set còn lại:
  - Reschedule sang **ngày tiếp theo còn trống** (không quá 3 set/ngày).
  - Nếu ngày sau vẫn quá tải → tiếp tục dời, đảm bảo FIFO theo ngày tới hạn ban đầu.

---

## 7. Reminder (Nhắc nhở học tập)

- Lưu reminder vào bảng `remind_schedules`:
  - `id`, `set_id`, `user_id`
  - `remind_date` (ngày nhắc)
  - `status` (enum: `pending`, `sent`, `done`, `skipped`, `rescheduled`, `cancelled`)
  - `created_at`, `updated_at`
- Backend job:
  - Chạy hàng ngày (00:00) tạo reminder cho tối đa 3 set/user.
  - Chạy theo giờ định trước của user (ví dụ: 9h sáng, 7h tối) để gửi notification.
  - Log toàn bộ trạng thái gửi vào `notification_logs`.

---

## 8. Re-schedule & Soft Delete

- **Re-schedule**:
  - Cho phép user/manual update ngày nhắc.
  - Lưu lịch sử vào `activity_logs` (ai đổi, đổi khi nào, lý do).
  - Giới hạn số lần reschedule: tối đa 2 lần cho cùng một reminder.
- **Soft delete**:
  - Trường `deleted_at` dùng cho rollback và audit.
  - Không xóa dữ liệu liên quan trong `review_histories` hoặc `remind_schedules`.

---

## 9. Công nghệ & Kiến trúc

- **repeatwise-mobile:** Flutter Riverpod annotation (UI, notification, quản lý set)
- **repeatwise-server:** Spring Boot 3 (REST API, SRS logic, scheduling, notification)
- **repeatwise-db:** PostgreSQL
- Chuẩn:
  - ID dạng UUID
  - Bảng có `created_at`, `updated_at`, `deleted_at`
  - Dùng JWT + refresh token cho authentication
  - Role-based access (hiện tại chỉ user cá nhân)

---

## 10. User Flow tổng quát

1. **Tạo set mới**
2. **Bắt đầu chu kỳ học**
3. **Nhắc nhở ôn** (push/email/in-app)
4. **Nhập điểm số** hoặc skip
5. **Hoàn thành chu kỳ**, tính delay chu kỳ mới
6. **Quản lý overload** (max 3 set/ngày)
7. **Xem thống kê tiến trình và điểm số**

---

## 11. Triết lý vận hành

- Chỉ triển khai tính năng dùng hàng ngày.
- Ưu tiên trải nghiệm thực tế và cá nhân hóa.
- Cấu trúc backend & DB đơn giản, dễ maintain và scale.
