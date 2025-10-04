package djnd.ben1607.drink_shop.controller.client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import djnd.ben1607.drink_shop.domain.entity.User;
import djnd.ben1607.drink_shop.domain.request.ChangePasswordDTO;
import djnd.ben1607.drink_shop.domain.request.LoginDTO;
import djnd.ben1607.drink_shop.domain.request.UserUpdate;
import djnd.ben1607.drink_shop.domain.request.CreateAccountDTO;
import djnd.ben1607.drink_shop.domain.response.ResLoginDTO;
import djnd.ben1607.drink_shop.domain.response.user.ResCreateUser;
import djnd.ben1607.drink_shop.repository.UserRepository;
import djnd.ben1607.drink_shop.service.EmailService;
import djnd.ben1607.drink_shop.service.UserService;
import djnd.ben1607.drink_shop.utils.SecurityUtils;
import djnd.ben1607.drink_shop.utils.annotation.ApiMessage;
import djnd.ben1607.drink_shop.utils.error.EillegalStateException;
import djnd.ben1607.drink_shop.utils.error.IdInvalidException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final SecurityUtils securityUtils;
    private final AuthenticationManagerBuilder builder;
    private final EmailService emailService;
    private final UserRepository userRepository;
    @Value("${djnd.jwt.access-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    public AuthController(UserService userService,
            AuthenticationManagerBuilder builder,
            SecurityUtils securityUtils,
            PasswordEncoder passwordEncoder,
            EmailService emailService, UserRepository userRepository) {
        this.builder = builder;
        this.emailService = emailService;
        this.securityUtils = securityUtils;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/auth/login")
    @ApiMessage("Login account")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO dto) throws IdInvalidException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                dto.getUsername(), dto.getPassword());
        Authentication authentication = builder.getObject().authenticate(authenticationToken); // not same password db
                                                                                               // BadCredentialsException
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO res = new ResLoginDTO();
        User user = this.userService.fetchUserByEmail(dto.getUsername());
        if (user != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(user.getId(), user.getEmail(), user.getName(),
                    user.getAvatar(), user.getAddress(), user.getPhone(),
                    user.getRole().getName(), user.getGender());
            res.setUser(userLogin);
        }
        // -> create token <-
        String accessToken = this.securityUtils.createAccessToken(authentication.getName(), res);
        res.setAccessToken(accessToken);

        // -> create refresh token <-
        String refreshToken = this.securityUtils.createRefreshToken(dto.getUsername(), res);

        // -> update <-
        this.userService.updateUserToken(refreshToken, dto.getUsername());

        // -> set cookies <-
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true) // chỉ được truy cập bởi mấy chủ web, còn client thì không đọc được
                .secure(true) // chỉ được gửi qua kết nối https
                .path("/") // gửi được tất cả request đến máy chủ
                .maxAge(refreshTokenExpiration) // thời hạn sống của cookie, refreshToken có thể tồn tại lâu hơn actoken
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(res); // lưu cookie
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Auto refresh token when user login back")
    public ResponseEntity<ResLoginDTO> autoRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "not") String refreshToken) throws IdInvalidException {
        if (refreshToken.equals("not")) {
            throw new IdInvalidException(">>> Not find refresh token! <<<");
        }
        Jwt decodedToken = this.securityUtils.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();
        if (email == null) {
            throw new IdInvalidException("<<<Email not exists! >>>");
        }
        User user = this.userService.fetchUserByEmailAndRefreshToken(email, refreshToken);
        if (user == null) {
            throw new IdInvalidException(">>> System error, not find token! <<<");
        }
        ResLoginDTO res = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(user.getId(), user.getEmail(), user.getName(),
                user.getAvatar(), user.getAddress(), user.getPhone(),
                user.getRole().getName(), user.getGender());
        res.setUser(userLogin);
        // -> create token <-
        String accessToken = this.securityUtils.createAccessToken(email, res);
        res.setAccessToken(accessToken);

        // -> create refresh token <-
        String new_refresh_Token = this.securityUtils.createRefreshToken(email, res);
        this.userService.updateUserToken(new_refresh_Token, email);

        // -> set cookie <-
        ResponseCookie cookie = ResponseCookie.from("refresh_token", new_refresh_Token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(res);

    }

    @PutMapping("/auth/logout")
    @ApiMessage("Logout account and delete refresh token")
    @CacheEvict(value = "userAccount", allEntries = true)
    public ResponseEntity<Void> logout(@CookieValue(name = "refresh_token", defaultValue = "not") String refreshToken)
            throws IdInvalidException {
        if (refreshToken.equals("not")) {
            throw new IdInvalidException(">>> Not found refresh token! <<<");
        }
        Jwt decodedToken = this.securityUtils.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();
        if (email == null) {
            throw new IdInvalidException(">>> Refresh token wrong! <<<");
        }
        User user = this.userService.fetchUserByEmailAndRefreshToken(email, refreshToken);
        if (user == null) {
            throw new IdInvalidException(">>> Wrong refresh token! <<<");
        }
        user.setRefreshToken(null);
        this.userService.updateUser(user);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout and delete refresh token")
    @CacheEvict(value = "userAccount", allEntries = true)
    public ResponseEntity<Void> logout2() throws IdInvalidException, EillegalStateException {
        String email = SecurityUtils.getCurrentUserLogin().isPresent() ? SecurityUtils.getCurrentUserLogin().get() : "";
        if (email.equals("")) {
            throw new IdInvalidException(">>> Access token wrong! <<<");
        }
        this.userService.logoutAccount();
        ResponseCookie formDeleteCookie = ResponseCookie
                .from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, formDeleteCookie.toString()).body(null);
    }

    @PutMapping("/auth/change-password")
    @ApiMessage("Change password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO request) throws IdInvalidException {
        String email = SecurityUtils.getCurrentUserLogin().get();
        User user = this.userService.fetchUserByEmail(email);
        if (!this.passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IdInvalidException("Old password is incorrect, please input old password again!");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IdInvalidException("New password and confirm password is not the same!");
        }
        user.setPassword(this.passwordEncoder.encode(request.getConfirmPassword()));
        user.setRefreshToken(null);
        this.userRepository.save(user);
        // this.emailService.sendOTPToEmail(user);
        ResponseCookie deleteFullToken = ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteFullToken.toString())
                .body("Change password successfully");

    }

    @PostMapping("/auth/change-password-by-otp")
    @ApiMessage("Change password by OTP")
    public ResponseEntity<?> changePasswordByOTPCode(@RequestBody ChangePasswordDTO dto) throws IdInvalidException {
        var user = this.userRepository.findByEmail(dto.getEmail());
        if (user != null) {
            if (dto.getNewPassword().equals(dto.getConfirmPassword())) {
                user.setPassword(this.passwordEncoder.encode(dto.getConfirmPassword()));
                user.setRefreshToken(null);
                this.userRepository.save(user);
                ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", null)
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(0)
                        .build();
                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                        .body("Change password successfull");
            } else {
                throw new IdInvalidException("New password and confirm password not the same!");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email user not found!");
    }

    @PostMapping("/auth/forgot-password")
    @ApiMessage("Get one time password")
    public ResponseEntity<Void> sendOTP(@RequestBody User user) throws IdInvalidException {
        if (!this.userService.existsByEmail(user.getEmail())) {
            throw new IdInvalidException(">>> Email is not exists! <<<");
        }
        User currentUser = this.userService.fetchUserByEmail(user.getEmail());
        this.emailService.sendOTPToEmail(currentUser);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/auth/verify-otp")
    @ApiMessage("Change password by One time password")
    public ResponseEntity<?> changePasswordByOTP(
            @RequestBody ChangePasswordDTO request) throws IdInvalidException {
        User user = this.userService.fetchUserByEmail(request.getEmail());
        if (user == null) {
            throw new IdInvalidException("Email not found!");
        }
        if (!user.isOTPRequired()) {
            throw new IdInvalidException("OTP expires!");
        }
        if (!this.passwordEncoder.matches(request.getOneTimePassword(),
                user.getOneTimePassword())) {
            throw new IdInvalidException(">>> OTP incorrect! <<<");
        }
        // if (!request.getConfirmPassword().equals(request.getNewPassword())) {
        // throw new IdInvalidException(">>> Password and confirm password is not the
        // same! <<<");
        // }
        // user.setPassword(this.passwordEncoder.encode(request.getConfirmPassword()));
        // user.setRefreshToken(null);
        // this.userRepository.save(user);
        // ResponseCookie setCookie = ResponseCookie.from("refresh_token", null)
        // .httpOnly(true)
        // .secure(true)
        // .path("/")
        // .maxAge(0)
        // .build();
        // return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
        // setCookie.toString()).body(null);
        return ResponseEntity.ok("Verify OTP successfully");
    }

    @PostMapping("/auth/register")
    @ApiMessage("Sign in account")
    public ResponseEntity<ResCreateUser> register(@RequestBody @Valid CreateAccountDTO user)
            throws IdInvalidException {
        if (this.userService.existsByEmail(user.getEmail())) {
            throw new IdInvalidException(">>> Email with " + user.getEmail() + " already exists! <<<");
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new IdInvalidException(">>> Password and confirm password is not the same! <<<");
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.register(user));
    }

    @GetMapping("/auth/account")
    @ApiMessage("Get account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() throws IdInvalidException, EillegalStateException {
        return ResponseEntity.ok(this.userService.getAccount());
    }

    @PutMapping("/auth/user/update")
    @ApiMessage("Update user by id")
    public ResponseEntity<?> updateUserById(@RequestBody UserUpdate dto) throws EillegalStateException {
        this.userService.updateUserGetAccount(dto);
        return ResponseEntity.ok("Update successfull");
    }

    @GetMapping("/auth/test-token")
    @ApiMessage("Test JWT token creation")
    public ResponseEntity<Map<String, Object>> testToken() {
        try {
            String testEmail = "test@example.com";
            String token = this.securityUtils.createSimpleToken(testEmail);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("email", testEmail);
            response.put("token", token);
            response.put("message", "JWT token created successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/auth/test-decode")
    @ApiMessage("Test JWT token decoding")
    public ResponseEntity<Map<String, Object>> testDecode(Authentication authentication) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("authentication", authentication != null ? authentication.getName() : "null");
            response.put("message", "JWT token decoded successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/auth/test-simple")
    @ApiMessage("Test simple endpoint")
    public ResponseEntity<Map<String, Object>> testSimple() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Simple endpoint works");
        return ResponseEntity.ok(response);
    }

}
