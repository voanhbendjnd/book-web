package djnd.ben1607.drink_shop.controller.client;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import djnd.ben1607.drink_shop.domain.entity.Review;
import djnd.ben1607.drink_shop.domain.request.ReviewDTO;
import djnd.ben1607.drink_shop.domain.response.ResultPaginationDTO;
import djnd.ben1607.drink_shop.domain.response.review.ResCreateReview;
import djnd.ben1607.drink_shop.domain.response.review.ResReview;
import djnd.ben1607.drink_shop.domain.response.review.ResUpdateReview;
import djnd.ben1607.drink_shop.service.ReviewService;
import djnd.ben1607.drink_shop.utils.annotation.ApiMessage;
import djnd.ben1607.drink_shop.utils.error.IdInvalidException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    @ApiMessage("Create new review")
    public ResponseEntity<ResCreateReview> create(@Valid @RequestBody ReviewDTO dto) throws IdInvalidException {
        if (dto.getBookId() == null) {
            throw new IdInvalidException(">>> Id book cannot be empty or null! <<<");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.reviewService.create(dto));
    }

    @PutMapping("/reviews")
    @ApiMessage("Update review")
    public ResponseEntity<ResUpdateReview> update(@Valid @RequestBody ReviewDTO dto) throws IdInvalidException {
        return ResponseEntity.ok(this.reviewService.update(dto));
    }

    @GetMapping("/reviews/{id}")
    @ApiMessage("Fetch review by ID")
    public ResponseEntity<ResReview> fetchById(@PathVariable("id") Long id) throws IdInvalidException {
        return ResponseEntity.ok(this.reviewService.fetchById(id));
    }

    @DeleteMapping("/reviews/{id}")
    @ApiMessage("Delete review by ID")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) throws IdInvalidException {
        this.reviewService.deleteById(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/reviews")
    @ApiMessage("Fetch all review with filter")
    public ResponseEntity<ResultPaginationDTO> fetchAll(@Filter Specification<Review> spec, Pageable pageable) {
        return ResponseEntity.ok(this.reviewService.fetchAll(spec, pageable));
    }

    @GetMapping("/reviews/book/{id}")
    @ApiMessage("Fetch all review of book with id")
    public ResponseEntity<List<ResReview>> fetchAllReviewOfBook(@PathVariable("id") Long id) throws IdInvalidException {
        if (id == null) {
            throw new IdInvalidException(">>> Id cannot be empty or null! <<<");
        }
        return ResponseEntity.ok(this.reviewService.fetchAllReviewOfBook(id));

    }
}
