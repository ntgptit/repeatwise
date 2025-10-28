# Vision Document - RepeatWise

## Tầm nhìn sản phẩm

RepeatWise hướng đến trở thành một công cụ học tập cá nhân hiệu quả, giúp người dùng ghi nhớ kiến thức lâu dài thông qua phương pháp khoa học Spaced Repetition.

## Vấn đề cần giải quyết

### Thách thức hiện tại

1. **Quên nhanh**: Con người quên 50-80% thông tin mới sau 24 giờ nếu không ôn tập
2. **Thiếu tổ chức**: Khó quản lý hàng nghìn flashcards trên nhiều chủ đề
3. **Lịch ôn tập không khoa học**: Không biết khi nào nên ôn lại
4. **Mất thời gian**: Ôn tập những gì đã biết thay vì tập trung vào điểm yếu

### Giải pháp của RepeatWise

1. **Thuật toán SRS**: Tự động tính toán thời điểm tối ưu để ôn tập
2. **Cấu trúc phân cấp**: Tổ chức folders/decks linh hoạt, dễ quản lý
3. **Thông minh**: Chỉ hiển thị cards cần ôn, tránh lãng phí thời gian
4. **Đa nền tảng**: Web + Mobile, đồng bộ mọi lúc mọi nơi

## Mục tiêu dự án

### Phase 1 - MVP (3-4 tháng)

**Core Features**:
- User authentication với JWT + Refresh Token
- Folder/Deck management với cấu trúc cây
- Flashcard CRUD operations
- SRS 7-box system
- Import/Export CSV/Excel
- Multi-mode study (SRS, Cram, Random)
- Basic statistics

**Platforms**: Web + Mobile (React Native)

### Phase 2 - Enhanced Features (Future)

- Rich text editor với images/audio
- Drag & drop UI
- Advanced analytics
- Social features (share decks)
- AI-generated cards
- Gamification (badges, streaks)

### Phase 3 - Premium Features (Future)

- Offline mode with sync
- Advanced customization
- API for third-party integrations
- Team collaboration features

## Đối tượng sử dụng

### Primary Users
- **Học sinh, sinh viên**: Học từ vựng, công thức, khái niệm
- **Lập trình viên**: Ghi nhớ syntax, design patterns, algorithms
- **Người học ngoại ngữ**: Học từ vựng, ngữ pháp mới

### Secondary Users
- **Giáo viên**: Tạo bộ flashcards cho học sinh
- **Chuyên gia**: Ghi nhớ kiến thức chuyên môn

## Tiêu chí thành công

### Metrics
1. **User Engagement**:
   - Daily active users > 70% total users
   - Average study session > 15 minutes/day

2. **Retention**:
   - Week 1 retention > 80%
   - Month 1 retention > 50%

3. **Performance**:
   - API response time < 200ms
   - App load time < 2s

4. **User Satisfaction**:
   - App rating > 4.5/5
   - Feature completion rate > 90%

## Lợi ích

### Cho người dùng
- Ghi nhớ kiến thức hiệu quả hơn 300% so với học truyền thống
- Tiết kiệm thời gian với lịch ôn tập tối ưu
- Quản lý kiến thức có hệ thống
- Học mọi lúc mọi nơi với mobile app

### Cho dự án
- Codebase sạch, dễ maintain
- Architecture mở rộng tốt
- Documentation đầy đủ
- Test coverage cao

## Rủi ro và giảm thiểu

### Technical Risks
1. **Performance với large datasets**
   - Mitigation: Pagination, indexes, async operations

2. **Data loss**
   - Mitigation: Daily backups, soft delete

### Business Risks
1. **User adoption**
   - Mitigation: Onboarding tutorials, sample decks

2. **Competition**
   - Mitigation: Focus on simplicity & performance

## Roadmap

- **Q1 2025**: MVP Backend + Web completion
- **Q2 2025**: Mobile app launch
- **Q3 2025**: Enhanced features + optimization
- **Q4 2025**: Premium features + scaling
