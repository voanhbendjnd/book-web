package djnd.ben1607.drink_shop.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import djnd.ben1607.drink_shop.domain.entity.Book;
import djnd.ben1607.drink_shop.domain.entity.BookImage;
import djnd.ben1607.drink_shop.domain.entity.Category;
import djnd.ben1607.drink_shop.domain.request.BookDTO;
import djnd.ben1607.drink_shop.domain.response.ResultPaginationDTO;
import djnd.ben1607.drink_shop.domain.response.book.ResBook;
import djnd.ben1607.drink_shop.domain.response.book.ResCreateBook;
import djnd.ben1607.drink_shop.domain.response.book.ResUpdateBook;
import djnd.ben1607.drink_shop.mapper.BookMapper;
import djnd.ben1607.drink_shop.repository.BookRepository;
import djnd.ben1607.drink_shop.repository.CategoryRepository;
import djnd.ben1607.drink_shop.utils.error.EillegalStateException;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final FileService fileService;
    private final CategoryRepository categRepository;
    private final BookMapper bookMapper; // 🚀 Inject BookMapper

    public BookService(
            BookRepository bookRepository,
            CategoryService categoryService,
            FileService fileService,
            CategoryRepository categoryRepository,
            BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.fileService = fileService;
        this.categRepository = categoryRepository;
        this.bookMapper = bookMapper; // 🚀 Initialize BookMapper
    }

    @Cacheable(value = "books", key = "#id")
    public Book findById(Long id) {
        return this.bookRepository.findById(id).orElse(null);
    }

    public boolean existsById(Long id) {
        return this.bookRepository.existsById(id);
    }

    public ResCreateBook createBasic(BookDTO dto) {
        // 🚀 Sử dụng MapStruct thay vì manual mapping
        Book book = bookMapper.toBook(dto);

        // Business logic: Set categories
        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            List<Category> categories = this.categRepository.findByNameIn(dto.getCategories());
            if (categories != null && !categories.isEmpty()) {
                book.setCategories(categories);
            }
        }

        // Business logic: Set default values
        book.setActive(true);
        book.setSold(0);

        // Save book
        Book savedBook = this.bookRepository.save(book);

        // 🚀 Sử dụng MapStruct thay vì ConvertModuleBook.create()
        // Before: return ConvertModuleBook.create(this.bookRepository.save(book));
        // After:
        return bookMapper.toResCreateBook(savedBook);
    }

    public ResCreateBook createMultipart(BookDTO dto, List<MultipartFile> files, MultipartFile file)
            throws URISyntaxException, IOException {
        // 🚀 Sử dụng MapStruct thay vì manual mapping
        Book book = bookMapper.toBook(dto);

        // Business logic: Set categories
        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            List<Category> categories = this.categRepository.findByNameIn(dto.getCategories());
            if (categories != null && !categories.isEmpty()) {
                book.setCategories(categories);
            }
        }

        // Business logic: Handle cover image
        if (file != null && !file.isEmpty()) {
            book.setCoverImage(this.fileService.storeProductCreate(file));
        }

        // Business logic: Handle multiple images
        if (files != null && !files.isEmpty()) {
            var imgs = new ArrayList<BookImage>();
            for (MultipartFile x : files) {
                var img = new BookImage();
                img.setImgUrl(this.fileService.storeProductCreate(x));
                img.setBook(book);
                imgs.add(img);
            }
            book.setBookImages(imgs);
        }

        // Business logic: Set default values
        book.setActive(true);
        book.setSold(0);

        // Save book
        Book savedBook = this.bookRepository.save(book);

        // 🚀 Sử dụng MapStruct thay vì ConvertModuleBook.create()
        // Before: return ConvertModuleBook.create(this.bookRepository.save(book));
        // After:
        return bookMapper.toResCreateBook(savedBook);
    }

    public ResUpdateBook updateMutilpart(BookDTO dto, List<MultipartFile> files, MultipartFile file)
            throws URISyntaxException, IOException {
        // Business logic: Set all books to active (existing logic)
        var listBook = this.bookRepository.findAll();
        for (var x : listBook) {
            x.setActive(true);
        }
        this.bookRepository.saveAll(listBook);

        // Find existing book
        Book bookDB = this.bookRepository.findById(dto.getId()).get();
        if (bookDB != null) {
            // 🚀 Sử dụng MapStruct để update book thay vì manual mapping
            // Before: Manual set từng field (15+ lines)
            // After: 1 line với MapStruct
            bookMapper.updateBookFromDTO(dto, bookDB);

            // Business logic: Handle cover image
            if (file != null && !file.isEmpty()) {
                bookDB.setCoverImage(this.fileService.storeProductCreate(file));
            }

            // Business logic: Handle multiple images
            if (files != null && !files.isEmpty()) {
                bookDB.setBookImages(files.stream().map(x -> {
                    var img = new BookImage();
                    img.setBook(bookDB);
                    try {
                        img.setImgUrl(this.fileService.storeProductCreate(x));
                    } catch (URISyntaxException | IOException e) {
                        throw new RuntimeException(e);
                    }
                    return img;
                }).collect(Collectors.toList()));
            }

            // Business logic: Set categories
            if (dto.getCategories() != null) {
                List<Category> categories = this.categRepository.findByNameIn(dto.getCategories());
                if (categories != null && !categories.isEmpty()) {
                    bookDB.setCategories(categories);
                }
            }

            // Save updated book
            Book lastBook = this.bookRepository.save(bookDB);

            // 🚀 Sử dụng MapStruct thay vì ConvertModuleBook.update()
            // Before: return ConvertModuleBook.update(lastBook);
            // After:
            return bookMapper.toResUpdateBook(lastBook);
        }
        return null;
    }

    public ResCreateBook create(BookDTO dto, MultipartFile file) throws URISyntaxException, IOException {
        // 🚀 Sử dụng MapStruct thay vì manual mapping
        Book book = bookMapper.toBook(dto);

        // Business logic: Set categories
        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            List<Category> categories = this.categRepository.findByNameIn(dto.getCategories());
            if (categories != null && !categories.isEmpty()) {
                book.setCategories(categories);
            }
        }

        // Business logic: Handle cover image
        if (file != null) {
            book.setCoverImage(this.fileService.storeProductCreate(file));
        }

        // Business logic: Set default values
        book.setActive(true);
        book.setSold(0);

        // Save book
        Book savedBook = this.bookRepository.save(book);

        // 🚀 Sử dụng MapStruct thay vì ConvertModuleBook.create()
        // Before: return ConvertModuleBook.create(this.bookRepository.save(book));
        // After:
        return bookMapper.toResCreateBook(savedBook);
    }

    public ResUpdateBook update(BookDTO dto) throws URISyntaxException, IOException {
        Book bookDB = this.bookRepository.findById(dto.getId()).get();
        if (bookDB != null) {
            // 🚀 Sử dụng MapStruct để update book thay vì manual mapping
            // Before: Manual set từng field (10+ lines)
            // After: 1 line với MapStruct
            bookMapper.updateBookFromDTO(dto, bookDB);

            // Business logic: Set categories
            if (dto.getCategories() != null) {
                List<Category> categories = this.categRepository.findByNameIn(dto.getCategories());
                if (categories != null && !categories.isEmpty()) {
                    bookDB.setCategories(categories);
                }
            }

            // Save updated book
            Book lastBook = this.bookRepository.save(bookDB);

            // 🚀 Sử dụng MapStruct thay vì ConvertModuleBook.update()
            // Before: return ConvertModuleBook.update(lastBook);
            // After:
            return bookMapper.toResUpdateBook(lastBook);
        }
        return null;
    }

    public ResBook fetchBookById(Long id) {
        Book book = this.bookRepository.findById(id).get();

        // 🚀 Sử dụng MapStruct cho basic mapping
        ResBook resBook = bookMapper.toResBook(book);

        // 🚀 Custom logic trong Service layer
        setCustomFields(book, resBook);

        return resBook;
    }

    public void deleteBookById(Long id) throws EillegalStateException {
        var book = this.bookRepository.findById(id).orElseThrow(() -> new EillegalStateException("Book not found"));
        book.setActive(false);
        this.bookRepository.save(book);
    }

    public ResultPaginationDTO fetchAllWithSpecAndFilter(Specification<Book> spec, Pageable pageable) {
        Page<Book> page = this.bookRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        res.setMeta(mt);

        // 🚀 Sử dụng MapStruct cho basic mapping + Custom logic
        List<ResBook> resBooks = page.getContent().stream()
                .map(book -> {
                    // MapStruct cho basic mapping
                    ResBook resBook = bookMapper.toResBook(book);

                    // Custom logic cho totalReviews và ratingAverage
                    setCustomFields(book, resBook);

                    return resBook;
                })
                .collect(Collectors.toList());

        res.setResult(resBooks);

        return res;
    }

    // 🚀 Helper methods để tái sử dụng custom logic
    private void setCustomFields(Book book, ResBook resBook) {
        // Handle totalReviews
        if (book.getReviews() != null) {
            long totalReviews = book.getReviews().stream()
                    .filter(review -> review != null)
                    .count();
            resBook.setTotalReviews((double) totalReviews);
        } else {
            resBook.setTotalReviews(0.0);
        }

        // Handle ratingAverage
        if (book.getReviews() != null && !book.getReviews().isEmpty()) {
            double ratingAverage = book.getReviews().stream()
                    .filter(review -> review != null && review.getRating() != null)
                    .mapToDouble(review -> review.getRating())
                    .average()
                    .orElse(0.0);
            resBook.setRatingAverage(ratingAverage);
        } else {
            resBook.setRatingAverage(0.0);
        }
    }

}
