package djnd.ben1607.drink_shop.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import djnd.ben1607.drink_shop.domain.entity.Address;
import djnd.ben1607.drink_shop.domain.entity.Book;
import djnd.ben1607.drink_shop.domain.entity.Cart;
import djnd.ben1607.drink_shop.domain.entity.CartItem;
import djnd.ben1607.drink_shop.domain.entity.Order;
import djnd.ben1607.drink_shop.domain.entity.OrderItem;
import djnd.ben1607.drink_shop.domain.entity.User;
import djnd.ben1607.drink_shop.domain.request.OrderDTO;
import djnd.ben1607.drink_shop.domain.response.ResultPaginationDTO;
import djnd.ben1607.drink_shop.domain.response.order.ResOrder;
import djnd.ben1607.drink_shop.repository.CartItemRepository;
import djnd.ben1607.drink_shop.repository.CartRepository;
import djnd.ben1607.drink_shop.repository.OrderRepository;
import djnd.ben1607.drink_shop.utils.SecurityUtils;
import djnd.ben1607.drink_shop.utils.constant.OrderStatusEnum;
import djnd.ben1607.drink_shop.utils.error.EillegalStateException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderService {
    OrderRepository orderRepository;
    UserService userService;
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;

    // checkout
    // không cần lưu item vì khi lưu order thì các item sẽ tự động lưu không cần
    // query
    @Transactional
    public void checkout(OrderDTO dto) throws IllegalStateException {
        User user = this.userService.fetchUserByEmail(
                SecurityUtils.getCurrentUserLogin()
                        .orElseThrow(() -> new IllegalStateException("User not logged in.")));
        Cart cart = this.cartRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalStateException("Cart not found for user."));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty.");
        }
        ;
        for (CartItem x : cart.getItems()) {
            Book book = x.getBook();
            int currentQty = book.getStockQuantity() - x.getQuantity();
            if (currentQty < 0) {
                throw new IllegalStateException("Không đủ tồn kho");
            }
            book.setStockQuantity(currentQty);
            Integer sold = book.getSold();
            int currentSold = (sold != null) ? sold : 0;
            book.setSold(currentSold + x.getQuantity());
        }
        Order order = new Order();
        order.setAddress(new Address(dto.getStreet(), dto.getCity(), dto.getZipCode()));
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setStatus(OrderStatusEnum.PROCESSING);
        order.setUser(user);
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getPrice());
                    orderItem.setOrder(order); // Liên kết OrderItem với Order mới
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setTotalAmount(orderItems.stream().mapToDouble(x -> x.getPrice() * x.getQuantity()).sum());
        order.setOrderItems(orderItems);
        this.orderRepository.save(order);
        this.cartItemRepository.deleteAllByCartId(user.getId());

    }

    // get item in cart
    public List<OrderItem> items() {
        User user = this.userService.fetchUserByEmail(SecurityUtils.getCurrentUserLogin().get());
        Cart cart = this.cartRepository.findById(user.getId()).get();
        List<OrderItem> item = cart.getItems().stream()
                .map(x -> {
                    OrderItem it = new OrderItem();
                    it.setBook(x.getBook());
                    it.setQuantity(x.getQuantity());
                    it.setPrice(x.getPrice());
                    return it;
                })
                .collect(Collectors.toList());

        return item;
    }

    public ResultPaginationDTO getAllOrder(Specification<Order> spec, Pageable pageable) {
        Page<Order> page = this.orderRepository.findAll(pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        res.setMeta(mt);
        res.setResult(page.getContent());
        return res;
    }

    public ResOrder getOrderById(Long id) throws EillegalStateException {
        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new EillegalStateException("Order with id " + id + " not found"));
        ResOrder res = new ResOrder();
        res.setTotalAmount(order.getTotalAmount());
        res.setId(id);
        res.setStatus(order.getPaymentMethod());
        return res;
    }

    // Nhận yêu cầu thanh toán
}
