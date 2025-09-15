package djnd.ben1607.drink_shop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import djnd.ben1607.drink_shop.domain.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    public Optional<Cart> findByUserId(Long id);
}
