# Data Layer

Layer này chứa logic truy cập dữ liệu và implement các repository interfaces từ domain layer.

## 📁 Cấu trúc thư mục

### datasources/
Chứa các data sources - nguồn dữ liệu thực tế:

#### remote/
Chứa các remote data sources (API calls):
- **auth_remote_datasource.dart**: Authentication API calls
- **learning_set_remote_datasource.dart**: Learning set API calls
- **remind_schedule_remote_datasource.dart**: Reminder API calls

#### local/
Chứa các local data sources (local storage):
- **auth_local_datasource.dart**: Local authentication storage
- **learning_set_local_datasource.dart**: Local learning set storage
- **remind_schedule_local_datasource.dart**: Local reminder storage

### repositories/
Chứa repository implementations:
- **auth_repository_impl.dart**: Authentication repository implementation
- **learning_set_repository_impl.dart**: Learning set repository implementation
- **remind_schedule_repository_impl.dart**: Reminder repository implementation

### mappers/
Chứa data mappers để chuyển đổi giữa các format dữ liệu:
- **auth_mapper.dart**: Authentication data mappers
- **learning_set_mapper.dart**: Learning set data mappers
- **remind_schedule_mapper.dart**: Reminder data mappers

## 🔧 Nguyên tắc

1. **Implement domain interfaces**: Repository implementations phải implement interfaces từ domain layer
2. **Data transformation**: Sử dụng mappers để chuyển đổi dữ liệu
3. **Error handling**: Xử lý lỗi network và storage
4. **Caching strategy**: Implement caching logic khi cần thiết

## 📋 Quy tắc tổ chức

- **Datasources**: Chịu trách nhiệm truy cập dữ liệu từ nguồn cụ thể
- **Repositories**: Orchestrate data access và implement business logic
- **Mappers**: Chuyển đổi dữ liệu giữa các format khác nhau

## 🔄 Luồng dữ liệu

```
Repository Implementation → Data Sources → External APIs/Local Storage
         ↓
    Data Mappers
         ↓
    Domain Models
```
