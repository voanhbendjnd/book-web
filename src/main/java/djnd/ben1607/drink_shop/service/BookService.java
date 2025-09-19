package djnd.ben1607.drink_shop.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import djnd.ben1607.drink_shop.repository.BookRepository;
import djnd.ben1607.drink_shop.utils.ChangeUpdate;
import djnd.ben1607.drink_shop.utils.convert.ConvertModuleBook;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final FileService fileService;

    public BookService(
            BookRepository bookRepository,
            CategoryService categoryService,
            FileService fileService) {
        this.bookRepository = bookRepository;
        this.categoryService = categoryService;
        this.fileService = fileService;
    }

    public boolean existsById(Long id) {
        return this.bookRepository.existsById(id);
    }

    public ResCreateBook createBasic(BookDTO dto) {
        Book book = new Book();
        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            List<Category> categories = this.categoryService.findByIdIn(dto.getCategories());
            if (categories != null && !categories.isEmpty()) {
                book.setCategories(categories);
            }
        }
        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getTitle());
        book.setDescription(dto.getDescription());
        book.setIsbn(dto.getIsbn());
        book.setLanguage(dto.getLanguage());
        book.setNumberOfPages(dto.getNumberOfPages());
        book.setPrice(dto.getPrice());
        book.setPublicationDate(dto.getPublicationDate());
        book.setPublisher(dto.getPublisher());
        book.setStockQuantity(dto.getStockQuantity());
        return ConvertModuleBook.create(this.bookRepository.save(book));
    }

    public ResCreateBook createMultipart(BookDTO dto, List<MultipartFile> files)
            throws URISyntaxException, IOException {
        Book book = new Book();
        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            List<Category> categories = this.categoryService.findByIdIn(dto.getCategories());
            if (categories != null && !categories.isEmpty()) {
                book.setCategories(categories);
            }
        }
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
        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getTitle());
        book.setDescription(dto.getDescription());
        book.setIsbn(dto.getIsbn());
        book.setLanguage(dto.getLanguage());
        book.setNumberOfPages(dto.getNumberOfPages());
        book.setPrice(dto.getPrice());
        book.setPublicationDate(dto.getPublicationDate());
        book.setPublisher(dto.getPublisher());
        book.setStockQuantity(dto.getStockQuantity());
        return ConvertModuleBook.create(this.bookRepository.save(book));
    }

    public ResUpdateBook updateMutilpart(BookDTO dto, List<MultipartFile> files)
            throws URISyntaxException, IOException {
        Book bookDB = this.bookRepository.findById(dto.getId()).get();
        if (bookDB != null) {
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
            bookDB.setAuthor(dto.getAuthor());
            bookDB.setTitle(dto.getTitle());
            bookDB.setDescription(dto.getDescription());
            bookDB.setIsbn(dto.getIsbn());
            bookDB.setLanguage(dto.getLanguage());
            bookDB.setNumberOfPages(dto.getNumberOfPages());
            bookDB.setPrice(dto.getPrice());
            bookDB.setPublicationDate(dto.getPublicationDate());
            bookDB.setPublisher(dto.getPublisher());
            bookDB.setStockQuantity(dto.getStockQuantity());
            if (dto.getCategories() != null) {
                List<Category> categories = this.categoryService.findByIdIn(dto.getCategories());
                if (categories != null && !categories.isEmpty()) {
                    bookDB.setCategories(categories);
                }
            }
            Book lastBook = this.bookRepository.save(bookDB);
            return ConvertModuleBook.update(lastBook);

        }
        return null;
    }

    public ResCreateBook create(BookDTO dto, MultipartFile file) throws URISyntaxException, IOException {
        Book book = new Book();
        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            List<Category> categories = this.categoryService.findByIdIn(dto.getCategories());
            if (categories != null && !categories.isEmpty()) {
                book.setCategories(categories);
            }
        }
        if (file != null) {
            book.setCoverImage(this.fileService.storeProductCreate(file));
        }
        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getTitle());
        book.setDescription(dto.getDescription());
        book.setIsbn(dto.getIsbn());
        book.setLanguage(dto.getLanguage());
        book.setNumberOfPages(dto.getNumberOfPages());
        book.setPrice(dto.getPrice());
        book.setPublicationDate(dto.getPublicationDate());
        book.setPublisher(dto.getPublisher());
        book.setStockQuantity(dto.getStockQuantity());
        return ConvertModuleBook.create(this.bookRepository.save(book));
    }

    public ResUpdateBook update(BookDTO dto) throws URISyntaxException, IOException {
        Book bookDB = this.bookRepository.findById(dto.getId()).get();
        Book book = new Book();
        if (bookDB != null) {
            if (dto.getCoverImage() != null) {
                // book.setCoverImage(this.fileService.storeProductCreate(dto.getCoverImage()));
            }
            book.setAuthor(dto.getAuthor());
            book.setTitle(dto.getTitle());
            book.setDescription(dto.getDescription());
            book.setIsbn(dto.getIsbn());
            book.setLanguage(dto.getLanguage());
            book.setNumberOfPages(dto.getNumberOfPages());
            book.setPrice(dto.getPrice());
            book.setPublicationDate(dto.getPublicationDate());
            book.setPublisher(dto.getPublisher());
            book.setStockQuantity(dto.getStockQuantity());
            if (dto.getCategories() != null) {
                List<Category> categories = this.categoryService.findByIdIn(dto.getCategories());
                if (categories != null && !categories.isEmpty()) {
                    book.setCategories(categories);
                }
            }

            ChangeUpdate.handle(book, bookDB);
            Book lastBook = this.bookRepository.save(bookDB);
            return ConvertModuleBook.update(lastBook);

        }
        return null;
    }

    public ResBook fetchBookById(Long id) {
        Book book = this.bookRepository.findById(id).get();
        return ConvertModuleBook.fetch(book);
    }

    public void deleteBookById(Long id) {
        this.bookRepository.delete(this.bookRepository.findById(id).get());
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
        res.setResult(page.getContent().stream().map(ConvertModuleBook::fetch).collect(Collectors.toList()));
        return res;
    }

}
