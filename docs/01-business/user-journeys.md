# User Journeys

## 1. Personas

### 1.1 Primary Persona: Minh - Sinh viên Đại học

**Demographics**:
- Tuổi: 20
- Nghề nghiệp: Sinh viên năm 2 ngành Công nghệ thông tin
- Trình độ: Trung cấp tiếng Anh
- Thiết bị: iPhone, laptop

**Goals**:
- Cải thiện vốn từ vựng tiếng Anh chuyên ngành
- Ôn tập hiệu quả cho kỳ thi TOEIC
- Quản lý thời gian học tập tốt hơn

**Pain Points**:
- Quên từ vựng đã học sau vài ngày
- Không biết khi nào nên ôn lại
- Bị quá tải khi có nhiều bài học cùng lúc
- Thiếu động lực duy trì thói quen học tập

**Motivations**:
- Đạt điểm cao trong kỳ thi
- Tự tin giao tiếp tiếng Anh
- Tiết kiệm thời gian học tập

### 1.2 Secondary Persona: Lan - Nhân viên văn phòng

**Demographics**:
- Tuổi: 28
- Nghề nghiệp: Nhân viên Marketing
- Trình độ: Cao cấp tiếng Anh
- Thiết bị: Android phone, tablet

**Goals**:
- Duy trì và nâng cao kỹ năng tiếng Anh
- Học từ vựng mới cho công việc
- Cân bằng giữa công việc và học tập

**Pain Points**:
- Ít thời gian học tập do công việc bận rộn
- Khó duy trì thói quen học tập đều đặn
- Cần học nhiều chủ đề khác nhau
- Muốn học hiệu quả trong thời gian ngắn

**Motivations**:
- Thăng tiến trong công việc
- Tự tin thuyết trình bằng tiếng Anh
- Mở rộng cơ hội nghề nghiệp

## 2. User Journey Maps

### 2.1 Journey 1: Onboarding Flow - Người dùng mới

**Persona**: Minh
**Goal**: Hiểu cách sử dụng app và tạo set đầu tiên

#### Journey Steps:

**Step 1: Tải và cài đặt app**
- **Action**: Tải app từ App Store, cài đặt
- **Touchpoint**: App Store, mobile app
- **Emotion**: Tò mò, hy vọng
- **Pain Point**: Không biết app có hiệu quả không

**Step 2: Đăng ký tài khoản**
- **Action**: Nhập email, mật khẩu, xác nhận email
- **Touchpoint**: Mobile app - Registration screen
- **Emotion**: Hồi hộp, mong đợi
- **Pain Point**: Có thể quên mật khẩu

**Step 3: Welcome tour**
- **Action**: Xem hướng dẫn về Spaced Repetition, cách tạo set
- **Touchpoint**: Mobile app - Onboarding screens
- **Emotion**: Hiểu biết, tự tin
- **Pain Point**: Có thể bỏ qua hướng dẫn

**Step 4: Tạo set mẫu**
- **Action**: Tạo set "Từ vựng TOEIC cơ bản" với 20 từ
- **Touchpoint**: Mobile app - Guided set creation
- **Emotion**: Thành công, có động lực
- **Pain Point**: Không biết nên chọn bao nhiêu từ

**Step 5: Bắt đầu chu kỳ học đầu tiên**
- **Action**: Xem lịch ôn tập 5 lần: 1, 3, 7, 14, 30 ngày
- **Touchpoint**: Mobile app - First learning cycle
- **Emotion**: Hào hứng, quyết tâm
- **Pain Point**: Lo lắng về việc theo được lịch

**Step 6: Nhận reminder đầu tiên**
- **Action**: Nhận notification sau 1 ngày, mở app ôn tập
- **Touchpoint**: Push notification, mobile app
- **Emotion**: Ngạc nhiên, thích thú
- **Pain Point**: Có thể quên hoặc bỏ qua

### 2.2 Journey 2: Empty States - Trạng thái trống

**Persona**: Minh
**Goal**: Hiểu và hành động khi gặp màn hình trống

#### Journey Steps:

**Step 1: Dashboard trống (chưa có set nào)**
- **Action**: Mở app lần đầu, thấy dashboard trống
- **Touchpoint**: Mobile app - Empty dashboard
- **Emotion**: Bối rối, không biết làm gì
- **Pain Point**: Không biết bắt đầu từ đâu
- **Solution**: Hiển thị CTA "Tạo set đầu tiên" với hướng dẫn

**Step 2: Lịch nhắc nhở trống**
- **Action**: Xem tab "Lịch học", thấy không có reminder nào
- **Touchpoint**: Mobile app - Empty calendar
- **Emotion**: Thất vọng, thiếu động lực
- **Pain Point**: Cảm thấy không có gì để học
- **Solution**: Hiển thị "Chưa có lịch học nào. Hãy tạo set mới!"

**Step 3: Thống kê trống**
- **Action**: Xem tab "Thống kê", thấy không có dữ liệu
- **Touchpoint**: Mobile app - Empty statistics
- **Emotion**: Không có cảm xúc, thiếu engagement
- **Pain Point**: Không thấy tiến bộ
- **Solution**: Hiển thị "Bắt đầu học để xem thống kê của bạn"

**Step 4: Set list trống sau khi xóa**
- **Action**: Xóa tất cả set, thấy danh sách trống
- **Touchpoint**: Mobile app - Empty set list
- **Emotion**: Hối hận, muốn khôi phục
- **Pain Point**: Mất dữ liệu học tập
- **Solution**: Hiển thị "Không có set nào. Tạo set mới hoặc khôi phục từ backup"

### 2.3 Journey 3: Error Handling - Xử lý lỗi người dùng

**Persona**: Lan
**Goal**: Hiểu cách xử lý khi nhập dữ liệu không hợp lệ

#### Journey Steps:

**Step 1: Nhập điểm không hợp lệ**
- **Action**: Nhập điểm 150% thay vì 100%
- **Touchpoint**: Mobile app - Score input screen
- **Emotion**: Bối rối, không hiểu lỗi
- **Pain Point**: Không biết điểm tối đa là bao nhiêu
- **Solution**: Hiển thị validation message "Điểm phải từ 0-100%"

**Step 2: Để trống tên set**
- **Action**: Tạo set mới nhưng không nhập tên
- **Touchpoint**: Mobile app - Create set screen
- **Emotion**: Vội vàng, bỏ sót
- **Pain Point**: Không biết tại sao không thể tạo
- **Solution**: Highlight field tên set và hiển thị "Tên set là bắt buộc"

**Step 3: Nhập word_count = 0**
- **Action**: Nhập số từ vựng = 0
- **Touchpoint**: Mobile app - Set creation
- **Emotion**: Nhầm lẫn, không hiểu logic
- **Pain Point**: Không biết tại sao không được
- **Solution**: Hiển thị "Số từ vựng phải lớn hơn 0"

**Step 4: Tên set quá dài**
- **Action**: Nhập tên set dài hơn 100 ký tự
- **Touchpoint**: Mobile app - Set creation
- **Emotion**: Bực mình, muốn đặt tên chi tiết
- **Pain Point**: Bị giới hạn không mong muốn
- **Solution**: Hiển thị character counter và "Tên set tối đa 100 ký tự"

**Step 5: Mất kết nối mạng**
- **Action**: Mất mạng khi đang nhập điểm
- **Touchpoint**: Mobile app - Score input
- **Emotion**: Lo lắng, sợ mất dữ liệu
- **Pain Point**: Không biết dữ liệu có được lưu không
- **Solution**: Hiển thị "Mất kết nối. Dữ liệu sẽ được lưu khi có mạng"

### 2.4 Journey 4: Tạo và Bắt đầu Học Set Mới

**Persona**: Minh
**Goal**: Tạo set từ vựng TOEIC và bắt đầu học

#### Journey Steps:

**Step 1: Khám phá ứng dụng**
- **Action**: Tải app, đăng ký tài khoản
- **Touchpoint**: App Store, mobile app
- **Emotion**: Tò mò, hy vọng
- **Pain Point**: Không biết app có hiệu quả không

**Step 2: Tạo set đầu tiên**
- **Action**: Nhấn "Tạo set mới", nhập thông tin
- **Touchpoint**: Mobile app - Create Set screen
- **Emotion**: Hồi hộp, mong đợi
- **Pain Point**: Không biết nên đặt tên set như thế nào

**Step 3: Xác nhận thông tin**
- **Action**: Review thông tin set, nhấn "Tạo"
- **Touchpoint**: Mobile app - Set confirmation
- **Emotion**: Tự tin, quyết tâm
- **Pain Point**: Lo lắng về số lượng từ vựng

**Step 4: Bắt đầu chu kỳ học**
- **Action**: Nhấn "Bắt đầu học", xem lịch ôn tập
- **Touchpoint**: Mobile app - Learning dashboard
- **Emotion**: Hào hứng, có động lực
- **Pain Point**: Không biết có theo được lịch không

**Step 5: Nhận reminder đầu tiên**
- **Action**: Nhận notification, mở app ôn tập
- **Touchpoint**: Push notification, mobile app
- **Emotion**: Ngạc nhiên, thích thú
- **Pain Point**: Có thể quên hoặc bỏ qua

### 2.5 Journey 5: Ôn tập và Nhập điểm

**Persona**: Minh
**Goal**: Hoàn thành lần ôn tập và nhập điểm số

#### Journey Steps:

**Step 1: Nhận nhắc nhở**
- **Action**: Nhận push notification về lần ôn
- **Touchpoint**: Push notification
- **Emotion**: Nhắc nhở, có thể hơi căng thẳng
- **Pain Point**: Có thể bận rộn không thể ôn ngay

**Step 2: Mở app và xem set cần ôn**
- **Action**: Mở app, xem danh sách set cần ôn
- **Touchpoint**: Mobile app - Dashboard
- **Emotion**: Tập trung, quyết tâm
- **Pain Point**: Có thể quên nội dung đã học

**Step 3: Ôn tập nội dung**
- **Action**: Xem lại từ vựng, tự đánh giá mức độ nhớ
- **Touchpoint**: Mobile app - Review screen
- **Emotion**: Tập trung, tự đánh giá
- **Pain Point**: Khó đánh giá chính xác mức độ nhớ

**Step 4: Nhập điểm số**
- **Action**: Chọn điểm từ 0-100%, nhấn "Lưu"
- **Touchpoint**: Mobile app - Score input
- **Emotion**: Thành thật với bản thân
- **Pain Point**: Có thể chủ quan hoặc quá nghiêm khắc

**Step 5: Xem phản hồi**
- **Action**: Xem thông báo hoàn thành, lịch ôn tiếp theo
- **Touchpoint**: Mobile app - Success screen
- **Emotion**: Hài lòng, có động lực
- **Pain Point**: Có thể thất vọng nếu điểm thấp

### 2.6 Journey 6: Quản lý Overload và Reschedule

**Persona**: Lan
**Goal**: Quản lý khi có nhiều set cần ôn cùng lúc

#### Journey Steps:

**Step 1: Nhận thông báo quá tải**
- **Action**: Nhận notification về 3+ set cần ôn
- **Touchpoint**: Push notification, email
- **Emotion**: Lo lắng, căng thẳng
- **Pain Point**: Không biết nên ưu tiên set nào

**Step 2: Xem danh sách set ưu tiên**
- **Action**: Mở app, xem danh sách set được ưu tiên
- **Touchpoint**: Mobile app - Priority list
- **Emotion**: Rõ ràng, có định hướng
- **Pain Point**: Có thể không đồng ý với thứ tự ưu tiên

**Step 3: Ôn tập set ưu tiên**
- **Action**: Ôn tập 3 set được chọn, nhập điểm
- **Touchpoint**: Mobile app - Review flow
- **Emotion**: Tập trung, hiệu quả
- **Pain Point**: Có thể mệt mỏi sau 3 lần ôn

**Step 4: Xem lịch reschedule**
- **Action**: Xem các set được dời sang ngày khác
- **Touchpoint**: Mobile app - Rescheduled list
- **Emotion**: Yên tâm, có kế hoạch
- **Pain Point**: Có thể quên các set bị dời

**Step 5: Tùy chỉnh lịch học**
- **Action**: Reschedule một số set theo lịch cá nhân
- **Touchpoint**: Mobile app - Reschedule feature
- **Emotion**: Kiểm soát, linh hoạt
- **Pain Point**: Có thể lạm dụng reschedule

### 2.7 Journey 7: Theo dõi tiến trình và thống kê

**Persona**: Lan
**Goal**: Xem thống kê học tập và đánh giá hiệu quả

#### Journey Steps:

**Step 1: Truy cập dashboard**
- **Action**: Mở app, chuyển sang tab "Thống kê"
- **Touchpoint**: Mobile app - Statistics screen
- **Emotion**: Tò mò, mong đợi
- **Pain Point**: Có thể khó hiểu các chỉ số

**Step 2: Xem tổng quan**
- **Action**: Xem tổng số set, chu kỳ hoàn thành, điểm trung bình
- **Touchpoint**: Mobile app - Overview stats
- **Emotion**: Tự hào hoặc thất vọng
- **Pain Point**: Có thể so sánh với mục tiêu

**Step 3: Phân tích chi tiết**
- **Action**: Xem thống kê từng set, xu hướng điểm số
- **Touchpoint**: Mobile app - Detailed analytics
- **Emotion**: Hiểu biết, có insight
- **Pain Point**: Có thể phát hiện vấn đề

**Step 4: Điều chỉnh chiến lược**
- **Action**: Dựa trên thống kê, điều chỉnh cách học
- **Touchpoint**: Mobile app - Settings, learning strategy
- **Emotion**: Quyết tâm cải thiện
- **Pain Point**: Có thể không biết cách cải thiện

**Step 5: Đặt mục tiêu mới**
- **Action**: Đặt mục tiêu cho chu kỳ tiếp theo
- **Touchpoint**: Mobile app - Goal setting
- **Emotion**: Mong đợi, có động lực
- **Pain Point**: Có thể đặt mục tiêu không thực tế

### 2.8 Journey 8: Cá nhân hóa thuật toán SRS

**Persona**: Lan
**Goal**: Tùy chỉnh thuật toán phù hợp với khả năng ghi nhớ

#### Journey Steps:

**Step 1: Nhận thông báo về tính năng nâng cao**
- **Action**: Nhận notification về tính năng "Cá nhân hóa thuật toán"
- **Touchpoint**: Push notification, in-app notification
- **Emotion**: Tò mò, quan tâm
- **Pain Point**: Không biết có nên thay đổi không

**Step 2: Xem hướng dẫn về các tùy chọn**
- **Action**: Đọc giải thích về 3 mức độ: Basic, Advanced, Custom
- **Touchpoint**: Mobile app - Algorithm customization guide
- **Emotion**: Hiểu biết, tự tin
- **Pain Point**: Có thể khó hiểu các thuật ngữ kỹ thuật

**Step 3: Chọn mức độ phù hợp**
- **Action**: Chọn "Advanced" để tùy chỉnh hệ số
- **Touchpoint**: Mobile app - Algorithm level selection
- **Emotion**: Quyết tâm, mong đợi
- **Pain Point**: Không biết nên chọn giá trị nào

**Step 4: Tùy chỉnh các hệ số**
- **Action**: Điều chỉnh base_delay=25, penalty=0.3, scaling=0.03
- **Touchpoint**: Mobile app - Coefficient adjustment
- **Emotion**: Kiểm soát, tùy chỉnh
- **Pain Point**: Có thể đặt giá trị không phù hợp

**Step 5: Chọn chuỗi ngày ôn tập**
- **Action**: Chọn "Balanced" thay vì "Conservative"
- **Touchpoint**: Mobile app - Interval selection
- **Emotion**: Tối ưu hóa, hiệu quả
- **Pain Point**: Có thể chọn quá aggressive

**Step 6: Xem preview thay đổi**
- **Action**: Xem ảnh hưởng của thay đổi lên lịch học
- **Touchpoint**: Mobile app - Change preview
- **Emotion**: Yên tâm, chắc chắn
- **Pain Point**: Có thể thấy thay đổi quá lớn

**Step 7: Áp dụng thay đổi**
- **Action**: Nhấn "Áp dụng", xác nhận thay đổi
- **Touchpoint**: Mobile app - Apply changes
- **Emotion**: Mong đợi, có động lực
- **Pain Point**: Có thể ảnh hưởng đến tiến độ hiện tại

### 2.9 Journey 9: Ngữ cảnh điểm số - Mức độ tự tin

**Persona**: Minh
**Goal**: Sử dụng mức độ tự tin thay vì điểm số để đánh giá

#### Journey Steps:

**Step 1: Khám phá tính năng mới**
- **Action**: Thấy toggle "Mức độ tự tin" trong settings
- **Touchpoint**: Mobile app - Settings screen
- **Emotion**: Tò mò, thích thú
- **Pain Point**: Không biết có nên thử không

**Step 2: Đọc hướng dẫn**
- **Action**: Đọc giải thích về 3 mức độ: Dễ, Bình thường, Khó
- **Touchpoint**: Mobile app - Confidence level guide
- **Emotion**: Hiểu biết, tự tin
- **Pain Point**: Không biết mức độ nào phù hợp

**Step 3: Bật tính năng**
- **Action**: Toggle sang "Mức độ tự tin", lưu setting
- **Touchpoint**: Mobile app - Toggle confidence mode
- **Emotion**: Thử nghiệm, mong đợi
- **Pain Point**: Có thể không quen với cách mới

**Step 4: Ôn tập với mức độ tự tin**
- **Action**: Ôn tập set, chọn "Bình thường" thay vì nhập điểm
- **Touchpoint**: Mobile app - Confidence level input
- **Emotion**: Dễ dàng, thoải mái
- **Pain Point**: Có thể không chính xác như điểm số

**Step 5: Xem kết quả quy đổi**
- **Action**: Xem điểm số được quy đổi (ví dụ: 72%)
- **Touchpoint**: Mobile app - Converted score display
- **Emotion**: Thích thú, hiểu biết
- **Pain Point**: Có thể không đồng ý với điểm quy đổi

**Step 6: So sánh với lịch sử**
- **Action**: Xem lịch sử có cả mức độ tự tin và điểm số
- **Touchpoint**: Mobile app - History with confidence levels
- **Emotion**: Rõ ràng, minh bạch
- **Pain Point**: Có thể khó so sánh với dữ liệu cũ

**Step 7: Điều chỉnh preference**
- **Action**: Chuyển về chế độ điểm số hoặc giữ nguyên
- **Touchpoint**: Mobile app - Preference adjustment
- **Emotion**: Kiểm soát, linh hoạt
- **Pain Point**: Có thể không quyết định được

### 2.10 Journey 10: Reschedule với lý do tracking

**Persona**: Lan
**Goal**: Reschedule reminder với lý do cụ thể

#### Journey Steps:

**Step 1: Nhận thông báo quá tải**
- **Action**: Nhận notification "3 set cần ôn hôm nay"
- **Touchpoint**: Push notification
- **Emotion**: Căng thẳng, lo lắng
- **Pain Point**: Không biết nên ưu tiên set nào

**Step 2: Xem danh sách set được reschedule**
- **Action**: Mở app, xem set nào bị dời lịch
- **Touchpoint**: Mobile app - Rescheduled sets list
- **Emotion**: Rõ ràng, có kế hoạch
- **Pain Point**: Có thể không đồng ý với lý do

**Step 3: Xem lý do reschedule**
- **Action**: Xem reason "system_overload" cho set bị dời
- **Touchpoint**: Mobile app - Reschedule reason display
- **Emotion**: Hiểu biết, chấp nhận
- **Pain Point**: Có thể muốn thay đổi lý do

**Step 4: Manual reschedule với lý do**
- **Action**: Reschedule một set với lý do "user_busy"
- **Touchpoint**: Mobile app - Manual reschedule
- **Emotion**: Kiểm soát, linh hoạt
- **Pain Point**: Có thể lạm dụng reschedule

**Step 5: Xem lịch sử reschedule**
- **Action**: Xem tất cả lý do reschedule trong tháng
- **Touchpoint**: Mobile app - Reschedule history
- **Emotion**: Minh bạch, có insight
- **Pain Point**: Có thể thấy pattern tiêu cực

**Step 6: Nhận gợi ý từ hệ thống**
- **Action**: Nhận suggestion "Bạn thường reschedule vào thứ 2"
- **Touchpoint**: Mobile app - System suggestion
- **Emotion**: Được hỗ trợ, thông minh
- **Pain Point**: Có thể cảm thấy bị theo dõi

## 3. Pain Points và Solutions

### 3.1 Pain Point: Quên ôn tập
**Problem**: User quên mở app để ôn tập
**Solution**: 
- Push notification nhắc nhở
- Email reminder
- In-app notification
- Calendar integration

### 3.2 Pain Point: Không biết đánh giá điểm
**Problem**: User khó đánh giá chính xác mức độ nhớ
**Touchpoint**: Mobile app - Score input
**Solution**:
- Hướng dẫn cách đánh giá
- Ví dụ cụ thể cho từng mức điểm
- Gợi ý dựa trên lịch sử

### 3.3 Pain Point: Quá tải khi có nhiều set
**Problem**: User bị stress khi có 3+ set cần ôn
**Solution**:
- Tự động ưu tiên set quan trọng
- Reschedule tự động
- Giải thích lý do ưu tiên
- Cho phép tùy chỉnh

### 3.4 Pain Point: Thiếu động lực
**Problem**: User dễ bỏ cuộc khi không thấy tiến bộ
**Solution**:
- Hiển thị tiến bộ rõ ràng
- Streak counter
- Achievement badges
- So sánh với mục tiêu

### 3.5 Pain Point: Khó hiểu thống kê
**Problem**: User không hiểu ý nghĩa các chỉ số
**Solution**:
- Giải thích đơn giản
- Visual charts và graphs
- So sánh với chuẩn
- Gợi ý cải thiện

### 3.6 Pain Point: Màn hình trống gây bối rối
**Problem**: User không biết làm gì khi gặp màn hình trống
**Solution**:
- Hiển thị CTA rõ ràng
- Hướng dẫn từng bước
- Ví dụ cụ thể
- Khuyến khích hành động

### 3.7 Pain Point: Lỗi validation không rõ ràng
**Problem**: User không hiểu tại sao input bị từ chối
**Solution**:
- Thông báo lỗi cụ thể
- Highlight field lỗi
- Gợi ý cách sửa
- Validation real-time

### 3.8 Pain Point: Khó tùy chỉnh thuật toán
**Problem**: User không biết nên đặt giá trị nào cho các hệ số
**Solution**:
- Hướng dẫn chi tiết từng hệ số
- Preview ảnh hưởng của thay đổi
- Gợi ý dựa trên hiệu suất học tập
- Có thể reset về mặc định

### 3.9 Pain Point: Mức độ tự tin không chính xác
**Problem**: User không chắc chắn về mức độ tự tin
**Solution**:
- Giải thích rõ ràng từng mức độ
- Ví dụ cụ thể cho từng level
- Có thể chuyển đổi giữa 2 chế độ
- Hiển thị điểm quy đổi để tham khảo

### 3.10 Pain Point: Reschedule quá nhiều
**Problem**: User lạm dụng tính năng reschedule
**Solution**:
- Giới hạn số lần reschedule
- Hiển thị pattern reschedule
- Gợi ý thời gian học tốt hơn
- Cảnh báo khi reschedule quá nhiều

## 4. Success Metrics

### 4.1 Engagement Metrics
- **Daily Active Users**: Số user mở app hàng ngày
- **Session Duration**: Thời gian sử dụng app mỗi lần
- **Retention Rate**: Tỷ lệ user quay lại sau 7, 30, 90 ngày
- **Completion Rate**: Tỷ lệ hoàn thành chu kỳ học

### 4.2 Learning Effectiveness
- **Score Improvement**: Cải thiện điểm số trung bình
- **Cycle Completion**: Số chu kỳ hoàn thành
- **Mastery Rate**: Tỷ lệ set đạt trạng thái mastered
- **Time to Mastery**: Thời gian để master một set

### 4.3 User Satisfaction
- **App Store Rating**: Đánh giá trên app store
- **User Feedback**: Feedback từ user
- **Support Tickets**: Số lượng ticket hỗ trợ
- **Feature Usage**: Mức độ sử dụng các tính năng

### 4.4 Onboarding Effectiveness
- **Onboarding Completion Rate**: Tỷ lệ hoàn thành onboarding
- **Time to First Set**: Thời gian từ đăng ký đến tạo set đầu tiên
- **First Review Completion**: Tỷ lệ hoàn thành lần ôn đầu tiên
- **Error Rate**: Tỷ lệ lỗi trong quá trình onboarding

### 4.5 Personalization Effectiveness
- **Algorithm Customization Rate**: Tỷ lệ user tùy chỉnh thuật toán
- **Confidence Mode Adoption**: Tỷ lệ user sử dụng mức độ tự tin
- **Reschedule Reason Tracking**: Tỷ lệ reschedule có lý do
- **Personalization Impact**: Ảnh hưởng của cá nhân hóa lên hiệu suất học tập
