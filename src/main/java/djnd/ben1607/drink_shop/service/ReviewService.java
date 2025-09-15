package djnd.ben1607.drink_shop.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import djnd.ben1607.drink_shop.domain.entity.Book;
import djnd.ben1607.drink_shop.domain.entity.Review;
import djnd.ben1607.drink_shop.domain.request.ReviewDTO;
import djnd.ben1607.drink_shop.domain.response.ResultPaginationDTO;
import djnd.ben1607.drink_shop.domain.response.review.ResCreateReview;
import djnd.ben1607.drink_shop.domain.response.review.ResReview;
import djnd.ben1607.drink_shop.domain.response.review.ResUpdateReview;
import djnd.ben1607.drink_shop.repository.BookRepository;
import djnd.ben1607.drink_shop.repository.ReviewRepository;
import djnd.ben1607.drink_shop.repository.UserRepository;
import djnd.ben1607.drink_shop.utils.ChangeUpdate;
import djnd.ben1607.drink_shop.utils.SecurityUtils;
import djnd.ben1607.drink_shop.utils.convert.ConvertModuleReview;
import djnd.ben1607.drink_shop.utils.error.IdInvalidException;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository,
            UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public ResCreateReview create(ReviewDTO dto) throws IdInvalidException {
        Book book = this.bookRepository.findById(dto.getBookId()).get();
        if (book == null) {
            throw new IdInvalidException(">>> ID book with: (" + dto.getBookId() + ") is not exist! <<<");

        }
        Review review = new Review();
        review.setComment(dto.getComment());
        review.setBook(book);
        review.setUser(this.userRepository.findByEmail(SecurityUtils.getCurrentUserLogin().get()));
        review.setRating(dto.getRating());
        review.setReviewDate(Instant.now().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        return ConvertModuleReview.create(this.reviewRepository.save(review));
    }

    public ResUpdateReview update(ReviewDTO dto) throws IdInvalidException {
        Review reviewDB = this.reviewRepository.findById(dto.getId()).get();
        Review review = new Review();
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());
        ChangeUpdate.handle(review, reviewDB);
        return ConvertModuleReview.update(this.reviewRepository.save(reviewDB));
    }

    public ResReview fetchById(Long id) throws IdInvalidException {
        return ConvertModuleReview.fetch(this.reviewRepository.findById(id).get());
    }

    public void deleteById(Long id) throws IdInvalidException {
        this.reviewRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAll(Specification<Review> spec, Pageable pageable) {
        Page<Review> page = this.reviewRepository.findAll(pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        res.setMeta(mt);
        res.setResult(page.getContent().stream().map(ConvertModuleReview::fetch).collect(Collectors.toList()));
        return res;
    }

    // public List<ResReview> fetchAllReviewOfBook(Long id) throws
    // IdInvalidException {
    // Optional<Book> bookOptional = this.bookRepository.findById(id);
    // if (!bookOptional.isPresent()) {
    // throw new IdInvalidException(">>> Book with ID (" + id + ") is not exist!
    // <<<");
    // }
    // return
    // bookOptional.get().getReviews().stream().map(ConvertModuleReview::fetch).collect(Collectors.toList());
    // }

    public List<ResReview> fetchAllReviewOfBook(Long id) throws IdInvalidException {
        return this.reviewRepository.findByBookId(id).stream()
                .map(ConvertModuleReview::fetch)
                .collect(Collectors.toList());
    }

    public Page<ResReview> fetchAllReviewOfBook(Long id, Pageable pageable) throws IdInvalidException {
        Page<Review> reviewPage = this.reviewRepository.findReviewsByBookId(id, pageable);
        return reviewPage.map(ConvertModuleReview::fetch);
    }
}