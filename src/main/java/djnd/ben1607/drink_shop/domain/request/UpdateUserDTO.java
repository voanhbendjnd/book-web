package djnd.ben1607.drink_shop.domain.request;

import djnd.ben1607.drink_shop.utils.constant.GenderEnum;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDTO {
    private Long id;
    @Pattern(regexp = "^\\S+(?:\\s+\\S+)+$", message = ">>> Name must be least 2 word! <<<")
    private String name;
    @Pattern(regexp = "^(0|\\+84|84)[35789]\\d{8}$", message = "Số điện thoại không hợp lệ!")
    private String phone;
    private String address;
    private GenderEnum gender;

}
