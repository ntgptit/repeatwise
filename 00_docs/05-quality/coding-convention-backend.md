# Backend Coding Convention - RepeatWise

## 1. Overview

Document này định nghĩa coding convention cho backend của RepeatWise, sử dụng **Java 17 + Spring Boot 3**.

**Tech Stack**:
- Language: Java 17
- Framework: Spring Boot 3.x
- ORM: Spring Data JPA (Hibernate)
- Database: PostgreSQL 15
- Build Tool: Maven
- Libraries: MapStruct, Apache POI, OpenCSV, Lombok

---

## 2. General Principles

### 2.1 Code Style Guide

**Base Style**: [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)

**Key Rules**:
- Indentation: 4 spaces (not tabs)
- Line length: 120 characters max
- Braces: K&R style (opening brace on same line)
- File encoding: UTF-8
- Line endings: LF (Unix style)

### 2.2 Code Quality Tools

**Required Tools**:
- **CheckStyle**: Enforce coding standards
- **SonarLint**: Code quality analysis
- **SpotBugs**: Bug detection
- **Maven CheckStyle Plugin**: Automated checks in CI/CD

**IDE Setup**:
- IntelliJ IDEA: Import `google-java-format` code style
- Eclipse: Install Google Java Format plugin
- VS Code: Install Java formatter extension

---

## 3. Naming Conventions

### 3.1 Package Naming

**Format**: Lowercase, no underscores
```java
✅ Good
com.repeatwise.service
com.repeatwise.repository
com.repeatwise.dto.request

❌ Bad
com.repeatWise.Service
com.repeatwise.dto_request
```

**Package Structure**:
```
com.repeatwise
├── controller       // REST controllers
├── service          // Service interfaces
├── service.impl     // Service implementations
├── repository       // Spring Data JPA repositories
├── entity           // JPA entities
├── dto              // Data Transfer Objects
│   ├── request      // Request DTOs
│   └── response     // Response DTOs
├── mapper           // MapStruct mappers
├── config           // Configuration classes
├── security         // Security components
├── exception        // Custom exceptions
├── strategy         // Strategy pattern implementations
├── visitor          // Visitor pattern implementations
├── event            // Domain events
├── util             // Utility classes
└── job              // Background jobs
```

### 3.2 Class Naming

**Format**: PascalCase, descriptive nouns

| Type | Convention | Example |
|------|-----------|---------|
| Entity | Noun | `User`, `Folder`, `Card` |
| Repository | `EntityRepository` | `FolderRepository` |
| Service Interface | `IEntityService` | `IFolderService` |
| Service Impl | `EntityServiceImpl` | `FolderServiceImpl` |
| Controller | `EntityController` | `FolderController` |
| Request DTO | `Action + Entity + Request` | `CreateFolderRequest` |
| Response DTO | `Entity + Response` | `FolderResponse` |
| Mapper | `EntityMapper` | `FolderMapper` |
| Exception | `Description + Exception` | `MaxDepthExceededException` |
| Config | `Description + Config` | `SecurityConfig` |
| Event | `Description + Event` | `CardReviewedEvent` |

**Examples**:
```java
✅ Good
public class FolderServiceImpl implements IFolderService { }
public class CreateFolderRequest { }
public class FolderResponse { }

❌ Bad
public class FolderServiceImplementation { }
public class FolderCreateReq { }
public class FolderDTO { }
```

### 3.3 Method Naming

**Format**: camelCase, verb-based

| Type | Convention | Example |
|------|-----------|---------|
| CRUD | `create`, `update`, `delete`, `get`, `find` | `createFolder()`, `getFolder()` |
| Query | `findBy...`, `getBy...`, `countBy...` | `findByUserAndParent()` |
| Boolean | `is...`, `has...`, `can...` | `isValid()`, `hasPermission()` |
| Converter | `toEntity`, `toResponse` | `toEntity()`, `toResponse()` |
| Business Logic | Descriptive verb | `calculateDueDate()`, `moveToBox()` |

**Examples**:
```java
✅ Good
public FolderResponse createFolder(CreateFolderRequest request, UUID userId)
public List<Folder> findByUserAndParentIsNull(User user)
public boolean isMaxDepthExceeded(Folder parent)
public void softDelete()

❌ Bad
public FolderResponse create(CreateFolderRequest request, UUID userId)  // Too generic
public List<Folder> get(User user)  // Ambiguous
public boolean maxDepth(Folder parent)  // Not boolean-style
public void delete()  // Ambiguous (soft or hard?)
```

### 3.4 Variable Naming

**Format**: camelCase, descriptive nouns

```java
✅ Good
private UUID userId;
private String folderName;
private List<Card> dueCards;
private LocalDate nextReviewDate;
private static final int MAX_FOLDER_DEPTH = 10;

❌ Bad
private UUID uid;  // Too short
private String name;  // Too generic
private List<Card> cards;  // Ambiguous
private LocalDate date;  // Too generic
private static final int MAX = 10;  // Not descriptive
```

**Constants**:
```java
// UPPER_SNAKE_CASE for constants
public static final int MAX_FOLDER_DEPTH = 10;
public static final String DEFAULT_LANGUAGE = "en";
```

---

## 3.5 Clean Code & Readability (BẮT BUỘC)

### 🔴 3.5.1 Tên biến, phương thức, lớp phải rõ ràng (BẮT BUỘC)

**Không viết tắt tùy tiện, tên phải tự giải thích.**

❌ **SAI - Tên mơ hồ, viết tắt:**
```java
// Class name
public class UsrMgr { }  // User Manager?
public class FldSrv { }  // Folder Service?

// Method name
public void proc(Usr u) { }  // Process what?
public List<Fld> get() { }  // Get what?

// Variable name
private String n;  // name?
private int cnt;  // count of what?
private List<Obj> lst;  // list of what?
private LocalDate dt;  // date of what?
```

✅ **ĐÚNG - Tên rõ ràng, có ý nghĩa:**
```java
// Class name
public class UserManager { }
public class FolderService { }

// Method name
public void processUserRegistration(User user) { }
public List<Folder> getRootFoldersByUser(UUID userId) { }

// Variable name
private String userName;
private int activeUserCount;
private List<Folder> rootFolders;
private LocalDate nextReviewDate;
```

**Nguyên tắc đặt tên:**
- ✅ Tên phải tự giải thích được mục đích
- ✅ Tên phải phản ánh đúng nội dung/hành vi
- ✅ Tránh viết tắt trừ các từ phổ biến (ID, URL, HTTP, DTO, etc.)
- ✅ Boolean phải bắt đầu với `is`, `has`, `can`, `should`
- ✅ Collection phải dùng số nhiều: `users`, `folders`, `cards`

**Các viết tắt được chấp nhận:**
```java
// OK - Widely known abbreviations
UUID userId;
URL apiUrl;
HTTP httpClient;
DTO userDTO;
JSON jsonData;
CSV csvFile;
HTML htmlContent;

// NOT OK - Custom abbreviations
String usrNm;  // Use userName
int fldrCnt;   // Use folderCount
List<Cd> cds;  // Use cards
```

### 🔴 3.5.2 Hàm không dài quá 30 dòng (BẮT BUỘC)

**Method phải ngắn gọn, tập trung vào một nhiệm vụ.**

❌ **SAI - Method quá dài (>30 dòng):**
```java
public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
    // Validate request - 10 lines
    if (request == null) {
        throw new ValidationException("Request is null");
    }
    if (StringUtils.isBlank(request.getName())) {
        throw new ValidationException("Name is blank");
    }
    // ... more validations

    // Get user - 5 lines
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", userId));

    // Get parent folder - 10 lines
    Folder parent = null;
    if (request.getParentFolderId() != null) {
        parent = folderRepository.findById(request.getParentFolderId())
            .orElseThrow(() -> new ResourceNotFoundException("Folder", request.getParentFolderId()));

        if (!parent.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Not authorized");
        }
    }

    // Validate depth - 5 lines
    if (parent != null && parent.getDepth() >= 9) {
        throw new MaxDepthExceededException("Max depth exceeded");
    }

    // Build and save - 10 lines
    Folder folder = new Folder();
    folder.setName(request.getName());
    folder.setDescription(request.getDescription());
    folder.setUser(user);
    folder.setParentFolder(parent);
    folder.setDepth(parent == null ? 0 : parent.getDepth() + 1);
    folder.updatePath();

    Folder saved = folderRepository.save(folder);

    // Publish event - 3 lines
    eventPublisher.publishEvent(new FolderCreatedEvent(saved.getId(), userId));

    return folderMapper.toResponse(saved);
}
// Total: ~50 lines - TOO LONG!
```

✅ **ĐÚNG - Tách thành các method nhỏ (<30 dòng mỗi method):**
```java
public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
    validateRequest(request);

    User user = getUser(userId);
    Folder parent = getParentFolder(request.getParentFolderId(), userId);
    validateDepth(parent);

    Folder folder = buildFolder(request, user, parent);
    Folder saved = folderRepository.save(folder);

    publishFolderCreatedEvent(saved.getId(), userId);

    return folderMapper.toResponse(saved);
}
// Total: ~12 lines - GOOD!

private void validateRequest(CreateFolderRequest request) {
    Objects.requireNonNull(request, "Request cannot be null");

    if (StringUtils.isBlank(request.getName())) {
        throw new ValidationException("FOLDER_001", getMessage("error.folder.name.required"));
    }
}

private User getUser(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", userId));
}

private Folder getParentFolder(UUID parentId, UUID userId) {
    if (parentId == null) {
        return null;
    }

    Folder parent = folderRepository.findById(parentId)
        .orElseThrow(() -> new ResourceNotFoundException("Folder", parentId));

    if (!parent.getUser().getId().equals(userId)) {
        throw new ForbiddenException("Not authorized to access parent folder");
    }

    return parent;
}

private void validateDepth(Folder parent) {
    if (parent == null) {
        return;
    }

    if (parent.getDepth() >= MAX_FOLDER_DEPTH - 1) {
        throw new MaxDepthExceededException("FOLDER_002",
            getMessage("error.folder.max.depth", new Object[]{MAX_FOLDER_DEPTH}));
    }
}

private Folder buildFolder(CreateFolderRequest request, User user, Folder parent) {
    return Folder.builder()
        .name(request.getName())
        .description(request.getDescription())
        .user(user)
        .parentFolder(parent)
        .depth(parent == null ? 0 : parent.getDepth() + 1)
        .build();
}

private void publishFolderCreatedEvent(UUID folderId, UUID userId) {
    eventPublisher.publishEvent(new FolderCreatedEvent(folderId, userId));
}
```

### 🔴 3.5.3 Dùng `final` cho biến không thay đổi (BẮT BUỘC)

**Tránh lỗi vô tình gán lại giá trị.**

❌ **SAI - Không dùng final:**
```java
@Service
public class FolderService {
    // Có thể bị gán lại - BUG risk!
    private FolderRepository folderRepository;
    private UserRepository userRepository;

    public void process(String name) {
        String processedName = name.trim();  // Có thể bị gán lại
        int maxDepth = 10;  // Có thể bị gán lại

        // ... 50 lines of code
        processedName = "something else";  // BUG: vô tình gán lại!
    }
}
```

✅ **ĐÚNG - Dùng final:**
```java
@Service
@RequiredArgsConstructor
public class FolderService {
    // final = immutable, không thể gán lại
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    public void process(String name) {
        final String processedName = name.trim();  // Không thể gán lại
        final int maxDepth = 10;  // Constant

        // ... 50 lines of code
        // processedName = "something else";  // Compile error! GOOD!
    }
}
```

**Khi nào dùng final:**
- ✅ Dependencies (fields) trong service/controller
- ✅ Method parameters nếu không cần modify
- ✅ Local variables nếu không cần modify
- ✅ Constants (static final)

```java
// Method parameters
public void updateFolder(final UUID folderId, final UpdateFolderRequest request) {
    // folderId và request không thể bị gán lại
}

// Constants
public static final int MAX_FOLDER_DEPTH = 10;
public static final String DEFAULT_LANGUAGE = "en";

// Local variables
public void process() {
    final User user = getCurrentUser();
    final List<Folder> folders = folderRepository.findAll();
    // user và folders không thể bị gán lại
}
```

### 🔴 3.5.4 Tối đa 3 tham số trong method (BẮT BUỘC)

**Nếu >3 tham số, dùng DTO hoặc Builder.**

❌ **SAI - Quá nhiều tham số:**
```java
// 7 parameters - TOO MANY!
public Folder createFolder(
    String name,
    String description,
    UUID userId,
    UUID parentFolderId,
    int depth,
    String path,
    boolean isActive) {
    // ...
}

// Cách gọi - rất khó đọc và dễ nhầm lẫn thứ tự
Folder folder = createFolder("My Folder", "Description", userId, parentId, 0, "/", true);
```

✅ **ĐÚNG - Dùng Request DTO:**
```java
// 2 parameters - GOOD!
public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
    // ...
}

// Request DTO
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFolderRequest {
    @NotBlank(message = "Folder name is required")
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private UUID parentFolderId;

    // Có thể thêm nhiều fields mà không ảnh hưởng signature
}

// Cách gọi - rõ ràng, dễ đọc
CreateFolderRequest request = CreateFolderRequest.builder()
    .name("My Folder")
    .description("Description")
    .parentFolderId(parentId)
    .build();

FolderResponse response = folderService.createFolder(request, userId);
```

**Nguyên tắc:**
- ✅ 0-3 parameters: OK, giữ nguyên
- ⚠️ 4-5 parameters: Cân nhắc refactor
- ❌ >5 parameters: **BẮT BUỘC** dùng DTO

### 🔴 3.5.5 Bắt buộc dùng Apache Commons Libraries (BẮT BUỘC)

**Khi xử lý String, Collections, IO, CSV, Validation.**

**Required dependencies trong `pom.xml`:**
```xml
<dependencies>
    <!-- Apache Commons Lang3 -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>

    <!-- Apache Commons Collections4 -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-collections4</artifactId>
    </dependency>

    <!-- Apache Commons IO -->
    <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
    </dependency>

    <!-- Apache Commons BeanUtils -->
    <dependency>
        <groupId>commons-beanutils</groupId>
        <artifactId>commons-beanutils</artifactId>
    </dependency>

    <!-- Apache Commons CSV -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-csv</artifactId>
    </dependency>

    <!-- Apache Commons Validator -->
    <dependency>
        <groupId>commons-validator</groupId>
        <artifactId>commons-validator</artifactId>
    </dependency>
</dependencies>
```

❌ **SAI - Tự implement các utility functions:**
```java
// String validation
if (name == null || name.trim().isEmpty()) { }
if (email == null || email.equals("")) { }

// Collection validation
if (list == null || list.size() == 0) { }
if (map == null || map.keySet().size() == 0) { }

// Array operations
String[] newArray = new String[oldArray.length];
System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);

// String operations
String result = str.substring(0, 1).toUpperCase() + str.substring(1);
```

✅ **ĐÚNG - Dùng Apache Commons:**
```java
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.validator.routines.EmailValidator;

// ===== Apache Commons Lang3 =====

// String validation
if (StringUtils.isBlank(name)) { }
if (StringUtils.isEmpty(email)) { }

// String operations
String trimmed = StringUtils.trim(input);
String normalized = StringUtils.normalizeSpace(input);
String capitalized = StringUtils.capitalize(name);
String joined = StringUtils.join(list, ", ");
String[] parts = StringUtils.split(input, ",");

// String comparison
if (StringUtils.equals(str1, str2)) { }
if (StringUtils.equalsIgnoreCase(str1, str2)) { }
if (StringUtils.containsIgnoreCase(text, "keyword")) { }

// Default values
String result = StringUtils.defaultIfBlank(input, "default");
String result2 = StringUtils.defaultString(input, "");

// Array operations
String[] newArray = ArrayUtils.clone(oldArray);
boolean contains = ArrayUtils.contains(array, "value");
String[] reversed = ArrayUtils.reverse(array);

// ===== Apache Commons Collections4 =====

// Collection validation
if (CollectionUtils.isEmpty(list)) { }
if (CollectionUtils.isNotEmpty(list)) { }

// Collection operations
Collection<String> union = CollectionUtils.union(list1, list2);
Collection<String> intersection = CollectionUtils.intersection(list1, list2);
Collection<String> subtract = CollectionUtils.subtract(list1, list2);

// Map operations
if (MapUtils.isEmpty(map)) { }
if (MapUtils.isNotEmpty(map)) { }
String value = MapUtils.getString(map, "key", "default");
Integer intValue = MapUtils.getInteger(map, "count", 0);

// ===== Apache Commons IO =====

// File operations
String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
FileUtils.copyFile(sourceFile, destFile);
FileUtils.deleteQuietly(file);
List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);

// ===== Apache Commons CSV =====

// Read CSV
try (Reader reader = Files.newBufferedReader(Path.of(csvFile));
     CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
         .withFirstRecordAsHeader()
         .withIgnoreHeaderCase()
         .withTrim())) {

    for (CSVRecord record : csvParser) {
        String name = record.get("Name");
        String email = record.get("Email");
        // process...
    }
}

// ===== Apache Commons Validator =====

// Email validation
EmailValidator emailValidator = EmailValidator.getInstance();
if (emailValidator.isValid(email)) { }

// URL validation
UrlValidator urlValidator = new UrlValidator();
if (urlValidator.isValid(url)) { }
```

**Use cases bắt buộc dùng Apache Commons:**

| Operation | Apache Commons | Package |
|-----------|---------------|---------|
| String null/empty check | `StringUtils.isBlank()`, `isEmpty()` | lang3 |
| String comparison | `StringUtils.equals()`, `equalsIgnoreCase()` | lang3 |
| String manipulation | `trim()`, `capitalize()`, `join()`, `split()` | lang3 |
| Collection null/empty check | `CollectionUtils.isEmpty()` | collections4 |
| Collection operations | `union()`, `intersection()`, `subtract()` | collections4 |
| Map null/empty check | `MapUtils.isEmpty()` | collections4 |
| File read/write | `FileUtils.readFileToString()`, `writeStringToFile()` | io |
| CSV parsing | `CSVParser`, `CSVFormat` | csv |
| Email validation | `EmailValidator` | validator |

### 🔴 3.5.6 Quản lý message lỗi bằng MessageSource (BẮT BUỘC)

**Tất cả error messages phải externalize vào `messages.properties`.**

**Setup MessageSource:**
```java
@Configuration
public class MessageConfig {

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
```

**File `src/main/resources/messages.properties`:**
```properties
# User errors
error.user.not.found=Không tìm thấy người dùng với ID {0}
error.user.already.exists=Email {0} đã được sử dụng
error.user.invalid.credentials=Thông tin đăng nhập không chính xác

# Folder errors
error.folder.not.found=Không tìm thấy thư mục với ID {0}
error.folder.name.required=Tên thư mục không được để trống
error.folder.name.too.long=Tên thư mục không được vượt quá {0} ký tự
error.folder.max.depth=Thư mục không thể vượt quá {0} cấp độ
error.folder.not.authorized=Bạn không có quyền truy cập thư mục này

# Order errors
error.order.items.empty=Đơn hàng phải có ít nhất 1 sản phẩm
error.order.insufficient.inventory=Sản phẩm {0} không đủ số lượng trong kho
error.order.processing.failed=Lỗi xử lý đơn hàng, vui lòng thử lại

# Validation errors
error.validation.required={0} không được để trống
error.validation.min.length={0} phải có ít nhất {1} ký tự
error.validation.max.length={0} không được vượt quá {1} ký tự
error.validation.email.invalid=Email không đúng định dạng
```

**File `src/main/resources/messages_en.properties` (English):**
```properties
# User errors
error.user.not.found=User not found with ID {0}
error.user.already.exists=Email {0} is already in use
error.user.invalid.credentials=Invalid credentials

# Folder errors
error.folder.not.found=Folder not found with ID {0}
error.folder.name.required=Folder name is required
error.folder.name.too.long=Folder name must not exceed {0} characters
error.folder.max.depth=Folder cannot exceed {0} levels
error.folder.not.authorized=You are not authorized to access this folder
```

❌ **SAI - Hardcode messages trong code:**
```java
@Service
public class FolderService {

    public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
        if (StringUtils.isBlank(request.getName())) {
            // Hardcoded message - BAD!
            throw new ValidationException("Tên thư mục không được để trống");
        }

        Folder parent = folderRepository.findById(request.getParentFolderId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Không tìm thấy thư mục với ID " + request.getParentFolderId()
            ));

        if (parent.getDepth() >= 9) {
            throw new MaxDepthExceededException(
                "Thư mục không thể vượt quá 10 cấp độ"
            );
        }

        // ...
    }
}
```

✅ **ĐÚNG - Dùng MessageSource:**
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class FolderService {

    private final FolderRepository folderRepository;
    private final MessageSource messageSource;

    public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
        if (StringUtils.isBlank(request.getName())) {
            throw new ValidationException(
                "FOLDER_001",
                getMessage("error.folder.name.required")
            );
        }

        Folder parent = folderRepository.findById(request.getParentFolderId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "FOLDER_002",
                getMessage("error.folder.not.found", request.getParentFolderId())
            ));

        if (parent.getDepth() >= MAX_FOLDER_DEPTH - 1) {
            throw new MaxDepthExceededException(
                "FOLDER_003",
                getMessage("error.folder.max.depth", MAX_FOLDER_DEPTH)
            );
        }

        // ...
    }

    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
```

**Custom Exception với MessageSource:**
```java
@Getter
public class BusinessException extends RuntimeException {
    private final String errorCode;
    private final String message;

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }
}

public class ValidationException extends BusinessException {
    public ValidationException(String errorCode, String message) {
        super(errorCode, message);
    }
}

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String errorCode, String message) {
        super(errorCode, message);
    }
}
```

**Global Exception Handler với MessageSource:**
```java
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.error("Resource not found: code={}, message={}",
            ex.getErrorCode(), ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("NOT_FOUND")
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex,
            HttpServletRequest request) {

        log.warn("Validation error: code={}, message={}",
            ex.getErrorCode(), ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("VALIDATION_ERROR")
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.badRequest().body(error);
    }
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String errorCode;
    private String message;
    private String path;
    private List<String> details;
}
```

**Lợi ích của MessageSource:**
- ✅ Support đa ngôn ngữ (i18n) - tự động chọn theo locale
- ✅ Tập trung quản lý messages tại một nơi
- ✅ Dễ dàng thay đổi nội dung mà không cần sửa code
- ✅ Consistent error messages trong toàn bộ ứng dụng
- ✅ Support parameterized messages (`{0}`, `{1}`, etc.)

---

## 4. Code Structure

### 4.1 Class Structure Order (BẮT BUỘC)

**Standard Order**: **Constructor → Public methods → Private methods**

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class FolderServiceImpl implements IFolderService {

    // 1. Instance fields (dependencies) - final for immutability
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final FolderMapper folderMapper;
    private final MessageSource messageSource;

    // 2. Constructor (auto-generated by @RequiredArgsConstructor)

    // 3. Public methods (interface implementations)
    @Override
    public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
        // Early return & Guard clauses
        Objects.requireNonNull(request, "CreateFolderRequest cannot be null");

        if (StringUtils.isBlank(request.getName())) {
            log.error("Folder creation failed: name is blank for userId={}", userId);
            throw new ValidationException("FOLDER_001",
                getMessage("error.folder.name.required"));
        }

        log.info("Creating folder: name={}, userId={}", request.getName(), userId);

        Folder parent = getParentFolder(request.getParentFolderId());
        validateDepth(parent);

        Folder folder = buildFolder(request, userId, parent);
        Folder saved = folderRepository.save(folder);

        log.info("Folder created successfully: folderId={}, userId={}", saved.getId(), userId);
        return folderMapper.toResponse(saved);
    }

    // 4. Private helper methods
    private void validateDepth(Folder parent) {
        if (parent == null) {
            return;
        }

        if (parent.getDepth() >= MAX_FOLDER_DEPTH - 1) {
            log.warn("Max depth exceeded: parentId={}, depth={}",
                parent.getId(), parent.getDepth());
            throw new MaxDepthExceededException("FOLDER_002",
                getMessage("error.folder.max.depth", new Object[]{MAX_FOLDER_DEPTH}));
        }
    }

    private Folder buildFolder(CreateFolderRequest request, UUID userId, Folder parent) {
        return Folder.builder()
            .name(request.getName())
            .description(request.getDescription())
            .user(userRepository.getReferenceById(userId))
            .parentFolder(parent)
            .depth(parent == null ? 0 : parent.getDepth() + 1)
            .build();
    }

    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
```

### 4.2 Method Length

**Guideline**: Max 50 lines per method

```java
✅ Good - Single responsibility, concise
@Override
public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
    validateRequest(request);

    Folder parent = getParentFolder(request.getParentFolderId());
    validateDepth(parent);

    Folder folder = buildFolder(request, userId, parent);
    Folder saved = folderRepository.save(folder);

    publishEvent(new FolderCreatedEvent(saved.getId(), userId));

    return folderMapper.toResponse(saved);
}

❌ Bad - Too long, multiple responsibilities (refactor into smaller methods)
@Override
public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
    // 100+ lines of validation, business logic, persistence
    // Should be split into smaller methods
}
```

### 4.3 Parameter Count

**Guideline**: Max 5 parameters

```java
✅ Good
public FolderResponse moveFolder(UUID folderId, UUID newParentId, UUID userId)

✅ Good - Use request object for many parameters
public FolderResponse createFolder(CreateFolderRequest request, UUID userId)

❌ Bad - Too many parameters
public Folder createFolder(String name, String desc, UUID parentId,
                          UUID userId, int depth, String path, boolean active)
// Refactor: Use CreateFolderRequest object
```

---

## 4.5 Coding Conventions & Best Practices (BẮT BUỘC TUÂN THỦ)

### 🔴 4.5.1 Guard Clauses & Early Return (BẮT BUỘC)

**Áp dụng để tránh deep nesting (>2 levels), cải thiện khả năng đọc code.**

❌ **SAI - Deep nesting:**
```java
public void processUser(User user) {
    if (user != null) {
        if (user.isActive()) {
            if (user.hasPermission()) {
                // business logic here
            }
        }
    }
}
```

✅ **ĐÚNG - Early return với Guard Clauses:**
```java
public void processUser(User user) {
    if (user == null) {
        log.warn("User is null, cannot process");
        return;
    }

    if (!user.isActive()) {
        log.warn("User {} is not active", user.getId());
        return;
    }

    if (!user.hasPermission()) {
        log.warn("User {} has no permission", user.getId());
        return;
    }

    // business logic here - no nesting
}
```

### 🔴 4.5.2 Fail Fast (BẮT BUỘC)

**Kiểm tra và ném exception ngay từ đầu method.**

❌ **SAI - Validation ở giữa logic:**
```java
public Order createOrder(OrderRequest request) {
    Order order = new Order();
    order.setItems(request.getItems());
    // ... nhiều logic khác

    if (CollectionUtils.isEmpty(request.getItems())) {
        throw new ValidationException("Items cannot be empty");
    }
}
```

✅ **ĐÚNG - Fail Fast:**
```java
public Order createOrder(OrderRequest request) {
    // Validate ngay đầu method
    Objects.requireNonNull(request, "OrderRequest cannot be null");

    if (CollectionUtils.isEmpty(request.getItems())) {
        log.error("Order creation failed: items list is empty");
        throw new ValidationException("ORDER_001",
            getMessage("error.order.items.empty"));
    }

    // Business logic sau validation
    Order order = new Order();
    order.setItems(request.getItems());
    // ...
}
```

### 🔴 4.5.3 Tối ưu vòng lặp với break/continue (BẮT BUỘC)

**Giảm nesting không cần thiết.**

❌ **SAI - Deep nesting trong loop:**
```java
for (User user : users) {
    if (user.isActive()) {
        if (user.getRole().equals(Role.ADMIN)) {
            // process admin
        }
    }
}
```

✅ **ĐÚNG - Sử dụng continue:**
```java
for (User user : users) {
    if (!user.isActive()) {
        continue;
    }

    if (!user.getRole().equals(Role.ADMIN)) {
        continue;
    }

    // process admin - no nesting
}
```

### 🔴 4.5.4 Dùng Enum thay vì String (BẮT BUỘC)

**Đại diện cho trạng thái, loại, constants.**

❌ **SAI - Magic strings:**
```java
public class Order {
    private String status; // "PENDING", "COMPLETED", "CANCELLED"

    public boolean isPending() {
        return "PENDING".equals(status); // Typo-prone
    }
}
```

✅ **ĐÚNG - Enum với business logic:**
```java
public enum OrderStatus {
    PENDING("Đang xử lý"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy");

    @Getter
    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }
}

public class Order {
    private OrderStatus status;

    public boolean isPending() {
        return status == OrderStatus.PENDING;
    }
}
```

### 🔴 4.5.5 Try-with-resources (BẮT BUỘC)

**Tự động đóng tài nguyên.**

❌ **SAI - Manual close:**
```java
public String readFile(String path) {
    InputStream is = null;
    try {
        is = new FileInputStream(path);
        // read file
    } catch (IOException e) {
        log.error("Failed to read file", e);
    } finally {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                log.error("Failed to close stream", e);
            }
        }
    }
}
```

✅ **ĐÚNG - Try-with-resources:**
```java
public String readFile(String path) {
    try (InputStream is = new FileInputStream(path);
         BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

        return reader.lines().collect(Collectors.joining("\n"));

    } catch (IOException e) {
        log.error("Failed to read file: path={}", path, e);
        throw new FileProcessingException("FILE_001",
            getMessage("error.file.read", new Object[]{path}), e);
    }
}
```

### 🔴 4.5.6 Thứ tự method trong class (BẮT BUỘC)

**Constructor → Public methods → Private methods**

✅ **ĐÚNG:**
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    // 1. Constructor (auto-generated by @RequiredArgsConstructor)

    // 2. Public methods
    public User createUser(UserRequest request) {
        validateRequest(request);
        User user = buildUser(request);
        return userRepository.save(user);
    }

    public List<User> getAllActiveUsers() {
        return userRepository.findByStatus(UserStatus.ACTIVE);
    }

    // 3. Private methods
    private void validateRequest(UserRequest request) {
        // validation logic
    }

    private User buildUser(UserRequest request) {
        return User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .build();
    }
}
```

### 🔴 4.5.7 Thông báo lỗi và log chi tiết (BẮT BUỘC)

**Cung cấp error code, message rõ ràng, log đầy đủ context.**

❌ **SAI:**
```java
throw new Exception("Error");
log.error("Failed");
```

✅ **ĐÚNG:**
```java
// Exception với error code và message từ MessageSource
throw new BusinessException(
    "ORDER_001",
    messageSource.getMessage("error.order.insufficient.inventory",
        new Object[]{productId}, locale)
);

// Log với context đầy đủ
log.error("Failed to create order: userId={}, productId={}, reason={}",
    userId, productId, "Insufficient inventory", e);
```

**Cấu trúc message trong `messages.properties`:**
```properties
error.order.insufficient.inventory=Sản phẩm {0} không đủ số lượng trong kho
error.user.not.found=Không tìm thấy người dùng với ID {0}
error.folder.max.depth=Thư mục không thể vượt quá {0} cấp độ
```

### 🔴 4.5.8 Log hợp lý (BẮT BUỘC)

**Log đúng level và đủ thông tin:**

| Level | Khi nào dùng | Ví dụ |
|-------|-------------|-------|
| `ERROR` | Lỗi nghiêm trọng cần can thiệp ngay | Exception thrown, system failure |
| `WARN` | Cảnh báo, business rule violations | Max depth exceeded, validation failure |
| `INFO` | Business events quan trọng | User login, folder created, review submitted |
| `DEBUG` | Chi tiết cho developer (chỉ dev) | Method entry/exit, variable values |

**Ví dụ:**
```java
@Slf4j
public class OrderService {

    public Order createOrder(OrderRequest request) {
        log.info("Creating order: userId={}, itemCount={}",
            request.getUserId(), request.getItems().size());

        try {
            Order order = processOrder(request);

            log.info("Order created successfully: orderId={}, total={}",
                order.getId(), order.getTotalAmount());

            return order;

        } catch (InsufficientInventoryException e) {
            log.warn("Order creation failed due to insufficient inventory: userId={}, productId={}",
                request.getUserId(), e.getProductId());
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error while creating order: userId={}",
                request.getUserId(), e);
            throw new OrderProcessingException("ORDER_002",
                getMessage("error.order.processing.failed"), e);
        }
    }
}
```

**DO:**
- ✅ Dùng parameterized logging (`{}` placeholders)
- ✅ Log business events quan trọng
- ✅ Include context (userId, resourceId, etc.)
- ✅ Log exceptions với stack trace

**DON'T:**
- ❌ Log sensitive data (passwords, tokens, personal info)
- ❌ Log inside loops (performance impact)
- ❌ Dùng string concatenation (`+`) trong logs
- ❌ Over-log (pollutes logs)

### 🔴 4.5.9 Lombok Annotations (BẮT BUỘC)

**Giảm boilerplate code:**

✅ **Entity:**
```java
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;
}
```

✅ **Service:**
```java
@Service
@RequiredArgsConstructor  // Constructor injection cho final fields
@Slf4j                    // Logger
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    // Constructor tự động generated
}
```

✅ **DTO:**
```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private UserStatus status;
}
```

### 🔴 4.5.10 DRY (Don't Repeat Yourself) (BẮT BUỘC)

**Tránh lặp code, extract thành method/utility.**

❌ **SAI - Duplicate code:**
```java
if (user.getName() == null || user.getName().trim().isEmpty()) { }
if (user.getEmail() == null || user.getEmail().trim().isEmpty()) { }
if (user.getPhone() == null || user.getPhone().trim().isEmpty()) { }
```

✅ **ĐÚNG - Sử dụng Apache Commons:**
```java
if (StringUtils.isBlank(user.getName())) { }
if (StringUtils.isBlank(user.getEmail())) { }
if (StringUtils.isBlank(user.getPhone())) { }
```

✅ **ĐÚNG - Extract method:**
```java
private void validateUserFields(User user) {
    validateNotBlank(user.getName(), "Name");
    validateNotBlank(user.getEmail(), "Email");
    validateNotBlank(user.getPhone(), "Phone");
}

private void validateNotBlank(String value, String fieldName) {
    if (StringUtils.isBlank(value)) {
        throw new ValidationException(fieldName + " cannot be blank");
    }
}
```

### 🔴 4.5.11 KISS (Keep It Simple, Stupid) (BẮT BUỘC)

**Giữ code đơn giản, dễ hiểu.**

❌ **SAI - Quá phức tạp:**
```java
return users.stream()
    .filter(u -> u.getStatus() != null && u.getStatus().equals(UserStatus.ACTIVE))
    .map(u -> {
        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setName(u.getName());
        dto.setEmail(u.getEmail());
        return dto;
    })
    .collect(Collectors.toList());
```

✅ **ĐÚNG - Đơn giản và rõ ràng:**
```java
return users.stream()
    .filter(User::isActive)
    .map(this::toDTO)
    .toList();

private UserDTO toDTO(User user) {
    return UserDTO.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .build();
}
```

### 🔴 4.5.12 Stream API (BẮT BUỘC)

**Xử lý collections hiệu quả.**

❌ **SAI - Imperative style:**
```java
List<String> activeUserNames = new ArrayList<>();
for (User user : users) {
    if (user.isActive()) {
        activeUserNames.add(user.getName());
    }
}
```

✅ **ĐÚNG - Declarative với Stream API:**
```java
List<String> activeUserNames = users.stream()
    .filter(User::isActive)
    .map(User::getName)
    .toList();
```

**Các pattern thường dùng:**
```java
// Filter + Map
List<UserDTO> dtos = users.stream()
    .filter(User::isActive)
    .map(userMapper::toDTO)
    .toList();

// GroupBy
Map<UserStatus, List<User>> usersByStatus = users.stream()
    .collect(Collectors.groupingBy(User::getStatus));

// Sum/Average
double totalAmount = orders.stream()
    .mapToDouble(Order::getAmount)
    .sum();

// AnyMatch/AllMatch
boolean hasAdmin = users.stream()
    .anyMatch(u -> u.getRole() == Role.ADMIN);

// FindFirst with Optional
Optional<User> admin = users.stream()
    .filter(u -> u.getRole() == Role.ADMIN)
    .findFirst();
```

### 🔴 4.5.13 Java NIO2 cho File Operations (BẮT BUỘC)

**Sử dụng `java.nio.file.Files` thay vì `java.io.File`.**

❌ **SAI - Legacy java.io:**
```java
File file = new File(path);
FileInputStream fis = new FileInputStream(file);
```

✅ **ĐÚNG - Modern java.nio:**
```java
// Đọc file
String content = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);

// Ghi file
Files.writeString(Path.of(filePath), content, StandardCharsets.UTF_8);

// Copy file
Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

// Di chuyển file
Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

// Xóa file
Files.deleteIfExists(Path.of(filePath));

// Kiểm tra tồn tại
if (Files.exists(Path.of(filePath))) { }

// Đọc từng dòng với Stream
try (Stream<String> lines = Files.lines(Path.of(filePath))) {
    lines.filter(line -> line.contains("ERROR"))
         .forEach(log::error);
}

// List files trong directory
try (Stream<Path> files = Files.list(Path.of(dirPath))) {
    files.filter(Files::isRegularFile)
         .forEach(System.out::println);
}
```

### 🔴 4.5.14 Apache Commons Libraries (BẮT BUỘC)

**Sử dụng khi xử lý String, Collections, IO, etc.**

```java
// Apache Commons Lang3
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ObjectUtils;

// String operations
if (StringUtils.isBlank(name)) { }
if (StringUtils.isEmpty(email)) { }
String trimmed = StringUtils.trim(input);
String normalized = StringUtils.normalizeSpace(input);

// Apache Commons Collections4
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

// Collection operations
if (CollectionUtils.isEmpty(list)) { }
if (CollectionUtils.isNotEmpty(list)) { }
Collection<String> intersection = CollectionUtils.intersection(list1, list2);

// Map operations
if (MapUtils.isEmpty(map)) { }
String value = MapUtils.getString(map, "key", "defaultValue");

// Apache Commons IO
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

// File operations
FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
```

### ⚠️ Hậu quả khi KHÔNG tuân thủ

**Code sẽ bị REJECT trong code review nếu vi phạm:**

🔴 **BLOCKER** - Không được merge:
- Deep nesting (>2 levels)
- Không dùng Enum cho status/type
- Không dùng try-with-resources
- Không dùng Stream API cho collection processing
- Dùng `java.io.File` thay vì NIO2
- Không có log hoặc log không đủ thông tin
- Repeat code (vi phạm DRY)
- Không áp dụng Guard Clauses/Early Return
- Không kiểm tra null (vi phạm Fail Fast)
- Magic numbers/strings (không dùng constants/enum)

---

## 5. Spring Boot Best Practices

### 5.1 Dependency Injection

**Preferred**: Constructor injection (recommended by Spring)

```java
✅ Good - Constructor injection (immutable, testable)
@Service
public class FolderServiceImpl implements IFolderService {

    private final FolderRepository folderRepository;
    private final FolderMapper folderMapper;

    public FolderServiceImpl(FolderRepository folderRepository,
                            FolderMapper folderMapper) {
        this.folderRepository = folderRepository;
        this.folderMapper = folderMapper;
    }
}

❌ Bad - Field injection (hard to test, mutable)
@Service
public class FolderServiceImpl implements IFolderService {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderMapper folderMapper;
}
```

**Use Lombok `@RequiredArgsConstructor` for conciseness**:
```java
✅ Good - Lombok constructor injection
@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements IFolderService {

    private final FolderRepository folderRepository;
    private final FolderMapper folderMapper;

    // Constructor auto-generated by Lombok
}
```

### 5.2 Service Layer Design

**Pattern**: Interface + Implementation

```java
// Service Interface
public interface IFolderService {
    FolderResponse createFolder(CreateFolderRequest request, UUID userId);
    FolderResponse updateFolder(UUID folderId, UpdateFolderRequest request, UUID userId);
    void deleteFolder(UUID folderId, UUID userId);
    FolderResponse getFolder(UUID folderId, UUID userId);
    List<FolderResponse> getFoldersByUser(UUID userId);
}

// Service Implementation
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FolderServiceImpl implements IFolderService {

    private final FolderRepository folderRepository;

    @Transactional
    @Override
    public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
        // Implementation
    }
}
```

**Why Interface?**
- Easy to mock for testing
- Supports multiple implementations
- Clear contract between layers

### 5.3 Transaction Management

**Rules**:
- Service layer methods are transactional
- Read-only transactions by default (optimization)
- Write operations explicitly marked `@Transactional`

```java
@Service
@Transactional(readOnly = true)  // Default for all methods
public class FolderServiceImpl implements IFolderService {

    // Read method - uses default readOnly=true
    @Override
    public FolderResponse getFolder(UUID folderId, UUID userId) {
        // No @Transactional needed
    }

    // Write method - override with readOnly=false
    @Transactional  // readOnly=false by default when not specified
    @Override
    public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
        // Write operations
    }

    // Write method with explicit settings
    @Transactional(rollbackFor = Exception.class, timeout = 30)
    @Override
    public void copyFolder(UUID folderId, UUID destinationId) {
        // Long-running operation
    }
}
```

### 5.4 Controller Design

**REST Best Practices**:

```java
@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final IFolderService folderService;

    // GET - Retrieve resource(s)
    @GetMapping
    public ResponseEntity<List<FolderResponse>> getFolders() {
        List<FolderResponse> folders = folderService.getFoldersByUser(getCurrentUserId());
        return ResponseEntity.ok(folders);
    }

    // GET - Retrieve single resource
    @GetMapping("/{folderId}")
    public ResponseEntity<FolderResponse> getFolder(@PathVariable UUID folderId) {
        FolderResponse folder = folderService.getFolder(folderId, getCurrentUserId());
        return ResponseEntity.ok(folder);
    }

    // POST - Create resource
    @PostMapping
    public ResponseEntity<FolderResponse> createFolder(
            @Valid @RequestBody CreateFolderRequest request) {
        FolderResponse folder = folderService.createFolder(request, getCurrentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(folder);
    }

    // PUT - Update resource (full replacement)
    @PutMapping("/{folderId}")
    public ResponseEntity<FolderResponse> updateFolder(
            @PathVariable UUID folderId,
            @Valid @RequestBody UpdateFolderRequest request) {
        FolderResponse folder = folderService.updateFolder(folderId, request, getCurrentUserId());
        return ResponseEntity.ok(folder);
    }

    // DELETE - Delete resource
    @DeleteMapping("/{folderId}")
    public ResponseEntity<Void> deleteFolder(@PathVariable UUID folderId) {
        folderService.deleteFolder(folderId, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    // PATCH - Partial update (optional, use PUT for MVP)
    @PatchMapping("/{folderId}/move")
    public ResponseEntity<FolderResponse> moveFolder(
            @PathVariable UUID folderId,
            @Valid @RequestBody MoveFolderRequest request) {
        FolderResponse folder = folderService.moveFolder(folderId, request.getNewParentId(), getCurrentUserId());
        return ResponseEntity.ok(folder);
    }

    // Helper method
    private UUID getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }
}
```

**Controller Guidelines**:
- Use `@RestController` (combines `@Controller` + `@ResponseBody`)
- Use `@RequestMapping` at class level for base path
- Use `@Valid` for automatic validation
- Use `ResponseEntity` for explicit HTTP status codes
- Return `201 Created` for POST, `204 No Content` for DELETE
- Keep controllers thin - delegate business logic to service layer

### 5.5 Repository Design

**Spring Data JPA Best Practices**:

```java
@Repository
public interface FolderRepository extends JpaRepository<Folder, UUID> {

    // Method naming convention - auto-implemented by Spring Data JPA
    List<Folder> findByUserAndParentFolderIsNullAndDeletedAtIsNull(User user);

    Optional<Folder> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

    // Custom JPQL query
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId " +
           "AND f.path LIKE CONCAT(:pathPrefix, '%') " +
           "AND f.deletedAt IS NULL")
    List<Folder> findDescendants(@Param("userId") UUID userId,
                                  @Param("pathPrefix") String pathPrefix);

    // Native SQL query (use sparingly)
    @Query(value = "SELECT * FROM folders WHERE user_id = :userId " +
                   "AND path ~ :pathPattern AND deleted_at IS NULL",
           nativeQuery = true)
    List<Folder> findByPathPattern(@Param("userId") UUID userId,
                                    @Param("pathPattern") String pathPattern);

    // Count query
    @Query("SELECT COUNT(f) FROM Folder f WHERE f.user.id = :userId AND f.deletedAt IS NULL")
    long countByUser(@Param("userId") UUID userId);

    // Delete query (use soft delete instead)
    @Modifying
    @Query("UPDATE Folder f SET f.deletedAt = :deletedAt WHERE f.id = :folderId")
    void softDelete(@Param("folderId") UUID folderId, @Param("deletedAt") Instant deletedAt);
}
```

**Repository Guidelines**:
- Use Spring Data JPA method naming conventions when possible
- Use `@Query` for complex queries
- Avoid native queries unless necessary (portability)
- Use `@Param` for named parameters
- Use `@Modifying` for UPDATE/DELETE queries
- Always include `deletedAt IS NULL` filter for soft-deleted entities

---

## 6. JPA Entity Design

### 6.1 Entity Annotation

```java
@Entity
@Table(name = "folders")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Folder extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder;

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> subFolders = new ArrayList<>();

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deck> decks = new ArrayList<>();

    @NotNull
    @Column(name = "depth", nullable = false)
    private Integer depth = 0;

    @NotNull
    @Size(max = 500)
    @Column(name = "path", nullable = false, length = 500)
    private String path;

    // Business logic methods
    public void addSubFolder(Folder subFolder) {
        if (this.depth >= 9) {
            throw new MaxDepthExceededException("Max depth 10 exceeded");
        }
        subFolder.setParentFolder(this);
        subFolder.setDepth(this.depth + 1);
        subFolder.updatePath();
        this.subFolders.add(subFolder);
    }

    public void updatePath() {
        if (parentFolder == null) {
            this.path = "/" + id.toString();
        } else {
            this.path = parentFolder.getPath() + "/" + id.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Folder)) return false;
        Folder folder = (Folder) o;
        return id != null && id.equals(folder.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
```

**Entity Guidelines**:
- Use `@Table(name = "...")` for explicit table name
- Use `@Column(name = "...")` for explicit column name (snake_case in DB)
- Use Lombok `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- Use `FetchType.LAZY` for associations (avoid N+1 problem)
- Use `CascadeType.ALL` carefully (understand implications)
- Override `equals()` and `hashCode()` for entities with collections
- Add business logic methods in entity (DDD principle)

### 6.2 Base Entity Pattern

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

@MappedSuperclass
@Getter
@Setter
public abstract class SoftDeletableEntity extends BaseEntity {

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public void softDelete() {
        this.deletedAt = Instant.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
```

**Benefits**:
- Consistent auditing fields across all entities
- Soft delete functionality built-in
- Reduces code duplication

---

## 7. DTO Design

### 7.1 Request DTO

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFolderRequest {

    @NotBlank(message = "Folder name is required")
    @Size(max = 100, message = "Folder name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private UUID parentFolderId;  // Optional
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFolderRequest {

    @NotBlank(message = "Folder name is required")
    @Size(max = 100, message = "Folder name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
```

**Request DTO Guidelines**:
- Use Bean Validation annotations (`@NotBlank`, `@Size`, `@Email`, etc.)
- Include validation error messages
- Use Lombok `@Data` for getters/setters/toString/equals/hashCode
- Keep DTOs immutable when possible (`@Value` from Lombok)

### 7.2 Response DTO

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderResponse {

    private UUID id;
    private String name;
    private String description;
    private UUID parentFolderId;
    private int depth;
    private int subFolderCount;
    private int deckCount;
    private Instant createdAt;
    private Instant updatedAt;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderTreeResponse {

    private UUID id;
    private String name;
    private UUID parentFolderId;
    private int depth;
    private List<FolderTreeResponse> children;  // Recursive structure
}
```

**Response DTO Guidelines**:
- Only expose necessary fields (don't expose internal fields)
- Use nested DTOs for complex structures
- Use Lombok `@Data` or `@Value` for immutability
- Map entities to DTOs using MapStruct

### 7.3 MapStruct Mapper

```java
@Mapper(componentModel = "spring")
public interface FolderMapper {

    // Entity to Response
    @Mapping(source = "parentFolder.id", target = "parentFolderId")
    @Mapping(expression = "java(folder.getSubFolders().size())", target = "subFolderCount")
    @Mapping(expression = "java(folder.getDecks().size())", target = "deckCount")
    FolderResponse toResponse(Folder folder);

    List<FolderResponse> toResponseList(List<Folder> folders);

    // Request to Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Folder toEntity(CreateFolderRequest request);

    // Update entity from request
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parentFolder", ignore = true)
    void updateEntity(@MappingTarget Folder folder, UpdateFolderRequest request);
}
```

**MapStruct Guidelines**:
- Use `componentModel = "spring"` for Spring dependency injection
- Use `@Mapping` to customize field mapping
- Use `ignore = true` for fields that should not be mapped
- Use `@MappingTarget` for update operations
- Keep mapper interfaces simple and focused

---

## 8. Exception Handling

### 8.1 Custom Exceptions

```java
// Base exception
public class RepeatWiseException extends RuntimeException {
    public RepeatWiseException(String message) {
        super(message);
    }

    public RepeatWiseException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Specific exceptions
public class ResourceNotFoundException extends RepeatWiseException {
    public ResourceNotFoundException(String resourceName, UUID id) {
        super(String.format("%s not found with id: %s", resourceName, id));
    }
}

public class ValidationException extends RepeatWiseException {
    public ValidationException(String message) {
        super(message);
    }
}

public class MaxDepthExceededException extends ValidationException {
    public MaxDepthExceededException(String message) {
        super(message);
    }
}
```

### 8.2 Global Exception Handler

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("NOT_FOUND")
            .message(ex.getMessage())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("VALIDATION_ERROR")
            .message(ex.getMessage())
            .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBeanValidation(MethodArgumentNotValidException ex) {
        log.warn("Bean validation error: {}", ex.getMessage());

        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.toList());

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("VALIDATION_ERROR")
            .message("Validation failed")
            .details(errors)
            .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("INTERNAL_SERVER_ERROR")
            .message("An unexpected error occurred")
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private List<String> details;
    private String path;
    private String traceId;
}
```

---

## 9. Logging

### 9.1 Logging Framework

**Use**: SLF4J + Logback (default in Spring Boot)

**Logger Declaration**:
```java
✅ Good - Use Lombok @Slf4j
@Service
@Slf4j
@RequiredArgsConstructor
public class FolderServiceImpl implements IFolderService {

    public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
        log.info("Creating folder: {} for user: {}", request.getName(), userId);
        // ...
    }
}

✅ Good - Manual logger (if not using Lombok)
@Service
public class FolderServiceImpl implements IFolderService {

    private static final Logger log = LoggerFactory.getLogger(FolderServiceImpl.class);

    public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
        log.info("Creating folder: {} for user: {}", request.getName(), userId);
        // ...
    }
}
```

### 9.2 Log Levels

| Level | When to Use | Example |
|-------|-------------|---------|
| `ERROR` | Errors that require immediate attention | Exception thrown, system failure |
| `WARN` | Potential issues, business rule violations | Max depth exceeded, validation failure |
| `INFO` | Important business events | User login, folder created, review submitted |
| `DEBUG` | Detailed execution flow (dev only) | Method entry/exit, variable values |
| `TRACE` | Very detailed info (rarely used) | SQL queries, detailed loops |

**Examples**:
```java
// ERROR - System failures
try {
    folderRepository.save(folder);
} catch (DataAccessException ex) {
    log.error("Failed to save folder: {}", folder.getId(), ex);
    throw new DatabaseException("Failed to save folder", ex);
}

// WARN - Business rule violations
if (parent.getDepth() >= 9) {
    log.warn("Max depth exceeded for folder: {}, user: {}", parent.getId(), userId);
    throw new MaxDepthExceededException("Cannot exceed depth 10");
}

// INFO - Business events
log.info("User {} logged in successfully", userId);
log.info("Folder {} created by user {}", folderId, userId);
log.info("Review session completed: {} cards reviewed", cardCount);

// DEBUG - Execution details (only in dev)
log.debug("Calculating due cards for user: {}, date: {}", userId, today);
log.debug("Found {} due cards", dueCards.size());
```

### 9.3 Logging Best Practices

**DO**:
- Use parameterized logging (`{}` placeholders)
- Log important business events
- Include context (user ID, resource ID, etc.)
- Log exceptions with stack trace

**DON'T**:
- Log sensitive data (passwords, tokens, personal info)
- Log inside loops (performance impact)
- Use string concatenation (`+`) in logs
- Over-log (pollutes logs)

```java
✅ Good
log.info("User {} created folder {}", userId, folderId);
log.error("Failed to load folder {}", folderId, ex);

❌ Bad
log.info("User " + userId + " created folder " + folderId);  // String concat
log.info("User logged in with password: {}", password);  // Sensitive data
for (Card card : cards) {
    log.debug("Processing card: {}", card.getId());  // Inside loop
}
```

---

## 10. Testing

### 10.1 Test Structure

**Package Structure**:
```
src/test/java/com/repeatwise/
├── service/
│   ├── FolderServiceImplTest.java
│   └── ReviewServiceImplTest.java
├── repository/
│   ├── FolderRepositoryTest.java
│   └── CardBoxPositionRepositoryTest.java
├── controller/
│   ├── FolderControllerTest.java
│   └── ReviewControllerTest.java
└── integration/
    ├── FolderIntegrationTest.java
    └── ReviewFlowIntegrationTest.java
```

### 10.2 Unit Test Example

```java
@ExtendWith(MockitoExtension.class)
class FolderServiceImplTest {

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FolderMapper folderMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private FolderServiceImpl folderService;

    @Test
    @DisplayName("Should create folder successfully when valid request")
    void shouldCreateFolderSuccessfully() {
        // Given
        UUID userId = UUID.randomUUID();
        CreateFolderRequest request = CreateFolderRequest.builder()
            .name("Test Folder")
            .description("Test description")
            .build();

        User user = new User();
        user.setId(userId);

        Folder savedFolder = Folder.builder()
            .id(UUID.randomUUID())
            .name("Test Folder")
            .user(user)
            .build();

        FolderResponse expectedResponse = FolderResponse.builder()
            .id(savedFolder.getId())
            .name("Test Folder")
            .build();

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(folderRepository.save(any(Folder.class))).thenReturn(savedFolder);
        when(folderMapper.toResponse(savedFolder)).thenReturn(expectedResponse);

        // When
        FolderResponse result = folderService.createFolder(request, userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Folder");

        verify(folderRepository).save(any(Folder.class));
        verify(eventPublisher).publishEvent(any(FolderCreatedEvent.class));
    }

    @Test
    @DisplayName("Should throw exception when max depth exceeded")
    void shouldThrowExceptionWhenMaxDepthExceeded() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        CreateFolderRequest request = CreateFolderRequest.builder()
            .name("Test Folder")
            .parentFolderId(parentId)
            .build();

        Folder parent = Folder.builder()
            .id(parentId)
            .depth(9)  // Max depth - 1
            .build();

        when(folderRepository.findById(parentId)).thenReturn(Optional.of(parent));

        // When & Then
        assertThatThrownBy(() -> folderService.createFolder(request, userId))
            .isInstanceOf(MaxDepthExceededException.class)
            .hasMessageContaining("depth 10");

        verify(folderRepository, never()).save(any());
    }
}
```

### 10.3 Repository Test Example (Integration Test)

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class FolderRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find root folders by user")
    void shouldFindRootFoldersByUser() {
        // Given
        User user = createUser("test@example.com");
        userRepository.save(user);

        Folder root1 = createFolder("Root 1", null, user);
        Folder root2 = createFolder("Root 2", null, user);
        Folder child = createFolder("Child", root1, user);

        folderRepository.saveAll(List.of(root1, root2, child));

        // When
        List<Folder> roots = folderRepository
            .findByUserAndParentFolderIsNullAndDeletedAtIsNull(user);

        // Then
        assertThat(roots).hasSize(2);
        assertThat(roots).extracting(Folder::getName)
            .containsExactlyInAnyOrder("Root 1", "Root 2");
    }

    private User createUser(String email) {
        return User.builder()
            .email(email)
            .name("Test User")
            .passwordHash("hash")
            .build();
    }

    private Folder createFolder(String name, Folder parent, User user) {
        return Folder.builder()
            .name(name)
            .user(user)
            .parentFolder(parent)
            .depth(parent == null ? 0 : parent.getDepth() + 1)
            .build();
    }
}
```

### 10.4 Test Naming Convention

**Format**: `should{ExpectedBehavior}When{StateUnderTest}`

```java
✅ Good
void shouldCreateFolderSuccessfully()
void shouldThrowExceptionWhenMaxDepthExceeded()
void shouldReturnEmptyListWhenNoFoldersExist()
void shouldUpdateDueDateWhenCardReviewed()

❌ Bad
void testCreateFolder()
void test1()
void folderCreation()
```

---

## 11. Security Best Practices

### 11.1 Authentication

```java
// Get current user
@Service
public class SecurityUtils {

    public static UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return principal.getUserId();
    }

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        return (User) auth.getPrincipal();
    }
}
```

### 11.2 Authorization

```java
// Service layer authorization
@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements IFolderService {

    private final FolderRepository folderRepository;

    @Override
    public FolderResponse getFolder(UUID folderId, UUID userId) {
        Folder folder = folderRepository.findById(folderId)
            .orElseThrow(() -> new ResourceNotFoundException("Folder", folderId));

        // Authorization check - user can only access their own folders
        if (!folder.getUser().getId().equals(userId)) {
            throw new ForbiddenException("User not authorized to access this folder");
        }

        return folderMapper.toResponse(folder);
    }
}
```

### 11.3 Input Validation

```java
// Always validate user input
@PostMapping
public ResponseEntity<FolderResponse> createFolder(
        @Valid @RequestBody CreateFolderRequest request) {  // @Valid triggers validation
    // Spring automatically validates and returns 400 if invalid
    FolderResponse folder = folderService.createFolder(request, getCurrentUserId());
    return ResponseEntity.status(HttpStatus.CREATED).body(folder);
}
```

### 11.4 SQL Injection Prevention

```java
✅ Good - Use JPA/JPQL with parameters
@Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.name = :name")
List<Folder> findByUserAndName(@Param("userId") UUID userId, @Param("name") String name);

❌ Bad - String concatenation (SQL injection risk)
@Query(value = "SELECT * FROM folders WHERE user_id = '" + userId + "'", nativeQuery = true)
List<Folder> findByUser(String userId);
```

---

## 12. Performance Best Practices

### 12.1 Database Query Optimization

```java
// Use JOIN FETCH to avoid N+1 problem
@Query("SELECT DISTINCT f FROM Folder f LEFT JOIN FETCH f.decks WHERE f.user.id = :userId")
List<Folder> findAllByUserWithDecks(@Param("userId") UUID userId);

// Use @EntityGraph
@EntityGraph(attributePaths = {"decks", "subFolders"})
List<Folder> findByUserId(UUID userId);

// Use pagination for large result sets
Page<Card> findByDeckId(UUID deckId, Pageable pageable);
```

### 12.2 Batch Operations

```java
@Transactional
public void importCards(UUID deckId, List<CardImportDto> cardDtos) {
    Deck deck = deckRepository.findById(deckId)
        .orElseThrow(() -> new ResourceNotFoundException("Deck", deckId));

    int batchSize = 1000;
    for (int i = 0; i < cardDtos.size(); i++) {
        CardImportDto dto = cardDtos.get(i);

        Card card = new Card();
        card.setDeck(deck);
        card.setFront(dto.getFront());
        card.setBack(dto.getBack());
        deck.getCards().add(card);

        // Flush and clear every 1000 entities
        if (i > 0 && i % batchSize == 0) {
            deckRepository.flush();
            deckRepository.clear();
        }
    }

    deckRepository.flush();
}
```

---

## 13. Code Review Checklist

Before submitting PR, check:

- [ ] Code follows Google Java Style Guide
- [ ] All tests pass
- [ ] Test coverage ≥ 70% for new code
- [ ] No hardcoded values (use constants or config)
- [ ] No sensitive data in logs
- [ ] Proper exception handling
- [ ] Input validation implemented
- [ ] Authorization checks in place
- [ ] No SQL injection vulnerabilities
- [ ] No N+1 query problems
- [ ] Javadoc for public APIs
- [ ] No compiler warnings
- [ ] No SonarLint issues
- [ ] Transaction boundaries correct
- [ ] Proper logging levels used

---

## 14. References

- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Spring Boot Best Practices](https://docs.spring.io/spring-boot/reference/)
- [Effective Java (3rd Edition)](https://www.oreilly.com/library/view/effective-java-3rd/9780134686097/)
- [Clean Code by Robert C. Martin](https://www.oreilly.com/library/view/clean-code-a/9780136083238/)
