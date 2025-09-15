package djnd.ben1607.drink_shop.domain.request;

import java.time.Instant;

import djnd.ben1607.drink_shop.utils.constant.GenderEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String name;
    private String email;
    private String address;
    private String phone;
    private GenderEnum gender;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
}
