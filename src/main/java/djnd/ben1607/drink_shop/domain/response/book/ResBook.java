package djnd.ben1607.drink_shop.domain.response.book;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResBook {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publicationDate;
    private String isbn;
    private String description;
    private String language;
    private Integer numberOfPages;
    private Double price;
    private Integer stockQuantity;
    private String coverImage;
    private String categories;
    private Instant createdAt, updatedAt;
    private String createdBy, updatedBy;
    private Double totalReviews;
    private Double ratingAverage;
}
