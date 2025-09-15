package djnd.ben1607.drink_shop.domain.request;

import djnd.ben1607.drink_shop.domain.entity.Role;
import djnd.ben1607.drink_shop.utils.constant.GenderEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountDTO {
    private Long id;
    @NotNull(message = ">>> Name cannot be null! <<<")
    @NotBlank(message = ">>> Name cannot be empty! <<<")
    @Pattern(regexp = "^\\S+(?:\\s+\\S+)+$", message = ">>> Name must be least 2 word! <<<")
    private String name;
    @NotNull(message = ">>> Password cannot be null! <<<")
    @NotBlank(message = ">>> Password cannot be empty! <<<")
    @Size(min = 6, message = ">>> Minimum password is 6 character or digit! <<<")
    @Pattern(regexp = ".*[a-zA-Z].*", message = ">>> Password must be contain character! <<<")
    @Pattern(regexp = ".*[0-9].*", message = ">>> Password must be contain digit! <<<")
    private String password;
    @NotNull(message = ">>> Confirm password cannot be null! <<<")
    @Size(min = 6, message = ">>> Minimum confirm password is 6 character or digit! <<<")
    @NotBlank(message = ">>> Password cannot be empty! <<<")
    @Pattern(regexp = ".*[a-zA-Z].*", message = ">>> Password must be contain character! <<<")
    @Pattern(regexp = ".*[0-9].*", message = ">>> Password must be contain digit! <<<")
    private String confirmPassword;
    @Email(message = ">>> Email must be valid! <<<")
    @NotBlank(message = ">>> Email cannot be empty! <<<")
    private String email;

    // @NotNull(message = "Số điện thoại không được rỗng!")
    // @NotBlank(message = "Số điện thoại không được bỏ trống!")
    // @Pattern(regexp = "^(0|\\+84|84)[35789]\\d{8}$", message = "Số điện thoại
    // không hợp lệ!")
    private String phone;
    private String address;
    private GenderEnum gender;
    private Role role;

}
