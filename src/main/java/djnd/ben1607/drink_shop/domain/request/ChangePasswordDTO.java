package djnd.ben1607.drink_shop.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTO {
    private String email;
    private String phone;
    private String oneTimePassword;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

}
