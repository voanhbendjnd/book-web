package djnd.ben1607.drink_shop.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountDashBoard {
    private long userCount;
    private long bookCount;
    private long orderCount;
}
