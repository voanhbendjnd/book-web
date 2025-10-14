package djnd.ben1607.drink_shop.service;

import djnd.ben1607.drink_shop.domain.entity.Book;
import djnd.ben1607.drink_shop.domain.entity.Cart;
import djnd.ben1607.drink_shop.domain.entity.CartItem;
import djnd.ben1607.drink_shop.domain.entity.User;
import djnd.ben1607.drink_shop.domain.request.AddItemDTO;
import djnd.ben1607.drink_shop.repository.BookRepository;
import djnd.ben1607.drink_shop.repository.CartItemRepository;
import djnd.ben1607.drink_shop.repository.CartRepository;
import djnd.ben1607.drink_shop.repository.UserRepository;
import djnd.ben1607.drink_shop.utils.SecurityUtils;
import djnd.ben1607.drink_shop.utils.error.CartException;
import djnd.ben1607.drink_shop.utils.error.IdInvalidException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CartService {
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    UserRepository userRepository;
    BookRepository bookRepository;

    @CacheEvict(value = "carts", key = "#userId")
    public void create(Long userId) {
        Cart cart = new Cart();
        Optional<User> userOptional = this.userRepository.findById(userId);
        cart.setUser(userOptional.orElse(null));
        this.cartRepository.save(cart);
    }

    @CacheEvict(value = "carts", key = "#dto.bookId")
    public void addItemToCart(AddItemDTO dto) throws IdInvalidException {
        // get user
        User user = this.userRepository.findByEmail(
                SecurityUtils.getCurrentUserLogin().isPresent() ? SecurityUtils.getCurrentUserLogin().get() : null);
        if (user != null) {
            // get cart
            Optional<Cart> cartUser = this.cartRepository.findById(user.getId());
            if (!cartUser.isPresent()) {
                this.create(user.getId());
            }
            CartItem item = this.cartItemRepository.findByBookIdAndCartId(dto.getBookId(), cartUser.get().getId());
            if (item != null) {
                item.setQuantity(item.getQuantity() + dto.getQuantity());
                this.cartItemRepository.save(item);
            } else {
                CartItem newItem = new CartItem();
                Book book = this.bookRepository.findById(dto.getBookId()).get();
                newItem.setBook(book);
                newItem.setCart(cartUser.get());
                newItem.setQuantity(dto.getQuantity());
                newItem.setPrice(book.getPrice());
                this.cartItemRepository.save(newItem);
            }
        } else
            throw new IdInvalidException(">>> Add book to to cart failure! <<<");
    }

    public void inscreaseItem(Long itemID) throws CartException, IdInvalidException {
        Optional<CartItem> item = this.cartItemRepository.findById(itemID);
        if (item.isPresent()) {
            if (item.get().getQuantity() == 99) {
                throw new CartException(">>> Reached maximum number of books added! <<<");
            }
            item.get().setQuantity(item.get().getQuantity() + 1);
            this.cartItemRepository.save(item.get());

        } else {
            throw new IdInvalidException("Id item with (" + itemID + ") is not exist!");
        }

    }

    public void decreaseItem(Long itemID) throws CartException, IdInvalidException {
        Optional<CartItem> item = this.cartItemRepository.findById(itemID);

        if (item.isPresent()) {
            if (item.get().getQuantity() == 0) {
                throw new CartException(">>> Quantity of book is 0, do not decrease book! <<<");
            } else if (item.get().getQuantity() == 1) {
                this.cartItemRepository.delete(item.get());
            } else {
                item.get().setQuantity(item.get().getQuantity() - 1);
                this.cartItemRepository.save(item.get());
            }
        } else {
            throw new IdInvalidException("Id item with (" + itemID + ") is not exist!");

        }
    }

    public void deleteItem(Long id) throws IdInvalidException {
        Optional<CartItem> item = this.cartItemRepository.findById(id);
        if (item.isPresent()) {
            this.cartItemRepository.delete(item.get());
        } else {
            throw new IdInvalidException(">>> Id item with (" + id + ") does not exist! <<<");
        }
    }

}
