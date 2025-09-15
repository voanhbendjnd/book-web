package djnd.ben1607.drink_shop.domain.response.review;

import java.time.Instant;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResReview {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDate reviewDate;
    private String username;
    private String titleBook;
    private String createdBy, updatedBy;
    private Instant createdAt, updatedAt;
}
