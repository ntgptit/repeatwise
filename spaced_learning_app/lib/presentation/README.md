# Presentation Layer

Layer này chứa UI và logic hiển thị, sử dụng Flutter framework.

## 📁 Cấu trúc thư mục

### screens/
Chứa các màn hình chính của ứng dụng:

#### auth/
- **login_screen.dart**: Màn hình đăng nhập
- **register_screen.dart**: Màn hình đăng ký

#### home/
- **home_screen.dart**: Màn hình chính
- **widgets/**: Widgets riêng cho home screen
  - **home_content.dart**: Nội dung chính
  - **home_error.dart**: Widget hiển thị lỗi
  - **home_header.dart**: Header của home screen

#### profile/
- **profile_screen.dart**: Màn hình profile

#### settings/
- **reminder_settings_screen.dart**: Cài đặt reminder

#### report/
- **daily_task_report_screen.dart**: Báo cáo task hàng ngày

#### app_info/
- **about_screen.dart**: Thông tin về ứng dụng

#### help/
- **spaced_repetition_info_screen.dart**: Thông tin về spaced repetition

### widgets/
Chứa các widget có thể tái sử dụng:

#### common/
- **app_bar_with_back.dart**: App bar với nút back
- **app_button.dart**: Button chung
- **app_card.dart**: Card chung
- **button/**: Các loại button
- **dialog/**: Dialog components
- **input/**: Input components
- **lists/**: List components

#### home/
- **dashboard/**: Dashboard widgets
  - **dashboard_section.dart**: Section dashboard
  - **stats_card.dart**: Card thống kê
- **due_tasks_section.dart**: Section tasks đến hạn
- **home_app_bar.dart**: App bar cho home
- **home_skeleton_screen.dart**: Skeleton loading
- **insights/**: Insight widgets
  - **insight_item.dart**: Item insight
  - **learning_insights_section.dart**: Section insights
  - **learning_insights_widget.dart**: Widget insights
- **learning_stats/**: Learning statistics widgets

#### profile/
- **login_prompt.dart**: Prompt đăng nhập
- **profile_edit_form.dart**: Form chỉnh sửa profile
- **profile_header.dart**: Header profile

#### progress/
- **compact_repetition_list.dart**: Danh sách repetition gọn
- **cycle_completion_dialog.dart**: Dialog hoàn thành cycle
- **cycle_group_card.dart**: Card nhóm cycle

### viewmodels/
Chứa các ViewModel (state management):
- **auth_viewmodel.dart**: ViewModel cho authentication
- **base_viewmodel.dart**: Base ViewModel
- **daily_task_report_viewmodel.dart**: ViewModel cho báo cáo
- Các ViewModel khác cho từng feature

### mixins/
- **view_model_refresher.dart**: Mixin cho refresh functionality

### utils/
- **book_formatter.dart**: Format cho book
- **cycle_formatter.dart**: Format cho cycle
- **repetition_utils.dart**: Utilities cho repetition

## 🔧 Nguyên tắc

1. **Separation of concerns**: Tách biệt UI logic và business logic
2. **Reusable widgets**: Tạo các widget có thể tái sử dụng
3. **State management**: Sử dụng ViewModels để quản lý state
4. **Responsive design**: UI phải responsive trên các kích thước màn hình

## 📋 Quy tắc tổ chức

- **Screens**: Màn hình chính của ứng dụng
- **Widgets**: Components có thể tái sử dụng
- **ViewModels**: Quản lý state và business logic cho UI
- **Mixins**: Chia sẻ functionality giữa các class
- **Utils**: Utilities cho presentation layer
