# Hướng dẫn Constructor Injection với Lombok

## Tổng quan

Dự án này sử dụng **Constructor Injection** thay vì Field Injection để tuân theo best practices của Spring Framework và đảm bảo tính immutable của dependencies.

## Lý do sử dụng Constructor Injection

### ✅ Ưu điểm:
- **Immutability**: Dependencies không thể thay đổi sau khi khởi tạo
- **Testability**: Dễ dàng mock dependencies trong unit tests
- **Required Dependencies**: Đảm bảo tất cả dependencies bắt buộc được inject
- **Performance**: Không sử dụng reflection như field injection
- **Best Practice**: Được khuyến nghị bởi Spring team

### ❌ Nhược điểm của Field Injection:
- Khó test
- Dependencies có thể null
- Sử dụng reflection (chậm hơn)
- Không thể tạo immutable objects

## Cách sử dụng với Lombok

### 1. Thêm annotation `@RequiredArgsConstructor`

```java
@Component
@RequiredArgsConstructor
public class LearningSetMapper {
    
    private final ModelMapper modelMapper;
    private final ReviewHistoryMapper reviewHistoryMapper;
    private final RemindScheduleMapper remindScheduleMapper;
    
    // Lombok sẽ tự động tạo constructor với tất cả final fields
}
```

### 2. Sử dụng `final` cho tất cả dependencies

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final MessageSource messageSource;
    private final CustomUserDetailsService userDetailsService;
    
    // Constructor sẽ được tạo tự động
}
```

### 3. Các annotation Lombok hữu ích khác

```java
@Component
@RequiredArgsConstructor  // Constructor cho final fields
@Slf4j                    // Tạo logger
@Data                     // Getters, setters, toString, equals, hashCode
@Builder                  // Builder pattern
@NoArgsConstructor       // Constructor rỗng
@AllArgsConstructor      // Constructor với tất cả fields
public class ExampleClass {
    
    private final String requiredField;
    private String optionalField;
}
```

## Ví dụ chuyển đổi

### ❌ Trước (Field Injection):
```java
@Component
public class LearningSetMapper {
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private ReviewHistoryMapper reviewHistoryMapper;
    
    @Autowired
    private RemindScheduleMapper remindScheduleMapper;
}
```

### ✅ Sau (Constructor Injection với Lombok):
```java
@Component
@RequiredArgsConstructor
public class LearningSetMapper {
    
    private final ModelMapper modelMapper;
    private final ReviewHistoryMapper reviewHistoryMapper;
    private final RemindScheduleMapper remindScheduleMapper;
}
```

## Các class đã được chuyển đổi

- ✅ `LearningSetMapper`
- ✅ `ReviewHistoryMapper`
- ✅ `RemindScheduleMapper`
- ✅ `AuthServiceImpl`
- ✅ `UserServiceImpl`
- ✅ `CustomUserDetailsService`
- ✅ `SecurityConfig`
- ✅ `UserSecurity`
- ✅ `JwtAuthenticationFilter`

## Best Practices

### 1. Luôn sử dụng `final` cho dependencies
```java
private final SomeService someService;  // ✅ Tốt
private SomeService someService;        // ❌ Không tốt
```

### 2. Sử dụng `@RequiredArgsConstructor` thay vì viết constructor thủ công
```java
@RequiredArgsConstructor  // ✅ Tốt - tự động tạo
public class MyClass {
    private final Dependency dependency;
}

// ❌ Không tốt - viết thủ công
public class MyClass {
    private final Dependency dependency;
    
    public MyClass(Dependency dependency) {
        this.dependency = dependency;
    }
}
```

### 3. Kết hợp với các annotation khác
```java
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MyService {
    private final MyRepository repository;
}
```

### 4. Sử dụng `@Qualifier` khi cần thiết
```java
@Component
@RequiredArgsConstructor
public class MyService {
    
    @Qualifier("primaryDataSource")
    private final DataSource primaryDataSource;
    
    @Qualifier("secondaryDataSource")
    private final DataSource secondaryDataSource;
}
```

## Testing với Constructor Injection

```java
@ExtendWith(MockitoExtension.class)
class MyServiceTest {
    
    @Mock
    private MyRepository repository;
    
    @Mock
    private AnotherService anotherService;
    
    @InjectMocks
    private MyService myService; // Tự động inject mocks
    
    @Test
    void testMethod() {
        // Test logic here
    }
}
```

## Lưu ý quan trọng

1. **Không sử dụng `@Autowired`** trên fields nữa
2. **Luôn sử dụng `final`** cho dependencies
3. **Sử dụng `@RequiredArgsConstructor`** để tự động tạo constructor
4. **Kết hợp với `@Slf4j`** để có logging
5. **Test dễ dàng hơn** với constructor injection

## Tài liệu tham khảo

- [Spring Framework Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-dependencies)
- [Lombok Documentation](https://projectlombok.org/features/RequiredArgsConstructor)
- [Spring Best Practices](https://spring.io/guides)
