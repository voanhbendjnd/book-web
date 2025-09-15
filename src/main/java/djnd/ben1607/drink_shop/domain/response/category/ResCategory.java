package djnd.ben1607.drink_shop.domain.response.category;

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
public class ResCategory {
    private Long id;
    private String name;
    private String description;
    private List<Book> listOfBook;
    private Integer totalBook;
    private String createdBy, updatedBy;
    private Instant updatedAt, createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Book {
        private Long id;
        private String title;
    }
}
