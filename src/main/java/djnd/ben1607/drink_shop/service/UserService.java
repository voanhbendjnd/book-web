package djnd.ben1607.drink_shop.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import djnd.ben1607.drink_shop.domain.entity.User;
import djnd.ben1607.drink_shop.domain.request.CreateAccountDTO;
import djnd.ben1607.drink_shop.domain.request.UserUpdate;
import djnd.ben1607.drink_shop.domain.response.ResLoginDTO;
import djnd.ben1607.drink_shop.domain.response.ResultPaginationDTO;
import djnd.ben1607.drink_shop.domain.response.user.ResCreateUser;
import djnd.ben1607.drink_shop.domain.response.user.ResFetchUser;
import djnd.ben1607.drink_shop.domain.response.user.ResUpdateUser;
import djnd.ben1607.drink_shop.mapper.UserMapper;
import djnd.ben1607.drink_shop.repository.CartRepository;
import djnd.ben1607.drink_shop.repository.RoleRepository;
import djnd.ben1607.drink_shop.repository.UserRepository;
import djnd.ben1607.drink_shop.utils.ChangeUpdate;
import djnd.ben1607.drink_shop.utils.SecurityUtils;
import djnd.ben1607.drink_shop.utils.convert.ConvertModuleUser;
import djnd.ben1607.drink_shop.utils.error.EillegalStateException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CartService cartService;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final UserMapper userMapper;
    // Logger log = LoggerFactory.getLogger(UserService.class);

    public boolean existsById(Long id) {
        return this.userRepository.existsById(id);

    }

    public UserService(UserRepository userRepository,
            RoleRepository roleRepository,
            CartService cartService,
            PasswordEncoder passwordEncoder,
            CartRepository cartRepository,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Cacheable(value = "users", key = "#email")
    public User fetchUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Cacheable(value = "users", key = "#email + '_' + #refreshToken")
    public User fetchUserByEmailAndRefreshToken(String email, String refreshToken) {
        return this.userRepository.findByEmailAndRefreshToken(email, refreshToken);
    }

    public void updateUserToken(String token, String email) {
        User user = this.userRepository.findByEmail(email);
        if (user != null) {
            user.setRefreshToken(token);
            if (!this.cartRepository.existsById(user.getId())) {
                this.cartService.create(user.getId());
            }
            this.userRepository.save(user);
        }
    }

    public ResCreateUser createNewUser(CreateAccountDTO user) throws EillegalStateException {
        var role = this.roleRepository.findByName("USER");
        if (role != null) {
            var userNew = this.userMapper.toUser(user);
            userNew.setRole(role);
            User lastUser = this.userRepository.save(userNew);
            this.cartService.create(lastUser.getId());
            // log.info("adding user with email {} successfull", lastUser.getEmail());
            return this.userMapper.toResCreateUser(lastUser);
        }
        throw new EillegalStateException("Role USER not found");

    }

    public ResCreateUser register(CreateAccountDTO user) throws EillegalStateException {
        var role = this.roleRepository.findByName("USER");
        if (role != null) {
            var userNew = this.userMapper.toUser(user);
            userNew.setRole(role);
            User lastUser = this.userRepository.save(userNew);
            this.cartService.create(lastUser.getId());
            return this.userMapper.toResCreateUser(lastUser);
        }

        throw new EillegalStateException("Role USER not found");
    }

    public List<ResCreateUser> createUsers(List<CreateAccountDTO> users) throws EillegalStateException {
        List<User> lastUsers = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        Set<String> userSet = new HashSet<>();
        for (CreateAccountDTO x : users) {
            String errorMessage = "";
            if (!userSet.add(x.getEmail())) {
                errorMessage = "[Duplicate email found in the input list: " + x.getEmail() + "]";
                errorList.add(errorMessage);
            } else if (this.userRepository.existsByEmail(x.getEmail())) {
                errorMessage = "[Email (" + x.getEmail() + ") already exist]";
                errorList.add(errorMessage);
            }
            if (errorMessage.isEmpty()) {
                var role = this.roleRepository.findByName("USER");
                if (role != null) {
                    x.setPassword(this.passwordEncoder.encode(x.getPassword()));
                    var user = this.userMapper.toUser(x);
                    user.setRole(role);
                    lastUsers.add(user);
                }
                throw new EillegalStateException("Role USER not found");

            }
        }
        if (errorList.isEmpty()) {
            this.userRepository.saveAll(lastUsers);
            return lastUsers.stream().map(this.userMapper::toResCreateUser).collect(Collectors.toList());
        }
        throw new EillegalStateException(errorList.stream().collect(Collectors.joining("-", "", "")));
    }

    @CacheEvict(value = "users", key = "#user.email")
    public ResUpdateUser updateUser(User user) {
        User userDB = this.userRepository.findById(user.getId()).get();
        ChangeUpdate.handle(user, userDB);
        User lastUser = this.userRepository.save(userDB);
        return ConvertModuleUser.updateTran(lastUser);
    }

    public ResFetchUser fetchUserByID(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return this.userMapper.toResFetchUser(user);
        }
        return null;
    }

    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public void deleteUserByID(Long id) throws EillegalStateException {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new EillegalStateException("User with ID (" + id + ")not found"));
        user.setActive(false);
        this.userRepository.save(user);
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> page = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        res.setMeta(mt);
        List<ResFetchUser> lastList = page.getContent().stream()
                .map(this.userMapper::toResFetchUser)
                .collect(Collectors.toList());
        res.setResult(lastList);
        return res;

    }

    public List<User> fetchAll() {
        return this.userRepository.findAll();
    }

    // tên cache luôn trên ram
    @Cacheable("userAccount")
    public ResLoginDTO.UserGetAccount getAccount() throws EillegalStateException {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new EillegalStateException("User not found"));
        if (email.isBlank() || email.isEmpty()) {
            throw new EillegalStateException("User not found");
        }
        User user = this.userRepository.findByEmail(email);
        if (user != null) {
            var res = new ResLoginDTO.UserGetAccount();
            // ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
            var userLogin = this.userMapper.toUserLogin(user);
            res.setUser(userLogin);
            return res;
        }
        throw new EillegalStateException("User not found");

    }

    @CacheEvict(value = "userAccount", allEntries = true)
    public void logoutAccount() throws EillegalStateException {
        User user = this.userRepository.findByEmail(
                SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new EillegalStateException("User not found")));
        user.setRefreshToken(null);
        this.userRepository.save(user);
    }

    @CacheEvict(value = "userAccount", allEntries = true)
    public void updateUserGetAccount(UserUpdate dto) throws EillegalStateException {
        var user = this.userRepository.findById(dto.id())
                .orElseThrow(() -> new EillegalStateException("User not found"));
        user.setAddress(dto.address());
        user.setEmail(dto.email());
        user.setName(dto.name());
        user.setPhone(dto.phone());
        user.setGender(dto.gender());
        this.userRepository.save(user);
    }

}
