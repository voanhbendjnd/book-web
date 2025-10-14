package djnd.ben1607.drink_shop.controller.admin;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import djnd.ben1607.drink_shop.domain.entity.User;
import djnd.ben1607.drink_shop.domain.request.CreateAccountDTO;
import djnd.ben1607.drink_shop.domain.response.ResultPaginationDTO;
import djnd.ben1607.drink_shop.domain.response.user.ResCreateUser;
import djnd.ben1607.drink_shop.domain.response.user.ResFetchUser;
import djnd.ben1607.drink_shop.domain.response.user.ResUpdateUser;
import djnd.ben1607.drink_shop.service.UserService;
import djnd.ben1607.drink_shop.utils.annotation.ApiMessage;
import djnd.ben1607.drink_shop.utils.error.EillegalStateException;
import djnd.ben1607.drink_shop.utils.error.IdInvalidException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create new user")
    public ResponseEntity<ResCreateUser> createNewUser(@Valid @RequestBody CreateAccountDTO user)
            throws IdInvalidException, EillegalStateException {
        if (this.userService.existsByEmail(user.getEmail())) {
            throw new IdInvalidException(">>> Email already exists!, please enter another email <<<");
        }
        if (user.getConfirmPassword().equals(user.getPassword())) {
            String hashPassword = this.passwordEncoder.encode(user.getPassword());
            user.setPassword(hashPassword);
            return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.createNewUser(user));
        }
        throw new IdInvalidException(">>> Password and confirm password is not the same! <<<");

    }

    @PutMapping("/users")
    @ApiMessage("Update user")
    public ResponseEntity<ResUpdateUser> updateUser(@Valid @RequestBody User user) throws IdInvalidException {
        if (this.userService.existsById(user.getId())) {
            return ResponseEntity.ok(this.userService.updateUser(user));
        } else {
            throw new IdInvalidException(">>>  Id with " + user.getId() + " do not exists! <<<");
        }
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch user by ID")
    public ResponseEntity<ResFetchUser> fetchUserByID(@PathVariable("id") Long id) throws IdInvalidException {
        if (this.userService.existsById(id)) {
            return ResponseEntity.ok(this.userService.fetchUserByID(id));
        }
        throw new IdInvalidException(">>> User with ID: " + id + " is not exists! <<<");
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all user")
    public ResponseEntity<ResultPaginationDTO> fetchAllUSer(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.ok(this.userService.fetchAllUser(spec, pageable));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete user by ID")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") Long id)
            throws IdInvalidException, EillegalStateException {
        if (this.userService.existsById(id)) {
            this.userService.deleteUserByID(id);
            return ResponseEntity.ok(null);
        }
        throw new IdInvalidException(">>> User with ID: " + id + " is not exists! <<<");
    }

    @GetMapping("/userss")
    @ApiMessage("Fetch all")
    public ResponseEntity<List<User>> fetchAll() {
        return ResponseEntity.ok(this.userService.fetchAll());
    }

    @PostMapping("users/b-create")
    @ApiMessage("Create new user")
    public ResponseEntity<List<ResCreateUser>> createUsers(@RequestBody List<CreateAccountDTO> listUsers)
            throws EillegalStateException {
        for (CreateAccountDTO x : listUsers) {
            if (!x.getPassword().equals(x.getConfirmPassword())) {
                throw new EillegalStateException("Password and confirm password of(" + x.getEmail() + ") not the same");
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.createUsers(listUsers));
    }
}
