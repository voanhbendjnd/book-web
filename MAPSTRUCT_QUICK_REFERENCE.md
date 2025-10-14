# 🚀 MapStruct Quick Reference

## 📋 Các Mapper có sẵn

| Mapper             | Location                     | Chức năng chính        |
| ------------------ | ---------------------------- | ---------------------- |
| **UserMapper**     | `mapper/UserMapper.java`     | User entity ↔ DTOs     |
| **BookMapper**     | `mapper/BookMapper.java`     | Book entity ↔ DTOs     |
| **CategoryMapper** | `mapper/CategoryMapper.java` | Category entity ↔ DTOs |
| **OrderMapper**    | `mapper/OrderMapper.java`    | Order entity ↔ DTOs    |

---

## 🎯 UserMapper Methods

```java
// Inject
@Autowired private UserMapper userMapper;

// Entity → Response DTO
ResLoginDTO.UserLogin userLogin = userMapper.toUserLogin(user);
ResLoginDTO.UserInsideToken token = userMapper.toUserInsideToken(user);

// DTO → Entity
User user = userMapper.toUser(createAccountDTO);

// Update Entity
userMapper.updateUserFromDTO(updateUserDTO, existingUser);

// Entity → DTO
UserDTO dto = userMapper.toUserDTO(user);
```

---

## 📚 BookMapper Methods

```java
// Inject
@Autowired private BookMapper bookMapper;

// DTO → Entity
Book book = bookMapper.toBook(bookDTO);

// Entity → Response DTO
ResBook resBook = bookMapper.toResBook(book);
ResCreateBook resCreate = bookMapper.toResCreateBook(book);

// Update Entity
bookMapper.updateBookFromDTO(bookDTO, existingBook);

// Entity → DTO
BookDTO dto = bookMapper.toBookDTO(book);

// List conversion
List<ResBook> books = bookMapper.toResBookList(bookList);
```

---

## 🏷️ CategoryMapper Methods

```java
// Inject
@Autowired private CategoryMapper categoryMapper;

// DTO → Entity
Category category = categoryMapper.toCategory(categoryDTO);

// Entity → Response DTO
ResCategory resCategory = categoryMapper.toResCategory(category);
ResCreateCategory resCreate = categoryMapper.toResCreateCategory(category);

// Update Entity
categoryMapper.updateCategoryFromDTO(categoryDTO, existingCategory);

// Entity → DTO
CategoryDTO dto = categoryMapper.toCategoryDTO(category);

// List conversion
List<ResCategory> categories = categoryMapper.toResCategoryList(categoryList);
List<CategoryDTO> dtos = categoryMapper.toCategoryDTOList(categoryList);
```

---

## 📦 OrderMapper Methods

```java
// Inject
@Autowired private OrderMapper orderMapper;

// DTO → Entity
Order order = orderMapper.toOrder(orderDTO);

// Entity → Response DTO
ResOrder resOrder = orderMapper.toResOrder(order);
ResDataOrder resData = orderMapper.toResDataOrder(order);

// Update Entity
orderMapper.updateOrderFromDTO(orderDTO, existingOrder);

// Entity → DTO
OrderDTO dto = orderMapper.toOrderDTO(order);

// List conversion
List<ResOrder> orders = orderMapper.toResOrderList(orderList);
List<OrderDTO> dtos = orderMapper.toOrderDTOList(orderList);
```

---

## 💡 Common Patterns

### **Pattern 1: CREATE**

```java
// DTO → Entity → Save → Response
Book book = bookMapper.toBook(dto);
book = bookRepository.save(book);
return bookMapper.toResCreateBook(book);
```

### **Pattern 2: READ**

```java
// Find → Entity → Response
Book book = bookRepository.findById(id).orElseThrow();
return bookMapper.toResBook(book);
```

### **Pattern 3: UPDATE**

```java
// Find → Update → Save → Response
Book book = bookRepository.findById(id).orElseThrow();
bookMapper.updateBookFromDTO(dto, book);
book = bookRepository.save(book);
return bookMapper.toResBook(book);
```

### **Pattern 4: LIST**

```java
// FindAll → Convert List → Response
List<Book> books = bookRepository.findAll();
return bookMapper.toResBookList(books);
```

---

## ⚡ Quick Commands

### **Rebuild project:**

```bash
./gradlew clean build
```

### **View generated mappers:**

```
build/generated/sources/annotationProcessor/java/main/
└── djnd/ben1607/drink_shop/mapper/
    ├── UserMapperImpl.java
    ├── BookMapperImpl.java
    ├── CategoryMapperImpl.java
    └── OrderMapperImpl.java
```

---

## ✅ Integration Checklist

- [ ] Inject mapper in constructor
- [ ] Use mapper for DTO → Entity conversion
- [ ] Use mapper for Entity → Response DTO conversion
- [ ] Use updateXxxFromDTO for updates
- [ ] Handle null cases appropriately
- [ ] Add business logic between conversions

---

## 📝 Example Integration

### **Before (Manual):**

```java
// ❌ 6 lines of manual mapping
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
// ✅ 1 line with MapStruct
ResLoginDTO.UserLogin userLogin = userMapper.toUserLogin(user);
```

---

**Tài liệu đầy đủ:**

- `MAPSTRUCT_USAGE_GUIDE.md` - Hướng dẫn chi tiết
- `MAPSTRUCT_CONTROLLER_EXAMPLES.md` - Ví dụ trong Controllers
- `MAPSTRUCT_IMPLEMENTATION_SUMMARY.md` - Tổng kết implementation
