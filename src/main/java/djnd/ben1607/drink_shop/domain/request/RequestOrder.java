package djnd.ben1607.drink_shop.domain.request;

import java.util.List;

import djnd.ben1607.drink_shop.utils.constant.PaymentMethodEnum;

public record RequestOrder(String name, String address, String phone, double totalAmount, PaymentMethodEnum type,
        List<Detail> details) {
    public record Detail(Long bookId, int quantity, String bookName) {
    }
}
