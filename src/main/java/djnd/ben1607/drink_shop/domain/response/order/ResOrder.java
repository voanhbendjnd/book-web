package djnd.ben1607.drink_shop.domain.response.order;

import djnd.ben1607.drink_shop.utils.constant.PaymentMethodEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResOrder {
    Long id;
    Double totalAmount;
    PaymentMethodEnum status;
}
