package djnd.ben1607.drink_shop.domain.response.order;

import djnd.ben1607.drink_shop.utils.constant.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResPayment {
    private Long id;
    private Long orderId;
    private double amount;
    private String currency;
    private OrderStatusEnum status;
}
