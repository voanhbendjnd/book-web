package djnd.ben1607.drink_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import djnd.ben1607.drink_shop.domain.entity.CartItem;
import jakarta.transaction.Transactional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    public CartItem findByBookIdAndCartId(Long bookID, Long cartID);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    public void deleteAllByCartId(@Param("cartId") Long cartId);
}
