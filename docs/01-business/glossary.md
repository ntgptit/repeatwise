# Glossary

## A

**API (Application Programming Interface)**
- **Định nghĩa**: Giao diện lập trình ứng dụng, cho phép các hệ thống giao tiếp với nhau
- **Context**: REST API cho mobile app và backend server

**Audit Log**
- **Định nghĩa**: Nhật ký ghi lại tất cả các thao tác thay đổi dữ liệu
- **Context**: Lưu trữ lịch sử thay đổi điểm số, reschedule, soft delete

## B

**Backend**
- **Định nghĩa**: Phần mềm chạy trên server, xử lý logic nghiệp vụ và dữ liệu
- **Context**: Spring Boot 3 application xử lý SRS logic và scheduling

**Base Delay**
- **Định nghĩa**: Số ngày mặc định giữa các chu kỳ học
- **Context**: Tham số trong thuật toán tính delay (mặc định 30 ngày)

## C

**Cycle (Chu kỳ học)**
- **Định nghĩa**: Một vòng học tập gồm 5 lần ôn tập
- **Context**: Đơn vị cơ bản của quy trình SRS

**Category**
- **Định nghĩa**: Phân loại set học tập (vocabulary, grammar, mixed, other)
- **Context**: Dùng cho thống kê và lọc set

## D

**Delay**
- **Định nghĩa**: Khoảng thời gian chờ trước khi bắt đầu chu kỳ học mới
- **Context**: Được tính toán dựa trên điểm số và số lượng từ

**DTO (Data Transfer Object)**
- **Định nghĩa**: Đối tượng truyền dữ liệu giữa các layer
- **Context**: Chuyển đổi dữ liệu giữa API và database

## E

**Entity**
- **Định nghĩa**: Đối tượng dữ liệu trong database
- **Context**: Các bảng như sets, review_histories, remind_schedules

## F

**Frontend**
- **Định nghĩa**: Giao diện người dùng, phần mềm chạy trên thiết bị
- **Context**: Flutter mobile application

## H

**History (Lịch sử)**
- **Định nghĩa**: Ghi lại các thao tác và kết quả học tập
- **Context**: review_histories lưu điểm số từng lần ôn

## I

**Idempotency**
- **Định nghĩa**: Tính chất của API, gọi nhiều lần cho cùng kết quả
- **Context**: Đảm bảo không tạo duplicate reminder

## J

**JWT (JSON Web Token)**
- **Định nghĩa**: Token xác thực người dùng
- **Context**: Authentication cho API calls

## L

**Learning**
- **Định nghĩa**: Trạng thái đang học của set
- **Context**: Một trong các trạng thái: not_started, learning, reviewing, mastered

## M

**Mastered**
- **Định nghĩa**: Trạng thái đã thành thạo của set
- **Context**: Khi user đạt điểm số cao và ổn định

**MVP (Minimum Viable Product)**
- **Định nghĩa**: Sản phẩm tối thiểu có thể sử dụng
- **Context**: Phiên bản đầu tiên với tính năng cốt lõi

## N

**Notification**
- **Định nghĩa**: Thông báo nhắc nhở học tập
- **Context**: Push notification, email, in-app message

## O

**Overload**
- **Định nghĩa**: Quá tải, vượt quá giới hạn cho phép
- **Context**: Giới hạn tối đa 3 set/ngày để tránh quá tải

## P

**Penalty**
- **Định nghĩa**: Hệ số giảm delay theo điểm số
- **Context**: Tham số trong thuật toán tính delay (mặc định 0.2)

**Pending**
- **Định nghĩa**: Trạng thái chờ xử lý
- **Context**: Reminder đang chờ được gửi

## R

**Reminder (Nhắc nhở)**
- **Định nghĩa**: Lịch nhắc nhở ôn tập
- **Context**: Bảng remind_schedules lưu lịch nhắc nhở

**Repository**
- **Định nghĩa**: Lớp truy cập dữ liệu
- **Context**: Spring Data JPA repositories

**Reschedule**
- **Định nghĩa**: Thay đổi lịch học
- **Context**: Dời ngày reminder sang ngày khác

**Review**
- **Định nghĩa**: Lần ôn tập
- **Context**: Mỗi chu kỳ có 5 lần review

## S

**Scaling**
- **Định nghĩa**: Hệ số tăng delay theo số lượng từ
- **Context**: Tham số trong thuật toán tính delay (mặc định 0.02)

**Set**
- **Định nghĩa**: Đơn vị học tập, một chủ đề/học phần
- **Context**: Chứa thông tin tên, mô tả, số từ, trạng thái

**Skipped**
- **Định nghĩa**: Trạng thái bỏ qua lần ôn
- **Context**: User không hoàn thành lần ôn

**Soft Delete**
- **Định nghĩa**: Xóa mềm, chỉ đánh dấu deleted_at
- **Context**: Bảo toàn dữ liệu cho audit và rollback

**SRS (Spaced Repetition System)**
- **Định nghĩa**: Hệ thống lặp lại có khoảng cách
- **Context**: Thuật toán tối ưu lịch ôn tập

**Status**
- **Định nghĩa**: Trạng thái hiện tại
- **Context**: Trạng thái set (not_started, learning, reviewing, mastered)

## T

**Token**
- **Định nghĩa**: Mã xác thực người dùng
- **Context**: JWT token cho authentication

## U

**UUID**
- **Định nghĩa**: Universal Unique Identifier
- **Context**: ID duy nhất cho tất cả entities

**User**
- **Định nghĩa**: Người dùng hệ thống
- **Context**: Học viên sử dụng ứng dụng

## V

**Vocabulary**
- **Định nghĩa**: Từ vựng
- **Context**: Một category của set học tập

## W

**Word Count**
- **Định nghĩa**: Số lượng từ trong set
- **Context**: Tham số ảnh hưởng đến delay chu kỳ

---

## Abbreviations

| Abbreviation | Full Form | Vietnamese |
|--------------|-----------|------------|
| API | Application Programming Interface | Giao diện lập trình ứng dụng |
| BRD | Business Requirements Document | Tài liệu yêu cầu nghiệp vụ |
| DTO | Data Transfer Object | Đối tượng truyền dữ liệu |
| JWT | JSON Web Token | Token xác thực JSON |
| MVP | Minimum Viable Product | Sản phẩm tối thiểu khả thi |
| SRS | Spaced Repetition System | Hệ thống lặp lại có khoảng cách |
| UUID | Universal Unique Identifier | Định danh duy nhất toàn cầu | 
