# Business Requirements Document (BRD)

## 1. System Objectives

### 1.1 Primary Objective
**RepeatWise** là ứng dụng quản lý học tập thông minh, giúp người học ghi nhớ lâu dài thông qua thuật toán **Spaced Repetition (SRS)**, tối ưu lịch ôn dựa trên điểm số và dung lượng nội dung.

### 1.2 Secondary Objectives
- Cung cấp trải nghiệm học tập tối giản, tập trung vào hiệu quả
- Quản lý học theo **set** (học phần/chủ đề) thay vì flashcard cá nhân
- Cá nhân hóa lịch học dựa trên tiến độ và khả năng của người dùng
- Tránh quá tải thông qua giới hạn 3 set/ngày

## 2. Business Scope

### 2.1 In-Scope
- **Quản lý Set học tập**: Tạo, chỉnh sửa, xóa set với thông tin cơ bản
- **Chu kỳ học SRS**: 5 lần ôn tập/chu kỳ với thuật toán tính delay thông minh
- **Ghi nhận điểm số**: Bắt buộc nhập điểm (0-100%) sau mỗi lần ôn
- **Lịch nhắc nhở**: Tự động tạo reminder và gửi notification
- **Quản lý overload**: Giới hạn tối đa 3 set/ngày, tự động reschedule
- **Thống kê tiến trình**: Theo dõi điểm số, chu kỳ học, hiệu quả
- **Re-schedule**: Cho phép thay đổi lịch học với lịch sử audit

### 2.2 Out-of-Scope
- **Flashcard chi tiết**: Không quản lý từng flashcard riêng lẻ
- **Cộng đồng học tập**: Chưa có tính năng chia sẻ, thi đua
- **Thương mại hóa**: Chưa có tính năng premium, subscription
- **Đa ngôn ngữ**: Chỉ hỗ trợ tiếng Việt và tiếng Anh
- **Offline mode**: Yêu cầu kết nối internet để đồng bộ dữ liệu

## 3. Core Values

### 3.1 Tối giản (Minimalism)
- Giao diện đơn giản, dễ sử dụng
- Chỉ tập trung vào tính năng cốt lõi
- Giảm thiểu thao tác phức tạp

### 3.2 Cá nhân hóa (Personalization)
- Thuật toán SRS thích ứng với từng người dùng
- Lịch học được tối ưu theo tiến độ cá nhân
- Thống kê chi tiết phản ánh hiệu quả học tập

### 3.3 Hiệu quả (Effectiveness)
- Tối ưu thời gian ôn tập đúng thời điểm
- Tránh quá tải thông qua giới hạn hợp lý
- Đo lường và cải thiện liên tục

## 4. Business Drivers

### 4.1 Market Need
- **Vấn đề hiện tại**: Người học thường quên kiến thức do không ôn tập đúng cách
- **Giải pháp**: Áp dụng khoa học Spaced Repetition để tối ưu lịch ôn
- **Lợi ích**: Ghi nhớ lâu dài, tiết kiệm thời gian, tăng hiệu quả học tập

### 4.2 Competitive Advantage
- **Tập trung vào Set**: Khác biệt với các app flashcard truyền thống
- **Thuật toán thông minh**: Tự động tính toán lịch học tối ưu
- **Quản lý overload**: Tránh quá tải, duy trì động lực học tập

### 4.3 Implementation Motivation
- **MVP Approach**: Bắt đầu với tính năng cốt lõi, validate với user thực tế
- **Scalable Architecture**: Thiết kế để dễ dàng mở rộng tính năng
- **Data-Driven**: Thu thập dữ liệu để cải thiện thuật toán liên tục

## 5. Success Metrics

### 5.1 User Engagement
- **Retention Rate**: Tỷ lệ người dùng tiếp tục sử dụng sau 7, 30, 90 ngày
- **Completion Rate**: Tỷ lệ hoàn thành chu kỳ học (5 lần ôn)
- **Active Sets**: Số lượng set đang học tích cực

### 5.2 Learning Effectiveness
- **Score Improvement**: Cải thiện điểm số trung bình qua các chu kỳ
- **Cycle Completion Time**: Thời gian hoàn thành chu kỳ
- **Mastery Rate**: Tỷ lệ set đạt trạng thái "mastered"

### 5.3 System Performance
- **Reminder Accuracy**: Tỷ lệ reminder được gửi đúng thời gian
- **Overload Prevention**: Hiệu quả của việc giới hạn 3 set/ngày
- **User Satisfaction**: Đánh giá từ người dùng về trải nghiệm

## 6. Stakeholder Benefits

### 6.1 End Users (Students)
- **Ghi nhớ lâu dài**: Áp dụng khoa học SRS
- **Tiết kiệm thời gian**: Lịch học được tối ưu tự động
- **Tránh quá tải**: Giới hạn hợp lý, duy trì động lực

### 6.2 Development Team
- **Clear Scope**: Phạm vi rõ ràng, tập trung vào MVP
- **Scalable Design**: Kiến trúc dễ mở rộng
- **Data Insights**: Dữ liệu để cải thiện sản phẩm

### 6.3 Business Owners
- **Market Validation**: Test ý tưởng với user thực tế
- **Growth Potential**: Cơ sở để mở rộng tính năng
- **Competitive Edge**: Điểm khác biệt trong thị trường edtech 
