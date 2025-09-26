package djnd.ben1607.drink_shop.repository;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import djnd.ben1607.drink_shop.domain.entity.Book;
import djnd.ben1607.drink_shop.utils.error.EillegalStateException;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    public List<Book> findByIdIn(List<Long> ids);

    // Phương thức mặc định (default method) để tìm Book hoặc ném ngoại lệ
    default Book findByIdOrThrow(Long id) throws EillegalStateException {
        // Đây là cách chuyên nghiệp để ném exception trong findById
        Supplier<EillegalStateException> supplier = () -> new EillegalStateException("Book not found with ID: " + id);

        return findById(id).orElseThrow(supplier);
    }
}
