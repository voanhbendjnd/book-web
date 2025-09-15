package djnd.ben1607.drink_shop.domain.response.book;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResCreateBook {
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
    private List<String> categories;
    private Instant createdAt;
    private String createdBy;

}
