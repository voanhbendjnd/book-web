package djnd.ben1607.drink_shop.service;

import org.springframework.stereotype.Service;

import djnd.ben1607.drink_shop.domain.response.CountDashBoard;
import djnd.ben1607.drink_shop.repository.BookRepository;
import djnd.ben1607.drink_shop.repository.OrderRepository;
import djnd.ben1607.drink_shop.repository.UserRepository;

@Service
public class DashBoardService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;

    public DashBoardService(
            UserRepository userRepository,
            BookRepository bookRepository,
            OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
    }

    public CountDashBoard getCountDashBoard() {
        var res = new CountDashBoard();
        res.setBookCount(this.bookRepository.count());
        res.setOrderCount(this.orderRepository.count());
        res.setUserCount(this.userRepository.count());
        return res;

    }
}
