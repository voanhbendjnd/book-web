package djnd.ben1607.drink_shop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import djnd.ben1607.drink_shop.domain.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    public List<Review> findByBookId(Long bookId);

    @Query("SELECT r from Review r WHERE r.book.id = :bookId")
    Page<Review> findReviewsByBookId(Long bookId, Pageable pageable);
}
