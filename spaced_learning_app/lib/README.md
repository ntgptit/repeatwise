# Cấu trúc thư mục dự án Spaced Learning App

Dự án này được tổ chức theo Clean Architecture với các layer rõ ràng:

## 📁 Core Layer (`core/`)
Chứa các thành phần cốt lõi của ứng dụng:
- **constants/**: Các hằng số của ứng dụng
- **di/**: Dependency injection
- **exceptions/**: Custom exceptions
- **extensions/**: Dart extensions
- **network/**: Network layer (API client, interceptors)
- **services/**: Core services (auth, platform, reminder)
- **theme/**: App theming
- **utils/**: Utility functions
- **navigation/**: Navigation logic
- **events/**: App events

## 📁 Data Layer (`data/`)
Chứa logic truy cập dữ liệu:
- **datasources/**: Data sources (remote API, local storage)
  - **remote/**: API data sources
  - **local/**: Local storage data sources
- **repositories/**: Repository implementations
- **mappers/**: Data mappers

## 📁 Domain Layer (`domain/`)
Chứa business logic:
- **entities/**: Domain entities
- **models/**: Domain models
- **repositories/**: Repository interfaces
- **usecases/**: Use cases (business logic)

## 📁 Presentation Layer (`presentation/`)
Chứa UI và logic hiển thị:
- **screens/**: UI screens
  - **auth/**: Authentication screens
  - **home/**: Home screen
  - **profile/**: Profile screens
  - **settings/**: Settings screens
  - **report/**: Report screens
- **widgets/**: Reusable widgets
  - **common/**: Common widgets
  - **home/**: Home-specific widgets
  - **profile/**: Profile-specific widgets
  - **progress/**: Progress-related widgets
- **viewmodels/**: ViewModels
- **mixins/**: Mixins
- **utils/**: Presentation utilities

## 🔄 Luồng dữ liệu
```
Presentation → Domain → Data → External Sources
     ↑           ↑       ↑
     └───────────┴───────┘
```

## 📋 Quy tắc tổ chức
1. Mỗi feature nên có cấu trúc riêng trong từng layer
2. Sử dụng dependency injection để giảm coupling
3. Tách biệt rõ ràng giữa UI logic và business logic
4. Sử dụng models/entities để truyền dữ liệu giữa các layer
