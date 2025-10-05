package djnd.ben1607.drink_shop.domain.response.order;

import java.time.Instant;

import djnd.ben1607.drink_shop.utils.constant.OrderStatusEnum;
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
    private OrderStatusEnum status;
    private Instant createdAt;
}
