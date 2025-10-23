# Hướng dẫn trò chuyện với chatbot AI (Claude Code/Codex)

Mục tiêu của tài liệu là giúp bạn chuẩn bị ngữ cảnh, điều phối hội thoại và tiết kiệm token khi làm việc với Claude. Kết hợp hướng dẫn này với các prompt pack trong thư mục để giữ chất lượng trao đổi ổn định.

## 1. Chuẩn bị trước khi mở hội thoại
- **Xác định mục tiêu cụ thể**: ghi rõ bạn muốn Claude làm gì (sửa lỗi, viết API, kiểm tra test, v.v.).
- **Chọn prompt pack phù hợp**: tra cứu `README.md` và nạp đúng file trong `01-claude-prompts` liên quan đến nhiệm vụ.
- **Đặt giới hạn phạm vi**: nếu yêu cầu dài, chia thành các nhánh công việc nhỏ và xử lý lần lượt.
- **Kiểm tra tài liệu nguồn**: mở sẵn các file trong `00_docs` để khi cần có thể trích dẫn nhanh mà không phải copy toàn bộ.

## 2. Mở đầu cuộc trò chuyện
1. Paste đoạn giới thiệu ngắn gồm:
   - bối cảnh (RepeatWise là gì, thành phần nào đang làm việc),
   - nhiệm vụ cụ thể trong phiên này,
   - tên prompt pack hoặc tài liệu bạn sẽ cung cấp.
2. Yêu cầu Claude nhắc lại mục tiêu để xác nhận hiểu đúng. Nếu Claude trả lời sai, chỉnh lại ngay trước khi tiếp tục.

## 3. Cách cung cấp thông tin bổ sung
- **Từng phần nhỏ (≤150 dòng)**: chia tài liệu lớn thành các khối và yêu cầu Claude tóm tắt mỗi khối.
- **Truy xuất có chủ đích**: chỉ gửi đoạn trích chứa thông tin Claude cần để đưa ra quyết định. Tránh gửi toàn bộ đặc tả khi chỉ cần một API hoặc rule cụ thể.
- **Giữ dấu vết nguồn**: luôn đính kèm đường dẫn file và số dòng chính (ví dụ `00_docs/03-design/...`) để tiện rà soát.

## 4. Điều phối phản hồi của Claude
- **Khuyến khích lập kế hoạch**: yêu cầu Claude đề xuất các bước thực hiện trước khi viết code.
- **So sánh với tài liệu**: nhắc Claude đối chiếu với pseudo code, validation rules hoặc API specs tương ứng trong prompt pack.
- **Đặt checkpoint**: sau mỗi phần hoàn thành (ví dụ implement service, viết test), yêu cầu Claude tự kiểm tra lại điều kiện chấp nhận.

## 5. Quản lý token
- Giữ mỗi thông điệp dưới ~500 token khi có thể.
- Khi cuộc trò chuyện vượt 6K token, đóng session và mở lại. Trong phiên mới, chỉ gửi các prompt pack cốt lõi và bản tóm tắt quyết định đã chốt.
- Dọn dẹp thông tin thừa: nếu Claude lặp lại nội dung, nhắc gọn lại hoặc yêu cầu liệt kê bullet point thay vì giải thích dài dòng.

## 6. Hậu kiểm sau khi nhận kết quả
- **Rà soát thủ công**: đối chiếu output với `00_docs` và tiêu chuẩn ở `11-coding-standards` trước khi commit.
- **Ghi chú nợ kỹ thuật**: nếu Claude đưa ra giả định, ghi lại trong PR/ticket để nhóm xác nhận.
- **Lưu prompt hữu ích**: nếu hội thoại hiệu quả, trích các đoạn chỉ dẫn thành prompt pack mới hoặc cập nhật tài liệu hiện có.

## 7. Mẹo xử lý lỗi thường gặp
- Claude quên ngữ cảnh → nhắc lại nhiệm vụ bằng 2-3 câu và resend prompt pack chính.
- Claude sinh code thiếu file → yêu cầu cung cấp danh sách file dự kiến và đối chiếu với kiến trúc trong `02-implementation-guides`.
- Claude trả lời chung chung → hướng dẫn đưa ví dụ cụ thể từ use case hoặc API, hoặc đặt câu hỏi yes/no để thu hẹp đáp án.

Luôn ưu tiên minh bạch về nguồn và phạm vi để giữ chất lượng câu trả lời ổn định dù token hạn chế.
