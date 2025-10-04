package djnd.ben1607.drink_shop.controller.client;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import djnd.ben1607.drink_shop.domain.entity.Order;
import djnd.ben1607.drink_shop.domain.request.OrderDTO;
import djnd.ben1607.drink_shop.domain.request.RequestOrder;
import djnd.ben1607.drink_shop.domain.response.ResultPaginationDTO;
import djnd.ben1607.drink_shop.domain.response.order.ResOrder;
import djnd.ben1607.drink_shop.service.OrderService;
import djnd.ben1607.drink_shop.utils.annotation.ApiMessage;
import djnd.ben1607.drink_shop.utils.constant.OrderStatusEnum;
import djnd.ben1607.drink_shop.utils.error.IdInvalidException;
import djnd.ben1607.drink_shop.utils.error.EillegalStateException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {
    OrderService orderService;

    @PostMapping("/orders")
    @ApiMessage("Order book")
    public ResponseEntity<?> order(@RequestBody RequestOrder request) throws EillegalStateException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.orderProduct(request));
    }

    @PostMapping("/checkout")
    @ApiMessage("Check out")
    public ResponseEntity<?> checkout(@RequestBody OrderDTO dto) {
        this.orderService.checkout(dto);
        return ResponseEntity.ok("Check out success");
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
    public ResponseEntity<?> history(
            // 1. Thêm tham số lọc Enum từ URL
            @RequestParam(value = "status", required = false) OrderStatusEnum status,

            // 2. Giữ nguyên Specification và Pageable
            @Filter Specification<Order> spec,
            Pageable pageable) throws EillegalStateException {
        // --------------------------------------------------------
        // BƯỚC 1: Xây dựng Specification cho tham số 'status'
        // --------------------------------------------------------
        Specification<Order> statusFilter = (root, query, criteriaBuilder) -> {
            if (status == null) {
                // Nếu không có tham số status, không lọc gì cả (Trả về TRUE)
                return criteriaBuilder.conjunction();
            }
            // Nếu có status, tạo mệnh đề WHERE status = PENDING (hoặc giá trị tương ứng)
            return criteriaBuilder.equal(root.get("status"), status);
        };

        // --------------------------------------------------------
        // BƯỚC 2: Kết hợp Specification mới với Specification ban đầu
        // --------------------------------------------------------
        // Sử dụng .and() để kết hợp điều kiện lọc mới với các điều kiện đã có trong
        // 'spec'
        Specification<Order> combinedSpec = spec.and(statusFilter);

        // --------------------------------------------------------
        // BƯỚC 3: Gọi Service với Specification đã kết hợp
        // --------------------------------------------------------
        return ResponseEntity.ok(this.orderService.watchHistory(combinedSpec, pageable));
    }

    // @GetMapping("/orders/history/filter")
    // @ApiMessage("Watching history with status filter")
    // public ResponseEntity<?> historyWithFilter(@RequestParam(value = "status",
    // required = false) String status,
    // Pageable pageable)
    // throws EillegalStateException {
    // return ResponseEntity.ok(this.orderService.watchHistory(status, pageable));
    // }
}
