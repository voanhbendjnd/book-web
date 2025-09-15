package djnd.ben1607.drink_shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import djnd.ben1607.drink_shop.domain.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    public boolean existsPermissionByApiPathAndMethod(String apiPath, String method);

    public List<Permission> findByIdIn(List<Long> ids);
}
