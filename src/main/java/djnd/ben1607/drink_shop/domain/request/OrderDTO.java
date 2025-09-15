package djnd.ben1607.drink_shop.domain.request;

import djnd.ben1607.drink_shop.utils.constant.OrderStatusEnum;
import djnd.ben1607.drink_shop.utils.constant.PaymentMethodEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDTO {
    String street;
    String city;
    String zipCode;
    PaymentMethodEnum paymentMethod;
}
