package djnd.ben1607.drink_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import djnd.ben1607.drink_shop.domain.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    public User findByEmail(String email);

    public User findByEmailAndRefreshToken(String email, String refreshToken);

    public boolean existsByEmail(String email);

}
