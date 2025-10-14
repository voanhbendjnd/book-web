# 🚀 MapStruct Integration Guide

## 📋 **Tổng quan**

MapStruct đã được tích hợp vào dự án để thay thế việc mapping thủ công giữa Entity và DTO. Điều này giúp:

- ✅ **Giảm boilerplate code**
- ✅ **Type-safe mapping**
- ✅ **Performance tốt hơn**
- ✅ **Dễ maintain và debug**

## 🛠️ **Cấu hình hiện tại**

### **Dependencies trong `build.gradle.kts`:**

```kotlin
implementation("org.mapstruct:mapstruct:1.6.2")
annotationProcessor("org.mapstruct:mapstruct-processor:1.6.2")
```

### **Mappers đã tạo:**

1. **`UserMapper`** - Mapping User entity
2. **`BookMapper`** - Mapping Book entity
3. **`CategoryMapper`** - Mapping Category entity
4. **`OrderMapper`** - Mapping Order entity

## 📁 **Cấu trúc Mappers**

### **1. UserMapper**

```java
@Mapper(componentModel = "spring")
@Component
public interface UserMapper {

    // Entity → DTO
    @Mapping(source = "role.name", target = "role")
    ResLoginDTO.UserLogin toUserLogin(User user);

    // DTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toUser(CreateAccountDTO createAccountDTO);

    // Update existing entity
    void updateUserFromDTO(UpdateUserDTO updateUserDTO, @MappingTarget User user);
}
```

### **2. BookMapper**

```java
@Mapper(componentModel = "spring")
@Component
public interface BookMapper {

    // Entity → DTO với custom mapping
    @Mapping(target = "categories", expression = "java(mapCategoriesToStrings(book.getCategories()))")
    BookDTO toBookDTO(Book book);

    // Helper method
    default List<String> mapCategoriesToStrings(List<Category> categories) {
        if (categories == null) return null;
        return categories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }
}
```

## 🎯 **Cách sử dụng trong Controllers**

### **Before (Manual Mapping):**

```java
// ❌ Old way - Manual mapping
ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
    user.getId(),
    user.getEmail(),
    user.getName(),
    user.getAvatar(),
    user.getAddress(),
    user.getPhone(),
    user.getRole().getName(),
    user.getGender()
);
```

### **After (MapStruct):**

```java
// ✅ New way - MapStruct
@Autowired
private UserMapper userMapper;

ResLoginDTO.UserLogin userLogin = this.userMapper.toUserLogin(user);
```

## 🔧 **Ví dụ thực tế trong AuthController**

### **1. Inject Mapper:**

```java
@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final UserMapper userMapper; // Inject mapper

    public AuthController(/* other dependencies */, UserMapper userMapper) {
        this.userMapper = userMapper;
    }
}
```

### **2. Sử dụng trong Login:**

```java
@PostMapping("/auth/login")
public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO dto) {
    // ... authentication logic ...

    User user = this.userService.fetchUserByEmail(dto.getUsername());
    if (user != null) {
        // 🚀 Sử dụng MapStruct thay vì manual mapping
        ResLoginDTO.UserLogin userLogin = this.userMapper.toUserLogin(user);
        res.setUser(userLogin);
    }

    // ... rest of logic ...
}
```

## 📝 **Các loại Mapping thường dùng**

### **1. Entity → DTO (Read Operations):**

```java
// Single mapping
BookDTO bookDTO = bookMapper.toBookDTO(book);

// List mapping
List<BookDTO> bookDTOs = bookMapper.toBookDTOList(books);
```

### **2. DTO → Entity (Create Operations):**

```java
// Create new entity
Book book = bookMapper.toBook(bookDTO);
Book savedBook = bookRepository.save(book);
```

### **3. Update Existing Entity:**

```java
// Update existing entity
Book existingBook = bookRepository.findById(id).orElseThrow();
bookMapper.updateBookFromDTO(bookDTO, existingBook);
bookRepository.save(existingBook);
```

### **4. Complex Mappings:**

```java
// Custom mapping với expression
@Mapping(target = "categories", expression = "java(mapCategoriesToStrings(book.getCategories()))")
BookDTO toBookDTO(Book book);

// Custom mapping với qualifiedByName
@Mapping(source = "orderItems", target = "orderItems", qualifiedByName = "mapOrderItems")
ResOrderDTO toResOrderDTO(Order order);

@Named("mapOrderItems")
default List<Object> mapOrderItems(List<OrderItem> orderItems) {
    // Custom logic here
    return orderItems.stream()
        .map(this::mapOrderItem)
        .collect(Collectors.toList());
}
```

## 🎨 **Advanced Features**

### **1. Conditional Mapping:**

```java
@Mapping(target = "active",
         expression = "java(user.getActive() != null ? user.getActive() : true)")
UserDTO toUserDTO(User user);
```

### **2. Custom Methods:**

```java
@Mapper(componentModel = "spring")
public interface BookMapper {

    // Helper method để xử lý complex logic
    default List<String> mapCategoriesToStrings(List<Category> categories) {
        if (categories == null) return null;
        return categories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    // Helper method với null check
    default String mapRoleName(Role role) {
        return role != null ? role.getName() : null;
    }
}
```

### **3. Multiple Source Objects:**

```java
@Mapping(source = "user.id", target = "userId")
@Mapping(source = "order.totalAmount", target = "totalAmount")
OrderSummaryDTO toOrderSummaryDTO(User user, Order order);
```

## 🔄 **Migration Strategy**

### **Bước 1: Update AuthController (✅ Completed)**

```java
// Thay thế manual mapping bằng MapStruct
ResLoginDTO.UserLogin userLogin = this.userMapper.toUserLogin(user);
```

### **Bước 2: Update BookController**

```java
@RestController
@RequestMapping("/api/v1")
public class BookController {
    private final BookService bookService;
    private final BookMapper bookMapper; // Add mapper

    @GetMapping("/books/{id}")
    public ResponseEntity<ResBook> fetch(@PathVariable("id") Long id) {
        Book book = bookService.findById(id);
        ResBook resBook = bookMapper.toResBookDTO(book); // Use mapper
        return ResponseEntity.ok(resBook);
    }
}
```

### **Bước 3: Update Service Layer**

```java
@Service
public class BookService {
    private final BookMapper bookMapper;

    public ResCreateBook createBasic(BookDTO dto) {
        Book book = bookMapper.toBook(dto); // DTO → Entity
        // ... business logic ...
        Book savedBook = bookRepository.save(book);
        return bookMapper.toResCreateBook(savedBook); // Entity → Response DTO
    }
}
```

## 🚀 **Best Practices**

### **1. Naming Convention:**

```java
// Good naming
UserMapper userMapper;
BookMapper bookMapper;
CategoryMapper categoryMapper;

// Method naming
toUserLogin(User user)           // Entity → DTO
toUser(CreateAccountDTO dto)     // DTO → Entity
updateUserFromDTO(UpdateUserDTO dto, @MappingTarget User user) // Update
```

### **2. Ignore Fields:**

```java
@Mapping(target = "id", ignore = true)           // Auto-generated
@Mapping(target = "createdAt", ignore = true)    // Auto-managed
@Mapping(target = "password", ignore = true)     // Sensitive data
```

### **3. Handle Null Values:**

```java
@Mapping(target = "role", source = "role.name",
         nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
UserDTO toUserDTO(User user);
```

### **4. Performance Tips:**

```java
// Use @Named for reusable mappings
@Named("mapOrderItems")
default List<Object> mapOrderItems(List<OrderItem> items) {
    // Reusable logic
}

// Use qualifiedByName for complex mappings
@Mapping(source = "items", target = "items", qualifiedByName = "mapOrderItems")
ResOrderDTO toResOrderDTO(Order order);
```

## 📊 **Benefits Summary**

| Aspect              | Before (Manual)        | After (MapStruct)      |
| ------------------- | ---------------------- | ---------------------- |
| **Lines of Code**   | 8-10 lines per mapping | 1 line per mapping     |
| **Type Safety**     | ❌ Runtime errors      | ✅ Compile-time safety |
| **Performance**     | ❌ Reflection          | ✅ Direct method calls |
| **Maintainability** | ❌ Hard to maintain    | ✅ Auto-generated      |
| **Debugging**       | ❌ Hard to debug       | ✅ Clear stack traces  |

## 🎯 **Next Steps**

1. **✅ Completed**: UserMapper integration in AuthController
2. **🔄 In Progress**: Update other controllers to use mappers
3. **📋 TODO**: Update Service layer to use mappers
4. **📋 TODO**: Add more complex mappings as needed
5. **📋 TODO**: Add unit tests for mappers

## 🔍 **Debugging MapStruct**

### **Generated Implementation Location:**

```
build/generated/sources/annotationProcessor/java/main/djnd/ben1607/drink_shop/mapper/
```

### **Common Issues:**

1. **Compilation Error**: Check if all dependencies are properly imported
2. **NullPointerException**: Add null checks in helper methods
3. **Mapping Not Working**: Verify field names match exactly

### **Enable Debug Logging:**

```yaml
# application.yml
logging:
  level:
    djnd.ben1607.drink_shop.mapper: DEBUG
```

---

**🎉 MapStruct đã được tích hợp thành công! Giờ bạn có thể sử dụng các mapper để thay thế việc mapping thủ công trong toàn bộ dự án.**
