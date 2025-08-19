# Hướng dẫn sử dụng Stream.toList() trong Java 17

## Tổng quan

Từ Java 16, chúng ta có thể sử dụng `.toList()` trực tiếp trên Stream thay vì `Collectors.toList()`. Đây là một cải tiến quan trọng giúp code ngắn gọn và dễ đọc hơn.

## So sánh cú pháp

### ❌ Trước Java 16 (cũ):
```java
import java.util.stream.Collectors;

List<String> names = users.stream()
    .map(User::getName)
    .collect(Collectors.toList());
```

### ✅ Từ Java 16+ (mới):
```java
List<String> names = users.stream()
    .map(User::getName)
    .toList();
```

## Lợi ích của .toList()

### 1. **Code ngắn gọn hơn**
- Không cần import `Collectors`
- Ít boilerplate code hơn
- Dễ đọc và hiểu hơn

### 2. **Performance tốt hơn**
- Không cần tạo `Collector` object
- Tối ưu hóa tốt hơn trong JVM

### 3. **Type safety**
- Type inference tốt hơn
- Ít lỗi compile-time hơn

## Các trường hợp sử dụng

### 1. **Mapping entities to DTOs**
```java
// ✅ Tốt
public List<UserResponse> toResponseList(List<User> users) {
    return users.stream()
        .map(this::toResponse)
        .toList();
}
```

### 2. **Filtering và mapping**
```java
// ✅ Tốt
List<String> activeUserNames = users.stream()
    .filter(User::isActive)
    .map(User::getName)
    .toList();
```

### 3. **Nested collections**
```java
// ✅ Tốt
List<String> allRoleNames = users.stream()
    .flatMap(user -> user.getRoles().stream())
    .map(Role::getName)
    .toList();
```

## Lưu ý quan trọng

### 1. **Immutable List**
`.toList()` trả về một **immutable List**, không thể thêm/xóa/sửa elements:

```java
List<String> names = users.stream()
    .map(User::getName)
    .toList();

// ❌ Sẽ throw UnsupportedOperationException
names.add("new name");
names.remove(0);
names.set(0, "new name");
```

### 2. **Khi cần mutable List**
Nếu cần mutable List, vẫn sử dụng `Collectors.toList()`:

```java
// ✅ Khi cần mutable List
List<String> mutableNames = users.stream()
    .map(User::getName)
    .collect(Collectors.toList());

// ✅ Có thể thay đổi
mutableNames.add("new name");
```

### 3. **Type inference issues**
Một số trường hợp có thể gặp vấn đề với type inference:

```java
// ❌ Có thể gặp lỗi type mismatch
List<GrantedAuthority> authorities = user.getRoles().stream()
    .map(role -> new SimpleGrantedAuthority(role.getName()))
    .toList(); // Type inference issue

// ✅ Sử dụng Collectors.toList() trong trường hợp này
List<GrantedAuthority> authorities = user.getRoles().stream()
    .map(role -> new SimpleGrantedAuthority(role.getName()))
    .collect(Collectors.toList());
```

## Các file đã được cập nhật

### ✅ Mappers (3 files)
- `LearningSetMapper.java` - Sử dụng `.toList()`
- `ReviewHistoryMapper.java` - Sử dụng `.toList()`
- `RemindScheduleMapper.java` - Sử dụng `.toList()`

### ✅ UserMapper (1 file)
- `UserMapper.java` - Sử dụng `.toList()`

### ⚠️ CustomUserDetailsService (1 file)
- `CustomUserDetailsService.java` - Giữ nguyên `Collectors.toList()` do type inference issues

## Best Practices

### 1. **Ưu tiên .toList() khi có thể**
```java
// ✅ Tốt - sử dụng .toList()
return entities.stream()
    .map(this::toResponse)
    .toList();
```

### 2. **Sử dụng Collectors.toList() khi cần mutable List**
```java
// ✅ Khi cần mutable List
List<String> mutableList = stream.collect(Collectors.toList());
```

### 3. **Sử dụng Collectors.toList() khi có type inference issues**
```java
// ✅ Khi có vấn đề với type inference
List<GrantedAuthority> authorities = stream.collect(Collectors.toList());
```

### 4. **Loại bỏ import Collectors khi không cần**
```java
// ❌ Không cần thiết nếu chỉ dùng .toList()
import java.util.stream.Collectors;

// ✅ Chỉ import khi cần
import java.util.stream.Collectors; // Chỉ khi cần Collectors.toList()
```

## Migration Checklist

- [x] Cập nhật tất cả mapper classes
- [x] Loại bỏ import `Collectors` không cần thiết
- [x] Kiểm tra type inference issues
- [x] Giữ lại `Collectors.toList()` cho trường hợp đặc biệt
- [x] Tạo documentation

## Kết luận

Việc sử dụng `.toList()` trong Java 17+ mang lại nhiều lợi ích:

- ✅ **Code ngắn gọn hơn**
- ✅ **Performance tốt hơn**
- ✅ **Type safety tốt hơn**
- ✅ **Dễ đọc và maintain**

Tuy nhiên cần lưu ý:
- ⚠️ **Immutable List** - không thể thay đổi sau khi tạo
- ⚠️ **Type inference issues** - một số trường hợp cần dùng `Collectors.toList()`

Dự án đã được cập nhật để tận dụng tối đa tính năng mới của Java 17!
