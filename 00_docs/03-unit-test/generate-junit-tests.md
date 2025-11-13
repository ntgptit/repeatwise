Ok, mình đã sửa lại để **thuần Markdown**, không lồng nhiều code block (chỉ dùng inline code). Dán thẳng vào Cursor là ổn.

# 🧪 Prompt: Generate JUnit Test cho Spring Boot Service

## 🎯 Mục tiêu

Sinh ra **JUnit test class** cho file service hiện tại trong Spring Boot, bao phủ đầy đủ luồng chính, nhánh điều kiện và xử lý lỗi.

## ⚙️ Yêu cầu tổng quát

* Framework: JUnit 5 + Mockito + AssertJ
* Annotation: `@ExtendWith(MockitoExtension.class)`
* Setup: mock toàn bộ dependency (`repository`, `client`, `service khác`), inject class đang test bằng `@InjectMocks`
* Phong cách: cấu trúc `given / when / then`, clean, không comment/print thừa

## 🧩 Phạm vi kiểm thử

1. **Success (Happy Path)**

   * Input hợp lệ → xử lý đúng logic
   * Kiểm tra giá trị trả về/trạng thái entity
   * Verify dependency được gọi đúng số lần và thứ tự
2. **Business Exception**

   * Vi phạm điều kiện nghiệp vụ → ném exception
   * Kiểm tra đúng loại exception và message
   * Không gọi bước sau khi đã fail
3. **Boundary / Optional**

   * Dữ liệu null, rỗng, `Optional.empty()`
   * Hành vi mong đợi: bỏ qua/phản hồi phù hợp, không crash
4. **Behavior Verification**

   * `verify(...)`, `verifyNoMoreInteractions(...)`
   * Dùng `ArgumentCaptor` để kiểm tra tham số truyền vào
5. **Fail Fast / Early Return**

   * Input không hợp lệ → dừng sớm, không gọi repository

## 🧠 Hướng dẫn sinh test theo logic

* Mỗi method: tối thiểu 1 case success + 1 case exception/invalid
* Với nhiều nhánh `if/else`, `switch`, `try-catch`, `optional.isPresent()` → test riêng cho từng nhánh
* Với `stream`/`filter`/`map` → test danh sách rỗng và danh sách hợp lệ
* Method `void` → tập trung verify side-effects
* Có transaction hoặc bất đồng bộ → thêm case lỗi trong quá trình xử lý

## 📛 Quy tắc đặt tên test

Dạng: `should_<ExpectedBehavior>_When_<Condition>()`
Ví dụ:

* `should_SaveEntity_When_InputValid()`
* `should_ThrowException_When_EntityAlreadyExists()`
* `should_ReturnEmptyList_When_NoDataFound()`

## ✅ Tiêu chí hoàn thành

* Bao phủ toàn bộ nhánh logic quan trọng
* Đảm bảo verify thứ tự và số lần tương tác cần thiết
* Không còn tương tác thừa sau khi hoàn tất (`verifyNoMoreInteractions`)
* Test biên và lỗi rõ ràng, tách bạch

## ⚡ Quick Command

> Generate JUnit 5 tests cho class service hiện tại, bao gồm success, business exception, boundary, verify behavior và fail-fast. Tập trung vào logic và hành vi mong đợi, không cần chèn code mẫu service.
