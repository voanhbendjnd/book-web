package djnd.ben1607.drink_shop.controller.client;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import djnd.ben1607.drink_shop.domain.entity.Order;
import djnd.ben1607.drink_shop.domain.request.OrderDTO;
import djnd.ben1607.drink_shop.domain.request.RequestOrder;
import djnd.ben1607.drink_shop.domain.response.ResultPaginationDTO;
import djnd.ben1607.drink_shop.domain.response.order.ResOrder;
import djnd.ben1607.drink_shop.service.OrderService;
import djnd.ben1607.drink_shop.utils.annotation.ApiMessage;
import djnd.ben1607.drink_shop.utils.error.IdInvalidException;
import djnd.ben1607.drink_shop.utils.error.EillegalStateException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderConcoller {
    OrderService orderService;

    @PostMapping("/orders")
    @ApiMessage("Order book")
    public ResponseEntity<?> order(@RequestBody RequestOrder request) throws EillegalStateException {
        this.orderService.orderProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Đặt hàng thành công");
    }

    @PostMapping("/checkout")
    @ApiMessage("Check out")
    public ResponseEntity<Void> checkout(@RequestBody OrderDTO dto) {
        this.orderService.checkout(dto);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/orders")
    @ApiMessage("Get all order")
    public ResponseEntity<ResultPaginationDTO> getOrder(@Filter Specification<Order> spec, Pageable pageable) {
        return ResponseEntity.ok(this.orderService.getAllOrder(spec, pageable));
    }

    @GetMapping("/orders/{id}")
    @ApiMessage("Get order by Id")
    public ResponseEntity<ResOrder> getOrderById(@PathVariable("id") Long id)
            throws IdInvalidException, EillegalStateException {
        Long lastID = (id != null) ? id : 0;
        if (lastID != 0) {
            return ResponseEntity.ok(this.orderService.getOrderById(lastID));
        }
        throw new IdInvalidException("Id cannot be empty and not null");
    }

    @GetMapping("/orders/history")
    @ApiMessage("Watching history")
    public ResponseEntity<?> history() throws EillegalStateException {
        return ResponseEntity.ok(this.orderService.watchHistory());
    }
}
