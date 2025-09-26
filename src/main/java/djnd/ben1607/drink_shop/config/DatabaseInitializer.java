package djnd.ben1607.drink_shop.config;

// import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import djnd.ben1607.drink_shop.domain.entity.Permission;
import djnd.ben1607.drink_shop.domain.entity.Role;
import djnd.ben1607.drink_shop.domain.entity.User;
import djnd.ben1607.drink_shop.repository.PermissionRepository;
import djnd.ben1607.drink_shop.repository.RoleRepository;
import djnd.ben1607.drink_shop.repository.UserRepository;
import djnd.ben1607.drink_shop.utils.constant.GenderEnum;

@Service
public class DatabaseInitializer implements CommandLineRunner {
    private final UserRepository UserRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository RoleRepository;
    private final PasswordEncoder passwordEncoder;
    // Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    public DatabaseInitializer(UserRepository userRepository, PermissionRepository permissionRepository,
            RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.RoleRepository = roleRepository;
        this.UserRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE <<<");
        Long permissionCnt = this.permissionRepository.count();
        Long userCnt = this.UserRepository.count();
        Long roleCnt = this.RoleRepository.count();
        if (permissionCnt == 0) {
            List<Permission> permissionList = new ArrayList<>();
            permissionList.add(new Permission("Create new a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
            permissionList.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
            permissionList
                    .add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
            permissionList.add(new Permission("Get a permission", "/api/v1/permissions{id}", "GET", "PERMISSIONS"));
            permissionList.add(new Permission("Fetch all permission", "/api/v1/permissions", "GET", "PERMISSIONS"));

            permissionList.add(new Permission("Create new a role", "/api/v1/roles", "POST", "ROLES"));
            permissionList.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
            permissionList.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
            permissionList.add(new Permission("Get a role", "/api/v1/roles/{id}", "GET", "ROLES"));
            permissionList.add(new Permission("Fetch all role", "/api/v1/roles", "GET", "ROLES"));

            permissionList.add(new Permission("Create new a user", "/api/v1/users", "POST", "USERS"));
            permissionList.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
            permissionList.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
            permissionList.add(new Permission("Get a user", "/api/v1/users/{id}", "GET", "USERS"));
            permissionList.add(new Permission("Fetch all user", "/api/v1/users", "GET", "USERS"));

            permissionList.add(new Permission("Create new a book", "/api/v1/books", "POST", "BOOKS"));
            permissionList.add(new Permission("Update a book", "/api/v1/books", "PUT", "BOOKS"));
            permissionList.add(new Permission("Delete a book", "/api/v1/books/{id}", "DELETE", "BOOKS"));
            permissionList.add(new Permission("Get a book", "/api/v1/books/{id}", "GET", "BOOKS"));
            permissionList.add(new Permission("Fetch all book", "/api/v1/books", "GET", "BOOKS"));

            permissionList.add(new Permission("Create new a category", "/api/v1/categories", "POST", "CATEGORIES"));
            permissionList.add(new Permission("Update a category", "/api/v1/categories", "PUT", "CATEGORIES"));
            permissionList.add(new Permission("Delete a category", "/api/v1/categories/{id}", "DELETE", "CATEGORIES"));
            permissionList.add(new Permission("Get a category", "/api/v1/categories/{id}", "GET", "CATEGORIES"));
            permissionList.add(new Permission("Fetch all category", "/api/v1/categories", "GET", "CATEGORIES"));
            this.permissionRepository.saveAll(permissionList);
        }

        if (roleCnt == 0) {
            List<Permission> listPermissionAll = this.permissionRepository.findAll();
            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setActive(true);
            adminRole.setDescription("SUPER ADMIN HAS FULL PERMISSIONS");
            adminRole.setPermissions(listPermissionAll);
            this.RoleRepository.save(adminRole);
        }
        if (userCnt == 0) {
            User admin = new User();
            admin.setAddress("CAN THO");
            admin.setEmail("benva.ce190709@gmail.com");
            admin.setGender(GenderEnum.MALE);
            admin.setName("BEN ANH VO");
            admin.setPassword(this.passwordEncoder.encode("123456"));
            Role roleAdmin = this.RoleRepository.findByName("SUPER_ADMIN");
            if (roleAdmin != null) {
                admin.setRole(roleAdmin);
            }
            this.UserRepository.save(admin);
        }
        if (permissionCnt != 0 && roleCnt != 0 && userCnt != 0) {
            System.out.println(">>> SKIP PROCESSING INITIALIER <<<");
            // logger.info("Run success at {}", LocalDateTime.now());

        } else {
            System.out.println(">>> INIT DATABASE SUCCESSFULL <<<");
            // logger.info("Run success at {}", LocalDateTime.now());
        }
    }

}
