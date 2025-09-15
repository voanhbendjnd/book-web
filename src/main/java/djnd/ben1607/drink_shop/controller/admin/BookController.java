package djnd.ben1607.drink_shop.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.turkraft.springfilter.boot.Filter;

import djnd.ben1607.drink_shop.domain.entity.Book;
import djnd.ben1607.drink_shop.domain.request.BookDTO;
import djnd.ben1607.drink_shop.domain.request.FileUploadRequest;
import djnd.ben1607.drink_shop.domain.response.ResultPaginationDTO;
import djnd.ben1607.drink_shop.domain.response.book.ResBook;
import djnd.ben1607.drink_shop.domain.response.book.ResCreateBook;
import djnd.ben1607.drink_shop.domain.response.book.ResUpdateBook;
import djnd.ben1607.drink_shop.service.BookService;
import djnd.ben1607.drink_shop.utils.annotation.ApiMessage;
import djnd.ben1607.drink_shop.utils.error.IdInvalidException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/books2")
    @ApiMessage("Create new book")
    public ResponseEntity<ResCreateBook> create(@RequestPart("coverImage") MultipartFile coverImage,
            @ModelAttribute BookDTO dto)
            throws IdInvalidException, URISyntaxException, IOException {
        if (coverImage != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(this.bookService.create(dto, coverImage));

        }
        throw new IOException("File invalid!");
    }

    @PostMapping("/books")
    @ApiMessage("Create new book basic")
    public ResponseEntity<ResCreateBook> createBasic(@RequestBody BookDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.bookService.createBasic(dto));
    }

    @PutMapping("/books")
    @ApiMessage("Update book by ID")
    public ResponseEntity<ResUpdateBook> update(@ModelAttribute BookDTO dto)
            throws IdInvalidException, URISyntaxException, IOException {

        if (!this.bookService.existsById(dto.getId())) {
            throw new IdInvalidException(">>> Id book (" + dto.getId() + ") is not exists! <<<");
        }
        if (dto.getCoverImage() != null) {
            List<String> notFile = Arrays.asList("jpg", "jpeg", "png");
            boolean checkFile = notFile.stream()
                    .anyMatch(x -> dto.getCoverImage().getOriginalFilename().toLowerCase().endsWith(x));
            if (!checkFile) {
                throw new IOException("File invalid");
            }
        }

        return ResponseEntity.ok(this.bookService.update(dto));
    }

    @GetMapping("/books/{id}")
    @ApiMessage("Fetch book by ID")
    public ResponseEntity<ResBook> fetch(@PathVariable("id") Long id) throws IdInvalidException {
        if (this.bookService.existsById(id)) {
            return ResponseEntity.ok(this.bookService.fetchBookById(id));
        }
        throw new IdInvalidException(">>> Book with id (" + id + ") is not exist! <<<");
    }

    @DeleteMapping("/books/{id}")
    @ApiMessage("Delete book by ID")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws IdInvalidException {
        if (this.bookService.existsById(id)) {
            this.bookService.deleteBookById(id);
            return ResponseEntity.ok(null);
        }
        throw new IdInvalidException(">>> ID book (" + id + ") is not exists! <<<");
    }

    @GetMapping("/books")
    @ApiMessage("Fetch all book")
    public ResponseEntity<ResultPaginationDTO> fetchAll(@Filter Specification<Book> spec, Pageable pageable) {
        return ResponseEntity.ok(this.bookService.fetchAllWithSpecAndFilter(spec, pageable));
    }

}
