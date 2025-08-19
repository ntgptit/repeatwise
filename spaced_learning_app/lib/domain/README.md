# Domain Layer

Layer này chứa business logic và các thành phần không phụ thuộc vào framework hay external dependencies.

## 📁 Cấu trúc thư mục

### entities/
Chứa các domain entities - các object cốt lõi của business domain:
- User entity
- LearningSet entity
- RemindSchedule entity
- ActivityLog entity

### models/
Chứa các domain models và DTOs:
- **auth/**: Authentication models
  - **auth_request.dart**: Auth request models
  - **refresh_token_request.dart**: Refresh token models
  - **register_request.dart**: Registration models
- **user/**: User-related models
  - **user_update_request.dart**: User update models
- **enums/**: Domain enums
  - **activity_type.dart**: Activity types
  - **insight_type.dart**: Insight types
- **auth_response.dart**: Authentication response models
- **due_stats.dart**: Due statistics models

### repositories/
Chứa repository interfaces (contracts):
- **auth_repository.dart**: Authentication repository interface
- **learning_set_repository.dart**: Learning set repository interface
- **remind_schedule_repository.dart**: Remind schedule repository interface

### usecases/
Chứa business logic use cases:
- Authentication use cases
- Learning management use cases
- Reminder management use cases
- Statistics use cases

## 🔧 Nguyên tắc

1. **Không phụ thuộc vào framework**: Domain layer không import Flutter hay bất kỳ framework nào
2. **Pure business logic**: Chỉ chứa logic nghiệp vụ thuần túy
3. **Repository pattern**: Sử dụng interfaces để abstract data access
4. **Use case pattern**: Encapsulate business rules trong use cases

## 📋 Quy tắc tổ chức

- Entities: Đại diện cho các object cốt lõi của domain
- Models: Data transfer objects và domain models
- Repositories: Interfaces định nghĩa cách truy cập dữ liệu
- Use cases: Business logic và rules
