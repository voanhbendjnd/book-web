package djnd.ben1607.drink_shop.domain.request;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private Instant publicationDate;
    private String isbn;
    private String description;
    private String language;
    private Integer numberOfPages;
    private Double price;
    private Integer stockQuantity;
    private List<String> categories;
}
