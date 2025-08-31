# Business Rules Gap Fixes - RepeatWise

## Tổng quan

Tài liệu này tóm tắt các gap nghiệp vụ đã được phát hiện và bổ sung trong hệ thống RepeatWise, bao gồm:

1. **Khoảng thời gian nghỉ trong chu kỳ**
2. **Định nghĩa trạng thái "Mastered"**

## 1. Khoảng thời gian nghỉ trong chu kỳ

### Vấn đề ban đầu
- `business-rules.md` chưa định nghĩa rõ khoảng cách giữa 5 lần ôn tập trong cùng một chu kỳ
- UC-011 và UC-012 chưa thể mô tả được logic tạo lịch cho các lần ôn tập

### Giải pháp đã thực hiện

#### 1.1 Cập nhật BR-020: Intra-Cycle Review Intervals
**Thay đổi chính:**
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

#### 1.2 Cập nhật Use Cases
**UC-011: Perform Review Session**
- Bổ sung logic kiểm tra điều kiện mastered theo BR-033
- Cập nhật acceptance criteria và test cases

**UC-012: Complete Review Session**
- Bổ sung logic xử lý khi hoàn thành chu kỳ với điểm cao
- Kiểm tra điều kiện mastered và tạo lịch ôn định kỳ

## 2. Định nghĩa trạng thái "Mastered"

### Vấn đề ban đầu
- `business-rules.md` có đề cập đến trạng thái mastered nhưng chưa có quy tắc rõ ràng
- Trong `domain-model.md` và các Use Cases, ý nghĩa và luồng chuyển đổi chưa được thể hiện

### Giải pháp đã thực hiện

#### 2.1 Bổ sung BR-033: Mastered Status Definition
**Điều kiện chính:**
- avg_score ≥ 85% trong 3 chu kỳ liên tiếp
- Không có lần ôn nào bị skip trong 3 chu kỳ cuối
- Tổng thời gian học ≥ 30 ngày (từ lần ôn đầu tiên)

**Xử lý đặc biệt:**
- Chỉ tính từ chu kỳ thứ 2 trở đi (bỏ qua chu kỳ đầu tiên)
- Nếu có skip trong 3 chu kỳ cuối, reset lại đếm chu kỳ
- Tự động tạo lịch ôn định kỳ mỗi 90 ngày

#### 2.2 Bổ sung BR-034: Mastered Status Maintenance
**Duy trì trạng thái:**
- Ôn định kỳ: Tự động tạo reminder mỗi 90 ngày
- Điều kiện duy trì: avg_score ≥ 70% trong lần ôn định kỳ
- Chuyển về reviewing: Nếu avg_score < 70%
- Reset về learning: Nếu avg_score < 50%

#### 2.3 Cập nhật Domain Model
**Bổ sung MasteredStatusService:**
```java
@Service
class MasteredStatusService {
    public boolean checkMasteredCondition(Set set)
    public void handleMasteredStatus(Set set)
    public void handleMasteredMaintenance(Set set, ReviewHistory review)
}
```

**Bổ sung Domain Events:**
- `SetMasteredEvent`: Khi set đạt trạng thái mastered
- `MasteredStatusChangedEvent`: Khi trạng thái mastered thay đổi

#### 2.4 Cập nhật Set Status Transitions
**BR-003 được cập nhật:**
- reviewing → mastered: Khi đạt điều kiện mastered (xem BR-033)
- mastered → reviewing: Khi avg_score < 70% trong chu kỳ tiếp theo
- mastered → learning: Khi user chọn reset set (tùy chọn)

## 3. Tác động đến hệ thống

### 3.1 Use Cases được cập nhật
- **UC-011**: Perform Review Session
- **UC-012**: Complete Review Session

### 3.2 Domain Model được bổ sung
- MasteredStatusService
- SetMasteredEvent, MasteredStatusChangedEvent
- Cập nhật domain rules summary

### 3.3 Business Rules được bổ sung
- BR-033: Mastered Status Definition
- BR-034: Mastered Status Maintenance
- BR-020: Intra-Cycle Review Intervals (cập nhật)

## 4. Lợi ích của việc bổ sung

### 4.1 Tính rõ ràng
- Định nghĩa rõ ràng khoảng thời gian ôn tập
- Quy tắc mastered status được cụ thể hóa
- Logic chuyển đổi trạng thái minh bạch

### 4.2 Tính nhất quán
- Các Use Cases phản ánh đúng business rules
- Domain model hỗ trợ đầy đủ logic nghiệp vụ
- Events được định nghĩa cho tracking và analytics

### 4.3 Tính mở rộng
- Hỗ trợ tùy chỉnh khoảng thời gian ôn tập
- Có thể điều chỉnh điều kiện mastered
- Dễ dàng thêm logic mới cho status transitions

## 5. Kết luận

Các gap nghiệp vụ đã được bổ sung đầy đủ, đảm bảo:
- Hệ thống có đủ thông tin để implement logic ôn tập
- Trạng thái mastered được định nghĩa rõ ràng và có thể implement
- Use Cases và Domain Model phản ánh đúng business rules
- Hệ thống có thể mở rộng và tùy chỉnh theo nhu cầu

Việc bổ sung này giúp team development có đủ thông tin để implement các tính năng core của RepeatWise một cách chính xác và nhất quán.
