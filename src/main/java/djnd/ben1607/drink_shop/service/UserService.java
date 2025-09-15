package djnd.ben1607.drink_shop.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import djnd.ben1607.drink_shop.domain.entity.User;
import djnd.ben1607.drink_shop.domain.request.CreateAccountDTO;
import djnd.ben1607.drink_shop.domain.response.ResLoginDTO;
import djnd.ben1607.drink_shop.domain.response.ResultPaginationDTO;
import djnd.ben1607.drink_shop.domain.response.user.ResCreateUser;
import djnd.ben1607.drink_shop.domain.response.user.ResFetchUser;
import djnd.ben1607.drink_shop.domain.response.user.ResUpdateUser;
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
    Logger log = LoggerFactory.getLogger(UserService.class);

    public boolean existsById(Long id) {
        return this.userRepository.existsById(id);

    }

    public UserService(UserRepository userRepository,
            RoleRepository roleRepository,
            CartService cartService,
            PasswordEncoder passwordEncoder,
            CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User fetchUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public User fetchUserByEmailAndRefreshToken(String email, String refreshToken) {
        return this.userRepository.findByEmailAndRefreshToken(email, refreshToken) != null
                ? this.userRepository.findByEmailAndRefreshToken(email, refreshToken)
                : null;
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

    public ResCreateUser createNewUser(CreateAccountDTO user) {
        User userNew = new User();
        userNew.setRole(this.roleRepository.findByName("USER"));
        userNew.setAddress(user.getAddress());
        userNew.setName(user.getName());
        userNew.setEmail(user.getEmail());
        userNew.setPassword(user.getPassword());
        userNew.setGender(user.getGender());
        userNew.setPhone(user.getPhone());
        User lastUser = this.userRepository.save(userNew);
        this.cartService.create(lastUser.getId());
        log.info("adding user with email {} successfull", lastUser.getEmail());

        return ConvertModuleUser.createdTran(lastUser);

    }

    public ResCreateUser register(CreateAccountDTO user) {
        User userNew = new User();
        userNew.setAddress(user.getAddress());
        userNew.setName(user.getName());
        userNew.setEmail(user.getEmail());
        userNew.setPassword(user.getPassword());
        userNew.setGender(user.getGender());
        userNew.setPhone(user.getPhone());
        userNew.setRole(this.roleRepository.findByName("USER"));
        userNew.setActive(true);
        User lastUser = this.userRepository.save(userNew);
        this.cartService.create(lastUser.getId());
        return ConvertModuleUser.createdTran(lastUser);
    }

    public List<ResCreateUser> createUsers(List<CreateAccountDTO> users) throws EillegalStateException {
        List<User> lastUsers = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        Set<String> userSet = new HashSet<>();
        for (CreateAccountDTO x : users) {
            if (!userSet.add(x.getEmail())) {
                throw new EillegalStateException("Duplicate email found in the input list: " + x.getEmail());
            }
        }
        for (CreateAccountDTO x : users) {
            if (this.userRepository.existsByEmail(x.getEmail())) {
                errorList.add("Email (" + x.getEmail() + ") already exist");
            }
            if (errorList.isEmpty()) {
                User user = new User();
                user.setEmail(x.getEmail());
                user.setActive(true);
                user.setAddress(x.getAddress());
                user.setGender(x.getGender());
                user.setName(x.getName());
                user.setPassword(this.passwordEncoder.encode(x.getPassword()));
                user.setPhone(x.getPhone());
                user.setRole(this.roleRepository.findByName("USER"));
                lastUsers.add(user);
            }
        }
        if (errorList.isEmpty()) {
            this.userRepository.saveAll(lastUsers);
            return lastUsers.stream().map(ConvertModuleUser::createdTran).collect(Collectors.toList());
        }
        throw new EillegalStateException(errorList.stream().collect(Collectors.joining("\n")));

    }

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
            return ConvertModuleUser.fetchUser(user);
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
                .map(ConvertModuleUser::fetchUser)
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
        User user = this.userRepository.findByEmail(
                SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new EillegalStateException("User not found")));
        ResLoginDTO.UserGetAccount res = new ResLoginDTO.UserGetAccount();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        userLogin.setId(user.getId());
        userLogin.setEmail(user.getEmail());
        userLogin.setAddress(user.getAddress());
        userLogin.setAvatar(user.getAvatar());
        userLogin.setName(user.getName());
        userLogin.setPhone(user.getPhone());
        userLogin.setRole(user.getRole().getName());
        res.setUser(userLogin);
        return res;
    }

    @CacheEvict(value = "userAccount", allEntries = true)
    public void logoutAccount() throws EillegalStateException {
        User user = this.userRepository.findByEmail(
                SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new EillegalStateException("User not found")));
        user.setRefreshToken(null);
        this.userRepository.save(user);
    }

}
