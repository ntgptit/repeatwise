# Core Layer

Layer này chứa các thành phần cốt lõi của ứng dụng, được sử dụng bởi tất cả các layer khác.

## 📁 Cấu trúc thư mục

### constants/
- **api_endpoints.dart**: Định nghĩa các endpoint API
- **app_constants.dart**: Các hằng số chung của ứng dụng

### di/
- **providers.dart**: Cấu hình dependency injection

### exceptions/
- **app_exceptions.dart**: Custom exceptions cho ứng dụng

### extensions/
- **color_extensions.dart**: Extensions cho Color
- Các extensions khác cho các class cơ bản

### network/
- **api_client.dart**: HTTP client chính
- **interceptors/**: Các interceptor cho API calls
  - **auth_interceptor.dart**: Xử lý authentication
  - **logging_interceptor.dart**: Logging API calls

### services/
- **auth_service.dart**: Service xử lý authentication
- **platform/**: Platform-specific services
  - **device_settings_service.dart**: Quản lý cài đặt thiết bị
- **reminder/**: Reminder services
  - **alarm_manager_service.dart**: Quản lý alarm
  - **base_notification_service.dart**: Base notification service
  - **cloud_reminder_service.dart**: Cloud reminder service

### theme/
- **app_color_scheme.dart**: Color scheme của ứng dụng
- **app_dimens.dart**: Dimensions và spacing
- **app_theme_data.dart**: Theme data chính

### utils/
- **date_utils.dart**: Utility functions cho date/time
- **debouncer.dart**: Debounce utility
- **error_handler.dart**: Error handling utilities

### navigation/
- **index.dart**: Navigation exports
- **go_router_extensions.dart**: Extensions cho GoRouter
- **navigation_error_handler.dart**: Xử lý lỗi navigation

### events/
- **app_events.dart**: App-wide events

## 🔧 Sử dụng

Core layer không phụ thuộc vào bất kỳ layer nào khác và cung cấp các thành phần cơ bản cho toàn bộ ứng dụng.
