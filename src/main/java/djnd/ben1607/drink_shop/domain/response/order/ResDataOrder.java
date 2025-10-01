package djnd.ben1607.drink_shop.domain.response.order;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResDataOrder {
    private long id;
    private String name;
    private String address;
    private double totalAmount;
    private Instant createdAt;
}
