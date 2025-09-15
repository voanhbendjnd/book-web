package djnd.ben1607.drink_shop.controller.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import djnd.ben1607.drink_shop.domain.request.AddItemDTO;
import djnd.ben1607.drink_shop.service.CartService;
import djnd.ben1607.drink_shop.utils.annotation.ApiMessage;
import djnd.ben1607.drink_shop.utils.error.CartException;
import djnd.ben1607.drink_shop.utils.error.IdInvalidException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/v1")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CartController {
    CartService cartService;

    @PostMapping("/cart/add-item")
    @ApiMessage("Add item to cart")
    public ResponseEntity<Void> addBookToCart(@Valid @RequestBody AddItemDTO dto) throws IdInvalidException {
        this.cartService.addItemToCart(dto);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/cart/item/{id}/increase")
    @ApiMessage("Increase item")
    public ResponseEntity<Void> increase(@PathVariable("id") Long id) throws CartException, IdInvalidException {
        if (id == null) {
            throw new IdInvalidException("Id not null!");
        }
        this.cartService.inscreaseItem(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/cart/item/{id}/decrease")
    @ApiMessage("Increase item")
    public ResponseEntity<Void> decrease(@PathVariable("id") Long id) throws CartException, IdInvalidException {
        if (id == null) {
            throw new IdInvalidException("Id not null!");
        }
        this.cartService.decreaseItem(id);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/cart/item/{id}")
    @ApiMessage("Delete item")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws IdInvalidException {
        if (id == null) {
            throw new IdInvalidException("Id not null!");
        }
        this.cartService.deleteItem(id);
        return ResponseEntity.ok(null);
    }
}
