package djnd.ben1607.drink_shop.domain.response.order;

import java.time.Instant;
import java.util.List;

import djnd.ben1607.drink_shop.utils.constant.PaymentMethodEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderHistory {
    Long id;
    String name;
    PaymentMethodEnum type;
    String email;
    String phone;
    Long userId;
    double totalAmount;
    Instant createdAt;
    Instant updatedAt;
    List<Detail> details;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detail {
        private String bookName;
        private int quantity;
        private Long id;
    }
}
