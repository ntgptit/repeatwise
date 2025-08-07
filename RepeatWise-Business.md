# RepeatWise – Business Specification

---

## 1. Tầm nhìn & Giá trị cốt lõi

- **RepeatWise** là ứng dụng quản lý học tập thông minh, giúp người học ghi nhớ lâu dài bằng cách nhắc ôn tập đúng thời điểm qua thuật toán **Spaced Repetition (SRS)**.
- Quản lý học theo **set** (học phần/chủ đề), không đi sâu flashcard, tối ưu thao tác và kiểm soát tiến trình.
- Trải nghiệm tối giản, phát triển tính năng thực sự hữu dụng, lấy cá nhân hóa làm trung tâm trước khi mở rộng cộng đồng/thương mại hóa.

---

## 2. Đơn vị quản lý: SET

- User được tạo **không giới hạn set** (mỗi set là một chủ đề/học phần).
- Mỗi set gồm: tên, mô tả, số lượng từ, trạng thái học (not_started, learning, reviewing, mastered), lịch sử các chu kỳ ôn tập.

---

## 3. Chu kỳ học & Quy trình SRS

- Mỗi set có **chu kỳ học gồm 5 lần ôn tập**.
- Sau 5 lần ôn, hệ thống tự đề xuất delay bắt đầu chu kỳ mới dựa trên:
    - Trung bình điểm số 5 lần ôn của chu kỳ trước (0~100%).
    - Số lượng từ vựng của set.
    - Điểm càng thấp → delay càng ngắn (ôn lại sớm), set nhiều từ → delay dài hơn (tránh overload).
- Chu kỳ **lặp lại liên tục**, không giới hạn số lần.

---

## 4. Ghi nhận & sử dụng điểm số

- Sau mỗi lần ôn, user **bắt buộc nhập điểm số** (0~100%).
- Lịch sử điểm từng lần ôn được lưu để phân tích, cá nhân hóa lịch học, thống kê tiến bộ.

---

## 5. Thuật toán tính lịch chu kỳ mới

next_cycle_delay_days = base_delay - penalty * (100 - avg_score) + scaling * word_count

- `base_delay`: Số ngày mặc định (ví dụ: 30)
- `penalty`: Hệ số giảm delay theo điểm (ví dụ: 0.2)
- `scaling`: Hệ số tăng delay theo số từ (ví dụ: 0.02)
- `avg_score`: Trung bình điểm 5 lần ôn gần nhất
- `word_count`: Số từ vựng của set
- **Giới hạn:** 7 ≤ delay ≤ 90 ngày

---

## 6. Quản lý lịch ôn & tránh overload

- **Mỗi ngày chỉ nhắc tối đa 3 set** (tránh quá tải cho user).
- Nếu quá 3 set tới hạn cùng ngày:
    1. Ưu tiên set quá hạn lâu nhất
    2. Ưu tiên set có điểm TB thấp hơn
    3. Các set còn lại tự động reschedule sang ngày tiếp theo
- Lịch học từng set được giãn tuyến tính theo số từ, đảm bảo không dồn set lớn vào cùng một ngày.

---

## 7. Reminder (Nhắc nhở học tập)

- Sử dụng bảng `remind_schedules` để lưu từng lần nhắc ôn cho từng set theo ngày.
- Trạng thái reminder: `pending`, `sent`, `done`, `skipped`, `rescheduled`, `cancelled`.
- Backend (cronjob hoặc scheduled task) sẽ:
    - Query reminder `pending` trong ngày, lấy tối đa 3 set/user.
    - Gửi notification (push/email/in-app).
    - Reminder vượt quá giới hạn sẽ tự động reschedule sang ngày khác.
    - Ghi nhận kết quả, log audit mọi thao tác (hoàn thành, reschedule, skip).

---

## 8. Re-schedule & Soft Delete

- User hoặc hệ thống có thể re-schedule reminder (dời ngày nhắc), lưu lịch sử thao tác (ai đổi, lúc nào, lý do).
- Soft delete dữ liệu qua trường `deleted_at` để rollback/audit, bảo vệ integrity.

---

## 9. Công nghệ & Kiến trúc

- **repeatwise-mobile:** React Native app (UI, notification, quản lý set).
- **repeatwise-server:** Spring Boot 3 (REST API, business logic, scheduling, notification).
- **repeatwise-db:** PostgreSQL (schema, migration, backup).
- Mọi bảng sử dụng UUID cho id, chuẩn hóa các trường `created_at`, `updated_at`, `deleted_at`.

---

## 10. User Flow tổng quát

1. Tạo set mới (tên, mô tả, số từ).
2. Bắt đầu chu kỳ học.
3. Mỗi lần ôn, app nhắc đúng ngày, user nhập điểm số.
4. Kết thúc 5 lần ôn, app tự động tính delay, tạo chu kỳ mới.
5. Mỗi ngày chỉ nhắc tối đa 3 set, reschedule phần dư.
6. Thống kê tiến trình học, điểm số, cảnh báo overload.

---

## 11. Triết lý vận hành

- **Chỉ xây cái thực sự dùng mỗi ngày.**
- **Ưu tiên trải nghiệm thực tế, tránh feature thừa.**
- **Đơn giản, dễ maintain, scale dễ dàng khi mở rộng.**
- **Hiệu quả với user cá nhân mới tính chuyện thương mại.**

---
