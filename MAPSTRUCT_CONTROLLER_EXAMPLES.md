# 🎯 MapStruct Controller Examples

## Ví dụ thực tế về cách sử dụng MapStruct Mappers trong Controllers

---

## 📘 **1. BookController Example**

### **Inject BookMapper:**

```java
@RestController
@RequestMapping("/api/v1")
public class BookController {
    private final BookService bookService;
    private final BookMapper bookMapper; // ← Inject BookMapper

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }
```

### **Create Book:**

```java
@PostMapping("/books2")
@ApiMessage("Create new book basic")
public ResponseEntity<ResCreateBook> createBasic(@RequestBody BookDTO dto) {
    // 🚀 Sử dụng MapStruct thay vì manual mapping
    Book book = bookMapper.toBook(dto);

    // Business logic
    book = bookRepository.save(book);

    // Convert entity to response DTO
    ResCreateBook response = bookMapper.toResCreateBook(book);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

### **Get Book by ID:**

```java
@GetMapping("/books/{id}")
@ApiMessage("Fetch book by ID")
public ResponseEntity<ResBook> fetch(@PathVariable("id") Long id) throws IdInvalidException {
    if (!this.bookService.existsById(id)) {
        throw new IdInvalidException(">>> Book with id (" + id + ") is not exist! <<<");
    }

    Book book = bookService.findById(id);

    // 🚀 Sử dụng MapStruct để convert
    ResBook response = bookMapper.toResBook(book);
    return ResponseEntity.ok(response);
}
```

### **Update Book:**

```java
@PutMapping("/books")
@ApiMessage("Update book by ID")
public ResponseEntity<?> updateDua(@RequestBody BookDTO dto) throws IdInvalidException {
    if (!this.bookService.existsById(dto.getId())) {
        throw new IdInvalidException(">>> Id book (" + dto.getId() + ") is not exists! <<<");
    }

    // Get existing book
    Book existingBook = bookService.findById(dto.getId());

    // 🚀 Update existing book using MapStruct
    bookMapper.updateBookFromDTO(dto, existingBook);

    // Save and return
    Book updatedBook = bookRepository.save(existingBook);
    ResBook response = bookMapper.toResBook(updatedBook);
    return ResponseEntity.ok(response);
}
```

### **Get All Books:**

```java
@GetMapping("/books")
@ApiMessage("Fetch all books")
public ResponseEntity<List<ResBook>> fetchAll() {
    List<Book> books = bookService.findAll();

    // 🚀 Convert list using MapStruct
    List<ResBook> response = bookMapper.toResBookList(books);
    return ResponseEntity.ok(response);
}
```

---

## 📗 **2. CategoryController Example**

### **Inject CategoryMapper:**

```java
@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper; // ← Inject CategoryMapper

    public CategoryController(
        CategoryService categoryService,
        CategoryMapper categoryMapper
    ) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }
```

### **Create Category:**

```java
@PostMapping("/categories")
@ApiMessage("Create new category")
public ResponseEntity<ResCreateCategory> create(@Valid @RequestBody CategoryDTO dto) {
    // 🚀 Convert DTO to Entity
    Category category = categoryMapper.toCategory(dto);

    // Business logic
    Category savedCategory = categoryRepository.save(category);

    // 🚀 Convert Entity to Response DTO
    ResCreateCategory response = categoryMapper.toResCreateCategory(savedCategory);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

### **Get Category:**

```java
@GetMapping("/categories/{id}")
@ApiMessage("Get category by ID")
public ResponseEntity<ResCategory> getCategory(@PathVariable Long id) {
    Category category = categoryService.findById(id)
        .orElseThrow(() -> new IdInvalidException("Category not found"));

    // 🚀 Convert using MapStruct
    ResCategory response = categoryMapper.toResCategory(category);
    return ResponseEntity.ok(response);
}
```

### **Update Category:**

```java
@PutMapping("/categories/{id}")
@ApiMessage("Update category")
public ResponseEntity<ResCategory> update(
    @PathVariable Long id,
    @RequestBody CategoryDTO dto
) {
    Category existingCategory = categoryService.findById(id)
        .orElseThrow(() -> new IdInvalidException("Category not found"));

    // 🚀 Update using MapStruct
    categoryMapper.updateCategoryFromDTO(dto, existingCategory);

    Category updatedCategory = categoryRepository.save(existingCategory);
    ResCategory response = categoryMapper.toResCategory(updatedCategory);
    return ResponseEntity.ok(response);
}
```

### **Get All Categories:**

```java
@GetMapping("/categories")
@ApiMessage("Get all categories")
public ResponseEntity<List<ResCategory>> getAllCategories() {
    List<Category> categories = categoryService.findAll();

    // 🚀 Convert list using MapStruct
    List<ResCategory> response = categoryMapper.toResCategoryList(categories);
    return ResponseEntity.ok(response);
}
```

---

## 📙 **3. OrderController Example**

### **Inject OrderMapper:**

```java
@RestController
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper; // ← Inject OrderMapper

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }
```

### **Create Order:**

```java
@PostMapping("/orders")
@ApiMessage("Create new order")
public ResponseEntity<ResOrder> createOrder(@Valid @RequestBody OrderDTO dto) {
    // 🚀 Convert DTO to Entity
    Order order = orderMapper.toOrder(dto);

    // Business logic (add user, calculate total, etc.)
    User currentUser = getCurrentUser();
    order.setUser(currentUser);
    order.setTotalAmount(calculateTotal(order));
    order.setStatus(OrderStatusEnum.PENDING);

    Order savedOrder = orderRepository.save(order);

    // 🚀 Convert to Response DTO
    ResOrder response = orderMapper.toResOrder(savedOrder);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

### **Get Order:**

```java
@GetMapping("/orders/{id}")
@ApiMessage("Get order by ID")
public ResponseEntity<ResDataOrder> getOrder(@PathVariable Long id) {
    Order order = orderService.findById(id)
        .orElseThrow(() -> new IdInvalidException("Order not found"));

    // 🚀 Convert using MapStruct
    ResDataOrder response = orderMapper.toResDataOrder(order);
    return ResponseEntity.ok(response);
}
```

### **Get User Orders:**

```java
@GetMapping("/orders/user")
@ApiMessage("Get current user orders")
public ResponseEntity<List<ResOrder>> getUserOrders() {
    User currentUser = getCurrentUser();
    List<Order> orders = orderService.findByUser(currentUser);

    // 🚀 Convert list using MapStruct
    List<ResOrder> response = orderMapper.toResOrderList(orders);
    return ResponseEntity.ok(response);
}
```

### **Update Order Status:**

```java
@PutMapping("/orders/{id}/status")
@ApiMessage("Update order status")
public ResponseEntity<ResDataOrder> updateStatus(
    @PathVariable Long id,
    @RequestParam OrderStatusEnum status
) {
    Order order = orderService.findById(id)
        .orElseThrow(() -> new IdInvalidException("Order not found"));

    // Business logic
    order.setStatus(status);
    Order updatedOrder = orderRepository.save(order);

    // 🚀 Convert to Response DTO
    ResDataOrder response = orderMapper.toResDataOrder(updatedOrder);
    return ResponseEntity.ok(response);
}
```

---

## 📕 **4. UserController Example**

### **Register User:**

```java
@PostMapping("/auth/register")
@ApiMessage("Sign in account")
public ResponseEntity<ResCreateUser> register(
    @RequestBody @Valid CreateAccountDTO dto
) throws IdInvalidException {
    if (this.userService.existsByEmail(dto.getEmail())) {
        throw new IdInvalidException("Email already exists");
    }

    if (!dto.getPassword().equals(dto.getConfirmPassword())) {
        throw new IdInvalidException("Passwords do not match");
    }

    // 🚀 Convert DTO to Entity
    User user = userMapper.toUser(dto);

    // Hash password
    String hashPassword = passwordEncoder.encode(dto.getPassword());
    user.setPassword(hashPassword);

    // Save
    User savedUser = userRepository.save(user);

    // Return response (service layer handles this)
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(this.userService.createResponse(savedUser));
}
```

### **Update User:**

```java
@PutMapping("/users/{id}")
@ApiMessage("Update user")
public ResponseEntity<ResUpdateUser> updateUser(
    @PathVariable Long id,
    @RequestBody UpdateUserDTO dto
) {
    User existingUser = userService.findById(id)
        .orElseThrow(() -> new IdInvalidException("User not found"));

    // 🚀 Update using MapStruct
    userMapper.updateUserFromDTO(dto, existingUser);

    User updatedUser = userRepository.save(existingUser);

    // Convert to response (you may need to create this mapper method)
    return ResponseEntity.ok(createUpdateResponse(updatedUser));
}
```

---

## 🔄 **5. Service Layer Integration**

### **BookService Example:**

```java
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public ResCreateBook createBasic(BookDTO dto) {
        // 🚀 DTO → Entity
        Book book = bookMapper.toBook(dto);

        // Business logic
        book.setActive(true);
        book.setSold(0);

        // Save
        Book savedBook = bookRepository.save(book);

        // 🚀 Entity → Response DTO
        return bookMapper.toResCreateBook(savedBook);
    }

    public ResBook fetchBookById(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new IdInvalidException("Book not found"));

        // 🚀 Entity → Response DTO
        return bookMapper.toResBook(book);
    }

    public ResBook update(BookDTO dto) {
        Book existingBook = bookRepository.findById(dto.getId())
            .orElseThrow(() -> new IdInvalidException("Book not found"));

        // 🚀 Update entity
        bookMapper.updateBookFromDTO(dto, existingBook);

        // Save
        Book updatedBook = bookRepository.save(existingBook);

        // 🚀 Entity → Response DTO
        return bookMapper.toResBook(updatedBook);
    }

    public List<ResBook> fetchAll() {
        List<Book> books = bookRepository.findAll();

        // 🚀 List conversion
        return bookMapper.toResBookList(books);
    }
}
```

---

## 💡 **Best Practices**

### **1. Always inject mappers in constructor:**

```java
// ✅ Good - Constructor injection
public BookController(BookService bookService, BookMapper bookMapper) {
    this.bookService = bookService;
    this.bookMapper = bookMapper;
}

// ❌ Bad - Field injection
@Autowired
private BookMapper bookMapper;
```

### **2. Handle null cases:**

```java
// ✅ Good - Check for null
Book book = bookRepository.findById(id).orElse(null);
if (book != null) {
    return bookMapper.toResBook(book);
}
return null;

// Or use Optional
return bookRepository.findById(id)
    .map(bookMapper::toResBook)
    .orElse(null);
```

### **3. Use appropriate mapper methods:**

```java
// ✅ For create operations
Book book = bookMapper.toBook(dto);
bookRepository.save(book);

// ✅ For update operations
Book existingBook = bookRepository.findById(id).orElseThrow();
bookMapper.updateBookFromDTO(dto, existingBook);
bookRepository.save(existingBook);

// ✅ For read operations
ResBook response = bookMapper.toResBook(book);
```

### **4. Combine with business logic:**

```java
public ResCreateBook createBook(BookDTO dto) {
    // 🚀 Map DTO to Entity
    Book book = bookMapper.toBook(dto);

    // 💼 Business logic
    book.setActive(true);
    book.setSold(0);
    book.setCreatedBy(getCurrentUser());

    // Categories handling (service layer responsibility)
    if (dto.getCategories() != null) {
        List<Category> categories = categoryRepository
            .findByNameIn(dto.getCategories());
        book.setCategories(categories);
    }

    // 💾 Save
    Book savedBook = bookRepository.save(book);

    // 🚀 Map Entity to Response DTO
    return bookMapper.toResCreateBook(savedBook);
}
```

---

## 🎯 **Summary**

### **MapStruct Usage Pattern:**

1. **Create Operation:**

   ```java
   Entity entity = mapper.toEntity(dto);
   // ... business logic ...
   Entity saved = repository.save(entity);
   return mapper.toResponseDTO(saved);
   ```

2. **Read Operation:**

   ```java
   Entity entity = repository.findById(id);
   return mapper.toResponseDTO(entity);
   ```

3. **Update Operation:**

   ```java
   Entity existing = repository.findById(id);
   mapper.updateEntityFromDTO(dto, existing);
   Entity updated = repository.save(existing);
   return mapper.toResponseDTO(updated);
   ```

4. **List Operation:**
   ```java
   List<Entity> entities = repository.findAll();
   return mapper.toResponseDTOList(entities);
   ```

---

**🎉 Bạn đã sẵn sàng sử dụng MapStruct trong toàn bộ dự án!**
