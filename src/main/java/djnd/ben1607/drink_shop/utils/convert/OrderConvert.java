package djnd.ben1607.drink_shop.utils.convert;

import djnd.ben1607.drink_shop.domain.entity.Order;
import djnd.ben1607.drink_shop.domain.response.order.ResDataOrder;

public class OrderConvert {
    public static ResDataOrder getOrder(Order order) {
        var res = new ResDataOrder();
        res.setAddress(order.getAddressShipping());
        res.setCreatedAt(order.getOrderCreateDate());
        res.setId(order.getId());
        res.setName(order.getName());
        res.setStatus(order.getStatus());
        res.setTotalAmount(order.getTotalAmount());
        return res;
    }
}
